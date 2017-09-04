package alec_wam.CrystalMod.items;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant;
import alec_wam.CrystalMod.blocks.crops.BlockCrystalPlant.PlantType;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCrystalSeeds extends Item implements net.minecraftforge.common.IPlantable
{
	public final PlantType TYPE;
    public ItemCrystalSeeds(PlantType type)
    {
    	TYPE = type;
        this.setCreativeTab(CrystalMod.tabCrops);
        ModItems.registerItem(this, type.getName().toLowerCase()+"crystalseeds");
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

    public IBlockState getPlant(){
    	return ModBlocks.crystalPlant.getDefaultState().withProperty(BlockCrystalPlant.TYPE, TYPE);
    }
    
    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = playerIn.getHeldItem(hand);
    	if (side != EnumFacing.UP)
        {
            return EnumActionResult.PASS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return EnumActionResult.PASS;
        }
        else if (BlockCrystalPlant.getTypeFromBlock(worldIn.getBlockState(pos)) == TYPE && worldIn.isAirBlock(pos.up()))
        {
            worldIn.setBlockState(pos.up(), getPlant());
            ItemStackTools.incStackSize(stack, -1);
            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.PASS;
        }
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