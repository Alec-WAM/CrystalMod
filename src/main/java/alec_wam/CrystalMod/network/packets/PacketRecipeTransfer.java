package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.integration.jei.RecipeTransferHandler;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TileProcessingPatternEncoder;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import alec_wam.CrystalMod.util.ItemStackTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.util.Constants;

public class PacketRecipeTransfer extends AbstractPacketThreadsafe {

	NBTTagCompound recipeNBT;
	
	public PacketRecipeTransfer(){}
	
	public PacketRecipeTransfer(NBTTagCompound recipe){
		this.recipeNBT = recipe;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			recipeNBT = ByteBufUtils.readNBTTagCompoundFromBuffer(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeNBTTagCompoundToBuffer(buf, this.recipeNBT);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
	}
	
	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		Container container = player.openContainer;
		if (container instanceof ContainerPanelCrafting) {
			ContainerPanelCrafting con = (ContainerPanelCrafting)container;
			//Empty grid into inventory
			TileEntityPanelCrafting panel = ((TileEntityPanelCrafting)con.panel);
			try{
				RecipeTransferHandler.transferItems(con, panel, recipeNBT);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		if (container instanceof ContainerPatternEncoder) {
			ContainerPatternEncoder con = (ContainerPatternEncoder)container;
			//Empty grid into inventory
			TilePatternEncoder encoder = con.encoder;
			
			
			if(encoder instanceof TileProcessingPatternEncoder){
				TileProcessingPatternEncoder pEncoder = (TileProcessingPatternEncoder)encoder;
				ItemStack[] inputs = new ItemStack[9];
				ItemStack[] outputs = new ItemStack[9];
				
				if(recipeNBT.hasKey("Inputs")){
					NBTTagList list = recipeNBT.getTagList("Inputs", Constants.NBT.TAG_COMPOUND);
					for(int t = 0; t < list.tagCount(); t++){
						NBTTagCompound nbt = list.getCompoundTagAt(t);
						int slot = nbt.getInteger("Slot");
						inputs[slot] = ItemStackTools.loadFromNBT(nbt);
					}
				}
				if(recipeNBT.hasKey("Outputs")){
					NBTTagList list = recipeNBT.getTagList("Outputs", Constants.NBT.TAG_COMPOUND);
					for(int t = 0; t < list.tagCount(); t++){
						NBTTagCompound nbt = list.getCompoundTagAt(t);
						int slot = nbt.getInteger("Slot");
						outputs[slot] = ItemStackTools.loadFromNBT(nbt);
					}
				}
				pEncoder.fillInputs(inputs);
				pEncoder.fillOutputs(outputs);
				return;
			}
			
			try{
				encoder.clearMatrix();
				ItemStack[][] actualRecipe = new ItemStack[9][];

		        for (int x = 0; x < actualRecipe.length; x++) {
		            NBTTagList list = recipeNBT.getTagList("#" + x, Constants.NBT.TAG_COMPOUND);

		            if (list.tagCount() > 0) {
		                actualRecipe[x] = new ItemStack[list.tagCount()];

		                for (int y = 0; y < list.tagCount(); y++) {
		                    actualRecipe[x][y] = ItemStackTools.loadFromNBT(list.getCompoundTagAt(y));
		                }
		            }
		        }
		        for(int i = 0; i < actualRecipe.length; i++){
		        	ItemStack[] array = actualRecipe[i];
		        	if(encoder !=null && array !=null && encoder.getMatrix() !=null){
		        		encoder.getMatrix().setInventorySlotContents(i, array[0]);
		        	}
		        }
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
