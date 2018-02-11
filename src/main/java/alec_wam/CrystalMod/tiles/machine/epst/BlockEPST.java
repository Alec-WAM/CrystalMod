package alec_wam.CrystalMod.tiles.machine.epst;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEPST extends BlockMachine implements ICustomModel {

	public BlockEPST() {
		super(Material.ROCK);
		this.setSoundType(SoundType.STONE);
		this.setHardness(50f).setResistance(2000F);
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new MachineBlockStateMapper());
		ModBlocks.initBasicModel(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEPST();
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEPST){
			if(placer !=null && placer instanceof EntityPlayer){
				((TileEPST)tile).setOwner(EntityPlayer.getUUID(((EntityPlayer)placer).getGameProfile()));
			}
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEPST)
        {
        	TileEPST dis = (TileEPST)tile;
            for(int i = 0; i < dis.getSizeInventory(); i++){
            	ItemStack stack = dis.getStackInSlot(i);
            	if(ItemStackTools.isValid(stack)){
            		ItemUtil.spawnItemInWorldWithRandomMotion(world, stack, pos);
            	}
            }
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEPST){
			TileEPST epst = (TileEPST)tile;
			if(UUIDUtils.areEqual(EntityPlayer.getUUID(player.getGameProfile()), epst.getOwner())){
				if(!world.isRemote){
					player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
				}
				return true;
			}
		}
		return false;
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
