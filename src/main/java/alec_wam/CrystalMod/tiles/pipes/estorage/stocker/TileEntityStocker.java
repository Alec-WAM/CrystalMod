package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import java.util.UUID;

import alec_wam.CrystalMod.api.estorage.INetworkTile;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityStocker extends TileEntityInventory implements INetworkTile, IFacingTile, IMessageHandler {

	private EnumFacing facing = EnumFacing.NORTH;
	public int[] stockAmts;
	public int[] craftAmounts;
	public boolean[] useOre;
	
	public TileEntityStocker() {
		super("Stocker", 5);
		stockAmts = new int[5];
		craftAmounts = new int[5];
		useOre = new boolean[5];
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
		nbt.setIntArray("StockArray", stockAmts);
		nbt.setIntArray("CraftAmountArray", craftAmounts);
		byte[] oreArray = new byte[5];
		for(int i = 0; i < useOre.length; i++){
			boolean ore = useOre[i];
			oreArray[i] = (byte)(ore ? 1 : 0);
		}
		nbt.setByteArray("OreArray", oreArray);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = EnumFacing.getFront(nbt.getInteger("Facing"));
		stockAmts = nbt.getIntArray("StockArray");
		craftAmounts = nbt.getIntArray("CraftAmountArray");
		byte[] oreArray = nbt.getByteArray("OreArray");
		for(int i = 0; i < oreArray.length; i++){
			byte ore = oreArray[i];
			if(i < useOre.length)useOre[i] = ore == 1 ? true : false;
		}
		updateAfterLoad();
	}
	
	@Override
	public void update(){
		super.update();
		if(getWorld().isRemote || network == null || network.craftingController == null)return;
		if(RedstoneMode.ON.passes(getWorld(), getPos())){
			for(int i = 0; i < getSizeInventory(); i++){
				ItemStack stack = getStackInSlot(i);
				if(ItemStackTools.isValid(stack)){
					int stockAmt = stockAmts[i];
					boolean oreDict = useOre[i];
					int amountPerRequest = craftAmounts[i];
					IItemHandler handler = ItemUtil.getExternalItemHandler(getWorld(), getPos().offset(facing), facing.getOpposite());
					if(handler !=null){
						int insertAmt = 1;
						EnumFacing insertFace = facing.getOpposite();
						//Fill inventory
						int itemCount = ItemUtil.countItems(handler, stack, oreDict);
						if(itemCount < stockAmt){
							boolean canInsert = ItemUtil.doInsertItem(handler, ItemUtil.copy(stack, insertAmt), insertFace, false) > 0;
							if(canInsert){
								ItemStack extractStack = ItemStackTools.safeCopy(stack);
								ItemStack took = network.getItemStorage().removeItem(extractStack, insertAmt, oreDict ? ItemStorage.ORE : ItemStorage.NORMAL, true);
			                	if(ItemStackTools.isEmpty(took)){
			                		network.craftingController.scheduleCraftingTaskIfUnscheduled(extractStack, amountPerRequest, oreDict);
			                	} else if(ItemStackTools.isEmpty(ItemHandlerHelper.insertItem(handler, took, true))){
			                		took = network.getItemStorage().removeItem(extractStack, insertAmt, oreDict ? ItemStorage.ORE : ItemStorage.NORMAL, false);
			                		ItemHandlerHelper.insertItem(handler, took, false);
			                	}
							}
						}
					} else {
						//Fill network
						ItemStackData data = network.getItemStorage().getItemData(stack);
						if(data == null && oreDict)data = network.getItemStorage().getOreItemData(stack);
						int itemCount = data == null ? 0 : data.getAmount();
						if(itemCount < stockAmt){
							int needed = (stockAmt-itemCount);
		                	if(needed > 0 && amountPerRequest > 0){
		                		network.craftingController.scheduleCraftingTaskIfUnscheduled(stack, amountPerRequest, oreDict);
		                	}
						}
					}
				}
			}
		}
	}

	@Override
	public void onItemChanged(int slot){
		/*if(slot < 5){
			stockAmts[slot] = 0;
			craftAmounts[slot] = 1;
			useOre[slot] = false;
		}*/
	}
	
	private EStorageNetwork network;
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		if(getNetwork() == null)return;
	}

	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	@Override
	public boolean useVerticalFacing(){
		return true;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(network !=null){
			if(messageData.hasKey("UUID")){
				UUID uuid = UUIDUtils.fromString(messageData.getString("UUID"));
				if(uuid !=null){
					if(!network.hasAbility(uuid, NetworkAbility.SETTINGS)){
						return;
					}
				}
			}
		}
		if(messageId.equalsIgnoreCase("StockAmount")){
			int index = messageData.getInteger("Index") % 5;
			stockAmts[index] = messageData.getInteger("Value");
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase("CraftAmount")){
			int index = messageData.getInteger("Index") % 5;
			craftAmounts[index] = messageData.getInteger("Value");
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase("Ore")){
			int index = messageData.getInteger("Index") % 5;
			useOre[index] = messageData.getBoolean("Value");
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
}
