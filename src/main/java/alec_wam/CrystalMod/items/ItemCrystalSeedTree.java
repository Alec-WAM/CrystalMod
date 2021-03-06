package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalSeedTree extends Item implements net.minecraftforge.common.IPlantable
{
	public final PlantType TYPE;
    public ItemCrystalSeedTree(PlantType type)
    {
    	TYPE = type;
        this.setCreativeTab(CrystalMod.tabCrops);
        ModItems.registerItem(this, type.getName().toLowerCase()+"crystaltreeseeds");
    }
    
    @Override
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
    
    public static IBlockState getPlant(CrystalColors.SuperSpecial type){
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
    
    public static IBlockState getPlant(CrystalColors.Basic type){
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
    @SuppressWarnings("deprecation")
	@Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = playerIn.getHeldItem(hand);
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
        	CrystalColors.Basic wood = iblockstate.getValue(CrystalColors.COLOR_BASIC);
        	if(wood == CrystalColors.Basic.BLUE && TYPE !=PlantType.BLUE)return EnumActionResult.PASS;
        	if(wood == CrystalColors.Basic.RED && TYPE !=PlantType.RED)return EnumActionResult.PASS;
        	if(wood == CrystalColors.Basic.GREEN && TYPE !=PlantType.GREEN)return EnumActionResult.PASS;
        	if(wood == CrystalColors.Basic.DARK && TYPE !=PlantType.DARK)return EnumActionResult.PASS;
            pos = pos.offset(side);

            if (worldIn.isAirBlock(pos))
            {
                IBlockState iblockstate1 = getPlant().getBlock().getStateForPlacement(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
                worldIn.setBlockState(pos, iblockstate1, 10);
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                
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