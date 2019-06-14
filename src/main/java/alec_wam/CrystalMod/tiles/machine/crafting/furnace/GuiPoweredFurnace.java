package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import alec_wam.CrystalMod.client.gui.ElementDualScaled;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed;
import alec_wam.CrystalMod.tiles.machine.crafting.CraftingMachineIOTab;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiPoweredFurnace extends GuiContainerTabbed<ContainerPoweredFurnace> implements IRecipeShownListener {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/powered_furnace.png");
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public TileEntityPoweredFurnace tileFurnace;
	ElementDualScaled progress;
	ElementDualScaled speed;
	public final GuiPoweredFurnaceRecipeBook recipeBook = new GuiPoweredFurnaceRecipeBook();
	private boolean canRenderRecipeBook;
	public GuiPoweredFurnace(int windowId, PlayerEntity player, TileEntityPoweredFurnace tileFurnace)
    {
        super(new ContainerPoweredFurnace(windowId, player, tileFurnace), player.inventory, new TranslationTextComponent("block.crystalmod.machine_furnace"), TEXTURE);

        this.tileFurnace = tileFurnace;
        this.name = Lang.translateToLocal("block.crystalmod.machine_furnace");
    }
	
	@Override
	public void init(){
		super.init();
		tabManager.getTabs().clear();
		tabManager.add(new CraftingMachineIOTab(tileFurnace));
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileFurnace.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/progress_arrow_right.png", 48, 16)));
	    this.speed = ((ElementDualScaled)addElement(new ElementDualScaled(this, 56, 44).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/scale_flame_cu.png", 32, 16)));
	    
	    this.canRenderRecipeBook = this.width < 379;
	    this.recipeBook.func_201520_a(this.width, this.height, this.minecraft, this.canRenderRecipeBook, this.field_147002_h);
	    this.guiLeft = this.recipeBook.updateScreenPosition(this.canRenderRecipeBook, this.width, this.xSize);
	    this.addButton(new ImageButton(this.guiLeft + 28, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (p_214087_1_) -> {
	         this.recipeBook.func_201518_a(this.canRenderRecipeBook);
	         this.recipeBook.toggleVisibility();
	         this.guiLeft = this.recipeBook.updateScreenPosition(this.canRenderRecipeBook, this.width, this.xSize);
	         ((ImageButton)p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
	      }));
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    if(progress !=null)this.progress.setQuantity(this.tileFurnace.getScaledProgress(24));
	    if(speed !=null)this.speed.setQuantity(this.tileFurnace.getScaledSpeed(16));
	}

	//RecipeBook stuff
	
	@Override
	public void tick() {
		super.tick();
		this.recipeBook.tick();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.recipeBook.isVisible() && this.canRenderRecipeBook) {
			this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
			this.recipeBook.render(mouseX, mouseY, partialTicks);
		} else {
			this.recipeBook.render(mouseX, mouseY, partialTicks);
			super.render(mouseX, mouseY, partialTicks);
			this.recipeBook.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
		}
		this.recipeBook.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (this.recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
			return true;
		} else {
			return this.canRenderRecipeBook && this.recipeBook.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		}
	}
	
	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
		this.recipeBook.slotClicked(slotIn);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		return this.recipeBook.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
		boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
		return this.recipeBook.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
	}

	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		return this.recipeBook.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
	}

	@Override
	public void recipesUpdated() {
		this.recipeBook.recipesUpdated();
	}

	@Override
	public RecipeBookGui func_194310_f() {
		return this.recipeBook;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override
	public void onClose() {
		this.recipeBook.removed();
		super.onClose();
	}
}
