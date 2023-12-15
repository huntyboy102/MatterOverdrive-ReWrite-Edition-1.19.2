
package huntyboy102.moremod.items;

import huntyboy102.moremod.items.includes.MOBaseItem;
import huntyboy102.moremod.api.events.MOEventScan;
import huntyboy102.moremod.api.inventory.IBlockScanner;
import huntyboy102.moremod.client.sound.MachineSound;
import huntyboy102.moremod.gui.GuiDataPad;
import huntyboy102.moremod.handler.SoundHandler;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOPhysicsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DataPad extends MOBaseItem implements IBlockScanner {
	@SideOnly(Side.CLIENT)
	private static MachineSound scanningSound;

	public DataPad(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote && hasGui(itemStackIn)) {
			openGui(handIn, itemStackIn);
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (!playerIn.isSneaking() && worldIn.getBlockState(pos).getBlock() != Blocks.AIR
				&& canScan(stack, worldIn.getBlockState(pos))) {
			playerIn.setActiveHand(hand);
			if (worldIn.isRemote) {
				playSound(playerIn.getPosition());
			} else {
				setLastBlock(stack, worldIn.getBlockState(pos).getBlock());
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack scanner) {
		return 20 * 2;
	}

	@Override
	public boolean hasDetails(ItemStack stack) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void openGui(EnumHand hand, ItemStack stack) {
		try {
			Minecraft.getMinecraft().displayGuiScreen(new GuiDataPad(hand, stack));
		} catch (Exception e) {
			MOLog.error("There was a problem while trying to open the Data Pad Gui", e);
		}

	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
		super.onUpdate(itemStack, world, entity, p_77663_4_, p_77663_5_);

		if (world.isRemote) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.isHandActive()) {

				} else {
					stopScanSounds();
				}
			}
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer)) {
			return stack;
		}
		if (worldIn.isRemote) {
			if (!MinecraftForge.EVENT_BUS
					.post(new MOEventScan((EntityPlayer) entityLiving, stack, getScanningPos(stack, entityLiving)))) {
				stopScanSounds();
			}
		} else {
			MOEventScan event = new MOEventScan((EntityPlayer) entityLiving, stack,
					getScanningPos(stack, entityLiving));
			if (!MinecraftForge.EVENT_BUS.post(event)) {
				if (destroysBlocks(stack)
						&& worldIn.isBlockModifiable((EntityPlayer) entityLiving, event.position.getBlockPos())) {
					worldIn.setBlockToAir(event.position.getBlockPos());
				}
				SoundHandler.PlaySoundAt(worldIn, MatterOverdriveSounds.scannerSuccess, SoundCategory.PLAYERS,
						entityLiving);
			}
		}
		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!(player instanceof EntityPlayer)) {
			return;
		}

		RayTraceResult hit = getScanningPos(stack, player);

		if (hit != null) {

			if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
				Block lastBlock = getLastBlock(stack);
				if (lastBlock != null && lastBlock != player.world.getBlockState(hit.getBlockPos()).getBlock()) {
					player.stopActiveHand();
				}
			}
		} else {
			if (player.world.isRemote) {
				stopScanSounds();
				player.stopActiveHand();
			}
		}
	}

	public Block getLastBlock(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			return Block.getBlockById(itemStack.getTagCompound().getInteger("LastBlock"));
		}
		return null;
	}

	public void setLastBlock(ItemStack itemStack, Block block) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}

		int blockID = Block.getIdFromBlock(block);
		if (itemStack.getTagCompound().getInteger("LastBlock") != blockID) {
			itemStack.getTagCompound().setInteger("LastBlock", blockID);
		}
	}

	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if (world.isRemote) {
			stopScanSounds();
		}
	}

	@SideOnly(Side.CLIENT)
	private void playSound(BlockPos pos) {
		if (scanningSound == null) {
			scanningSound = new MachineSound(MatterOverdriveSounds.scannerScanning, SoundCategory.PLAYERS, pos, 0.6f,
					1);
			Minecraft.getMinecraft().getSoundHandler().playSound(scanningSound);
		}
	}

	@SideOnly(Side.CLIENT)
	private void stopScanSounds() {
		if (scanningSound != null) {
			scanningSound.stopPlaying();
			scanningSound = null;
		}
	}

	@Override
	public RayTraceResult getScanningPos(ItemStack itemStack, EntityLivingBase player) {
		return MOPhysicsHelper.rayTrace(player, player.world, 5, 0, new Vec3d(0, player.getEyeHeight(), 0), true,
				false);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.NONE;
	}

	public void addToScanWhitelist(ItemStack itemStack, Block block) {
		String id = block.getRegistryName().toString();
		NBTTagList list = itemStack.getTagCompound().getTagList("whitelist", Constants.NBT.TAG_STRING);
		list.appendTag(new NBTTagString(id));
		itemStack.getTagCompound().setTag("whitelist", list);
	}

	public void setOrdering(ItemStack stack, int order) {
		stack.setTagInfo("Ordering", new NBTTagInt(order));
	}

	public void setOpenGuide(ItemStack stack, int guideID) {
		stack.setTagInfo("guideID", new NBTTagInt(guideID));
	}

	public void setOpenPage(ItemStack stack, int page) {
		stack.setTagInfo("page", new NBTTagInt(page));
	}

	public void setCategory(ItemStack stack, String category) {
		stack.setTagInfo("Category", new NBTTagString(category));
	}

	public void setSelectedActiveQuest(ItemStack itemStack, int quest) {
		itemStack.setTagInfo("SelectedActiveQuest", new NBTTagShort((short) quest));
	}

	public int getGuideID(ItemStack stack) {
		TagCompountCheck(stack);
		if (hasOpenGuide(stack)) {
			return stack.getTagCompound().getInteger("guideID");
		}
		return -1;
	}

	public int getPage(ItemStack stack) {
		TagCompountCheck(stack);
		return stack.getTagCompound().getInteger("page");
	}

	public boolean hasOpenGuide(ItemStack stack) {
		TagCompountCheck(stack);
		return stack.getTagCompound().hasKey("guideID", Constants.NBT.TAG_INT);
	}

	public int getOrdering(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Ordering", Constants.NBT.TAG_STRING)) {
			return stack.getTagCompound().getInteger("Ordering");
		}
		return 2;
	}

	public String getCategory(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return stack.getTagCompound().getString("Category");
		}
		return "";
	}

	public int getActiveSelectedQuest(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return stack.getTagCompound().getShort("SelectedActiveQuest");
		}
		return 0;
	}

	@Override
	public boolean destroysBlocks(ItemStack itemStack) {
		return itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("Destroys");
	}

	@Override
	public boolean showsGravitationalWaves(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			if (itemStack.getTagCompound().hasKey("showGravWaves")) {
				return itemStack.getTagCompound().getBoolean("showGravWaves");
			}
		}
		return true;
	}

	public boolean canScan(ItemStack itemStack, IBlockState state) {
		if (itemStack.getTagCompound() != null
				&& itemStack.getTagCompound().hasKey("whitelist", Constants.NBT.TAG_LIST)) {
			NBTTagList tagList = itemStack.getTagCompound().getTagList("whitelist", Constants.NBT.TAG_STRING);
			for (int i = 0; i < tagList.tagCount(); i++) {
				if (tagList.getStringTagAt(i).equals(state.getBlock().getRegistryName().toString())) {
					return true;
				}
			}

			return false;
		}
		return false;
	}

	public boolean hasGui(ItemStack itemStack) {
		return itemStack.getTagCompound() == null || !itemStack.getTagCompound().getBoolean("nogui");
	}

}
