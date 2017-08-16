package alec_wam.CrystalMod.tiles.darkinfection;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockDarkInfection extends BlockContainer implements ICustomModel {

	public static PropertyInteger OPENING = PropertyInteger.create("opening", 0, 8);
	
	public BlockDarkInfection() {
		super(Material.ROCK);
		setHardness(0.3F);
		setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, OPENING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
    {
		return 0;
    }
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te !=null && te instanceof TileDarkInfection){
			TileDarkInfection infection = (TileDarkInfection)te;
			return state.withProperty(OPENING, infection.getOpeningPhase());
		}
		return state;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
		TileEntity te = world.getTileEntity(pos);
		if(te !=null && te instanceof TileDarkInfection){
			TileDarkInfection infection = (TileDarkInfection)te;
			if(infection.activated) return Lists.newArrayList();
		}
		return super.getDrops(world, pos, state, fortune);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(!player.isSneaking()){
			TileEntity te = world.getTileEntity(pos);
			if(te !=null && te instanceof TileDarkInfection){
				TileDarkInfection infection = (TileDarkInfection)te;
				infection.activated = true;
				world.playSound(null, pos, ModSounds.dark_infection_start, SoundCategory.BLOCKS, 1.0f, 1.0f);
				BlockUtil.markBlockForUpdate(world, pos);
			}
			
			return true;
		}
        return false;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDarkInfection();
	}

	@Override
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new CustomStateMapper());
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "opening=0"));
	}
	
	public static class CustomStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			int opening = state.getValue(OPENING);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			builder.append("opening");
			builder.append("=");
			builder.append(""+opening);
			
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
