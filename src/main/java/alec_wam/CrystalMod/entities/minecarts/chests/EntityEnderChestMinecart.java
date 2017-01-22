package alec_wam.CrystalMod.entities.minecarts.chests;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEnderChestMinecart extends EntityMinecart {
	
	public EntityEnderChestMinecart(World worldIn)
    {
        super(worldIn);
    }

    public EntityEnderChestMinecart(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    protected void entityInit()
    {
        super.entityInit();
    }
    
    @Override
    public void onUpdate(){
    	super.onUpdate();
    }
    
	public void setDead()
    {
		super.setDead();
    }
	
	public void killMinecart(DamageSource source)
    {
		super.killMinecart(source);

        if (this.getEntityWorld().getGameRules().getBoolean("doEntityDrops"))
        {
            entityDropItem(new ItemStack(Blocks.ENDER_CHEST), 0.0F);
        }
    }
	
	public ItemStack getPickedResult(RayTraceResult target)
    {
		ItemStack stack = new ItemStack(ModItems.enderChestMinecart);
		return stack;
    }
	
	public IBlockState getDefaultDisplayTile()
    {
        return Blocks.ENDER_CHEST.getDefaultState();
    }
	
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand)
    {
        if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player, hand))) return true;
        
        InventoryEnderChest chest = player.getInventoryEnderChest();
    	if(!getEntityWorld().isRemote && chest !=null){
    		player.displayGUIChest(chest);
    		return true;
    	}
        
        return true;
    }
	
	protected void writeEntityToNBT(NBTTagCompound compound)
    {
		super.writeEntityToNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
    	super.readEntityFromNBT(compound);
    }

	@Override
	public Type getType() {
		return Type.CHEST;
	}

}
