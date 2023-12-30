package huntyboy102.moremod.fluids;

import huntyboy102.moremod.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;

public class FluidMatterPlasma extends FluidType {
	public static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/matter_plasma/still");
	public static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/matter_plasma/flowing");
	public FluidMatterPlasma() {
		super(Properties.create()
				.density(8000)
				.viscosity(8000)
				.lightLevel(15)
		);
	}
}


