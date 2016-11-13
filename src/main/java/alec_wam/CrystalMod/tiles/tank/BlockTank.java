package alec_wam.CrystalMod.tiles.tank;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;

public class BlockTank extends EnumBlock<BlockTank.TankType> implements ITileEntityProvider, ICustomModel {

	public static final PropertyEnum<TankType> TYPE = PropertyEnum.<TankType>create("type", TankType.class);
	public static int[] tankCaps = new int[]{8, 16, 32, 64, 128, 1};
	public BlockTank(){
		super(Material.GLASS, TYPE, TankType.class);
		setSoundType(SoundType.GLASS);
		setHardness(2f).setResistance(10F);
		setLightOpacity(0);
		setCreativeTab(CrystalMod.tabBlocks);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, TankType.BLUE));
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelResourceLocation inv = new ModelResourceLocation(getRegistryName(), "inventory");
		ClientProxy.registerCustomModel(inv, ModelTank.INSTANCE);
		for(TankType type : TankType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
			ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "type="+type.getName()), ModelTank.INSTANCE);
		}
    }
	
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
        return (IBlockState)new FakeTankState(state, world, pos, (tile !=null && tile instanceof TileEntityTank) ? (TileEntityTank)tile : null);
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (willHarvest) {
			return true;
	    }
	    return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
	    super.harvestBlock(worldIn, player, pos, state, te, stack);
	    worldIn.setBlockToAir(pos);
	}
	
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        ItemStack tank = new ItemStack(ModBlocks.crystalTank, 1, state.getValue(TYPE).getMeta());
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityTank){
        	TileEntityTank tiletank = (TileEntityTank) tile;
        	ItemBlockTank.saveTank(tank, tiletank.tank);
        }
        ret.add(tank);
        return ret;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof TileEntityTank){
        	TileEntityTank tank = (TileEntityTank) tile;
        	FluidTank tankSaved = null;
        	if(stack.hasTagCompound()){
        		NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().copy();
        		tankSaved = ItemBlockTank.loadTank(nbt);
        	}
        	if(tankSaved !=null){
        		tank.tank.setFluid(tankSaved.getFluid());
        		BlockUtil.markBlockForUpdate(world, pos);
        	}
        	if(tank.creative){
        		BlockUtil.markBlockForUpdate(world, pos);
        	}
        }
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, ItemStack held, EnumFacing side, float par7, float par8,
            float par9) {

        ItemStack current = held;
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank) tile;
        	if (current != null) {
            	if(ToolUtil.isToolEquipped(entityplayer, hand)){
            		return ToolUtil.breakBlockWithTool(this, world, pos, entityplayer, hand);
            	}
                if(FluidUtil.interactWithFluidHandler(current, FluidUtil.getFluidHandler(world, pos, side), entityplayer)){
                	return true;
                }
        	}
            
            if(!world.isRemote){
        		if(tank.tank !=null){
        			FluidStack fluid = tank.tank.getFluid();
        			if(fluid == null){
        				ChatUtil.sendNoSpam(entityplayer, "Fluid: (Empty) 0 mB / "+tank.tank.getCapacity()+" mB");
        			}
        			else ChatUtil.sendNoSpam(entityplayer, "Fluid: ("+fluid.getLocalizedName()+") "+fluid.amount+" mB / "+tank.tank.getCapacity()+" mB");
        		}
    		}
    		return true;
        } 
        return false;
    }
	
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank) tile;
            return tank.getFluidLightLevel();
        }

        return super.getLightValue(state, world, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank) tile;
            return tank.getComparatorInputOverride();
        }

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
    	return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
	public static enum TankType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK, PURE, CREATIVE;

		final int meta;
		
		TankType(){
			meta = ordinal();
		}
		
		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}
		
	}
	
	public TileEntity createNewTileEntity(World world, int meta)
    {
		if(meta == 5){
			return new TileEntityTank(tankCaps[5], true);
		}
		if(meta < tankCaps.length){
			return new TileEntityTank(tankCaps[meta]);
		}
        return null;
    }
}
