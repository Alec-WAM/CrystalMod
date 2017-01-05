package alec_wam.CrystalMod.blocks.crops.material;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.common.util.ChatUtil;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.crop.CropRecipe;
import alec_wam.CrystalMod.api.crop.SpecialCropRecipe;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMaterialCrop extends BlockContainer {

	public static PropertyBool GROWN = PropertyBool.create("grown");
	
	public BlockMaterialCrop() {
		super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(null);
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileMaterialCrop){
			if(!((TileMaterialCrop)tile).isGrown()){
				return 5.0F;
			}
		}
		return 0.0f;
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing direction, float p_180639_6_, float p_180639_7_, float p_180639_8_)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileMaterialCrop){
			TileMaterialCrop crop = (TileMaterialCrop)tile;
			if(player.isSneaking()){
				ChatUtil.sendNoSpam(player, ""+crop.getTimeRemaining()+"s", ""+crop.getCropYield());
				return true;
			}
			if(!world.isRemote){
				if(ItemStackTools.isValid(stack)){
					IMaterialCrop newCrop = null;
					if(stack.getItem() instanceof ItemMaterialSeed){
						IMaterialCrop seedCrop = ItemMaterialSeed.getCrop(stack);
						if(seedCrop !=null){
							for(CropRecipe recipe : CrystalModAPI.getRecipes()){
								if(recipe.matches(crop.getCrop(), seedCrop)){
									newCrop = recipe.getOutput();
									break;
								}
							}
						}
					} else {
						for(SpecialCropRecipe recipe : CrystalModAPI.getSpecialRecipes()){
							if(recipe.matches(crop.getCrop(), stack)){
								newCrop = recipe.getOutput();
								break;
							}
						}
					}
					if(newCrop !=null){
						if(crop.isCombo()){
							ChatUtil.sendNoSpam(player, "You can not combine more than one crop!");
							return false;
						}
						crop.setCrop(newCrop);
						crop.calculateYield();
						crop.setGrowthTime(0);
						crop.setCombo(true);
						BlockUtil.markBlockForUpdate(world, pos);
						if(!player.capabilities.isCreativeMode){
							ItemStackTools.incStackSize(stack, -1);
						}
						return true;
					}
				}

				if(crop.isGrown()){
					if(ItemStackTools.isEmpty(stack)){
						ItemUtil.spawnItemsInWorldWithoutMotion(world, crop.getDrops(false, EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, player)), pos);
						crop.setGrowthTime(0);
						crop.setCombo(false);
						crop.calculateYield();
						BlockUtil.markBlockForUpdate(world, pos);
						return true;
					}
				}
			}
		}
		return false;
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
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileMaterialCrop){
			return ItemMaterialSeed.getSeed(((TileMaterialCrop)tile).getCrop());
		}
		return ItemStackTools.getEmptyStack();
	}
	
	
	@Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> items = Lists.newArrayList();
        TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileMaterialCrop){
			items.addAll(((TileMaterialCrop)tile).getDrops(true, fortune));
		}
        return items;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMaterialCrop();
	}
	
	@Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{GROWN});
    }
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(GROWN) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(GROWN, Boolean.valueOf(meta == 1));
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
		boolean grown = false;
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileMaterialCrop){
			grown = ((TileMaterialCrop)tile).isGrown();
		}
        return state.withProperty(GROWN, grown);
    }

}
