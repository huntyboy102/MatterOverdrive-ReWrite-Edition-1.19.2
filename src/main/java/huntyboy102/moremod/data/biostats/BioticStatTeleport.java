
package huntyboy102.moremod.data.biostats;

import java.util.Collections;
import java.util.HashSet;

import com.google.common.collect.Multimap;

import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.network.packet.server.PacketTeleportPlayer;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOPhysicsHelper;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.handler.KeyHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BioticStatTeleport extends AbstractBioticStat implements IConfigSubscriber {

	public static final int TELEPORT_DELAY = 40;
	public static int ENERGY_PER_TELEPORT = 4096;
	private static int MAX_TELEPORT_HEIGHT_CHECK = 8;
	private static int MAX_TELEPORT_DISTANCE = 32;
	private final HashSet<String> blackListedBlocks;
	@OnlyIn(Dist.CLIENT)
	private boolean hasPressedKey;

	public BioticStatTeleport(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setShowOnWheel(true);
		blackListedBlocks = new HashSet<>();
	}

	@Override
	public String getDetails(int level) {
		String keyName = ChatFormatting.AQUA
				+ GameSettings
						.getKeyDisplayString(ClientProxy.keyHandler.getBinding(KeyHandler.ABILITY_USE_KEY).getKeyCode())
				+ ChatFormatting.GRAY;
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(), keyName,
				ChatFormatting.YELLOW.toString() + ENERGY_PER_TELEPORT + " FE" + ChatFormatting.GRAY);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (android.getPlayer().level.isClientSide) {
			manageActivate(android);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void manageActivate(AndroidPlayer androidPlayer) {
		if (ClientProxy.keyHandler.getBinding(KeyHandler.ABILITY_USE_KEY).isKeyDown()
				&& this.equals(androidPlayer.getActiveStat())) {
			if (ClientProxy.keyHandler.getBinding(KeyHandler.ABILITY_SWITCH_KEY).isKeyDown()) {
				hasPressedKey = false;
			} else {
				hasPressedKey = true;
			}
		} else if (hasPressedKey) {
			Vec3 pos = getPos(androidPlayer);
			if (pos != null && !MinecraftForge.EVENT_BUS
					.post(new MOEventBionicStat(this, androidPlayer.getUnlockedLevel(this), androidPlayer))) {
				MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketTeleportPlayer(pos.x, pos.y, pos.z));
				hasPressedKey = false;
			}
			hasPressedKey = false;
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {

	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	public Vec3 getPos(AndroidPlayer androidPlayer) {
		BlockHitResult position = MOPhysicsHelper.rayTraceForBlocks(androidPlayer.getPlayer(),
				androidPlayer.getPlayer().level, MAX_TELEPORT_DISTANCE, 0,
				new Vec3(0, androidPlayer.getPlayer().getEyeHeight(), 0), true, true);
		if (position != null && position.getType() != BlockHitResult.Type.MISS && position.getBlockPos() != null) {
			BlockPos pos = getTopSafeBlock(androidPlayer.getPlayer().level, position.getBlockPos(), position.sideHit);
			if (pos != null) {
				return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			}
			return null;
		}

		position = MOPhysicsHelper.rayTrace(androidPlayer.getPlayer(), androidPlayer.getPlayer().level, 6, 0,
				new Vec3(0, androidPlayer.getPlayer().getEyeHeight(), 0), true, true);
		if (position != null) {
			return position.hitVec;
		}
		return null;
	}

	private BlockPos getTopSafeBlock(Level world, BlockPos pos, Direction side) {
		int airBlockCount = 0;
		int heightCheck = MAX_TELEPORT_HEIGHT_CHECK;
		if (side == Direction.UP) {
			heightCheck = 3;
		}
		int height = Math.min(pos.getY() + heightCheck, world.getHeight());
		BlockState block;
		for (int i = pos.getY(); i < height; i++) {
			BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
			block = world.getBlockState(blockPos);
			String unlocalizedName = block.getBlock().getTranslationKey().substring(5);
			if (blackListedBlocks.contains(unlocalizedName)) {
				return null;
			}

			if (block.getBlock().isAir(block, world, blockPos) || block.getBlock().isPassable(world, blockPos)
					|| !block.getBlock().isCollidable() || block instanceof IFluidBlock) {
				airBlockCount++;
			} else {
				airBlockCount = 0;
			}

			if (airBlockCount >= 2) {
				return blockPos.offset(Direction.DOWN);
			}
		}

		pos = pos.offset(side);

		BlockState above = world.getBlockState(pos.offset(Direction.UP));
		BlockState aboveTwo = world.getBlockState(pos.offset(Direction.UP, 2));
		if (!above.getBlock().isNormalCube(above, world, pos.offset(Direction.UP))
				&& !aboveTwo.getBlock().isNormalCube(aboveTwo, world, pos.offset(Direction.UP, 2))) {
			return pos;
		}

		return null;
	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {

	}

	@Override
	public void changeAndroidStats(AndroidPlayer androidPlayer, int level, boolean enabled) {
		if (androidPlayer.getPlayer().level.isClientSide) {
			if (!isEnabled(androidPlayer, level)) {
				hasPressedKey = false;
			}
		}
	}

	@Override
	public Multimap<String, AttributeModifier> attributes(AndroidPlayer androidPlayer, int level) {
		return null;
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		long lastTeleport = android.getAndroidEffects().getEffectLong(AndroidPlayer.EFFECT_LAST_TELEPORT);
		long worldTime = android.getPlayer().level.getDayTime();
		return super.isEnabled(android, level) && lastTeleport <= worldTime
				&& android.hasEnoughEnergyScaled(ENERGY_PER_TELEPORT) && this.equals(android.getActiveStat());
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean getHasPressedKey() {
		return hasPressedKey;
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return this.equals(android.getActiveStat());
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		this.blackListedBlocks.clear();
		String[] blackListedBlocks = config.getStringList("teleport_blacklist",
				ConfigurationHandler.CATEGORY_ABILITIES, new String[] { "hellsand", "barrier", "bedrock" },
				"The Unlocalized names of the blacklist blocks that the player can't teleport to");

		Collections.addAll(this.blackListedBlocks, blackListedBlocks);

		MAX_TELEPORT_HEIGHT_CHECK = config.getInt("teleport_max_height_check", ConfigurationHandler.CATEGORY_ABILITIES,
				8, "The max height amount that the teleport ability checks if there is no 2 blocks air space");
		ENERGY_PER_TELEPORT = config.getInt("teleport_energy_cost", ConfigurationHandler.CATEGORY_ABILITIES, 4096,
				"The Energy cost of each Teleportation");
		MAX_TELEPORT_DISTANCE = config.getInt("teleport_max_distance", ConfigurationHandler.CATEGORY_ABILITIES, 32,
				"The maximum distance in blocks, the player can teleport to");
	}
}
