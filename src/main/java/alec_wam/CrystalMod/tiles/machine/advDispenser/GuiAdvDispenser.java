package alec_wam.CrystalMod.tiles.machine.advDispenser;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.GuiButtonCustomIcon;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.ClickType;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.HandType;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.InteractType;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvDispenser extends GuiContainer
{
    private static final ResourceLocation DISPENSER_GUI_TEXTURES = new ResourceLocation("crystalmod:textures/gui/machine/advdispenser.png");
    private final InventoryPlayer playerInventory;
    public TileAdvDispenser dispenser;

    public GuiAdvDispenser(InventoryPlayer playerInv, TileAdvDispenser dispenser)
    {
        super(new ContainerAdvDispenser(playerInv, dispenser));
        this.playerInventory = playerInv;
        this.dispenser = dispenser;
    }

    @Override
    public void initGui(){
    	super.initGui();
    	this.refreshButtons();
    }
    
    public void refreshButtons(){
    	this.buttonList.clear();
    	int interactY = 0;
    	if(dispenser !=null){
    		interactY = dispenser.interact.ordinal() * 16;
    	}
    	this.buttonList.add(new GuiButtonCustomIcon(0, guiLeft + 62, guiTop + 8, 1.0F, 240, interactY, DISPENSER_GUI_TEXTURES));
    	String click = "";
    	if(dispenser !=null){
    		click = dispenser.click == ClickType.LEFT ? "L" : "R";
    	}
    	this.buttonList.add(new GuiButton(1, guiLeft + 84, guiTop + 8, 20, 20, click));
    	
    	int[][] iconsRedstone = new int[RedstoneMode.values().length][2];
    	iconsRedstone[RedstoneMode.NONE.ordinal()] = new int[]{0, 0};
    	iconsRedstone[RedstoneMode.IGNORE.ordinal()] = new int[]{0, 16};
    	iconsRedstone[RedstoneMode.OFF.ordinal()] = new int[]{0, 32};
    	iconsRedstone[RedstoneMode.ON.ordinal()] = new int[]{0, 48};
    	this.buttonList.add(new GuiButtonCustomIcon(2, guiLeft + 106, guiTop + 8, 1.0F, iconsRedstone[dispenser.redstone.ordinal()][0], iconsRedstone[dispenser.redstone.ordinal()][1], new ResourceLocation("crystalmod:textures/gui/icons.png")));
		
    	
    	String sneak = "";
    	if(dispenser !=null){
    		sneak = dispenser.isSneaking ? "s" : "S";
    	}
    	this.buttonList.add(new GuiButton(3, guiLeft + 128, guiTop + 8, 20, 20, sneak));
    	String hand = "";
    	if(dispenser !=null){
    		hand = dispenser.hand == HandType.MAIN ? "M" : dispenser.hand == HandType.BOTH ? "B" : "O";
    	}
    	this.buttonList.add(new GuiButton(4, guiLeft + 150, guiTop + 8, 20, 20, hand));
    }
    
    @Override
    public void actionPerformed(GuiButton button){
    	//Interact
    	if(button.id == 0){
    		int next = dispenser.interact.ordinal();
    		if(next+1 < InteractType.values().length){
    			next++;
    		} else {
    			next = 0;
    		}
    		InteractType nextType = InteractType.values()[next];
    		dispenser.interact = nextType;
    		NBTTagCompound nbt = new NBTTagCompound();
    		nbt.setInteger("Interact", next);
    		CrystalModNetwork.sendToServer(new PacketTileMessage(dispenser.getPos(), "Settings", nbt));
    		refreshButtons();
    		return;
    	}
    	//Click
    	if(button.id == 1){
    		int next = dispenser.click.ordinal();
    		if(next+1 < ClickType.values().length){
    			next++;
    		} else {
    			next = 0;
    		}
    		ClickType nextType = ClickType.values()[next];
    		dispenser.click = nextType;
    		NBTTagCompound nbt = new NBTTagCompound();
    		nbt.setInteger("Click", next);
    		CrystalModNetwork.sendToServer(new PacketTileMessage(dispenser.getPos(), "Settings", nbt));
    		refreshButtons();
    		return;
    	}
    	//Redstone
    	if(button.id == 2){
    		RedstoneMode nextType = dispenser.redstone.next();
    		dispenser.redstone = nextType;
    		NBTTagCompound nbt = new NBTTagCompound();
    		nbt.setInteger("Redstone", nextType.ordinal());
    		CrystalModNetwork.sendToServer(new PacketTileMessage(dispenser.getPos(), "Settings", nbt));
    		refreshButtons();
    		return;
    	}
    	//Sneaking
    	if(button.id == 3){
    		boolean old = dispenser.isSneaking;
    		boolean newSneak = !old;
    		dispenser.isSneaking = newSneak;
    		NBTTagCompound nbt = new NBTTagCompound();
    		nbt.setBoolean("Sneaking", newSneak);
    		CrystalModNetwork.sendToServer(new PacketTileMessage(dispenser.getPos(), "Settings", nbt));
    		refreshButtons();
    		return;
    	}
    	//Hand
    	if(button.id == 4){
    		int next = dispenser.hand.ordinal();
    		if(next+1 < HandType.values().length){
    			next++;
    		} else {
    			next = 0;
    		}
    		HandType nextType = HandType.values()[next];
    		dispenser.hand = nextType;
    		NBTTagCompound nbt = new NBTTagCompound();
    		nbt.setInteger("Hand", next);
    		CrystalModNetwork.sendToServer(new PacketTileMessage(dispenser.getPos(), "Settings", nbt));
    		refreshButtons();
    		return;
    	}
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	//ModLogger.info("Mouse:" + mouseX + " " + mouseY);
    	for(GuiButton button : buttonList){
    		if(isPointInRegion(button.xPosition - guiLeft, button.yPosition - guiTop, button.width, button.height, mouseX, mouseY)){
    			if(button.id == 0){
    				List<String> list = Lists.newArrayList();
    				list.add(Lang.localizeFormat("gui.advdispenser.tooltip.interact", Lang.localize("gui.advdispenser.tooltip.interact." + dispenser.interact.name().toLowerCase())));
    				list.add(Lang.localize("gui.advdispenser.tooltip.interact." + dispenser.interact.name().toLowerCase()+".desc"));
    				this.drawHoveringText(list, mouseX, mouseY);
    			}
    			if(button.id == 1){
    				List<String> list = Lists.newArrayList();
    				list.add(Lang.localize("gui.advdispenser.tooltip.click"));
    				this.drawHoveringText(list, mouseX, mouseY);
    			}
    			if(button.id == 2){
    				List<String> list = Lists.newArrayList();
    				list.add(Lang.localizeFormat("gui.advdispenser.tooltip.redstone", Lang.localize("gui.redstone."+dispenser.redstone.name().toLowerCase())));
    				this.drawHoveringText(list, mouseX, mouseY);
    			}
    			if(button.id == 3){
    				List<String> list = Lists.newArrayList();
    				list.add(Lang.localize("gui.advdispenser.tooltip.stance." + (dispenser.isSneaking ? "sneak" : "normal")));
    				this.drawHoveringText(list, mouseX, mouseY);
    			}
    			if(button.id == 4){
    				List<String> list = Lists.newArrayList();
    				list.add(Lang.localizeFormat("gui.advdispenser.tooltip.hand", Lang.localize("gui.advdispenser.tooltip.hand."+dispenser.hand.name().toLowerCase())));
    				this.drawHoveringText(list, mouseX, mouseY);
    			}
    		}
    	}
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DISPENSER_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}