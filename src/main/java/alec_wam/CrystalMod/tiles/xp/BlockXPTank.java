package alec_wam.CrystalMod.tiles.xp;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.machine.INBTDrop;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockXPTank extends BlockContainer implements ICustomModel {

	public static final PropertyBool ENDER = PropertyBool.create("ender");
	
	public BlockXPTank() {
		super(Material.IRON);
		setSoundType(SoundType.GLASS);
		setHardness(2f).setResistance(10F);
		setLightOpacity(0);
		setCreativeTab(CrystalMod.tabBlocks);
		this.setDefaultState(getDefaultState().withProperty(ENDER, false));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel(){
		ModelResourceLocation normal = new ModelResourceLocation(getRegistryName(), "ender=false");
		ModelResourceLocation ender = new ModelResourceLocation(getRegistryName(), "ender=true");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, normal);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, ender);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new RenderTileEntityXPTank());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }
	
	@Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{ENDER});
    }
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(ENDER) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(ENDER, meta == 1);
	}
	
	@Override
	public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
		if(ItemStackTools.isValid(stack)){
			int xp = TileEntityXPTank.getXPFromStack(stack);
			if(xp >= 1)tooltip.add(Lang.localizeFormat("xptank.tooltip.level", xp));
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
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing side, float par7, float par8, float par9) {

        ItemStack current = entityplayer.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityXPTank) {
        	if (ItemStackTools.isValid(current)) {
            	if(ToolUtil.isToolEquipped(entityplayer, hand)){
            		return ToolUtil.breakBlockWithTool(this, world, pos, entityplayer, hand);
            	}            
            	if(ItemUtil.canCombine(current, ModFluids.bucketList.get(ModFluids.fluidEnder))){
            		if(state.getValue(ENDER) == false){        			
            			if(!entityplayer.capabilities.isCreativeMode)entityplayer.setHeldItem(hand, ItemUtil.consumeItem(current));
        				NBTTagCompound nbt = new NBTTagCompound();
        				((TileEntityXPTank) tile).writeCustomNBT(nbt);
        				world.setBlockState(pos, state.withProperty(ENDER, true), 3);
        				((TileEntityXPTank)world.getTileEntity(pos)).readCustomNBT(nbt);
        				return true;
        			}
        		}
            	IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(current);
            	if (containerFluidHandler != null)
            	{
            		
            		if(FluidUtil.interactWithFluidHandler(entityplayer, hand, world, pos, side)){
            			return true;
            		}
            	}
        	}
        	
            if(!world.isRemote){
        		entityplayer.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
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
    
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	    if (willHarvest) {
	      return true;
	    }
	    return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te,
	      @Nullable ItemStack stack) {
	    super.harvestBlock(worldIn, player, pos, state, te, stack);
	    worldIn.setBlockToAir(pos);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		if(world == null || pos == null)return super.getPickBlock(state, target, world, pos, player);
		return getNBTDrop(world, pos, world.getTileEntity(pos));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	    if (world == null || pos == null) {
	      return super.getDrops(world, pos, state, fortune);
	    }
    	return Lists.newArrayList(getNBTDrop(world, pos, world.getTileEntity(pos)));
	}
	
	public static final String TILE_NBT_STACK = "TileData";
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof INBTDrop){
			INBTDrop machine = (INBTDrop)tileEntity;
			NBTTagCompound nbt = new NBTTagCompound();
			machine.writeToStack(nbt);
			ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
		}
		return stack;
	}

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
        TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
        	if(tile !=null && tile instanceof INBTDrop){
        		INBTDrop machine = (INBTDrop)tile;
        		machine.readFromStack(ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK));
        		update = true;
        	}
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityXPTank();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam)
    {
    	super.eventReceived(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
}
