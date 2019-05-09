package alec_wam.CrystalMod.tiles;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.tiles.chests.metal.BlockMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChestRender;
import alec_wam.CrystalMod.tiles.chests.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChestRender;
import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chests.wooden.BlockWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChestRender;
import alec_wam.CrystalMod.tiles.chests.wooden.WoodenCrystalChestType;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.jar.TileEntityJarRender;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;

public class CustomItemRender extends TileEntityItemStackRenderer
{
    public static CustomItemRender instance = new CustomItemRender();

    @Override
    public void renderByItem(ItemStack itemStackIn)
    {
        Item item = itemStackIn.getItem();
        Block block = Block.getBlockFromItem(item);
        if (block instanceof BlockWoodenCrystalChest)
        {
        	WoodenCrystalChestType typeOut = ((BlockWoodenCrystalChest)block).type;
            if (typeOut == null)
            {
            	TileEntityWoodenCrystalChestRender.renderChest(0, 0, 0, WoodenCrystalChestType.BLUE, EnumFacing.NORTH, 0, -1);
            }
            else
            {
                TileEntityWoodenCrystalChestRender.renderChest(0, 0, 0, typeOut, EnumFacing.NORTH, 0, -1);
            }
        }
        else if (block instanceof BlockMetalCrystalChest)
        {
        	MetalCrystalChestType typeOut = ((BlockMetalCrystalChest)block).type;
            if (typeOut == null)
            {
            	TileEntityMetalCrystalChestRender.renderChest(0, 0, 0, MetalCrystalChestType.BLUE, EnumFacing.NORTH, 0, -1);
            }
            else
            {
            	TileEntityMetalCrystalChestRender.renderChest(0, 0, 0, typeOut, EnumFacing.NORTH, 0, -1);
            }
        }
        else if (block instanceof BlockWirelessChest)
        {
        	int code = ItemNBTHelper.getInteger(itemStackIn, WirelessChestHelper.NBT_CODE, 0);
        	TileEntityWirelessChestRender.renderChest(0, 0, 0, code, EnumFacing.NORTH, ItemNBTHelper.verifyExistance(itemStackIn, WirelessChestHelper.NBT_OWNER), 0, -1);
        }
        else if (block instanceof BlockJar)
        {
        	IBakedModel ibakedmodel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
        	RenderUtil.renderModel(ibakedmodel, itemStackIn);
        	PotionType type = PotionTypes.EMPTY;
    		int count = 0;
    		boolean lamp = false;
    		EnumMap<EnumFacing, Boolean> labels = Maps.newEnumMap(EnumFacing.class); 
    		if(ItemNBTHelper.verifyExistance(itemStackIn, "TILE_DATA")){
    			NBTTagCompound tileNBT = ItemNBTHelper.getCompound(itemStackIn).getCompound("TILE_DATA");
    			type = PotionUtils.getPotionTypeFromNBT(tileNBT);
    			count = tileNBT.getInt("Count");
    			lamp = tileNBT.getBoolean("IsShulker");
    			for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
    				labels.put(facing, tileNBT.getBoolean("Label."+facing.getName().toUpperCase()));
    			}
    		}    		
    		
        	for(int pass = 0; pass < 2; pass++){
    			TileEntityJarRender.renderInternalJar(0, 0, 0, type, count, lamp, labels, pass);
    		}
        }
        else
        {
            super.renderByItem(itemStackIn);
        }
    }
}