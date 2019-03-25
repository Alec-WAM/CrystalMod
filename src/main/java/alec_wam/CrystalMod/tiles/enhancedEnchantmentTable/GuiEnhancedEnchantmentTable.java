package alec_wam.CrystalMod.tiles.enhancedEnchantmentTable;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class GuiEnhancedEnchantmentTable extends GuiContainer {

	final InventoryPlayer playerInv;
	final TileEntityEnhancedEnchantmentTable table;
	public List<Integer> selections = Lists.newArrayList();
	public GuiButton buttonTransfer;
	public int arrowKeyIndex = -1;
	public int cost = 0;
	public boolean displayTransferButton;
	
	public GuiEnhancedEnchantmentTable(EntityPlayer player, TileEntityEnhancedEnchantmentTable table) {
		super(new ContainerEnhancedEnchantmentTable(player.inventory, table));
		playerInv = player.inventory;
		this.table = table;
		this.ySize = 190;
	}	
	
	@Override
	public void initGui(){
		super.initGui();
		/*this.buttonList.clear();
		buttonTransfer = new GuiButton(0, guiLeft + 17, guiTop + 84, 60, 20, "Transfer");
		this.buttonList.add(buttonTransfer);*/
	}
	
	@Override
	public void updateScreen()
    {
        super.updateScreen();
        displayTransferButton = ItemStackTools.isValid(table.getStackInSlot(0)) && ItemStackTools.isValid(table.getStackInSlot(1)) && !selections.isEmpty() && cost > 0;
        boolean validSelections =
        		ItemStackTools.isValid(table.getStackInSlot(0));
        if(!validSelections && !selections.isEmpty()){
        	selections.clear();
        	cost = 0;
        }
	}
	
	public void calcCost(){
		Map<Enchantment, Integer> enchantments = ItemStackTools.isValid(table.getStackInSlot(0)) ? EnchantmentHelper.getEnchantments(table.getStackInSlot(0)) : Maps.newHashMap();
		if(enchantments.isEmpty()){
			cost = 0;
			return;
		}
		int totalCost = 0;
		@SuppressWarnings("unchecked")
		Entry<Enchantment, Integer>[] entries = (Entry<Enchantment, Integer>[]) enchantments.entrySet().toArray(new Entry[0]);
		for(int i : selections){
			Entry<Enchantment, Integer> entry = entries[i];
			Enchantment e = entry.getKey();
			int lvl = entry.getValue();
			totalCost+=e.getMinEnchantability(lvl);
		}
		cost = Math.max(totalCost/2, selections.isEmpty() ? 0 : 1);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
    {
		if(button == buttonTransfer){
			
		}
    }
	
	public void performTransfer(){
		int playerXP = this.mc.player.experienceLevel;
		boolean canAfford = this.mc.player.capabilities.isCreativeMode || playerXP >= cost;
		if(canAfford){
			if(!mc.player.capabilities.isCreativeMode){
				mc.player.removeExperienceLevel(cost);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Amount", cost);
				CrystalModNetwork.sendToServer(new PacketEntityMessage(mc.player, "#RemoveXP#", nbt));
			}
		    //playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
		    mc.world.playSound(table.getPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.6f, 0.8f, false);
			int[] currentSel = Util.convertToInt(selections);
			selections.clear();
			cost = 0;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setIntArray("Selections", currentSel);
			CrystalModNetwork.sendToServer(new PacketTileMessage(table.getPos(), "Transfer", nbt));
		}
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{ 
		int wheelMovement = Mouse.getEventDWheel();
		if (wheelMovement != 0) {
			Map<Enchantment, Integer> enchantments = ItemStackTools.isValid(table.getStackInSlot(0)) ? EnchantmentHelper.getEnchantments(table.getStackInSlot(0)) : Maps.newHashMap();
			if(wheelMovement < 0){
				if(!enchantments.isEmpty()){
					if(arrowKeyIndex < enchantments.size()-1){
						arrowKeyIndex++;
					}
				}
			}
			if(wheelMovement > 0){
				if(!enchantments.isEmpty()){
					if(arrowKeyIndex > 0){
						arrowKeyIndex--;
					}
				}
			}
		}
		super.handleMouseInput();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		Map<Enchantment, Integer> enchantments = ItemStackTools.isValid(table.getStackInSlot(0)) ? EnchantmentHelper.getEnchantments(table.getStackInSlot(0)) : Maps.newHashMap();
		if(keyCode == Keyboard.KEY_RETURN){
			if(arrowKeyIndex >= 0){
				int index = selections.indexOf(arrowKeyIndex);
				if(index == -1){
					selections.add(arrowKeyIndex);
				} else {
					selections.remove(index);
				}
				calcCost();
			}
		}
		if(keyCode == Keyboard.KEY_DOWN){
			if(!enchantments.isEmpty()){
				if(arrowKeyIndex < enchantments.size()-1){
					arrowKeyIndex++;
				}
			}
		}
		if(keyCode == Keyboard.KEY_UP){
			if(!enchantments.isEmpty()){
				if(arrowKeyIndex > 0){
					arrowKeyIndex--;
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}

	private int colorEnchantmentListBack = new Color(232, 230, 93).getRGB();
	private int colorEnchantmentListBack_hover = new Color(163, 162, 75).getRGB();
	private int colorEnchantmentListBorder = Color.BLACK.brighter().getRGB();
	private int colorEnchantmentListBorder_selected = Color.MAGENTA.darker().getRGB();
	
	public boolean isSelected(int index){
		return selections.contains(index);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
		if(this.isPointInRegion(38, 64, 20, 14, mouseX, mouseY) && displayTransferButton){
			performTransfer();
			return;
		}
		int x = 88;
		int y = 15;
		//Is not inside list, not inside inventory slots, and is not middle click
		if(!isPointInRegion(x, y, 80, 80, mouseX, mouseY) && isPointInRegion(5, 4, 167, 101, mouseX, mouseY) && mouseButton != 2){
			arrowKeyIndex = -1;
		}
		Map<Enchantment, Integer> enchantments = ItemStackTools.isValid(table.getStackInSlot(0)) ? EnchantmentHelper.getEnchantments(table.getStackInSlot(0)) : Maps.newHashMap();
		//Check if normal click or is middle click if we are scrolling
		if(!enchantments.isEmpty() && (mouseButton == 0 || (mouseButton == 2 && arrowKeyIndex >=0))){
			int start = arrowKeyIndex > 7 ? arrowKeyIndex-7 : 0;
			for(int i = start; i < start + (Math.min(enchantments.size() - start, 8)); i++){
				int offset = y + ((i - start) * 10);
				//Is inside list element or is a middle click during scroll
				if(isPointInRegion(x, offset, 80, 10, mouseX, mouseY) || (mouseButton == 2 && i == arrowKeyIndex)){
					int index = selections.indexOf(i);
					if(index == -1){
						selections.add(i);
					} else {
						selections.remove(index);
					}
					calcCost();
					return;
				} 
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		int x = 88;
		int y = 15;
		int colorText = Color.BLACK.getRGB();
		
		Map<Enchantment, Integer> enchantments = ItemStackTools.isValid(table.getStackInSlot(0)) ? EnchantmentHelper.getEnchantments(table.getStackInSlot(0)) : Maps.newHashMap();
		if(!enchantments.isEmpty()){
			int start = arrowKeyIndex > 7 ? arrowKeyIndex-7 : 0;
			for(int i = start; i < start + (Math.min(enchantments.size() - start, 8)); i++){
				@SuppressWarnings("unchecked")
				Entry<Enchantment, Integer> entry = (Entry<Enchantment, Integer>) enchantments.entrySet().toArray(new Entry[0])[i];
				int offset = y + ((i - start) * 10);
				Enchantment e = entry.getKey();
				boolean selected = isSelected(i);
				drawRect(x, offset, x+80, offset+10, selected ? colorEnchantmentListBorder_selected : colorEnchantmentListBorder);
				
				boolean hover = arrowKeyIndex > -1 ? arrowKeyIndex == i : isPointInRegion(x, offset, 80, 10, mouseX, mouseY);
				drawRect(x+1, offset+1, x+80-1, offset+10-1, hover ? colorEnchantmentListBack_hover : colorEnchantmentListBack);
				
				if(e !=null){
					String name = ItemUtil.getEnchantmentWithLevel(e, entry.getValue());
					GuiUtil.renderScaledText(name, fontRendererObj, x+3, offset+2, 13.0F, 0.8F, colorText);
				} else {
					fontRendererObj.drawString("ERROR", x+3, offset+1, colorText);
				}
			}
			
			if(start > 0){
				int selectionsAbove = 0;
				if(!selections.isEmpty()){
					for(int i : selections){
						if(i < start){
							selectionsAbove++;
						}
					}
				}
				String remaining = "+" + (start);
				fontRendererObj.drawString(remaining, 88, 5, colorText);
				String remainingSelected = "+" + (selectionsAbove);
				if(selectionsAbove > 0)fontRendererObj.drawString(remainingSelected, 102, 5, colorEnchantmentListBorder_selected);
			}
			
			if(enchantments.size() > start+8){
				int selectionsBelow = 0;
				if(!selections.isEmpty()){
					for(int i : selections){
						if(i > start+7){
							selectionsBelow++;
						}
					}
				}
				String remaining = "+" + (enchantments.size()-(start+8));
				fontRendererObj.drawString(remaining, 88, 97, colorText);
				String remainingSelected = "+" + (selectionsBelow);
				if(selectionsBelow > 0)fontRendererObj.drawString(remainingSelected, 102, 97, colorEnchantmentListBorder_selected);
			}
		}
		
		if(displayTransferButton){
			drawRect(38, 64, 58, 78, Color.GRAY.darker().getRGB());
			drawRect(39, 65, 57, 77, Color.GRAY.getRGB());
			int playerXP = this.mc.player.experienceLevel;
			boolean canAfford = this.mc.player.capabilities.isCreativeMode || playerXP >= cost;
			String cost = ""+this.cost;
			fontRendererObj.drawStringWithShadow(cost, 48 - (this.fontRendererObj.getStringWidth(cost) / 2), 67, canAfford ? 8453920 : 16736352);
		}
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	public final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/enhanced_enchantment_table.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
