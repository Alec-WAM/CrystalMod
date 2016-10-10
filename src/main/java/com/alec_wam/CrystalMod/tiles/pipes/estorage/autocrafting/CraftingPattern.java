package com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class CraftingPattern {
    public static final String NBT = "Pattern";
    public static final String NBT_CRAFTER_X = "CrafterX";
    public static final String NBT_CRAFTER_Y = "CrafterY";
    public static final String NBT_CRAFTER_Z = "CrafterZ";
    public static final String NBT_CRAFTER_DIM = "CrafterDim";

    private int crafterX;
    private int crafterY;
    private int crafterZ;
    public int crafterDim;
    private IAutoCrafter crafter;
    private boolean processing;
    private ItemStack[] inputs;
    private ItemStack[] outputs;
    private ItemStack[] byproducts;

    
    public CraftingPattern(IAutoCrafter crafter, ItemStack pattern){
    	this(crafter.getPos().getX(), crafter.getPos().getY(), crafter.getPos().getZ(), crafter.getDimension(), ItemPattern.isProcessing(pattern), ItemPattern.getInputs(pattern), ItemPattern.getOutputs(pattern), ItemPattern.getByproducts(pattern));
    }

    public CraftingPattern(int crafterX, int crafterY, int crafterZ, int crafterDim, boolean processing, ItemStack[] inputs, ItemStack[] outputs, ItemStack[] byproducts) {
        this.crafterX = crafterX;
        this.crafterY = crafterY;
        this.crafterZ = crafterZ;
        this.crafterDim = crafterDim;
        this.processing = processing;
        this.inputs = inputs;
        this.outputs = outputs;
        this.byproducts = byproducts;
    }

    public IAutoCrafter getCrafter(World world) {
        if (crafter == null) {
            TileEntity tile = world.getTileEntity(new BlockPos(crafterX, crafterY, crafterZ));
            if(tile instanceof IAutoCrafter){
            	crafter = (IAutoCrafter)tile;
            }
        }

        return crafter;
    }

    public boolean isProcessing() {
        return processing;
    }

    public ItemStack[] getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public ItemStack[] getByproducts() {
        return byproducts;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean(ItemPattern.NBT_PROCESSING, processing);

        NBTTagList inputsTag = new NBTTagList();
        for (ItemStack input : inputs) {
            inputsTag.appendTag(input.serializeNBT());
        }
        tag.setTag(ItemPattern.NBT_INPUTS, inputsTag);

        NBTTagList outputsTag = new NBTTagList();
        for (ItemStack output : outputs) {
            outputsTag.appendTag(output.serializeNBT());
        }
        tag.setTag(ItemPattern.NBT_OUTPUTS, outputsTag);

        if (byproducts != null) {
            NBTTagList byproductsTag = new NBTTagList();
            for (ItemStack byproduct : byproducts) {
                byproductsTag.appendTag(byproduct.serializeNBT());
            }
            tag.setTag(ItemPattern.NBT_BYPRODUCTS, byproductsTag);
        }

        tag.setInteger(NBT_CRAFTER_X, crafter.getPos().getX());
        tag.setInteger(NBT_CRAFTER_Y, crafter.getPos().getY());
        tag.setInteger(NBT_CRAFTER_Z, crafter.getPos().getZ());
        tag.setInteger(NBT_CRAFTER_DIM, crafter.getDimension());
    }

    public static CraftingPattern readFromNBT(NBTTagCompound tag) {
        int cx = tag.getInteger(NBT_CRAFTER_X);
        int cy = tag.getInteger(NBT_CRAFTER_Y);
        int cz = tag.getInteger(NBT_CRAFTER_Z);
        int cd = tag.getInteger(NBT_CRAFTER_DIM);

        boolean processing = tag.getBoolean(ItemPattern.NBT_PROCESSING);

        NBTTagList inputsTag = tag.getTagList(ItemPattern.NBT_INPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack inputs[] = new ItemStack[inputsTag.tagCount()];

        for (int i = 0; i < inputsTag.tagCount(); ++i) {
            inputs[i] = ItemStack.loadItemStackFromNBT(inputsTag.getCompoundTagAt(i));

            if (inputs[i] == null) {
                return null;
            }
        }

        NBTTagList outputsTag = tag.getTagList(ItemPattern.NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack outputs[] = new ItemStack[outputsTag.tagCount()];

        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            outputs[i] = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));

            if (outputs[i] == null) {
                return null;
            }
        }

        ItemStack byproducts[] = new ItemStack[0];

        if (tag.hasKey(ItemPattern.NBT_BYPRODUCTS)) {
            NBTTagList byproductsTag = tag.getTagList(ItemPattern.NBT_BYPRODUCTS, Constants.NBT.TAG_COMPOUND);
            byproducts = new ItemStack[byproductsTag.tagCount()];

            for (int i = 0; i < byproductsTag.tagCount(); ++i) {
                byproducts[i] = ItemStack.loadItemStackFromNBT(byproductsTag.getCompoundTagAt(i));

                if (byproducts[i] == null) {
                    return null;
                }
            }
        }

        return new CraftingPattern(cx, cy, cz, cd, processing, inputs, outputs, byproducts);
    }
}
