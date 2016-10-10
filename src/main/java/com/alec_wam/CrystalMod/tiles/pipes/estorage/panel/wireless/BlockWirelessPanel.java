package com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.wireless;


import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.util.ChatUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockWirelessPanel extends BlockContainer {
	
	public BlockWirelessPanel() {
		super(Material.IRON);
		this.setHardness(2f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
    	
		int height = getTowerHeight(world, pos);
		if(height > 0){
			String strH = "Tower Height is "+height;
			BlockPos towerTop = getPosAtY(pos, pos.getY()+1+height);
			IBlockState topState = world.getBlockState(towerTop);
			if(topState.getBlock() == Blocks.REDSTONE_BLOCK){
				if(!world.isRemote){
					ChatUtil.sendNoSpam(player, strH, "Top: "+towerTop);
				}
			}else{
				if(!world.isRemote){
					ChatUtil.sendNoSpam(player, strH, "No Redstone Block at top");
				}
			}
			return true;
		}
		return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWirelessPanel();
	}
	
	private BlockPos getPosAtY(BlockPos p, int y) {
        return new BlockPos(p.getX(), y, p.getZ());
    }
	
	public int getTowerHeight(World world, final BlockPos start){
		Block search = Blocks.IRON_BARS;
		
		int count = 0;
		
		for(int y = (start.getY()+1); y < world.getHeight(); y++){
			BlockPos pos = getPosAtY(start, y);
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == search){
				count++;
			}else{
				break;
			}
		}
		return count;
	}

}

