
package huntyboy102.moremod.handler;

import huntyboy102.moremod.handler.thread.RegisterItemsFromRecipes;
import huntyboy102.moremod.network.packet.client.PacketUpdateMatterRegistry;
import huntyboy102.moremod.util.MOLog;
import matteroverdrive.MatterOverdrive;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.concurrent.Future;

public class MatterRegistrationHandler {
	private Future matterCalculationThread;

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load load) {
		if (!load.getWorld().isRemote && load.getWorld().provider.getDimension() == 0) {
			File matterRegistryFile = getMatterRegistryFile(load.getWorld());
			File customHandlersFile = getCustomHandlersFile(load.getWorld());

			try {
				MatterOverdrive.MATTER_REGISTRY.loadCustomHandlers(customHandlersFile);
			} catch (Exception e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading custom matter handlers");
			}

			try {
				if (MatterOverdrive.MATTER_REGISTRY.needsCalculation(matterRegistryFile)
						&& MatterOverdrive.MATTER_REGISTRY.AUTOMATIC_CALCULATION) {
					try {
						runCalculationThread(load.getWorld());
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "There was a problem calculating Matter from Recipes or Furnaces");
					}
				} else {
					try {
						MatterOverdrive.MATTER_REGISTRY.loadFromFile(matterRegistryFile);
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "There was a problem loading the Matter Registry file.");
						if (MatterOverdrive.MATTER_REGISTRY.AUTOMATIC_CALCULATION) {
							MOLog.log(Level.INFO, e, "Starting automatic matter calculation thread.");
							runCalculationThread(load.getWorld());
						} else {
							MOLog.log(Level.INFO, e,
									"Automatic matter calculation disabled. To enable go to Matter Overdrive configs");
						}
					}
				}
			} catch (Exception e) {
				MOLog.log(Level.ERROR, e,
						"There was a problem while trying to load Matter Registry or trying to Calculate it");
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload unload) {
		if (!unload.getWorld().isRemote && unload.getWorld().provider.getDimension() == 0) {
			MatterOverdrive.MATTER_REGISTRY.unload();
		}
	}

	public void runCalculationThread(World world) {
		File matterRegistryFile = getMatterRegistryFile(world);
		if (matterCalculationThread != null) {
			MOLog.log(Level.INFO, "Old calculation thread is running. Stopping old calculation thread");
			matterCalculationThread.cancel(true);
			matterCalculationThread = null;
		}
		matterCalculationThread = MatterOverdrive.THREAD_POOL.submit(new RegisterItemsFromRecipes(matterRegistryFile));
	}

	public void onRegistrationComplete() {
		PacketUpdateMatterRegistry updateMatterRegistry = new PacketUpdateMatterRegistry(
				MatterOverdrive.MATTER_REGISTRY);

		FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playerEntities.stream()
				.filter(playerMP -> playerMP instanceof EntityPlayerMP)
				.forEach(playerMP -> MatterOverdrive.NETWORK.sendTo(updateMatterRegistry, (EntityPlayerMP) playerMP));
	}

	private File getMatterRegistryFile(World world) {
		File worldDirectory = world.getSaveHandler().getWorldDirectory();
		return new File(worldDirectory.getPath() + "/matter_registry.dat");
	}

	private File getCustomHandlersFile(World world) {
		File worldDirectory = world.getSaveHandler().getWorldDirectory();
		return new File(worldDirectory.getPath() + "/custom_matter.json");
	}
}
