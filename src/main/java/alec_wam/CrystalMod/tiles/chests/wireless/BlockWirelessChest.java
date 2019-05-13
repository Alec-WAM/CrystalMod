package alec_wam.CrystalMod.tiles.chests.wireless;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.BlockContainerCustom;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockWirelessChest extends BlockContainerCustom implements IBucketPickupHandler, ILiquidContainer {
	public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE_MAIN = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	   
	public BlockWirelessChest(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
		loadButtons();
	}

	@Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list)
    {
		for(EnumDyeColor dye : EnumDyeColor.values()){
    		ItemStack stack = new ItemStack(this);
    		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, WirelessChestHelper.getDefaultCode(dye));
    		list.add(stack);
	    }
    }
	
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	int code = ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0);
    	UUID owner = ItemNBTHelper.getUUID(stack, WirelessChestHelper.NBT_OWNER, null);
		if(owner !=null){
			String username = ProfileUtil.getUsernameClient(owner);
			tooltip.add(new TextComponentTranslation("crystalmod.info.owner", username));
		}
		String color1 = ItemUtil.getDyeName(WirelessChestHelper.getDye1(code));
		String color2 = ItemUtil.getDyeName(WirelessChestHelper.getDye2(code));
		String color3 = ItemUtil.getDyeName(WirelessChestHelper.getDye3(code));
		tooltip.add(new TextComponentTranslation("crystalmod.info.code", color1, color2, color3));
    }

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityWirelessChest();
	}
	
	public static DyeButton[][] buttons = new DyeButton[4][3];
	private static VoxelShape[][] buttonShapes = new VoxelShape[4][3];
	private static VoxelShape[] bakedShapes = new VoxelShape[4];
	public static void loadButtons(){
		for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL){
			for(int i = 0; i < 3; i++){
				DyeButton button = new DyeButton(i);
				button.rotate(0, 0.5625, 0.0625, 1, 0, 0, 0);
	            int buttonFacing = 0;
	            if (facing == EnumFacing.EAST) {
	            	buttonFacing = 1;
	            }
	            if (facing == EnumFacing.SOUTH) {
	            	buttonFacing = 2;
	            }
	            if (facing == EnumFacing.WEST) {
	            	buttonFacing = 3;
	            }
	            button.rotateMeta(buttonFacing);
	            buttons[facing.getHorizontalIndex()][i] = button;
	            AxisAlignedBB button1Axis = new AxisAlignedBB(button.getMin().getVec3().scale(16.0D), button.getMax().getVec3().scale(16.0D));
				buttonShapes[facing.getHorizontalIndex()][i] = BlockUtil.makeVoxelShape(button1Axis);
			}
		}
		
		for(int i = 0; i < 4; i++){
			VoxelShape[] bShapes = buttonShapes[i];
			VoxelShape buttons = VoxelShapes.or(bShapes[0], VoxelShapes.or(bShapes[1], bShapes[2]));
			bakedShapes[i] = VoxelShapes.or(buttons, SHAPE_MAIN);
		}
	}
	
	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		EnumFacing facing = state.get(FACING);
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityWirelessChest){
			TileEntityWirelessChest chest = (TileEntityWirelessChest)tile;
			if(!chest.open && chest.lidAngle == 0.0f){
				return bakedShapes[facing.getHorizontalIndex()];
			}
		}
		return SHAPE_MAIN;
	}
	
	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE_MAIN;
	}
	
	@Override
	public RayTraceResult getRayTraceResult(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original)
    {
		EnumFacing facing = state.get(FACING);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityWirelessChest){
			TileEntityWirelessChest chest = (TileEntityWirelessChest)tile;
			if(!chest.open && chest.lidAngle == 0.0f){
				VoxelShape[] bShapes = buttonShapes[facing.getHorizontalIndex()];
				for(int i = 0; i < 3; i++){
					RayTraceResult res = bShapes[i].func_212433_a(start, end, pos);
					if (res != null) {
						res.hitInfo = "Button_"+i;
						return res;
					}
				}
			}
		}
	    
		return original;
    }
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 *  
	 * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
	 * currentPos
	 */
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileEntityWirelessChest)
        {
            TileEntityWirelessChest chest = (TileEntityWirelessChest)te;
            if(!chest.isOwner(player.getUniqueID())){
            	return -1.0F;
            }
        }
        
		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity te = worldIn.getTileEntity(pos);

        if (te == null || !(te instanceof TileEntityWirelessChest))
        {
            return false;
        }
        
        TileEntityWirelessChest chest = (TileEntityWirelessChest)te;
        
        boolean owner = chest.isOwner(player.getUniqueID());
        
        if(!owner){
        	if(chest.isBoundToPlayer()){
        		if(!worldIn.isRemote) {
        			ChatUtil.sendChat(player, Lang.localizeFormat("message.ownerlocked", ProfileUtil.getUsernameServer(chest.getOwner())));
        		}
        	}
        	return true;
        }
        
        ItemStack stack = player.getHeldItem(hand);
    	if (ItemStackTools.isValid(stack)) {
        	if(ToolUtil.isHoldingWrench(player, hand) && player.isSneaking()){
        		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
        	} 
        	
        	if(stack.getItem() == Items.GOLD_INGOT){
        		if (!chest.isBoundToPlayer())
                {
        			if(worldIn.isRemote)return true;
        			worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.4F, 0.8f);
                	if (!player.abilities.isCreativeMode)
                		ItemStackTools.incStackSize(stack, -1);
					UUID uuid = EntityPlayer.getUUID(player.getGameProfile());
                    chest.bindToPlayer(uuid);
	                NBTTagCompound nbt = new NBTTagCompound();
	                nbt.setUniqueId("UUID", uuid);
	                CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "Owner", nbt), chest);
                    return true;
                }
        		return false;
        	}
       	 
        	RayTraceResult result = BlockUtil.rayTrace(worldIn, player, RayTraceFluidMode.NEVER);
    		if(result !=null){
    			if(result.hitInfo !=null && result.hitInfo instanceof String){
    				String hitType = ((String)result.hitInfo);
    				EnumDyeColor color = ItemUtil.getDyeColor(stack);
    				if(hitType.startsWith("Button_") && color !=null){
    					int button = Integer.parseInt(hitType.substring(7));
    					int code = chest.getCode();
    					int color1 = WirelessChestHelper.getColor1(code);
    					int color2 = WirelessChestHelper.getColor2(code);
    					int color3 = WirelessChestHelper.getColor3(code);
    					if(button == 0){
    						color1 = color.getId();
    					}
    					if(button == 1){
    						color2 = color.getId();
    					}
    					if(button == 2){
    						color3 = color.getId();
    					}
    					code = (color1) | (color2 << 4) | (color3 << 8);
    					
    					if (chest.getCode() != code)
    	                {
    						if(worldIn.isRemote) return true;
    						worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8f);
    	                	if (!player.abilities.isCreativeMode)
    	                		ItemStackTools.incStackSize(stack, -1);
    	                    chest.setCode(code);
    	                    chest.markDirty();
    						return true;
    	                }
    				}
    			}
    		}	
        }
    	
    	if(worldIn.isRemote)return true;
		if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
        {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;

            NetworkHooks.openGui(entityPlayerMP, chest, buf -> buf.writeBlockPos(pos));
        }
		return true;
    }
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.getPlacementHorizontalFacing();

	    IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(FACING, enumfacing).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
		if (state.get(WATERLOGGED)) {
			worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IFluidState getFluidState(IBlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
		return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
		if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
			if (!worldIn.isRemote()) {
				worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
				worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}	
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		int powerInput = 0;
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof IInventory){
			return Container.calcRedstoneFromInventory((IInventory)tile);
		}
		
		return powerInput;
	}
}