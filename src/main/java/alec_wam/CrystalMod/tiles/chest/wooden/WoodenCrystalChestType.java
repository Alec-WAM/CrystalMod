package alec_wam.CrystalMod.tiles.chest.wooden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import alec_wam.CrystalMod.tiles.chest.TileEntityBlueCrystalChest;
import alec_wam.CrystalMod.tiles.chest.TileEntityDarkCrystalChest;
import alec_wam.CrystalMod.tiles.chest.TileEntityGreenCrystalChest;
import alec_wam.CrystalMod.tiles.chest.TileEntityRedCrystalChest;
import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public enum WoodenCrystalChestType implements IStringSerializable
{
    BLUE(54, 9, false, "Blue Wooden Crystal Chest", "bluewoodenchest.png", 0, Arrays.asList("plankBlue"), TileBlueWoodenCrystalChest.class, "mmmmCmmmm"),
    RED(72, 9, true, "Red Wooden Crystal Chest", "redwoodenchest.png", 1, Arrays.asList("plankRed"), TileRedWoodenCrystalChest.class, "mmmm0mmmm"),
    GREEN(81, 9, true, "Green Wooden Crystal Chest", "greenwoodenchest.png", 2, Arrays.asList("plankGreen"), TileGreenWoodenCrystalChest.class, "mmmm1mmmm"),
    DARK(108, 12, true, "Dark Wooden Crystal Chest", "darkwoodenchest.png", 3, Arrays.asList("plankDark"), TileDarkWoodenCrystalChest.class, "mmmm2mmmm");
    public int size;
    private int rowLength;
    public String friendlyName;
    private boolean tieredChest;
    private String modelTexture;
    private int textureRow;
    public Class<? extends TileWoodenCrystalChest> clazz;
    private String[] recipes;
    private ArrayList<String> matList;

    WoodenCrystalChestType(int size, int rowLength, boolean tieredChest, String friendlyName, String modelTexture, int textureRow, List<String> mats,
            Class<? extends TileWoodenCrystalChest> clazz, String... recipes)
    {
        this.size = size;
        this.rowLength = rowLength;
        this.tieredChest = tieredChest;
        this.friendlyName = friendlyName;
        this.modelTexture = modelTexture;
        this.textureRow = textureRow;
        this.clazz = clazz;
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

    public static TileWoodenCrystalChest makeEntity(int metadata)
    {
        // Compatibility
        int chesttype = validateMeta(metadata);
        if (chesttype == metadata)
        {
            try
            {
            	TileWoodenCrystalChest te = values()[chesttype].clazz.newInstance();
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

    public static void registerBlocksAndRecipes(BlockWoodenCrystalChest blockResult)
    {
        Object previous = "chestWood";
        for (WoodenCrystalChestType typ : values())
        {
            generateRecipesForType(blockResult, previous, typ);
            ItemStack chest = new ItemStack(blockResult, 1, typ.ordinal());
            if (typ.tieredChest) previous = chest;
        }
    }

    public static void generateRecipesForType(BlockWoodenCrystalChest blockResult, Object previousTier, WoodenCrystalChestType type)
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
                        '3', new ItemStack(blockResult, 1, 3) /* Dark Chest */
                );
            }
        }
    }
    
    public static Object translateOreName(String mat)
    {
    	if(mat.equalsIgnoreCase("plankBlue")){
    		return new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.BLUE.getMeta());
    	}
    	else if(mat.equalsIgnoreCase("plankRed")){
    		return new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.RED.getMeta());
    	}
    	else if(mat.equalsIgnoreCase("plankGreen")){
    		return new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.GREEN.getMeta());
    	}
    	else if(mat.equalsIgnoreCase("plankDark")){
    		return new ItemStack(ModBlocks.crystalPlanks, 1, WoodType.DARK.getMeta());
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

    public Slot makeSlot(IInventory chestInventory, int index, int x, int y)
    {
        return new Slot(chestInventory, index, x, y);
    }
}