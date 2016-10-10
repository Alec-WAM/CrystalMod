package com.alec_wam.CrystalMod.tiles.matter;

import net.minecraft.nbt.NBTTagCompound;

import com.alec_wam.CrystalMod.tiles.matter.imps.Matter;

public class MatterStack {

	private Matter matter;
	private int meta;
	public int amount;
	
	public MatterStack(Matter matter, int amount){
		this.matter = matter;
		this.meta = 0;
		this.amount = amount;
	}
	public MatterStack(Matter matter, int meta, int amount){
		this(matter, amount);
		this.meta = meta;
	}
	
	public static MatterStack loadMatterStackFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        Matter mat = MatterRegistry.getMatterFromName(nbt.getString("MatterID"));
        if(mat == null)return null;
        int amt = nbt.getInteger("Amount");
        MatterStack stack = new MatterStack(mat, nbt.getInteger("MatterMeta"), amt);
        return stack;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
    	nbt.setString("MatterID", matter.getUnlocalizedName(this));
    	nbt.setInteger("MatterMeta", meta);
        nbt.setInteger("Amount", amount);
        return nbt;
    }

    public final Matter getMatter()
    {
        return matter;
    }
    
    public int getMeta()
    {
        return meta;
    }
    
    public void setMeta(int newMeta)
    {
        meta = newMeta;
    }

    /**
     * @return A copy of this MatterStack
     */
    public MatterStack copy()
    {
        return new MatterStack(getMatter(), meta, amount);
    }
	
}
