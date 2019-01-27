package alec_wam.CrystalMod.tiles.machine.elevator.caller;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;
import alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCaller.ElevatorButton;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockElevatorCaller extends BlockContainer {

	public static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockElevatorCaller() {
		super(Material.IRON);
		setHardness(2.0f);
		setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d origin, Vec3d direction){
		/*EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if(player == null){
			return super.collisionRayTrace(world, pos, origin, direction);
		}*/
		
        setBlockBounds(0, 0, 0, 1, 1, 1);
        @SuppressWarnings("deprecation")
		RayTraceResult rayTrace = super.collisionRayTrace(state, world, pos, origin, direction);
        EnumFacing orientation = rayTrace.sideHit;
        if(rayTrace != null) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof TileEntityElevatorCaller) {
                TileEntityElevatorCaller caller = (TileEntityElevatorCaller)te;
                for(TileEntityElevatorCaller.ElevatorButton button : caller.getFloors()) {
                    float startX = 0, startZ = 0, endX = 0, endZ = 0;
                    switch(orientation){
                        case NORTH:
                            startZ = 0F;
                            endZ = 0.01F;
                            endX = 1 - (float)button.posX;
                            startX = 1 - ((float)button.posX + (float)button.width);
                            break;
                        case SOUTH:
                            startZ = 0.99F;
                            endZ = 1F;
                            startX = (float)button.posX;
                            endX = (float)button.posX + (float)button.width;
                            break;
                        case WEST:
                            startX = 0F;
                            endX = 0.01F;
                            startZ = (float)button.posX;
                            endZ = (float)button.posX + (float)button.width;
                            break;
                        case EAST:
                            startX = 0.99F;
                            endX = 1F;
                            endZ = 1 - (float)button.posX;
                            startZ = 1 - ((float)button.posX + (float)button.width);
                            break;
						case DOWN:
							break;
						case UP:
							break;
						default:
							break;
                    }
                    float offset = /*!world.isRemote ? 0.35f : */0f;
                    
                    setBlockBounds(startX, 1 - (float)(button.posY + button.height)+offset, startZ, endX, 1 - (float)button.posY+offset, endZ);
                    @SuppressWarnings("deprecation")
					RayTraceResult buttonTrace = super.collisionRayTrace(state, world, pos, origin, direction);
                    if(buttonTrace != null) {
                        if(startX > 0.01F && startX < 0.98F) startX += 0.01F;
                        if(startZ > 0.01F && startZ < 0.98F) startZ += 0.01F;
                        if(endX > 0.02F && endX < 0.99F) endX -= 0.01F;
                        if(endZ > 0.02F && endZ < 0.99F) endZ -= 0.01F;
                        setBlockBounds(startX, 1.01F - (float)(button.posY + button.height), startZ, endX, 0.99F - (float)button.posY, endZ);
                        buttonTrace.subHit = button.floorNumber;
                        return buttonTrace;
                    }
                }
            }
        }

        setBlockBounds(0, 0, 0, 1, 1, 1);
        return rayTrace;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityElevatorCaller();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityElevatorCaller) {
        	TileEntityElevatorCaller caller = (TileEntityElevatorCaller) te;
        	if(playerIn.isSneaking()){
        		ChatUtil.sendChat(playerIn, "Buttons: "+caller.buttons.length);
    			for(ElevatorButton button : caller.buttons){
    				ChatUtil.sendChat(playerIn, button.buttonText+": "+button.posX+" "+button.posY+" "+button.height);
    			}
    			BlockUtil.markBlockForUpdate(worldIn, pos);
    			return true;
    		}
        	RayTraceResult mop = EntityUtil.getEntityLookedObject(playerIn);
            if(mop != null && mop.subHit >= 0) {
            	if(!worldIn.isRemote){
                	ModLogger.info(""+mop.subHit);
                	for(EnumFacing face : EnumFacing.VALUES){
                		TileEntity tile = worldIn.getTileEntity(pos.offset(face));
                		if(tile instanceof TileEntityElevator){
                			TileEntityElevator ele = (TileEntityElevator)tile;
                			int lvl = -1;
                			TileEntityElevator con = null;
        					BlockPos conPos = ele.findBottomElevator();
							if(conPos !=null){
								TileEntity tile2 = worldIn.getTileEntity(conPos);
								if(tile2 !=null && tile2 instanceof TileEntityElevator){
									con = (TileEntityElevator)tile2;
								}
							}
							if(con !=null){
								String name = ""+lvl;
								if(mop.subHit < caller.buttons.length){
									if(caller.buttons[mop.subHit] !=null){
										name = caller.buttons[mop.subHit].buttonText;
									}
								}
								if(mop.subHit < con.floors.size()){
		    						BlockPos fPos = con.floors.get(mop.subHit);
		    						if(fPos !=null){
	    								lvl = fPos.getY()-conPos.getY();
	    							}
        						}
								if(lvl > -1){
	        						if(ele.getCurrentLevel() !=lvl){
		        						ele.toLevel(lvl);
		        						ChatUtil.sendChat(playerIn, "Floor: "+name);
	        						}
	        					}
        					}
                		}
                	}
                    
                }
            	return true;
        	}
        	return false;
        }
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        world.setBlockState(pos, state.withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite()), 2);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevatorCaller) {
        	TileEntityElevatorCaller tileEntityElevator = (TileEntityElevatorCaller) te;
            tileEntityElevator.updateFloors();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElevatorCaller) {
        	TileEntityElevatorCaller tileEntityElevator = (TileEntityElevatorCaller) te;
            tileEntityElevator.updateFloors();
        }
        super.breakBlock(world, pos, state);
    }
	
	@Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return super.rotateBlock(world, pos, axis);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING_HORIZ, getFacingHoriz(meta));
    }
	
	public static EnumFacing getFacingHoriz(int meta) {
        return EnumFacing.values()[meta+2];
    }
	
	@Override
	public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING_HORIZ).getIndex()-2;
    }
	
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ);
    }

	private AxisAlignedBB bounds = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
	
	private void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		this.bounds = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos){
		return bounds;
	}
	
}
