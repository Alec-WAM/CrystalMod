package alec_wam.CrystalMod.client;

import java.util.function.Consumer;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketOpenCustomGui;
import alec_wam.CrystalMod.tiles.chests.metal.GuiMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wireless.GuiWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wooden.GuiWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.energy.battery.GuiBattery;
import alec_wam.CrystalMod.tiles.energy.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.GuiPoweredFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.TileEntityPoweredFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.GuiGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.TileEntityGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.press.GuiPress;
import alec_wam.CrystalMod.tiles.machine.crafting.press.TileEntityPress;
import alec_wam.CrystalMod.tiles.machine.miner.GuiMiner;
import alec_wam.CrystalMod.tiles.machine.miner.TileEntityMiner;
import alec_wam.CrystalMod.tiles.pipes.item.GuiItemPipe;
import alec_wam.CrystalMod.tiles.pipes.item.GuiPipeFilter;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class GuiHandler{

	public static final ResourceLocation ITEM_NORMAL = CrystalMod.resourceL("item_normal");
	public static final ResourceLocation TILE_NORMAL = CrystalMod.resourceL("tile_normal");
	public static final ResourceLocation TILE_PIPE_CONNECTOR = CrystalMod.resourceL("tile_pipe_connector");
	
	public static Screen openGui(ResourceLocation id, int windowId, PlayerInventory inv, ITextComponent text, PacketBuffer additionalData)
    {
    	PlayerEntity player = Minecraft.getInstance().player;
    	if(id.equals(TILE_NORMAL) || id.equals(TILE_PIPE_CONNECTOR)){
			BlockPos pos = additionalData.readBlockPos();        
	    	TileEntity tile = Minecraft.getInstance().world.getTileEntity(pos);
	    	if (tile != null)
	        {
	    		if(id.equals(TILE_NORMAL)){
		        	if(tile instanceof TileEntityWoodenCrystalChest){
		        		TileEntityWoodenCrystalChest chest = (TileEntityWoodenCrystalChest) tile;
		        		return GuiWoodenCrystalChest.GUI.buildGUI(windowId, chest.type, player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityMetalCrystalChest){
		        		TileEntityMetalCrystalChest chest = (TileEntityMetalCrystalChest) tile;
		        		return GuiMetalCrystalChest.GUI.buildGUI(windowId, chest.type, player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityWirelessChest){
		        		TileEntityWirelessChest chest = (TileEntityWirelessChest) tile;
		        		return new GuiWirelessChest(windowId, player.inventory, chest);
		            }
		        	if(tile instanceof TileEntityEngineFurnace){
		        		TileEntityEngineFurnace engine = (TileEntityEngineFurnace) tile;
		        		return new GuiEngineFurnace(windowId, player, engine);
		            }
		        	if(tile instanceof TileEntityBattery){
		        		TileEntityBattery battery = (TileEntityBattery) tile;
		        		return new GuiBattery(windowId, player, battery);
		            }
		        	if(tile instanceof TileEntityPoweredFurnace){
		        		TileEntityPoweredFurnace machine = (TileEntityPoweredFurnace) tile;
		        		return new GuiPoweredFurnace(windowId, player, machine);
		            }
		        	if(tile instanceof TileEntityGrinder){
		        		TileEntityGrinder machine = (TileEntityGrinder) tile;
		        		return new GuiGrinder(windowId, player, machine);
		            }
		        	if(tile instanceof TileEntityPress){
		        		TileEntityPress machine = (TileEntityPress) tile;
		        		return new GuiPress(windowId, player, machine);
		            }
		        	if(tile instanceof TileEntityMiner){
		        		TileEntityMiner machine = (TileEntityMiner) tile;
		        		return new GuiMiner(windowId, player, machine);
		            }
	    		}
	    		if(id.equals(TILE_PIPE_CONNECTOR)){
	    			if(tile instanceof TileEntityPipeItem){
	    				TileEntityPipeItem pipe = (TileEntityPipeItem) tile;
	    				Direction facing = additionalData.readEnumValue(Direction.class);
		        		return new GuiItemPipe(windowId, player, pipe, facing);
	    			}
	    		}
	        }
        }
        if(id.equals(ITEM_NORMAL)){
        	Hand hand = additionalData.readEnumValue(Hand.class);
        	ItemStack stack = Minecraft.getInstance().player.getHeldItem(hand);
        	if(stack.getItem() == ModItems.itemFilter){
        		return new GuiPipeFilter(windowId, player, stack, hand);
        	}
        }
        return null;
    }
	
	public static void openCustomGui(ResourceLocation id, ServerPlayerEntity player, INamedContainerProvider containerSupplier, Consumer<PacketBuffer> extraDataWriter)
    {
        if (player.world.isRemote) return;
        player.closeContainer();
        player.getNextWindowId();
        int openContainerId = player.currentWindowId;
        PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason

        PacketBuffer output = new PacketBuffer(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openCustomGui, found "+ output.readableBytes()+ " bytes");
        }
        Container c = containerSupplier.createMenu(openContainerId, player.inventory, player);
        
        PacketOpenCustomGui packet = new PacketOpenCustomGui(id, openContainerId, containerSupplier.getDisplayName(), output);
        CrystalModNetwork.getNetworkChannel().sendTo(packet, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

        player.openContainer = c;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }
	
	@SuppressWarnings("rawtypes")
	public static void createScreen(ResourceLocation id, int windowId, Minecraft mc, ITextComponent text, PacketBuffer additionalData) {
		Screen u = openGui(id, windowId, mc.player.inventory, text, additionalData);
		if(u instanceof IHasContainer){
			mc.player.openContainer = ((IHasContainer)u).getContainer();
		}
		mc.displayGuiScreen(u);
	}

}
