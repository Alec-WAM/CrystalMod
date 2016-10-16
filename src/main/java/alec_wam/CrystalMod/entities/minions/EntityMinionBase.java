package alec_wam.CrystalMod.entities.minions;

import alec_wam.CrystalMod.entities.EntityOwnable;
import alec_wam.CrystalMod.entities.minions.ai.AIManager;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMinionBase extends EntityOwnable implements IMessageHandler {
	
	protected AIManager aiManager;
	
	public EntityMinionBase(World worldIn) {
		super(worldIn);
		setSize(0.3F, 0.8F);
		//((PathNavigateGround)this.getNavigator()).setCanSwim(false);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        //this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        //this.tasks.addTask(9, new EntityAILookIdle(this));
        
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
	
	public void onLivingUpdate()
    {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
    }
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
    }
	
	public boolean processInteract(EntityPlayer par1EntityPlayer, EnumHand hand, ItemStack held)
    {
		ItemStack itemstack = held;

        if(this.isTamed()/* && isOwner(par1EntityPlayer)*/){
        	if(itemstack !=null){
        		if(itemstack.getItem() == Items.SLIME_BALL){
        			if(this.worldObj.isRemote)swingArm(EnumHand.MAIN_HAND);;
        			return true;
        		}
        	}else{
        		if(par1EntityPlayer.isSneaking() && !this.worldObj.isRemote){
        			this.aiSit.setSitting(!this.isSitting());
        			ChatUtil.sendNoSpam(par1EntityPlayer, "Sitting: "+this.isSitting());
        		}
        	}
        }
        
        if (itemstack == null && !this.isTamed())
        {
           
           if (!this.worldObj.isRemote)
            {
                this.setTamed(true);
                this.navigator.clearPathEntity();
                this.setAttackTarget((EntityLivingBase)null);
                this.setHealth(50.0F);
                this.setOwnerId(par1EntityPlayer.getUniqueID());
                this.playTameEffect(true);
                this.setSitting(false);
                this.worldObj.setEntityState(this, (byte)7);
            }

            return true;
        }

        return super.processInteract(par1EntityPlayer, hand, itemstack);
    }

	@Override
	protected boolean canDespawn() {
		return false;
	}
    
	@Override
	public boolean isChild() {
		return super.isChild();
	}
	
	public float getRenderSizeModifier(){
		return 0.5F;
	}
	
	protected void consumeItemFromStack(EntityPlayer player, ItemStack stack)
    {
        if (!player.capabilities.isCreativeMode)
        {
            --stack.stackSize;

            if (stack.stackSize <= 0)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            }
        }
    }
	
	/** Used to toss item**/
	public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem)
    {
        if (droppedItem == null)
        {
            return null;
        }
        else if (droppedItem.stackSize == 0)
        {
            return null;
        }
        else
        {
            double d0 = this.posY - 0.30000001192092896D + (double)this.getEyeHeight();
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, d0, this.posZ, droppedItem);
            entityitem.setPickupDelay(40);

            if (traceItem)
            {
                entityitem.setThrower(this.getName());
            }

            if (dropAround)
            {
                float f = this.rand.nextFloat() * 0.5F;
                float f1 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                entityitem.motionX = (double)(-MathHelper.sin(f1) * f);
                entityitem.motionZ = (double)(MathHelper.cos(f1) * f);
                entityitem.motionY = 0.20000000298023224D;
            }
            else
            {
                float f2 = 0.3F;
                entityitem.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f2);
                entityitem.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f2);
                entityitem.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f2 + 0.1F);
                float f3 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                f2 = 0.02F * this.rand.nextFloat();
                entityitem.motionX += Math.cos((double)f3) * (double)f2;
                entityitem.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                entityitem.motionZ += Math.sin((double)f3) * (double)f2;
            }

            if(!this.worldObj.isRemote)
            this.worldObj.spawnEntityInWorld(entityitem);

            return entityitem;
        }
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) 
	{
		super.writeEntityToNBT(nbt);
		aiManager.writeToNBT(nbt);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		aiManager.readFromNBT(nbt);
	}
	
	@Override
	public void onDeath(DamageSource damageSource) 
	{
		super.onDeath(damageSource);

		if (!worldObj.isRemote)
		{
			aiManager.disableAllToggleAIs();
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		//NO OP
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
