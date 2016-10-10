package com.alec_wam.CrystalMod.tiles.machine.power.battery;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.network.CrystalModNetwork;
import com.alec_wam.CrystalMod.network.packets.PacketTileMessage;
import com.alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import com.alec_wam.CrystalMod.util.ChatUtil;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.BlockUtil;
import com.alec_wam.CrystalMod.util.tool.ToolUtil;

public class BlockBattery extends BlockContainer {
	
	/*public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 8);
	public static final PropertyEnum<IOType> UP_IO = PropertyEnum.<IOType>create("up_io", IOType.class);
	public static final PropertyEnum<IOType> DOWN_IO = PropertyEnum.<IOType>create("down_io", IOType.class);
	public static final PropertyEnum<IOType> NORTH_IO = PropertyEnum.<IOType>create("north_io", IOType.class);
	public static final PropertyEnum<IOType> SOUTH_IO = PropertyEnum.<IOType>create("south_io", IOType.class);
	public static final PropertyEnum<IOType> WEST_IO = PropertyEnum.<IOType>create("west_io", IOType.class);
	public static final PropertyEnum<IOType> EAST_IO = PropertyEnum.<IOType>create("east_io", IOType.class);*/


	public BlockBattery() {
		super(Material.IRON);
		setHardness(20.0F);
	    setResistance(120.0F);
	    setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        /*TileEntity te = world.getTileEntity(pos);
        int power = 0;
        EnumFacing face = EnumFacing.NORTH;
        IOType u = IOType.IN;
        IOType d = IOType.IN;
        IOType n = IOType.IN;
        IOType s = IOType.IN;
        IOType e = IOType.IN;
        IOType w = IOType.IN;
        if (te !=null && te instanceof TileEntityBattery) {
        	TileEntityBattery battery = (TileEntityBattery)te;
        	face = EnumFacing.getFront(battery.facing);
        	power = Math.min(8, battery.getScaledEnergyStored(9));
        	u = battery.getIO(EnumFacing.UP);
        	d = battery.getIO(EnumFacing.DOWN);
        	n = battery.getIO(EnumFacing.NORTH);
        	s = battery.getIO(EnumFacing.SOUTH);
        	e = battery.getIO(EnumFacing.EAST);
        	w = battery.getIO(EnumFacing.WEST);
        }*/
        return state;//.withProperty(FACING, face).withProperty(POWER, power).withProperty(UP_IO, u).withProperty(DOWN_IO, d).withProperty(NORTH_IO, n).withProperty(SOUTH_IO, s).withProperty(WEST_IO, w).withProperty(EAST_IO, e);
    }
	
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return (IBlockState)new FakeBatteryState(state, world, pos, (tile !=null && tile instanceof TileEntityBattery) ? (TileEntityBattery)tile : null);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }
	
	@Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this);
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityBattery)) {
        	TileEntityBattery battery = (TileEntityBattery)tile;
        	
    		if(player.isSneaking()){
    			
    			if(ToolUtil.isToolEquipped(player, hand)){
    				if(!world.isRemote){
    					ItemStack stack = new ItemStack(ModBlocks.battery);
    					NBTTagCompound nbt = new NBTTagCompound();
    					battery.writeCustomNBT(nbt);
    					ItemNBTHelper.getCompound(stack).setTag("BatteryData", nbt);
    					ItemUtil.spawnItemInWorldWithoutMotion(world, stack, pos);
    				}
    				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					return true;
				}
    			
    			if(!world.isRemote){
    				
    				EnumFacing fixedDir = battery.fixFace(side);
    				
    				IOType type = battery.getIO(fixedDir);
    				IOType newType = null;
    				if(type == IOType.BLOCKED){
    					newType = IOType.IN;
    				}
    				if(type == IOType.IN){
    					newType = IOType.OUT;
    				}
    				if(type == IOType.OUT){
    					newType = IOType.BLOCKED;
    				}
    				if(newType !=null){
	    				battery.setIO(fixedDir, newType);
	    				NBTTagCompound nbt = new NBTTagCompound();
	    				nbt.setString("IOType", newType.getName());
	    				CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "IO."+fixedDir.name().toUpperCase(), nbt), tile);
	    				BlockUtil.markBlockForUpdate(world, pos);
    				}
    			}
    			return true;
    			
    		}else{
    			if(!world.isRemote){
    				//String scale = ""+battery.getScaledEnergyStored(8)+" isRemote = "+world.isRemote;
    				ChatUtil.sendNoSpam(player, "Power: "+battery.getCEnergyStored(side)+" / "+battery.getMaxCEnergyStored(side));
    			}
    			return true;
    		}
        }
    	return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing face = getFacingFromEntity(pos, placer);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityBattery){
        	if(ItemNBTHelper.verifyExistance(stack, "BatteryData"))((TileEntityBattery)tile).readCustomNBT(stack.getTagCompound().getCompoundTag("BatteryData"));
        	((TileEntityBattery)tile).facing = face.getIndex();
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }
    
    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entityIn) {
    	 if (MathHelper.abs((float) entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float) entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
             double d0 = entityIn.posY + entityIn.getEyeHeight();

             if (d0 - clickedBlock.getY() > 2.0D) {
                 return EnumFacing.UP;
             }

             if (clickedBlock.getY() - d0 > 0.0D) {
                 return EnumFacing.DOWN;
             }
         }
        return entityIn.getHorizontalFacing().getOpposite();
    }
	
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBattery();
	}
	
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof TileEntityBattery){
        	TileEntityBattery bat = (TileEntityBattery)te;
        	int next = bat.facing;
        	next++;
        	next%=6;
        	bat.facing = next;
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }

    /**
     * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
     * Note, this is up to the block to decide. It may not be accurate or representative.
     * @param world The world
     * @param pos Block position in world
     * @return An array of valid axes to rotate around, or null for none or unknown
     */
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
        return EnumFacing.VALUES;
    }

}
