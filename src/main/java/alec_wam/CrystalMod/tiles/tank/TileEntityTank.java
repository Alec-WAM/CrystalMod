package alec_wam.CrystalMod.tiles.tank;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.util.BlockUtil;

public class TileEntityTank extends TileEntityMod {

	public FluidTank tank;
	public boolean hasUpdate = false;
    private int prevLightValue = 0;
    private int cachedComparatorOverride = 0;
    public boolean creative = false;

    public TileEntityTank(){
    	tank = new FluidTank(Fluid.BUCKET_VOLUME * 16);
    }
    public TileEntityTank(int buckets){
    	tank = new FluidTank(Fluid.BUCKET_VOLUME * buckets);
    }
    public TileEntityTank(int buckets, boolean creative){
    	this(buckets);
    	this.creative = creative;
    }
    
	public void initialize() {
        updateComparators();
    }

    public void updateComparators() {
        int co = calculateComparatorInputOverride();
        cachedComparatorOverride = co;
        hasUpdate = true;
    }

    boolean init = false;
    /* UPDATING */
    @Override
    public void update() {
    	super.update();
        if (worldObj == null) return;
        
        if (!isInvalid() && !init) {
            initialize();
            init = true;
        }

        if (worldObj.isRemote) {
        	int lightValue = getFluidLightLevel();
            if (prevLightValue != lightValue) {
                prevLightValue = lightValue;
                worldObj.setLightFor(EnumSkyBlock.BLOCK, pos, lightValue);
            }
            return;
        }

        if (hasUpdate) {
        	BlockUtil.markBlockForUpdate(getWorld(), getPos());
            worldObj.updateComparatorOutputLevel(pos, getBlockType());
            hasUpdate = false;
        }

        
	    
    }
    
    /* SAVING & LOADING */
    @Override
    public void readCustomNBT(NBTTagCompound data) {
        super.readCustomNBT(data);
        tank = ItemBlockTank.loadTank(data);
        data.setBoolean("Creative", creative);
        updateAfterLoad();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound data) {
        super.writeCustomNBT(data);
        data.setInteger("tankType", this.getBlockMetadata());
        ItemBlockTank.saveTank(data, tank);
        creative = data.getBoolean("Creative");
    }

    /* ITANKCONTAINER */
    
    

    public int getFluidLightLevel() {
        FluidStack tankFluid = tank.getFluid();
        return tankFluid == null || tankFluid.amount == 0 ? 0 : tankFluid.getFluid().getLuminosity(tankFluid);
    }

    public int calculateComparatorInputOverride() {
        if (tank !=null && tank.getFluid() !=null) {
            return (tank.getFluid().amount * 15) / tank.getCapacity();
        } else {
            return 0;
        }
    }

    public int getComparatorInputOverride() {
        return cachedComparatorOverride;
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return tank;
            	}
            	
            	public int fill(FluidStack resource, boolean doFill) {
                    if (resource == null) {
                        return 0;
                    }
                    FluidStack resourceCopy = resource.copy();
                    boolean infi = false;
                    IBlockState state = getWorld() !=null ? getWorld().getBlockState(getPos()) : null;
                    if(state !=null && state.getBlock() instanceof BlockTank){
                    	infi = state.getValue(BlockTank.TYPE) == TankType.CREATIVE;
                    }
                    if(infi){
                    	FluidStack resourceCreative = resource.copy();
                    	resourceCreative.amount = getTank().getCapacity();
                    	int oldComparator = getComparatorInputOverride();
                    	
                    	getTank().fill(resourceCreative, doFill);
                    	hasUpdate = true;
                    	dirtyClient = true;
                    	if (oldComparator != calculateComparatorInputOverride()) {
                    		updateComparators();
                    	}
                    	return resource.amount;
                    }
                    int totalUsed = 0;

                    FluidStack liquid = tank.getFluid();
                    if (liquid != null && liquid.amount > 0 && !liquid.isFluidEqual(resourceCopy)) {
                        return 0;
                    }

                    int oldComparator = getComparatorInputOverride();

                    if(resourceCopy.amount > 0) {
                        int used = tank.fill(resourceCopy, doFill);
                        resourceCopy.amount-=used;
                        if (used > 0) {
                        	hasUpdate = true;
                        	dirtyClient = true;
                        }
                        totalUsed += used;
                    }

                    if (oldComparator != calculateComparatorInputOverride()) {
                        updateComparators();
                    }

                    return totalUsed;
                }

                public FluidStack drain(int maxEmpty, boolean doDrain) {
                	boolean infi = false;
                    IBlockState state = getWorld() !=null ? getWorld().getBlockState(getPos()) : null;
                    if(state !=null && state.getBlock() instanceof BlockTank){
                    	infi = state.getValue(BlockTank.TYPE) == TankType.CREATIVE;
                    }
                    if(infi){
                    	if(getTank() == null || getTank().getFluid() == null)return null;
                		FluidStack output = getTank().getFluid().copy();
                		output.amount = maxEmpty;
                		return output;
                	}
                	hasUpdate = true;
                	dirtyClient = true;
                    int oldComparator = getComparatorInputOverride();
                    FluidStack output = getTank().drain(maxEmpty, doDrain);

                    if (oldComparator != calculateComparatorInputOverride()) {
                        updateComparators();
                    }

                    return output;
                }

                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    if (resource == null) {
                        return null;
                    }
                    if (!resource.isFluidEqual(getTank().getFluid())) {
                        return null;
                    }
                    return drain(resource.amount, doDrain);
                }

				@Override
				public IFluidTankProperties[] getTankProperties() {
					return getTank().getTankProperties();
				}
                
            };
        }
        return super.getCapability(capability, facing);
    }
}