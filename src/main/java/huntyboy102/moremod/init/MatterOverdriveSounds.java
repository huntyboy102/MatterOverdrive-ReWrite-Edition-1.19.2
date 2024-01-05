
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
	public static final RegistryObject<SoundEvent> weaponsPhaserBeam = register("weapons.phaser_beam");
	public static final RegistryObject<SoundEvent> weaponsPhaserSwitchMode = register("weapons.phaser_switch_mode");
	public static final RegistryObject<SoundEvent> scannerBeep = register("scanner_beep");
	public static final RegistryObject<SoundEvent> scannerSuccess = register("scanner_success");
	public static final RegistryObject<SoundEvent> scannerFail = register("scanner_fail");
	public static final RegistryObject<SoundEvent> scannerScanning = register("scanner_scanning");
	public static final RegistryObject<SoundEvent> replicateSuccess = register("replicate_success");
	public static final RegistryObject<SoundEvent> analyzer = register("analyzer");
	public static final RegistryObject<SoundEvent> decomposer = register("decomposer");
	public static final RegistryObject<SoundEvent> machine = register("machine");
	public static final RegistryObject<SoundEvent> transporter = register("transporter");
	public final static RegistryObject<SoundEvent> windy = register("windy");
	public final static RegistryObject<SoundEvent> anomalyConsume = register("anomaly_consume");
	public final static RegistryObject<SoundEvent> electricMachine = register("electric_machine");
	public final static RegistryObject<SoundEvent> forceField = register("force_field");
	public final static RegistryObject<SoundEvent> failedAnimalDie = register("failed_animal_die");
	public final static RegistryObject<SoundEvent> failedAnimalIdleChicken = register("failed_animal_idle_chicken");
	public final static RegistryObject<SoundEvent> failedAnimalIdlePig = register("failed_animal_idle_pig");
	public final static RegistryObject<SoundEvent> failedAnimalIdleCow = register("failed_animal_idle_cow");
	public final static RegistryObject<SoundEvent> failedAnimalIdleSheep = register("failed_animal_idle_sheep");
	public final static RegistryObject<SoundEvent> guiButtonSoft = register("gui.button_soft");
	public final static RegistryObject<SoundEvent> guiButtonLoud = register("gui.button_loud");
	public final static RegistryObject<SoundEvent> guiButtonExpand = register("gui.button_expand");
	public final static RegistryObject<SoundEvent> guiBioticStatUnlock = register("gui.biotic_stat_unlock");
	public final static RegistryObject<SoundEvent> musicTransformation = register("music.transformation");
	public final static RegistryObject<SoundEvent> guiGlitch = register("gui.glitch");
	public final static RegistryObject<SoundEvent> androidTeleport = register("android.teleport");
	public final static RegistryObject<SoundEvent> androidShieldLoop = register("android.shield_loop");
	public final static RegistryObject<SoundEvent> androidShieldHit = register("android.shield_hit");
	public final static RegistryObject<SoundEvent> androidCloakOn = register("android.cloak_on");
	public final static RegistryObject<SoundEvent> androidCloakOff = register("android.cloak_off");
	public final static RegistryObject<SoundEvent> weaponsPhaserRifleShot = register("weapons.phaser_rifle_shot");
	public final static RegistryObject<SoundEvent> weaponsOverheatAlarm = register("weapons.overheat_alarm");
	public final static RegistryObject<SoundEvent> weaponsOverheat = register("weapons.overheat");
	public final static RegistryObject<SoundEvent> weaponsReload = register("weapons.reload");
	public final static RegistryObject<SoundEvent> androidNightVision = register("android.night_vision");
	public final static RegistryObject<SoundEvent> androidPowerDown = register("android.power_down");
	public final static RegistryObject<SoundEvent> weaponsBoltHit = register("weapons.bolt_hit");
	public final static RegistryObject<SoundEvent> weaponsSizzle = register("weapons.sizzle");
	public final static RegistryObject<SoundEvent> weaponsOmniToolHum = register("weapons.omni_tool_hum");
	public final static RegistryObject<SoundEvent> blocksCrateOpen = register("blocks.crate_open");
	public final static RegistryObject<SoundEvent> blocksCrateClose = register("blocks.crate_close");
	public final static RegistryObject<SoundEvent> weaponsLaserRicochet = register("weapons.laser_ricochet");
	public final static RegistryObject<SoundEvent> weaponsLaserFire = register("weapons.laser_fire");
	public final static RegistryObject<SoundEvent> guiQuestComplete = register("gui.quest_complete");
	public final static RegistryObject<SoundEvent> guiQuestStarted = register("gui.quest_started");
	public final static RegistryObject<SoundEvent> weaponsPlasmaShotgunShot = register("weapons.plasma_shotgun_shot");
	public final static RegistryObject<SoundEvent> weaponsPlasmaShotgunCharging = register("weapons.plasma_shotgun_charging");
	public final static RegistryObject<SoundEvent> weaponsSniperRifleFire = register("weapons.sniper_rifle_fire");
	public final static RegistryObject<SoundEvent> androidShieldPowerUp = register("android.shield_power_up");
	public final static RegistryObject<SoundEvent> androidShieldPowerDown = register("android.shield_power_down");
	public final static RegistryObject<SoundEvent> mobsRogueAndroidSay = register("mobs.rogue_android_say");
	public final static RegistryObject<SoundEvent> mobsRogueAndroidDeath = register("mobs.rogue_android_death");
	public final static RegistryObject<SoundEvent> androidShockwave = register("android.shockwave");
	public final static RegistryObject<SoundEvent> fxElectricArc = register("fx.electric_arc");
	public final static RegistryObject<SoundEvent> blocksPylon = register("blocks.pylon");
	public final static RegistryObject<SoundEvent> weaponsExplosiveShot = register("weapons.explosive_shot");

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

	private static RegistryObject<SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, name)));
	}
}
