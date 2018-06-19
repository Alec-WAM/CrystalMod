package alec_wam.CrystalMod.tiles.machine.dna;

import java.util.UUID;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.dna.ItemDNA.DNAItemType;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityDNAMachine extends TileEntityMachine implements ISidedInventory {

	public final int POWER_NEED = 2000;
	
	public TileEntityDNAMachine() {
		super("DNAMachine", 3);
	}
	
	public ItemStack getSampleStack() {
		return getStackInSlot(0);
	}
	
	public ItemStack getEmptySyringes() {
		return getStackInSlot(1);
	}
	
	public void useSyringe(){
        setInventorySlotContents(1, ItemUtil.consumeItem(getEmptySyringes()));
	}
	
	public ItemStack getFinishedSyringes() {
		return getStackInSlot(2);
	}
	
	public void setFinishedSyringes(ItemStack stack){
		setInventorySlotContents(2, stack);
	}
	
	@Override
	public boolean canStart() {
		//Check inputs
		if(!hasValidInput()){
			return false;
		}		
		if(getEnergyStorage().getCEnergyStored() < POWER_NEED){
			return false;
		}		
		//Lets make some samples!
		ItemStack sample = getSampleStack();
		UUID playerDNA = PlayerDNA.loadPlayerDNA(sample);
		
		final ItemStack output = new ItemStack(ModItems.dnaItems, 1, DNAItemType.FILLED_SYRINGE.getMeta());
        PlayerDNA.savePlayerDNA(output, playerDNA);
        
        //Can we actually fit the syringe?
        ItemStack existingSyringes = getFinishedSyringes();
        return ItemStackTools.isValid(output) && (ItemStackTools.isNullStack(existingSyringes) || (ItemUtil.canCombine(output, existingSyringes) && ItemStackTools.getStackSize(existingSyringes) + ItemStackTools.getStackSize(output) <= output.getMaxStackSize()));
    }
	
	@Override
	public boolean canContinueRunning(){
		return hasValidInput();
	}
	
	@Override
	public boolean canFinish() {
        return processRem <= 0 && hasValidInput();
    }
    
    protected boolean hasValidInput() {
    	ItemStack sample = getSampleStack();
		if (ItemStackTools.isNullStack(sample) || !(sample.getItem() == ModItems.dnaItems && sample.getMetadata() == DNAItemType.SAMPLE_FULL.getMeta())) {
            return false;
        }
		UUID playerDNA = PlayerDNA.loadPlayerDNA(sample);
		if(playerDNA == null){
			return false;
		}
		//Do we have the empty syringes?
        ItemStack syringes = getEmptySyringes();
        if (ItemStackTools.isNullStack(syringes) || !(syringes.getItem() == ModItems.dnaItems && syringes.getMetadata() == DNAItemType.EMPTY_SYRINGE.getMeta())) {
            return false;
        }
        return true;
    }
    
    @Override
	public void processStart() {
    	this.processMax = POWER_NEED;
        this.processRem = this.processMax;
        syncProcessValues();
    }
    
    @Override
	public void processFinish() {
    	ItemStack sample = getSampleStack();
		UUID playerDNA = PlayerDNA.loadPlayerDNA(sample);
		
		final ItemStack output = new ItemStack(ModItems.dnaItems, 1, DNAItemType.FILLED_SYRINGE.getMeta());
        PlayerDNA.savePlayerDNA(output, playerDNA);
        
        //Can we actually fit the syringe?
        ItemStack existingSyringes = getFinishedSyringes();
    	if (ItemStackTools.isNullStack(existingSyringes)) {
            setFinishedSyringes(output);
        }
        else {
            ItemStackTools.incStackSize(existingSyringes, ItemStackTools.getStackSize(output));
        }
    	useSyringe();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
    	if(index == 0){
			return stack.getItem() == ModItems.dnaItems && stack.getMetadata() == DNAItemType.SAMPLE_FULL.getMeta();
		}
    	if(index == 1){
			return stack.getItem() == ModItems.dnaItems && stack.getMetadata() == DNAItemType.EMPTY_SYRINGE.getMeta();
		}
    	if(index == 2){
			return stack.getItem() == ModItems.dnaItems && stack.getMetadata() == DNAItemType.FILLED_SYRINGE.getMeta();
		}
    	return false;
	}
    
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if(direction == EnumFacing.UP){
			return index == 0 && itemStackIn.getItem() == ModItems.dnaItems && itemStackIn.getMetadata() == DNAItemType.SAMPLE_FULL.getMeta();
		}
		if(direction.getAxis().isHorizontal()){
			return index == 1 && itemStackIn.getItem() == ModItems.dnaItems && itemStackIn.getMetadata() == DNAItemType.EMPTY_SYRINGE.getMeta();
		}
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.UP){
			return new int[]{0};
		}
		if(side.getAxis().isHorizontal()){
			return new int[]{1};
		}
		return new int[]{2};
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return direction == EnumFacing.DOWN && index == 2;
	}
	
	@Override
	public Object getContainer(EntityPlayer player, int id) {
		return new ContainerDNAMachine(player, this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getGui(EntityPlayer player, int id) {
		return new GuiDNAMachine(player, this);
	}

}
