
package huntyboy102.moremod.api;

import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.api.matter.IMatterRegistry;
import huntyboy102.moremod.api.renderer.IBionicPartRenderRegistry;
import huntyboy102.moremod.api.starmap.IStarmapRenderRegistry;
import huntyboy102.moremod.api.android.IAndroidStatRegistry;
import huntyboy102.moremod.api.android.IAndroidStatRenderRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface MatterOverdriveAPI {
	static MatterOverdriveAPI getInstance() {
		return MOApi.instance();
	}

	IMatterRegistry getMatterRegistry();

	IAndroidStatRegistry getAndroidStatRegistry();

	IDialogRegistry getDialogRegistry();

	@OnlyIn(Dist.CLIENT)
	IAndroidStatRenderRegistry getAndroidStatRenderRegistry();

	@OnlyIn(Dist.CLIENT)
    IBionicPartRenderRegistry getBionicStatRenderRegistry();

	@OnlyIn(Dist.CLIENT)
    IStarmapRenderRegistry getStarmapRenderRegistry();
}
