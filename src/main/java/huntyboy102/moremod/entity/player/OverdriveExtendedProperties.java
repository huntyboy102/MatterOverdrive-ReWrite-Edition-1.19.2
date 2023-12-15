
package huntyboy102.moremod.entity.player;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.events.MOEventQuest;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.data.quest.PlayerQuestData;
import huntyboy102.moremod.gui.GuiDataPad;
import huntyboy102.moremod.network.packet.client.quest.PacketSyncQuests;
import huntyboy102.moremod.network.packet.client.quest.PacketUpdateQuest;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class OverdriveExtendedProperties {
	public static final String EXT_PROP_NAME = "MOPlayer";
	@CapabilityInject(value = OverdriveExtendedProperties.class)
	public static Capability<OverdriveExtendedProperties> CAPIBILITY;
	private final EntityPlayer player;
	private final PlayerQuestData questData;

	public OverdriveExtendedProperties(EntityPlayer player) {
		this.player = player;
		questData = new PlayerQuestData(this);
	}

	public static void register() {
		CapabilityManager.INSTANCE.register(OverdriveExtendedProperties.class,
				new Capability.IStorage<OverdriveExtendedProperties>() {
					@Override
					public NBTBase writeNBT(Capability<OverdriveExtendedProperties> capability,
							OverdriveExtendedProperties instance, EnumFacing side) {
						NBTTagCompound data = new NBTTagCompound();
						instance.saveNBTData(data);
						return data;
					}

					@Override
					public void readNBT(Capability<OverdriveExtendedProperties> capability,
							OverdriveExtendedProperties instance, EnumFacing side, NBTBase nbt) {
						instance.loadNBTData((NBTTagCompound) nbt);
					}
				}, OverdriveExtendedProperties.class);
	}

	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound questNBT = new NBTTagCompound();
		questData.writeToNBT(questNBT, EnumSet.allOf(PlayerQuestData.DataType.class));
		compound.setTag("QuestData", questNBT);
	}

	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound questNBT = compound.getCompoundTag("QuestData");
		questData.readFromNBT(questNBT, EnumSet.allOf(PlayerQuestData.DataType.class));
	}

	public void sync(EnumSet<PlayerQuestData.DataType> dataTypes) {
		if (player != null && !player.world.isRemote && player instanceof EntityPlayerMP) {
			MatterOverdrive.NETWORK.sendTo(new PacketSyncQuests(questData, dataTypes), (EntityPlayerMP) player);
		}
	}

	public void copy(OverdriveExtendedProperties oterExtendetProperies) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		oterExtendetProperies.saveNBTData(tagCompound);
		loadNBTData(tagCompound);
	}

	public void addQuest(QuestStack questStack) {
		if (!MinecraftForge.EVENT_BUS.post(new MOEventQuest.Added(questStack, player))) {
			if (isServer()) {
				if (questData.getActiveQuests().size() <= 0 && questData.getCompletedQuests().size() <= 0) {
					ItemStack scanner = new ItemStack(MatterOverdrive.ITEMS.dataPad);
					scanner.setStackDisplayName("Scientist's Data Pad");
					MatterOverdrive.ITEMS.dataPad.addToScanWhitelist(scanner, Blocks.CARROTS);
					MatterOverdrive.ITEMS.dataPad.addToScanWhitelist(scanner, Blocks.POTATOES);
					MatterOverdrive.ITEMS.dataPad.addToScanWhitelist(scanner, Blocks.WHEAT);
					scanner.getTagCompound().setBoolean("Destroys", true);
					player.addItemStackToInventory(scanner);
				}
				QuestStack addedQuest = questData.addQuest(questStack);
				if (addedQuest != null) {
					addedQuest.getQuest().initQuestStack(player.getRNG(), addedQuest, player);
					MatterOverdrive.NETWORK.sendTo(new PacketUpdateQuest(addedQuest, PacketUpdateQuest.ADD_QUEST),
							(EntityPlayerMP) player);
				}
			} else {
				QuestStack addedQuest = questData.addQuest(questStack);
				ClientProxy.questHud.addStartedQuest(addedQuest);
			}
		}
	}

	public void update(Side side) {
		if (side.equals(Side.SERVER)) {
			questData.manageQuestCompletion();
		}
	}

	public boolean hasCompletedQuest(QuestStack questStack) {
		return questData.hasCompletedQuest(questStack);
	}

	public boolean hasQuest(QuestStack questStack) {
		return questData.hasQuest(questStack);
	}

	public void onQuestCompleted(QuestStack questStack, int index) {
		if (isServer()) {

			List<IQuestReward> rewards = new ArrayList<>();
			questStack.addRewards(rewards, getPlayer());
			int xp = questStack.getXP(getPlayer());
			MOEventQuest.Completed event = new MOEventQuest.Completed(questStack, player, xp, rewards);

			if (!MinecraftForge.EVENT_BUS.post(event)) {
				questData.addQuestToCompleted(questStack);
				getPlayer().addExperience(event.xp);
				for (IQuestReward reward : event.rewards) {
					reward.giveReward(questStack, getPlayer());
				}
				questStack.getQuest().onCompleted(questStack, player);

				player.sendMessage(new TextComponentString(String.format("[Matter Overdrive] %1$s completed %2$s",
						player.getDisplayName().getFormattedText(), questStack.getTitle(player))));
			}
			MatterOverdrive.NETWORK.sendTo(
					new PacketUpdateQuest(index, null, questStack, PacketUpdateQuest.COMPLETE_QUEST),
					(EntityPlayerMP) player);
		} else {
			ClientProxy.questHud.addCompletedQuest(questStack);
			getQuestData().getCompletedQuests().add(questStack);
			getQuestData().removeQuest(index);
			if (Minecraft.getMinecraft().currentScreen instanceof GuiDataPad) {
				((GuiDataPad) Minecraft.getMinecraft().currentScreen).refreshQuests(this);
			}
		}
	}

	public void onQuestAbandoned(QuestStack questStack) {
		if (isServer()) {

		} else {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiDataPad) {
				((GuiDataPad) Minecraft.getMinecraft().currentScreen).refreshQuests(this);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateQuestFromServer(int index, QuestStack questStack, QuestState questState) {
		if (index < getQuestData().getActiveQuests().size()) {
			ClientProxy.questHud.addObjectivesChanged(getQuestData().getActiveQuests().get(index), questStack,
					questState);
			getQuestData().getActiveQuests().set(index, questStack);
		}
	}

	public boolean isServer() {
		return player != null && !player.world.isRemote;
	}

	public PlayerQuestData getQuestData() {
		return questData;
	}

	public void onEvent(Event event) {
		questData.onEvent(event);
	}

	public EntityPlayer getPlayer() {
		return player;
	}
}
