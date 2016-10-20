package alec_wam.CrystalMod.tiles.machine.power.battery;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.tool.ToolUtil;

public class BlockBattery extends BlockContainer implements ICustomModel {
	
	public static final PropertyEnum<BatteryType> TYPE = PropertyEnum.<BatteryType>create("type", BatteryType.class);
	public static enum BatteryType implements IStringSerializable, IEnumMeta {
			BLUE("blue"),
			RED("red"),
			GREEN("green"),
			DARK("dark"),
			PURE("pure"),
			CREATIVE("creative");

			private final String unlocalizedName;
			private final int meta;

			BatteryType(String name) {
		      meta = ordinal();
		      unlocalizedName = name;
		    }

		    @Override
		    public String getName() {
		      return unlocalizedName;
		    }

		    @Override
		    public int getMeta() {
		      return meta;
		    }
	}

	public BlockBattery() {
		super(Material.IRON);
		setHardness(20.0F);
	    setResistance(120.0F);
	    setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new BatteryBlockStateMapper());
		for(BatteryType type : BatteryType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for(BatteryType type : BatteryType.values()) {
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
	}
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state;
    }
	
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return (IBlockState)new FakeBatteryState(state, world, pos, (tile !=null && tile instanceof TileEntityBattery) ? (TileEntityBattery)tile : null);
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return ((BatteryType) state.getValue(TYPE)).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}

	public static BatteryType fromMeta(int meta) {
	    if(meta < 0 || meta >= BatteryType.values().length) {
	      meta = 0;
	    }

	    return BatteryType.values()[meta];
	}

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { TYPE });
    }
	
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	BatteryType type = fromMeta(stack.getMetadata());
    	int energy = 0;
    	if(stack.hasTagCompound()){
	    	NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
	    	if(nbt.hasKey("Energy")){
	    		energy = nbt.getInteger("Energy");
	    	}
	    	if(nbt.hasKey("BatteryData")){
	    		NBTTagCompound nbtBat = nbt.getCompoundTag("BatteryData");
	    		if(nbtBat.hasKey("Energy"))energy = nbtBat.getInteger("Energy");
	    	}
    	}

    	if(type == BatteryType.CREATIVE){
    		tooltip.add("Energy: Infinite "+Lang.localize("power.cu"));
    		tooltip.add("Max Output: "+TileEntityBattery.MAX_SEND[type.getMeta()]+" "+Lang.localize("power.cu")+"/t");
    	} else {
	    	tooltip.add("Energy: "+energy+" / "+TileEntityBattery.MAX_ENERGY[type.getMeta()] + " "+Lang.localize("power.cu"));
	    	tooltip.add("Max Input: "+TileEntityBattery.MAX_RECEIVE[type.getMeta()]+" "+Lang.localize("power.cu")+"/t");
	    	tooltip.add("Max Output: "+TileEntityBattery.MAX_SEND[type.getMeta()]+" "+Lang.localize("power.cu")+"/t");
    	}
    }
    
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityBattery)) {
        	TileEntityBattery battery = (TileEntityBattery)tile;
        	
    		if(player.isSneaking()){
    			
    			if(ToolUtil.isToolEquipped(player, hand)){
    				if(!world.isRemote){
    					ItemStack stack = new ItemStack(ModBlocks.battery, 1, getMetaFromState(state));
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
    				IOType newType = type.getNext();
    				
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
    				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    				//String scale = ""+battery.getScaledEnergyStored(8)+" isRemote = "+world.isRemote;
    				/*String power = "Power: "+battery.getCEnergyStored(side)+" / "+battery.getMaxCEnergyStored(side);
    				String facing = ("Facing: "+battery.facing);
    				ChatUtil.sendNoSpam(player, power, facing);*/
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
		return new TileEntityBattery(meta);
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

    public static class BatteryBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
		}
	}
}
