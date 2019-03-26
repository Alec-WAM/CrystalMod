package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import java.io.IOException;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBufferClientData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

public class PacketEnderBufferClientSync extends AbstractPacketThreadsafe {

	public int code;
	public UUID privateUUID;
	public int cu;
	public int fu;
	public FluidStack fluid;
	
	public PacketEnderBufferClientSync(){}
	
	public PacketEnderBufferClientSync(int code, UUID privateUUID, int cu, int fu, FluidStack fluid){
		this.code = code;
		this.privateUUID = privateUUID;
		this.cu = cu;
		this.fu = fu;
		this.fluid = fluid;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);		
		code = buffer.readInt();
		boolean hasUUID = buffer.readBoolean();
		if(hasUUID){
			privateUUID = buffer.readUniqueId();
		}
		cu = buffer.readInt();
		fu = buffer.readInt();
		boolean validFluid = buffer.readBoolean();
		if(validFluid){
			try {
				this.fluid = FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag());
			}
			catch (IOException e) {
				this.fluid = null;
			}
		} else {
			this.fluid = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(code);
		buffer.writeBoolean(privateUUID !=null);
		if(privateUUID !=null){
			buffer.writeUniqueId(privateUUID);
		}
		buffer.writeInt(cu);
		buffer.writeInt(fu);
		buffer.writeBoolean(fluid !=null);
		if(fluid !=null){
			buffer.writeCompoundTag(fluid.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EnderBufferClientData data = new EnderBufferClientData();
		data.cu = cu;
		data.rf = fu;
		data.fluid = fluid;
		EnderBuffer buffer = null;
		if(privateUUID !=null){
			buffer = EnderBufferManager.get(CrystalMod.proxy.getClientWorld()).getPrivate(privateUUID).getBuffer(code);
		} else {
			buffer = EnderBufferManager.get(CrystalMod.proxy.getClientWorld()).getBuffer(code);
		}
		if(buffer !=null){
			buffer.clientData = data;
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		
	}

}
