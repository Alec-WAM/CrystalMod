package alec_wam.CrystalMod.tiles.machine.power.battery;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		setSoundType(SoundType.METAL);
		setHardness(20.0F);
	    setResistance(120.0F);
	    setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomStateMapper(this, new BatteryBlockStateMapper());
		ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
		ClientProxy.registerCustomModel(inv, ModelBattery.INSTANCE);
		ClientProxy.registerCustomModel(new ModelResourceLocation(this.getRegistryName(), "normal"), ModelBattery.INSTANCE);
		for(BatteryType type : BatteryType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(BatteryType type : BatteryType.values()) {
			ItemStack stack = new ItemStack(this, 1, type.getMeta());
			if(type == BatteryType.CREATIVE){
				NBTTagCompound stackNBT = ItemNBTHelper.getCompound(stack);
				NBTTagCompound batteryNBT = new NBTTagCompound();
				for(EnumFacing face : EnumFacing.VALUES){
					batteryNBT.setByte("io."+face.name().toLowerCase(), (byte)IOType.OUT.ordinal());
				}
				stackNBT.setTag("BatteryData", batteryNBT);
			}
			list.add(stack);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos);
    }
	
	@Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return new FakeBatteryState(state, world, pos, (tile !=null && tile instanceof TileEntityBattery) ? (TileEntityBattery)tile : null);
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return state.getValue(TYPE).getMeta();
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
	
    @Override
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
    		tooltip.add("Max Output: "+StringUtils.convertToCommas(TileEntityBattery.MAX_SEND[type.getMeta()])+" "+Lang.localize("power.cu")+"/t");
    	} else {
	    	tooltip.add("Energy: "+StringUtils.convertToCommas(energy)+" / "+StringUtils.convertToCommas(TileEntityBattery.MAX_ENERGY[type.getMeta()]) + " "+Lang.localize("power.cu"));
	    	tooltip.add("Max Input: "+StringUtils.convertToCommas(TileEntityBattery.MAX_RECEIVE[type.getMeta()])+" "+Lang.localize("power.cu")+"/t");
	    	tooltip.add("Max Output: "+StringUtils.convertToCommas(TileEntityBattery.MAX_SEND[type.getMeta()])+" "+Lang.localize("power.cu")+"/t");
    	}
    }
    
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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
    					ItemUtil.spawnItemInWorldWithRandomMotion(world, stack, pos);
    				}
    				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					return true;
				}
    		}else{
    			if(!world.isRemote){
    				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
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
	
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBattery(meta);
	}
	
	//TODO Look into rotation issues
	@Override
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
	@Override
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
