package alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageImport;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentIOType;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiAttachmantImport extends GuiContainer {

	private TileEntityPipeEStorage pipe;
	private EnumFacing dir;
	public GuiAttachmantImport(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
		super(new ContainerAttachmantImport(player, pipe, dir));
		this.pipe = pipe;
		this.dir = dir;
	}
	
	public AttachmentData getData(){
		return pipe.getAttachmentData(dir);
	}
	
	public AttachmentEStorageImport getImport(){
		if(this.getData() == null || !(getData() instanceof AttachmentEStorageImport))return null;
		return (AttachmentEStorageImport)getData();
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button.id == BUTTON_RED){
			final RedstoneMode next = RedstoneMode.getNextRedstoneMode(getImport().rMode);
			getImport().rMode = next;
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "RMode", dir, next.name()));
			refreshButtons();
			return;
		}
		boolean safe = getImport() !=null && !ItemStackTools.isNullStack(getImport().getFilter());
		if(safe){
			ItemStack filterStack = getImport().getFilter();
			if(filterStack.getMetadata() == FilterType.NORMAL.ordinal()){
				if(button.id == BUTTON_BW){
					boolean black = !ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
					ItemNBTHelper.setBoolean(filterStack, "BlackList", black);
					CrystalModNetwork.sendToServer(new PacketPipe(pipe, "FilterSetBlack", dir, ""+black));
					refreshButtons();
					return;
				}
				if(button.id == BUTTON_META){
					boolean meta = !ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
					ItemNBTHelper.setBoolean(filterStack, "MetaMatch", meta);
					CrystalModNetwork.sendToServer(new PacketPipe(pipe, "FilterSetMeta", dir, ""+meta));
					refreshButtons();
					return;
				}
				if(button.id == BUTTON_NBT){
					boolean nbtMatch = !ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
					ItemNBTHelper.setBoolean(filterStack, "NBTMatch", nbtMatch);
					CrystalModNetwork.sendToServer(new PacketPipe(pipe, "FilterSetNBTMatch", dir, ""+nbtMatch));
					refreshButtons();
					return;
				}
				if(button.id == BUTTON_ORE){
					boolean ore = !ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
					ItemNBTHelper.setBoolean(filterStack, "OreMatch", ore);
					CrystalModNetwork.sendToServer(new PacketPipe(pipe, "FilterSetOre", dir, ""+ore));
					refreshButtons();
					return;
				}
			}
		}

		if(button.id == BUTTON_IO){
			final AttachmentIOType next = getImport().ioType.getNext();
			getImport().ioType = next;
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "IOType", dir, next.name()));
			refreshButtons();
			return;
		}
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
	}
	
	public int BUTTON_RED = 0, BUTTON_BW = 1, BUTTON_META = 2, BUTTON_NBT = 3, BUTTON_ORE = 4, BUTTON_IO = 5;
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		if(getImport() == null)return;
		this.buttonList.add(new GuiButton(BUTTON_RED, sx+8+140, sy+10, 20, 20, getImport().rMode.name()));
		this.buttonList.add(new GuiButton(BUTTON_IO, sx+8+140, sy+40, 20, 20, getImport().ioType.name()));
		boolean safe = getImport() !=null && !ItemStackTools.isNullStack(getImport().getFilter());
		if(safe){
			ItemStack filterStack = getImport().getFilter();
			if(filterStack.getMetadata() == FilterType.NORMAL.ordinal()){
				boolean black = ItemNBTHelper.getBoolean(filterStack, "BlackList", false);
				this.buttonList.add(new GuiButton(BUTTON_BW, sx+45+(45/2), sy+2, 45, 10, (black ? "Block" : "Allow")));
				boolean meta = ItemNBTHelper.getBoolean(filterStack, "MetaMatch", true);
				this.buttonList.add(new GuiButton(BUTTON_META, sx+45-(45/2), sy+50+12, 45, 12, (meta ? TextFormatting.GREEN : TextFormatting.RED)+"Meta"));
				boolean nbtMatch = ItemNBTHelper.getBoolean(filterStack, "NBTMatch", true);
				this.buttonList.add(new GuiButton(BUTTON_NBT, sx+45+45+(45/2)-1, sy+50+12, 45, 12, (nbtMatch ? TextFormatting.GREEN : TextFormatting.RED)+"NBT"));
				boolean oreMatch = ItemNBTHelper.getBoolean(filterStack, "OreMatch", false);
				this.buttonList.add(new GuiButton(BUTTON_ORE, sx+45+(45/2), sy+50+12, 45, 12, (oreMatch ? TextFormatting.GREEN : TextFormatting.RED)+"Ore"));
			}
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe_filter.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
