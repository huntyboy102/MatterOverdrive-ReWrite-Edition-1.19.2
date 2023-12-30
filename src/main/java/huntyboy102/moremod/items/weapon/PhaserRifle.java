package huntyboy102.moremod.items.weapon;

import java.util.List;

import javax.annotation.Nonnull;

import huntyboy102.moremod.entity.weapon.PlasmaBolt;
import huntyboy102.moremod.items.weapon.module.WeaponModuleBarrel;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.weapon.IWeaponModule;
import huntyboy102.moremod.api.weapon.WeaponShot;
import huntyboy102.moremod.client.sound.MOPositionedSound;
import huntyboy102.moremod.client.sound.WeaponSound;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PhaserRifle extends EnergyWeapon {

	public static final int RANGE = 32;
	private static final int HEAT_PER_SHOT = 20;
	private static final int MAX_HEAT = 80;
	private static final int MAX_USE_TIME = 512;
	private static final int ENERGY_PER_SHOT = 1024;

	public PhaserRifle(String name) {
		super(name, RANGE);
		this.bFull3D = true;
		this.leftClickFire = true;
	}

	@Override
	protected int getCapacity() {
		return 32000;
	}

	@Override
	protected int getInput() {
		return 128;
	}

	@Override
	protected int getOutput() {
		return 128;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return MAX_USE_TIME;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vector2f getSlotPosition(int slot, ItemStack weapon) {
		switch (slot) {
		case Reference.MODULE_BATTERY:
			return new Vector2f(170, 115);
		case Reference.MODULE_COLOR:
			return new Vector2f(60, 45);
		case Reference.MODULE_BARREL:
			return new Vector2f(60, 115);
		case Reference.MODULE_SIGHTS:
			return new Vector2f(150, 35);
		default:
			return new Vector2f(205, 80 + ((slot - Reference.MODULE_OTHER) * 22));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vector2f getModuleScreenPosition(int slot, ItemStack weapon) {
		switch (slot) {
		case Reference.MODULE_BATTERY:
			return new Vector2f(165, 80);
		case Reference.MODULE_COLOR:
			return new Vector2f(100, 80);
		case Reference.MODULE_BARREL:
			return new Vector2f(90, 90);
		case Reference.MODULE_SIGHTS:
			return new Vector2f(140, 72);
		}
		return getSlotPosition(slot, weapon);
	}

	@Override
	public boolean supportsModule(int slot, ItemStack weapon) {
		return true;

	}

	@Override
	public boolean supportsModule(ItemStack weapon, ItemStack module) {
		if (module != null && module.getItem() instanceof IWeaponModule
				&& ((IWeaponModule) module.getItem()).getSlot(module) == Reference.MODULE_BARREL) {
			return module.getItemDamage() != WeaponModuleBarrel.HEAL_BARREL_ID && module.getItemDamage() != WeaponModuleBarrel.BLOCK_BARREL_ID && module.getItemDamage() != WeaponModuleBarrel.EXPLOSION_BARREL_ID;
		}
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (hand == EnumHand.OFF_HAND) {
			return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
		}
		playerIn.setActiveHand(hand);

		if (worldIn.isRemote) {
//            for(int i = 0; i < 3; i++)
//                Minecraft.getMinecraft().entityRenderer.itemRenderer.updateEquippedItem();
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public boolean isAlwaysEquipped(ItemStack weapon) {
		return true;
	}

	@Override
	public int getBaseShootCooldown(ItemStack weapon) {
		return 11;
	}

	@Override
	public float getBaseZoom(ItemStack weapon, EntityLivingBase shooter) {
		return 0.2f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isWeaponZoomed(EntityLivingBase entityPlayer, ItemStack weapon) {
		// Fix the requirement to have the button down for a "right click".
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
		return entityPlayer.isHandActive() && entityPlayer.getActiveHand() == EnumHand.MAIN_HAND;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public WeaponSound getFireSound(ItemStack weapon, EntityLivingBase entity) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onShooterClientUpdate(ItemStack itemStack, World world, EntityPlayer entityPlayer,
			boolean sendServerTick) {
		if (Mouse.isButtonDown(0) && hasShootDelayPassed()) {
			if (canFire(itemStack, world, entityPlayer)) {
				itemStack.setTagInfo("LastShot", new NBTTagLong(world.getTotalWorldTime()));
				Vec3d dir = entityPlayer.getLook(1);
				Vec3d pos = getFirePosition(entityPlayer, dir, isWeaponZoomed(entityPlayer, itemStack));
				WeaponShot shot = createClientShot(itemStack, entityPlayer, isWeaponZoomed(entityPlayer, itemStack));
				onClientShot(itemStack, entityPlayer, pos, dir, shot);
				addShootDelay(itemStack);
				sendShootTickToServer(world, shot, dir, pos);
				if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
					if (isWeaponZoomed(entityPlayer, itemStack)) {
						ClientProxy.instance().getClientWeaponHandler()
								.setRecoil(0.5f + getAccuracy(itemStack, entityPlayer, true), 1, 0.05f);
						ClientProxy.instance().getClientWeaponHandler()
								.setCameraRecoil(0.5f + getAccuracy(itemStack, entityPlayer, true) * 0.1f, 1);
						// entityPlayer.hurtTime = 6 + (int) ((getHeat(itemStack) /
						// getMaxHeat(itemStack)) * 8);
						// entityPlayer.maxHurtTime = 15;
					} else {
						ClientProxy.instance().getClientWeaponHandler()
								.setRecoil(2 + getAccuracy(itemStack, entityPlayer, true) * 2, 1, 0.07f);
						ClientProxy.instance().getClientWeaponHandler()
								.setCameraRecoil(0.5f + getAccuracy(itemStack, entityPlayer, true) * 0.5f, 1);
						// entityPlayer.hurtTime = 10 + (int) ((getHeat(itemStack) /
						// getMaxHeat(itemStack)) * 8);
						// entityPlayer.maxHurtTime = 25;
					}
				}
				return;
			} else if (needsRecharge(itemStack)) {
				chargeFromEnergyPack(itemStack, entityPlayer);
			}
		}
		super.onShooterClientUpdate(itemStack, world, entityPlayer, sendServerTick);
	}

	@SideOnly(Side.CLIENT)
	private Vec3d getFirePosition(EntityPlayer entityPlayer, Vec3d dir, boolean isAiming) {
		Vec3d pos = entityPlayer.getPositionEyes(1);
		if (!isAiming) {
			pos = pos.subtract((double) (MathHelper.cos(entityPlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F),
					0, (double) (MathHelper.sin(entityPlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F));
		}
		pos = pos.add(dir.x, dir.y, dir.z);
		return pos;
	}

	@Override
	public boolean onServerFire(ItemStack weapon, EntityLivingBase shooter, WeaponShot shot, Vec3d position, Vec3d dir,
			int delay) {
		if (shooter instanceof EntityPlayer) {
		if (!((EntityPlayer) shooter).capabilities.isCreativeMode) {
		DrainEnergy(weapon, getShootCooldown(weapon), false);
		float newHeat = (getHeat(weapon) + 4) * 2.2f;
		setHeat(weapon, newHeat);
		manageOverheat(weapon, shooter.world, shooter);
		}
		}
		if (!isOverheated(weapon)) {
			PlasmaBolt fire = spawnProjectile(weapon, shooter, position, dir, shot);
			fire.simulateDelay(delay);
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onClientShot(ItemStack weapon, EntityLivingBase shooter, Vec3d position, Vec3d dir, WeaponShot shot) {
		MOPositionedSound sound = new MOPositionedSound(MatterOverdriveSounds.weaponsPhaserRifleShot,
				SoundCategory.PLAYERS, 0.8f + itemRand.nextFloat() * 0.2f, 0.9f + itemRand.nextFloat() * 0.2f);
		sound.setPosition((float) position.x, (float) position.y, (float) position.z);
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		spawnProjectile(weapon, shooter, position, dir, shot);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onProjectileHit(RayTraceResult hit, ItemStack weapon, World world, float amount) {

	}

	@Override
	public PlasmaBolt getDefaultProjectile(ItemStack weapon, EntityLivingBase shooter, Vec3d position, Vec3d dir,
			WeaponShot shot) {
		PlasmaBolt bolt = super.getDefaultProjectile(weapon, shooter, position, dir, shot);
		bolt.setKnockBack(0.1f);
		return bolt;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.BOW;
	}

	@Override
	protected void addCustomDetails(ItemStack weapon, EntityPlayer player, List infos) {

	}

	@Override
	public int getBaseEnergyUse(ItemStack item) {
		return ENERGY_PER_SHOT / getBaseShootCooldown(item);
	}

	@Override
	protected int getBaseMaxHeat(ItemStack item) {
		return MAX_HEAT;
	}

	@Override
	public float getWeaponBaseDamage(ItemStack weapon) {
		return 8;
	}

	@Override
	public boolean canFire(ItemStack itemStack, World world, EntityLivingBase shooter) {
		return !isOverheated(itemStack) && DrainEnergy(itemStack, getShootCooldown(itemStack), true)
				&& !isEntitySpectator(shooter);
	}

	@Override
	public float getShotSpeed(ItemStack weapon, EntityLivingBase shooter) {
		return 4;
	}

	@Override
	public float getWeaponBaseAccuracy(ItemStack weapon, boolean zoomed) {
		return 1f + getHeat(weapon) / (zoomed ? 30f : 10f);
	}

}