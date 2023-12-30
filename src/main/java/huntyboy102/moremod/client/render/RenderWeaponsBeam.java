
package huntyboy102.moremod.client.render;

import static org.lwjgl.opengl.GL11.GL_ONE;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import huntyboy102.moremod.api.weapon.IWeapon;
import huntyboy102.moremod.client.render.tileentity.TileEntityRendererStation;
import huntyboy102.moremod.items.weapon.EnergyWeapon;
import huntyboy102.moremod.items.weapon.OmniTool;
import huntyboy102.moremod.items.weapon.Phaser;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.StackUtils;
import huntyboy102.moremod.util.WeaponHelper;
import org.lwjgl.opengl.GL11;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.RenderHandler;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.sound.WeaponSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeaponsBeam extends RenderBeam<EntityPlayer> {
	private static final ResourceLocation beamTexture = new ResourceLocation(Reference.PATH_FX + "plasmabeam.png");
	final Map<Entity, WeaponSound> soundMap = new HashMap<>();

	public void onRenderWorldLast(RenderHandler renderHandler, RenderWorldLastEvent event) {
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL_ONE, GL_ONE, 0, 0);

		GlStateManager.pushMatrix();
		GlStateManager.translate(-Minecraft.getMinecraft().player.posX, -Minecraft.getMinecraft().player.posY,
				-Minecraft.getMinecraft().player.posZ);
		renderClient(renderHandler, event.getPartialTicks());
		renderOthers(renderHandler, event.getPartialTicks());
		GlStateManager.popMatrix();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

	public void renderOthers(RenderHandler renderHandler, float ticks) {
		Minecraft.getMinecraft().world.getLoadedEntityList().stream().filter(o -> o instanceof EntityPlayer)
				.filter(player -> !player.equals(Minecraft.getMinecraft().player)).forEach(o -> {
					EntityPlayer player = (EntityPlayer) o;
					if (shouldRenderBeam(player)) {
						renderRaycastedBeam(player.getPositionEyes(ticks).add(0, player.getEyeHeight(), 0),
								player.getLook(0), new Vec3d(-0.5, -0.3, 1), player);
					} else {
						stopWeaponSound(player);
					}
				});
	}

	public void renderClient(RenderHandler renderHandler, float ticks) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		if (shouldRenderBeam(player)) {
			Vec3d pos = player.getPositionEyes(ticks);
			Vec3d look = player.getLook(0);
			renderRaycastedBeam(pos, look, new Vec3d(-0.1, -0.1, 0.15), player);
		} else {
			stopWeaponSound(player);
		}
	}

	@SideOnly(Side.CLIENT)
	private void playWeaponSound(EntityPlayer player, Random random) {
		if (!soundMap.containsKey(player)) {
			ItemStack weaponStack = player.getActiveItemStack();
			if (!StackUtils.isNullOrEmpty(weaponStack) && weaponStack.getItem() instanceof IWeapon) {
				// WeaponSound sound = new WeaponSound(new
				// ResourceLocation(((IWeapon)weaponStack.getItem()).getFireSound(weaponStack,
				// player)), (float)player.posX, (float)player.posY, (float)player.posZ,
				// random.nextFloat() * 0.05f + 0.2f, 1);
				WeaponSound sound = ((IWeapon) weaponStack.getItem()).getFireSound(weaponStack, player);
				soundMap.put(player, sound);
				Minecraft.getMinecraft().getSoundHandler().playSound(sound);
			}

		} else if (soundMap.get(player).isDonePlaying()) {
			stopWeaponSound(player);
			playWeaponSound(player, random);
		} else {
			soundMap.get(player).setPosition((float) player.posX, (float) player.posY, (float) player.posZ);
		}
	}

	private void stopWeaponSound(EntityPlayer entity) {
		if (soundMap.containsKey(entity)) {
			WeaponSound sound = soundMap.get(entity);
			sound.stopPlaying();
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
			soundMap.remove(entity);

		}
	}

	@Override
	protected boolean shouldRenderBeam(EntityPlayer entity) {
		return entity.isHandActive() && !StackUtils.isNullOrEmpty(entity.getActiveItemStack())
				&& (entity.getActiveItemStack().getItem() instanceof Phaser
						|| entity.getActiveItemStack().getItem() instanceof OmniTool);
	}

	@Override
	protected void onBeamRaycastHit(RayTraceResult hit, EntityPlayer caster) {
		ItemStack weaponStack = caster.getActiveItemStack();
		if (!StackUtils.isNullOrEmpty(weaponStack) && weaponStack.getItem() instanceof EnergyWeapon) {
			((EnergyWeapon) weaponStack.getItem()).onProjectileHit(hit, weaponStack, caster.world, 1);
			if (weaponStack.getItem() instanceof OmniTool && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
				GlStateManager.pushMatrix();
				RenderUtils.applyColorWithMultipy(getBeamColor(caster),
						0.5f + (float) (1 + Math.sin(caster.world.getWorldTime() * 0.5f)) * 0.5f);
				Minecraft.getMinecraft().renderEngine.bindTexture(TileEntityRendererStation.glowTexture);
				GlStateManager.translate(hit.getBlockPos().getX() + 0.5, hit.getBlockPos().getY() + 0.5,
						hit.getBlockPos().getZ() + 0.5);
				GlStateManager.translate(hit.sideHit.getDirectionVec().getX() * 0.5,
						hit.sideHit.getDirectionVec().getY() * 0.5, hit.sideHit.getDirectionVec().getZ() * 0.5);
				if (hit.sideHit == EnumFacing.SOUTH) {
					GlStateManager.rotate(90, 1, 0, 0);

				} else if (hit.sideHit == EnumFacing.NORTH) {
					GlStateManager.rotate(90, -1, 0, 0);
				} else if (hit.sideHit == EnumFacing.EAST) {
					GlStateManager.rotate(90, 0, 0, -1);
				} else if (hit.sideHit == EnumFacing.WEST) {
					GlStateManager.rotate(90, 0, 0, 1);
				} else if (hit.sideHit == EnumFacing.DOWN) {
					GlStateManager.rotate(180, 1, 0, 0);
				}
				GlStateManager.scale(1, 1.5 + Math.sin(caster.world.getWorldTime() * 0.5) * 0.5, 1);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	protected void onBeamRender(EntityPlayer caster) {
		playWeaponSound(caster, random);
	}

	@Override
	protected Color getBeamColor(EntityPlayer caster) {
		return new Color(WeaponHelper.getColor(caster.getActiveItemStack()));
	}

	@Override
	protected ResourceLocation getBeamTexture(EntityPlayer caster) {
		ItemStack weaponStack = caster.getActiveItemStack();
		if (!StackUtils.isNullOrEmpty(weaponStack) && weaponStack.getItem() instanceof IWeapon) {
			if (weaponStack.getItem() instanceof Phaser) {
				return beamTexture;

			} else if (weaponStack.getItem() instanceof OmniTool) {
				return beamTexture;
			}
		}
		return null;
	}

	@Override
	protected float getBeamMaxDistance(EntityPlayer caster) {
		int range = Phaser.RANGE;
		ItemStack weaponStack = caster.getActiveItemStack();
		if (!StackUtils.isNullOrEmpty(weaponStack) && weaponStack.getItem() instanceof IWeapon) {
			range = ((IWeapon) weaponStack.getItem()).getRange(weaponStack);
		}
		return range;
	}

	@Override
	protected float getBeamThickness(EntityPlayer caster) {
		ItemStack weaponStack = caster.getActiveItemStack();
		if (!StackUtils.isNullOrEmpty(weaponStack) && weaponStack.getItem() instanceof IWeapon) {
			if (weaponStack.getItem() instanceof Phaser) {
				return 0.03f;
			} else if (weaponStack.getItem() instanceof OmniTool) {
				return 0.07f;
			}
		}
		return 0.05f;
	}
}
