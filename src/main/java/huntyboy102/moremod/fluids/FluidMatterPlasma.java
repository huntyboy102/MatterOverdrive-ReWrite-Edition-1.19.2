
package huntyboy102.moremod.fluids;

import huntyboy102.moremod.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidMatterPlasma extends ForgeFlowingFluid {
	public FluidMatterPlasma() {
		super(new Properties(() -> Fluids.EMPTY, () -> Fluids.EMPTY,
				new ResourceLocation(Reference.MOD_ID, "fluids/matter_plasma/still"),
				new ResourceLocation(Reference.MOD_ID, "fluids/matter_plasma/flowing"))
				.density(8000)
				.viscosity(8000)
				.luminosity(15));
	}
}
