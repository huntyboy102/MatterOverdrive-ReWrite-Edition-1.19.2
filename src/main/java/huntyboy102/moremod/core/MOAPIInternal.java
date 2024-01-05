
package huntyboy102.moremod.core;

import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.MatterOverdriveAPI;
import huntyboy102.moremod.api.android.IAndroidStatRegistry;
import huntyboy102.moremod.api.android.IAndroidStatRenderRegistry;
import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.api.matter.IMatterRegistry;
import huntyboy102.moremod.api.renderer.IBionicPartRenderRegistry;
import huntyboy102.moremod.api.starmap.IStarmapRenderRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MOAPIInternal implements MatterOverdriveAPI {
	public static final MOAPIInternal INSTANCE = new MOAPIInternal();

	@Override
	public IMatterRegistry getMatterRegistry() {
		return MatterOverdriveRewriteEdition.MATTER_REGISTRY;
	}

	@Override
	public IAndroidStatRegistry getAndroidStatRegistry() {
		return MatterOverdriveRewriteEdition.STAT_REGISTRY;
	}

	@Override
	public IDialogRegistry getDialogRegistry() {
		return MatterOverdriveRewriteEdition.DIALOG_REGISTRY;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IAndroidStatRenderRegistry getAndroidStatRenderRegistry() {
		return ClientProxy.renderHandler.getStatRenderRegistry();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBionicPartRenderRegistry getBionicStatRenderRegistry() {
		return ClientProxy.renderHandler.getBionicPartRenderRegistry();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IStarmapRenderRegistry getStarmapRenderRegistry() {
		return ClientProxy.renderHandler.getStarmapRenderRegistry();
	}
}
