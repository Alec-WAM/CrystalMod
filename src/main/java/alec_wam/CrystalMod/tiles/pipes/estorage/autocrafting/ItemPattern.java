package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

public class ItemPattern extends Item {
	
	private static Map<ItemStack, CraftingPattern> PATTERN_CACHE = new HashMap<ItemStack, CraftingPattern>();
	
	private static final String NBT_SLOT = "Slot_%d";
    public static final String NBT_OUTPUTS = "Outputs";
    public static final String NBT_BYPRODUCTS = "Byproducts";
    public static final String NBT_PROCESSING = "Processing";
    public static final String NBT_OREDICT = "Oredict";

    public ItemPattern() {
        super();
        setMaxStackSize(1);
    	this.setCreativeTab(CrystalMod.tabItems);
    	ModItems.registerItem(this, "craftingpattern");
    }
    
    public static CraftingPattern getPatternFromCache(World world, ItemStack stack) {
        if (!PATTERN_CACHE.containsKey(stack)) {
            PATTERN_CACHE.put(stack, new CraftingPattern(world, null, stack));
        }

        return PATTERN_CACHE.get(stack);
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List<String> list, boolean b) {
    	if (!pattern.hasTagCompound()) {
            return;
        }

        CraftingPattern cpattern = getPatternFromCache(player.worldObj, pattern);

        if (cpattern.isValid()) {
        	if (GuiScreen.isShiftKeyDown() || isProcessing(pattern)) {
                list.add(TextFormatting.YELLOW + Lang.localize("pattern.inputs") + TextFormatting.RESET);

                ItemUtil.combineMultipleItemsInTooltip(list, cpattern.getInputs());

                list.add(TextFormatting.YELLOW + Lang.localize("pattern.outputs") + TextFormatting.RESET);
            }

        	if(cpattern.isOredict()){
        		list.add(TextFormatting.YELLOW+"Ore");
        	}
        	ItemUtil.combineMultipleItemsInTooltip(list, cpattern.getOutputs());
        } else {
        	list.add("Invalid "+cpattern.getInputs().size()+" | "+cpattern.getOutputs().size());
        }
    	/*if (isValid(pattern)) {
            if (GuiScreen.isShiftKeyDown() || isProcessing(pattern)) {
                list.add(TextFormatting.YELLOW + Lang.localize("pattern.inputs") + TextFormatting.RESET);

                ItemUtil.combineMultipleItemsInTooltip(list, getInputs(pattern));

                list.add(TextFormatting.YELLOW + Lang.localize("pattern.outputs") + TextFormatting.RESET);
            }

            if(isOredict(pattern)){
            	list.add(TextFormatting.YELLOW+"Ore");
            }
            
            ItemUtil.combineMultipleItemsInTooltip(list, getOutputs(pattern));
        }*/
    }

    public static void setInput(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setTag(String.format(NBT_SLOT, slot), stack.serializeNBT());
    }

    public static ItemStack getInput(ItemStack pattern, int slot) {
        String id = String.format(NBT_SLOT, slot);

        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
            return null;
        }

        return ItemStack.loadItemStackFromNBT(pattern.getTagCompound().getCompoundTag(id));
    }

    public static void addOutput(ItemStack pattern, ItemStack stack) {
    	if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagList outputs;
        if (!pattern.getTagCompound().hasKey(NBT_OUTPUTS)) {
            outputs = new NBTTagList();
        } else {
            outputs = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        }

        outputs.appendTag(stack.serializeNBT());

        pattern.getTagCompound().setTag(NBT_OUTPUTS, outputs);
    }

    public static List<ItemStack> getOutputs(ItemStack pattern) {
    	if (!isProcessing(pattern)) {
            return null;
        }

        List<ItemStack> outputs = new ArrayList<ItemStack>();

        NBTTagList outputsTag = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));

            if (stack != null) {
                outputs.add(stack);
            }
        }

        return outputs;
    }

    public static void setProcessing(ItemStack pattern, boolean processing) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_PROCESSING, processing);
    }

    public static boolean isProcessing(ItemStack pattern) {
        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(NBT_PROCESSING)) {
            return false;
        }

        return pattern.getTagCompound().getBoolean(NBT_PROCESSING);
    }

    public static void setOredict(ItemStack pattern, boolean ore) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_OREDICT, ore);
    }

    public static boolean isOredict(ItemStack pattern) {
        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(NBT_OREDICT)) {
            return false;
        }

        return pattern.getTagCompound().getBoolean(NBT_OREDICT);
    }
}
