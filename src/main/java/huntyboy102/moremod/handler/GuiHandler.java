
package huntyboy102.moremod.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.analyzer.TileEntityMachineMatterAnalyzer;
import huntyboy102.moremod.machines.decomposer.TileEntityMachineDecomposer;
import huntyboy102.moremod.machines.dimensional_pylon.TileEntityMachineDimensionalPylon;
import huntyboy102.moremod.machines.fusionReactorController.TileEntityMachineFusionReactorController;
import huntyboy102.moremod.machines.pattern_monitor.TileEntityMachinePatternMonitor;
import huntyboy102.moremod.machines.pattern_storage.TileEntityMachinePatternStorage;
import huntyboy102.moremod.machines.replicator.TileEntityMachineReplicator;
import huntyboy102.moremod.machines.transporter.TileEntityMachineTransporter;
import huntyboy102.moremod.util.MOLog;
import org.apache.logging.log4j.Level;

import huntyboy102.moremod.container.ContainerAnalyzer;
import huntyboy102.moremod.container.ContainerAndroidSpawner;
import huntyboy102.moremod.container.ContainerAndroidStation;
import huntyboy102.moremod.container.ContainerDimensionalPylon;
import huntyboy102.moremod.container.ContainerFactory;
import huntyboy102.moremod.container.ContainerFusionReactor;
import huntyboy102.moremod.container.ContainerInscriber;
import huntyboy102.moremod.container.ContainerReplicator;
import huntyboy102.moremod.container.ContainerSolarPanel;
import huntyboy102.moremod.container.ContainerStarMap;
import huntyboy102.moremod.container.ContainerWeaponStation;
import huntyboy102.moremod.container.MOBaseContainer;
import huntyboy102.moremod.container.matter_network.ContainerPatternMonitor;
import huntyboy102.moremod.gui.GuiAndroidSpawner;
import huntyboy102.moremod.gui.GuiAndroidStation;
import huntyboy102.moremod.gui.GuiChargingStation;
import huntyboy102.moremod.gui.GuiContractMarket;
import huntyboy102.moremod.gui.GuiDecomposer;
import huntyboy102.moremod.gui.GuiDimensionalPylon;
import huntyboy102.moremod.gui.GuiFusionReactor;
import huntyboy102.moremod.gui.GuiHoloSign;
import huntyboy102.moremod.gui.GuiInscriber;
import huntyboy102.moremod.gui.GuiMatterAnalyzer;
import huntyboy102.moremod.gui.GuiMicrowave;
import huntyboy102.moremod.gui.GuiNetworkRouter;
import huntyboy102.moremod.gui.GuiNetworkSwitch;
import huntyboy102.moremod.gui.GuiPatternMonitor;
import huntyboy102.moremod.gui.GuiPatternStorage;
import huntyboy102.moremod.gui.GuiRecycler;
import huntyboy102.moremod.gui.GuiReplicator;
import huntyboy102.moremod.gui.GuiSolarPanel;
import huntyboy102.moremod.gui.GuiSpacetimeAccelerator;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.GuiTransporter;
import huntyboy102.moremod.gui.GuiWeaponStation;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.MOGuiMachine;
import huntyboy102.moremod.tile.MOTileEntity;
import huntyboy102.moremod.tile.TileEntityAndroidSpawner;
import huntyboy102.moremod.tile.TileEntityAndroidStation;
import huntyboy102.moremod.tile.TileEntityHoloSign;
import huntyboy102.moremod.tile.TileEntityInscriber;
import huntyboy102.moremod.tile.TileEntityMachineChargingStation;
import huntyboy102.moremod.tile.TileEntityMachineContractMarket;
import huntyboy102.moremod.tile.TileEntityMachineMatterRecycler;
import huntyboy102.moremod.tile.TileEntityMachineNetworkRouter;
import huntyboy102.moremod.tile.TileEntityMachineNetworkSwitch;
import huntyboy102.moremod.tile.TileEntityMachineSolarPanel;
import huntyboy102.moremod.tile.TileEntityMachineSpacetimeAccelerator;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import huntyboy102.moremod.tile.TileEntityMicrowave;
import huntyboy102.moremod.tile.TileEntityWeaponStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class GuiHandler implements IGuiHandler {
	private Map<Class<? extends MOTileEntity>, Class<? extends MOGuiBase>> tileEntityGuiList;
	private Map<Class<? extends MOTileEntity>, Class<? extends MOBaseContainer>> tileEntityContainerList;

	public GuiHandler() {
		tileEntityGuiList = new HashMap<>();
		tileEntityContainerList = new HashMap<>();
	}

	public void register(Side side) {
		if (side == Side.SERVER) {
			// Container Registration
			registerContainer(TileEntityMachineSolarPanel.class, ContainerSolarPanel.class);
			registerContainer(TileEntityWeaponStation.class, ContainerWeaponStation.class);
			registerContainer(TileEntityMachineFusionReactorController.class, ContainerFusionReactor.class);
			registerContainer(TileEntityAndroidStation.class, ContainerAndroidStation.class);
			registerContainer(TileEntityMachineStarMap.class, ContainerStarMap.class);
			registerContainer(TileEntityInscriber.class, ContainerInscriber.class);
			registerContainer(TileEntityAndroidSpawner.class, ContainerAndroidSpawner.class);
			registerContainer(TileEntityMachineReplicator.class, ContainerReplicator.class);
			registerContainer(TileEntityMachinePatternMonitor.class, ContainerPatternMonitor.class);
			registerContainer(TileEntityMachineMatterAnalyzer.class, ContainerAnalyzer.class);
			registerContainer(TileEntityMachineDimensionalPylon.class, ContainerDimensionalPylon.class);
		} else {
			// Gui Registration
			registerGuiAndContainer(TileEntityMachineReplicator.class, GuiReplicator.class, ContainerReplicator.class);
			registerGui(TileEntityMachineDecomposer.class, GuiDecomposer.class);
			registerGui(TileEntityMachineNetworkRouter.class, GuiNetworkRouter.class);
			registerGuiAndContainer(TileEntityMachineMatterAnalyzer.class, GuiMatterAnalyzer.class,
					ContainerAnalyzer.class);
			registerGui(TileEntityMachinePatternStorage.class, GuiPatternStorage.class);
			registerGuiAndContainer(TileEntityMachineSolarPanel.class, GuiSolarPanel.class, ContainerSolarPanel.class);
			registerGuiAndContainer(TileEntityWeaponStation.class, GuiWeaponStation.class,
					ContainerWeaponStation.class);
			registerGuiAndContainer(TileEntityMachinePatternMonitor.class, GuiPatternMonitor.class,
					ContainerPatternMonitor.class);
			registerGui(TileEntityMachineNetworkSwitch.class, GuiNetworkSwitch.class);
			registerGui(TileEntityMachineTransporter.class, GuiTransporter.class);
			registerGui(TileEntityMachineMatterRecycler.class, GuiRecycler.class);
			registerGuiAndContainer(TileEntityMachineFusionReactorController.class, GuiFusionReactor.class,
					ContainerFusionReactor.class);
			registerGuiAndContainer(TileEntityAndroidStation.class, GuiAndroidStation.class,
					ContainerAndroidStation.class);
			registerGuiAndContainer(TileEntityMachineStarMap.class, GuiStarMap.class, ContainerStarMap.class);
			registerGui(TileEntityHoloSign.class, GuiHoloSign.class);
			registerGui(TileEntityMachineChargingStation.class, GuiChargingStation.class);
			registerGuiAndContainer(TileEntityInscriber.class, GuiInscriber.class, ContainerInscriber.class);
			registerGui(TileEntityMachineContractMarket.class, GuiContractMarket.class);
			registerGuiAndContainer(TileEntityAndroidSpawner.class, GuiAndroidSpawner.class,
					ContainerAndroidSpawner.class);
			registerGui(TileEntityMachineSpacetimeAccelerator.class, GuiSpacetimeAccelerator.class);
			registerGuiAndContainer(TileEntityMachineDimensionalPylon.class, GuiDimensionalPylon.class,
					ContainerDimensionalPylon.class);
			registerGui(TileEntityMicrowave.class, GuiMicrowave.class);
		}
	}

	public void registerContainer(Class<? extends MOTileEntity> tileEntity,
			Class<? extends MOBaseContainer> container) {
		tileEntityContainerList.put(tileEntity, container);
	}

	public void registerGuiAndContainer(Class<? extends MOTileEntity> tileEntity, Class<? extends MOGuiBase> gui,
			Class<? extends MOBaseContainer> container) {
		tileEntityContainerList.put(tileEntity, container);
		tileEntityGuiList.put(tileEntity, gui);
	}

	public void registerGui(Class<? extends MOTileEntity> tileEntity, Class<? extends MOGuiBase> gui) {
		tileEntityGuiList.put(tileEntity, gui);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID) {
		default:
			if (entity != null && tileEntityContainerList.containsKey(entity.getClass())) {
				try {
					Class<? extends MOBaseContainer> containerClass = tileEntityContainerList.get(entity.getClass());
					Constructor[] constructors = containerClass.getDeclaredConstructors();
					for (Constructor<?> constructor : constructors) {
						Class[] parameterTypes = constructor.getParameterTypes();
						if (parameterTypes.length == 2) {
							if (parameterTypes[0].isInstance(player.inventory)
									&& parameterTypes[1].isInstance(entity)) {
								onContainerOpen(entity, Side.SERVER);
								return constructor.newInstance(player.inventory, entity);
							}
						}
					}
				} catch (InvocationTargetException e) {
					MOLog.log(Level.WARN, e, "Could not call TileEntity constructor in server GUI handler");
				} catch (InstantiationException e) {
					MOLog.log(Level.WARN, e, "Could not instantiate TileEntity in server GUI handler");
				} catch (IllegalAccessException e) {
					MOLog.log(Level.WARN, e, "Could not access TileEntity constructor in server GUI handler");
				}
			} else if (entity instanceof MOTileEntityMachine) {
				return ContainerFactory.createMachineContainer((MOTileEntityMachine) entity, player.inventory);
			}
		}
		return null;
	}

	private void onContainerOpen(TileEntity entity, Side side) {
		if (entity instanceof MOTileEntityMachine) {
			((MOTileEntityMachine) entity).onContainerOpen(side);
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID) {
		default:
			if (tileEntityGuiList.containsKey(entity.getClass())) {
				try {

					Class<? extends MOGuiBase> containerClass = tileEntityGuiList.get(entity.getClass());
					Constructor[] constructors = containerClass.getDeclaredConstructors();
					for (Constructor<?> constructor : constructors) {
						Class[] parameterTypes = constructor.getParameterTypes();
						if (parameterTypes.length == 2) {
							if (parameterTypes[0].isInstance(player.inventory)
									&& parameterTypes[1].isInstance(entity)) {
								onContainerOpen(entity, Side.CLIENT);
								return constructor.newInstance(player.inventory, entity);
							}
						}
					}
				} catch (InvocationTargetException e) {
					MOLog.log(Level.WARN, e, "Could not call TileEntity constructor in client GUI handler");
				} catch (InstantiationException e) {
					MOLog.log(Level.WARN, e, "Could not instantiate the TileEntity in client GUI handler");
				} catch (IllegalAccessException e) {
					MOLog.log(Level.WARN, e, "Could not access TileEntity constructor in client GUI handler");
				}
			} else if (entity instanceof MOTileEntityMachine) {
				return new MOGuiMachine<>(
						ContainerFactory.createMachineContainer((MOTileEntityMachine) entity, player.inventory),
						(MOTileEntityMachine) entity);
			}
		}
		return null;
	}
}
