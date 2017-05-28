package alec_wam.CrystalMod.entities.minecarts.chests;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.chest.CrystalChestType;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class EntityCrystalChestMinecartBase extends EntityMinecartChest {

	public static final Map<CrystalChestType, Class<? extends EntityCrystalChestMinecartBase>> minecarts = new HashMap<CrystalChestType, Class<? extends EntityCrystalChestMinecartBase>>();
	
	public static void register(Class<? extends EntityCrystalChestMinecartBase> clazz, CrystalChestType type){
		minecarts.put(type, clazz);
	}
	
	public static EntityCrystalChestMinecartBase makeMinecart(World world, CrystalChestType type) {
		try {
			Class<? extends EntityCrystalChestMinecartBase> cls = minecarts.get(type);
			return cls.getConstructor(World.class).newInstance(world);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static EntityCrystalChestMinecartBase makeMinecart(World world, double x, double y, double z, CrystalChestType type) {
		try {
			Class<? extends EntityCrystalChestMinecartBase> cls = minecarts.get(type);
			return cls.getConstructor(World.class, double.class, double.class, double.class).newInstance(world, x, y, z);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
	
	public EntityCrystalChestMinecartBase(World worldIn)
    {
        super(worldIn);
    }

    public EntityCrystalChestMinecartBase(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }
	
	public abstract CrystalChestType getChestType();
	
	public void setDead()
    {
		this.isDead = true;
        if (this.dropContentsWhenDead)
        {
        	if(!getEntityWorld().isRemote)InventoryHelper.dropInventoryItems(this.getEntityWorld(), this, this);
        }
    }
	
	public void killMinecart(DamageSource source)
    {
		this.setDead();

        if (this.getEntityWorld().getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = new ItemStack(Items.MINECART, 1);

            if (this.getName() != null)
            {
                itemstack.setStackDisplayName(this.getName());
            }

            this.entityDropItem(itemstack, 0.0F);
            
            if (this.dropContentsWhenDead)
            {
            	if(!getEntityWorld().isRemote)InventoryHelper.dropInventoryItems(this.getEntityWorld(), this, this);
            }
            entityDropItem(new ItemStack(ModBlocks.crystalChest, 1, getChestType().ordinal()), 0.0F);
        }
    }
	
	public ItemStack getPickedResult(RayTraceResult target)
    {
		return new ItemStack(ModItems.chestMinecart, 1, getChestType().ordinal());
    }
	
	public IBlockState getDefaultDisplayTile()
    {
        return ModBlocks.crystalChest.getStateFromMeta(getChestType().ordinal());
    }
	
	public int getSizeInventory(){
		return getChestType().getRowCount() * getChestType().getRowLength();
	}
	
	@Nullable
    public ItemStack getStackInSlot(int index)
    {
        return this.inventory.get(index);
    }
	
	@Nullable
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }
	
	@Nullable
    public ItemStack removeStackFromSlot(int index)
    {
		ItemStack itemstack = this.inventory.get(index);
        if (ItemStackTools.isValid(itemstack))
        {
            this.inventory.set(index, ItemStackTools.getEmptyStack());
            return itemstack;
        }
        else
        {
            return ItemStackTools.getEmptyStack();
        }
    }
	
	public void setInventorySlotContents(int index, @Nullable ItemStack stack)
    {
		this.inventory.set(index, stack);

        if (ItemStackTools.isValid(stack) && ItemStackTools.getStackSize(stack) > this.getInventoryStackLimit())
        {
        	ItemStackTools.setStackSize(stack, this.getInventoryStackLimit());
        }
    }
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player, hand))) return true;
        if (!this.getEntityWorld().isRemote)
        {
            player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ENTITY, player.getEntityWorld(), getEntityId(), 0, 0);
        }

        return true;
    }
	
	protected void writeEntityToNBT(NBTTagCompound compound)
    {
		if (this.hasDisplayTile())
        {
            compound.setBoolean("CustomDisplayTile", true);
            IBlockState iblockstate = this.getDisplayTile();
            ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(iblockstate.getBlock());
            compound.setString("DisplayTile", resourcelocation == null ? "" : resourcelocation.toString());
            compound.setInteger("DisplayData", iblockstate.getBlock().getMetaFromState(iblockstate));
            compound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }
		ItemStackHelper.saveAllItems(compound, inventory);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
    	if (compound.getBoolean("CustomDisplayTile"))
        {
            Block block;

            if (compound.hasKey("DisplayTile", 8))
            {
                block = Block.getBlockFromName(compound.getString("DisplayTile"));
            }
            else
            {
                block = Block.getBlockById(compound.getInteger("DisplayTile"));
            }

            int i = compound.getInteger("DisplayData");
            this.setDisplayTile(block == null ? Blocks.AIR.getDefaultState() : block.getStateFromMeta(i));
            this.setDisplayTileOffset(compound.getInteger("DisplayOffset"));
        }
    	ItemStackHelper.loadAllItems(compound, inventory);
    }


}
