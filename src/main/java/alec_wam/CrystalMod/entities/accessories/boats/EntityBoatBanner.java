package alec_wam.CrystalMod.entities.accessories.boats;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityBoatBanner extends Entity {
	private static final DataParameter<ItemStack> BANNER_STACK = EntityDataManager.<ItemStack>createKey(EntityBoatBanner.class, DataSerializers.OPTIONAL_ITEM_STACK);
	
	public EntityBoatBanner(World worldIn) {
		super(worldIn);
	}
	
	public EntityBoatBanner(World worldIn, ItemStack stack) {
		this(worldIn);
		dataManager.set(BANNER_STACK, stack);
	}
	
	public ItemStack getBanner(){
		return dataManager.get(BANNER_STACK);
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if(this.isDead){
			return;
		}
		
		if(!isRiding()) {
			if(!world.isRemote){
				setDead();
			}
			
			return;
		}
		
		Entity riding = getRidingEntity();
		rotationYaw = riding.prevRotationYaw;
		rotationPitch = 0F;
	}
	
	@Override
	public void setDead()
    {
        if (!this.world.isRemote)
        {
            ItemUtil.spawnItemInWorldWithRandomMotion(world, getBanner(), getPosition());
        }

		super.setDead();
    }
	
	@Override
	protected void entityInit() {
		noClip = true;
		dataManager.register(BANNER_STACK, new ItemStack(Items.BANNER));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if(compound.hasKey("BannerStack")){
			ItemStack stack = new ItemStack(compound.getCompoundTag("BannerStack"));
			if(ItemStackTools.isValid(stack)){
				dataManager.set(BANNER_STACK, stack);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if(ItemStackTools.isValid(getBanner())){
    		compound.setTag("BannerStack", getBanner().writeToNBT(new NBTTagCompound()));
    	}
	}

}
