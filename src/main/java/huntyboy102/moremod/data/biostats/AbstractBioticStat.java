
package huntyboy102.moremod.data.biostats;

import java.util.ArrayList;
import java.util.List;

import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.client.render.HoloIcons;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.api.android.BionicStatGuiInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractBioticStat implements IBioticStat {
	protected String name;
	boolean showOnHud;
	boolean showOnWheel;
	@OnlyIn(Dist.CLIENT)
    HoloIcon icon;
	private int xp;
	private IBioticStat root;
	private BionicStatGuiInfo guiInfo;
	private boolean rootMaxLevel;
	private List<IBioticStat> competitors;
	private List<ItemStack> requiredItems;
	private List<IBioticStat> enabledBlacklist;
	private int maxLevel;

	public AbstractBioticStat(String name, int xp) {
		this.name = name;
		this.xp = xp;
		competitors = new ArrayList<>();
		requiredItems = new ArrayList<>();
		enabledBlacklist = new ArrayList<>();
		maxLevel = 1;
	}

	@Override
	public String getUnlocalizedName() {
		return name;
	}

	protected String getUnlocalizedDetails() {
		return "biotic_stat." + name + ".details";
	}

	@Override
	public String getDisplayName(AndroidPlayer androidPlayer, int level) {
		return MOStringHelper.translateToLocal("biotic_stat." + name + ".name");
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		return checkBlacklistActive(android, level);
	}

	public String getDetails(int level) {
		return MOStringHelper.translateToLocal("biotic_stat." + name + ".details");
	}

	@Override
	public boolean canBeUnlocked(AndroidPlayer android, int level) {
		// if the root is not unlocked then this stat can't be unlocked
		if (root != null && !android.isUnlocked(root, rootMaxLevel ? root.maxLevel() : 1)) {
			return false;
		}
		if (isLocked(android, level)) {
			return false;
		}
		if (requiredItems.size() > 0 && !android.getPlayer().getAbilities().instabuild) {
			for (ItemStack item : requiredItems) {

				if (!hasItem(android, item)) {
					return false;
				}
			}
		}
		return android.isAndroid()
				&& (android.getPlayer().getAbilities().instabuild || android.getPlayer().experienceLevel >= xp);
	}

	@Override
	public boolean isLocked(AndroidPlayer androidPlayer, int level) {
		return areCompeditrosUnlocked(androidPlayer);
	}

	protected boolean hasItem(AndroidPlayer player, ItemStack stack) {
		int amountCount = stack.getCount();

		for (int i = 0; i < player.getPlayer().getInventory().getContainerSize(); i++) {
			ItemStack s = player.getPlayer().getInventory().getItem(i);
			if (!s.isEmpty() && s.sameItem(stack)) {
				amountCount -= s.getCount();
			}
		}

		return amountCount <= 0;
	}

	@Override
	public void onUnlock(AndroidPlayer android, int level) {
		android.getPlayer().addExperienceLevel(-xp);
		consumeItems(android);
	}

	@Override
	public void onUnlearn(AndroidPlayer androidPlayer, int level) {

	}

	// consume all the necessary items from the player inventory
	// does not check if the items exist
	protected void consumeItems(AndroidPlayer androidPlayer) {
		for (ItemStack itemStack : requiredItems) {
			int itemCount = itemStack.getCount();
			for (int j = 0; j < androidPlayer.getPlayer().getInventory().getContainerSize(); j++) {
				ItemStack pStack = androidPlayer.getPlayer().getInventory().getItem(j);
				if (!pStack.isEmpty() && pStack.sameItem(itemStack)) {
					int countShouldTake = Math.min(itemCount, pStack.getCount());
					androidPlayer.getPlayer().getInventory().decrStackSize(j, countShouldTake);
					itemCount -= countShouldTake;
				}

				if (itemCount <= 0) {
					return;
				}
			}
		}
	}

	@Override
	public void onTooltip(AndroidPlayer android, int level, List<String> list, int mouseX, int mouseY) {
		String name = ChatFormatting.BOLD + getDisplayName(android, level);
		if (maxLevel() > 1) {
			name += ChatFormatting.RESET + String.format(" [%s/%s]", level, maxLevel());
		}
		list.add(ChatFormatting.WHITE + name);
		String details = getDetails(level + 1);
		String[] detailsSplit = details.split("/n/");
		for (String detail : detailsSplit) {
			list.add(ChatFormatting.GRAY + detail);
		}
		if (root != null) {
			String rootLevel = "";
			if (root.maxLevel() > 1) {
				if (rootMaxLevel) {
					rootLevel = " " + root.maxLevel();
				}
			}
			list.add(ChatFormatting.DARK_AQUA + MOStringHelper.translateToLocal("gui.tooltip.parent") + ": "
					+ ChatFormatting.GOLD + String.format("[%s%s]", root.getDisplayName(android, 0), rootLevel));
		}

		StringBuilder requires = new StringBuilder();
		if (requiredItems.size() > 0) {
			for (ItemStack itemStack : requiredItems) {
				if (requires.length() > 0) {
					requires.append(ChatFormatting.GRAY + ", ");
				}
				if (itemStack.getCount() > 1) {
					requires.append(ChatFormatting.DARK_GREEN.toString()).append(itemStack.getCount()).append("x");
				}

				requires.append(ChatFormatting.DARK_GREEN + "[").append(itemStack.getDisplayName()).append("]");
			}
		}

		if (requires.length() > 0) {
			list.add(ChatFormatting.DARK_AQUA + MOStringHelper.translateToLocal("gui.tooltip.requires") + ": "
					+ requires);
		}

		if (competitors.size() > 0) {
			String locks = ChatFormatting.RED + MOStringHelper.translateToLocal("gui.tooltip.locks") + ": ";
			for (IBioticStat compeditor : competitors) {
				locks += String.format("[%s] ", compeditor.getDisplayName(android, 0));
			}
			list.add(locks);
		}

		if (level < maxLevel()) {
			list.add((android.getPlayer().experienceLevel < xp ? ChatFormatting.RED : ChatFormatting.GREEN) + "XP: "
					+ xp);
		}
	}

	public boolean checkBlacklistActive(AndroidPlayer androidPlayer, int level) {
		for (IBioticStat stat : enabledBlacklist) {
			if (stat.isActive(androidPlayer, level)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void registerIcons(TextureManager textureMap, HoloIcons holoIcons) {
		icon = holoIcons.registerIcon(textureMap, "biotic_stat_" + name, 18);
	}

	public void addReqiredItm(ItemStack stack) {
		requiredItems.add(stack);
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return showOnHud;
	}

	@Override
	public boolean showOnWheel(AndroidPlayer androidPlayer, int level) {
		return showOnWheel;
	}

	@Override
	public int maxLevel() {
		return maxLevel;
	}

	public IBioticStat getRoot() {
		return root;
	}

	public void setRoot(IBioticStat stat, boolean rootMaxLevel) {
		this.root = stat;
		this.rootMaxLevel = rootMaxLevel;
	}

	public void addCompetitor(IBioticStat stat) {
		this.competitors.add(stat);
	}

	public void removeCompetitor(IBioticStat competitor) {
		this.competitors.remove(competitor);
	}

	public List<IBioticStat> getCompetitors() {
		return competitors;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setShowOnHud(boolean showOnHud) {
		this.showOnHud = showOnHud;
	}

	public void setShowOnWheel(boolean showOnWheel) {
		this.showOnWheel = showOnWheel;
	}

	public void setGuiInfo(BionicStatGuiInfo guiInfo) {
		this.guiInfo = guiInfo;
	}

	@Override
	public BionicStatGuiInfo getGuiInfo(AndroidPlayer androidPlayer, int level) {
		return guiInfo;
	}

	public List<ItemStack> getRequiredItems() {
		return requiredItems;
	}

	public List<IBioticStat> getEnabledBlacklist() {
		return enabledBlacklist;
	}

	public void addToEnabledBlacklist(IBioticStat stat) {
		enabledBlacklist.add(stat);
	}

	@Override
	public HoloIcon getIcon(int level) {
		return icon;
	}

	@Override
	public int getXP(AndroidPlayer androidPlayer, int level) {
		return xp;
	}

	public boolean areCompeditrosUnlocked(AndroidPlayer androidPlayer) {
		if (competitors.size() > 0) {
			for (IBioticStat competitor : competitors) {
				if (androidPlayer.isUnlocked(competitor, 0)) {
					return true;
				}
			}
		}
		return false;
	}
}
