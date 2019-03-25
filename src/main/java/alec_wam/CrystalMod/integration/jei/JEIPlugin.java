package alec_wam.CrystalMod.integration.jei;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.crafting.CrystalCraftingManager;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.CrystalWorkbenchCategory;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapedCrystalOreRecipeHandler;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapedCrystalRecipeHandler;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapedNBTCrystalRecipeHandler;
import alec_wam.CrystalMod.integration.jei.crystalworkbench.ShapelessCrystalRecipeHandler;
import alec_wam.CrystalMod.integration.jei.customrecipe.DNASampleCategory;
import alec_wam.CrystalMod.integration.jei.customrecipe.PipeCoverCategory;
import alec_wam.CrystalMod.integration.jei.machine.FluidMixerRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.GrinderRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.InfuserRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.LiquidizerRecipeCategory;
import alec_wam.CrystalMod.integration.jei.machine.PressRecipeCategory;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.lamps.BlockFakeLight.LightType;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.ContainerCrystalFurnace;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.BlockPanel.PanelType;
import alec_wam.CrystalMod.tiles.workbench.ContainerCrystalWorkbench;
import alec_wam.CrystalMod.tiles.workbench.GuiCrystalWorkbench;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

@mezz.jei.api.JEIPlugin
public class JEIPlugin  extends BlankModPlugin {
	
	@Override
	public void register(@Nonnull IModRegistry registry) {
		RecipeTransferHandler helper = new RecipeTransferHandler(registry);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(helper, VanillaRecipeCategoryUid.CRAFTING);
		RecipeTransferEncoder helperEncoder = new RecipeTransferEncoder(registry);
		registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(helperEncoder);
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registry.addRecipeCategories(new CrystalWorkbenchCategory(guiHelper));
		
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		registry.addRecipeHandlers(
				new ShapedCrystalRecipeHandler(jeiHelpers),
				new ShapedNBTCrystalRecipeHandler(jeiHelpers),
				new ShapedCrystalOreRecipeHandler(jeiHelpers),
				new ShapelessCrystalRecipeHandler(jeiHelpers)
		);

		registry.addRecipes(CrystalCraftingManager.getInstance().getRecipeList());

		PressRecipeCategory.register(jeiHelpers, registry, guiHelper);
		CauldronRecipeCategory.register(jeiHelpers, registry, guiHelper);
		LiquidizerRecipeCategory.register(jeiHelpers, registry, guiHelper);
		GrinderRecipeCategory.register(jeiHelpers, registry, guiHelper);
		InfuserRecipeCategory.register(jeiHelpers, registry, guiHelper);
		FusionRecipeCategory.register(jeiHelpers, registry, guiHelper);
		FluidMixerRecipeCategory.register(jeiHelpers, registry, guiHelper);
		PipeCoverCategory.register(jeiHelpers, registry, guiHelper);
		DNASampleCategory.register(jeiHelpers, registry, guiHelper);
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalWorkbench), CrystalModRecipeUids.WORKBENCH);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalWorkbench), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.storagePanel, 1, PanelType.CRAFTING.getMeta()), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()), VanillaRecipeCategoryUid.SMELTING);
		
		registry.addRecipeClickArea(GuiCrystalWorkbench.class, 88, 32, 28, 23, CrystalModRecipeUids.WORKBENCH);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalWorkbench.class, CrystalModRecipeUids.WORKBENCH, 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrystalFurnace.class, VanillaRecipeCategoryUid.SMELTING, 36, 1, 0, 35);
		
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.materialCrop));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalReedsBlue));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalReedsRed));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalReedsGreen));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalReedsDark));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalPlant));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalTreePlantBlue));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalTreePlantRed));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalTreePlantGreen));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystalTreePlantDark));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.glowBerryBlue));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.glowBerryRed));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.glowBerryGreen));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.glowBerryDark));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.corn));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.cubeBlock));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.cubeCore));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.bambooDoor));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.fakeLight, 1, LightType.LIGHT.getMeta()));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.fakeLight, 1, LightType.DARK.getMeta()));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.crystexPortal));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.backpackNormal));
		jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(ModBlocks.backpackCrafting));

		String desc = "Dropped upon killing an Ender Dragon.";
		//Lang.localize("jei.desc.wings");
		registry.addDescription(new ItemStack(ModItems.wings), desc);
		
		registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerCrystalMod());
		registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerPanel());
	}
	
	public static IJeiRuntime runtime;
	
	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){
		runtime = jeiRuntime;
	}
	
}
