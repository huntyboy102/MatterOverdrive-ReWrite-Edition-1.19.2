package huntyboy102.moremod.client.render;

import huntyboy102.moremod.api.inventory.IBionicPart;
import huntyboy102.moremod.api.renderer.IBionicPartRenderRegistry;
import huntyboy102.moremod.api.renderer.IBionicPartRenderer;

import java.util.HashMap;
import java.util.Map;

public class AndroidBionicPartRenderRegistry implements IBionicPartRenderRegistry {
	private final Map<Class<? extends IBionicPart>, IBionicPartRenderer> rendererMap;

	public AndroidBionicPartRenderRegistry() {
		rendererMap = new HashMap<>();
	}

	@Override
	public void register(Class<? extends IBionicPart> partClass, IBionicPartRenderer renderer) {
		rendererMap.put(partClass, renderer);
	}

	@Override
	public IBionicPartRenderer removeRenderer(Class<? extends IBionicPart> partClass) {
		return rendererMap.remove(partClass);
	}

	@Override
	public IBionicPartRenderer getRenderer(Class<? extends IBionicPart> partClass) {
		return rendererMap.get(partClass);
	}
}
