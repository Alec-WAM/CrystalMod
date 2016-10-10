package com.alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class ItemPipeCover extends Item {
	public static final ArrayList<ItemStack> allCovers = new ArrayList<ItemStack>();
    public static final ArrayList<String> allCoverIDs = new ArrayList<String>();
    public static final ArrayList<String> blacklistedCovers = new ArrayList<String>();
    
    public static final Map<ItemStack, ItemStack> coverRecipes = Maps.newHashMap();

    public ItemPipeCover(){
    	super();
    	this.setCreativeTab(CrystalMod.tabCovers);
    	ModItems.registerItem(this, "pipecover");
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        CoverData data = getCoverData(itemstack);
        String displayName = getCoverDataDisplayName(data);
        return super.getItemStackDisplayName(itemstack) + ": " + displayName;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
    	CoverData data = getCoverData(stack);
        if (data != null && data.getBlockState() != null && Item.getItemFromBlock(data.getBlockState().getBlock()) != null) {
            Item.getItemFromBlock(data.getBlockState().getBlock()).addInformation(new ItemStack(data.getBlockState().getBlock(), 1, data.getBlockState().getBlock()
                        .getMetaFromState(data.getBlockState())), player, list, debug);
        }
    }
    
    public static String getCoverDataDisplayName(CoverData data) {
        if (data == null || data.getBlockState() == null || Item.getItemFromBlock(data.getBlockState().getBlock()) == null) {
            return "";
        }
        String s = new ItemStack(data.getBlockState().getBlock(), 1, data.getBlockState().getBlock().getMetaFromState(data.getBlockState())).getDisplayName();
        return s;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        for (ItemStack stack : allCovers) {
            itemList.add(stack);
        }
    }
    
    public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player)
    {
    	if(world.getTileEntity(pos) !=null && world.getTileEntity(pos) instanceof TileEntityPipe)return true;
        return false;
    }
    
    public void initialize() {
        for (Object o : Block.REGISTRY) {
            Block b = (Block) o;

            if (!isBlockValidForCover(b)) {
                continue;
            }

            Item item = Item.getItemFromBlock(b);

            if (item == null) {
                continue;
            }

            if (isBlockBlacklisted(b)) {
                continue;
            }

            registerValidCovers(b, item);
        }
    }

    private void registerValidCovers(Block block, Item item) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(16);
        try {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                for (CreativeTabs ct : item.getCreativeTabs()) {
                    block.getSubBlocks(item, ct, stacks);
                }
            } else {
                for (int i = 0; i < 16; i++) {
                    stacks.add(new ItemStack(item, 1, i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ItemStack stack : stacks) {
            try {
                if (block.hasTileEntity(block.getDefaultState())) continue;

                // Check if all of these functions work correctly.
                // If an exception is fired, or null is returned, this generally means that
                // this block is invalid.
                try {
                    if (stack.getDisplayName() == null || Strings.isNullOrEmpty(stack.getUnlocalizedName())) continue;
                } catch (Throwable t) {
                    continue;
                }

                addCover(stack);
            } catch (IndexOutOfBoundsException e) {

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void addCover(ItemStack itemStack) {
        if (itemStack.stackSize == 0) itemStack.stackSize = 1;

        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == null) return;

        String recipeId = "crystalmod:cover{" + Util.getNameForBlock(block) + "#" + itemStack.getItemDamage() + "}";

        @SuppressWarnings("deprecation")
		ItemStack cover = getCoverForBlock(block.getStateFromMeta(itemStack.getItemDamage()));

        if (!allCoverIDs.contains(recipeId)) {
        	allCoverIDs.add(recipeId);
            allCovers.add(cover);
            coverRecipes.put(cover, itemStack);
        }
    }
    
    public static ItemStack getCoverForBlock(IBlockState state) {
        return getCover(new CoverData(state));
    }

    public static ItemStack getCover(CoverData cover) {
        if (cover == null) return null;

        ItemStack stack = new ItemStack(ModItems.pipeCover);

        NBTTagCompound nbt = new NBTTagCompound();
        cover.writeToNBT(nbt);
        ItemNBTHelper.getCompound(stack).setTag("cover", nbt);
        return stack;
    }
    
    private static boolean isBlockBlacklisted(Block block) {
        String blockName = Util.getNameForBlock(block);

        if (blockName == null) return true;

        // Blocks blacklisted by mods should always be treated as blacklisted
        for (String blacklistedBlock : blacklistedCovers)
            if (blockName.equals(blacklistedBlock)) return true;

        // Blocks blacklisted by config should depend on the config settings
        /*for (String blacklistedBlock : BuildCraftTransport.facadeBlacklist) {
            if (blockName.equals(JavaTools.stripSurroundingQuotes(blacklistedBlock))) return true
                ^ BuildCraftTransport.facadeTreatBlacklistAsWhitelist;
        }

        return false ^ BuildCraftTransport.facadeTreatBlacklistAsWhitelist;*/
        return false;
    }

    @SuppressWarnings("deprecation")
	private static boolean isBlockValidForCover(Block block) {
        try {
        	IBlockState state = block.getDefaultState();
        	if((!block.isFullCube(state) && block.getMaterial(state) == Material.GLASS))return true;
            if (!block.isFullBlock(state) || (!block.isFullCube(state)) || block.hasTileEntity(block.getDefaultState())) return false;
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static CoverData getCoverData(ItemStack stack) {
        if (!stack.hasTagCompound() || !ItemNBTHelper.verifyExistance(stack, "cover")) return null;
        NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompoundTag("cover");
        return CoverData.readFromNBT(nbt);
    }
    
}
