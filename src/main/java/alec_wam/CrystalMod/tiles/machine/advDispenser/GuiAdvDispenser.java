package alec_wam.CrystalMod.tiles.machine.advDispenser;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.ClickType;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.HandType;
import alec_wam.CrystalMod.tiles.machine.advDispenser.TileAdvDispenser.InteractType;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
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
    	String interact = "";
    	if(dispenser !=null){
    		interact = dispenser.interact.name();
    	}
    	this.buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 10, 20, 10, interact));
    	String click = "";
    	if(dispenser !=null){
    		click = dispenser.click.name();
    	}
    	this.buttonList.add(new GuiButton(1, guiLeft + 40, guiTop + 10, 20, 10, click));
    	String redstone = "";
    	if(dispenser !=null){
    		redstone = dispenser.redstone.name();
    	}
    	this.buttonList.add(new GuiButton(2, guiLeft + 70, guiTop + 10, 20, 10, redstone));
    	String sneak = "";
    	if(dispenser !=null){
    		sneak = dispenser.isSneaking ? "Sneaking" : "Normal";
    	}
    	this.buttonList.add(new GuiButton(3, guiLeft + 100, guiTop + 10, 20, 10, sneak));
    	String hand = "";
    	if(dispenser !=null){
    		hand = dispenser.hand.name();
    	}
    	this.buttonList.add(new GuiButton(4, guiLeft + 130, guiTop + 10, 20, 10, hand));
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
        //String s = this.dispenserInventory.getDisplayName().getUnformattedText();
        //this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
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