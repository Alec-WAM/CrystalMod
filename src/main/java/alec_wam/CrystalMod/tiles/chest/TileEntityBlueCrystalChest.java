package alec_wam.CrystalMod.tiles.chest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;

public class TileEntityBlueCrystalChest extends TileEntityLockable implements ITickable, IInventory
{
    private int ticksSinceSync = -1;
    public float prevLidAngle;
    public float lidAngle;
    private int numUsingPlayers;
    private CrystalChestType type;
    public ItemStack[] chestContents;
    private ItemStack[] topStacks;
    private byte facing;
    private boolean inventoryTouched;
    private boolean hadStuff;
    private String customName;

    public TileEntityBlueCrystalChest()
    {
        this(CrystalChestType.BLUE);
    }

    protected TileEntityBlueCrystalChest(CrystalChestType type)
    {
        super();
        this.type = type;
        this.chestContents = new ItemStack[getSizeInventory()];
        this.topStacks = new ItemStack[8];
    }

    public ItemStack[] getContents()
    {
        return chestContents;
    }

    public void setContents(ItemStack[] contents)
    {
        chestContents = new ItemStack[getSizeInventory()];
        for (int i = 0; i < contents.length; i++)
        {
            if (i < chestContents.length)
            {
                chestContents[i] = contents[i];
            }
        }
        inventoryTouched = true;
    }

    @Override
    public int getSizeInventory()
    {
        return type.size;
    }

    public int getFacing()
    {
        return this.facing;
    }

    public CrystalChestType getType()
    {
        return type;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        inventoryTouched = true;
        return chestContents[i];
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        sortTopStacks();
    }

