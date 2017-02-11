package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileSecurityEncoder extends TileEntityInventory implements IMessageHandler {

	public TileSecurityEncoder() {
		super("Encoder", 1);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.startsWith("CardAbility")){
			String type = messageData.getString("Type");
			for(NetworkAbility ability : NetworkAbility.values()){
				if(ability.getId().equalsIgnoreCase(type)){
					ItemStack card = getStackInSlot(0);
					if(ItemStackTools.isValid(card)){
						ItemSecurityCard.setAbility(card, ability, messageData.getBoolean("Value"));
					}
				}
			}
		}
	}

}
