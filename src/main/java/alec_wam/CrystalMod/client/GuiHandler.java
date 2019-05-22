package alec_wam.CrystalMod.client;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.tiles.chests.metal.GuiMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wireless.GuiWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wooden.GuiWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.energy.battery.GuiBattery;
import alec_wam.CrystalMod.tiles.energy.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.pipes.item.GuiItemPipe;
import alec_wam.CrystalMod.tiles.pipes.item.GuiPipeFilter;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class GuiHandler{

	public static final ResourceLocation ITEM_NORMAL = CrystalMod.resourceL("item_normal");
	public static final ResourceLocation TILE_NORMAL = CrystalMod.resourceL("tile_normal");
	public static final ResourceLocation TILE_PIPE_CONNECTOR = CrystalMod.resourceL("tile_pipe_connector");

	public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer)
    {
    	EntityPlayer player = Minecraft.getInstance().player;
        if(openContainer.getId().equals(TILE_NORMAL) || openContainer.getId().equals(TILE_PIPE_CONNECTOR)){
			BlockPos pos = openContainer.getAdditionalData().readBlockPos();        
	    	TileEntity tile = Minecraft.getInstance().world.getTileEntity(pos);
	    	if (tile != null)
	        {
	    		if(openContainer.getId().equals(TILE_NORMAL)){
		        	if(tile instanceof TileEntityWoodenCrystalChest){
		        		TileEntityWoodenCrystalChest chest = (TileEntityWoodenCrystalChest) tile;
		        		return GuiWoodenCrystalChest.GUI.buildGUI(chest.type, player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityMetalCrystalChest){
		        		TileEntityMetalCrystalChest chest = (TileEntityMetalCrystalChest) tile;
		        		return GuiMetalCrystalChest.GUI.buildGUI(chest.type, player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityWirelessChest){
		        		TileEntityWirelessChest chest = (TileEntityWirelessChest) tile;
		        		return new GuiWirelessChest(player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityEngineFurnace){
		        		TileEntityEngineFurnace engine = (TileEntityEngineFurnace) tile;
		        		return new GuiEngineFurnace(player, engine);
		            }
		        	if(tile instanceof TileEntityBattery){
		        		TileEntityBattery battery = (TileEntityBattery) tile;
		        		return new GuiBattery(player, battery);
		            }
	    		}
	    		if(openContainer.getId().equals(TILE_PIPE_CONNECTOR)){
	    			if(tile instanceof TileEntityPipeItem){
	    				TileEntityPipeItem pipe = (TileEntityPipeItem) tile;
	    				EnumFacing facing = openContainer.getAdditionalData().readEnumValue(EnumFacing.class);
		        		return new GuiItemPipe(player, pipe, facing);
	    			}
	    		}
	        }
        }
        if(openContainer.getId().equals(ITEM_NORMAL)){
        	EnumHand hand = openContainer.getAdditionalData().readEnumValue(EnumHand.class);
        	ItemStack stack = Minecraft.getInstance().player.getHeldItem(hand);
        	if(stack.getItem() == ModItems.pipeFilter){
        		return new GuiPipeFilter(player, stack, hand);
        	}
        }
        return null;
    }

}
