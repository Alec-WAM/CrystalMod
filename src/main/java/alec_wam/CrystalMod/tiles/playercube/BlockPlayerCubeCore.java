package alec_wam.CrystalMod.tiles.playercube;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.api.block.IExplosionImmune;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.world.ModDimensions;

public class BlockPlayerCubeCore extends Block implements IExplosionImmune
{
	enum ORIENTATION implements IStringSerializable
	{
		NW("nw"), NE("ne"), ES("es"), SW("sw");

		String value;

		private ORIENTATION(String value)
		{
			this.value = value;
		}

		@Override
		public String getName()
		{
			return value;
		}
	}

	public static PropertyEnum<BlockPlayerCubeCore.ORIENTATION> orientation = PropertyEnum.<BlockPlayerCubeCore.ORIENTATION> create("orientation", BlockPlayerCubeCore.ORIENTATION.class);

	public BlockPlayerCubeCore()
	{
		super(Material.IRON);

		this.setBlockUnbreakable();
		this.setSoundType(SoundType.GLASS);
		this.setResistance(6000000.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(orientation, ORIENTATION.NW));
		this.setCreativeTab(null);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		ORIENTATION blockPosition;

		if (worldIn.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock() != this && worldIn.getBlockState(pos.offset(EnumFacing.WEST)).getBlock() != this)
		{
			blockPosition = ORIENTATION.NW;
		}
		else if (worldIn.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock() != this && worldIn.getBlockState(pos.offset(EnumFacing.EAST)).getBlock() != this)
		{
			blockPosition = ORIENTATION.NE;
		}
		else if (worldIn.getBlockState(pos.offset(EnumFacing.EAST)).getBlock() != this && worldIn.getBlockState(pos.offset(EnumFacing.SOUTH)).getBlock() != this)
		{
			blockPosition = ORIENTATION.ES;
		}
		else
		{
			blockPosition = ORIENTATION.SW;
		}

		return state.withProperty(orientation, blockPosition);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { orientation });
	}

	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		if(other.getBlock() == this || other.getBlock() == ModBlocks.cubeBlock)return false;
		return super.shouldSideBeRendered(state, worldIn, pos, side);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (worldIn.provider.getDimension() == ModDimensions.CUBE_ID)
		{
			if (!worldIn.isRemote)
			{
				CubeManager.getInstance().teleportPlayerBack((EntityPlayerMP) playerIn);
			}
			return true;
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, held, side, hitX, hitY, hitZ);
	}
}
