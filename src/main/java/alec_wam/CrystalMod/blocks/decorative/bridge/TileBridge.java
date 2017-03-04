package alec_wam.CrystalMod.blocks.decorative.bridge;

import java.util.Arrays;
import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import net.minecraft.block.BlockPlanks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

public class TileBridge extends TileEntityModStatic {

	private final EnumMap<EnumFacing, Boolean[]> poles = Maps.newEnumMap(EnumFacing.class);
	private final EnumMap<EnumFacing, BlockPlanks.EnumType> bases = Maps.newEnumMap(EnumFacing.class);

	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			NBTTagCompound nbtFace = new NBTTagCompound();
			if(getBase(facing) !=null)nbtFace.setInteger("BaseType", getBase(facing).getMetadata());
			nbtFace.setBoolean("HasLeftPost", hasPost(facing, 0));
			nbtFace.setBoolean("HasRightPost", hasPost(facing, 1));
			nbtFace.setBoolean("HasTopPost", hasPost(facing, 2));
			nbt.setTag(facing.getName().toLowerCase(), nbtFace);
		}
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		for(EnumFacing facing : EnumFacing.HORIZONTALS){
			if(nbt.hasKey(facing.getName().toLowerCase())){
				NBTTagCompound nbtFace = nbt.getCompoundTag(facing.getName().toLowerCase());
				if(nbtFace.hasKey("BaseType")){
					int type = nbtFace.getInteger("BaseType");
					BlockPlanks.EnumType base = BlockPlanks.EnumType.byMetadata(type);
					setBase(facing, base);
				}
				else if(nbtFace.hasKey("HasBase")){
					setBase(facing, BlockPlanks.EnumType.OAK);
				}
				else {
					setBase(facing, null);
				}
				//setBase(facing, nbtFace.getBoolean("HasBase"));
				setPost(facing, 0, nbtFace.getBoolean("HasLeftPost"));
				setPost(facing, 1, nbtFace.getBoolean("HasRightPost"));
				setPost(facing, 2, nbtFace.getBoolean("HasTopPost"));
			}
		}
	}
	
	/**index 0 = Left, 1 = Right, 2 = Top**/
	public void setPost(EnumFacing side, int index, boolean val){
		Boolean[] array = {false, false, false};
		if(poles.containsKey(side)){
			array = poles.get(side);
		}
		array[index] = val;
		poles.put(side, array);
	}
	
	/**index 0 = Left, 1 = Right, 2 = Top**/
	public boolean hasPost(EnumFacing side, int index){
		if(poles.containsKey(side)){
			return poles.get(side)[index];
		} else {
			poles.put(side, new Boolean[]{false, false, false});
		}
		return false;
	}
	
	public void setBase(EnumFacing side, BlockPlanks.EnumType type){
		if(type == null){
			bases.remove(side);
		} else{
			bases.put(side, type);
		}
	}
	
	public BlockPlanks.EnumType getBase(EnumFacing side){
		return bases.getOrDefault(side, null);
	}
}
