package alec_wam.CrystalMod.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.util.ItemStackTools;

public class ItemCrystalSeedTree extends Item implements net.minecraftforge.common.IPlantable
{
	public final PlantType TYPE;
    public ItemCrystalSeedTree(PlantType type)
    {
    	TYPE = type;
        this.setCreativeTab(CrystalMod.tabItems);
        ModItems.registerItem(this, type.getName().toLowerCase()+"crystaltreeseeds");
    }
    
    @SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

    public IBlockState getPlant(){
    	return getPlant(TYPE);
    }
    
    public static IBlockState getPlant(PlantType type){
    	switch(type){
	    	 default : case BLUE : {
	    		 return ModBlocks.crystalTreePlantBlue.getDefaultState();
	    	 }
	    	 case RED : {
	    		 return ModBlocks.crystalTreePlantRed.getDefaultState();
	    	 }
	    	 case GREEN : {
	    		 return ModBlocks.crystalTreePlantGreen.getDefaultState();
	    	 }
	    	 case DARK : {
	    		 return ModBlocks.crystalTreePlantDark.getDefaultState();
	    	 }
    	}
    }
    
    public static IBlockState getPlant(WoodType type){
    	switch(type){
	    	 default : case BLUE : {
	    		 return ModBlocks.crystalTreePlantBlue.getDefaultState();
	    	 }
	    	 case RED : {
	    		 return ModBlocks.crystalTreePlantRed.getDefaultState();
	    	 }
	    	 case GREEN : {
	    		 return ModBlocks.crystalTreePlantGreen.getDefaultState();
	    	 }
	    	 case DARK : {
	    		 return ModBlocks.crystalTreePlantDark.getDefaultState();
	    	 }
    	}
    }
    
    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if (side == EnumFacing.DOWN && side == EnumFacing.UP)
        {
            return EnumActionResult.PASS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return EnumActionResult.PASS;
        }
    	IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (block == ModBlocks.crystalLog)
        {
        	WoodType wood = iblockstate.getValue(BlockCrystalLog.VARIANT);
        	if(wood == WoodType.BLUE && TYPE !=PlantType.BLUE)return EnumActionResult.PASS;
        	if(wood == WoodType.RED && TYPE !=PlantType.RED)return EnumActionResult.PASS;
        	if(wood == WoodType.GREEN && TYPE !=PlantType.GREEN)return EnumActionResult.PASS;
        	if(wood == WoodType.DARK && TYPE !=PlantType.DARK)return EnumActionResult.PASS;
            pos = pos.offset(side);

            if (worldIn.isAirBlock(pos))
            {
                IBlockState iblockstate1 = getPlant().getBlock().onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
                worldIn.setBlockState(pos, iblockstate1, 10);

                if (!playerIn.capabilities.isCreativeMode)
                {
                	ItemStackTools.incStackSize(stack, -1);
                }
            }

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
        return ModBlocks.crystalPlantType;
    }

    @Override
    public net.minecraft.block.state.IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos)
    {
    	return getPlant();
    }
}