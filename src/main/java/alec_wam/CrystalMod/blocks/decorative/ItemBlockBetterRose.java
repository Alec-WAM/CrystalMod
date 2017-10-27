package alec_wam.CrystalMod.blocks.decorative;

import java.util.Locale;

import alec_wam.CrystalMod.blocks.ItemBlockMeta;
import alec_wam.CrystalMod.blocks.decorative.BlockBetterRoses.RoseType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockBetterRose extends ItemBlockMeta {

	public ItemBlockBetterRose(Block block) {
		super(block);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(mappingProperty == null) return super.getUnlocalizedName(stack);

		String name = RoseType.values()[stack.getMetadata()].toString().toLowerCase(Locale.US);
	    return super.getUnlocalizedName() + "." + name;
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if(world.isAirBlock(pos.up()) || world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up())){
        	BlockBetterRoses.placeFullPlant(world, pos, RoseType.values()[stack.getMetadata()]);


            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == this.block)
            {
                setTileEntityNBT(world, player, pos, stack);
                this.block.onBlockPlacedBy(world, pos, state, player, stack);
            }

            return true;
        }
        return false;
    }

}
