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
	
    public static final String NBT_INPUTS = "Inputs";
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

    public static void addInput(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_INPUTS);
    }

    public static void addOutput(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_OUTPUTS);
    }

    public static void addByproduct(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_BYPRODUCTS);
    }

    private static void add(ItemStack pattern, ItemStack stack, String type) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        if (!pattern.getTagCompound().hasKey(type)) {
            pattern.getTagCompound().setTag(type, new NBTTagList());
        }

        pattern.getTagCompound().getTagList(type, Constants.NBT.TAG_COMPOUND).appendTag(stack.serializeNBT());
    }

    public static List<ItemStack> getInputs(ItemStack pattern) {
        return get(pattern, NBT_INPUTS);
    }

    public static List<ItemStack> getOutputs(ItemStack pattern) {
        return get(pattern, NBT_OUTPUTS);
    }

    public static List<ItemStack> getByproducts(ItemStack pattern) {
        return get(pattern, NBT_BYPRODUCTS);
    }

    private static List<ItemStack> get(ItemStack pattern, String type) {
        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(type)) {
            return null;
        }

        NBTTagList stacksList = pattern.getTagCompound().getTagList(type, Constants.NBT.TAG_COMPOUND);

        List<ItemStack> stacks = Lists.newArrayList();

        for (int i = 0; i < stacksList.tagCount(); ++i) {
            stacks.add(ItemStack.loadItemStackFromNBT(stacksList.getCompoundTagAt(i)));
        }

        return stacks;
    }

    public static boolean isValid(ItemStack pattern) {
        if (pattern.getTagCompound() == null || (!pattern.getTagCompound().hasKey(NBT_INPUTS) || !pattern.getTagCompound().hasKey(NBT_OUTPUTS) || !pattern.getTagCompound().hasKey(NBT_PROCESSING))) {
            return false;
        }

        for (ItemStack input : getInputs(pattern)) {
            if (input == null) {
                return false;
            }
        }

        for (ItemStack output : getOutputs(pattern)) {
            if (output == null) {
                return false;
            }
        }
        
        List<ItemStack> byproducts = getByproducts(pattern);
        if (byproducts !=null && !byproducts.isEmpty()) {
            for (ItemStack byproduct : byproducts) {
                if (byproduct == null) {
                    return false;
                }
            }
        }

        return true;
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
