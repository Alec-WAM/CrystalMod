package alec_wam.CrystalMod.client;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chests.metal.GuiMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wireless.GuiWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wooden.GuiWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class GuiHandler{

	public static final ResourceLocation TILE_NORMAL = CrystalMod.resourceL("tile_normal");
	
	public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer)
    {
        BlockPos pos = openContainer.getAdditionalData().readBlockPos();        
    	TileEntity tile = Minecraft.getInstance().world.getTileEntity(pos);
    	if (tile != null)
        {
    		if(openContainer.getId().equals(TILE_NORMAL)){
	        	if(tile instanceof TileEntityWoodenCrystalChest){
	        		TileEntityWoodenCrystalChest chest = (TileEntityWoodenCrystalChest) tile;
	        		return GuiWoodenCrystalChest.GUI.buildGUI(chest.type, Minecraft.getInstance().player.inventory, chest);
	            }
	        	if(tile instanceof TileEntityMetalCrystalChest){
	        		TileEntityMetalCrystalChest chest = (TileEntityMetalCrystalChest) tile;
	        		return GuiMetalCrystalChest.GUI.buildGUI(chest.type, Minecraft.getInstance().player.inventory, chest);
	            }
	        	if(tile instanceof TileEntityWirelessChest){
	        		TileEntityWirelessChest chest = (TileEntityWirelessChest) tile;
	        		return new GuiWirelessChest(Minecraft.getInstance().player.inventory, chest);
	            }
	        	if(tile instanceof TileEntityEngineFurnace){
	        		TileEntityEngineFurnace engine = (TileEntityEngineFurnace) tile;
	        		return new GuiEngineFurnace(Minecraft.getInstance().player, engine);
	            }
    		}
        }
        return null;
    }

}
