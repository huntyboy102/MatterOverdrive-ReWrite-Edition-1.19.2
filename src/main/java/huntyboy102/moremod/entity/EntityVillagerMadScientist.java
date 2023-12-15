
package huntyboy102.moremod.entity;

import huntyboy102.moremod.entity.monster.EntityMutantScientist;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.entity.tasks.EntityAITalkToPlayer;
import huntyboy102.moremod.entity.tasks.EntityAIWatchDialogPlayer;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogQuestGiver;
import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.api.events.MOEventDialogConstruct;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.client.render.conversation.DialogShot;
import matteroverdrive.data.dialog.*;
import huntyboy102.moremod.init.MatterOverdriveDialogs;
import huntyboy102.moremod.init.MatterOverdriveEntities;
import huntyboy102.moremod.init.MatterOverdriveQuests;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.network.packet.server.PacketManageConversation;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class EntityVillagerMadScientist extends EntityVillager implements IDialogNpc, IDialogQuestGiver {
	private static final DataParameter<Boolean> VARIANT = EntityDataManager.createKey(EntityVillagerMadScientist.class,
			DataSerializers.BOOLEAN);
	public static DialogMessage cocktailOfAscensionComplete;
	private static DialogMessage convertMe;
	private static DialogMessage canYouConvert;
	private static DialogMessage whatDidYouDo;
	private static DialogMessage cocktailOfAscension;
	private EntityPlayer dialogPlayer;
	private IDialogMessage startMessage;

	public EntityVillagerMadScientist(World world) {
		super(world, VillagerRegistry.getId(MatterOverdriveEntities.MAD_SCIENTIST_PROFESSION));
		this.tasks.addTask(1, new EntityAITalkToPlayer(this));
		this.tasks.addTask(1, new EntityAIWatchDialogPlayer(this));
	}

	public static void registerDialogMessages(IDialogRegistry registry, Side side) {

		convertMe = new DialogMessageQuestOnObjectivesCompleted(null, "dialog.mad_scientist.convert.question",
				new QuestStack(MatterOverdriveQuests.punyHumans), new int[] { 0 }).setUnlocalized(true);
		registry.registerMessage(convertMe);

		canYouConvert = new DialogMessageQuestGive("dialog.mad_scientist.requirements.line",
				"dialog.mad_scientist.requirements.question", new QuestStack(MatterOverdriveQuests.punyHumans));
		registry.registerMessage(canYouConvert);
		canYouConvert.addOption(convertMe);
		canYouConvert.addOption(MatterOverdriveDialogs.backHomeMessage);
		canYouConvert.setUnlocalized(true);

		DialogMessage undo = new DialogMessage("dialog.mad_scientist.undo.line", "dialog.mad_scientist.undo.question");
		registry.registerMessage(undo);
		undo.setUnlocalized(true);
		undo.addOption(MatterOverdriveDialogs.trade);
		undo.addOption(MatterOverdriveDialogs.backHomeMessage);

		whatDidYouDo = new DialogMessageAndroidOnly("dialog.mad_scientist.whatDidYouDo.line",
				"dialog.mad_scientist.whatDidYouDo.question");
		registry.registerMessage(whatDidYouDo);
		whatDidYouDo.setUnlocalized(true);
		whatDidYouDo.addOption(undo);
		whatDidYouDo.addOption(MatterOverdriveDialogs.backHomeMessage);

		DialogMessage acceptCocktail = new DialogMessageQuestGive(null,
				"dialog.mad_scientist.junkie.cocktail_quest.question.accept",
				new QuestStack(MatterOverdriveQuests.cocktailOfAscension)).setReturnToMain(true).setUnlocalized(true);
		registry.registerMessage(acceptCocktail);
		DialogMessage declineCocktail = new DialogMessageBackToMain(null,
				"dialog.mad_scientist.junkie.cocktail_quest.question.decline").setUnlocalized(true);
		registry.registerMessage(declineCocktail);
		DialogMessage[] cocktailQuest = MatterOverdrive.DIALOG_FACTORY.constructMultipleLineDialog(
				DialogMessageQuestStart.class, "dialog.mad_scientist.junkie.cocktail_quest", 8, ". . . . . .");
		((DialogMessageQuestStart) cocktailQuest[0])
				.setQuest(new QuestStack(MatterOverdriveQuests.cocktailOfAscension));
		cocktailOfAscension = cocktailQuest[0];
		DialogMessage lastLine = cocktailQuest[cocktailQuest.length - 1];
		lastLine.addOption(acceptCocktail);
		lastLine.addOption(declineCocktail);

		cocktailOfAscensionComplete = new DialogMessageQuestOnObjectivesCompleted(
				"dialog.mad_scientist.junkie.cocktail_quest.line",
				"dialog.mad_scientist.junkie.cocktail_quest.complete.question",
				new QuestStack(MatterOverdriveQuests.cocktailOfAscension), new int[] { 0, 1, 2 }).setUnlocalized(true);
		DialogMessage areYouOk = new DialogMessageQuit(null,
				"dialog.mad_scientist.junkie.cocktail_quest.are_you_ok.question").setUnlocalized(true);
		cocktailOfAscensionComplete.addOption(areYouOk);

		if (side == Side.CLIENT) {
			canYouConvert.setShots(DialogShot.closeUp);
			undo.setShots(DialogShot.closeUp);
			whatDidYouDo.setShots(DialogShot.fromBehindLeftClose);
			for (DialogMessage aCocktailQuest : cocktailQuest) {
				MatterOverdrive.DIALOG_FACTORY.addRandomShots(aCocktailQuest);
			}
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(VARIANT, false);
	}

	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("junkie", getJunkie());
	}

	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		setJunkie(nbtTagCompound.getBoolean("junkie"));
	}

	@Override
	public EntityVillager createChild(EntityAgeable entity) {
		EntityVillagerMadScientist villager = new EntityVillagerMadScientist(this.world);
		villager.onInitialSpawn(world.getDifficultyForLocation(getPosition()), null);
		return villager;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			MatterOverdrive.NETWORK.sendToServer(new PacketManageConversation(this, true));
			return true;
		}

		return false;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);
		setJunkie(rand.nextBoolean());
		return livingdata;
	}

	@Override
	public IDialogMessage getStartDialogMessage(EntityPlayer player) {
		return this.startMessage;
	}

	private IDialogMessage assembleStartingMessage(EntityPlayer player) {
		if (getJunkie()) {
			DialogMessage mainJunkieMessage = new DialogMessage(
					MOStringHelper.formatVariations("dialog.mad_scientist.junkie.main", "line", 2), null)
					.setUnlocalized(true);
			MatterOverdrive.DIALOG_FACTORY.addOnlyVisibleOptions(player, this, mainJunkieMessage, canYouConvert,
					MatterOverdriveDialogs.trade, cocktailOfAscension, cocktailOfAscensionComplete,
					MatterOverdriveDialogs.quitMessage);
			return mainJunkieMessage;
		} else {
			if (MOPlayerCapabilityProvider.GetAndroidCapability(player).isAndroid()) {
				DialogMessage mainAndroidMessage = new DialogMessage(
						MOStringHelper.formatVariations("dialog.mad_scientist.main.line", "android", 3), null);
				mainAndroidMessage.setUnlocalized(true);
				mainAndroidMessage.addOption(whatDidYouDo);
				mainAndroidMessage.addOption(MatterOverdriveDialogs.trade);
				mainAndroidMessage.addOption(MatterOverdriveDialogs.quitMessage);
				return mainAndroidMessage;
			} else {
				DialogMessage mainHumanMessage = new DialogMessage(
						MOStringHelper.formatVariations("dialog.mad_scientist.main.line", "human", 3), null);
				mainHumanMessage.setUnlocalized(true);
				mainHumanMessage.addOption(canYouConvert);
				mainHumanMessage.addOption(MatterOverdriveDialogs.quitMessage);
				return mainHumanMessage;
			}
		}
	}

	@Override
	public EntityPlayer getDialogPlayer() {
		return dialogPlayer;
	}

	@Override
	public void setDialogPlayer(EntityPlayer player) {
		dialogPlayer = player;
		if (player != null) {
			if (!MinecraftForge.EVENT_BUS.post(new MOEventDialogConstruct.Pre(this, player, startMessage))) {
				startMessage = assembleStartingMessage(player);
				MinecraftForge.EVENT_BUS.post(new MOEventDialogConstruct.Post(this, player, startMessage));
			}
		} else {
			startMessage = null;
		}
	}

	@Override
	public boolean canTalkTo(EntityPlayer player) {
		return MOPlayerCapabilityProvider.GetAndroidCapability(player) == null
				|| !MOPlayerCapabilityProvider.GetAndroidCapability(player).isTurning();
	}

	@Override
	public EntityLiving getEntity() {
		return this;
	}

	@Override
	public void onPlayerInteract(EntityPlayer player, DialogMessage dialogMessage) {
		if (dialogMessage == cocktailOfAscensionComplete) {
			this.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("wither"), 1000, 1));
			world.playSound(null, this.posX, this.posY, this.posZ, MatterOverdriveSounds.failedAnimalDie,
					SoundCategory.NEUTRAL, 1, 1);
			// world.createExplosion(this,posX,posY,posZ,3,false);
			this.setDead();
			EntityMutantScientist mutantScientist = new EntityMutantScientist(world);
			mutantScientist.spawnExplosionParticle();
			mutantScientist.setPosition(posX, posY, posZ);
			mutantScientist.onInitialSpawn(world.getDifficultyForLocation(getPosition()), null);
			world.spawnEntity(mutantScientist);
		} else if (dialogMessage == convertMe) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider.GetExtendedCapability(player);
			for (QuestStack questStack : extendedProperties.getQuestData().getActiveQuests()) {
				if (questStack.getQuest() == MatterOverdriveQuests.punyHumans) {
					questStack.markComplited(player, false);
					MOPlayerCapabilityProvider.GetAndroidCapability(player).startConversion();
				}
			}
		}
	}

	@Override
	public void giveQuest(IDialogMessage message, QuestStack questStack, EntityPlayer entityPlayer) {
		if (questStack != null) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(entityPlayer);
			if (extendedProperties != null && questStack.getQuest().canBeAccepted(questStack, entityPlayer)) {
				QuestStack newQuestStack = questStack.copy();
				newQuestStack.setGiver(this);
				extendedProperties.addQuest(newQuestStack);
			}
		}
	}

	public boolean getJunkie() {
		return this.dataManager.get(VARIANT);
	}

	public void setJunkie(boolean junkie) {
		this.dataManager.set(VARIANT, junkie);
		if (junkie) {
			this.setCustomNameTag(MOStringHelper.translateToLocal("entity.matteroverdrive.mad_scientist.junkie.name"));
		}
	}

}
