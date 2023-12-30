
package huntyboy102.moremod.fluids;

import huntyboy102.moremod.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;

public class FluidMoltenTritanium extends FluidType {
	public static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/molten_tritanium/still");
	public static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/molten_tritanium/flowing");

	public FluidMoltenTritanium(String fluidName) {
        super(Properties.create()
				.viscosity(6000)
				.lightLevel(15)
				.temperature(2000)
		);
	}
}
