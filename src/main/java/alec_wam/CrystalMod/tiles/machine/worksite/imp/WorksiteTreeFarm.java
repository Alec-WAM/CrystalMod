package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobChopTree;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemSlotFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteUserBlocks;
import alec_wam.CrystalMod.tiles.machine.worksite.WorkerFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RotationType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.HarvestResult;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.util.tool.MultiHarvestComparator;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil;
import alec_wam.CrystalMod.util.tool.TreeUtil;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;

public class WorksiteTreeFarm extends TileWorksiteUserBlocks {

	/**
	 * flag should be set to true whenever updating inventory internally (e.g.
	 * harvesting blocks) to prevent unnecessary inventory rescanning. should be
	 * set back to false after blocks are added to inventory
	 */
	private boolean shouldCountResources = true;
	int saplingCount;
	int bonemealCount;
	Set<BlockPos> blocksToChop;
	List<BlockPos> blocksToPlant;
	List<BlockPos> blocksToFertilize;

	public WorksiteTreeFarm() {
		blocksToChop = new HashSet<BlockPos>();
		blocksToPlant = new ArrayList<BlockPos>();
		blocksToFertilize = new ArrayList<BlockPos>();

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
				RelativeSide.FRONT, frontIndices);// saplings
		this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM,
				RelativeSide.BOTTOM, bottomIndices);// bonemeal
		ItemSlotFilter filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack == null) {
					return true;
				}
				if (stack.getItem() instanceof ItemBlock) {
					ItemBlock item = (ItemBlock) stack.getItem();
					return item.getBlock() instanceof BlockSapling;
				}
				return false;
			}
		};
		this.inventory.setFilterForSlots(filter, frontIndices);
		filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack == null) {
					return true;
				}
				
				if(ToolUtil.isAxe(stack))
				{
					return true;
				}
				return stack.getItem() == Items.DYE
						&& stack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage();
			}
		};
		this.inventory.setFilterForSlots(filter, bottomIndices);
	}

	@Override
	public void onTargetsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToChop);
		validateCollection(blocksToPlant);
	}

	@Override
	public void onBoundsAdjusted() {
		validateCollection(blocksToFertilize);
		validateCollection(blocksToChop);
		validateCollection(blocksToPlant);
	}

	private void countResources() {
		shouldCountResources = false;
		saplingCount = 0;
		bonemealCount = 0;
		ItemStack stack;
		for (int i = 27; i < 33; i++) {
			stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}
			if (stack.getItem() instanceof ItemBlock) {
				ItemBlock item = (ItemBlock) stack.getItem();
				if (item.getBlock() instanceof BlockSapling) {
					saplingCount += stack.stackSize;
				}
			} else if (stack.getItem() == Items.DYE
					&& stack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
				bonemealCount += stack.stackSize;
			}
		}
	}

	public boolean giveAxe(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand() == null){
			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				ItemStack axe = inventory.getStackInSlot(slot);
				if(axe !=null && ToolUtil.isAxe(axe) && !ToolUtil.isBrokenTinkerTool(axe) && !ToolUtil.isEmptyRfTool(axe)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, axe);
					this.inventory.setInventorySlotContents(slot, null);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean takeAxe(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand() != null){
			ItemStack held = worker.getHeldItemMainhand();
			if(ToolUtil.isBrokenTinkerTool(held) || ToolUtil.isEmptyRfTool(held)){
				if(addStackToInventoryNoDrop(held, false, RelativeSide.BOTTOM, RelativeSide.TOP)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean processWork() {
		BlockPos position;
		if (!blocksToChop.isEmpty()) {
			Iterator<BlockPos> it = blocksToChop.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				EntityMinionWorker worker = getRandomWorker(new WorkerFilter(){
					public boolean matches(EntityMinionWorker worker) {
						ItemStack axe = worker.getHeldItemMainhand();
						return axe !=null && ToolUtil.isAxe(axe) && !ToolUtil.isBrokenTinkerTool(axe) && !ToolUtil.isEmptyRfTool(axe);
					}
				}, WorkerFilter.idleFilter);
				if (worker != null) {
					if(worker.addCommand(new JobChopTree(position))){
						it.remove();
						return true;
					}
				}
			}
		} else if (saplingCount > 0 && !blocksToPlant.isEmpty()) {
			ItemStack stack = null;
			int slot = 27;
			for (int i = 27; i < 30; i++) {
				stack = inventory.getStackInSlot(i);
				if (stack != null
						&& stack.getItem() instanceof ItemBlock
						&& ((ItemBlock) stack.getItem()).getBlock() instanceof BlockSapling) {
					slot = i;
					break;
				} else {
					stack = null;
				}
			}
			if (stack != null)// e.g. a sapling stack is present
			{
				Iterator<BlockPos> it = blocksToPlant.iterator();
				while (it.hasNext() && (position = it.next()) != null) {
					it.remove();
					if (worldObj.isAirBlock(position)) {
						Block block = ((ItemBlock) stack.getItem()).getBlock();
						int i = ((ItemBlock) stack.getItem()).getMetadata(stack.getMetadata());
						worldObj.setBlockState(position, block.getStateFromMeta(i), 3);
						saplingCount--;
						inventory.decrStackSize(slot, 1);
						return true;
					}
				}
			}
		} else if (bonemealCount > 0 && !blocksToFertilize.isEmpty()) {
			Iterator<BlockPos> it = blocksToFertilize.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				/*List<EntityLivingBase> workers2 = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(position, position.add(1, 1, 1)));
				if (!workers2.isEmpty()) {
					continue;
				}*/
				IBlockState state = worldObj.getBlockState(position);
				if (state.getBlock() instanceof BlockSapling) {
					ItemStack stack = null;
					for (int i = 30; i < 33; i++) {
						stack = inventory.getStackInSlot(i);
						if (stack != null && stack.getItem() == Items.DYE
								&& stack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
							bonemealCount--;
							ItemDye.applyBonemeal(stack, worldObj, position, FakePlayerUtil.getPlayer((WorldServer)worldObj));
							if (stack.stackSize <= 0) {
								inventory.setInventorySlotContents(i, null);
							}
							state = worldObj.getBlockState(position);
							if (state.getBlock() instanceof BlockSapling) {
								blocksToFertilize.add(position);
							} else if (TreeUtil.isLog(state)) {
								blocksToChop.add(position);
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

	private void pickupSaplings() {
		BlockPos p1 = getWorkBoundsMin();
		BlockPos p2 = getWorkBoundsMax().add(1, 1, 1);
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2);
		List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);
		ItemStack stack;
		for (EntityItem item : items) {
			stack = item.getEntityItem();
			if (stack == null) {
				continue;
			}
			if (stack.getItem() == Items.APPLE) {
				item.setDead();
				addStackToInventory(stack, RelativeSide.TOP);
				continue;
			}
			if (stack.getItem() instanceof ItemBlock) {
				ItemBlock ib = (ItemBlock) stack.getItem();
				if (ib.getBlock() instanceof BlockSapling) {
					if (!ItemUtil.canInventoryHold(inventory, inventory
							.getRawIndicesCombined(RelativeSide.FRONT,
									RelativeSide.TOP), stack)) {
						break;
					}
					item.setDead();
					addStackToInventory(stack, RelativeSide.FRONT,
							RelativeSide.TOP);
				}
				if (TreeUtil.isLog(stack)) {
					if (!ItemUtil.canInventoryHold(inventory, inventory
							.getRawIndicesCombined(RelativeSide.TOP), stack)) {
						break;
					}
					item.setDead();
					addStackToInventory(stack, RelativeSide.TOP);
				}
			}
		}
	}
	
	public void manageAxes(){
		Iterator<EntityMinionWorker> iter = workers.iterator();
		while(iter.hasNext()){
			EntityMinionWorker worker = iter.next();
			if(worker !=null){
				takeAxe(worker);
			}
		}
	}

	private void addTreeBlocks(BlockPos base) {
		worldObj.theProfiler.startSection("TreeFinder");
		// TreeFinder.findAttachedTreeBlocks(worldObj.getBlock(base.x, base.y,
		// base.z), worldObj, base.x, base.y, base.z, blocksToChop);
		blocksToChop.add(base);
		worldObj.theProfiler.endSection();
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FORESTRY;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			BlockUtil.openWorksiteGui(player, 0, getPos().getX(), getPos().getY(), getPos().getZ());
			return true;
		}
		return true;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		super.writeCustomNBT(tag);
		if (!blocksToChop.isEmpty()) {
			NBTTagList chopList = new NBTTagList();
			NBTTagCompound posTag;
			for (BlockPos position : blocksToChop) {
				posTag = new NBTTagCompound();
				posTag.setInteger("x", position.getX());
				posTag.setInteger("y", position.getY());
				posTag.setInteger("z", position.getZ());
				chopList.appendTag(posTag);
			}
			tag.setTag("targetList", chopList);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		super.readCustomNBT(tag);
		if (tag.hasKey("targetList")) {
			NBTTagList chopList = tag.getTagList("targetList",
					Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < chopList.tagCount(); i++) {
				NBTTagCompound posTag = chopList.getCompoundTagAt(i);
				blocksToChop.add(new BlockPos(posTag.getInteger("x"), posTag.getInteger("y"), posTag.getInteger("z")));
			}
		}
		this.shouldCountResources = true;
	}

	@Override
	protected void fillBlocksToProcess(Collection<BlockPos> targets) {
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
	protected void scanBlockPosition(BlockPos pos) {
		IBlockState state;
		if (worldObj.isAirBlock(pos)) {
			state = worldObj.getBlockState(pos.down());
			if (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS) {
				blocksToPlant.add(pos);
			}
		} else {
			state = worldObj.getBlockState(pos);
			if (state.getBlock() instanceof BlockSapling) {
				blocksToFertilize.add(pos);
			} else if (TreeUtil.isLog(state)) {
				if (!blocksToChop.contains(pos)) {
					addTreeBlocks(pos);
				}
			}
		}
	}

	@Override
	protected boolean hasWorksiteWork() {
		return (bonemealCount > 0 && !blocksToFertilize.isEmpty())
				|| (saplingCount > 0 && !blocksToPlant.isEmpty())
				|| !blocksToChop.isEmpty();
	}

	@Override
	protected void updateBlockWorksite() {
		worldObj.theProfiler.startSection("Count Resources");
		if (shouldCountResources) {
			countResources();
		}
		worldObj.theProfiler.endStartSection("SaplingPickup");
		if (worldObj.getWorldTime() % 20 == 0) {
			pickupSaplings();
		}
		worldObj.theProfiler.endStartSection("MinionAxe");
		if (worldObj.getWorldTime() % 20 == 0) {
			manageAxes();
		}
		worldObj.theProfiler.endSection();
	}

	@Override
	public boolean hasWork() {
		return hasWorksiteWork();
	}

	@Override
	public void onPostBoundsAdjusted() {

	}

	@Override
	public void onBlockBroken() {

	}

	@Override
	public boolean isWorkerOkay(EntityMinionWorker minion) {
		return true;
	}

	@Override
	public boolean renderBounds() {
		return true;
	}
	

	private static final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
	public boolean chopDownTree(ItemStack stack, BlockPos pos)
    {
		if(getWorld().isRemote) return false;
		IBlockState state = getWorld().getBlockState(pos);
		Block block = state.getBlock();
		if(!TreeUtil.isLog(state)) return false;
		TreeHarvestUtil harvester = new TreeHarvestUtil();
		HarvestResult res = new HarvestResult();
		harvester.harvest(getWorld(), pos, res);
	      
		List<BlockPos> sortedTargets = new ArrayList<BlockPos>(res.getHarvestedBlocks());
		harvestComparator.refPoint = pos;
		Collections.sort(sortedTargets, harvestComparator);
	            
		boolean hasAxe = (stack !=null && stack.stackSize > 0 && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		for(int i=0; hasAxe && i < sortedTargets.size();i++) {
			BlockPos bc = sortedTargets.get(i);
			int fourtune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
	  		if(fourtune < 2)fourtune += getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
	  		
	  		List<ItemStack> itemDrops = TreeUtil.doMultiHarvest(stack, getWorld(), bc, block, fourtune);
	  		for(ItemStack drop : itemDrops){
	  			addStackToInventory(drop, RelativeSide.FRONT, RelativeSide.TOP);
	  		}
	  		hasAxe = (stack !=null && stack.stackSize > 0 && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		}
        return true;
    }

}
