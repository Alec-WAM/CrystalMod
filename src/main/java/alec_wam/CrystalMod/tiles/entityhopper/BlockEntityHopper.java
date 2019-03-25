package alec_wam.CrystalMod.tiles.entityhopper;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.entityhopper.TileEntityEntityHopper.FilterType;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityHopper extends BlockContainer {

	public BlockEntityHopper() {
		super(Material.IRON);
		setHardness(5.0F);
	    setResistance(60.0F);
	    setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityEntityHopper)) {
        	TileEntityEntityHopper hopper = (TileEntityEntityHopper)tile;
			if(ToolUtil.isToolEquipped(player, hand)){
				if(!world.isRemote){
					FilterType newFilter = player.isSneaking() ? hopper.getFilter().previous() : hopper.getFilter().next();
					ChatUtil.sendNoSpam(player, Lang.localize("entityhopper.filter."+newFilter.name().toLowerCase()));
					hopper.setFilter(newFilter);
				}
				return true;
			}
        }
    	return false;
    }
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity collidingEntity, boolean bool) {
	    /*TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityEntityHopper)){
			super.addCollisionBoxToList(state, worldIn, pos, entityBox,
					collidingBoxes, collidingEntity);
			return;
		}
		TileEntityEntityHopper hopper = (TileEntityEntityHopper)tile;
		if (collidingEntity !=null && hopper.passesFilter(collidingEntity)) {
			return;
		}*/
		super.addCollisionBoxToList(state, worldIn, pos, entityBox,
				collidingBoxes, collidingEntity, bool);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEntityHopper();
	}

}
