package alec_wam.CrystalMod.blocks.crops.material;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.crop.CropRecipe;
import alec_wam.CrystalMod.api.crop.SpecialCropRecipe;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class ItemMaterialSeed extends Item implements ICustomModel {

	public ItemMaterialSeed(){
		super();
		this.setCreativeTab(CrystalMod.tabCrops);
		this.setMaxStackSize(16);
		ModItems.registerItem(this, "materialseed");
	}
	
	@Override
	public void initModel(){
		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ModelSeed.LOCATION;
            }
        });
        ModelBakery.registerItemVariants(this, ModelSeed.LOCATION);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(facing != EnumFacing.UP){
			return EnumActionResult.PASS; 
		}
		BlockPos up = pos.up();
		//Permission Check
		ItemStack stack = playerIn.getHeldItem(hand);
		if(playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(up, facing, stack)){
			IBlockState state = worldIn.getBlockState(pos);
			if(state.isSideSolid(worldIn, pos, facing) && worldIn.isAirBlock(up)){
				worldIn.setBlockState(up, ModBlocks.materialCrop.getDefaultState(), 3);
				TileEntity tile = worldIn.getTileEntity(up);
				if(tile !=null && tile instanceof TileMaterialCrop){
					TileMaterialCrop crop = (TileMaterialCrop)tile;
					crop.setCrop(getCrop(stack));
					if(!playerIn.capabilities.isCreativeMode){
						ItemStackTools.incStackSize(stack, -1);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
        return EnumActionResult.PASS;
    }
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(Entry<String, IMaterialCrop> entry : CrystalModAPI.getCropMap().entrySet()){
			list.add(getSeed(entry.getValue()));
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
		IMaterialCrop crop = getCrop(stack);
		if (crop != null) {
			list.add(CrystalModAPI.localizeCrop(crop));
			list.add(TextFormatting.GOLD + "Tier: " + crop.getSeedInfo().getTier());
			int secondsLeft = crop.getGrowthTime(null, null);
			int minutesLeft = secondsLeft / 60;
			int hoursLeft = minutesLeft / 60;
			int daysLeft = hoursLeft / 24;
			secondsLeft = secondsLeft % 60;
			minutesLeft = minutesLeft % 60;
			hoursLeft = hoursLeft % 24;
			String time = "";
			if (daysLeft > 0) {
				time = daysLeft + "d " + hoursLeft + "h " + minutesLeft + "m " + secondsLeft + "s";
			} else if (hoursLeft > 0) {
				time = hoursLeft + "h " + minutesLeft + "m " + secondsLeft + "s";
			} else if (minutesLeft > 0) {
				time = minutesLeft + "m " + secondsLeft + "s";
			} else if (secondsLeft > 0) {
				time = secondsLeft + "s";
			}
			list.add(TextFormatting.BLUE + "Grow Time: " + TextFormatting.DARK_PURPLE + time);

			CropRecipe recipe = CrystalModAPI.lookupRecipe(crop);
			if (recipe != null) {
				list.add("");
				list.add(TextFormatting.AQUA + "Recipe:");
				list.add(TextFormatting.YELLOW + recipe.getRecipe());
			}
			if (recipe == null) {
				SpecialCropRecipe recipe2 = CrystalModAPI.lookupSpecialRecipe(crop);
				if (recipe2 != null) {
					list.add("");
					list.add(TextFormatting.AQUA + "Recipe:");
					list.add(TextFormatting.YELLOW + recipe2.getRecipe());
				}
			}
		} else {
			list.add("Crops: " + CrystalModAPI.getCropMap().size());
		}
	}
	
	public static ItemStack getSeed(IMaterialCrop crop){
		ItemStack stack = new ItemStack(ModItems.materialSeed);
		if(stack !=null){
			if(crop == null){
				ItemNBTHelper.setString(stack, "Crop", "");
			}else{
				ItemNBTHelper.setString(stack, "Crop", crop.getUnlocalizedName());
			}
		}
		return stack;
	}
	
	public static void setCrop(ItemStack stack, IMaterialCrop crop){
		if(stack !=null){
			if(crop == null){
				ItemNBTHelper.setString(stack, "Crop", "");
			}else{
				ItemNBTHelper.setString(stack, "Crop", crop.getUnlocalizedName());
			}
		}
	}
	
	public static IMaterialCrop getCrop(ItemStack stack){
		if(!stack.hasTagCompound())return null;
		String name = ItemNBTHelper.getString(stack, "Crop", "");
		if(!Strings.isNullOrEmpty(name)){
			IMaterialCrop crop = CrystalModAPI.getCrop(name);
			if(crop !=null){
				return crop;
			}
		}
		return null;
	}
	
}
