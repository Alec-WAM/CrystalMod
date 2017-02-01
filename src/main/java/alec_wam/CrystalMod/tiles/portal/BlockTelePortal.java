package alec_wam.CrystalMod.tiles.portal;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CrateType;
import alec_wam.CrystalMod.tiles.crate.BlockCrate.CustomBlockStateMapper;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.tiles.machine.elevator.ItemMiscCard.CardType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Vector3d;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTelePortal extends BlockContainer implements ICustomModel {

	public BlockTelePortal() {
		super(Material.IRON);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.METAL);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileTelePortal();
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void initModel(){
    	ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		String nameOverride = getRegistryName().getResourcePath();
		ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(baseLocation, "inventory"));
    }
	
	@SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState();
	}
    
	@Override
	public int getMetaFromState(IBlockState state){
		return 0;
	}
	
    @Override
    protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, BlockStateFacing.facingProperty);
    }
    
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(worldIn.isRemote)return true;
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileTelePortal)) return false;
		ItemStack stack = player.getHeldItem(hand);
		if(ItemStackTools.isValid(stack)){
			if(stack.getItem() == ModItems.miscCard && stack.getMetadata() == CardType.TELEPORT_PORTAL.getMetadata()){
				if(ItemNBTHelper.verifyExistance(stack, "PortalPos")){
					BlockPos portalPos = NBTUtil.getPosFromTag(ItemNBTHelper.getCompound(stack).getCompoundTag("PortalPos"));
					int dim = ItemNBTHelper.getInteger(stack, "PortalDim", 0);
					TileTelePortal portal = (TileTelePortal)tile;
					portal.otherPortalPos = portalPos;
					portal.otherPortalDim = dim;
					ModLogger.info("Set link to "+portalPos);
					return true;
				}
			}
		}
		return false;
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
    	Vector3d facingVec = new Vector3d(new BlockPos(entityIn));
		facingVec.sub(new Vector3d(pos));
		EnumFacing facing = EnumFacing.getFacingFromVector((float)facingVec.x, (float)facingVec.y, (float)facingVec.z);
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileTelePortal){
			TileTelePortal portal = (TileTelePortal)tile;
			if(facing == portal.facing){
				portal.travel(entityIn);
			}
		}
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof IFacingTile){
        	EnumFacing face = BlockMachine.getFacingFromEntity(pos, placer, true);
        	((IFacingTile)tile).setFacing(face.getIndex());
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }
    
    @Override
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
    
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	return EnumFacing.VALUES;
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
    
    public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
