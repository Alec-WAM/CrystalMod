package alec_wam.CrystalMod.entities.pet.bombomb;

import java.util.List;

import alec_wam.CrystalMod.entities.EntityOwnable;
import alec_wam.CrystalMod.entities.ai.AIManager;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityBombomb extends EntityOwnable implements IMessageHandler {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityBombomb.class, DataSerializers.VARINT);
	protected AIManager aiManager;
	
	public int keyRotation;
	
	public EntityBombomb(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 0.8F);
		this.setTamed(false);
        this.getNavigator().getNodeProcessor().setCanSwim(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        
        aiManager = new AIManager(this);
        
        aiManager.addAI(new FollowOwnerAI());
        aiManager.addAI(new BombombAICombat());
	}
	
	public AIManager getAIManager(){
		return aiManager;
	}
	
	@Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, Integer.valueOf(EnumDyeColor.YELLOW.getDyeDamage()));
    }
	
	@Override
	public double getYOffset()
    {
       return 0.0D;
    }
	
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        //this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MinionConstants.SPEED_RUN);

        if (this.isTamed())
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }
	
	@Override
	public void setTamed(boolean tamed)
    {
		super.setTamed(tamed);
		if (tamed)
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }
	
	@Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(world.isRemote){
        	keyRotation++;
        	keyRotation %=360;
        }
        /*if(this.isRiding()){
        	this.rotationYaw = this.getRidingEntity().rotationYaw;
        }*/
        /*if(getOwner() !=null && Strings.isNullOrEmpty(getCustomNameTag())){
        	if(getOwner() instanceof EntityPlayer){
        		EntityPlayer player = (EntityPlayer)getOwner();
	        	String ownerName = player.getName();
	        	ScorePlayerTeam scoreplayerteam = this.getEntityWorld().getScoreboard().getPlayersTeam(ownerName);
	        	
	        	String name = scoreplayerteam != null ? scoreplayerteam.formatString(ownerName): ownerName;
	        	setCustomNameTag(name);
        	}
        }*/
    }
	
	@Override
	public void onUpdate()
    {
        super.onUpdate();
        aiManager.onUpdate();
        if(!getEntityWorld().isRemote){
        	//Needs Healing
        	if(getHealth() < getMaxHealth()){
	        	List<EntityItem> items = this.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox());
		        for(EntityItem item : items){
		        	if(item !=null && ItemStackTools.isValid(item.getEntityItem())){
		        		ItemStack stack = item.getEntityItem();
		        		float value = 0.0f;
		        		if(stack.getItem() == Item.getItemFromBlock(Blocks.TNT)){
		        			value=5.0f;
		        		}
		        		if(stack.getItem() == Items.GUNPOWDER){
		        			value=1.0f;
		        		}
		        		if(value > 0.0f && getHealth() + value <= getMaxHealth()){
		        			heal(value);
		        			ItemStackTools.incStackSize(stack, -1);
		        			if(ItemStackTools.isEmpty(stack)){
		        				item.setDead();
		        			}
		        			if(getHealth() >= getMaxHealth()){
		        				break;
		        			}
	        			}
		        	}
		        }
        	}
        }
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) 
	{
		super.writeEntityToNBT(nbt);
		aiManager.writeToNBT(nbt);
        nbt.setByte("Color", (byte)getColor().getDyeDamage());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		aiManager.readFromNBT(nbt);
		if (nbt.hasKey("Color", 99))
        {
            this.setColor(EnumDyeColor.byDyeDamage(nbt.getByte("Color")));
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
	protected boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		ItemStack stack = player.getHeldItem(hand);
		if(isOwner(player)){
			if(ItemStackTools.isValid(stack)){
				EnumDyeColor color = ItemUtil.getDyeColor(stack);
				if(color !=null){
					if(getColor() !=color){
						setColor(color);
						if(!player.capabilities.isCreativeMode){
							ItemStackTools.incStackSize(stack, -1);
						}
						return true;
					}
				}
				/*if(stack.getItem() == ModItems.minionStaff){
					if(!isRiding()){
						startRiding(player);
					} else {
						dismountRidingEntity();
					}
					return true;
				}*/
			} else {
				if(player.isSneaking()){
					BombombAICombat ai = getAIManager().getAI(BombombAICombat.class);
					if(ai !=null){
						if(!getEntityWorld().isRemote){
		        			final int next = !player.isSneaking() ? ai.getNextTriggerBehavior() : ai.getPrevTriggerBehavior();
		        			ai.setTriggerBehavior(next);
		        			NBTTagCompound nbt = new NBTTagCompound();
		        			nbt.setInteger("ID", next);
		        			CrystalModNetwork.sendTo(new PacketEntityMessage(this, "ATTACK_TRIGGER_SET", nbt), (EntityPlayerMP) player);
		        			ChatUtil.sendNoSpam(player, "Combat Trigger set to "+ai.getTriggerBehavior().getParsedText());
		        		}
						return true;
					}
				}
			}
		}
        return false;
    }
	
	public EnumDyeColor getColor()
    {
        return EnumDyeColor.byDyeDamage(this.dataManager.get(COLOR).intValue() & 15);
    }

    /**
     * Set this wolf's collar color.
     */
    public void setColor(EnumDyeColor color)
    {
    	this.dataManager.set(COLOR, Integer.valueOf(color.getDyeDamage()));
    }

	public float getFlashIntensity(float partialTickTime) {
		return 0f;
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

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("ATTACK_TRIGGER_SET")){
			BombombAICombat combatAI = getAIManager().getAI(BombombAICombat.class);
			combatAI.setTriggerBehavior(messageData.getInteger("ID"));
		}
		if(messageId.equalsIgnoreCase("Explosion")){
			double x = posX;
        	double y = posY;
        	double z = posZ;
            getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D, new int[0]);
		}
	}

}
