package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobChopTree;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobDestoryBlock;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobPlantCrop;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RotationType;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemSlotFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteUserBlocks;
import alec_wam.CrystalMod.tiles.machine.worksite.WorkerFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.HarvestResult;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.util.tool.ChorusPlantUtil;
import alec_wam.CrystalMod.util.tool.ChorusPlantUtil.ChorusPlantData;
import alec_wam.CrystalMod.util.tool.MultiHarvestComparator;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.util.tool.TreeHarvestUtil;
import alec_wam.CrystalMod.util.tool.TreeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.Constants;

public class WorksiteTreeFarm extends TileWorksiteUserBlocks {

	/**
	 * flag should be set to true whenever updating inventory internally (e.g.
	 * harvesting blocks) to prevent unnecessary inventory rescanning. should be
	 * set back to false after blocks are added to inventory
	 */
	private boolean shouldCountResources = true;
	int saplingCount;
	int chorusFlowerCount;
	int bonemealCount;
	Set<BlockPos> blocksToChop;
	List<BlockPos> blocksToPlant;
	List<BlockPos> blocksToFertilize;
	List<BlockPos> busyBlocks;
	List<BlockPos> junkBlocks;

	public WorksiteTreeFarm() {
		blocksToChop = new HashSet<BlockPos>();
		blocksToPlant = new ArrayList<BlockPos>();
		blocksToFertilize = new ArrayList<BlockPos>();
		busyBlocks = new ArrayList<BlockPos>();
		junkBlocks = new ArrayList<BlockPos>();

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
				if (ItemStackTools.isNullStack(stack)) {
					return true;
				}
				if (stack.getItem() instanceof ItemBlock) {
					ItemBlock item = (ItemBlock) stack.getItem();
					return item.getBlock() instanceof BlockSapling || item.getBlock() instanceof BlockChorusFlower;
				}
				return false;
			}
		};
		this.inventory.setFilterForSlots(filter, frontIndices);
		filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (ItemStackTools.isNullStack(stack)) {
					return true;
				}
				
				if(isAxe(stack))
				{
					return true;
				}
				if(stack.getItem() instanceof ItemShears)
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
	public EnumSet<WorksiteUpgrade> getValidUpgrades()
	{
		return EnumSet.of(
				WorksiteUpgrade.SIZE_MEDIUM,
				WorksiteUpgrade.SIZE_LARGE,
				WorksiteUpgrade.BASIC_CHUNK_LOADER
				);
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
		chorusFlowerCount = 0;
		bonemealCount = 0;
		ItemStack stack = ItemStackTools.getEmptyStack();
		for (int i = 27; i < 33; i++) {
			stack = inventory.getStackInSlot(i);
			if (!ItemStackTools.isValid(stack)) {
				continue;
			}
			if (stack.getItem() instanceof ItemBlock) {
				ItemBlock item = (ItemBlock) stack.getItem();
				if (item.getBlock() instanceof BlockSapling) {
					saplingCount += ItemStackTools.getStackSize(stack);
				}
				if (item.getBlock() instanceof BlockChorusFlower) {
					chorusFlowerCount += ItemStackTools.getStackSize(stack);
				}
			} else if (stack.getItem() == Items.DYE
					&& stack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
				bonemealCount += ItemStackTools.getStackSize(stack);
			}
		}
	}

	public static boolean isAxe(ItemStack axe){
		return ItemStackTools.isValid(axe) && ToolUtil.isAxe(axe) && !ToolUtil.isBrokenTinkerTool(axe) && !ToolUtil.isEmptyRfTool(axe);
	}
	
	public boolean giveAxe(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand().isEmpty()){
			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				ItemStack axe = inventory.getStackInSlot(slot);
				if(isAxe(axe)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, axe);
					this.inventory.setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean takeAxe(EntityMinionWorker worker) {
		if(ItemStackTools.isValid(worker.getHeldItemMainhand())){
			ItemStack held = worker.getHeldItemMainhand();
			if(ToolUtil.isBrokenTinkerTool(held) || ToolUtil.isEmptyRfTool(held)){
				if(addStackToInventoryNoDrop(held, false, RelativeSide.BOTTOM, RelativeSide.TOP)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
					return true;
				}
			}
		}
		return false;
	}
	
	public WorkerFilter axeFilter = new WorkerFilter(){
		@Override
		public boolean matches(EntityMinionWorker worker) {
			ItemStack axe = worker.getHeldItemMainhand();
			return isAxe(axe);
		}
	};
	
	public WorkerFilter axeFilterBack = new WorkerFilter(){
		@Override
		public boolean matches(EntityMinionWorker worker) {
			ItemStack axe = worker.getBackItem();
			return isAxe(axe);
		}
	};
	
	public boolean isFoliage(IBlockState state){
		Block block = state.getBlock();
		return block instanceof BlockTallGrass || block instanceof BlockFlower || block instanceof BlockDoublePlant;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean processWork() {
		//Cleanup foliage
		if(bbMax != null && bbMin != null){
			int w = bbMax.getX() - bbMin.getX() + 1;
			int h = bbMax.getZ() - bbMin.getZ() + 1;
			BlockPos p;
			for (int x = 0; x < w; x++) {
				for (int z = 0; z < h; z++) {
					//not in bounds
					if (!isTarget(bbMin.getX() + x, bbMin.getZ() + z)) {
						p = bbMin.add(x, 0, z);
						IBlockState state = world.getBlockState(p);
						if(state.getBlock() instanceof BlockSapling || isFoliage(state)){
							junkBlocks.add(p);
						}
					}
				}
			}
		}
		
		
		BlockPos position;
		if(!junkBlocks.isEmpty()){
			Iterator<BlockPos> it = junkBlocks.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				if(busyBlocks.contains(position))continue;
				EntityMinionWorker worker = getClosestWorker(position, WorkerFilter.idleFilter);
				if (worker != null) {
					final BlockPos workPos = position;
					if(worker.addCommand(new JobDestoryBlock(position, true){
						@Override
						public void onCompleted(EntityMinionWorker worker, TileWorksiteBase worksite){
							busyBlocks.remove(workPos);
						}
					})){
						busyBlocks.add(workPos);
						it.remove();
						return true;
					}
				}
			}
		}
		if (!blocksToChop.isEmpty()) {
			Iterator<BlockPos> it = blocksToChop.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				if(busyBlocks.contains(position))continue;
				EntityMinionWorker worker = getClosestWorker(position, axeFilter, axeFilterBack, WorkerFilter.idleFilter);
				if (worker != null) {
					final BlockPos workPos = position;
					if(worker.addCommand(new JobChopTree(position){
						@Override
						public void onCompleted(EntityMinionWorker worker, TileWorksiteBase worksite){
							busyBlocks.remove(workPos);
						}
					})){
						busyBlocks.add(workPos);
						it.remove();
						return true;
					}
				}
			}
		} else if (saplingCount > 0 && !blocksToPlant.isEmpty()) {
			ItemStack stack = ItemStackTools.getEmptyStack();
			BlockSapling saplingBlock = null;
			int slot = 27;
			for (int i = 27; i < 30; i++) {
				stack = inventory.getStackInSlot(i);
				if (ItemStackTools.isValid(stack)
						&& stack.getItem() instanceof ItemBlock
						&& ((ItemBlock) stack.getItem()).getBlock() instanceof BlockSapling) {
					saplingBlock = (BlockSapling)((ItemBlock) stack.getItem()).getBlock();
					slot = i;
					break;
				} else {
					stack = ItemStackTools.getEmptyStack();
				}
			}
			if (ItemStackTools.isValid(stack))// e.g. a sapling stack is present
			{
				Iterator<BlockPos> it = blocksToPlant.iterator();
				while (it.hasNext() && (position = it.next()) != null) {
					if(busyBlocks.contains(position))continue;
					if (getWorld().isAirBlock(position) && canPlantSapling(position, saplingBlock)) {
						EntityMinionWorker worker = getClosestWorker(position, WorkerFilter.idleFilter);
						if(worker == null){
							//Look for workers with axes and use them
							EntityMinionWorker worker2 = getClosestWorker(position, axeFilter);
							if(worker2 !=null){
								worker2.switchItems();
								//Remove any odd items
								ItemStack held = worker2.getHeldItemMainhand();
								if(ItemStackTools.isValid(held)){
									addStackToInventory(held, RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
									worker2.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
								}
								worker = worker2;
							}
						}
						if(worker !=null){
							final BlockPos workPos = position;
							if(worker.addCommand(new JobPlantCrop(position){
								@Override
								public void onCompleted(EntityMinionWorker worker, TileWorksiteBase worksite){
									busyBlocks.remove(workPos);
								}
							})){
								busyBlocks.add(workPos);
								ItemStack copy = ItemUtil.copy(stack, 1);
								saplingCount--;
								inventory.decrStackSize(slot, 1);
								worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, copy);
								it.remove();
								return true;
							}
						}
					}
				}
			}
		} else if (chorusFlowerCount > 0 && !blocksToPlant.isEmpty()) {
			ItemStack stack = ItemStackTools.getEmptyStack();
			int slot = 27;
			for (int i = 27; i < 30; i++) {
				stack = inventory.getStackInSlot(i);
				if (ItemStackTools.isValid(stack)
						&& stack.getItem() instanceof ItemBlock
						&& ((ItemBlock) stack.getItem()).getBlock() instanceof BlockChorusFlower) {
					slot = i;
					break;
				} else {
					stack = ItemStackTools.getEmptyStack();
				}
			}
			if (ItemStackTools.isValid(stack))
			{
				Iterator<BlockPos> it = blocksToPlant.iterator();
				while (it.hasNext() && (position = it.next()) != null) {
					if(busyBlocks.contains(position))continue;
					if (getWorld().isAirBlock(position) && canPlantChorus(position)) {
						EntityMinionWorker worker = getClosestWorker(position, WorkerFilter.idleFilter);
						if(worker == null){
							//Look for workers with axes and use them
							EntityMinionWorker worker2 = getClosestWorker(position, axeFilter);
							if(worker2 !=null){
								worker2.switchItems();
								//Remove any odd items
								ItemStack held = worker2.getHeldItemMainhand();
								if(ItemStackTools.isValid(held)){
									addStackToInventory(held, RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
									worker2.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
								}
								worker = worker2;
							}
						}
						if(worker !=null){
							final BlockPos workPos = position;
							if(worker.addCommand(new JobPlantCrop(position){
								@Override
								public void onCompleted(EntityMinionWorker worker, TileWorksiteBase worksite){
									busyBlocks.remove(workPos);
								}
							})){
								busyBlocks.add(workPos);
								ItemStack copy = ItemUtil.copy(stack, 1);
								chorusFlowerCount--;
								inventory.decrStackSize(slot, 1);
								worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, copy);
								it.remove();
								return true;
							}
						}
					}
				}
			}
		} else if (bonemealCount > 0 && !blocksToFertilize.isEmpty()) {
			Iterator<BlockPos> it = blocksToFertilize.iterator();
			while (it.hasNext() && (position = it.next()) != null) {
				it.remove();
				IBlockState state = getWorld().getBlockState(position);
				if (state.getBlock() instanceof BlockSapling) {
					ItemStack stack = ItemStackTools.getEmptyStack();
					for (int i = 30; i < 33; i++) {
						stack = inventory.getStackInSlot(i);
						if (stack != null && stack.getItem() == Items.DYE
								&& stack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
							bonemealCount--;
							ItemDye.applyBonemeal(stack, getWorld(), position, FakePlayerUtil.getPlayer((WorldServer)getWorld()));
							if (ItemStackTools.isEmpty(stack)) {
								inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
							}
							state = getWorld().getBlockState(position);
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

	private boolean canPlantSapling(BlockPos pos, BlockSapling sapling){
		return sapling.canPlaceBlockAt(getWorld(), pos);
	}
	
	private boolean canPlantChorus(BlockPos pos){
		return Blocks.CHORUS_FLOWER.canPlaceBlockAt(getWorld(), pos);
	}
			
	private void pickupSaplings() {
		BlockPos p1 = getWorkBoundsMin();
		BlockPos p2 = getWorkBoundsMax().add(1, 1, 1);
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2);
		List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, bb);
		ItemStack stack;
		for (EntityItem item : items) {
			stack = item.getEntityItem();
			if (!ItemStackTools.isValid(stack)) {
				continue;
			}
			if (stack.getItem() == Items.APPLE) {
				item.setDead();
				addStackToInventory(stack, RelativeSide.TOP);
				continue;
			}
			if (stack.getItem() == Items.CHORUS_FRUIT) {
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
				if (ib.getBlock() instanceof BlockChorusFlower) {
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
		getWorld().theProfiler.startSection("TreeFinder");
		// TreeFinder.findAttachedTreeBlocks(getWorld().getBlock(base.x, base.y,
		// base.z), getWorld(), base.x, base.y, base.z, blocksToChop);
		blocksToChop.add(base);
		getWorld().theProfiler.endSection();
	}
	
	private void addChorusBlock(BlockPos base) {
		getWorld().theProfiler.startSection("ChorusFinder");
		//ModLogger.info("Found Chorus Plant");
		if(ChorusPlantUtil.isFullyGrownPlant(getWorld(), base))blocksToChop.add(base);
		getWorld().theProfiler.endSection();
	}
	
	@Override
	public WorkType getWorkType() {
		return WorkType.FORESTRY;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player) {
		if (!player.getEntityWorld().isRemote) {
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
				posTag = BlockUtil.saveBlockPos(position);
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
				blocksToChop.add(BlockUtil.loadBlockPos(posTag));
			}
		}
		this.shouldCountResources = true;
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
	protected void scanBlockPosition(BlockPos pos) {
		IBlockState state;
		if (getWorld().isAirBlock(pos)) {
			state = getWorld().getBlockState(pos.down());
			//TODO handle canSustainPlant for saplings
			if (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.END_STONE) {
				blocksToPlant.add(pos);
			}
		} else {
			state = getWorld().getBlockState(pos);
			if(isFoliage(state) && !junkBlocks.contains(pos)){
				junkBlocks.add(pos);
			}
			else if (state.getBlock() instanceof BlockSapling) {
				blocksToFertilize.add(pos);
			} 
			else if (TreeUtil.isLog(state)) {
				if (!blocksToChop.contains(pos)) {
					addTreeBlocks(pos);
				}
			}else if (ChorusPlantUtil.isChorusPlant(state)) {
				if (!blocksToChop.contains(pos)) {
					addChorusBlock(pos);
				}
			}
		}
	}

	@Override
	protected boolean hasWorksiteWork() {
		return (bonemealCount > 0 && !blocksToFertilize.isEmpty())
				|| ((saplingCount > 0 || chorusFlowerCount > 0) && !blocksToPlant.isEmpty())
				|| !blocksToChop.isEmpty();
	}

	@Override
	protected void updateBlockWorksite() {
		getWorld().theProfiler.startSection("Count Resources");
		if (shouldCountResources) {
			countResources();
		}
		getWorld().theProfiler.endStartSection("SaplingPickup");
		if (getWorld().getWorldTime() % 20 == 0) {
			pickupSaplings();
		}
		getWorld().theProfiler.endStartSection("MinionAxe");
		if (getWorld().getWorldTime() % 20 == 0) {
			manageAxes();
		}
		getWorld().theProfiler.endSection();
	}

	@Override
	public boolean hasWork() {
		return hasWorksiteWork();
	}

	@Override
	public void onPostBoundsAdjusted() {
		super.onPostBoundsAdjusted();
	}

	@Override
	public void onBlockBroken() {
		super.onBlockBroken();
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
	            
		boolean hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		for(int i=0; hasAxe && i < sortedTargets.size();i++) {
			BlockPos bc = sortedTargets.get(i);
			IBlockState bs = getWorld().getBlockState(bc);
			int fourtune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
	  		if(fourtune < 2)fourtune += getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
	  		
	  		List<ItemStack> itemDrops = null;
	  		boolean sheared = false;
  			if(TreeHarvestUtil.isLeaves(bs, getWorld(), bc)){
	  			ItemStack shears = ItemStackTools.getEmptyStack();
	  			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
	  			slots : for(int s = 0; s < slots.length; s++){
					int slot = slots[s];
					ItemStack item = inventory.getStackInSlot(slot);
					if(item.getItem() instanceof ItemShears){
						shears = item;
						break slots;
					}
				}
	  			if(ItemStackTools.isValid(shears)){
	  				if(bs.getBlock() instanceof IShearable){
	  					itemDrops = ((IShearable)bs.getBlock()).onSheared(shears, getWorld(), bc, fourtune);
	  					getWorld().setBlockToAir(bc);
	  					if (shears.attemptDamageItem(1, getWorld().rand))
	  	                {
	  						shears.shrink(1);
	  	                }
	  					sheared = true;
	  				}
	  			} 
	  		} 
  			if(!sheared){
	  			itemDrops = TreeUtil.doMultiHarvest(stack, getWorld(), bc, block, fourtune);	  			
	  		}
	  		for(ItemStack drop : itemDrops){
	  			addStackToInventory(drop, RelativeSide.FRONT, RelativeSide.TOP);
	  		}
	  		hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		}
        return true;
    }
	
	public boolean chopDownChorus(ItemStack stack, BlockPos pos)
    {
		if(getWorld().isRemote) return false;
		IBlockState state = getWorld().getBlockState(pos);
		Block block = state.getBlock();
		if(!ChorusPlantUtil.isChorusPlant(state)) return false;
		ChorusPlantData data = ChorusPlantUtil.buildPlantData(getWorld(), pos);
		  
		//Harvest Flowers First
		List<BlockPos> sortedTargets = new ArrayList<BlockPos>(data.flowerList);	            
		boolean hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		for(int i=0; hasAxe && i < sortedTargets.size(); i++) {
			BlockPos bc = sortedTargets.get(i);
			ItemStack flowerStack = new ItemStack(Blocks.CHORUS_FLOWER);
			getWorld().playEvent(2001, bc, Block.getStateId(state));
			getWorld().setBlockToAir(bc);
		    addStackToInventory(flowerStack, RelativeSide.FRONT, RelativeSide.TOP);
	  		hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		}
		
		//Now harvest plant		
		hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		sortedTargets = new ArrayList<BlockPos>(data.plantList);
		harvestComparator.refPoint = pos;
		Collections.sort(sortedTargets, harvestComparator);
		for(int i=0; hasAxe && i < sortedTargets.size();i++) {
			BlockPos bc = sortedTargets.get(i);
			int fourtune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
	  		if(fourtune < 2)fourtune += getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
	  		
	  		List<ItemStack> itemDrops = TreeUtil.doMultiHarvest(stack, getWorld(), bc, block, fourtune);
	  		for(ItemStack drop : itemDrops){
	  			addStackToInventory(drop, RelativeSide.FRONT, RelativeSide.TOP);
	  		}
	  		hasAxe = (ItemStackTools.isValid(stack) && !ToolUtil.isBrokenTinkerTool(stack) && !ToolUtil.isEmptyRfTool(stack));
		}
        return true;
    }

}
