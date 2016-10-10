package com.alec_wam.CrystalMod.proxy;

import java.io.File;

import org.apache.logging.log4j.Level;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.capability.ExtendedPlayer;
import com.alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import com.alec_wam.CrystalMod.crafting.ModCrafting;
import com.alec_wam.CrystalMod.enchantment.ModEnchantments;
import com.alec_wam.CrystalMod.entities.ModEntites;
import com.alec_wam.CrystalMod.entities.minions.warrior.ContainerMinionWarrior;
import com.alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import com.alec_wam.CrystalMod.fluids.Fluids;
import com.alec_wam.CrystalMod.integration.TConstructIntegration;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.backpack.BackpackUtils;
import com.alec_wam.CrystalMod.items.backpack.ItemBackpack;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpack;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpackCrafting;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpackEnderChest;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpackFurnace;
import com.alec_wam.CrystalMod.items.backpack.container.ContainerBackpackRepair;
import com.alec_wam.CrystalMod.items.guide.GuidePages;
import com.alec_wam.CrystalMod.network.AbstractPacket;
import com.alec_wam.CrystalMod.tiles.accessories.clock.Alarm;
import com.alec_wam.CrystalMod.tiles.chest.ContainerCrystalChest;
import com.alec_wam.CrystalMod.tiles.chest.TileEntityBlueCrystalChest;
import com.alec_wam.CrystalMod.tiles.machine.ContainerNull;
import com.alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import com.alec_wam.CrystalMod.tiles.machine.enderbuffer.gui.ContainerEnderBuffer;
import com.alec_wam.CrystalMod.tiles.machine.mobGrinder.TileEntityMobGrinder;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.furnace.ContainerEngineFurnace;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.furnace.TileEntityEngineFurnace;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.lava.ContainerEngineLava;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.lava.TileEntityEngineLava;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import com.alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteAnimalControl;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteBoundsAdjust;
import com.alec_wam.CrystalMod.tiles.machine.worksite.gui.ContainerWorksiteInventorySideSelection;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteAnimalFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteCropFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.ContainerWorksiteTreeFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import com.alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import com.alec_wam.CrystalMod.tiles.matter.MatterRegistry;
import com.alec_wam.CrystalMod.tiles.pipes.ContainerNormalPipe;
import com.alec_wam.CrystalMod.handler.EventHandler;
import com.alec_wam.CrystalMod.tiles.pipes.PipeNetworkTickHandler;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil;
import com.alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerCrafter;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerPatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileCrafter;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.ContainerPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.ContainerPanelMonitor;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.TileEntityPanelMonitor;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ContainerPanelWireless;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.ItemWirelessPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless.TileEntityWirelessPanel;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.ContainerHDDInterface;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.TileEntityHDDInterface;
import com.alec_wam.CrystalMod.tiles.pipes.item.ContainerItemPipe;
import com.alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import com.alec_wam.CrystalMod.tiles.pipes.liquid.ContainerLiquidPipe;
import com.alec_wam.CrystalMod.tiles.pipes.liquid.TileEntityPipeLiquid;
import com.alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import com.alec_wam.CrystalMod.tiles.weather.ContainerWeather;
import com.alec_wam.CrystalMod.tiles.weather.TileEntityWeather;
import com.alec_wam.CrystalMod.tiles.workbench.ContainerCrystalWorkbench;
import com.alec_wam.CrystalMod.tiles.workbench.TileEntityCrystalWorkbench;
import com.alec_wam.CrystalMod.util.FarmUtil;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.world.CrystalModWorldGenerator;
import com.alec_wam.CrystalMod.world.DropCapture;
import com.alec_wam.CrystalMod.world.ModDimensions;
import com.alec_wam.CrystalMod.world.WorldTickHandler;
import com.alec_wam.CrystalMod.world.game.tag.TagManager;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CommonProxy implements IGuiHandler {

	public static File modConfigDir;
	public Configuration mainConfig;
	
	public void preInit(FMLPreInitializationEvent e) {
		modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "crystalmod", "crystalmod.cfg"));
        
        readMainConfig();
        
        MinecraftForge.EVENT_BUS.register(new Config());
        
        Fluids.registerFluids();
        
		ModBlocks.init();
		ModItems.init();
		ModEntites.init();
        
        MatterRegistry.init();
        
        ItemMobEssence.initDefaultMobs();
        
        
        CapabilityManager.INSTANCE.register(ExtendedPlayer.class, new ExtendedPlayerProvider.Storage(), ExtendedPlayer.class);
        
        CrystalModWorldGenerator generator = CrystalModWorldGenerator.instance;
        GameRegistry.registerWorldGenerator(generator, 5);
        MinecraftForge.EVENT_BUS.register(generator);
        
        MinecraftForge.EVENT_BUS.register(DropCapture.instance);
        
        
        
        if(Loader.isModLoaded("tconstruct")){
        	TConstructIntegration.preInit();
        }
	}
	
	public void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(Config.CATEGORY_GENERAL, "General settings");

            Config.init(cfg);
            Config.postInit();
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }
	
	public void init(FMLInitializationEvent event) {
		ModDimensions.register();
		MatterRegistry.initValues();
		AttachmentUtil.initAttachments();
		MinecraftForge.EVENT_BUS.register(PipeNetworkTickHandler.instance);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
		MinecraftForge.EVENT_BUS.register(Alarm.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		new TagManager();
		
		ModCrafting.init();
		if(Loader.isModLoaded("tconstruct")){
        	TConstructIntegration.init();
        }
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		ModItems.pipeCover.initialize();
		for(ItemStack cover : ItemPipeCover.coverRecipes.keySet()){
			ItemStack cover6 = cover.copy();
			cover6.stackSize = 6;
			GameRegistry.addRecipe(new ShapedOreRecipe(cover6, new Object[] { "S ", "CN", 'C', ItemPipeCover.coverRecipes.get(cover), 'S', "slimeball", 'N', "nuggetCrystal" }));
		}
		if(Fluids.fluidXpJuice == null) { //should have been registered by enderio
	      Fluids.forgeRegisterXPJuice();      
	    }
		
		ModEnchantments.init();
		ModCrafting.addSlabToBlocks();
		GuidePages.createPages();
		FarmUtil.addDefaultCrops();
	}
	
	
	public void sendPacketToServerOnly(AbstractPacket packet) {

	}
	
	@Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
		/*TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null)
        {
        	if(te instanceof TileEntityPipeItem)return new GuiItemPipe(player.inventory, (TileEntityPipeItem) te, EnumFacing.getFront(ID));
        }*/
        return null;
    }

	public static final int GUI_ID_ITEM = 10;
	public static final int GUI_ID_TE_FACING = 16;
	public static final int GUI_ID_WORK_CONFIG = 30;
	public static final int GUI_ID_WORK_BOUNDS = 31;
	public static final int GUI_ID_WORK_ALT = 32;
	public static final int GUI_ID_ENTITY = 5;
	
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
    	if(ID == GUI_ID_ENTITY){
    		Entity entity = world == null ? null : world.getEntityByID(x);
    		if(entity !=null){
    			if(entity instanceof EntityMinionWarrior){
    				EntityMinionWarrior warrior = (EntityMinionWarrior)entity;
    				if(y == 0){
    					return new ContainerMinionWarrior(player, warrior);
    				}
    			}
    		}
    		return null;
    	}
    	if(ID == GUI_ID_ITEM){
    		if(y >=0 && y < player.inventory.getSizeInventory()){
    			ItemStack held = player.inventory.getStackInSlot(y);
    			if(held !=null && held.getItem() instanceof ItemBackpack){
    				if(x == 1 && BackpackUtils.hasCraftingUpgrade(held)){
    					return new ContainerBackpackCrafting(player, held);
    				}
    				if(x == 2 && BackpackUtils.hasEnderChestUpgrade(held)){
    					return new ContainerBackpackEnderChest(player, held);
    				}
    				if(x == 3 && BackpackUtils.hasAnvilUpgrade(held)){
    					return new ContainerBackpackRepair(player, held);
    				}
    				if(x == 4 && BackpackUtils.hasFurnaceUpgrade(held)){
    					return new ContainerBackpackFurnace(player, held);
    				}
    				return new ContainerBackpack(player, held);
    			}
    		}
    		EnumHand hand = z > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    		if(player.getHeldItem(hand) !=null){
    			ItemStack held = player.getHeldItem(hand);
    			if(held.getItem() instanceof ItemWirelessPanel){
					BlockPos pos = ItemWirelessPanel.getBlockPos(held);

    				TileEntity te = world.getTileEntity(pos);
    		        if (te != null)
    		        {
    		        	if(te instanceof TileEntityWirelessPanel){
    		        		return new ContainerPanelWireless(player.inventory, (TileEntityWirelessPanel)te);
    		        	}
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
        	if(te instanceof TileEntityCrystalWorkbench){
        		return new ContainerCrystalWorkbench(player.inventory, world, (TileEntityCrystalWorkbench) te);
        	}
        	
        	//if(te instanceof TileEntityPipe)return ((TileEntityPipe)te).getContainer(ID, player);
        	
        	if(te instanceof TileEntityHDDInterface)return new ContainerHDDInterface(player.inventory, (TileEntityHDDInterface)te);
        	if(te instanceof TileEntityPanelMonitor)return new ContainerPanelMonitor(player, (TileEntityPanelMonitor)te);
        	if(te instanceof TileEntityPanelCrafting)return new ContainerPanelCrafting(player.inventory, (TileEntityPanelCrafting)te);
        	if(te instanceof TileEntityPanel)return new ContainerPanel(player.inventory, (TileEntityPanel)te);
        	if(te instanceof TilePatternEncoder) return new ContainerPatternEncoder(player, (TilePatternEncoder)te);
        	if(te instanceof TileCrafter) return new ContainerCrafter(player, (TileCrafter)te);
        	if(te instanceof TileEntityWeather) return new ContainerWeather();
        	if(te instanceof TileEntityMachine) return ((TileEntityMachine)te).getContainer(player, ID);
        	if(te instanceof TileEntityEngineFurnace) return new ContainerEngineFurnace(player, (TileEntityEngineFurnace)te);
        	if(te instanceof TileEntityEngineLava) return new ContainerEngineLava(player, (TileEntityEngineLava)te);
        	if(te instanceof TileEntityEnderBuffer) return new ContainerEnderBuffer(player, (TileEntityEnderBuffer)te);
        	if(te instanceof TileEntityMobGrinder) return new ContainerNull();
        }
        return null;
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    	return entityPlayer instanceof EntityPlayerMP ? ((EntityPlayerMP)entityPlayer).interactionManager.getBlockReachDistance() : 5;
    }

	public World getWorld(int dim) {
		if(!DimensionManager.isDimensionRegistered(dim)){
			System.out.println("Tried getting unregister world "+dim);
			return null;
		}
		return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dim);
	}

	public World getClientWorld() {
		return null;
	}

	public EntityPlayer getPlayerForUsername(String playerName) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
	}

	public boolean isOp(GameProfile profile) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(profile);
	}

	public boolean isShiftKeyDown() {
		return false;
	}
	
}
