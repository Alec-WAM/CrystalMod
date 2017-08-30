package alec_wam.CrystalMod.tiles.cases;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCase extends EnumBlock<BlockCase.EnumCaseType> implements ITileEntityProvider {

	public static final PropertyEnum<EnumCaseType> TYPE = PropertyEnum.<EnumCaseType>create("type", EnumCaseType.class);
	
	public BlockCase() {
		super(Material.WOOD, TYPE, EnumCaseType.class);
		this.setHardness(1.5F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), EnumCaseType.NOTE.getMeta(), new ModelResourceLocation(this.getRegistryName(), prop.getName()+"="+EnumCaseType.NOTE.getName()));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), EnumCaseType.PISTON.getMeta(), new ModelResourceLocation(this.getRegistryName(), prop.getName()+"="+EnumCaseType.PISTON.getName()+"_inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), EnumCaseType.STICKY_PISTON.getMeta(), new ModelResourceLocation(this.getRegistryName(), prop.getName()+"="+EnumCaseType.STICKY_PISTON.getName()+"_inventory"));
	}
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		EnumCaseType type = state.getValue(TYPE);
		return (type == EnumCaseType.NOTE) ? SoundType.WOOD : SoundType.STONE;
    }
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			//BlockCrystalGlass block = (BlockCrystalGlass)state.getBlock();
			EnumCaseType type = state.getValue(TYPE);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(TYPE.getName());
			builder.append("=");
			builder.append(type);
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		//if(player.isSneaking()) return false;
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityCaseBase){
			TileEntityCaseBase tileCase = (TileEntityCaseBase)tile;
			
			if(tileCase instanceof TileEntityCasePiston && player.isSneaking()){
				TileEntityCasePiston piston = (TileEntityCasePiston)tileCase;
				String extend = ""+piston.opening;
				String progress = ""+piston.progress[facing.getIndex()]+" / "+piston.lastProgress[facing.getIndex()];
				ChatUtil.sendChat(player, extend, progress);
				return true;
			}
			
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
				tileCase.onOpened();
			}
			return true;
		}
        return false;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
		//super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile != null && tile instanceof TileEntityCasePiston)
        {
        	TileEntityCasePiston piston = (TileEntityCasePiston)tile;
        	
        	for(EnumFacing enumfacing : piston.validFaces){
        		BlockPos pos2 = pos.offset(enumfacing);
        		float progress = piston.progress[enumfacing.getIndex()];
        		if (progress >= 1.0D)
        		{
        			IBlockState iblockstate;
        			iblockstate = Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockDirectional.FACING, enumfacing).withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(piston.isExtending[enumfacing.getIndex()] != 1.0F - progress < 0.25F));
        			float f = piston.getExtendedProgress(progress, enumfacing);
        			double d0 = enumfacing.getFrontOffsetX() * f;
        			double d1 = enumfacing.getFrontOffsetY() * f;
        			double d2 = enumfacing.getFrontOffsetZ() * f;
        			iblockstate.addCollisionBoxToList(worldIn, pos2, entityBox.offset(-d0, -d1, -d2), collidingBoxes, entityIn, true);
        		}
        	}
        } 
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
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
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, getMetaFromState(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof TileEntityCaseBase){
			TileEntityCaseBase caseTile = (TileEntityCaseBase)tileEntity;
			caseTile.writeToStack(stack);
		}
		return stack;
	}

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState blockState)
    {
        super.onBlockAdded(world, pos, blockState);
        world.notifyNeighborsOfStateChange(pos, this, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase entityliving, ItemStack itemStack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityCaseBase)
        {
        	TileEntityCaseBase caseTile = (TileEntityCaseBase) te;
            if(itemStack.hasTagCompound()){
            	caseTile.readFromStack(itemStack);
            }
            BlockUtil.markBlockForUpdate(world, pos);
        }
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		EnumCaseType type = EnumCaseType.values()[meta];
		if(type == EnumCaseType.NOTE){
			return new TileEntityCaseNoteblock();
		}
		if(type == EnumCaseType.PISTON){
			return new TileEntityCasePiston();
		}
		if(type == EnumCaseType.STICKY_PISTON){
			return new TileEntityCasePiston();
		}
		return null;
	}
	
	public static enum EnumCaseType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta {
		NOTE, PISTON, STICKY_PISTON;

		final int meta;
		
		EnumCaseType(){
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
	
}
