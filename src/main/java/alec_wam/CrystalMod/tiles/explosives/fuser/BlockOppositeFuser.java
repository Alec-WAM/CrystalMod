package alec_wam.CrystalMod.tiles.explosives.fuser;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

public class BlockOppositeFuser extends BlockContainer implements ICustomModel {

	public static final PropertyBool NORTH_SOUTH = PropertyBool.create("northsouth");
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	
	public BlockOppositeFuser() {
		super(Material.IRON);
		this.setHardness(1.5F);
		this.setCreativeTab(CrystalMod.tabBlocks);
		//TODO Render Crystals on sides
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH_SOUTH, ACTIVE);
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
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te !=null && te instanceof TileOppositeFuser){
			TileOppositeFuser fuser = (TileOppositeFuser)te;
			return state.withProperty(ACTIVE, false).withProperty(NORTH_SOUTH, false);
		}
		return state;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		TileEntity te = world.getTileEntity(pos);
		if(te !=null && te instanceof TileOppositeFuser){
			TileOppositeFuser fuser = (TileOppositeFuser)te;
			fuser.facingNS = (placer.getHorizontalFacing() == EnumFacing.NORTH || placer.getHorizontalFacing() == EnumFacing.SOUTH);
			BlockUtil.markBlockForUpdate(world, pos);
		}
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack held = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		if(te !=null && te instanceof TileOppositeFuser){
			TileOppositeFuser fuser = (TileOppositeFuser)te;
			if(ItemStackTools.isValid(held)){
				if(held.getItem() == ModItems.crystals){
					if(held.getMetadata() == CrystalType.PURE.getMetadata() && !fuser.hasPure){
						//PURE
						if(fuser.facingNS){
							if(facing == EnumFacing.SOUTH){
								fuser.hasPure = true;
								if(fuser.hasDark){
									fuser.triggerExplosion();
								}
								return true;
							}
						} else {
							if(facing == EnumFacing.EAST){
								fuser.hasPure = true;
								if(fuser.hasDark){
									fuser.triggerExplosion();
								}
								return true;
							}
						}
					}
					if(held.getMetadata() == CrystalType.DARK.getMetadata() && !fuser.hasDark){
						//DARK
						if(fuser.facingNS){
							if(facing == EnumFacing.NORTH){
								fuser.hasDark = true;
								if(fuser.hasPure){
									fuser.triggerExplosion();
								}
								return true;
							}
						} else {
							if(facing == EnumFacing.WEST){
								fuser.hasDark = true;
								if(fuser.hasPure){
									fuser.triggerExplosion();
								}
								return true;
							}
						}
					}
				}
			}
		}
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileOppositeFuser();
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
    	//ModelLoader.setCustomStateMapper(this, new CustomStateMapper());
    	ModBlocks.initBasicModel(this);
	}
	
	public static class CustomStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			boolean active = state.getValue(ACTIVE);
			boolean ns = state.getValue(NORTH_SOUTH);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			builder.append("active");
			builder.append("=");
			builder.append(""+active);
			builder.append(",");
			builder.append("northsouth");
			builder.append("=");
			builder.append(""+ns);
			
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
