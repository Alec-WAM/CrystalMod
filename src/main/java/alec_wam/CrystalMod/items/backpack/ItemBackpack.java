package alec_wam.CrystalMod.items.backpack;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpack;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackCrafting;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackEnderChest;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackFurnace;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackRepair;
import alec_wam.CrystalMod.items.backpack.container.SlotRange;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.NBTUtils;

public class ItemBackpack extends Item implements ICustomModel {
	
	public ItemBackpack(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		this.setMaxStackSize(1);
        this.setMaxDamage(0);
        ModItems.registerItem(this, "backpack");
        addPropertyOverride(new ResourceLocation("open"), new IItemPropertyGetter() {
  	      @Override
  	      @SideOnly(Side.CLIENT)
  	      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
  	        return updateOpenProperty(stack, entityIn);
  	      }
  	    });
	}
	
	private float updateOpenProperty(ItemStack stack, EntityLivingBase entityIn) {
	    return ItemNBTHelper.getBoolean(stack, "isOpen", false) ? 1.0F : 0.0F;
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (worldIn.isRemote == false)
        {
            if(!playerIn.isSneaking()){
            	playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, worldIn, ItemNBTHelper.getInteger(itemStackIn, "LastTab", 0), (int)playerIn.inventory.currentItem, hand.ordinal());
            }
            else playerIn.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, worldIn, 0, (int)playerIn.inventory.currentItem, hand.ordinal());
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        super.onCreated(stack, world, player);
        // Create the UUID when the item is crafted
        NBTUtils.getUUIDFromItemStack(stack, "UUID", true);
    }
    
    public boolean isBurning(ItemStack stack)
    {
        return ItemNBTHelper.getInteger(stack, "Furnace.Burntime", 0) > 0;
    }
    
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
    	super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    	boolean flag = this.isBurning(stack);
        boolean flag1 = false;

        InventoryBackpackModular inv = null;
        int furnaceZero = BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE;
        boolean isOpen = false;
        if(entityIn instanceof EntityPlayer){
        	EntityPlayer player = (EntityPlayer) entityIn;
        	if(player.openContainer !=null){
        		if(player.openContainer instanceof ContainerBackpack){
        			inv = ((ContainerBackpack)player.openContainer).inventoryItemModular;
        			isOpen = isSelected;
        		}
        		else if(player.openContainer instanceof ContainerBackpackCrafting){
        			inv = ((ContainerBackpackCrafting)player.openContainer).inventoryItemModular;
        			isOpen = isSelected;
        		}
        		else if(player.openContainer instanceof ContainerBackpackEnderChest){
        			inv = ((ContainerBackpackEnderChest)player.openContainer).inventoryItemModular;
        			isOpen = isSelected;
        		}
        		else if(player.openContainer instanceof ContainerBackpackRepair){
        			inv = ((ContainerBackpackRepair)player.openContainer).inventoryItemModular;
        			isOpen = isSelected;
        		}
        		else if(player.openContainer instanceof ContainerBackpackFurnace){
        			inv = ((ContainerBackpackFurnace)player.openContainer).inventoryItemModular;
        			isOpen = isSelected;
        		}else
                {
                    inv = new InventoryBackpackModular(stack, BackpackUtils.INV_SIZE, true, player);
                    isOpen = false;
                }
        	}else
            {
                inv = new InventoryBackpackModular(stack, BackpackUtils.INV_SIZE, true, player);
                isOpen = false;
            }
        }
        
        if(isOpen !=ItemNBTHelper.getBoolean(stack, "isOpen", false)){
        	ItemNBTHelper.setBoolean(stack, "isOpen", isOpen);
        }
        
        if (flag)
        {
        	int current = ItemNBTHelper.getInteger(stack, "Furnace.Burntime", 0);
        	ItemNBTHelper.setInteger(stack, "Furnace.Burntime", current-1);
        	//ModLogger.info("Cooktime: "+current);
        	flag1 = true;
        }

        if (!worldIn.isRemote && inv !=null)
        {
            if (this.isBurning(stack) || inv.getStackInSlot(furnaceZero+1) != null && inv.getStackInSlot(furnaceZero) != null)
            {
                if (!this.isBurning(stack) && canSmelt(inv))
                {
                    int burn = TileEntityFurnace.getItemBurnTime(inv.getStackInSlot(furnaceZero+1));
                    ItemNBTHelper.setInteger(stack, "Furnace.CurrentItemBurntime", burn);
                    ItemNBTHelper.setInteger(stack, "Furnace.Burntime", burn);
                    
                    if (this.isBurning(stack))
                    {
                        flag1 = true;

                        if (inv.getStackInSlot(furnaceZero+1) != null)
                        {
                            --inv.getStackInSlot(furnaceZero+1).stackSize;

                            if (inv.getStackInSlot(furnaceZero+1).stackSize == 0)
                            {
                                inv.setStackInSlot(furnaceZero+1, inv.getStackInSlot(furnaceZero+1).getItem().getContainerItem(inv.getStackInSlot(furnaceZero+1)));
                            }
                        }
                    }
                }

                if (this.isBurning(stack) && canSmelt(inv))
                {
                	int currentCookTime = ItemNBTHelper.getInteger(stack, "Furnace.Cooktime", 0);
                	ItemNBTHelper.setInteger(stack, "Furnace.Cooktime", currentCookTime+1);
                    if (ItemNBTHelper.getInteger(stack, "Furnace.Cooktime", 0) >= ItemNBTHelper.getInteger(stack, "Furnace.TotalCooktime", 0))
                    {
                    	ItemNBTHelper.setInteger(stack, "Furnace.Cooktime", 0);
                    	ItemNBTHelper.setInteger(stack, "Furnace.TotalCooktime", 200);
                        smeltItem(inv);
                        flag1 = true;
                    }
                }
                else
                {
                	ItemNBTHelper.setInteger(stack, "Furnace.Cooktime", 0);
                	flag1 = true;
                }
            }
            else if (!this.isBurning(stack) && ItemNBTHelper.getInteger(stack, "Furnace.Cooktime", 0) > 0)
            {
            	ItemNBTHelper.setInteger(stack, "Furnace.Cooktime", MathHelper.clamp_int(ItemNBTHelper.getInteger(stack, "Furnace.Cooktime", 0) - 2, 0, ItemNBTHelper.getInteger(stack, "Furnace.TotalCooktime", 0)));
            	flag1 = true;
            }

            if (flag != this.isBurning(stack))
            {
                flag1 = true;
            }
        }

        if (flag1)
        {
            if(inv!=null)inv.onContentsChanged(0);
        }
    }
    
    private boolean canSmelt(InventoryBackpackModular inv)
    {
    	int furnaceZero = BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE;
        if (inv.getStackInSlot(furnaceZero) == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(inv.getStackInSlot(furnaceZero));
            if (itemstack == null) return false;
            if (inv.getStackInSlot(furnaceZero+2) == null) return true;
            if (!inv.getStackInSlot(furnaceZero+2).isItemEqual(itemstack)) return false;
            int result = inv.getStackInSlot(furnaceZero+2).stackSize + itemstack.stackSize;
            return result <= inv.getInventoryStackLimit() && result <= inv.getStackInSlot(furnaceZero+2).getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem(InventoryBackpackModular inv)
    {
        if (canSmelt(inv))
        {
        	int furnaceZero = BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE;
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(inv.getStackInSlot(furnaceZero));

            if (inv.getStackInSlot(furnaceZero+2) == null)
            {
            	inv.setStackInSlot(furnaceZero+2, itemstack.copy());
            }
            else if (inv.getStackInSlot(furnaceZero+2).getItem() == itemstack.getItem())
            {
            	inv.getStackInSlot(furnaceZero+2).stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
            }

            if (inv.getStackInSlot(furnaceZero).getItem() == Item.getItemFromBlock(Blocks.SPONGE) && inv.getStackInSlot(furnaceZero).getMetadata() == 1 && inv.getStackInSlot(furnaceZero+1) != null && inv.getStackInSlot(furnaceZero+1).getItem() == Items.BUCKET)
            {
                inv.setStackInSlot(furnaceZero+1, new ItemStack(Items.WATER_BUCKET));
            }

            --inv.getStackInSlot(furnaceZero).stackSize;

            if (inv.getStackInSlot(furnaceZero).stackSize <= 0)
            {
            	inv.setStackInSlot(furnaceZero, null);
            }
        }
    }
    
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
	
	public static void performGuiAction(EntityPlayer player, int action, int element)
    {
		ItemStack stack = null;
		IItemHandler inv = null;
        if (player.openContainer instanceof ContainerBackpack)
        {
        	ContainerBackpack container = (ContainerBackpack)player.openContainer;
            inv = container.inventoryItemModular;
            stack = container.getModularItem();
        }
        if (player.openContainer instanceof ContainerBackpackCrafting)
        {
        	ContainerBackpackCrafting container = (ContainerBackpackCrafting)player.openContainer;
            inv = container.inventoryItemModular;
            stack = container.getModularItem();
        }
        if (player.openContainer instanceof ContainerBackpackEnderChest)
        {
        	ContainerBackpackEnderChest container = (ContainerBackpackEnderChest)player.openContainer;
            inv = container.inventoryItemModular;
            stack = container.getModularItem();
        }
        if (player.openContainer instanceof ContainerBackpackRepair)
        {
        	ContainerBackpackRepair container = (ContainerBackpackRepair)player.openContainer;
            inv = container.inventoryItemModular;
            stack = container.getModularItem();
        }
        if (player.openContainer instanceof ContainerBackpackFurnace)
        {
        	ContainerBackpackFurnace container = (ContainerBackpackFurnace)player.openContainer;
            inv = container.inventoryItemModular;
            stack = container.getModularItem();
        }
        
        if (stack != null && stack.getItem() instanceof ItemBackpack)
        {
            if (action == 0 && inv !=null)
            {
                IItemHandler playerInv = new PlayerMainInvWrapper(player.inventory);

                switch(element & 0x7FFF)
                {
                    case 0: // Move all items to Bag
                        // Holding shift, move all items, even from hotbar
                        if ((element & 0x8000) != 0)
                        {
                            BackpackUtils.tryMoveAllItems(playerInv, inv);
                        }
                        else
                        {
                        	BackpackUtils.tryMoveAllItemsWithinSlotRange(playerInv, inv, new SlotRange(9, 27), new SlotRange(inv));
                        }
                        break;
                    case 1: // Move matching items to Bag
                    	BackpackUtils.tryMoveMatchingItems(playerInv, inv);
                        break;
                    case 2: // Leave one stack of each item type and fill that stack
                    	BackpackUtils.leaveOneFullStackOfEveryItem(playerInv, inv, true);
                        break;
                    case 3: // Fill stacks in player inventory from bag
                    	BackpackUtils.fillStacksOfMatchingItems(inv, playerInv);
                        break;
                    case 4: // Move matching items to player inventory
                    	BackpackUtils.tryMoveMatchingItems(inv, playerInv);
                        break;
                    case 5: // Move all items to player inventory
                    	BackpackUtils.tryMoveAllItems(inv, playerInv);
                        break;
                }
            }
            if(action == 1){
            	IItemHandler playerInv = new PlayerMainInvWrapper(player.inventory);
            	player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, player.worldObj, element, BackpackUtils.getSlotOfFirstMatchingItemStack(playerInv, stack), 0);
            	if(stack !=null){
                	ItemNBTHelper.setInteger(stack, "LastTab", element);
                }
            }
        }
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
	}
	
}
