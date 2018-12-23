package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobHarvestCrop;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobPlantCrop;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobTillDirt;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RotationType;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemSlotFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteUserBlocks;
import alec_wam.CrystalMod.tiles.machine.worksite.WorkerFilter;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FarmUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public class WorksiteCropFarm extends TileWorksiteUserBlocks {

	Set<BlockPos> blocksToTill;
	Set<BlockPos> blocksToHarvest;
	Set<BlockPos> blocksToPlant;
	Set<BlockPos> blocksToFertilize;

	int plantableCount;
	int bonemealCount;
	boolean shouldCountResources;

	public WorksiteCropFarm() {
		this.shouldCountResources = true;

		blocksToTill = new HashSet<BlockPos>();
		blocksToHarvest = new HashSet<BlockPos>();
		blocksToPlant = new HashSet<BlockPos>();
		blocksToFertilize = new HashSet<BlockPos>();

		this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33) {
			@Override
			public void markDirty() {
				super.markDirty();
				shouldCountResources = true;
			}
		};
		int[] topIndices = ItemUtil.getIndiceArrayForSpread(0, 27);
		int[] frontIndices = ItemUtil.getIndiceArrayForSpread(27, 3);
		int[] bottomIndices = ItemUtil.getIndiceArrayForSpread(30, 3);
		this.inventory.setAccessibleSideDefault(RelativeSide.TOP,
				RelativeSide.TOP, topIndices);
		this.inventory.setAccessibleSideDefault(RelativeSide.FRONT,
				RelativeSide.FRONT, frontIndices);// plantables
		this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM,
				RelativeSide.BOTTOM, bottomIndices);// bonemeal

		ItemSlotFilter filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (FarmUtil.isSeed(stack, true)) {
					return true;
				}
				return false;
			}
		};
		this.inventory.setFilterForSlots(filter, frontIndices);

		filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (ItemStackTools.isEmpty(stack)) {
					return false;
				}
				if(stack.getItem() instanceof ItemHoe){
					return true;
				}
				return stack.getItem() == Items.DYE && stack.getItemDamage() == 15;
			}
		};
		this.inventory.setFilterForSlots(filter, bottomIndices);
	}

	@Override
	public void onTargetsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToHarvest);
		validateCollection(blocksToPlant);
		validateCollection(blocksToTill);
	}

	@Override
	public void onBoundsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToHarvest);
		validateCollection(blocksToPlant);
		validateCollection(blocksToTill);
	}

	private void countResources() {
		shouldCountResources = false;
		plantableCount = 0;
		bonemealCount = 0;
		ItemStack stack;
		for (int i = 27; i < 30; i++) {
			stack = inventory.getStackInSlot(i);
			if (ItemStackTools.isEmpty(stack)) {
				continue;
			}
			if (FarmUtil.isSeed(stack, true)) {
				plantableCount += ItemStackTools.getStackSize(stack);
			}
		}
		for (int i = 30; i < 33; i++) {
			stack = inventory.getStackInSlot(i);
			if (ItemStackTools.isEmpty(stack)) {
				continue;
			}
			if (stack.getItem() == Items.DYE && stack.getItemDamage() == 15) {
				bonemealCount += ItemStackTools.getStackSize(stack);
			}
		}
	}

	@Override
	protected void fillBlocksToProcess(Collection<BlockPos> targets) {
		if(bbMax == null || bbMin == null)return;
		int w = bbMax.getX() - bbMin.getX() + 1;
		int h = bbMax.getZ() - bbMin.getZ() + 1;
		BlockPos p;
		for (int x = 0; x < w; x++) {
			for (int z = 0; z < h; z++) {
				if (isTarget(bbMin.getX() + x, bbMin.getZ() + z)) {
					p = bbMin.add(x, 0, z);
					targets.add(p);
				}
			}
		}
	}

	@Override
	protected void scanBlockPosition(BlockPos position) {
		IBlockState state = getWorld().getBlockState(position);
		if (getWorld().isAirBlock(position)) {
			state = getWorld().getBlockState(position.down());
			if (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS) {
				blocksToTill.add(position.down());
			} else if (state.getBlock() == Blocks.FARMLAND) {
				blocksToPlant.add(position);
			} else {
				for (int i = 27; i < 30; i++) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (ItemStackTools.isEmpty(stack)) {
						continue;
					}
					IPlantable plantable = FarmUtil.getSeedPlantable(stack);
					if(FarmUtil.canPlant(getWorld(), position, plantable)){
						blocksToPlant.add(position);
					}
				}
			}
		} else if (FarmUtil.isCrop(getWorld(), position)) {
			if (FarmUtil.isGrownCrop(getWorld(), position)) {
				if(FarmUtil.isStackedCrop(getWorld(), position)){
					if(!getWorld().isAirBlock(position.up())){
						blocksToHarvest.add(position);
					}
				}
				else blocksToHarvest.add(position);
			} else {
				blocksToFertilize.add(position);
			}
		} else if (state.getBlock() instanceof BlockStem) {
			if (state.getValue(BlockStem.AGE) >= 7) {
				stem : for(EnumFacing face : EnumFacing.HORIZONTALS){
					IBlockState state2 = getWorld().getBlockState(position.offset(face));
					if(state2.getBlock() == Blocks.MELON_BLOCK || state2.getBlock() == Blocks.PUMPKIN){
						blocksToHarvest.add(position.offset(face));
						break stem;
					}
				}
			} else {
				blocksToFertilize.add(position);
			}
		}
	}

	public void pickupItems() {
		BlockPos p1 = getWorkBoundsMin();
		BlockPos p2 = getWorkBoundsMax().add(1, 1, 1);
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2);
		List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, bb);
		ItemStack stack;
		for (EntityItem item : items) {
			stack = item.getEntityItem();
			if (ItemStackTools.isValid(stack)) {
				continue;
			}
			item.setDead();
			addStackToInventory(stack, RelativeSide.TOP);
			continue;
		}
	}

	public boolean giveHoe(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand().isEmpty()){
			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				ItemStack hoe = inventory.getStackInSlot(slot);
				if(ItemStackTools.isValid(hoe) && hoe.getItem() instanceof ItemHoe && !ToolUtil.isEmptyRfTool(hoe) && !ToolUtil.isBrokenTinkerTool(hoe)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, hoe);
					this.inventory.setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
					return true;
				}
			}
		}
		return false;
	}
	
	public WorkerFilter hoeFilter = new WorkerFilter(){
		@Override
		public boolean matches(EntityMinionWorker worker){
			ItemStack held = worker.getHeldItemMainhand();
			return ItemStackTools.isValid(held) && held.getItem() instanceof ItemHoe && !ToolUtil.isBrokenTinkerTool(held) && !ToolUtil.isEmptyRfTool(held);
		}
	};
	
	@Override
	protected boolean processWork() {
		Iterator<BlockPos> it;
		BlockPos position;
		IBlockState state;
		if (!blocksToTill.isEmpty()) {
			it = blocksToTill.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				state = getWorld().getBlockState(position);
				if (getWorld().isAirBlock(position.up()) && (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT)) {
					EntityMinionWorker worker = getRandomWorker(hoeFilter, WorkerFilter.idleFilter);
					if (worker != null) {
						worker.addCommand(new JobTillDirt(position));
						return true;
					}
				}
			}
		} else if (!blocksToHarvest.isEmpty()) {
			it = blocksToHarvest.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				IBlockState cropState = getWorld().getBlockState(position);
				boolean isGoard = (cropState.getBlock() == Blocks.MELON_BLOCK || cropState.getBlock() == Blocks.PUMPKIN);
				if (FarmUtil.isCrop(getWorld(), position) || isGoard) {
					boolean grown = FarmUtil.isGrownCrop(getWorld(), position);
					if (grown || isGoard) {
						EntityMinionWorker worker = getClosestWorker(position, WorkerFilter.idleFilter, WorkerFilter.anyFilter);
						if (worker != null) {
							worker.addCommand(new JobHarvestCrop(position));
							it.remove();
							return true;
						}
					}
				}
			}
		} else if (!blocksToPlant.isEmpty() && plantableCount > 0) {
			it = blocksToPlant.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				if (getWorld().isAirBlock(position)) {
					ItemStack stack = ItemStackTools.getEmptyStack();
					for (int i = 27; i < 30; i++) {
						stack = inventory.getStackInSlot(i);
						if (ItemStackTools.isEmpty(stack)) {
							continue;
						}
						IPlantable plantable = FarmUtil.getSeedPlantable(stack);
						if (plantable !=null) {
							if(FarmUtil.canPlant(getWorld(), position, plantable)) {
								EntityMinionWorker worker = getClosestWorker(position, WorkerFilter.idleFilter);
								if(worker == null){
									//Look for workers with hoes and use them
									EntityMinionWorker worker2 = getClosetWorkerRemoveTool(position, hoeFilter);
									if(worker2 !=null){
										worker = worker2;
									}
								}
								if(worker !=null){
									if(worker.addCommand(new JobPlantCrop(position))){
										ItemStack copy = ItemUtil.copy(stack, 1);
										plantableCount--;
										ItemStackTools.incStackSize(stack, -1);
										if (ItemStackTools.isEmpty(stack)) {
											inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
										}
										worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, copy);
										it.remove();
										return true;
									}
								}
							}
						}
					}
					return false;
				}
			}
		} else if (!blocksToFertilize.isEmpty() && bonemealCount > 0) {
			it = blocksToFertilize.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				if (FarmUtil.isCrop(getWorld(), position)	&& !FarmUtil.isGrownCrop(getWorld(), position)) {
					ItemStack stack = ItemStackTools.getEmptyStack();
					Item item;
					for (int i = 30; i < 33; i++) {
						stack = inventory.getStackInSlot(i);
						if (ItemStackTools.isEmpty(stack)) {
							continue;
						}
						item = stack.getItem();
						if (item == Items.DYE && stack.getItemDamage() == 15) {
							bonemealCount--;
							if(!getWorld().isRemote && ItemDye.applyBonemeal(stack, getWorld(), position)){
								if (ItemStackTools.isEmpty(stack)) {
									inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
								}
							}
							
							if (FarmUtil.isCrop(getWorld(), position)) {
								if (!FarmUtil.isGrownCrop(getWorld(),	position)) {
									blocksToFertilize.add(position);
								} else {
									blocksToHarvest.add(position);
								}
							}
							return true;
						}
					}
					return false;
				}
			}
		}
		return false;
	}

	
	
	@Override
	public WorkType getWorkType() {
		return WorkType.FARMING;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player) {
		if (!player.getEntityWorld().isRemote) {
			BlockUtil.openWorksiteGui(player,
					0, getPos().getX(), getPos().getY(), getPos().getZ());
			return true;
		}
		return true;
	}

	@Override
	protected boolean hasWorksiteWork() {
		return (plantableCount > 0 && !blocksToPlant.isEmpty())
				|| (bonemealCount > 0 && !blocksToFertilize.isEmpty())
				|| !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
	}

	@Override
	protected void updateBlockWorksite() {
		getWorld().theProfiler.startSection("Count Resources");
		if (shouldCountResources) {
			countResources();
		}
		getWorld().theProfiler.endSection();
	}

	@Override
	public boolean hasWork() {
		return this.hasWorksiteWork();
	}

	@Override
	public boolean renderBounds() {
		return true;
	}

	@Override
	public boolean isWorkerOkay(EntityMinionWorker minion) {
		return true;
	}

}