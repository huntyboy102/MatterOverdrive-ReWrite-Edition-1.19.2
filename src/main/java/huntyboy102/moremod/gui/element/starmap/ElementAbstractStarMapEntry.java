
package huntyboy102.moremod.gui.element.starmap;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.gui.element.MOElementButton;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.starmap.data.SpaceBody;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public abstract class ElementAbstractStarMapEntry<T extends SpaceBody> extends MOElementButton {
	public static final ScaleTexture BG = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry.png"), 32, 32).setOffsets(18, 12, 15, 15);
	public static final ScaleTexture BG_FLIPPED = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry_flipped.png"), 32, 32)
			.setOffsets(12, 18, 15, 15);
	public static final ScaleTexture BG_MIDDLE_NORMAL = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry_middle.png"), 32, 32)
			.setOffsets(15, 15, 15, 15).setTextureSize(96, 32);
	public static final ScaleTexture BG_MIDDLE_DOWN = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry_middle.png"), 32, 32)
			.setOffsets(15, 15, 15, 15).setTextureSize(96, 32).setUV(64, 0);
	public static final ScaleTexture BG_CIRCLE = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry_circle.png"), 32, 32)
			.setOffsets(15, 15, 15, 15);
	public static ScaleTexture BG_MIDDLE_OVER = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "holo_list_entry_middle.png"), 32, 32)
			.setOffsets(15, 15, 15, 15).setTextureSize(96, 32).setUV(32, 0);
	protected final T spaceBody;
	protected final ElementGroupList groupList;
	protected final HoloIcon travelIcon;
	protected HoloIcon searchIcon;

	public ElementAbstractStarMapEntry(GuiStarMap gui, ElementGroupList groupList, int width, int height, T spaceBody) {
		super(gui, groupList, 0, 0, spaceBody.getSpaceBodyName(), 0, 0, 0, 0, width, height, "");
		this.spaceBody = spaceBody;
		this.groupList = groupList;
		this.travelIcon = ClientProxy.holoIcons.getIcon("travel_icon");
		this.searchIcon = ClientProxy.holoIcons.getIcon("icon_search");
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float multiply = getMultiply(spaceBody);

		RenderUtils.applyColorWithMultipy(getSpaceBodyColor(spaceBody), multiply);
		if (isSelected(spaceBody)) {
			getBG(spaceBody).render(posX, posY, sizeX - 64, sizeY);
			if (canView(spaceBody, Minecraft.getInstance().player)) {
				BG_MIDDLE_NORMAL.render(posX + sizeX - 64, posY, 32, sizeY);
			}
			if (canTravelTo(spaceBody, Minecraft.getInstance().player)) {
				BG_FLIPPED.render(posX + sizeX - 32, posY, 32, sizeY);
			}
			RenderUtils.applyColorWithMultipy(getSpaceBodyColor(spaceBody), multiply * 0.75f);

		} else {
			if (intersectsWith(mouseX, mouseY)) {
				getBG(spaceBody).render(posX, posY, sizeX - 64, sizeY);
			} else {
				getBG(spaceBody).render(posX, posY, sizeX - 64, sizeY);
			}
		}
		RenderSystem.disableBlend();
	}

	protected ScaleTexture getBG(T spaceBody) {
		return BG;
	}

	float getMultiply(T spaceBody) {
		return 0.1f;
	}

	boolean isSelected(T spaceBody) {
		return groupList.isSelected(this);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		if (isSelected(spaceBody)) {
			float multiply = 1f;
			Color color = getSpaceBodyColor(spaceBody);
			drawElementName(spaceBody, color, multiply);
			int iconsX = 0;

			if (canTravelTo(spaceBody, Minecraft.getInstance().player)) {
				multiply = 0.5f;
				if (intersectsWith(mouseX, mouseY) && mouseX > sizeX - 32 && mouseX < sizeX) {
					multiply = 1f;
				}

				RenderUtils.applyColorWithMultipy(color, multiply);
				ClientProxy.holoIcons.bindSheet();
				ClientProxy.holoIcons.renderIcon(travelIcon, posX + sizeX - 32 + 6, posY + 5);
				iconsX += 32;
			}

			if (canView(spaceBody, Minecraft.getInstance().player)) {
				multiply = 0.5f;
				if (intersectsWith(mouseX, mouseY) && mouseX > sizeX - 64 && mouseX < sizeX - 32) {
					multiply = 1f;
				}

				RenderUtils.applyColorWithMultipy(color, multiply);
				ClientProxy.holoIcons.bindSheet();
				ClientProxy.holoIcons.renderIcon(searchIcon, posX + sizeX - 64 + searchIcon.getOriginalWidth() / 2,
						posY + searchIcon.getOriginalHeight() / 2);
				iconsX += 32;
			}

			multiply = 0.8f;
			Map<HoloIcon, Integer> icons = getIcons(spaceBody);
			if (icons != null) {
				for (Map.Entry<HoloIcon, Integer> entry : icons.entrySet()) {
					if (entry.getValue() != 0) {
						GL11.glEnable(GL11.GL_BLEND);
						RenderUtils.applyColorWithMultipy(getSpaceBodyColor(spaceBody), multiply);
						BG_CIRCLE.render(posX + 128 + iconsX, posY, 32, 32);
						ClientProxy.holoIcons.renderIcon(entry.getKey(),
								posX + iconsX + 128 + 16 - entry.getKey().getOriginalWidth() / 2,
								posY + 16 - entry.getKey().getOriginalHeight() / 2);
						if (entry.getValue() > 0) {
							RenderUtils.drawString(String.valueOf(entry.getValue()), posX + iconsX + 128 + 16 + 3,
									posY + 16 + 3, Reference.COLOR_HOLO, 1);
						}
						iconsX += 32;
					}
				}
			}
		} else {
			drawElementName(spaceBody, getSpaceBodyColor(spaceBody), 0.3f);
			int x = 0;
			Map<HoloIcon, Integer> icons = getIcons(spaceBody);
			if (icons != null) {
				for (Map.Entry<HoloIcon, Integer> entry : icons.entrySet()) {
					if (entry.getValue() != 0) {
						GL11.glEnable(GL11.GL_BLEND);
						RenderUtils.applyColorWithMultipy(getSpaceBodyColor(spaceBody), 0.3f);
						BG_CIRCLE.render(posX + 128 + x, posY, 32, 32);
						ClientProxy.holoIcons.renderIcon(entry.getKey(),
								posX + x + 128 + 16 - entry.getKey().getOriginalWidth() / 2,
								posY + 16 - entry.getKey().getOriginalHeight() / 2);
						if (entry.getValue() > 0) {
							RenderUtils.drawString(String.valueOf(entry.getValue()), posX + x + 128 + 16 + 3,
									posY + 16 + 3, getSpaceBodyColor(spaceBody), 0.6f);
						}
						x += 32;
					}
				}
			}
		}
	}

	protected abstract void drawElementName(T spaceBody, Color color, float multiply);

	protected abstract Map<HoloIcon, Integer> getIcons(T spaceBody);

	@Override
	public void addTooltip(List<String> var1, int mouseX, int mouseY) {
		if (isSelected(spaceBody)) {
			if (canTravelTo(spaceBody, Minecraft.getInstance().player) && mouseX > sizeX - 32 && mouseX < sizeX) {
				var1.add("Travel To");
			} else if (canView(spaceBody, Minecraft.getInstance().player) && mouseX > sizeX - 64
					&& mouseX < sizeX - 32) {
				var1.add("Enter");
			}
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (isSelected(spaceBody)) {
			if (mouseX > sizeX - 32 && mouseX < sizeX) {
				if (canTravelTo(spaceBody, Minecraft.getInstance().player)) {
					onTravelPress();
				} else {
					return false;
				}
			} else if (mouseX > sizeX - 64 && mouseX < sizeX - 32) {
				if (canView(spaceBody, Minecraft.getInstance().player)) {
					onViewPress();
				}
			}
			playSound();
		} else {
			if (mouseX < sizeX - 64) {
				playSound();
				onSelectPress();
				return true;
			}
		}
		return false;
	}

	protected abstract boolean canTravelTo(T spaceBody, Player player);

	protected abstract boolean canView(T spaceBody, Player player);

	protected void playSound() {
		SoundEvent event = getSound();
		if (event != null) {
			MOGuiBase.playSound(event, getSoundVolume(), 0.9f + rand.nextFloat() * 0.2f);
		}
	}

	protected abstract void onViewPress();

	protected abstract void onTravelPress();

	protected abstract void onSelectPress();

	protected Color getSpaceBodyColor(T spaceBody) {
		return Reference.COLOR_HOLO;
	}

	public T getSpaceBody() {
		return spaceBody;
	}
}