    protected void sortTopStacks()
    {
        if (!type.isTransparent() || (worldObj != null && worldObj.isRemote))
        {
            return;
        }
        ItemStack[] tempCopy = new ItemStack[getSizeInventory()];
        boolean hasStuff = false;
        int compressedIdx = 0;
        mainLoop: for (int i = 0; i < getSizeInventory(); i++)
        {
            if (!ItemStackTools.isNullStack(chestContents[i]))
            {
                for (int j = 0; j < compressedIdx; j++)
                {
                    if (tempCopy[j].isItemEqual(chestContents[i]))
                    {
                    	ItemStackTools.incStackSize(tempCopy[j], ItemStackTools.getStackSize(chestContents[i]));
                        continue mainLoop;
                    }
                }
                tempCopy[compressedIdx++] = chestContents[i].copy();
                hasStuff = true;
            }
        }
        if (!hasStuff && hadStuff)
        {
            hadStuff = false;
            for (int i = 0; i < topStacks.length; i++)
            {
                topStacks[i] = null;
            }
            if (worldObj != null)
            {
            	BlockUtil.markBlockForUpdate(getWorld(), getPos());
            }
            return;
        }
        hadStuff = true;
        Arrays.sort(tempCopy, new Comparator<ItemStack>()
        {
            @Override
            public int compare(ItemStack o1, ItemStack o2)
            {
                if (ItemStackTools.isNullStack(o1))
                {
                    return 1;
                } else if (ItemStackTools.isNullStack(o2))
                {
                    return -1;
                } else
                {
                    return ItemStackTools.getStackSize(o2) - ItemStackTools.getStackSize(o1);
                }
            }
        });
        int p = 0;
        for (int i = 0; i < tempCopy.length; i++)
        {
            if (ItemStackTools.isValid(tempCopy[i]))
            {
                topStacks[p++] = tempCopy[i];
                if (p == topStacks.length)
                {
                    break;
                }
            }
        }
        for (int i = p; i < topStacks.length; i++)
        {
            topStacks[i] = ItemStackTools.getEmptyStack();
        }
        if (worldObj != null)
        {
        	BlockUtil.markBlockForUpdate(getWorld(), getPos());
        }
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (!ItemStackTools.isNullStack(chestContents[i]))
        {
            if (ItemStackTools.getStackSize(chestContents[i]) <= j)
            {
                ItemStack itemstack = chestContents[i];
                chestContents[i] = ItemStackTools.getEmptyStack();
                markDirty();
                return itemstack;
            }
            ItemStack itemstack1 = chestContents[i].splitStack(j);
            if (ItemStackTools.isEmpty(chestContents[i]))
            {
                chestContents[i] = ItemStackTools.getEmptyStack();
            }
            markDirty();
            return itemstack1;
        } else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        chestContents[i] = itemstack;
        if (ItemStackTools.getStackSize(itemstack) > getInventoryStackLimit())
        {
        	ItemStackTools.setStackSize(itemstack, getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : type.name();
    }

    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String name)
    {
        this.customName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);

        NBTTagList nbttaglist = nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        this.chestContents = new ItemStack[getSizeInventory()];

        if (nbttagcompound.hasKey("CustomName", Constants.NBT.TAG_STRING))
        {
            this.customName = nbttagcompound.getString("CustomName");
        }

        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 0xff;
            if (j >= 0 && j < chestContents.length)
            {
                chestContents[j] = ItemStackTools.loadFromNBT(nbttagcompound1);
            }
        }
        facing = nbttagcompound.getByte("facing");
        sortTopStacks();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < chestContents.length; i++)
        {
            if (chestContents[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                chestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbttagcompound.setTag("Items", nbttaglist);
        nbttagcompound.setByte("facing", facing);

        if (this.hasCustomName())
        {
            nbttagcompound.setString("CustomName", this.customName);
        }
        return nbttagcompound;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        if (worldObj == null)
        {
            return true;
        }
        if (worldObj.getTileEntity(pos) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
    }

    @Override
    public void update()
    {
        // Resynchronize clients with the server state
        if (worldObj != null && !this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            this.numUsingPlayers = 0;
            float var1 = 5.0F;
            List<EntityPlayer> var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - var1, pos.getY() - var1, pos.getZ() - var1, pos.getX() + 1 + var1, pos.getY() + 1 + var1, pos.getZ() + 1 + var1));

            for (EntityPlayer var4 : var2)
            {
                if (var4.openContainer instanceof ContainerCrystalChest)
                {
                    ++this.numUsingPlayers;
                }
            }
        }

        if (worldObj != null && !worldObj.isRemote && ticksSinceSync < 0)
        {
            worldObj.addBlockEvent(pos, ModBlocks.crystalChest, 3, ((numUsingPlayers << 3) & 0xF8) | (facing & 0x7));
        }
        if (!worldObj.isRemote && inventoryTouched)
        {
            inventoryTouched = false;
            sortTopStacks();
        }

        this.ticksSinceSync++;
        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (numUsingPlayers > 0 && lidAngle == 0.0F)
        {
            double d = pos.getX() + 0.5D;
            double d1 = pos.getZ() + 0.5D;
            worldObj.playSound(null, d, pos.getY() + 0.5D, d1, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
        if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (numUsingPlayers > 0)
            {
                lidAngle += f;
            } else
            {
                lidAngle -= f;
            }
            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }
            float f2 = 0.5F;
            if (lidAngle < f2 && f1 >= f2)
            {
                double d2 = pos.getX() + 0.5D;
                double d3 = pos.getZ() + 0.5D;
                worldObj.playSound(null, d2, pos.getY() + 0.5D, d3, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if (i == 1)
        {
            numUsingPlayers = j;
        } else if (i == 2)
        {
            facing = (byte) j;
        } else if (i == 3)
        {
            facing = (byte) (j & 0x7);
            numUsingPlayers = (j & 0xF8) >> 3;
        }
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        if (worldObj == null)
        {
            return;
        }
        numUsingPlayers++;
        worldObj.addBlockEvent(pos, ModBlocks.crystalChest, 1, numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        if (worldObj == null)
        {
            return;
        }
        numUsingPlayers--;
        worldObj.addBlockEvent(pos, ModBlocks.crystalChest, 1, numUsingPlayers);
    }

    public void setFacing(byte facing2)
    {
        this.facing = facing2;
    }

    public ItemStack[] getTopItemStacks()
    {
        return topStacks;
    }

    public TileEntityBlueCrystalChest updateFromMetadata(int l)
    {
        if (worldObj != null && worldObj.isRemote)
        {
            if (l != type.ordinal())
            {
                worldObj.setTileEntity(pos, CrystalChestType.makeEntity(l));
                return (TileEntityBlueCrystalChest) worldObj.getTileEntity(pos);
            }
        }
        return this;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("type", getType().ordinal());
        nbt.setByte("facing", facing);
        ItemStack[] stacks = buildItemStackDataList();
        if (stacks != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < stacks.length; i++)
            {
                if (stacks[i] != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    stacks[i].writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbt.setTag("stacks", nbttaglist);
        }

        return new SPacketUpdateTileEntity(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        if (pkt.getTileEntityType() == 0)
        {
            NBTTagCompound nbt = pkt.getNbtCompound();
            type = CrystalChestType.values()[nbt.getInteger("type")];
            facing = nbt.getByte("facing");

            NBTTagList tagList = nbt.getTagList("stacks", Constants.NBT.TAG_COMPOUND);
            ItemStack[] stacks = new ItemStack[topStacks.length];

            for (int i = 0; i < stacks.length; i++)
            {
                NBTTagCompound nbt1 = tagList.getCompoundTagAt(i);
                int j = nbt1.getByte("Slot") & 0xff;
                if (j >= 0 && j < stacks.length)
                {
                    stacks[j] = ItemStackTools.loadFromNBT(nbt1);
                }
            }

            if (type.isTransparent() && stacks != null)
            {
                int pos = 0;
                for (int i = 0; i < topStacks.length; i++)
                {
                    if (stacks[pos] != null)
                    {
                        topStacks[i] = stacks[pos];
                    } else
                    {
                        topStacks[i] = null;
                    }
                    pos++;
                }
            }
        }
    }

    public ItemStack[] buildItemStackDataList()
    {
        if (type.isTransparent())
        {
            ItemStack[] sortList = new ItemStack[topStacks.length];
            int pos = 0;
            for (ItemStack is : topStacks)
            {
                if (is != null)
                {
                    sortList[pos++] = is;
                } else
                {
                    sortList[pos++] = null;
                }
            }
            return sortList;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int par1)
    {
        if (this.chestContents[par1] != null)
        {
            ItemStack var2 = this.chestContents[par1];
            this.chestContents[par1] = ItemStackTools.getEmptyStack();
            return var2;
        } else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }

    public void rotateAround()
    {
        facing++;
        if (facing > EnumFacing.EAST.ordinal())
        {
            facing = (byte) EnumFacing.NORTH.ordinal();
        }
        setFacing(facing);
        worldObj.addBlockEvent(pos, ModBlocks.crystalChest, 2, facing);
    }

    public void wasPlaced(EntityLivingBase entityliving, ItemStack itemStack)
    {
    }

    public void removeAdornments()
    {
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < this.chestContents.length; ++i)
        {
            this.chestContents[i] = null;
        }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
    {
        return null;
    }

    @Override
    public String getGuiID()
    {
        return "CrystalMod:" + type.name();
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }
}