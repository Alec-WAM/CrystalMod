package alec_wam.CrystalMod.blocks;

import java.util.Locale;

import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockCrystalLadder extends ItemColored {

	public ItemBlockCrystalLadder(Block block) {
		super(block, true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		@SuppressWarnings("deprecation")
		IBlockState state = block.getStateFromMeta(stack.getMetadata());
		CrystalColors.Basic type = state.getValue(CrystalColors.COLOR_BASIC);
		String name = type.toString().toLowerCase(Locale.US);
	    return super.getUnlocalizedName(stack) + "." + name;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getDefaultState().withProperty(CrystalColors.COLOR_BASIC, CrystalColors.Basic.byMetadata(i));

            if (facing.getAxis().isHorizontal() && ((BlockCrystalLadder)this.block).canBlockStay(worldIn, pos, facing))
            {
            	iblockstate1 = iblockstate1.withProperty(BlockCrystalLadder.FACING, facing);
            }
            else
            {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    if (((BlockCrystalLadder)this.block).canBlockStay(worldIn, pos, enumfacing))
                    {
                    	iblockstate1 = iblockstate1.withProperty(BlockCrystalLadder.FACING, enumfacing);
                    }
                }
            }
            
            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
	
}
