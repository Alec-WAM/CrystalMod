package alec_wam.CrystalMod.integration.jei;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.crafting.CrystalCraftingManager;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.CrystalWorkbenchCategory;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapedCrystalRecipeHandler;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapelessCrystalRecipeHandler;
import alec_wam.CrystalMod.integration.jei.machine.InfuserRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.LiquidizerRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.PressRecipeCategory;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.ContainerCrystalFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.press.ContainerPress;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.workbench.ContainerCrystalWorkbench;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@mezz.jei.api.JEIPlugin
public class JEIPlugin  extends BlankModPlugin {
	
	@Override
	public void register(@Nonnull IModRegistry registry) {
		RecipeTransferHandler helper = new RecipeTransferHandler(registry);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(helper);
		RecipeTransferEncoder helperEncoder = new RecipeTransferEncoder(registry);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(helperEncoder);
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new CrystalWorkbenchCategory(guiHelper));
		
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		registry.addRecipeHandlers(
				new ShapedCrystalRecipeHandler(jeiHelpers),
				new ShapelessCrystalRecipeHandler(jeiHelpers)
		);

		registry.addRecipes(CrystalCraftingManager.getInstance().getRecipeList());

		PressRecipeCategory.register(registry, guiHelper);
		LiquidizerRecipeCategory.register(registry, guiHelper);
		InfuserRecipeCategory.register(registry, guiHelper);
		
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalWorkbench), CrystalModRecipeUids.WORKBENCH);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalWorkbench), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.storagePanel, 1, PanelType.CRAFTING.getMeta()), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()), VanillaRecipeCategoryUid.SMELTING);
		
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalWorkbench.class, CrystalModRecipeUids.WORKBENCH, 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalFurnace.class, VanillaRecipeCategoryUid.SMELTING, 36, 1, 0, 35);
		//recipeTransferRegistry.addRecipeTransferHandler(ContainerPanelCrafting.class, VanillaRecipeCategoryUid.CRAFTING, 37, 9, 0, 35);
		//recipeTransferRegistry.addRecipeTransferHandler(ContainerBackpackCrafting.class, VanillaRecipeCategoryUid.CRAFTING, BackpackUtils.MAIN_SIZE, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE-1, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE, BackpackUtils.MAIN_SIZE+BackpackUtils.CRAFTING_SIZE+26);
		
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalReeds));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalPlantBlue));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalPlantRed));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalPlantGreen));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalPlantDark));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalTreePlantBlue));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalTreePlantRed));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalTreePlantGreen));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.crystalTreePlantDark));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.cubeBlock));
		jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.cubeCore));
		
		registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerCrystalMod());
	}
	
	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){
		
	}
	
}
