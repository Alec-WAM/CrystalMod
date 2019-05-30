package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.gui.GuiButtonIcon;
import alec_wam.CrystalMod.client.gui.GuiButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.GuiButtonTiny;
import alec_wam.CrystalMod.client.gui.GuiButtonTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiItemPipe extends GuiContainerBase {

	private final TileEntityPipeItem pipe;
	private final EnumFacing facing;
	private GuiButton buttonIO;
	private GuiButtonIcon buttonRedstone;
	private GuiButton buttonPriorityUp;
	private GuiButton buttonPriorityDown;
	private GuiButton buttonRR;
	private boolean twoFilters;
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/pipe/item.png");
	public GuiItemPipe(EntityPlayer player, TileEntityPipeItem pipe, EnumFacing facing) {
		super(new ContainerItemPipe(player, pipe, facing), TEXTURE);
		this.pipe = pipe;
		this.facing = facing;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		setupButtons();
	}
	
	public void setupButtons(){
		this.buttons.clear();
		PipeConnectionMode connectionMode = pipe.getConnectionSetting(facing);
		String io = Lang.localize("gui.pipe.io."+connectionMode.name().toLowerCase());
		List<String> ioInfo = Lists.newArrayList();
		if(connectionMode == PipeConnectionMode.IN || connectionMode == PipeConnectionMode.OUT){
			ioInfo.add(Lang.localize("gui.pipe.io."+connectionMode.name().toLowerCase() + ".info"));
		}
		buttonIO = new GuiButtonTooltip(0, guiLeft + 5, guiTop + 5, 40, 20, io, ioInfo) {
			@Override
			public void onClick(double mouseX, double mouseY){
				pipe.incrsConnectionMode(facing);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Mode", pipe.getConnectionSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "IO.Client", nbt));
				setupButtons();
			}
		};
		
		RedstoneMode setting = pipe.getRedstoneSetting(facing);
		int index = setting.ordinal();
		int iconX = 0;
		int iconY = index * 16;
		String redstoneTooltip = Lang.localize("gui.redstone."+setting.name().toLowerCase());
		buttonRedstone = new GuiButtonIconTooltip(1, guiLeft + 150, guiTop + 5, iconX, iconY, redstoneTooltip) {
			@Override
			public void onClick(double mouseX, double mouseY){
				pipe.incrsRedstoneMode(facing);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Mode", pipe.getRedstoneSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Redstone.Client", nbt));
				setupButtons();
			}
		};
		
		buttonPriorityDown = new GuiButtonTiny(2, guiLeft + 11, guiTop + 70, 10, 10, "-") {
			@Override
			public void onClick(double mouseX, double mouseY){
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current - 1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Value", current - 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		};
		buttonPriorityUp = new GuiButtonTiny(3, guiLeft + 22, guiTop + 70, 10, 10, "+") {
			@Override
			public void onClick(double mouseX, double mouseY){
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current + 1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Value", current + 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		};
		boolean rr = pipe.isRoundRobinEnabled(facing);
		String rrString = rr ? "R" : "D";
		List<String> rrInfo = Lists.newArrayList();
		rrInfo.add(Lang.localize("gui.pipe.io."+ (rr ? "round" : "dist")));
		rrInfo.add(Lang.localize("gui.pipe.io."+ (rr ? "round" : "dist") +".info"));
		buttonRR = new GuiButtonTooltip(4, guiLeft + 12, guiTop + 35, 20, 20, rrString, rrInfo) {
			@Override
			public void onClick(double mouseX, double mouseY){
				final boolean rr = pipe.isRoundRobinEnabled(facing);
				pipe.setRoundRobin(facing, !rr);;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setBoolean("Value", !rr);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "RoundRobin", nbt));
				setupButtons();
			}
		};
		
		this.addButton(buttonIO);
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.IN){
			this.addButton(buttonRedstone);
			this.addButton(buttonRR);
		}
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.OUT){
			this.addButton(buttonPriorityDown);
			this.addButton(buttonPriorityUp);
		}
		final boolean oldFilters = twoFilters;
		twoFilters = connectionMode == PipeConnectionMode.BOTH;
		if(oldFilters != twoFilters){
			((ContainerItemPipe)this.inventorySlots).updateSlots();
			CrystalModNetwork.sendToServer(new PacketGuiMessage("UpdateSlots"));
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		//Render Filter Slot
		if(twoFilters){
			drawTexturedModalRect(guiLeft + 71, guiTop + 34, 133, 57, 36, 18);
		} else {
			drawTexturedModalRect(guiLeft + 79, guiTop + 34, 133, 57, 18, 18);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		PipeConnectionMode connectionMode = pipe.getConnectionSetting(facing);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.OUT){
			String str = "" + pipe.getPriority(facing);
			this.fontRenderer.drawString(str, 22.0F - (this.fontRenderer.getStringWidth(str) / 2), 60.0F, 4210752);
		}
		String dir = Lang.localize("gui.direction."+facing.getName());
		this.fontRenderer.drawString(dir, 88.0F - (this.fontRenderer.getStringWidth(dir) / 2), 10.0F, 4210752);
		
		if(twoFilters){
			boolean emptyFilterIn = pipe.getInFilter(facing).isEmpty();
			boolean emptyFilterOut = pipe.getOutFilter(facing).isEmpty();
			
			if(emptyFilterIn || emptyFilterOut){
				GlStateManager.pushMatrix();
				GlStateManager.translated(80.0, 40, 0.0F);
				GlStateManager.scaled(0.8, 0.8, 1.0);
				String inString = Lang.localize("gui.pipe.io.in");
				if(emptyFilterIn)this.fontRenderer.drawString(inString, -(this.fontRenderer.getStringWidth(inString) / 2), 0.0F, 4210752);
				String outString = Lang.localize("gui.pipe.io.out");
				if(emptyFilterOut)this.fontRenderer.drawString(outString, 23.0F - (this.fontRenderer.getStringWidth(outString) / 2), 0.0F, 4210752);
				GlStateManager.popMatrix();
			}
		}
		
		GlStateManager.enableLighting();
	}

}
