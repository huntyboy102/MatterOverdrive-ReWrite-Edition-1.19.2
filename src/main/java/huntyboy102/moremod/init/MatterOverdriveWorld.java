
package huntyboy102.moremod.init;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.world.DimensionalRifts;
import huntyboy102.moremod.world.MOWorldGen;
import net.minecraft.core.Registry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber
public class MatterOverdriveWorld {
	public static final DeferredRegister<DimensionalRifts> DIMENSIONAL_RIFTS = DeferredRegister.create(Registry.DIMENSION_REGISTRY, Reference.MOD_ID);
	public final MOWorldGen worldGen;
	private final DimensionalRifts dimensionalRifts;

	public MatterOverdriveWorld(ConfigurationHandler configurationHandler) {
		worldGen = new MOWorldGen();
		dimensionalRifts = new DimensionalRifts(1);
		configurationHandler.subscribe(worldGen);
	}

	public void init(ConfigurationHandler configurationHandler) {
		worldGen.init(configurationHandler);
	}

	public void onWorldTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side.isServer()) {
			worldGen.manageBuildingGeneration();
		}
	}

	public DimensionalRifts getDimensionalRifts() {
		return dimensionalRifts;
	}
}