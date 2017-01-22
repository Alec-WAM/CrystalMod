package alec_wam.CrystalMod.tiles.fusion;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BlockPedistal extends BlockContainer implements ICustomModel {

	public BlockPedistal() {
		super(Material.ROCK);
		this.setHardness(1f).setResistance(10F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setSoundType(SoundType.STONE);
	}
	
	public BlockPedistal(Material material) {
		super(material);
		this.setHardness(1f).setResistance(10F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new PedistalBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePedistal();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateFacing(this);
    }
    
	@Override
    public int getMetaFromState(IBlockState state){
    	return 0;
    }
    
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		int facing = ((IFacingTile)te).getFacing();
        		face = EnumFacing.getFront(facing);
        	}
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (worldIn.isRemote) {
            return true;
        }
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof IPedistal){
			IPedistal pedistal = (IPedistal)tile;
			boolean locked = false;
			if(tile instanceof IFusionPedistal){
				locked = ((IFusionPedistal)tile).isLocked();
			}
			if(locked)return false;
			ItemStack heldItem = playerIn.getHeldItem(hand);
			if(ItemStackTools.isValid(heldItem)){
				ItemStack insertStack = heldItem;
				if(ItemStackTools.isEmpty(pedistal.getStack())){
					insertStack = ItemUtil.copy(heldItem, 1);
				}
				IItemHandler handler = ItemUtil.getExternalItemHandler(worldIn, pos, EnumFacing.UP);
				if(handler == null)return false;
				int insert = ItemUtil.doInsertItem(handler, insertStack, EnumFacing.UP);
				if(insert > 0){
					ItemStackTools.incStackSize(heldItem, -insert);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 0.95f);
					return true;
				}
			} 
			ItemStack pedistalStack = pedistal.getStack();
			if(ItemStackTools.isValid(pedistalStack)){
				ItemStack drop = ItemStackTools.safeCopy(pedistalStack);
				pedistal.setStack(ItemStackTools.getEmptyStack());
				boolean dropItem = true;
				if(ItemStackTools.isEmpty(heldItem) && playerIn.isSneaking()){
					playerIn.setHeldItem(hand, drop);
					dropItem = false;
				}
				if(dropItem){
					playerIn.inventory.addItemStackToInventory(drop);
					if(!ItemStackTools.isEmpty(drop)){
						ItemUtil.spawnItemInWorldWithRandomMotion(worldIn, drop, pos);
					}
				}
				worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 0.85f);
				return true;
			}
			return false;
		}
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
        TileEntity tile = world.getTileEntity(pos);
        /*boolean update = false;
        if(tile !=null && tile instanceof IFacingTile){
        	
        	EnumFacing face = BlockPistonBase.getFacingFromEntity(pos, placer);
        	((IFacingTile)tile).setFacing(face.getIndex());
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);*/
    }
    
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	int next = tile.getFacing();
        	next++;
        	next%=6;
        	tile.setFacing(next);
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	TileEntity tile = world.getTileEntity(pos);
    	if(tile !=null && tile instanceof IFacingTile){
    		return EnumFacing.VALUES;
    	}
        return new EnumFacing[0];
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile !=null && tile instanceof IInventory)ItemUtil.dropContent(0, (IInventory)tile, worldIn, pos);
        super.breakBlock(worldIn, pos, state);
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
	
	public static class PedistalBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockPedistal block = (BlockPedistal)state.getBlock();
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = block.getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
