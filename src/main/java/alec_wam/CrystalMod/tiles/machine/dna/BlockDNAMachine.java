package alec_wam.CrystalMod.tiles.machine.dna;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDNAMachine extends BlockMachine implements ICustomModel  {
	
	public BlockDNAMachine() {
		super(Material.IRON);
		this.setHardness(1f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new MachineBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityMachine){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		boolean bool = state.getValue(BlockStateMachine.activeProperty);
        return bool ? 14 : super.getLightValue(state, world, pos);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDNAMachine();
	}
	
	public static class MachineBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockMachine block = (BlockMachine)state.getBlock();
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateMachine.activeProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateMachine.activeProperty));
			
			builder.append(",");
			
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
