package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffold extends EnumBlock<WoodenBlockProperies.WoodType> {

	public BlockScaffold() {
		super(Material.WOOD, WoodenBlockProperies.WOOD, WoodType.class);
		this.setHardness(0F);
		this.setResistance(0F);
		this.setSoundType(SoundType.WOOD);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		super.initModel();
		/*ModelLoader.setCustomStateMapper(this, new NormalBlockStateMapper());
		for(WoodType type : WoodType.values()){
			ResourceLocation baseLocation = getRegistryName();
			ModelResourceLocation inv = new ModelResourceLocation(baseLocation, "inventory");
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
			ClientProxy.registerCustomModel(inv, new ModelScaffold(type));
			ClientProxy.registerCustomModel(new ModelResourceLocation(baseLocation, "normal"), new ModelScaffold(type));
		}*/
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
	
	@Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new FakeBlockStateWithData(state, world, pos);
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;//required so that when climbing inside it stays invisible
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = player.getHeldItem(hand);
		if(ItemStackTools.isValid(stack)){
			if(stack.getItem() == Item.getItemFromBlock(this)){
				WoodType type = WoodType.byMetadata(stack.getMetadata());
				if(state.getValue(WoodenBlockProperies.WOOD) == type){
					BlockPos topPos = pos.up();
					while(world.getBlockState(topPos) == state && topPos.getY() < world.getHeight()){
						topPos = topPos.up();
					}
					IBlockState topState = world.getBlockState(topPos);
					if(topState.getBlock().isReplaceable(world, topPos) && world.mayPlace(this, topPos, false, EnumFacing.UP, null) && player.canPlayerEdit(topPos, EnumFacing.UP, stack)){
						world.setBlockState(topPos, state);
						if(!player.capabilities.isCreativeMode){
							ItemStackTools.incStackSize(stack, -1);
							player.setHeldItem(hand, stack);
						}
					}
					return true;
				}
			}
		}
		return false;
    }
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
		if(player.isSneaking()){
			return (player.getDigSpeed(state, pos) / 0.5F / 30F);					
		}
		IBlockState topState = worldIn.getBlockState(pos.up());
        return topState == state ? -1.0F : (player.getDigSpeed(state, pos) / 0.5F / 30F);
    }
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
		if(player.isSneaking()) return;
		IBlockState state = world.getBlockState(pos);
		BlockPos topPos = pos;
		while(world.getBlockState(topPos.up()) == state){
			topPos = topPos.up();
		}
		
		IBlockState topState = world.getBlockState(topPos);
		if(topPos.getY() > pos.getY() && world.isBlockModifiable(player, topPos)){
			world.playEvent(2001, topPos, Block.getStateId(topState));

            if (!player.capabilities.isCreativeMode)
            {
                dropBlockAsItem(world, topPos, topState, 0);
            }

            world.setBlockState(topPos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
		}
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
		if(player.isSneaking() || !player.capabilities.isCreativeMode) return super.removedByPlayer(state, world, pos, player, willHarvest);
		
		BlockPos topPos = pos;
		while(world.getBlockState(topPos.up()).getBlock() == state.getBlock()){
			topPos = topPos.up();
		}
		
		IBlockState topState = world.getBlockState(topPos);
		if(topPos.getY() > pos.getY() && world.isBlockModifiable(player, topPos)){
			world.playEvent(2001, topPos, Block.getStateId(topState));
			world.setBlockState(topPos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
			return false;
		}
		return super.removedByPlayer(topState, world, topPos, player, willHarvest);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side) && blockAccess.getBlockState(pos.offset(side)).getBlock() !=this;
    }

}
