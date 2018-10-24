package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSturdyScaffold extends EnumBlock<WoodenBlockProperies.WoodType>{

	private static final double OFFSET = 0.0125D;//shearing & cactus are  0.0625D;
  	protected static final AxisAlignedBB AABB = new AxisAlignedBB(OFFSET, 0, OFFSET, 1 - OFFSET, 1, 1 - OFFSET);//required to make entity collied happen for ladder climbing
	public BlockSturdyScaffold() {
		super(Material.WOOD, WoodenBlockProperies.WOOD, WoodType.class);
		this.setHardness(0F);
		this.setResistance(0F);
		this.setSoundType(SoundType.WOOD);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		return AABB;
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
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}
	
	private static final double CLIMB_SPEED = 0.25D;
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
		if (!(entityIn instanceof EntityLivingBase)) {
			return;
		}
		EntityLivingBase entity = (EntityLivingBase) entityIn;
		if (!entityIn.isCollidedHorizontally) {
			return;
		}
		if (entity.isSneaking()) {
			entity.motionY = 0.0D;
		}
		else if (entity.moveForward > 0.0F && entity.motionY < CLIMB_SPEED) {
			entity.motionY = CLIMB_SPEED;
		}
		if (worldIn.isRemote && entity instanceof EntityPlayer && entity.ticksExisted % 22 == 0) {
			CrystalModNetwork.sendToServer(new PacketEntityMessage(entity, "#ZeroFall#"));
		}
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
		/*if(world.isRemote){
        	Minecraft.getMinecraft().playerController.resetBlockRemoving();
        } else {
        	if(player instanceof EntityPlayerMP){
        		((EntityPlayerMP)player).interactionManager.cancelDestroyingBlock();
        	}
        }*/
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

}
