package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;

public class CraftingPattern {

	private World world;
    private IAutoCrafter crafter;
    private ItemStack pattern;
    private List<ItemStack> inputs;
    private List<ItemStack> outputs;
    private List<ItemStack> byproducts;

    
    public CraftingPattern(World world, IAutoCrafter crafter, ItemStack pattern){
    	this.world = world;
    	this.crafter = crafter;
    	this.pattern = pattern;
        this.inputs = Lists.newArrayList();
        this.outputs = Lists.newArrayList();
        this.byproducts = Lists.newArrayList();
        
        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
        	List<ItemStack> list = ItemPattern.getInputs(pattern);
            ItemStack slot = i >= list.size() ? null : list.get(i);

            if (slot != null) {
                for (int j = 0; j < slot.stackSize; ++j) {
                    inputs.add(ItemHandlerHelper.copyStackWithSize(slot, 1));
                }

                inv.setInventorySlotContents(i, slot);
            }
        }

        if (!ItemPattern.isProcessing(pattern)) {
            ItemStack output = CraftingManager.getInstance().findMatchingRecipe(inv, world);

            if (output != null) {
                outputs.add(output.copy());

                for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(inv, world)) {
                    if (remaining != null) {
                        byproducts.add(remaining.copy());
                    }
                }
            }
        } else {
            outputs = ItemPattern.getOutputs(pattern);
        }
    }

    public ItemStack getPatternStack(){
    	return pattern;
    }
    
    public IAutoCrafter getCrafter() {
        return crafter;
    }

    public boolean isProcessing() {
        return ItemPattern.isProcessing(pattern);
    }
    
    public boolean isOredict() {
        return ItemPattern.isOredict(pattern);
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public List<ItemStack> getByproducts() {
        return byproducts;
    }
    
    public List<ItemStack> getByproducts(ItemStack[] took) {
        List<ItemStack> byproducts = new ArrayList<ItemStack>();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            inv.setInventorySlotContents(i, took[i]);
        }

        for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(inv, world)) {
            if (remaining != null) {
                byproducts.add(remaining.copy());
            }
        }

        return byproducts;
    }
    
    public boolean isValid() {
        return !inputs.isEmpty() && !outputs.isEmpty();
    }
    
    public int getQuantityPerRequest(ItemStack requested) {
        int quantity = 0;

        for (ItemStack output : outputs) {
            if (ItemUtil.canCombine(requested, output)) {
                quantity += output.stackSize;

                if (!ItemPattern.isProcessing(pattern)) {
                    break;
                }
            }
        }

        return quantity;
    }
}
