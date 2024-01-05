
package huntyboy102.moremod.init;

import java.lang.reflect.Field;

import huntyboy102.moremod.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryObject;

public class MatterOverdriveSounds {
	public static final RegistryObject<SoundEvent> weaponsPhaserBeam = newEvent("weapons.phaser_beam");
	public static final RegistryObject<SoundEvent> weaponsPhaserSwitchMode = newEvent("weapons.phaser_switch_mode");
	public static final RegistryObject<SoundEvent> scannerBeep = newEvent("scanner_beep");
	public static final RegistryObject<SoundEvent> scannerSuccess = newEvent("scanner_success");
	public static final RegistryObject<SoundEvent> scannerFail = newEvent("scanner_fail");
	public static final RegistryObject<SoundEvent> scannerScanning = newEvent("scanner_scanning");
	public static final RegistryObject<SoundEvent> replicateSuccess = newEvent("replicate_success");
	public static final RegistryObject<SoundEvent> analyzer = newEvent("analyzer");
	public static final RegistryObject<SoundEvent> decomposer = newEvent("decomposer");
	public static final RegistryObject<SoundEvent> machine = newEvent("machine");
	public static final RegistryObject<SoundEvent> transporter = newEvent("transporter");
	public final static RegistryObject<SoundEvent> windy = newEvent("windy");
	public final static RegistryObject<SoundEvent> anomalyConsume = newEvent("anomaly_consume");
	public final static RegistryObject<SoundEvent> electricMachine = newEvent("electric_machine");
	public final static RegistryObject<SoundEvent> forceField = newEvent("force_field");
	public final static RegistryObject<SoundEvent> failedAnimalDie = newEvent("failed_animal_die");
	public final static RegistryObject<SoundEvent> failedAnimalIdleChicken = newEvent("failed_animal_idle_chicken");
	public final static RegistryObject<SoundEvent> failedAnimalIdlePig = newEvent("failed_animal_idle_pig");
	public final static RegistryObject<SoundEvent> failedAnimalIdleCow = newEvent("failed_animal_idle_cow");
	public final static RegistryObject<SoundEvent> failedAnimalIdleSheep = newEvent("failed_animal_idle_sheep");
	public final static RegistryObject<SoundEvent> guiButtonSoft = newEvent("gui.button_soft");
	public final static RegistryObject<SoundEvent> guiButtonLoud = newEvent("gui.button_loud");
	public final static RegistryObject<SoundEvent> guiButtonExpand = newEvent("gui.button_expand");
	public final static RegistryObject<SoundEvent> guiBioticStatUnlock = newEvent("gui.biotic_stat_unlock");
	public final static RegistryObject<SoundEvent> musicTransformation = newEvent("music.transformation");
	public final static RegistryObject<SoundEvent> guiGlitch = newEvent("gui.glitch");
	public final static RegistryObject<SoundEvent> androidTeleport = newEvent("android.teleport");
	public final static RegistryObject<SoundEvent> androidShieldLoop = newEvent("android.shield_loop");
	public final static RegistryObject<SoundEvent> androidShieldHit = newEvent("android.shield_hit");
	public final static RegistryObject<SoundEvent> androidCloakOn = newEvent("android.cloak_on");
	public final static RegistryObject<SoundEvent> androidCloakOff = newEvent("android.cloak_off");
	public final static RegistryObject<SoundEvent> weaponsPhaserRifleShot = newEvent("weapons.phaser_rifle_shot");
	public final static RegistryObject<SoundEvent> weaponsOverheatAlarm = newEvent("weapons.overheat_alarm");
	public final static RegistryObject<SoundEvent> weaponsOverheat = newEvent("weapons.overheat");
	public final static RegistryObject<SoundEvent> weaponsReload = newEvent("weapons.reload");
	public final static RegistryObject<SoundEvent> androidNightVision = newEvent("android.night_vision");
	public final static RegistryObject<SoundEvent> androidPowerDown = newEvent("android.power_down");
	public final static RegistryObject<SoundEvent> weaponsBoltHit = newEvent("weapons.bolt_hit");
	public final static RegistryObject<SoundEvent> weaponsSizzle = newEvent("weapons.sizzle");
	public final static RegistryObject<SoundEvent> weaponsOmniToolHum = newEvent("weapons.omni_tool_hum");
	public final static RegistryObject<SoundEvent> blocksCrateOpen = newEvent("blocks.crate_open");
	public final static RegistryObject<SoundEvent> blocksCrateClose = newEvent("blocks.crate_close");
	public final static RegistryObject<SoundEvent> weaponsLaserRicochet = newEvent("weapons.laser_ricochet");
	public final static RegistryObject<SoundEvent> weaponsLaserFire = newEvent("weapons.laser_fire");
	public final static RegistryObject<SoundEvent> guiQuestComplete = newEvent("gui.quest_complete");
	public final static RegistryObject<SoundEvent> guiQuestStarted = newEvent("gui.quest_started");
	public final static RegistryObject<SoundEvent> weaponsPlasmaShotgunShot = newEvent("weapons.plasma_shotgun_shot");
	public final static RegistryObject<SoundEvent> weaponsPlasmaShotgunCharging = newEvent("weapons.plasma_shotgun_charging");
	public final static RegistryObject<SoundEvent> weaponsSniperRifleFire = newEvent("weapons.sniper_rifle_fire");
	public final static RegistryObject<SoundEvent> androidShieldPowerUp = newEvent("android.shield_power_up");
	public final static RegistryObject<SoundEvent> androidShieldPowerDown = newEvent("android.shield_power_down");
	public final static RegistryObject<SoundEvent> mobsRogueAndroidSay = newEvent("mobs.rogue_android_say");
	public final static RegistryObject<SoundEvent> mobsRogueAndroidDeath = newEvent("mobs.rogue_android_death");
	public final static RegistryObject<SoundEvent> androidShockwave = newEvent("android.shockwave");
	public final static RegistryObject<SoundEvent> fxElectricArc = newEvent("fx.electric_arc");
	public final static RegistryObject<SoundEvent> blocksPylon = newEvent("blocks.pylon");
	public final static RegistryObject<SoundEvent> weaponsExplosiveShot = newEvent("weapons.explosive_shot");

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

	private static RegistryObject<SoundEvent> newEvent(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, name)));
	}
}
