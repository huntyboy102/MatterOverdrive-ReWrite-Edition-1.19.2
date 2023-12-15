
package huntyboy102.moremod.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import huntyboy102.moremod.handler.thread.VersionCheckThread;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.Platform;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionCheckerHandler implements IConfigSubscriber {
	public static final String[] mirrors = new String[] { Reference.VERSIONS_CHECK_URL };
	final String regex = "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)";
	final Pattern pattern = Pattern.compile(regex);
	public Future<String> download;
	int lastPoll = 400;
	private boolean updateInfoDisplayed = false;
	private int currentMirror = 0;
	private boolean checkForUpdates;

	// Called when a player ticks.
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {

		if (event.player.world.isRemote || Platform.isDev() || event.phase != TickEvent.Phase.START
				|| !checkForUpdates) {
			return;
		}

		if (FMLCommonHandler.instance().getSide() == Side.SERVER
				&& FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning()) {
			if (!event.player.canUseCommand(2, ""))
				return;
		}

		if (lastPoll > 0) {
			--lastPoll;
			return;
		}
		lastPoll = 400;

		if (updateInfoDisplayed) {
			return;
		}

		if (currentMirror < mirrors.length) {
			if (download == null) {
				download = MatterOverdrive.THREAD_POOL.submit(new VersionCheckThread(mirrors[currentMirror]));
				currentMirror++;
			}
		}

		if (download != null && download.isDone()) {
			String result = null;

			try {
				result = download.get();
			} catch (InterruptedException e) {
				MOLog.log(Level.ERROR, e, "Version checking from '%1$s' was interrupted", mirrors[currentMirror - 1]);
			} catch (ExecutionException e) {
				MOLog.log(Level.ERROR, e, "Version checking from '%1$s' has failed", mirrors[currentMirror - 1]);
			} finally {
				if (result != null) {
					try {
						updateInfoDisplayed = constructVersionAndCheck(result, event.player);
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "There was a problem while decoding the update info from website.");
					}
				}

				download.cancel(false);
				download = null;
			}
		}
	}

	private boolean constructVersionAndCheck(String jsonText, EntityPlayer player) {
		JsonParser parser = new JsonParser();

		JsonObject root = parser.parse(jsonText).getAsJsonObject();

		JsonObject versions = root.get("versions").getAsJsonObject();

		JsonArray versionData = versions.get(Loader.MC_VERSION).getAsJsonArray();

		int data = 0;
		JsonObject latest = versionData.get(0).getAsJsonObject();
		String type = latest.get("type").getAsString();
		if (type.equals("alpha")) {
			while (type.equals("alpha")) {
				if (versionData.size() > data)
					latest = versionData.get(data++).getAsJsonObject();
				else
					break; // Shouldn't ever happen within reason
			}
		}

		if (type.equals("alpha"))
			return false;

		String fileName = latest.get("name").getAsString();

		Matcher matcher = pattern.matcher(fileName);
		if (!matcher.find())
			return false;

		String fullVersion = matcher.group(0);

		boolean hasNew = false;
		if (!Reference.VERSION.equals(fullVersion)) {
			String[] arr = Reference.VERSION.split("\\.");
			if (Integer.parseInt(arr[0]) >= Integer.parseInt(matcher.group(1))) {
				if (Integer.parseInt(arr[1]) >= Integer.parseInt(matcher.group(2))) {
					if (Integer.parseInt(arr[2]) >= Integer.parseInt(matcher.group(3))) {
						if (matcher.groupCount() == 5 && arr.length == 4) {
							if (Integer.parseInt(arr[3]) >= Integer.parseInt(matcher.group(4))) {
								hasNew = true;
							}
						} else {
							hasNew = true;
						}
					}
				}
			}
		}

		if (hasNew) {
			TextComponentString chat = new TextComponentString(TextFormatting.GOLD + "[Matter Overdrive] ");
			Style style = new Style();
			chat.appendSibling(new TextComponentTranslation("alert.new_update"))
					.setStyle(style.setColor(TextFormatting.WHITE));
			player.sendMessage(chat);

			chat = new TextComponentString("");
			ITextComponent versionName = new TextComponentString(
					root.get("title").getAsString() + " " + fullVersion + " ")
					.setStyle(new Style().setColor(TextFormatting.AQUA));
			chat.appendSibling(versionName);
			chat.appendText(TextFormatting.WHITE + "[");
			style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Reference.DOWNLOAD_URL));
			style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new TextComponentTranslation("info." + Reference.MOD_ID + ".updater.hover")
							.setStyle(new Style().setColor(TextFormatting.YELLOW))));
			style.setColor(TextFormatting.GREEN);
			chat.appendSibling(new TextComponentTranslation("info." + Reference.MOD_ID + ".updater.download"))
					.setStyle(style);
			chat.appendText(TextFormatting.WHITE + "]");
			player.sendMessage(chat);
			return true;
		} else {
			MOLog.info("Matter Overdrive Version %1$s is up to date.", Reference.VERSION);
			return false;
		}
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		String comment = "Should Matter Overdrive check for newer versions, every time the world starts";
		checkForUpdates = config.getBool(ConfigurationHandler.KEY_VERSION_CHECK, ConfigurationHandler.CATEGORY_CLIENT,
				true, comment);
		config.config.get(ConfigurationHandler.CATEGORY_CLIENT, ConfigurationHandler.KEY_VERSION_CHECK, true)
				.setComment(comment);
	}
}