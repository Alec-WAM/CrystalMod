package alec_wam.CrystalMod.entities.mob.enderman;

import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityCrystalEnderman extends EntityEnderman {

	private static final DataParameter<Byte> COLOR = EntityDataManager.<Byte>createKey(EntityCrystalEnderman.class, DataSerializers.BYTE);
	
	public EntityCrystalEnderman(World worldIn) {
		super(worldIn);
	}
    
    @Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(COLOR, Byte.valueOf((byte)0));
    }

	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
    }
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setByte("Color", (byte)this.getColor());
    }
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("Color", 99))
        {
            int i = tagCompund.getByte("Color");
            this.setColor(i);
        }
    }
	
	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
    {
        super.dropFewItems(wasRecentlyHit, lootingModifier);
        
        int i = this.rand.nextInt(3 + lootingModifier);

        for (int k = 0; k < i; ++k)
        {
        	int META = CrystalType.BLUE_SHARD.getMeta() + getColor();
            this.entityDropItem(new ItemStack(ModItems.crystals, 1, META), 0.0f);
        }
    }
	
	/**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        super.onInitialSpawn(difficulty, livingdata);
        setColor(rand.nextInt(4));
        //TODO Add config for pure chance
        if(rand.nextInt(15) == 0){
        	setColor(4);
        }
        return livingdata;
    }
    
    public int getColor()
    {
        return this.dataManager.get(COLOR);
    }
    
    public void setColor(int color){
    	this.dataManager.set(COLOR, Byte.valueOf((byte)color));
    }
	
}
