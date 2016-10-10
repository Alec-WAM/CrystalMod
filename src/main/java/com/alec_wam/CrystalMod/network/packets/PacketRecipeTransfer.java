package com.alec_wam.CrystalMod.network.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.util.Constants;

import com.alec_wam.CrystalMod.integration.jei.RecipeTransferHandler;
import com.alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.ContainerPatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.TilePatternEncoder;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.ModLogger;
import com.google.common.collect.Lists;

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
			try{
				encoder.clearMatrix();
				ItemStack[][] actualRecipe = new ItemStack[9][];

		        for (int x = 0; x < actualRecipe.length; x++) {
		            NBTTagList list = recipeNBT.getTagList("#" + x, Constants.NBT.TAG_COMPOUND);

		            if (list.tagCount() > 0) {
		                actualRecipe[x] = new ItemStack[list.tagCount()];

		                for (int y = 0; y < list.tagCount(); y++) {
		                    actualRecipe[x][y] = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(y));
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
