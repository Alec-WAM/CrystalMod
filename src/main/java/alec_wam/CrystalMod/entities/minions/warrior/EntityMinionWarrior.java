package alec_wam.CrystalMod.entities.minions.warrior;

import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

public class EntityMinionWarrior extends EntityMinionBase {

	public InventoryWarrior inventory;
	
	private EnumMovementState movementState;
	private ItemStack backStack;
	
	public EntityMinionWarrior(World worldIn) {
		super(worldIn);
		
		movementState = EnumMovementState.STAY;
		
		aiManager.addAI(new MinionAIFollow());
		aiManager.addAI(new MinionAIWander(MinionConstants.SPEED_WALK/2));
		aiManager.addAI(new MinionAICombat());
		
		inventory = new InventoryWarrior(this);
	}
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		NBTTagCompound invList = new NBTTagCompound();
		this.inventory.writeToNBT(invList);
		nbt.setTag("Inventory", invList);
		nbt.setInteger("MovementState", getMovementState().getId());
		if(this.backStack !=null){
			NBTTagCompound backNBT = new NBTTagCompound();
			backStack.writeToNBT(backNBT);
			nbt.setTag("BackStack", backNBT);
		}
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		this.movementState = EnumMovementState.fromId(nbt.hasKey("MovementState") ? nbt.getInteger("MovementState") : 0);
		if(nbt.hasKey("Inventory")){
			inventory.clear();
			inventory.readFromNBT(nbt.getCompoundTag("Inventory"));
		}
		backStack = null;
		if(nbt.hasKey("BackStack")){
			backStack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("BackStack"));
		}
	}
	
	public void switchItems(){
		final ItemStack held = this.getHeldItemMainhand();
		final ItemStack back = this.backStack;
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, back);
		backStack = held;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        boolean inter = super.processInteract(player, hand);

        if(inter){
        	return true;
        }
        ItemStack stack = player.getHeldItem(hand);
        if(!ItemStackTools.isNullStack(stack)){
        	
        	if(stack.getItem() == Items.STICK){
        		if(getHeldItemMainhand() !=null && !player.isSneaking()){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(getHeldItemMainhand(), 0.0F);
                    }
        			setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
        			return true;
        		}
        		if(!ItemStackTools.isNullStack(backStack) && player.isSneaking()){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(backStack, 0.0F);
                    }
        			backStack = ItemStackTools.getEmptyStack();
        			return true;
        		}
        	}
        	
        	EntityEquipmentSlot slot = getSlotForItemStack(stack);
        	if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR){
        		ItemStack old = getItemStackFromSlot(slot);
        		if(!ItemStackTools.isNullStack(old)){
        			if (!this.getEntityWorld().isRemote)
                    {
        				entityDropItem(old, 0.0F);
                    }
        		}
        		this.setItemStackToSlot(slot, ItemUtil.copy(stack, 1));
        		consumeItemFromStack(player, stack);
        		return true;
        	}
        	
        	if(stack.getItem() instanceof ItemSword){
        		boolean useBack = getHeldItemMainhand() !=null && !(getHeldItemMainhand().getItem() instanceof ItemSword);
        		if(useBack){
        			if(!ItemStackTools.isNullStack(backStack)){
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
        		if(!ItemStackTools.isNullStack(getHeldItemMainhand())){
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
        		boolean useBack = getHeldItemMainhand() !=null && !isBow(getHeldItemMainhand());
        		if(useBack){
        			if(!ItemStackTools.isNullStack(backStack)){
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
        		if(!ItemStackTools.isNullStack(getHeldItemMainhand())){
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
        	}
        	
        	if(stack.getItem() == Items.ARROW){
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
        	}
        	if(stack.getItem() == Item.getItemFromBlock(Blocks.STONE_PRESSURE_PLATE)){
        		if(!getEntityWorld().isRemote){
        			EnumMovementState next = null;
        			if(getMovementState() == EnumMovementState.STAY){
        				next = EnumMovementState.MOVE;
        				MinionAIWander wander = this.getAIManager().getAI(MinionAIWander.class);
        				wander.setSpeed(MinionConstants.SPEED_WALK/2);
        			} else if(getMovementState() == EnumMovementState.MOVE){
        				next = EnumMovementState.FOLLOW;
        			} else if(getMovementState() == EnumMovementState.FOLLOW){
        				next = EnumMovementState.STAY;
        			}
        			setMovementState(next);
        			NBTTagCompound nbt = new NBTTagCompound();
        			nbt.setInteger("ID", next.getId());
        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "MOVEMENT_SET", nbt), (EntityPlayerMP) player);
        			ChatUtil.sendNoSpam(player, "Movement type set to "+Lang.localize("ai.movement."+(next.name().toLowerCase())));
        		}
        		return true;
        	}
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
        	if(stack.getItem() == Items.APPLE){
        		this.switchItems();
        		return true;
        	}
        }
        
        return false;
    }
	
	@Override
	public void handleMessage(String type, NBTTagCompound data, boolean client){
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
		} else if(type.equalsIgnoreCase("SWITCHITEMS")){
			this.switchItems();
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
		return !ItemStackTools.isNullStack(stack) && stack.getItem() == Items.BOW;
	}

	public ItemStack getBackItem() {
		return backStack;
	}
	
	public void setBackItem(ItemStack stack) {
		backStack = stack;
	}

}
