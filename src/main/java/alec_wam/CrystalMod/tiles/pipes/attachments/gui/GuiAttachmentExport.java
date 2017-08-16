package alec_wam.CrystalMod.tiles.pipes.attachments.gui;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageExport;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentIOType;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.item.PacketPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiAttachmentExport extends GuiContainer {

	private TileEntityPipeEStorage pipe;
	private EnumFacing dir;
	public GuiAttachmentExport(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
		super(new ContainerAttachmentExport(player, pipe, dir));
		this.pipe = pipe;
		this.dir = dir;
	}
	
	public AttachmentData getData(){
		return pipe.getAttachmentData(dir);
	}
	
	public AttachmentEStorageExport getPart(){
		if(this.getData() == null || !(getData() instanceof AttachmentEStorageExport))return null;
		return (AttachmentEStorageExport)getData();
	}
	
	@Override
	public void initGui(){
		super.initGui();
		refreshButtons();
	}
	
	private void refreshButtons() {
		this.buttonList.clear();
		int sx = (width - xSize) / 2;
		int sy = (height - ySize) / 2;
		if(getPart() == null)return;
		RedstoneMode mode = getPart().rMode;
		this.buttonList.add(new GuiButton(0, sx+8+140, sy+10, 20, 20, mode.name()));
		AttachmentIOType ioType = getPart().ioType;
		this.buttonList.add(new GuiButton(1, sx+8+140, sy+40, 20, 20, ioType.name()));
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button.id == 0){
			final RedstoneMode next = RedstoneMode.getNextRedstoneMode(getPart().rMode);
			getPart().rMode = next;
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "RMode", dir, next.name()));
			refreshButtons();
			return;
		}
		if(button.id == 1){
			AttachmentIOType next = getPart().ioType.getNext();
			if(next == AttachmentIOType.BOTH){
				next = next.getNext();
			}
			getPart().ioType = next;
			CrystalModNetwork.sendToServer(new PacketPipe(pipe, "IOType", dir, next.name()));
			refreshButtons();
			return;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    int sx = (width - xSize) / 2;
	    int sy = (height - ySize) / 2;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/pipe_attach_export.png"));
	    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
	}
	
}
