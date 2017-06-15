package alec_wam.CrystalMod.tiles.machine.power.redstonereactor;

import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.darkinfection.BlockInfected.InfectedBlockType;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneReactor extends BlockContainer implements ICustomModel {

	public BlockRedstoneReactor() {
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10.0F);
		setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
		TileEntity tileentitychest = world.getTileEntity(pos);
        if (tileentitychest != null && tileentitychest instanceof IInventory)
        {
        	ItemUtil.dropContent(0, (IInventory)tileentitychest, world, tileentitychest.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
		TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRedstoneReactor)
        {
           TileRedstoneReactor reactor = (TileRedstoneReactor)tile;
           if(reactor.remainingFuel.getValue() > 0 && !world.isBlockPowered(pos)){
	           Random random = world.rand;
	           double d0 = 0.0625D;
	
	           double d1 = (double)((float)pos.getX() + random.nextFloat());
	           double d2 = (double)((float)pos.getY() + random.nextFloat());
	           double d3 = (double)((float)pos.getZ() + random.nextFloat());
	
	           if (!world.getBlockState(pos.up()).isOpaqueCube())
	           {
	        	   d2 = (double)pos.getY() + 0.0625D + 1.0D;
	           }
	
	           if (d1 < (double)pos.getX() || d1 > (double)(pos.getX() + 1) || d2 < 0.0D || d2 > (double)(pos.getY() + 1) || d3 < (double)pos.getZ() || d3 > (double)(pos.getZ() + 1))
	           {
	        	   float f = random.nextFloat() * 0.6F + 0.4F;
	        	   world.spawnParticle(EnumParticleTypes.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
	           }
           }
        }
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(!player.isSneaking()){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRedstoneReactor();
	}
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
		}
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}

}
