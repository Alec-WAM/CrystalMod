package alec_wam.CrystalMod.tiles.pipes.covers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.CustomBakedModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPipeCover extends Item implements ICustomModel {
	public static final ArrayList<ItemStack> allCovers = new ArrayList<ItemStack>();
    public static final ArrayList<String> allCoverIDs = new ArrayList<String>();
    public static final ArrayList<String> blacklistedCovers = new ArrayList<String>();

    public ItemPipeCover(){
    	super();
    	this.setCreativeTab(CrystalMod.tabCovers);
    	ModItems.registerItem(this, "pipecover");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void initModel(){
    	ModItems.initBasicModel(this);
    	ClientProxy.registerCustomModel(new CustomBakedModel(new ModelResourceLocation(getRegistryName(), "inventory"), ModelCover.INSTANCE){
    		@Override
			public void preModelRegister(){
    			ModelCover.map.clear();
    		}
    	});
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
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> itemList) {
        for (ItemStack stack : allCovers) {
            itemList.add(stack);
        }
    }
    
    @Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
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

    @SuppressWarnings("deprecation")
	private void registerValidCovers(Block block, Item item) {
        NonNullList<ItemStack> stacks = NonNullList.create();
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
            	IBlockState state = block.getStateFromMeta(stack.getMetadata());
                if(!isValidForCover(state))continue;
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

    @SuppressWarnings("deprecation")
	public static boolean isItemValidForCover(ItemStack stack){
    	Item item = stack.getItem();
    	if(item instanceof ItemBlock){
    		ItemBlock iblock = (ItemBlock)stack.getItem();
    		Block block = iblock.getBlock();
    		return isValidForCover(block.getStateFromMeta(iblock.getMetadata(stack)));
    	}
    	return false;
    }
    
    @SuppressWarnings("deprecation")
	public static boolean isValidForCover(IBlockState state){
    	if (state.getBlock().hasTileEntity() || state.getBlock().hasTileEntity(state)) return false;
        if(!state.isFullCube()) return false;        
    	return true;
    }
    
    public void addCover(ItemStack itemStack) {
        if (ItemStackTools.isEmpty(itemStack)) ItemStackTools.setStackSize(itemStack, 1);

        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == null) return;

        String recipeId = "crystalmod:cover{" + Util.getNameForBlock(block) + "#" + itemStack.getItemDamage() + "}";

        @SuppressWarnings("deprecation")
		ItemStack cover = getCoverForBlock(block.getStateFromMeta(itemStack.getItemDamage()));

        if (!allCoverIDs.contains(recipeId)) {
        	allCoverIDs.add(recipeId);
            allCovers.add(cover);
        }
    }
    
    @SuppressWarnings("deprecation")
	public static ItemStack getCoverFromItem(ItemStack stack){
    	Item item = stack.getItem();
    	if(item instanceof ItemBlock){
    		ItemBlock iblock = (ItemBlock)stack.getItem();
    		return getCoverForBlock(iblock.getBlock().getStateFromMeta(iblock.getMetadata(stack)));
    	}
    	return ItemStackTools.getEmptyStack();
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
        	if(block == Blocks.AIR || block instanceof IShearable)return false;
        	IBlockState state = block.getDefaultState();
        	if (block.hasTileEntity() || block.hasTileEntity(block.getDefaultState())) return false;
            if(!state.isFullCube())return false;
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
    
    public static ItemStack getItemFromCover(ItemStack cover){
    	ItemStack stack = ItemStackTools.getEmptyStack();
    	CoverData data = getCoverData(cover);
    	if(data !=null){
    		IBlockState state = data.getBlockState();
    		return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
    	}
    	return stack;
    }
    
}
