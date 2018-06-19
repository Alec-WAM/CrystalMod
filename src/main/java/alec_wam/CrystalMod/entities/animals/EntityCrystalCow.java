package alec_wam.CrystalMod.entities.animals;

import java.util.List;

import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class EntityCrystalCow extends EntityCow implements net.minecraftforge.common.IShearable{

	private static final DataParameter<Byte> COLOR = EntityDataManager.<Byte>createKey(EntityCrystalCow.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> DELAY = EntityDataManager.<Integer>createKey(EntityCrystalCow.class, DataSerializers.VARINT);
	protected int crystalDelay;
	
	public EntityCrystalCow(World worldIn) {
		super(worldIn);
	}

    @Override
	protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DELAY, Integer.valueOf(0));
        this.dataManager.register(COLOR, new Byte((byte)0));
    }

    @Override
	public EntityCrystalCow createChild(EntityAgeable ageable)
    {
    	EntityCrystalCow cow = new EntityCrystalCow(this.getEntityWorld());
    	if(ageable instanceof EntityCrystalCow){
    		cow.setColor(((EntityCrystalCow)ageable).getColor());
    	}
        return cow;
    }
	
	public void setDelay(int delay){
		this.dataManager.set(DELAY, Integer.valueOf(delay));
		this.crystalDelay = delay;
	}
	
	public int getDelay()
    {
        return this.getEntityWorld().isRemote ? this.dataManager.get(DELAY) : this.crystalDelay;
    }
	
	public int getColor()
    {
        return this.dataManager.get(COLOR);
    }
    
    public void setColor(int color){
    	this.dataManager.set(COLOR, Byte.valueOf((byte)color));
    }
	
    @Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        super.onInitialSpawn(difficulty, livingdata);
        setColor(rand.nextInt(4));
        return livingdata;
    }
    
    @Override
	public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("CrystalDelay", this.getDelay());
        tagCompound.setByte("Color", (byte)getColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.setDelay(tagCompund.getInteger("CrystalDelay"));
        this.setColor(tagCompund.getByte("Color"));
    }
    
    @Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(!getEntityWorld().isRemote){
        	if(getDelay() > 0){
        		final int delay = getDelay();
        		setDelay(delay-1);
        	}
        }
    }
    
    public boolean isCrystalGrown(){
    	return getDelay() <= 0;
    }
    
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return getGrowingAge() >= 0 && isCrystalGrown();
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		if(getGrowingAge() >= 0 && isCrystalGrown()){
			int i = this.rand.nextInt(2 + fortune);

	        for (int k = 0; k < i; ++k)
	        {
	        	int META = CrystalType.BLUE_SHARD.getMeta() + getColor();
	        	ret.add(new ItemStack(ModItems.crystals, 1, META));
	        }
	        
	        this.setDelay(20 * 10);
		}
		return ret;
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack)
    {
		if(stack.getItem() == ModItems.crystalBerry){
			return true;
		}
        return false;
    }

}
