
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.gravity.AnomalySuppressor;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.blocks.BlockGravitationalAnomaly;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOBlockHelper;
import huntyboy102.moremod.fx.GravitationalStabilizerBeamParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.awt.*;

public class TileEntityMachineGravitationalStabilizer extends MOTileEntityMachine implements IMOTickable {
	public static Color color1 = new Color(0xFFFFFF);
	public static Color color2 = new Color(0xFF0000);
	public static Color color3 = new Color(0x115A84);
	BlockHitResult hit;

	public TileEntityMachineGravitationalStabilizer() {
		super(4);
	}

	@Override
	public void update() {
		super.update();

		if (level.isClientSide) {
			spawnParticles(level);
			hit = seacrhForAnomalies(level);
		}
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	BlockHitResult seacrhForAnomalies(Level world) {
		Direction front = world.getBlockState(getBlockPos()).getValue(MOBlock.PROPERTY_DIRECTION);
		for (int i = 1; i < 64; i++) {
			BlockPos offsetPos = getBlockPos().relative(front, i);
			BlockState blockState = world.getBlockState(offsetPos);

			if (blockState.getBlock() instanceof BlockGravitationalAnomaly || blockState.getMaterial().isSolidBlocking()) {
				Vec3i offset = new Vec3i(
						Math.abs(front.getStepX() * 0.5),
						Math.abs(front.getStepY() * 0.5),
						Math.abs(front.getStepZ() * 0.5)
				);

				Vec3 hitPos = new Vec3(offsetPos).subtract(offset.getX(), offset.getY(), offset.getZ());

				return new BlockHitResult(hitPos, front.getOpposite(), offsetPos);
			}
		}
		return null;
	}

	void manageAnomalies(Level world) {
		hit = seacrhForAnomalies(world);
		if (hit != null && world.getBlockEntity(hit.getBlockPos()) instanceof TileEntityGravitationalAnomaly) {
			((TileEntityGravitationalAnomaly) world.getBlockEntity(hit.getBlockPos()))
					.suppress(new AnomalySuppressor(getBlockPos(), 20, 0.7f));
		}
	}

	public float getPercentage() {
		if (hit != null) {
			BlockEntity tile = level.getBlockEntity(hit.getBlockPos());
			if (tile instanceof TileEntityGravitationalAnomaly) {
				return Math.max(0, Math
						.min((float) (((TileEntityGravitationalAnomaly) tile).getEventHorizon() - 0.3f) / 2.3f, 1f));
			}
		}
		return -1;
	}

	@OnlyIn(Dist.CLIENT)
	void spawnParticles(Level world) {
		if (hit != null && world.getBlockEntity(hit.getBlockPos()) instanceof TileEntityGravitationalAnomaly) {
			if (random.nextFloat() < 0.3f) {

				float r = (float) getParticleColorR();
				float g = (float) getParticleColorG();
				float b = (float) getParticleColorB();
				Direction up = MOBlockHelper.getAboveSide(world.getBlockState(getBlockPos()).getValue(MOBlock.PROPERTY_DIRECTION))
						.getOpposite();
				GravitationalStabilizerBeamParticle particle = new GravitationalStabilizerBeamParticle(world,
						new Vector3f(getBlockPos().getX() + 0.5f, getBlockPos().getY() + 0.5f, getBlockPos().getZ() + 0.5f),
						new Vector3f(hit.getBlockPos().getX() + 0.5f, hit.getBlockPos().getY() + 0.5f,
								hit.getBlockPos().getZ() + 0.5f),
						new Vector3f(up.getStepX(), up.getStepY(), up.getStepZ()), 1f, 0.3f, 80);
				particle.setColor(r, g, b, 1);
				ClientProxy.renderHandler.getRenderParticlesHandler().addEffect(particle,
						RenderParticlesHandler.Blending.Additive);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 4086 * 2;
	}

	@Nonnull
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		AABB bb = Block.FULL_BLOCK_AABB.offset(getBlockPos());
		if (hit != null) {
			return bb.expand(hit.getBlockPos().getX() - getBlockPos().getX(), hit.getBlockPos().getY() - getBlockPos().getY(),
					hit.getBlockPos().getZ() - getBlockPos().getZ());
		}
		return bb;
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.forceField;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public boolean getServerActive() {
		return hit != null;
	}

	@Override
	public float soundVolume() {
		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}

		return getPercentage() * 0.5f;
	}

	public double getBeamColorR() {
		float percent = getPercentage();
		if (percent == -1)
			return color3.getRed();
		return (color2.getRed() * percent + color1.getRed() * (1 - percent)) / 255;
	}

	public double getBeamColorG() {
		float percent = getPercentage();
		if (percent == -1)
			return color3.getGreen();
		return (color2.getGreen() * percent + color1.getGreen() * (1 - percent)) / 255;
	}

	public double getBeamColorB() {
		float percent = getPercentage();
		if (percent == -1)
			return color3.getBlue();
		return (color2.getBlue() * percent + color1.getBlue() * (1 - percent)) / 255;
	}

	public double getParticleColorR() {
		return getBeamColorR();
	}

	public double getParticleColorG() {
		return getBeamColorG();
	}

	public double getParticleColorB() {
		return getBeamColorB();
	}

	public BlockHitResult getHit() {
		return hit;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onServerTick(TickEvent.Phase phase, Level world) {
		if (world == null) {
			return;
		}

		if (phase.equals(TickEvent.Phase.START) && getRedstoneActive()) {
			manageAnomalies(world);
		}
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}
}