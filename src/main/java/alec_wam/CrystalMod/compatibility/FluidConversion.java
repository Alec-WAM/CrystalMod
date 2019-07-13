package alec_wam.CrystalMod.compatibility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potions;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidConversion {
	
	public static net.minecraftforge.fluids.Fluid getFluidFromState(IFluidState state){
		net.minecraft.fluid.Fluid fluid = state.getFluid();
		if(fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA){
			return FixedFluidRegistry.LAVA;
		}
		if(fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER){
			return FixedFluidRegistry.WATER;
		}
		return null;
	}
	
	@Nullable
    public static FluidStack loadFluidStackFromNBT(CompoundNBT nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        if (!nbt.contains("FluidName"))
        {
            return null;
        }

        String fluidName = nbt.getString("FluidName");
        if (FixedFluidRegistry.getFluidFromName(fluidName) == null)
        {
            return null;
        }
        FluidStack stack = new FluidStackFixed(FixedFluidRegistry.getFluidFromName(fluidName), nbt.getInt("Amount"));

        if (nbt.contains("Tag"))
        {
            stack.tag = nbt.getCompound("Tag");
        }
        return stack;
    }

    public static CompoundNBT writeToNBT(FluidStack stack, CompoundNBT nbt)
    {
        nbt.putString("FluidName", stack.getUnlocalizedName());
        nbt.putInt("Amount", stack.amount);

        if (stack.tag != null)
        {
            nbt.put("Tag", stack.tag);
        }
        return nbt;
    }
    
    public static LazyOptional<IFluidHandlerItem> getHandlerFromItem(ItemStack stack){
    	FluidStack fluid = FixedFluidRegistry.getBucketFluid(stack);
    	if(fluid !=null){
    		return LazyOptional.of(() -> new FixedFluidBucketHandler(stack));
    	}
    	if(stack.getItem() == Items.BUCKET){
    		return LazyOptional.of(() -> new FixedFluidBucketHandler(stack));
    	}
    	if(stack.getItem() == Items.GLASS_BOTTLE){
    		return LazyOptional.of(() -> new WaterBottleFluidHandler(stack));
    	}
    	if(stack.getItem() == Items.POTION){
    		String potion = ItemNBTHelper.getString(stack, "Potion", "");
    		@SuppressWarnings("deprecation")
			ResourceLocation resourcelocation = Registry.POTION.getKey(Potions.WATER);
    		if(potion.equalsIgnoreCase(resourcelocation.toString())){
    			return LazyOptional.of(() -> new WaterBottleFluidHandler(stack));
    		}
    	}
    	return null;
    }
    
    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        return FluidUtil.getFluidHandler(world, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
    }
    
    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull IFluidHandler handler)
    {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty())
        {
            return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(playerInventory -> {

                    FluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                    if (!fluidActionResult.isSuccess())
                    {
                        fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                    }

                    if (fluidActionResult.isSuccess())
                    {
                        player.setHeldItem(hand, fluidActionResult.getResult());
                        return true;
                    }
                    return false;
                })
                .orElse(false);
        }
        return false;
    }

    /**
     * Takes an Fluid Container Item and tries to fill it from the given tank.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the filled container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container ItemStack to fill.
     *                    Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take.
     *                    Can be null, only used if the inventory cannot take the filled stack.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doFill)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.abilities.isCreativeMode)
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.getCount() == 1) // don't need to stow anything, just fill the container stack
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess())
            {
                return filledReal;
            }
        }
        else
        {
            FluidActionResult filledSimulated = tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null)
                {
                    FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doFill)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Takes an Fluid Container Item, tries to empty it into the fluid handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container        The filled Fluid Container Itemstack to empty.
     *                         Will not be modified directly, if modifications are necessary a modified copy is returned in the result.
     * @param fluidDestination The fluid destination to fill from the fluid container.
     * @param inventory        An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount        Maximum amount of fluid to take from the tank.
     * @param player           The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doDrain)
    {
        if (container.isEmpty())
        {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.abilities.isCreativeMode)
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        }
        else if (container.getCount() == 1) // don't need to stow anything, just fill and edit the container stack
        {
            FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess())
            {
                return emptiedReal;
            }
        }
        else
        {
            FluidActionResult emptiedSimulated = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedSimulated.isSuccess())
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null)
                {
                    FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doDrain)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }
    
    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable PlayerEntity player, boolean doFill)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getHandlerFromItem(containerCopy)
                .map(containerFluidHandler -> {
                    FluidStack simulatedTransfer = FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
                    if (simulatedTransfer != null)
                    {
                    	if (doFill)
                        {
                            FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                            if (player != null)
                            {
                                SoundEvent soundevent = simulatedTransfer.getFluid().getFillSound(simulatedTransfer);
                                player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                        }
                        else
                        {
                            containerFluidHandler.fill(simulatedTransfer, true);
                        }

                        ItemStack resultContainer = containerFluidHandler.getContainer();
                        return new FluidActionResult(resultContainer);
                    }
                    return FluidActionResult.FAILURE;
                })
                .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     *                         Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     *                         See {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, PlayerEntity, boolean)}.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     *         NOTE If the container is consumable, the empty container will be null on success.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable PlayerEntity player, boolean doDrain)
    {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getHandlerFromItem(containerCopy)
                .map(containerFluidHandler -> {
                	if (doDrain)
                    {
                		FluidStack transfer = FluidUtil.tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                		if (transfer != null)
                        {
                			if (player != null)
                            {
                                SoundEvent soundevent = transfer.getFluid().getEmptySound(transfer);
                                player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            }
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new FluidActionResult(resultContainer);
                        }
                    }
                    else
                    {
                        FluidStack simulatedTransfer = FluidUtil.tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
                        if (simulatedTransfer != null)
                        {
                            containerFluidHandler.drain(simulatedTransfer, true);
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new FluidActionResult(resultContainer);
                        }
                    }
                    return FluidActionResult.FAILURE;
                })
                .orElse(FluidActionResult.FAILURE);
    }
    
    
}
