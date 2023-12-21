
package huntyboy102.moremod.init;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.fluids.FluidMatterPlasma;
import huntyboy102.moremod.fluids.FluidMoltenTritanium;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.internal.ForgeBindings;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class OverdriveFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MOD_ID);

	public static final ResourceLocation MATTER_PLASMA_ID = new ResourceLocation(Reference.MOD_ID, "matter_plasma");
	public static final ResourceLocation MOLTEN_TRITANIUM_ID = new ResourceLocation(Reference.MOD_ID, "molten_tritanium");

	public static final RegistryObject<FlowingFluid> MATTER_PLASMA = FLUIDS.register("matter_plasma", () ->
			new ForgeFlowingFluid.Source(OverdriveFluids.MATTER_PLASMA_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FlOWING_MATTER_PLASMA = FLUIDS.register("flowing_matter_plasma", () ->
			new ForgeFlowingFluid.Flowing(OverdriveFluids.MATTER_PLASMA_PROPERTIES));

	public static final RegistryObject<FlowingFluid> MOLTEN_TRITANIUM = FLUIDS.register("molten_tritanium", () ->
			new ForgeFlowingFluid.Source(OverdriveFluids.MOLTEN_TRITANIUM_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_TRITANIUM = FLUIDS.register("flowing_molten_tritanium", () ->
			new ForgeFlowingFluid.Flowing(OverdriveFluids.MOLTEN_TRITANIUM_PROPERTIES));

	public static final ForgeFlowingFluid.Properties MATTER_PLASMA_PROPERTIES = new ForgeFlowingFluid.Properties(
			MATTER_PLASMA,
			FlOWING_MATTER_PLASMA
	)

	public static void init() {
		matterPlasma = new FluidMatterPlasma("matter_plasma");
		FluidRegistry.registerFluid(matterPlasma);
		FluidRegistry.addBucketForFluid(matterPlasma);

		moltenTritanium = new FluidMoltenTritanium("molten_tritanium");
		FluidRegistry.registerFluid(moltenTritanium);
		FluidRegistry.addBucketForFluid(moltenTritanium);

	}
}
