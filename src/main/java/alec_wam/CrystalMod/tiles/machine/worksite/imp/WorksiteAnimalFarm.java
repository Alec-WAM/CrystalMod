package alec_wam.CrystalMod.tiles.machine.worksite.imp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobKillEntity;
import alec_wam.CrystalMod.entities.minions.worker.jobs.JobShearEntity;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RotationType;
import alec_wam.CrystalMod.tiles.machine.worksite.ItemSlotFilter;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBoundedInventory;
import alec_wam.CrystalMod.tiles.machine.worksite.WorkerFilter;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class WorksiteAnimalFarm extends TileWorksiteBoundedInventory {

	private int workerRescanDelay;
	private boolean shouldCountResources;

	public int maxPigCount = 6;
	public int maxCowCount = 6;
	public int maxChickenCount = 6;
	public int maxSheepCount = 6;

	private int wheatCount;
	private int bucketCount;
	private int carrotCount;
	private int seedCount;

	private List<EntityPair> pigsToBreed = new ArrayList<EntityPair>();
	private List<EntityPair> chickensToBreed = new ArrayList<EntityPair>();
	private List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
	private List<Integer> cowsToMilk = new ArrayList<Integer>();
	private List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();
	private List<Integer> sheepToShear = new ArrayList<Integer>();
	private List<Integer> entitiesToCull = new ArrayList<Integer>();

	public WorksiteAnimalFarm() {
		this.shouldCountResources = true;

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
				RelativeSide.FRONT, frontIndices);// feed
		this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM,
				RelativeSide.BOTTOM, bottomIndices);// buckets/shears
		ItemSlotFilter filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (!ItemStackTools.isValid(stack)) {
					return true;
				}
				Item item = stack.getItem();
				if (item == Items.WHEAT_SEEDS || item == Items.WHEAT
						|| item == Items.CARROT) {
					return true;
				}
				return false;
			}

			@Override
			public String toString() {
				return "Anon filter -- wheat / seeds / carrot";
			}
		};
		inventory.setFilterForSlots(filter, frontIndices);

		filter = new ItemSlotFilter() {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (!ItemStackTools.isValid(stack)) {
					return true;
				}
				Item item = stack.getItem();
				if (item == Items.BUCKET || item instanceof ItemShears || item instanceof ItemSword) {
					return true;
				}
				return false;
			}

			@Override
			public String toString() {
				return "Anon filter -- bucket / shears / sword";
			}
		};
		inventory.setFilterForSlots(filter, bottomIndices);
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}

	@Override
	protected boolean hasWorksiteWork() {
		return hasAnimalWork();
	}

	public boolean takeBrokenItems(EntityMinionWorker worker) {
		if(!ItemStackTools.isNullStack(worker.getHeldItemMainhand())){
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
	
	@Override
	protected void updateWorksite() {
		getWorld().theProfiler.startSection("Count Resources");
		if (shouldCountResources) {
			countResources();
		}
		getWorld().theProfiler.endStartSection("Animal Rescan");
		workerRescanDelay--;
		if (workerRescanDelay <= 0) {
			rescan();
		}
		getWorld().theProfiler.endStartSection("EggPickup");
		if (getWorld().getWorldTime() % 20 == 0) {
			pickupEggs();
		}
		getWorld().theProfiler.endStartSection("ToolManager");
		Iterator<EntityMinionWorker> iter = workers.iterator();
		while(iter.hasNext()){
			EntityMinionWorker worker = iter.next();
			if(worker !=null){
				takeBrokenItems(worker);
			}
		}
		getWorld().theProfiler.endSection();
	}

	private void countResources() {

		this.shouldCountResources = false;
		carrotCount = 0;
		seedCount = 0;
		wheatCount = 0;
		bucketCount = 0;
		ItemStack stack;
		for (int i = 27; i < 30; i++) {
			stack = inventory.getStackInSlot(i);
			if (!ItemStackTools.isValid(stack)) {
				continue;
			}
			if (stack.getItem() == Items.CARROT) {
				carrotCount += ItemStackTools.getStackSize(stack);
			} else if (stack.getItem() == Items.WHEAT_SEEDS) {
				seedCount += ItemStackTools.getStackSize(stack);
			} else if (stack.getItem() == Items.WHEAT) {
				wheatCount += ItemStackTools.getStackSize(stack);
			}
		}
		for (int i = 30; i < 33; i++) {
			stack = inventory.getStackInSlot(i);
			if (!ItemStackTools.isValid(stack)) {
				continue;
			}
			if (stack.getItem() == Items.BUCKET) {
				bucketCount += ItemStackTools.getStackSize(stack);
			} 
		}
		// AWLog.logDebug("counting animal farm resources.."+wheatCount+","+seedCount+","+carrotCount+","+bucketCount+","+shears);
	}

	private void rescan() {
		// AWLog.logDebug("rescanning animal farm");
		getWorld().theProfiler.startSection("Animal Rescan");
		pigsToBreed.clear();
		cowsToBreed.clear();
		cowsToMilk.clear();
		sheepToBreed.clear();
		chickensToBreed.clear();
		entitiesToCull.clear();

		BlockPos min = getWorkBoundsMin();
		BlockPos max = getWorkBoundsMax();
		AxisAlignedBB bb = new AxisAlignedBB(min, max.add(1, 1, 1));

		List<EntityAnimal> entityList = getWorld().getEntitiesWithinAABB(
				EntityAnimal.class, bb);

		List<EntityAnimal> cows = new ArrayList<EntityAnimal>();
		List<EntityAnimal> pigs = new ArrayList<EntityAnimal>();
		List<EntityAnimal> sheep = new ArrayList<EntityAnimal>();
		List<EntityAnimal> chickens = new ArrayList<EntityAnimal>();

		for (EntityAnimal animal : entityList) {
			if (animal instanceof EntityCow) {
				cows.add(animal);
			} else if (animal instanceof EntityChicken) {
				chickens.add(animal);
			} else if (animal instanceof IShearable) {
				sheep.add(animal);
			} else if (animal instanceof EntityPig) {
				pigs.add(animal);
			}
		}

		scanForCows(cows);
		scanForSheep(sheep);
		scanForAnimals(chickens, chickensToBreed, maxChickenCount);
		scanForAnimals(pigs, pigsToBreed, maxPigCount);
		workerRescanDelay = 200;
		getWorld().theProfiler.endSection();
	}

	public boolean hasBreedingItem(EntityAnimal animal) {
		for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
			if (this.inventory.getStackInSlot(i) != null
					&& animal.isBreedingItem(this.inventory.getStackInSlot(i)))
				return true;
		}
		return false;
	}

	public ItemStack getBreedingItem(EntityAnimal animal) {
		for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
			if (ItemStackTools.isValid(this.inventory.getStackInSlot(i))
					&& animal.isBreedingItem(this.inventory.getStackInSlot(i)))
				return this.inventory.getStackInSlot(i).copy();
		}
		return ItemStackTools.getEmptyStack();
	}

	private void scanForAnimals(List<EntityAnimal> animals,
			List<EntityPair> targets, int maxCount) {
		EntityAnimal animal1;
		EntityAnimal animal2;
		EntityPair breedingPair;

		int age;

		for (int i = 0; i < animals.size(); i++) {
			animal1 = animals.get(i);
			age = animal1.getGrowingAge();

			if (age != 0 || animal1.isInLove()) {
				continue;
			}// unbreedable first-target, skip
			while (i + 1 < animals.size())// loop through remaining animals to
											// find a breeding partner
			{
				i++;
				animal2 = animals.get(i);
				age = animal2.getGrowingAge();
				if (age == 0 && !animal2.isInLove())// found a second breedable
													// animal, add breeding
													// pair, exit to outer loop
				{
					breedingPair = new EntityPair(animal1, animal2);
					targets.add(breedingPair);
					break;
				}
			}
		}

		int grownCount = 0;
		for (EntityAnimal animal : animals) {
			if (animal.getGrowingAge() >= 0) {
				grownCount++;
			}
		}

		if (grownCount > maxCount) {
			for (int i = 0, cullCount = grownCount - maxCount; i < animals
					.size() && cullCount > 0; i++) {
				if (animals.get(i).getGrowingAge() >= 0) {
					entitiesToCull.add(animals.get(i).getEntityId());
					cullCount--;
				}
			}
		}
	}

	private void scanForSheep(List<EntityAnimal> sheep) {
		scanForAnimals(sheep, sheepToBreed, maxSheepCount);
		for (EntityAnimal animal : sheep) {
			if (animal instanceof IShearable) {
				IShearable sheep1 = (IShearable) animal;
				if (sheep1.isShearable(null, getWorld(), new BlockPos(animal))) {
					sheepToShear.add(animal.getEntityId());
				}
			}
		}
	}

	private void scanForCows(List<EntityAnimal> animals) {
		scanForAnimals(animals, cowsToBreed, maxCowCount);
		for (EntityAnimal animal : animals) {
			if (animal.getGrowingAge() >= 0) {
				cowsToMilk.add(animal.getEntityId());
			}
		}
	}

	@Override
	public boolean hasWork() {
		return hasWorksiteWork();
	}

	@Override
	public boolean renderBounds() {
		return true;
	}

	@Override
	public boolean isWorkerOkay(EntityMinionWorker minion) {
		return true;
	}

	@Override
	protected boolean processWork() {
		// AWLog.logDebug("processing animal farm work!");

		boolean didWork = false;
		boolean canBreed = !workers.isEmpty();
		if(canBreed){
			if (!cowsToBreed.isEmpty() && wheatCount >= 2) {
				didWork = tryBreeding(cowsToBreed);
				if (didWork) {
					wheatCount -= 2;
					ItemUtil.removeItems(inventory,
							inventory.getAccessDirectionFor(RelativeSide.FRONT),
							new ItemStack(Items.WHEAT), 2);
					return true;
				}
			}
			if (!sheepToBreed.isEmpty() && wheatCount >= 2) {
				didWork = tryBreeding(sheepToBreed);
				if (didWork) {
					wheatCount -= 2;
					ItemUtil.removeItems(inventory,
							inventory.getAccessDirectionFor(RelativeSide.FRONT),
							new ItemStack(Items.WHEAT), 2);
					return true;
				}
			}
			if (!chickensToBreed.isEmpty() && seedCount >= 2) {
				didWork = tryBreeding(chickensToBreed);
				if (didWork) {
					seedCount -= 2;
					ItemUtil.removeItems(inventory,
							inventory.getAccessDirectionFor(RelativeSide.FRONT),
							new ItemStack(Items.WHEAT_SEEDS), 2);
					return true;
				}
			}
			if (!pigsToBreed.isEmpty() && carrotCount >= 2) {
				didWork = tryBreeding(pigsToBreed);
				if (didWork) {
					carrotCount -= 2;
					ItemUtil.removeItems(inventory,
							inventory.getAccessDirectionFor(RelativeSide.FRONT),
							new ItemStack(Items.CARROT), 2);
					return true;
				}
			}
		}
		if (!sheepToShear.isEmpty()) {
			didWork = tryShearing(sheepToShear);
			if (didWork) {
				return true;
			}
		}
		if (bucketCount > 0 && !cowsToMilk.isEmpty()) {
			didWork = tryMilking(cowsToMilk);
			if (didWork) {
				ItemUtil.removeItems(inventory,
						inventory.getAccessDirectionFor(RelativeSide.BOTTOM),
						new ItemStack(Items.BUCKET), 1);
				this.addStackToInventory(new ItemStack(Items.MILK_BUCKET),
						RelativeSide.TOP);
				return true;
			}
		}
		if (!entitiesToCull.isEmpty()) {
			if (tryCulling(entitiesToCull)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryBreeding(List<EntityPair> targets) {
		Entity animalA;
		Entity animalB;
		EntityPair pair;
		if (!targets.isEmpty()) {
			pair = targets.remove(0);
			animalA = pair.getEntityA(getWorld());
			animalB = pair.getEntityB(getWorld());
			if (!(animalA instanceof EntityAnimal)
					|| !(animalB instanceof EntityAnimal)) {
				return false;
			}
			if (!hasBreedingItem((EntityAnimal) animalA))
				return false;
			((EntityAnimal) animalA).setInLove(null);// setInLove(EntityPlayer
															// breeder)
			((EntityAnimal) animalB).setInLove(null);// setInLove(EntityPlayer
															// breeder)
			return true;
		}
		return false;
	}

	private boolean tryMilking(List<Integer> targets) {
		if (targets.isEmpty()) {
			return false;
		}
		EntityCow cow = (EntityCow) getWorld().getEntityByID(targets.remove(0));
		if (cow == null) {
			return false;
		}

		return true;
	}

	public boolean giveShears(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand() == null){
			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				ItemStack shears = inventory.getStackInSlot(slot);
				if(ItemStackTools.isValid(shears) && shears.getItem() instanceof ItemShears && !ToolUtil.isEmptyRfTool(shears)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, shears);
					this.inventory.setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean tryShearing(List<Integer> targets) {
		if (targets.isEmpty()) {
			return false;
		}

		EntityMinionWorker worker = getRandomWorker(new WorkerFilter(){
			public boolean matches(EntityMinionWorker worker){
				ItemStack held = worker.getHeldItemMainhand();
				return !ItemStackTools.isNullStack(held) && held.getItem() instanceof ItemShears;
			}
		}, WorkerFilter.idleFilter);
		if(worker == null){
			return false;
		}
		
		
		EntityLivingBase sheep = (EntityLivingBase) getWorld().getEntityByID(targets.remove(0));
		if (sheep == null || !(sheep instanceof IShearable)) {
			return false;
		}
		IShearable shear = (IShearable)sheep;
		if (!shear.isShearable(null, getWorld(), new BlockPos(sheep))) {
			return false;
		}
		if(worker.addCommand(new JobShearEntity(sheep))){
			return true;
		}
		return false;
	}
	
	public boolean giveSword(EntityMinionWorker worker) {
		if(worker.getHeldItemMainhand() == null){
			int[] slots = this.inventory.getRawIndices(RelativeSide.BOTTOM);
			for(int i = 0; i < slots.length; i++){
				int slot = slots[i];
				ItemStack sword = inventory.getStackInSlot(slot);
				if(ItemStackTools.isValid(sword) && sword.getItem() instanceof ItemSword && !ToolUtil.isEmptyRfTool(sword)){
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
					this.inventory.setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
					return true;
				}
			}
		}
		return false;
	}

	private boolean tryCulling(List<Integer> targets) {
		int entityId;
		Entity entity;
		EntityAnimal animal;

		EntityMinionWorker worker = getRandomWorker(new WorkerFilter(){
			public boolean matches(EntityMinionWorker worker){
				ItemStack held = worker.getHeldItemMainhand();
				return !ItemStackTools.isNullStack(held) && held.getItem() instanceof ItemSword;
			}
		}, WorkerFilter.idleFilter);
		if (worker == null) {
			return false;
		}

		while (!targets.isEmpty()) {
			entityId = targets.remove(0);
			entity = getWorld().getEntityByID(entityId);
			if (entity instanceof EntityAnimal) {
				animal = (EntityAnimal) entity;
				if (animal.isInLove() || !(animal.getGrowingAge() >= 0)) {
					continue;
				}
				if(worker.addCommand(new JobKillEntity(animal))){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player) {
		if (!player.getEntityWorld().isRemote) {
			BlockUtil.openWorksiteGui(player, 0, getPos().getX(), getPos().getY(), getPos().getZ());
		}
		return true;
	}

	private void pickupEggs() {
		BlockPos p1 = getWorkBoundsMin();
		BlockPos p2 = getWorkBoundsMax().add(1, 1, 1);
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2);
		List<EntityItem> items = getWorld().getEntitiesWithinAABB(
				EntityItem.class, bb);
		ItemStack stack;
		for (EntityItem item : items) {
			stack = item.getEntityItem();
			if (!ItemStackTools.isValid(stack)) {
				continue;
			}
			if (stack.getItem() == Items.EGG) {
				if (!ItemUtil.canInventoryHold(inventory,
						inventory.getRawIndices(RelativeSide.TOP), stack)) {
					break;
				}
				item.setDead();
				addStackToInventory(stack, RelativeSide.TOP);
			}
		}
	}

	private boolean hasAnimalWork() {
		return !entitiesToCull.isEmpty()
				|| (carrotCount > 0 && !pigsToBreed.isEmpty())
				|| (seedCount > 0 && !chickensToBreed.isEmpty())
				|| (wheatCount > 0 && (!cowsToBreed.isEmpty() || !sheepToBreed
						.isEmpty()))
				|| (bucketCount > 0 && !cowsToMilk.isEmpty())
				|| (!sheepToShear.isEmpty());
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FARMING;
	}

	@Override
	public void openAltGui(EntityPlayer player) {
		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_WORK_ALT, getPos().getX(), getPos().getY(), getPos().getZ());
	}

	@Override
	public void readCustomNBT(NBTTagCompound tag) {
		super.readCustomNBT(tag);
		if (tag.hasKey("maxChickens")) {
			maxChickenCount = tag.getInteger("maxChickens");
		}
		if (tag.hasKey("maxCows")) {
			maxCowCount = tag.getInteger("maxCows");
		}
		if (tag.hasKey("maxPigs")) {
			maxPigCount = tag.getInteger("maxPigs");
		}
		if (tag.hasKey("maxSheep")) {
			maxSheepCount = tag.getInteger("maxSheep");
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound tag) {
		super.writeCustomNBT(tag);
		tag.setInteger("maxChickens", maxChickenCount);
		tag.setInteger("maxCows", maxCowCount);
		tag.setInteger("maxPigs", maxPigCount);
		tag.setInteger("maxSheep", maxSheepCount);
	}

	private static class EntityPair {

		int idA;
		int idB;

		private EntityPair(Entity a, Entity b) {
			idA = a.getEntityId();
			idB = b.getEntityId();
		}

		private EntityPair(int a, int b) {
			idA = a;
			idB = b;
		}

		public Entity getEntityA(World world) {
			return world.getEntityByID(idA);
		}

		public Entity getEntityB(World world) {
			return world.getEntityByID(idB);
		}
	}

}
