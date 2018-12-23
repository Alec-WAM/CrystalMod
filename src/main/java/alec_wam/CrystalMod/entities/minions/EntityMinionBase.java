package alec_wam.CrystalMod.entities.minions;

import alec_wam.CrystalMod.entities.EntityOwnable;
import alec_wam.CrystalMod.entities.ai.AIManager;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMinionBase extends EntityOwnable implements IMessageHandler {
	
	protected AIManager aiManager;
	
	protected ItemStack backStack = ItemStackTools.getEmptyStack();
	
	public EntityMinionBase(World worldIn) {
		super(worldIn);
		setSize(0.3F, 0.8F);
		this.tasks.addTask(1, new EntityAISwimming(this));
        
        aiManager = new AIManager(this);
	}
	
	public AIManager getAIManager(){
		return aiManager;
	}
	
	public String getOccupationName(){
		return "Basic";
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		aiManager.onUpdate();
	}
	
	@Override
	public void onLivingUpdate()
    {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
    }
	
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
    }
	
	@Override
	public boolean processInteract(EntityPlayer par1EntityPlayer, EnumHand hand)
    {
		ItemStack itemstack = par1EntityPlayer.getHeldItem(hand);
        
        if (ItemStackTools.isNullStack(itemstack) && !this.isTamed())
        {
           
           if (!this.getEntityWorld().isRemote)
            {
                this.setTamed(true);
                this.navigator.clearPathEntity();
                this.setAttackTarget((EntityLivingBase)null);
                this.setHealth(50.0F);
                this.setOwnerId(par1EntityPlayer.getUniqueID());
                this.playTameEffect(true);
                this.setSitting(false);
                this.getEntityWorld().setEntityState(this, (byte)7);
            }

            return true;
        }

        return super.processInteract(par1EntityPlayer, hand);
    }

	@Override
	protected boolean canDespawn() {
		return false;
	}
    
	@Override
	public boolean isChild() {
		return super.isChild();
	}
	
	@Override
	public float getRenderSizeModifier(){
		return 0.5F;
	}
	
	protected void consumeItemFromStack(EntityPlayer player, ItemStack stack)
    {
        if (!player.capabilities.isCreativeMode)
        {
        	ItemStackTools.incStackSize(stack, -1);

            if (ItemStackTools.isEmpty(stack))
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStackTools.getEmptyStack());
            }
        }
    }
	
	/** Used to toss item**/
	public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem)
    {
        if (!ItemStackTools.isValid(droppedItem))
        {
            return null;
        }
        else
        {
            double d0 = this.posY - 0.30000001192092896D + this.getEyeHeight();
            EntityItem entityitem = new EntityItem(this.getEntityWorld(), this.posX, d0, this.posZ, droppedItem);
            entityitem.setPickupDelay(40);

            if (traceItem)
            {
                entityitem.setThrower(this.getName());
            }

            if (dropAround)
            {
                float f = this.rand.nextFloat() * 0.5F;
                float f1 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                entityitem.motionX = -MathHelper.sin(f1) * f;
                entityitem.motionZ = MathHelper.cos(f1) * f;
                entityitem.motionY = 0.20000000298023224D;
            }
            else
            {
                float f2 = 0.3F;
                entityitem.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f2;
                entityitem.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f2;
                entityitem.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f2 + 0.1F;
                float f3 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                f2 = 0.02F * this.rand.nextFloat();
                entityitem.motionX += Math.cos(f3) * f2;
                entityitem.motionY += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                entityitem.motionZ += Math.sin(f3) * f2;
            }

            if(!this.getEntityWorld().isRemote)
            this.getEntityWorld().spawnEntity(entityitem);

            return entityitem;
        }
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) 
	{
		super.writeEntityToNBT(nbt);
		aiManager.writeToNBT(nbt);
		if(ItemStackTools.isValid(backStack)){
			NBTTagCompound backNBT = new NBTTagCompound();
			backStack.writeToNBT(backNBT);
			nbt.setTag("BackStack", backNBT);
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		aiManager.readFromNBT(nbt);
		this.aiSit.setSitting(false);
		this.setSitting(false);
		backStack = ItemStackTools.getEmptyStack();
		if(nbt.hasKey("BackStack")){
			backStack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("BackStack"));
		}
	}
	
	@Override
	public void onDeath(DamageSource damageSource) 
	{
		super.onDeath(damageSource);

		if (!getEntityWorld().isRemote)
		{
			aiManager.disableAllToggleAIs();
		}
	}
	
	@Override
	public void handleMessage(String type, NBTTagCompound data, boolean client){
		if(type.equalsIgnoreCase("SWITCHITEMS")){
			switchItems();
		}
		if(type.equalsIgnoreCase("SET_BACK")){
			this.backStack = ItemStackTools.loadFromNBT(data);
		}
		if(type.equalsIgnoreCase("CLEAR_BACK")){
			this.backStack = ItemStackTools.getEmptyStack();
		}
	}
	
	public void switchItems(){
		final ItemStack held = this.getHeldItemMainhand();
		final ItemStack back = this.backStack;
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, back);
		setBackItem(held);
	}
	
	public void swapHands() {
		final ItemStack main = getHeldItemMainhand();
		final ItemStack off = getHeldItemOffhand();
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, off);
		this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, main);
	}
	
	public ItemStack getBackItem() {
		return backStack;
	}
	
	public void setBackItem(ItemStack stack) {
		backStack = stack;
		if(!world.isRemote){
			if(ItemStackTools.isValid(stack)){
				CrystalModNetwork.sendToAllAround(new PacketEntityMessage(this, "SET_BACK", stack.writeToNBT(new NBTTagCompound())), this);
			} else {
				CrystalModNetwork.sendToAllAround(new PacketEntityMessage(this, "CLEAR_BACK"), this);
			}
		}
	}
	
	public void saveToItem(EntityPlayer player, ItemStack stack){
		NBTTagCompound nbt = new NBTTagCompound();
		writeEntityToNBT(nbt);
		ItemNBTHelper.getCompound(stack).setTag("EntityData", nbt);
	}
	
	public void loadFromItem(EntityPlayer player, ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "EntityData")){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompoundTag("EntityData");
			readEntityFromNBT(nbt);
		}
	}

}
