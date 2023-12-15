
package huntyboy102.moremod.core;

import huntyboy102.moremod.proxy.ClientProxy;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.MatterOverdriveAPI;
import matteroverdrive.api.android.IAndroidStatRegistry;
import matteroverdrive.api.android.IAndroidStatRenderRegistry;
import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.api.matter.IMatterRegistry;
import huntyboy102.moremod.api.renderer.IBionicPartRenderRegistry;
import huntyboy102.moremod.api.starmap.IStarmapRenderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MOAPIInternal implements MatterOverdriveAPI {
	public static final MOAPIInternal INSTANCE = new MOAPIInternal();

	@Override
	public IMatterRegistry getMatterRegistry() {
		return MatterOverdrive.MATTER_REGISTRY;
	}

	@Override
	public IAndroidStatRegistry getAndroidStatRegistry() {
		return MatterOverdrive.STAT_REGISTRY;
	}

	@Override
	public IDialogRegistry getDialogRegistry() {
		return MatterOverdrive.DIALOG_REGISTRY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IAndroidStatRenderRegistry getAndroidStatRenderRegistry() {
		return ClientProxy.renderHandler.getStatRenderRegistry();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBionicPartRenderRegistry getBionicStatRenderRegistry() {
		return ClientProxy.renderHandler.getBionicPartRenderRegistry();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IStarmapRenderRegistry getStarmapRenderRegistry() {
		return ClientProxy.renderHandler.getStarmapRenderRegistry();
	}
}
