package alec_wam.CrystalMod.tiles.pipes.item.filters;

import java.awt.Color;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiItemFilterNormal extends GuiContainer{

	public final ItemStack filter;
	public EnumHand hand;
	
	public GuiItemFilterNormal(EntityPlayer player, ItemStack filter, EnumHand hand){
		super(new ContainerItemFilterNormal(player, filter, hand));
		this.filter = filter;
		this.hand = hand;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
	}

	@Override
	public void actionPerformed(GuiButton button){
		boolean safe = ItemStackTools.isValid(filter);
		if(safe){
			ItemStack filterStack = filter;
			if(button.id == 0){
				boolean black = !ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
				ItemNBTHelper.setBoolean(filterStack, "BlackList", black);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("BlackList", black);
				sendPipeMessage(nbt);
				refreshButtons();
				return;
			}
			if(button.id == 1){
				boolean meta = !ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
				ItemNBTHelper.setBoolean(filterStack, "MetaMatch", meta);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Meta", meta);
				sendPipeMessage(nbt);
				refreshButtons();
				return;
			}
			if(button.id == 2){
				boolean nbtMatch = !ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
				ItemNBTHelper.setBoolean(filterStack, "NBTMatch", nbtMatch);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("NBTMatch", nbtMatch);
				sendPipeMessage(nbt);
				refreshButtons();
				return;
			}
			if(button.id == 3){
				boolean ore = !ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
				ItemNBTHelper.setBoolean(filterStack, "OreMatch", ore);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Ore", ore);
				sendPipeMessage(nbt);
				refreshButtons();
				return;
			}
		}
	}
	
	public void sendPipeMessage(NBTTagCompound nbt){
		CrystalModNetwork.sendToServer(new PacketGuiMessage("Settings", nbt));
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		boolean safe = ItemStackTools.isValid(filter);
		if(safe){
			ItemStack filterStack = filter;
			boolean black = ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
			this.buttonList.add(new GuiButton(0, sx+45+(45/2), sy+2, 45, 10, (black ? "Block" : "Allow")));
			if(filter.getMetadata() == FilterType.NORMAL.ordinal()){
				boolean meta = ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
				this.buttonList.add(new GuiButton(1, sx+45-(45/2), sy+50+12, 45, 12, (meta ? TextFormatting.GREEN : TextFormatting.RED)+"Meta"));
				boolean nbtMatch = ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
				this.buttonList.add(new GuiButton(2, sx+45+45+(45/2)-1, sy+50+12, 45, 12, (nbtMatch ? TextFormatting.GREEN : TextFormatting.RED)+"NBT"));
				boolean oreMatch = ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
				this.buttonList.add(new GuiButton(3, sx+45+(45/2), sy+50+12, 45, 12, (oreMatch ? TextFormatting.GREEN : TextFormatting.RED)+"Ore"));
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if(ItemStackTools.isValid(filter)){
			if(filter.getMetadata() == FilterType.MOD.ordinal()){
				FilterInventory inv = new FilterInventory(filter, 3, "");
				if(ItemStackTools.isValid(inv.getStackInSlot(0))){
					ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(0).getItem());
					String modIDInput = resourceInput.getResourceDomain();
					this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5, 0);
				}
				if(ItemStackTools.isValid(inv.getStackInSlot(1))){
					ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(1).getItem());
					String modIDInput = resourceInput.getResourceDomain();
					this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5+18, 0);
				}
				if(ItemStackTools.isValid(inv.getStackInSlot(2))){
					ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(inv.getStackInSlot(2).getItem());
					String modIDInput = resourceInput.getResourceDomain();
					this.fontRendererObj.drawString("Mod: "+modIDInput, (45+18+5), 14+5+36, 0);
				}
			}
		}
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    String type = "";
	    if(ItemStackTools.isValid(filter)){
	    	type = filter.getMetadata() == FilterType.MOD.ordinal() ? "filter_mod" : "filter";
		    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe_"+type+".png"));
		    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	    }
	}
}
