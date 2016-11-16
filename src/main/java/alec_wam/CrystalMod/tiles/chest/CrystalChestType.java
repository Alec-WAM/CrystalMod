package alec_wam.CrystalMod.tiles.chest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.ItemIngot.IngotType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public enum CrystalChestType implements IStringSerializable
{
    BLUE(54, 9, true, "Blue Crystal Chest", "bluechest.png", 0, Arrays.asList("ingotBlue"), TileEntityBlueCrystalChest.class, "mmmmCmmmm"),
    RED(72, 9, true, "Red Crystal Chest", "redchest.png", 1, Arrays.asList("ingotRed"), TileEntityRedCrystalChest.class, "mmmm0mmmm"),
    GREEN(81, 9, true, "Green Crystal Chest", "greenchest.png", 2, Arrays.asList("ingotGreen"), TileEntityGreenCrystalChest.class, "mmmm1mmmm"),
    DARK(108, 12, true, "Dark Crystal Chest", "darkchest.png", 3, Arrays.asList("ingotDark"), TileEntityDarkCrystalChest.class, "mmmm2mmmm"),
    PURE(108, 12, true, "Pure Crystal Chest", "purechest.png", 4, Arrays.asList("ingotPure"), TileEntityPureCrystalChest.class, "mmmm3mmmm"),
    DARKIRON(45, 9, false, "Dark Iron Chest", "dironchest.png", 5, Arrays.asList("ingotDarkIron"), TileEntityDarkIronChest.class, "mmmmCmmmm");
    public int size;
    private int rowLength;
    public String friendlyName;
    private boolean tieredChest;
    private String modelTexture;
    private int textureRow;
    public Class<? extends TileEntityBlueCrystalChest> clazz;
    private String[] recipes;
    private ArrayList<String> matList;
    private Item itemFilter;

    CrystalChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture, int textureRow, List<String> mats,
            Class<? extends TileEntityBlueCrystalChest> clazz, String... recipes)
    {
        this(size, rowLength, tieredChest, friendlyName, modelTexture, textureRow, mats, clazz, (Item)null, recipes);
    }
    CrystalChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture, int textureRow, List<String> mats,
            Class<? extends TileEntityBlueCrystalChest> clazz, Item itemFilter, String... recipes)
    {
        this.size = size;
        this.rowLength = rowLength;
        this.tieredChest = tieredChest;
        this.friendlyName = friendlyName;
        this.modelTexture = modelTexture;
        this.textureRow = textureRow;
        this.clazz = clazz;
        this.itemFilter = itemFilter;
        this.recipes = recipes;
        this.matList = new ArrayList<String>();
        matList.addAll(mats);
    }
    
    @Override
    public String getName()
    {
        return name().toLowerCase();
    }

    public String getModelTexture()
    {
        return modelTexture;
    }

    public int getTextureRow()
    {
        return textureRow;
    }

    public static TileEntityBlueCrystalChest makeEntity(int metadata)
    {
        // Compatibility
        int chesttype = validateMeta(metadata);
        if (chesttype == metadata)
        {
            try
            {
                TileEntityBlueCrystalChest te = values()[chesttype].clazz.newInstance();
                return te;
            }
            catch (InstantiationException e)
            {
                // unpossible
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // unpossible
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void registerBlocksAndRecipes(BlockCrystalChest blockResult)
    {
        Object previous = "chestWood";
        for (CrystalChestType typ : values())
        {
            generateRecipesForType(blockResult, previous, typ);
            ItemStack chest = new ItemStack(blockResult, 1, typ.ordinal());
            if (typ.tieredChest) previous = chest;
        }
    }

    public static void generateRecipesForType(BlockCrystalChest blockResult, Object previousTier, CrystalChestType type)
    {
        for (String recipe : type.recipes)
        {
            String[] recipeSplit = new String[] { recipe.substring(0, 3), recipe.substring(3, 6), recipe.substring(6, 9) };
            Object mainMaterial = null;
            for (String mat : type.matList)
            {
                mainMaterial = translateOreName(mat);
                ItemStack chestStack = new ItemStack(blockResult, 1, type.ordinal());
                addRecipe(chestStack, recipeSplit,
                        'm', mainMaterial, 'P', previousTier, /* previous tier of chest */
                        'G', "blockGlass", 'C', "chestWood",
                        '0', new ItemStack(blockResult, 1, 0), /* Blue Chest */
                        '1', new ItemStack(blockResult, 1, 1), /* Red Chest */
                        '2', new ItemStack(blockResult, 1, 2), /* Green Chest */
                        '3', new ItemStack(blockResult, 1, 3), /* Dark Chest */
                        '4', new ItemStack(blockResult, 1, 4)  /* Pure Chest */
                );
                addRecipe(new ItemStack(ModItems.chestMinecart, 1, type.ordinal()), 
                		"C", "M",
                        'C', chestStack, 'M', Items.MINECART
                );
                addRecipe(new ItemStack(ModItems.chestMinecart, 1, type.ordinal()), 
                		"III", "IMI", "III",
                        'I', mainMaterial, 'M', getPreviousMinecart(type) /* previous tier of minecart */
                );
            }
        }
    }

    public static Object getPreviousMinecart(CrystalChestType type){
    	if(type == CrystalChestType.BLUE || !type.tieredChest){
    		return Items.CHEST_MINECART;
    	}
    	return new ItemStack(ModItems.chestMinecart, 1, type.ordinal()-1);
    }
    
    public static Object translateOreName(String mat)
    {
    	if(mat.equalsIgnoreCase("ingotBlue")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.BLUE.getMetadata());
    	}
    	else if(mat.equalsIgnoreCase("ingotRed")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.RED.getMetadata());
    	}
    	else if(mat.equalsIgnoreCase("ingotGreen")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.GREEN.getMetadata());
    	}
    	else if(mat.equalsIgnoreCase("ingotDark")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.DARK.getMetadata());
    	}
    	else if(mat.equalsIgnoreCase("ingotPure")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.PURE.getMetadata());
    	}
    	else if(mat.equalsIgnoreCase("ingotDarkIron")){
    		return new ItemStack(ModItems.ingots, 1, IngotType.DARK_IRON.getMetadata());
    	}
    	else if (mat.equals("obsidian"))
        {
            return Blocks.OBSIDIAN;
        }
        else if (mat.equals("dirt"))
        {
            return Blocks.DIRT;
        }
        return mat;
    }

    public static void addRecipe(ItemStack is, Object... parts)
    {
        ShapedOreRecipe oreRecipe = new ShapedOreRecipe(is, parts);
        GameRegistry.addRecipe(oreRecipe);
    }

    public int getRowCount()
    {
        return size / rowLength;
    }

    public int getRowLength()
    {
        return rowLength;
    }

    public boolean isTransparent()
    {
        return this == PURE;
    }

    public List<String> getMatList()
    {
        return matList;
    }

    public static int validateMeta(int i)
    {
        if (i < values().length && values()[i].size > 0)
        {
            return i;
        }
        else
        {
            return 0;
        }
    }

    public boolean isValidForCreativeMode()
    {
        return validateMeta(ordinal()) == ordinal();
    }

    public boolean isExplosionResistant()
    {
        return this == PURE;
    }

    public Slot makeSlot(IInventory chestInventory, int index, int x, int y)
    {
        return new Slot(chestInventory, index, x, y);
    }

    public boolean acceptsStack(ItemStack itemstack)
    {
        return itemFilter == null || itemstack == null || itemstack.getItem() == itemFilter;
    }
    
    public void adornItemDrop(ItemStack item)
    {
        
    }
}