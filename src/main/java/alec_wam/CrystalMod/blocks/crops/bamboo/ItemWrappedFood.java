package alec_wam.CrystalMod.blocks.crops.bamboo;

import java.util.List;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWrappedFood extends ItemFood implements ICustomModel {

	public ItemWrappedFood(){
		super(0, 0.0F, false);
		this.setCreativeTab(CreativeTabs.FOOD);
		ModItems.registerItem(this, "wrappedfood");
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void initModel(){
		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return ModelWrappedFood.LOCATION;
            }
        });
        ModelBakery.registerItemVariants(this, ModelWrappedFood.LOCATION);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(WrappedFoodType type : WrappedFoodType.values()){
			ItemStack wrapped = new ItemStack(this);
			ItemNBTHelper.setString(wrapped, "Food", type.getName());
			list.add(wrapped);
		}
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
		if (!worldIn.isRemote)
        {
            player.heal(1.0F);
        }
		ItemStack foodStack = getFood(stack).getFoodStack();
		if(ItemStackTools.isValid(foodStack)){
			ItemFood food = (ItemFood) foodStack.getItem();
			try
	        {
	            ReflectionHelper.findMethod(ItemFood.class, "onFoodEaten", "func_77849_c", ItemStack.class, World.class, EntityPlayer.class).invoke(food, stack, worldIn, player);
	        }
	        catch (Exception e)
	        {
	            
	        }
		}
    }
	
	@Override
	public int getHealAmount(ItemStack stack)
    {
		ItemStack food = getFood(stack).getFoodStack();
		if(ItemStackTools.isValid(food)){
			if(food.getItem() instanceof ItemFood){
				return ((ItemFood)food.getItem()).getHealAmount(food);
			}
		}
        return 0;
    }

	@Override
	public float getSaturationModifier(ItemStack stack)
    {
		ItemStack food = getFood(stack).getFoodStack();
		if(ItemStackTools.isValid(food)){
			if(food.getItem() instanceof ItemFood){
				return ((ItemFood)food.getItem()).getSaturationModifier(food);
			}
		}
        return 0.0f;
    }
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
    {
		String foodName = "*Error*";
		ItemStack food = getFood(stack).getFoodStack();
		if(ItemStackTools.isValid(food)){
			foodName = food.getDisplayName();
		}
		return Lang.translateToLocalFormatted("item.crystalmod.wrappedfood.name", foodName);
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
		/*list.add(ItemNBTHelper.getString(stack, "Food", ""));
		ItemStack food = getFood(stack).getFoodStack();
		if(ItemStackTools.isValid(food)){
			list.add(food.getDisplayName());
		}*/
	}
	
	public static WrappedFoodType getFood(ItemStack wrapped){
		if(ItemStackTools.isValid(wrapped)){
			return WrappedFoodType.byName(ItemNBTHelper.getString(wrapped, "Food", ""));
		}
		return WrappedFoodType.APPLE;
	}
	
	public static void setFood(ItemStack stack, WrappedFoodType type){
		if(ItemStackTools.isValid(stack)){
			ItemNBTHelper.setString(stack, "Food", type.getUnlocalizedName());
		}
	}
	
	public static enum WrappedFoodType implements IStringSerializable, IEnumMetaItem
    {
		APPLE(Items.APPLE),
		BEEF_RAW(Items.BEEF),
		BEEF_COOKED(Items.COOKED_BEEF),
		BEETROOT(Items.BEETROOT),
		BREAD(Items.BREAD),
		CARROT(Items.CARROT),
		CHICKEN_RAW(Items.CHICKEN),
		CHICKEN_COOKED(Items.COOKED_CHICKEN),
		COOKIE(Items.COOKIE),
		FISH_RAW(Items.FISH, FishType.COD.getMetadata()),
		FISH_COOKED(Items.COOKED_FISH, FishType.COD.getMetadata()),
		SALMON_RAW(Items.FISH, FishType.SALMON.getMetadata()),
		SALMON_COOKED(Items.COOKED_FISH, FishType.SALMON.getMetadata()),
		WHITEFISH_RAW(ModItems.miscFood, FoodType.WHITE_FISH_RAW.getMetadata()),
		WHITEFISH_COOKED(ModItems.miscFood, FoodType.WHITE_FISH_COOKED.getMetadata()),
		MELON(Items.MELON),
		MUTTON_RAW(Items.MUTTON),
		MUTTON_COOKED(Items.COOKED_MUTTON),
		PORK_RAW(Items.PORKCHOP),
		PORK_COOKED(Items.COOKED_PORKCHOP),
		POTATO(Items.POTATO),
		POTATO_BAKED(Items.BAKED_POTATO),
		PUMPKIN_PIE(Items.PUMPKIN_PIE),
		RABBIT_RAW(Items.RABBIT),
		RABBIT_COOKED(Items.COOKED_RABBIT);
		
		private static final WrappedFoodType[] METADATA_LOOKUP = new WrappedFoodType[values().length];
        private final int metadata;
        private final String unlocalizedName;
        private final ItemStack itemStack;
        private final String foodName;

        private WrappedFoodType(Item item){
        	this(item, 0);
        }
        
        private WrappedFoodType(Item item, int meta){
        	this.metadata = ordinal();
            this.unlocalizedName = name().toLowerCase();
            this.itemStack = new ItemStack(item, 1, meta);
            this.foodName = ItemUtil.getStringForItemStack(itemStack, true, false);
        }
        
        private WrappedFoodType(String name, Item item){
        	this(name, ItemUtil.getStringForItemStack(new ItemStack(item), true, false));
        }
        
        private WrappedFoodType(String name, String foodName)
        {
            this.metadata = ordinal();
            this.unlocalizedName = name;
            this.foodName = foodName;
            this.itemStack = ItemUtil.getStackFromString(foodName, true);
        }

        @Override
		public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }
        
        public String getFoodName()
        {
            return this.foodName;
        }
        
        public ItemStack getFoodStack(){
        	return itemStack;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static WrappedFoodType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }
        
        public static WrappedFoodType byName(String name)
        {
        	for(WrappedFoodType t : WrappedFoodType.values()){
        		if(t.getName().equalsIgnoreCase(name)){
        			return t;
        		}
        	}
            return WrappedFoodType.APPLE;
        }

        @Override
		public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (WrappedFoodType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
