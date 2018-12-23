package alec_wam.CrystalMod.entities.minions.warrior;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.inventory.InventoryArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMinionWarrior extends EntityMinionBase {
	protected static final DataParameter<Boolean> EATING = EntityDataManager.<Boolean>createKey(EntityMinionWarrior.class, DataSerializers.BOOLEAN);
    
    public InventoryWarrior inventory;
	public InventoryArmor armorInventory;
	private int slotSelected = -1;
	private int backSlot = -1;
	
	private EnumMovementState movementState;
	private BlockPos guardPos;
	
	public EntityMinionWarrior(World worldIn) {
		super(worldIn);
		
		movementState = EnumMovementState.STAY;
		
		aiManager.addAI(new MinionAIFollow());
		aiManager.addAI(new MinionAIWander(MinionConstants.SPEED_WALK/2));
		aiManager.addAI(new MinionAIEat());
		aiManager.addAI(new MinionAICombat());
		
		inventory = new InventoryWarrior(this);
		armorInventory = new InventoryArmor(this);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(EATING, Boolean.valueOf(false));
	}	
	
	public boolean isEating()
    {
        return this.dataManager.get(EATING).booleanValue();
    }

    public void setEating(boolean eating)
    {
        this.dataManager.set(EATING, Boolean.valueOf(eating));
    }
	
    @Override
    public void onItemUseFinish(){
    	if (!this.activeItemStack.isEmpty() && this.isHandActive())
        {
            this.updateItemUse(this.activeItemStack, 16);
            boolean eating = false;
            if(this.isEating()){
            	if(this.activeItemStack.getItem() instanceof ItemFood){
            		ItemFood food = (ItemFood)this.activeItemStack.getItem();
            		float healAmt = (float)food.getHealAmount(activeItemStack);
            		this.heal(healAmt);
            		eating = true;
            	}
        		this.setEating(false);
            }
            ItemStack itemstack = this.activeItemStack.onItemUseFinish(this.world, this);
            itemstack = net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(this, activeItemStack, getItemInUseCount(), itemstack);
            this.setHeldItem(this.getActiveHand(), itemstack);
            this.resetActiveHand();
            if(eating){
            	this.inventory.updateDisplayItems();
            }
        }
    }
    
	public BlockPos getGuardPos(){
		return guardPos;
	}
	
	public double getMaximumWanderDistance(){
		return 16;
	}
	
	public boolean isWithinGuardBounds(BlockPos pos){
		if(guardPos == null || guardPos == BlockPos.ORIGIN)return false;
		return guardPos.distanceSq(pos) < getMaximumWanderDistance() * getMaximumWanderDistance();
	}
	
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setTag("Inventory", inventory.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("MovementState", getMovementState().getId());
		if(guardPos !=null && guardPos !=BlockPos.ORIGIN){
			BlockUtil.writeBlockPosToNBT(nbt, "GuardPos", guardPos);
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		this.movementState = EnumMovementState.fromId(nbt.hasKey("MovementState") ? nbt.getInteger("MovementState") : 0);
		if(nbt.hasKey("Inventory")){
			inventory.readFromNBT(nbt.getCompoundTag("Inventory"));
		}
		guardPos = nbt.hasKey("GuardPos") ? BlockUtil.readBlockPosFromNBT(nbt, "GuardPos") : null;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		//TODO Create Warrior GUI
        boolean inter = super.processInteract(player, hand);

        if(inter){
        	return true;
        }
        ItemStack stack = player.getHeldItem(hand);
        if(!ItemStackTools.isNullStack(stack)){
        	/*if(stack.getItem() == Items.STICK){
        		this.backSlot = -1;
        		this.slotSelected = -1;
        		inventory.markDirty();
        		return true;
        	}*/
        	/*if(stack.getItem() == Items.STICK){
        		if(ItemStackTools.isValid(getHeldItemMainhand()) && !player.isSneaking()){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(getHeldItemMainhand(), 0.0F);
                    }
        			setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
        			return true;
        		}
        		if(ItemStackTools.isValid(backStack) && player.isSneaking()){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(backStack, 0.0F);
                    }
        			backStack = ItemStackTools.getEmptyStack();
        			return true;
        		}
        	}*/
        	
        	/*EntityEquipmentSlot slot = getSlotForItemStack(stack);
        	if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR){
        		ItemStack old = getItemStackFromSlot(slot);
        		if(ItemStackTools.isValid(old)){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(old, 0.0F);
                    }
        		}
        		this.setItemStackToSlot(slot, ItemUtil.copy(stack, 1));
        		consumeItemFromStack(player, stack);
        		return true;
        	}*/
        	
        	/*if(stack.getItem() instanceof ItemSword){
        		boolean useBack = ItemStackTools.isValid(getHeldItemMainhand()) && !(getHeldItemMainhand().getItem() instanceof ItemSword);
        		if(useBack){
        			if(ItemStackTools.isValid(backStack)){
            			if(ItemUtil.canCombine(stack, backStack)){
            				return false;
            			}
            			if (!this.getEntityWorld().isRemote)
                        {
            				entityDropItem(backStack, 0.0F);
                        }
            		}
        			backStack = ItemUtil.copy(stack, 1);
            		consumeItemFromStack(player, stack);
            		return true;
        		}
        		if(ItemStackTools.isValid(getHeldItemMainhand())){
        			if(ItemUtil.canCombine(stack, getHeldItemMainhand())){
        				return false;
        			}
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(getHeldItemMainhand(), 0.0F);
                    }
        		}
        		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUtil.copy(stack, 1));
        		consumeItemFromStack(player, stack);
        		return true;
        	}
        	
        	if(isBow(stack)){
        		boolean useBack = ItemStackTools.isValid(getHeldItemMainhand()) && !isBow(getHeldItemMainhand());
        		if(useBack){
        			if(ItemStackTools.isValid(backStack)){
            			if(ItemUtil.canCombine(stack, backStack)){
            				return false;
            			}
            			if (!this.getEntityWorld().isRemote)
                        {
            				entityDropItem(backStack, 0.0F);
                        }
            		}
        			backStack = ItemStackTools.safeCopy(stack);
            		consumeItemFromStack(player, stack);
            		return true;
        		}
        		if(ItemStackTools.isValid(getHeldItemMainhand())){
        			if(ItemUtil.canCombine(stack, getHeldItemMainhand())){
        				return false;
        			}
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(getHeldItemMainhand(), 0.0F);
                    }
        		}
        		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
        		consumeItemFromStack(player, stack);
        		return true;
        	}*/
        	
        	/*if(stack.getItem() == Items.ARROW){
        		MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
        		if(!getEntityWorld().isRemote){
        			final int next = !player.isSneaking() ? combatAI.getNextMethodBehavior() : combatAI.getPrevMethodBehavior();
        			combatAI.setMethodBehavior(next);
        			NBTTagCompound nbt = new NBTTagCompound();
        			nbt.setInteger("ID", next);
        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "ATTACK_METHOD_SET", nbt), (EntityPlayerMP) player);
        			ChatUtil.sendNoSpam(player, "Combat Method set to "+combatAI.getMethodBehavior().getParsedText());
        		}
        		return true;
        	}
        	if(stack.getItem() == Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE)){
        		MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
        		if(!getEntityWorld().isRemote){
        			final int next = !player.isSneaking() ? combatAI.getNextTriggerBehavior() : combatAI.getPrevTriggerBehavior();
        			combatAI.setTriggerBehavior(next);
        			NBTTagCompound nbt = new NBTTagCompound();
        			nbt.setInteger("ID", next);
        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "ATTACK_TRIGGER_SET", nbt), (EntityPlayerMP) player);
        			ChatUtil.sendNoSpam(player, "Combat Trigger set to "+combatAI.getTriggerBehavior().getParsedText());
        		}
        		return true;
        	}
        	if(stack.getItem() == Items.BONE){
        		MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
        		if(!getEntityWorld().isRemote){
        			final int next = !player.isSneaking() ? combatAI.getNextTargetBehavior() : combatAI.getPrevTargetBehavior();
        			combatAI.setTargetBehavior(next);
        			NBTTagCompound nbt = new NBTTagCompound();
        			nbt.setInteger("ID", next);
        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "ATTACK_TARGET_SET", nbt), (EntityPlayerMP) player);
        			ChatUtil.sendNoSpam(player, "Combat Target set to "+combatAI.getTargetBehavior().getParsedText());
        		}
        		return true;
        	}*/
        	/*if(stack.getItem() == Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE)){
        		if(!getEntityWorld().isRemote){
        			EnumMovementState next = null;
        			this.guardPos = null;
        			if(getMovementState() == EnumMovementState.STAY){
        				next = EnumMovementState.FOLLOW;
        			} else if(getMovementState() == EnumMovementState.FOLLOW){
        				next = EnumMovementState.GUARD;
        				MinionAIWander wander = this.getAIManager().getAI(MinionAIWander.class);
        				this.guardPos = new BlockPos(this);
        				wander.setSpeed(MinionConstants.SPEED_WALK/2);
        			} else if(getMovementState() == EnumMovementState.FOLLOW){
        				next = EnumMovementState.GUARD;
        			}
        			setMovementState(next);
        			NBTTagCompound nbt = new NBTTagCompound();
        			nbt.setInteger("ID", next.getId());
        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "MOVEMENT_SET", nbt), (EntityPlayerMP) player);
        			ChatUtil.sendNoSpam(player, "Movement type set to "+Lang.localize("ai.movement."+(next.name().toLowerCase())));
        		}
        		return true;
        	}*/
        	if(stack.getItem() == Items.SADDLE){
        		if(getRidingEntity() == null){
        			List<EntityHorse> entities = getEntityWorld().getEntitiesWithinAABB(EntityHorse.class, this.getEntityBoundingBox().expand(5, 2, 5));
        			for(EntityHorse horse : entities){
        				if(horse.isEntityAlive() && horse.getPassengers().isEmpty()){
        					if(!getEntityWorld().isRemote)startRiding(horse);
        					return true;
        				}
        			}
        		}else{
        			if(!getEntityWorld().isRemote)dismountRidingEntity();
        			return true;
        		}
        	}
        }
        
        if(!player.isSneaking() && isOwner(player)){
        	if(!getEntityWorld().isRemote){
        		player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ENTITY, getEntityWorld(), getEntityId(), 0, 0);
        	}
        	return true;
        }
        
        return false;
    }
	
	@Override
	public void handleMessage(String type, NBTTagCompound data, boolean client){
		super.handleMessage(type, data, client);
		if(type.equalsIgnoreCase("ATTACK_METHOD_SET")){
			MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
			combatAI.setMethodBehavior(data.getInteger("ID"));
		} else if(type.equalsIgnoreCase("ATTACK_TRIGGER_SET")){
			MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
			combatAI.setTriggerBehavior(data.getInteger("ID"));
		} else if(type.equalsIgnoreCase("ATTACK_TARGET_SET")){
			MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
			combatAI.setTargetBehavior(data.getInteger("ID"));
		} else if(type.equalsIgnoreCase("MOVEMENT_SET")){
			setMovementState(EnumMovementState.fromId(data.getInteger("ID")));
			if(!client && getMovementState() == EnumMovementState.GUARD){
				this.guardPos = new BlockPos(this);
			}
		} 
		
		if(type.equalsIgnoreCase("REQUEST_COMBAT_SYNC")){
			int playerID = data.getInteger("ID");
			Entity entity = getEntityWorld().getEntityByID(playerID);
			if(entity instanceof EntityPlayerMP){
				MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("MOVE", getMovementState().getId());
				nbt.setInteger("METHOD", combatAI.getMethodBehavior().getNumericId());
				nbt.setInteger("TRIGGER", combatAI.getTriggerBehavior().getNumericId());
				nbt.setInteger("TARGET", combatAI.getTargetBehavior().getNumericId());
				CrystalModNetwork.sendTo(new PacketEntityMessage(this, "COMBAT_SYNC", nbt), (EntityPlayerMP)entity);
			}
		}
		if(type.equalsIgnoreCase("COMBAT_SYNC")){
			MinionAICombat combatAI = getAIManager().getAI(MinionAICombat.class);
			int move = data.getInteger("MOVE");
			int method = data.getInteger("METHOD");
			int trigger = data.getInteger("TRIGGER");
			int target = data.getInteger("TARGET");
			setMovementState(EnumMovementState.fromId(move));
			combatAI.setMethodBehavior(method);
			combatAI.setTriggerBehavior(trigger);
			combatAI.setTargetBehavior(target);
			if(client)updateCurrentScreen();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void updateCurrentScreen(){
		if(Minecraft.getMinecraft().currentScreen !=null && Minecraft.getMinecraft().currentScreen instanceof GuiMinionWarrior){
			((GuiMinionWarrior)Minecraft.getMinecraft().currentScreen).updateIcons();
		}
	}

	public void setMovementState(EnumMovementState state)
	{
		movementState = state;
	}

	public EnumMovementState getMovementState()
	{
		return movementState;
	}
	
	public static boolean isBow(ItemStack stack){
		return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemBow;
	}

	public int getBackSelected() {
		return backSlot;
	}
	
	public int getSlotSelected() {
		return slotSelected;
	}

	public void setSlotSelected(int slotSelected) {
		final int old = this.slotSelected;
		this.slotSelected = slotSelected;
		this.backSlot = old;
		
		ItemStack handStack = this.slotSelected == -1 ? ItemStackTools.getEmptyStack() : inventory.getStackInSlot(this.slotSelected);
		ItemStack backStack = backSlot == -1 ? ItemStackTools.getEmptyStack() : inventory.getStackInSlot(backSlot);
		setHeldItem(EnumHand.MAIN_HAND, handStack);
		setBackItem(backStack);
	}
	
	@Override
	public void onDeath(DamageSource damageSource) 
	{
		super.onDeath(damageSource);
		if (!getEntityWorld().isRemote)
		{
			for(EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}){
				ItemStack armor = getItemStackFromSlot(slot);
				if(ItemStackTools.isValid(armor) && !EnchantmentHelper.hasVanishingCurse(armor)){
					entityDropItem(armor, 0.0F);
				}
			}
			for(int i = 0; i < inventory.getSizeInventory(); i++){
				ItemStack stack = inventory.getStackInSlot(i);
				if(ItemStackTools.isValid(stack) && !EnchantmentHelper.hasVanishingCurse(stack)){
					entityDropItem(stack, 0.0F);
				}
			}
		}
	}
	
	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
    {
		
    }
	
	@Override
	protected void damageArmor(float damage)
    {
		this.armorInventory.damageArmor(damage);
    }

}
