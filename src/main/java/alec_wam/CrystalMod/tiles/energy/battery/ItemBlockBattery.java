package alec_wam.CrystalMod.tiles.energy.battery;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyContainerWrapper;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBlockBattery extends ItemBlock {

	public final EnumCrystalColorSpecialWithCreative type;
	
	public ItemBlockBattery(BlockBattery block, Item.Properties properties, EnumCrystalColorSpecialWithCreative type) {
		super(block, properties);
		this.type = type; 
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.NBTTagCompound nbt) {
		final NBTTagCompound itemNBT = ItemNBTHelper.getCompound(stack).getCompound(TileEntityBattery.NBT_DATA);
		int send = TileEntityBattery.MAX_IO[type.ordinal()];
		int rec = TileEntityBattery.MAX_IO[type.ordinal()];
		if(itemNBT.hasKey("Send")){
			send = Math.min(TileEntityBattery.MAX_IO[type.ordinal()], itemNBT.getInt("Send"));
		} 
		if(itemNBT.hasKey("Receive")){
			rec = Math.min(TileEntityBattery.MAX_IO[type.ordinal()], itemNBT.getInt("Receive"));
		}			
		return new CEnergyContainerWrapper(stack, TileEntityBattery.MAX_ENERGY[type.ordinal()], rec, send) {
			@Override
			public int getItemEnergy(){
				if(type == EnumCrystalColorSpecialWithCreative.CREATIVE){
					return TileEntityBattery.MAX_ENERGY[type.ordinal()];
				}
				NBTTagCompound itemNBT = ItemNBTHelper.getCompound(stack).getCompound(TileEntityBattery.NBT_DATA);
				return itemNBT.getInt("Energy");
			}

			@Override
			public void setItemEnergy(int energy){
				if(type == EnumCrystalColorSpecialWithCreative.CREATIVE){
					return;
				}
				NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
				if(!nbt.hasKey(TileEntityBattery.NBT_DATA)){
					nbt.setTag(TileEntityBattery.NBT_DATA, BlockBattery.getDefaultItemNBT(type));
				}
				NBTTagCompound itemNBT = nbt.getCompound(TileEntityBattery.NBT_DATA);
				itemNBT.setInt("Energy", energy);
			}
		};
	}
	
	public int getEnergy(ItemStack stack){
		NBTTagCompound itemNBT = ItemNBTHelper.getCompound(stack).getCompound(TileEntityBattery.NBT_DATA);
		return itemNBT.getInt("Energy");
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
		if(type == EnumCrystalColorSpecialWithCreative.CREATIVE)return false;
		return getEnergy(stack) < TileEntityBattery.MAX_ENERGY[type.ordinal()];
    }

    @Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
    	int cap = TileEntityBattery.MAX_ENERGY[type.ordinal()];
		int energy = getEnergy(stack);
        return (double) (cap - energy) / (double) cap;
    }

    @Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return 0x00ffff;
    }
	
}
