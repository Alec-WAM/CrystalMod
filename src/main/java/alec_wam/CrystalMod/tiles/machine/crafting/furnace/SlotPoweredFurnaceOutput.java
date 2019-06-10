package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import java.util.Map.Entry;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SlotPoweredFurnaceOutput extends Slot {
   /** The player that is using the GUI where this slot resides. */
   private final PlayerEntity player;
   private int removeCount;

   public SlotPoweredFurnaceOutput(PlayerEntity player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
      super(inventoryIn, slotIndex, xPosition, yPosition);
      this.player = player;
   }

   /**
    * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
    */
   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   /**
    * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
    */
   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(amount, this.getStack().getCount());
      }

      return super.decrStackSize(amount);
   }

   @Override
   public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
      this.onCrafting(stack);
      super.onTake(thePlayer, stack);
      return stack;
   }

   /**
    * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
    * internal count then calls onCrafting(item).
    */
   @Override
   protected void onCrafting(ItemStack stack, int amount) {
      this.removeCount += amount;
      this.onCrafting(stack);
   }

   /**
    * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
    */
   @Override
   protected void onCrafting(ItemStack stack) {
      stack.onCrafting(this.player.world, this.player, this.removeCount);
      if (!this.player.world.isRemote) {
         for(Entry<ResourceLocation, Integer> entry : ((TileEntityPoweredFurnace)this.inventory).getRecipeUseCounts().entrySet()) {
            FurnaceRecipe furnacerecipe = (FurnaceRecipe)this.player.world.getRecipeManager().func_215367_a(entry.getKey()).orElse(null);
            float f;
            if (furnacerecipe != null) {
               f = furnacerecipe.func_222138_b();
            } else {
               f = 0.0F;
            }

            int i = entry.getValue();
            if (f == 0.0F) {
               i = 0;
            } else if (f < 1.0F) {
               int j = MathHelper.floor((float)i * f);
               if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
                  ++j;
               }

               i = j;
            }

            while(i > 0) {
               int k = ExperienceOrbEntity.getXPSplit(i);
               i -= k;
               this.player.world.func_217376_c(new ExperienceOrbEntity(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
            }
         }

         ((IRecipeHolder)this.inventory).onCrafting(this.player);
      }

      this.removeCount = 0;
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerSmeltedEvent(this.player, stack);
   }
}