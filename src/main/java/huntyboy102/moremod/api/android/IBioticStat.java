
package huntyboy102.moremod.api.android;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidAttributes;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.network.packet.server.PacketBioticActionKey;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.client.render.HoloIcons;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public interface IBioticStat {
	/**
	 * The unlocalized name of the Bionic Stat. This is also used in the Matter
	 * Overdrive console commands.
	 *
	 * @return the name itself without any prefixes like biotic_stat.stat
	 */
	String getUnlocalizedName();

	/**
	 * Returns the display name of the stat. By default it returns the translated
	 * unlocalized name. Used mainly for the GUI display name.
	 *
	 * @param androidPlayer The Android Player
	 * @param level         The level of the Bionic Stat
	 * @return The Display name of the Bionic Stat
	 */
	String getDisplayName(AndroidPlayer androidPlayer, int level);

	/**
	 * Tells if the stat can be unlock in the Android Station GUI by the given
	 * Android Player.
	 *
	 * @param android The Android Player.
	 * @param level   The Level that will be unlocked.
	 * @return If the Bionic stat can be Unlocked.
	 */
	boolean canBeUnlocked(AndroidPlayer android, int level);

	/**
	 * Called on each Player Update tick on a valid Android Player. Called only if
	 * the Ability is Unlocked and is Enabled.
	 *
	 * @param android The Android Player.
	 * @param level   The unlocked stat level.
	 */
	void onAndroidUpdate(AndroidPlayer android, int level);

	/**
	 * Called then the Ability Action Key is pressed. This is also called on the
	 * server by {@link PacketBioticActionKey}
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The unlocked stat level.
	 * @param server        is this method called server side.
	 */
	void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server);

	/**
	 * Called then the Client hits a keyboard key. Called only on the Client.
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The Unlocked stat level.
	 * @param keycode       the Keyboard Key Binding.
	 * @param down          The state of the pressed key.
	 */
	@OnlyIn(Dist.CLIENT)
	void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down);

	/**
	 * Called on these Living Events: LivingEvent.LivingJumpEvent, LivingAttackEvent
	 * and LivingHurtEvent.
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The unlocked stat level.
	 * @param event         The Living update itself. It can be either
	 *                      LivingEvent.LivingJumpEvent, LivingAttackEvent or
	 *                      LivingHurtEvent.
	 */
	void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event);

	/**
	 * Called when an Ability is unlocked.
	 *
	 * @param android The Android Player that unlocked the ability.
	 * @param level   The level of the unlocked ability.
	 */
	void onUnlock(AndroidPlayer android, int level);

	/**
	 * Called when the Ability is unlearned by the player. Used mainly to reset
	 * changed stuff.
	 *
	 * @param androidPlayer the android player.
	 * @param level         the unlocked level.
	 */
	void onUnlearn(AndroidPlayer androidPlayer, int level);

	/**
	 * Called on the GUI tooltip.
	 *
	 * @param android The Android Player.
	 * @param level   The unlocked stat level.
	 * @param list    The list of all Tooltips. Here is where you add the tooltips
	 *                to be displayed.
	 * @param mouseX  The mouse x inate.
	 * @param mouseY  The mouse y inate.
	 */
	void onTooltip(AndroidPlayer android, int level, List<String> list, int mouseX, int mouseY);

	/**
	 * Called each Player tick, even if the stat isn't unlocked. Compared to
	 * {@link IBioticStat#onAndroidUpdate(AndroidPlayer, int)} which is called ONLY
	 * if the stat IS unlocked.
	 *
	 * @param androidPlayer
	 * @param level
	 * @param enabled
	 */
	void changeAndroidStats(AndroidPlayer androidPlayer, int level, boolean enabled);

	/**
	 * A multimap of all the
	 * {@link net.minecraft.world.entity.ai.attributes.AttributeModifier}. Each
	 * {@link net.minecraft.world.entity.ai.attributes.AttributeModifier} is assigned to a
	 * unlocalized name of the Player's Attribute. Appliance is managed
	 * automatically.
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The unlocked stat level.
	 * @return A multimap of all
	 *         {@link net.minecraft.world.entity.ai.attributes.AttributeModifier}.
	 * @see AndroidAttributes
	 * @see net.minecraft.world.entity.ai.attributes.Attributes;
	 */
	Multimap<String, AttributeModifier> attributes(AndroidPlayer androidPlayer, int level);

	/**
	 * Returns the root of this stat.
	 *
	 * @return the root stat.
	 */
	IBioticStat getRoot();

	/**
	 * A list of required items for unlocking the Bionic stat.
	 *
	 * @return The Item list for unlocking the stat.
	 */
	List<ItemStack> getRequiredItems();

	/**
	 * Is the Bionic stat Enabled. This will display it Red if disabled and will not
	 * call {@link IBioticStat#onAndroidUpdate(AndroidPlayer, int)}.
	 *
	 * @param android The Android Player
	 * @param level   The unlocked stat level
	 * @return Is the stat unlocked.
	 */
	boolean isEnabled(AndroidPlayer android, int level);

	/**
	 * Is the Bionic stat Active. This will display the Ability on the GUI as
	 * Active. This is mainly used by the Ability stats that drain energy while
	 * active.
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The unlocked stat level
	 * @return If the stat is active.
	 */
	boolean isActive(AndroidPlayer androidPlayer, int level);

	/**
	 * Is the Bionic Stat visible on the Android HUD.
	 *
	 * @param android The Android Player
	 * @param level   The unlocked stat level
	 * @return Is the stat visible on the HUD.
	 */
	boolean showOnHud(AndroidPlayer android, int level);

	/**
	 * Is the Bionic stat shown on the Android's Ability Wheel. This is mainly used
	 * mainly for ability stat such as the Clock, Teleport or the Shield.
	 *
	 * @param androidPlayer The Android Player.
	 * @param level         The unlocked stat level.
	 * @return Is the stat on the Ability Wheel.
	 */
	boolean showOnWheel(AndroidPlayer androidPlayer, int level);

	/**
	 * Called to register any custom Icons
	 *
	 * @param holoIcons The TextureMap for the Holo Icons
	 */
	void registerIcons(TextureMap textureMap, HoloIcons holoIcons);

	/**
	 * Gets the Bionic Stat Icon. Called only on the Client.
	 *
	 * @param level The unlocked stat level.
	 * @return The stat Icon.
	 */
	@OnlyIn(Dist.CLIENT)
	HoloIcon getIcon(int level);

	/**
	 * The maximum level the Bionic stat can reach.
	 *
	 * @return The max stat level.
	 */
	int maxLevel();

	/**
	 * Get the amount of XP required to unlock at a certain level
	 *
	 * @param androidPlayer The Android Player
	 * @param level         The stat level.
	 * @return how much XP is required to unlock
	 */
	int getXP(AndroidPlayer androidPlayer, int level);

	/**
	 * Gets the delay time the ability has. The time will be displayed on the
	 * ability icon. Time is in world ticks.
	 *
	 * @param androidPlayer the android player.
	 * @param level         the stat level.
	 * @return the delay time of the ability.
	 */
	int getDelay(AndroidPlayer androidPlayer, int level);

	/**
	 * Is the stat locked and cannot be obtained at all.
	 *
	 * @param androidPlayer the android player
	 * @param level         the stat level
	 * @return is the stat locked
	 */
	boolean isLocked(AndroidPlayer androidPlayer, int level);

	/**
	 * Gets the GUI info for the Android Station GUI
	 *
	 * @param androidPlayer the android player
	 * @param level         the android stat level
	 * @return
	 */
	BionicStatGuiInfo getGuiInfo(AndroidPlayer androidPlayer, int level);
}
