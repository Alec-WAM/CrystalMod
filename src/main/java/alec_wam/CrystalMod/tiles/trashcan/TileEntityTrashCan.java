package alec_wam.CrystalMod.tiles.trashcan;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;

public class TileEntityTrashCan extends TileEntityModStatic {

	private final LazyOptional<IItemHandler> holderItems;
	private final LazyOptional<IFluidHandler> holderFluids;
	public TileEntityTrashCan() {
		super(ModBlocks.TILE_TRASH);
		VoidInventory inv = new VoidInventory();
		holderItems = LazyOptional.of(() -> inv);
		VoidTank tank = new VoidTank();
		holderFluids = LazyOptional.of(() -> tank);
	}

	public static class VoidInventory implements IItemHandler {

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return ItemStackTools.getEmptyStack();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return ItemStackTools.getEmptyStack();
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStackTools.getEmptyStack();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return true;
		}
		
	}
	
	public static class VoidTank implements IFluidHandler {

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[]{new IFluidTankProperties() {

				@Override
				public FluidStack getContents() {
					return null;
				}

				@Override
				public int getCapacity() {
					return 8000;
				}

				@Override
				public boolean canFill() {
					return true;
				}

				@Override
				public boolean canDrain() {
					return false;
				}

				@Override
				public boolean canFillFluidType(FluidStack fluidStack) {
					return true;
				}

				@Override
				public boolean canDrainFluidType(FluidStack fluidStack) {
					return false;
				}
			}};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return resource.amount;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return null;
		}
		
	}
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return holderItems.cast();
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holderFluids.cast();
        return super.getCapability(cap, side);
    }
	
}
