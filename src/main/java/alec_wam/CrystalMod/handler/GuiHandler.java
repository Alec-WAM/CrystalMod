package alec_wam.CrystalMod.handler;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.capability.ContainerExtendedInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.capability.GuiExtendedInventory;
import alec_wam.CrystalMod.entities.accessories.ContainerHorseEnderChest;
import alec_wam.CrystalMod.entities.accessories.GuiHorseEnderChest;
import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import alec_wam.CrystalMod.entities.accessories.boats.EntityBoatChest;
import alec_wam.CrystalMod.entities.minecarts.chests.EntityCrystalChestMinecartBase;
import alec_wam.CrystalMod.entities.minecarts.chests.wireless.EntityWirelessChestMinecart;
import alec_wam.CrystalMod.entities.minions.warrior.ContainerMinionWarrior;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.entities.minions.warrior.GuiMinionWarrior;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.items.guide.GuiGuideMainPage;
import alec_wam.CrystalMod.items.tools.backpack.BackpackOpenSourceItem;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.block.BackpackOpenSourceBlock;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ContainerBackpackUpgradeWindow;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ContainerBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.GuiBackpackUpgradeWindow;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.GuiBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.tiles.cases.ContainerCase;
import alec_wam.CrystalMod.tiles.cases.GuiCase;
import alec_wam.CrystalMod.tiles.cases.TileEntityCaseBase;
import alec_wam.CrystalMod.tiles.chest.ContainerCrystalChest;
import alec_wam.CrystalMod.tiles.chest.GUIChest;
import alec_wam.CrystalMod.tiles.chest.TileEntityBlueCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wireless.ContainerWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.GuiWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.TileWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wooden.ContainerWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chest.wooden.GUIWoodenChest;
import alec_wam.CrystalMod.tiles.chest.wooden.TileWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.enhancedEnchantmentTable.ContainerEnhancedEnchantmentTable;
import alec_wam.CrystalMod.tiles.enhancedEnchantmentTable.GuiEnhancedEnchantmentTable;
import alec_wam.CrystalMod.tiles.enhancedEnchantmentTable.TileEntityEnhancedEnchantmentTable;
import alec_wam.CrystalMod.tiles.machine.ContainerNull;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.advDispenser.ContainerAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.advDispenser.GuiAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.gui.ContainerEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.gui.GuiEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.epst.ContainerEPST;
import alec_wam.CrystalMod.tiles.machine.epst.GuiEPST;
import alec_wam.CrystalMod.tiles.machine.epst.TileEPST;
import alec_wam.CrystalMod.tiles.machine.power.battery.ContainerBattery;
import alec_wam.CrystalMod.tiles.machine.power.battery.GuiBattery;
import alec_wam.CrystalMod.tiles.machine.power.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.machine.power.engine.furnace.ContainerEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.power.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.power.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.power.engine.lava.ContainerEngineLava;
import alec_wam.CrystalMod.tiles.machine.power.engine.lava.GuiEngineLava;
import alec_wam.CrystalMod.tiles.machine.power.engine.lava.TileEntityEngineLava;
import alec_wam.CrystalMod.tiles.machine.power.engine.vampire.ContainerEngineVampire;
import alec_wam.CrystalMod.tiles.machine.power.engine.vampire.GuiEngineVampire;
import alec_wam.CrystalMod.tiles.machine.power.engine.vampire.TileEntityEngineVampire;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.ContainerRedstoneReactor;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.GuiRedstoneReactor;
import alec_wam.CrystalMod.tiles.machine.power.redstonereactor.TileRedstoneReactor;
import alec_wam.CrystalMod.tiles.machine.seismic.GuiSeismicScanner;
import alec_wam.CrystalMod.tiles.machine.seismic.TileEntitySeismicScanner;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteAnimalControl;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteBoundsAdjust;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteInventorySideSelection;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteAnimalControl;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteBoundsAdjust;
import alec_wam.CrystalMod.tiles.machine.worksite.gui.GuiWorksiteInventorySideSelection;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteAnimalFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteCropFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteTreeFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.GuiWorksiteAnimalFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.GuiWorksiteCropFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.GuiWorksiteTreeFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import alec_wam.CrystalMod.tiles.obsidiandispenser.GuiObsidianDispener;
import alec_wam.CrystalMod.tiles.obsidiandispenser.TileObsidianDispenser;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.GuiEStoragePipe;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.GuiCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.GuiPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.ContainerPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.PanelSourceNormal;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.GuiPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.ContainerPanelMonitor;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.GuiPanelMonitor;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.TileEntityPanelMonitor;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ItemWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.PanelSourceWireless;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.power.ContainerPowerCore;
import alec_wam.CrystalMod.tiles.pipes.estorage.power.GuiPowerCore;
import alec_wam.CrystalMod.tiles.pipes.estorage.power.TileNetworkPowerCore;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.ContainerSecurityController;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.ContainerSecurityEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.GuiSecurityController;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.GuiSecurityEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.TileSecurityController;
import alec_wam.CrystalMod.tiles.pipes.estorage.security.TileSecurityEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.stocker.ContainerStocker;
import alec_wam.CrystalMod.tiles.pipes.estorage.stocker.GuiStocker;
import alec_wam.CrystalMod.tiles.pipes.estorage.stocker.TileEntityStocker;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ContainerHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.ContainerHDDArray;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.GuiHDDArray;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.array.TileHDDArray;
import alec_wam.CrystalMod.tiles.pipes.item.ContainerItemPipe;
import alec_wam.CrystalMod.tiles.pipes.item.GuiItemPipe;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ContainerItemFilterNormal;
import alec_wam.CrystalMod.tiles.pipes.item.filters.GuiItemFilterNormal;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.pipes.liquid.ContainerLiquidPipe;
import alec_wam.CrystalMod.tiles.pipes.liquid.GuiLiquidPipe;
import alec_wam.CrystalMod.tiles.pipes.liquid.TileEntityPipeLiquid;
import alec_wam.CrystalMod.tiles.soundmuffler.GuiSoundMuffler;
import alec_wam.CrystalMod.tiles.soundmuffler.TileSoundMuffler;
import alec_wam.CrystalMod.tiles.tooltable.ContainerEnhancementTable;
import alec_wam.CrystalMod.tiles.tooltable.GuiEnhancementTable;
import alec_wam.CrystalMod.tiles.tooltable.TileEnhancementTable;
import alec_wam.CrystalMod.tiles.weather.ContainerWeather;
import alec_wam.CrystalMod.tiles.weather.GuiWeather;
import alec_wam.CrystalMod.tiles.weather.TileEntityWeather;
import alec_wam.CrystalMod.tiles.workbench.ContainerCrystalWorkbench;
import alec_wam.CrystalMod.tiles.workbench.GuiCrystalWorkbench;
import alec_wam.CrystalMod.tiles.workbench.TileEntityCrystalWorkbench;
import alec_wam.CrystalMod.tiles.xp.GuiXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPTank;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int GUI_ID_ITEM = 10;
	public static final int GUI_ID_TE_FACING = 16;
	public static final int GUI_ID_WORK_CONFIG = 30;
	public static final int GUI_ID_WORK_BOUNDS = 31;
	public static final int GUI_ID_WORK_ALT = 32;
	public static final int GUI_ID_ENTITY = 5;
	public static final int GUI_ID_GUIDE = 6;
	public static final int GUI_ID_BACKPACK = 7;
	public static final int GUI_ID_EXTENDED = 9;
	public static final int GUI_ID_BACKPACK_BLOCK = 100;

    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
    	if(ID == GUI_ID_EXTENDED){
    		return new GuiExtendedInventory(player);
    	}
    	if(ID == GUI_ID_ENTITY){
    		Entity entity = world == null ? null : world.getEntityByID(x);
    		if(entity !=null){
    			if(entity instanceof EntityMinionWarrior){
    				EntityMinionWarrior warrior = (EntityMinionWarrior)entity;
    				if(y == 0){
    					return new GuiMinionWarrior(player, warrior);
    				}
    			}
    			if(entity instanceof EntityCrystalChestMinecartBase){
    				EntityCrystalChestMinecartBase minecart = (EntityCrystalChestMinecartBase)entity;
    				return GUIChest.GUI.buildGUI(minecart.getChestType(), player.inventory, minecart);
    			}
    			if(entity instanceof EntityWirelessChestMinecart){
    				EntityWirelessChestMinecart minecart = (EntityWirelessChestMinecart)entity;
    				return new GuiWirelessChest(player.inventory, minecart);
    			}
    			
    			if(entity instanceof EntityBoatChest){
    				EntityBoatChest chest = (EntityBoatChest)entity;
    				if(chest.getInventory() !=null){
    					return new GuiWirelessChest(player.inventory, chest);
    				}
    			}
    			
    			if(entity instanceof EntityHorse){
    				EntityHorse horse = (EntityHorse)entity;
    				if(HorseAccessories.hasEnderChest(horse)){
    					ContainerHorseChest horseChest = new ContainerHorseChest("HorseChest", 2);
    					horseChest.setCustomName(horse.getName());
    					return new GuiHorseEnderChest(player.inventory, horseChest, horse);
    				}
    			}
    		}
    		return null;
    	}
    	if(ID == GUI_ID_GUIDE){
    		if(CrystalMod.proxy.getForcedGuidePage() !=null && CrystalMod.proxy.getForcedGuidePage() instanceof GuiGuideBase){
    			final GuiGuideBase gui = (GuiGuideBase)CrystalMod.proxy.getForcedGuidePage();
    			CrystalMod.proxy.setForcedGuidePage(null);
    			return gui;
    		}
    		ExtendedPlayer playerData = ExtendedPlayerProvider.getExtendedPlayer(player);
    		if(playerData !=null){
    			if(playerData.lastOpenBook !=null){
    				return playerData.lastOpenBook;
    			}
    		}
    		return new GuiGuideMainPage();
    	}
    	if(ID == GUI_ID_BACKPACK){
    		ItemStack backpack = BackpackUtil.getPlayerBackpack(player);
    		if(ItemStackTools.isValid(backpack)){
    			IBackpack type = BackpackUtil.getType(backpack);
    			if(type == null)return null;
    			if(z >= 1){
    				ExtendedPlayerProvider.getExtendedPlayer(player).setOpenBackpack(backpack);
    				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack);
    				BackpackUpgrade[] tabs = upgradeInv.getTabs();
    				if(y > 0 && tabs.length > 0){
    					int index = y-1;
    					BackpackUpgrade tab = tabs[index % tabs.length];
    					if(tab !=null){
    						BackpackOpenSourceItem source = new BackpackOpenSourceItem(player, backpack);
    						return new GuiBackpackUpgradeWindow(player.inventory, upgradeInv, tab, source);
    					}
    				}
    				return new GuiBackpackUpgrades(player.inventory, upgradeInv);
    			}
    			return type.getClientGuiElement(player, world);
    		}
    	}
    	if(ID >= GUI_ID_BACKPACK_BLOCK){
    		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    		if(te !=null && te instanceof TileEntityBackpack){
    			TileEntityBackpack backpack = ((TileEntityBackpack)te);
    			IBackpack type = BackpackUtil.getType(backpack.getBackpack());
    			if(type !=null && ID > GUI_ID_BACKPACK_BLOCK){
    				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack.getBackpack());
    				BackpackUpgrade[] tabs = upgradeInv.getTabs();
    				int index = ID-GUI_ID_BACKPACK_BLOCK;
					if(index > 1 && tabs.length > 0){
    					BackpackUpgrade tab = tabs[(index - 2) % tabs.length];
    					if(tab !=null){
    						BackpackOpenSourceBlock source = new BackpackOpenSourceBlock(player, backpack);
    						return new GuiBackpackUpgradeWindow(player.inventory, upgradeInv, tab, source);
    					}
    				}
    				return new GuiBackpackUpgrades(player.inventory, upgradeInv);
    			}
    			return backpack.getClientGuiElement(player, world);
    		}
    	}
    	if(ID == GUI_ID_ITEM){
    		EnumHand hand = z > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    		if(player.getHeldItem(hand) !=null){
    			ItemStack held = player.getHeldItem(hand);
    			if(held.getItem() instanceof ItemWirelessPanel){
					BlockPos pos = ItemWirelessPanel.getBlockPos(held);

    				TileEntity te = world.getTileEntity(pos);
    		        if (te != null)
    		        {
    		        	if(te instanceof TileEntityWirelessPanel){
    		        		return new GuiPanel(player.inventory, new PanelSourceWireless((TileEntityWirelessPanel)te, held));
    		        	}
    		        }
    			}
    			if(held.getItem() instanceof ItemPipeFilter){
    				if(held.getMetadata() == FilterType.NORMAL.ordinal() || held.getMetadata() == FilterType.MOD.ordinal()){
    					return new GuiItemFilterNormal(player, held, hand);
    				}
    			}
    		}
    		return null;
    	}
    	
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null)
        {
        	if(ID == GUI_ID_WORK_BOUNDS && te instanceof TileWorksiteBase){return new GuiWorksiteBoundsAdjust(new ContainerWorksiteBoundsAdjust(player, (TileWorksiteBase)te));}
    		if(ID == GUI_ID_WORK_CONFIG && te instanceof TileWorksiteBoundedInventory){return new GuiWorksiteInventorySideSelection(new ContainerWorksiteInventorySideSelection(player, (TileWorksiteBoundedInventory)te));}
    		if(ID == GUI_ID_WORK_ALT ){
    			if(te instanceof WorksiteAnimalFarm)return new GuiWorksiteAnimalControl(new ContainerWorksiteAnimalControl(player, (WorksiteAnimalFarm)te));
			}
        	if(ID >=GUI_ID_TE_FACING && ID <=GUI_ID_TE_FACING+EnumFacing.VALUES.length){
        		EnumFacing dir = EnumFacing.getFront(ID-GUI_ID_TE_FACING);
        		if(te instanceof TileEntityPipeEStorage){
        			TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)te;
        			if(pipe.getAttachmentData(dir) !=null){
        				return pipe.getAttachmentData(dir).getGui(player, pipe, dir);
        			}
        			return new GuiEStoragePipe(player.inventory, (TileEntityPipeEStorage) te, dir);
        		}
        		if(te instanceof TileEntityPipeItem){
        			return new GuiItemPipe(player.inventory, (TileEntityPipeItem) te, dir);
        		}
        		if(te instanceof TileEntityPipeLiquid){
        			return new GuiLiquidPipe(player.inventory, (TileEntityPipeLiquid) te, dir);
        		}
        	}
        	
        	
        	if(te instanceof WorksiteTreeFarm){
        		return new GuiWorksiteTreeFarm(new ContainerWorksiteTreeFarm(player, (WorksiteTreeFarm)te));
        	}
        	
        	if(te instanceof WorksiteAnimalFarm){
        		return new GuiWorksiteAnimalFarm(new ContainerWorksiteAnimalFarm(player, (WorksiteAnimalFarm)te));
        	}
        	
        	if(te instanceof WorksiteCropFarm){
        		return new GuiWorksiteCropFarm(new ContainerWorksiteCropFarm(player, (WorksiteCropFarm)te));
        	}
        	
            if(te instanceof TileEntityBlueCrystalChest){
        		TileEntityBlueCrystalChest icte = (TileEntityBlueCrystalChest) te;
            	return GUIChest.GUI.buildGUI(icte.getType(), player.inventory, icte);
            }
            if(te instanceof TileWoodenCrystalChest){
            	TileWoodenCrystalChest icte = (TileWoodenCrystalChest) te;
            	return GUIWoodenChest.GUI.buildGUI(icte.getType(), player.inventory, icte);
            }
            if(te instanceof TileWirelessChest) return new GuiWirelessChest(player.inventory, (TileWirelessChest)te);
            if(te instanceof TileEntityCrystalWorkbench)return new GuiCrystalWorkbench(player.inventory, world, (TileEntityCrystalWorkbench) te);
            if(te instanceof TileAdvDispenser)return new GuiAdvDispenser(player.inventory, (TileAdvDispenser) te);
            if(te instanceof TileEntityHDDInterface)return new GuiHDDInterface(player.inventory, (TileEntityHDDInterface) te);
            if(te instanceof TileHDDArray)return new GuiHDDArray(player.inventory, (TileHDDArray) te);
            if(te instanceof TileEntityPanelMonitor)return new GuiPanelMonitor(player, (TileEntityPanelMonitor) te);
            if(te instanceof TileNetworkPowerCore)return new GuiPowerCore(player, (TileNetworkPowerCore) te);
            if(te instanceof TileEntityPanelCrafting)return new GuiPanelCrafting(player.inventory, (TileEntityPanelCrafting) te);
            if(te instanceof TileEntityPanel)return new GuiPanel(player.inventory, new PanelSourceNormal((TileEntityPanel) te));
        	if(te instanceof TilePatternEncoder) return new GuiPatternEncoder(player, (TilePatternEncoder)te);
        	if(te instanceof TileCrafter) return new GuiCrafter(player, (TileCrafter)te);
        	if(te instanceof TileSecurityController) return new GuiSecurityController(player, (TileSecurityController)te);
        	if(te instanceof TileSecurityEncoder) return new GuiSecurityEncoder(player, (TileSecurityEncoder)te);
        	if(te instanceof TileEntityStocker) return new GuiStocker(player.inventory, (TileEntityStocker)te);
        	if(te instanceof TileEntityWeather) return new GuiWeather((TileEntityWeather)te);
        	if(te instanceof TileEntityMachine) return ((TileEntityMachine)te).getGui(player, ID);
        	if(te instanceof TileEntityEngineFurnace) return new GuiEngineFurnace(player, (TileEntityEngineFurnace)te);
        	if(te instanceof TileEntityEngineLava) return new GuiEngineLava(player, (TileEntityEngineLava)te);
        	if(te instanceof TileEntityEngineVampire) return new GuiEngineVampire(player, (TileEntityEngineVampire)te);
        	if(te instanceof TileEntityEnderBuffer) return new GuiEnderBuffer(player, (TileEntityEnderBuffer)te);
        	if(te instanceof TileEntityBattery) return new GuiBattery(player, (TileEntityBattery)te);
        	if(te instanceof TileSoundMuffler) return new GuiSoundMuffler((TileSoundMuffler)te);
        	if(te instanceof TileEnhancementTable) return new GuiEnhancementTable(player.inventory, (TileEnhancementTable)te);
        	if(te instanceof TileRedstoneReactor) return new GuiRedstoneReactor(player, (TileRedstoneReactor)te);
        	if(te instanceof TileEntityCaseBase) return new GuiCase(player, (TileEntityCaseBase)te);
        	if(te instanceof TileObsidianDispenser) return new GuiObsidianDispener(player.inventory, (TileObsidianDispenser)te);
        	if(te instanceof TileEPST) return new GuiEPST(player.inventory, (TileEPST)te);
        	if(te instanceof TileEntitySeismicScanner)return new GuiSeismicScanner((TileEntitySeismicScanner)te);    
        	if(te instanceof TileEntityXPTank)return new GuiXPTank((TileEntityXPTank)te);       	               
        	if(te instanceof TileEntityEnhancedEnchantmentTable)return new GuiEnhancedEnchantmentTable(player, (TileEntityEnhancedEnchantmentTable)te);    
        	
        } 
        return null;
    }
	
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
    	if(ID == GUI_ID_EXTENDED){
    		return new ContainerExtendedInventory(player.inventory, !world.isRemote, player);
    	}
    	if(ID == GUI_ID_ENTITY){
    		Entity entity = world == null ? null : world.getEntityByID(x);
    		if(entity !=null){
    			if(entity instanceof EntityMinionWarrior){
    				EntityMinionWarrior warrior = (EntityMinionWarrior)entity;
    				if(y == 0){
    					return new ContainerMinionWarrior(player, warrior);
    				}
    			}
    			if(entity instanceof EntityCrystalChestMinecartBase){
    				EntityCrystalChestMinecartBase minecart = (EntityCrystalChestMinecartBase)entity;
    				return new ContainerCrystalChest(player.inventory, minecart, minecart.getChestType(), 0, 0);
    			}
    			if(entity instanceof EntityWirelessChestMinecart){
    				EntityWirelessChestMinecart minecart = (EntityWirelessChestMinecart)entity;
    				return new ContainerWirelessChest(player.inventory, minecart);
    			}
    			if(entity instanceof EntityBoatChest){
    				EntityBoatChest chest = (EntityBoatChest)entity;
    				if(chest.getInventory() !=null){
    					return new ContainerWirelessChest(player.inventory, chest);
    				}
    			}
    			if(entity instanceof AbstractHorse){
    				AbstractHorse horse = (AbstractHorse)entity;
    				if(HorseAccessories.hasEnderChest(horse)){
    					IInventory horseChest = HorseAccessories.getHorseChest(horse);
    					if(horseChest !=null)return new ContainerHorseEnderChest(player.inventory, horseChest, horse, player);
    				}
    			}
    		}
    		return null;
    	}
    	if(ID == GUI_ID_BACKPACK){
    		ItemStack backpack = BackpackUtil.getPlayerBackpack(player);
    		if(ItemStackTools.isValid(backpack) && backpack.getItem() instanceof ItemBackpackBase){
    			IBackpack type = BackpackUtil.getType(backpack);
    			if(type == null)return null;
    			if(z >= 1){
    				ExtendedPlayerProvider.getExtendedPlayer(player).setOpenBackpack(backpack);
    				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack);
    				BackpackUpgrade[] tabs = upgradeInv.getTabs();
    				if(y > 0 && tabs.length > 0){
    					int index = y-1;
    					BackpackUpgrade tab = tabs[index % tabs.length];
    					if(tab !=null){
    						return new ContainerBackpackUpgradeWindow(player.inventory, upgradeInv, tab);
    					}
    				}
    				return new ContainerBackpackUpgrades(player.inventory, upgradeInv);
    			}
    			return type.getServerGuiElement(player, world);
    		}
    	}
    	if(ID >= GUI_ID_BACKPACK_BLOCK){
    		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    		if(te !=null && te instanceof TileEntityBackpack){
    			TileEntityBackpack backpack = ((TileEntityBackpack)te);
    			IBackpack type = BackpackUtil.getType(backpack.getBackpack());
    			if(type !=null && ID > GUI_ID_BACKPACK_BLOCK){
    				InventoryBackpackUpgrades upgradeInv = type.getUpgradeInventory(backpack.getBackpack());
    				BackpackUpgrade[] tabs = upgradeInv.getTabs();
    				int index = ID-GUI_ID_BACKPACK_BLOCK;
					if(index > 1 && tabs.length > 0){
    					BackpackUpgrade tab = tabs[(index - 2) % tabs.length];
    					if(tab !=null){
    						return new ContainerBackpackUpgradeWindow(player.inventory, upgradeInv, tab);
    					}
    				}
    				return new ContainerBackpackUpgrades(player.inventory, upgradeInv);
    			}
    			return backpack.getServerGuiElement(player, world);
    		}
    	}
    	if(ID == GUI_ID_ITEM){
    		EnumHand hand = z > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    		if(player.getHeldItem(hand) !=null){
    			ItemStack held = player.getHeldItem(hand);
    			if(held.getItem() instanceof ItemWirelessPanel){
					BlockPos pos = ItemWirelessPanel.getBlockPos(held);

    				TileEntity te = world.getTileEntity(pos);
    		        if (te != null)
    		        {
    		        	if(te instanceof TileEntityWirelessPanel){
    		        		return new ContainerPanel(player.inventory, new PanelSourceWireless((TileEntityWirelessPanel)te, held));
    		        	}
    		        }
    			}
    			if(held.getItem() instanceof ItemPipeFilter){
    				if(held.getMetadata() == FilterType.NORMAL.ordinal() || held.getMetadata() == FilterType.MOD.ordinal()){
    					return new ContainerItemFilterNormal(player, held, hand);
    				}
    			}
    		}
    		return null;
    	}
    	TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null)
        {
        	if(ID == GUI_ID_WORK_BOUNDS && te instanceof TileWorksiteBase){return new ContainerWorksiteBoundsAdjust(player, (TileWorksiteBase)te);}
    		if(ID == GUI_ID_WORK_CONFIG && te instanceof TileWorksiteBoundedInventory){return new ContainerWorksiteInventorySideSelection(player, (TileWorksiteBoundedInventory)te);}
    		if(ID == GUI_ID_WORK_ALT ){
    			if(te instanceof WorksiteAnimalFarm)return new ContainerWorksiteAnimalControl(player, (WorksiteAnimalFarm)te);
			}

        	if(ID >=GUI_ID_TE_FACING && ID <=GUI_ID_TE_FACING+EnumFacing.VALUES.length){
        		EnumFacing dir = EnumFacing.getFront(ID-GUI_ID_TE_FACING);
        		if(te instanceof TileEntityPipeEStorage){
        			TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)te;
        			if(pipe.getAttachmentData(dir) !=null){
        				return pipe.getAttachmentData(dir).getContainer(player, pipe, dir);
        			}
        		}
        		if(te instanceof TileEntityPipeItem)return new ContainerItemPipe(player.inventory, (TileEntityPipeItem)te, dir);
        		
        	}
        	
        	if(te instanceof WorksiteTreeFarm){
        		return new ContainerWorksiteTreeFarm(player, (WorksiteTreeFarm)te);
        	}
        	if(te instanceof WorksiteAnimalFarm){
        		return new ContainerWorksiteAnimalFarm(player, (WorksiteAnimalFarm)te);
        	}
        	if(te instanceof WorksiteCropFarm){
        		return new ContainerWorksiteCropFarm(player, (WorksiteCropFarm)te);
        	}
        	
        	if(te instanceof TileEntityPipeLiquid)return new ContainerLiquidPipe(player.inventory, (TileEntityPipeLiquid)te);
        	if(te instanceof TileEntityBlueCrystalChest){
        		TileEntityBlueCrystalChest icte = (TileEntityBlueCrystalChest) te;
        		return new ContainerCrystalChest(player.inventory, icte, icte.getType(), 0, 0);
        	}
        	if(te instanceof TileWoodenCrystalChest){
        		TileWoodenCrystalChest icte = (TileWoodenCrystalChest) te;
        		return new ContainerWoodenCrystalChest(player.inventory, icte, icte.getType(), 0, 0);
        	}
        	if(te instanceof TileWirelessChest) return new ContainerWirelessChest(player.inventory, (TileWirelessChest)te);
        	if(te instanceof TileEntityCrystalWorkbench){
        		return new ContainerCrystalWorkbench(player.inventory, world, (TileEntityCrystalWorkbench) te);
        	}
        	if(te instanceof TileAdvDispenser)return new ContainerAdvDispenser(player.inventory, (TileAdvDispenser) te);
        	//if(te instanceof TileEntityPipe)return ((TileEntityPipe)te).getContainer(ID, player);
        	
        	if(te instanceof TileEntityHDDInterface)return new ContainerHDDInterface(player.inventory, (TileEntityHDDInterface)te);
        	if(te instanceof TileHDDArray)return new ContainerHDDArray(player.inventory, (TileHDDArray) te);
        	if(te instanceof TileEntityPanelMonitor)return new ContainerPanelMonitor(player, (TileEntityPanelMonitor)te);
        	if(te instanceof TileNetworkPowerCore)return new ContainerPowerCore(player, (TileNetworkPowerCore) te);
        	if(te instanceof TileEntityPanelCrafting)return new ContainerPanelCrafting(player.inventory, (TileEntityPanelCrafting)te);
        	if(te instanceof TileEntityPanel)return new ContainerPanel(player.inventory, new PanelSourceNormal((TileEntityPanel)te));
        	if(te instanceof TilePatternEncoder) return new ContainerPatternEncoder(player, (TilePatternEncoder)te);
        	if(te instanceof TileCrafter) return new ContainerCrafter(player, (TileCrafter)te);
        	if(te instanceof TileSecurityController) return new ContainerSecurityController(player, (TileSecurityController)te);
        	if(te instanceof TileSecurityEncoder) return new ContainerSecurityEncoder(player, (TileSecurityEncoder)te);
        	if(te instanceof TileEntityStocker) return new ContainerStocker(player.inventory, (TileEntityStocker)te);
        	if(te instanceof TileEntityWeather) return new ContainerWeather();
        	if(te instanceof TileEntityMachine) return ((TileEntityMachine)te).getContainer(player, ID);
        	if(te instanceof TileEntityEngineFurnace) return new ContainerEngineFurnace(player, (TileEntityEngineFurnace)te);
        	if(te instanceof TileEntityEngineLava) return new ContainerEngineLava(player, (TileEntityEngineLava)te);
        	if(te instanceof TileEntityEngineVampire) return new ContainerEngineVampire(player, (TileEntityEngineVampire)te);
        	if(te instanceof TileEntityEnderBuffer) return new ContainerEnderBuffer(player, (TileEntityEnderBuffer)te);
        	if(te instanceof TileEntityBattery) return new ContainerBattery(player, (TileEntityBattery)te);
        	if(te instanceof TileSoundMuffler) return new ContainerNull();
        	if(te instanceof TileEnhancementTable) return new ContainerEnhancementTable(player.inventory, (TileEnhancementTable)te);
        	if(te instanceof TileRedstoneReactor) return new ContainerRedstoneReactor(player.inventory, (TileRedstoneReactor)te);
        	if(te instanceof TileEntityCaseBase) return new ContainerCase(player.inventory, (TileEntityCaseBase)te);
        	if(te instanceof TileObsidianDispenser) return new ContainerDispenser(player.inventory, (TileObsidianDispenser)te);
        	if(te instanceof TileEPST) return new ContainerEPST(player.inventory, (TileEPST)te);
        	if(te instanceof TileEntitySeismicScanner) return new ContainerNull();
        	if(te instanceof TileEntityXPTank) return new ContainerNull();
        	if(te instanceof TileEntityEnhancedEnchantmentTable)return new ContainerEnhancedEnchantmentTable(player.inventory, (TileEntityEnhancedEnchantmentTable)te); 
        	if(te instanceof TileEntityBackpack)return ((TileEntityBackpack)te).getServerGuiElement(player, world);
        }
        return null;
    }

	public static void openNetworkGui(World world, BlockPos pos, EntityPlayer player, NetworkAbility...abilities) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && TileEntityPipeEStorage.isNetworkTile(tile)){
			EStorageNetwork network = TileEntityPipeEStorage.getTileNetwork(tile);
			if(network !=null){
				if(network.hasAbility(player, abilities)){
					player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					ChatUtil.sendNoSpam(player, "You cannot open this screen.");
				}
			} else {
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}
	
}
