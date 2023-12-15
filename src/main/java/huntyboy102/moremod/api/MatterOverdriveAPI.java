
package huntyboy102.moremod.api;

import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.api.matter.IMatterRegistry;
import huntyboy102.moremod.api.renderer.IBionicPartRenderRegistry;
import huntyboy102.moremod.api.starmap.IStarmapRenderRegistry;
import matteroverdrive.api.android.IAndroidStatRegistry;
import matteroverdrive.api.android.IAndroidStatRenderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface MatterOverdriveAPI {
	static MatterOverdriveAPI getInstance() {
		return MOApi.instance();
	}

	IMatterRegistry getMatterRegistry();

	IAndroidStatRegistry getAndroidStatRegistry();

	IDialogRegistry getDialogRegistry();

	@SideOnly(Side.CLIENT)
	IAndroidStatRenderRegistry getAndroidStatRenderRegistry();

	@SideOnly(Side.CLIENT)
    IBionicPartRenderRegistry getBionicStatRenderRegistry();

	@SideOnly(Side.CLIENT)
    IStarmapRenderRegistry getStarmapRenderRegistry();
}
