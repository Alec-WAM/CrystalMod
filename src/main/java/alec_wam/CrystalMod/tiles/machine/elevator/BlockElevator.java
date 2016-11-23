package alec_wam.CrystalMod.tiles.machine.elevator;

import java.util.Map.Entry;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;

public class BlockElevator extends BlockContainer {

	public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockElevator() {
		super(Material.IRON);
		setHardness(2.0f).setResistance(15F);
        super.setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityElevator();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityElevator) {
        	TileEntityElevator ele = (TileEntityElevator) te;
        	
        		if(ToolUtil.isToolEquipped(playerIn, hand) && playerIn.isSneaking()){
        			TileEntityElevator con = null;
					BlockPos conPos = ele.findBottomElevator();
					if(conPos !=null){
						TileEntity tile2 = worldIn.getTileEntity(conPos);
						if(tile2 !=null && tile2 instanceof TileEntityElevator){
							con = (TileEntityElevator)tile2;
						}
					}
					if(con !=null){
						con.updateFloors(false);
					}
        			return true;
        		}
        		if(!worldIn.isRemote){
        		if(stack !=null && stack.getItem() == Items.STICK){
        			if(stack.hasDisplayName()){
        				String input = stack.getDisplayName();
        				int floor = -1;
        				try{
        					floor = Integer.parseInt(input);
        				}catch(Exception e){}
        				
        				if(floor > -1){
        					int lvl = -1;
        					String error = "";
        					TileEntityElevator con = null;
        					BlockPos conPos = ele.findBottomElevator();
							if(conPos !=null){
								TileEntity tile2 = worldIn.getTileEntity(conPos);
								if(tile2 !=null && tile2 instanceof TileEntityElevator){
									con = (TileEntityElevator)tile2;
								}
							}
							if(con !=null){
								if(floor < con.floors.size()){
		    						BlockPos fPos = con.floors.get(floor);
		    						if(fPos !=null){
	    								lvl = fPos.getY()-conPos.getY();
	    							}else{
	        							error = "CONTROLLER POS";
	        						}
        						}else{
        							error = "FLOOR POS";
        						}
        					}else{
    							error = "NULL CON";
    						}
        					if(lvl > -1){
        						if(ele.getCurrentLevel() !=lvl){
	        						ele.toLevel(lvl);
	        						ChatUtil.sendChat(playerIn, "Level "+lvl);
        						}
        					}else{
        						ChatUtil.sendChat(playerIn, "Error: "+error);
        					}
        					return true;
        				}
        			}
        		}
        		for(Entry<Integer, BlockPos> entry : ele.floors.entrySet()){
        			int index = entry.getKey();
        			BlockPos fPos = entry.getValue();
        			if(fPos !=null){
        				ChatUtil.sendChat(playerIn, "Floor "+index+": "+fPos.toString());
        			}
        		}
        	}
        	return false;
        }
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        world.setBlockState(pos, state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite()), 2);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevator) {
            TileEntityElevator TileEntityElevator = (TileEntityElevator) te;
            TileEntityElevator.clearCaches();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevator) {
            TileEntityElevator TileEntityElevator = (TileEntityElevator) te;
            TileEntityElevator.clearCaches();
        }
        super.breakBlock(world, pos, state);
    }
	
	@Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world, pos, axis);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING_HORIZ, getFacingHoriz(meta));
    }
	
	public static EnumFacing getFacingHoriz(int meta) {
        return EnumFacing.values()[meta+2];
    }
	
	public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING_HORIZ).getIndex()-2;
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ);
    }

}
