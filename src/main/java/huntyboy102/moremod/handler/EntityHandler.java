
package huntyboy102.moremod.handler;

import huntyboy102.moremod.api.events.MOEventTransport;
import huntyboy102.moremod.api.events.anomaly.MOEventGravitationalAnomalyConsume;
import huntyboy102.moremod.data.quest.PlayerQuestData;
import huntyboy102.moremod.entity.EntityVillagerMadScientist;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.init.MatterOverdriveEntities;
import huntyboy102.moremod.util.MatterHelper;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.EnumSet;

public class EntityHandler {
	/*
	 * @SubscribeEvent public void
	 * onEntityConstructing(EntityEvent.EntityConstructing event) { if (event.entity
	 * instanceof EntityPlayer) { if (AndroidPlayer.get((EntityPlayer) event.entity)
	 * == null) { AndroidPlayer.register((EntityPlayer) event.entity); } if
	 * (MOPlayerCapabilityProvider.GetExtendedCapability((EntityPlayer)event.entity)
	 * == null) { OverdriveExtendedProperties.register((EntityPlayer)event.entity);
	 * } } }
	 */

	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntityLiving());
			if (androidPlayer.isAndroid()) {
				androidPlayer.onEntityFall(event);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayer) {
			MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntity())
					.sync(EnumSet.allOf(AndroidPlayer.DataType.class));
			MOPlayerCapabilityProvider.GetExtendedCapability(event.getEntity())
					.sync(EnumSet.allOf(PlayerQuestData.DataType.class));
		}
	}

	@SubscribeEvent
	public void onEntityJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntityLiving());
			if (androidPlayer != null && androidPlayer.isAndroid()) {
				androidPlayer.onEntityJump(event);
				androidPlayer.triggerEventOnStats(event);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
		AndroidPlayer newAndroidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntityPlayer());
		AndroidPlayer oldAndroidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getOriginal());
		if (newAndroidPlayer != null && oldAndroidPlayer != null) {
			newAndroidPlayer.copy(oldAndroidPlayer);
			if (event.isWasDeath()) {
				newAndroidPlayer.onPlayerRespawn();
			}
			newAndroidPlayer.sync(EnumSet.allOf(AndroidPlayer.DataType.class));
		}
		OverdriveExtendedProperties newExtendedProperties = MOPlayerCapabilityProvider
				.GetExtendedCapability(event.getEntityPlayer());
		OverdriveExtendedProperties oldExtenderDProperties = MOPlayerCapabilityProvider
				.GetExtendedCapability(event.getOriginal());
		if (newExtendedProperties != null && oldExtenderDProperties != null) {
			newExtendedProperties.copy(oldExtenderDProperties);
			newExtendedProperties.sync(EnumSet.allOf(PlayerQuestData.DataType.class));
		}
	}

	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntityLiving()).triggerEventOnStats(event);
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent deathEvent) {
		if (deathEvent.getSource() != null && deathEvent.getSource().getTrueSource() instanceof EntityPlayer) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(deathEvent.getSource().getTrueSource());
			extendedProperties.onEvent(deathEvent);
		}
	}

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntityLiving());
			if (androidPlayer.isAndroid()) {
				androidPlayer.onEntityHurt(event);
			}
		}
	}

	@SubscribeEvent
	public void onEntityItemPickup(EntityItemPickupEvent event) {
		if (event.getEntityLiving() != null) {
			if (!event.getItem().getItem().isEmpty() && MatterHelper.containsMatter(event.getItem().getItem())) {
				for (int i = 0; i < 9; i++) {
					if (!event.getEntityPlayer().inventory.getStackInSlot(i).isEmpty()
							&& event.getEntityPlayer().inventory.getStackInSlot(i)
									.getItem() == MatterOverdrive.ITEMS.portableDecomposer) {
						MatterOverdrive.ITEMS.portableDecomposer.decomposeItem(
								event.getEntityPlayer().inventory.getStackInSlot(i), event.getItem().getItem());
					}
				}
			}
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(event.getEntityPlayer());
			if (extendedProperties != null) {
				extendedProperties.onEvent(event);
			}
		}
	}

	@SubscribeEvent
	public void onEntityTransport(MOEventTransport eventTransport) {
		if (eventTransport.getEntity() instanceof EntityPlayer) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(eventTransport.getEntity());
			if (extendedProperties != null) {
				extendedProperties.onEvent(eventTransport);
			}
		}
	}

	@SubscribeEvent
	public void onEntityAnomalyConsume(MOEventGravitationalAnomalyConsume.Post event) {
		if (event.entity instanceof EntityPlayer) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(event.entity);
			if (extendedProperties != null) {
				extendedProperties.onEvent(event);
			}
		}
	}

	@SubscribeEvent
	public void OnAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(Reference.MOD_NAME, "MOPlayer"),
					new MOPlayerCapabilityProvider((EntityPlayer) event.getObject()));
		}
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityVillager
				&& ((EntityVillager) event.getEntity()).getProfessionForge()
						.equals(MatterOverdriveEntities.MAD_SCIENTIST_PROFESSION)
				&& !event.getEntity().getClass().equals(EntityVillagerMadScientist.class)) {
			event.setCanceled(true);
			EntityVillagerMadScientist villager = new EntityVillagerMadScientist(event.getWorld());
			villager.onInitialSpawn(
					event.getWorld().getDifficultyForLocation(((EntityVillager) event.getEntity()).getPos()), null);
			// villager.setProfession(MatterOverdriveEntities.MAD_SCIENTIST_PROFESSION);
			villager.setGrowingAge(-24000);
			villager.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 0.0F,
					0.0F);
			event.getWorld().spawnEntity(villager);
			if (event.getEntity().hasCustomName()) {
				villager.setCustomNameTag(event.getEntity().getCustomNameTag());
			}
		}
	}
}