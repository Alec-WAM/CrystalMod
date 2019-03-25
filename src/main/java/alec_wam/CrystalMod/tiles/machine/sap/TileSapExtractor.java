package alec_wam.CrystalMod.tiles.machine.sap;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil.BaseHarvestTarget;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil.TreeData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TileSapExtractor extends TileEntityMachine {

	private boolean validTree;
	private int leafCount;
	private CrystalColors.Basic treeType;
	private boolean hasInitialized;
	public TileSapExtractor() {
		super("SapExtractor", 1);
	}
	
	@Override
	public void update(){
		super.update();
		if(!hasInitialized){
			updateTreeInfo();
			hasInitialized = true;
		}
	}
	
	public void updateTreeInfo(){
		EnumFacing dir = EnumFacing.getHorizontal(facing);
		if(dir !=null){
			BlockPos treePos = getPos().offset(dir.getOpposite());
			IBlockState log =getWorld().getBlockState(treePos);
			if(log.getBlock() !=ModBlocks.crystalLog){
				this.validTree = false;
				this.leafCount = 0;
				this.treeType = null;
				if(!getWorld().isRemote){
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Type", -1);
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "TreeType", nbt), this); 
				}
				return;
			}
			TreeData data = TreeHarvestUtil.isFullTree(getWorld(), treePos, new BaseHarvestTarget(ModBlocks.crystalLog));
			if(data.isValid()){
				this.validTree = true;
				this.leafCount = data.getLeaves();
				this.treeType = log.getValue(CrystalColors.COLOR_BASIC);
				if(!getWorld().isRemote){
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Type", treeType.ordinal());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "TreeType", nbt), this); 
				}
			} else {
				this.validTree = false;
				this.leafCount = 0;
				this.treeType = null;
				if(!getWorld().isRemote){
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Type", -1);
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "TreeType", nbt), this); 
				}
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("TreeType")){
			int type = messageData.getInteger("Type");
			
			if(type < 0){
				treeType = null;
			} else {
				treeType = CrystalColors.Basic.byMetadata(type);
			}
		}
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canStart() {
		if(treeType == null)return false;
		ItemStack sap = new ItemStack(ModItems.crystalSap, 1, treeType.getMeta());
		
		boolean canFit = true;
		if(ItemStackTools.isValid(getStackInSlot(0))){
			canFit = ItemUtil.canCombine(sap, getStackInSlot(0)) && ItemStackTools.getStackSize(getStackInSlot(0)) + 1 <= sap.getMaxStackSize();
		}
		
		return validTree && leafCount >= 5 && canFit && this.getEnergyStorage().getCEnergyStored() >=10000;
	}

	@Override
	public void processStart() {
		this.processMax = 10000;
        this.processRem = this.processMax;
        syncProcessValues();
	}

	@Override
	public boolean canFinish() {
		if(treeType == null || !validTree)return false;
		return processRem <= 0;
	}

	@Override
	public void processFinish() {
		ItemStack sap = new ItemStack(ModItems.crystalSap, 1, treeType.getMeta());
		
		int chance = MathHelper.getInt(getWorld().rand, 20, 40);
		
		if(getWorld().rand.nextInt(100) <= chance){
			if(ItemStackTools.isEmpty(getStackInSlot(0))){
				this.setInventorySlotContents(0, sap);
			} else {
				ItemStackTools.incStackSize(getStackInSlot(0), 1);
			}
			getWorld().playSound(null, getPos(), SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 0.6f, 0.8f);
		}
	}

	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerSapExtractor(player, this);
	}

	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiSapExtractor(player, this);
	}

	public CrystalColors.Basic getTreeType() {
		return treeType;
	}

	@Override
	public boolean canContinueRunning() {
		return treeType != null && validTree;
	}

}
