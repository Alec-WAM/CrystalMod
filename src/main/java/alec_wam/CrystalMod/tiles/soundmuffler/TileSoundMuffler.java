package alec_wam.CrystalMod.tiles.soundmuffler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class TileSoundMuffler extends TileEntityModStatic implements IMessageHandler {

	private LinkedList<String> soundList = new LinkedList<String>();
	private float volume;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagList listSound = new NBTTagList();
		for(int i = 0; i < soundList.size(); i++){
			NBTTagCompound soundNBT = new NBTTagCompound();
			soundNBT.setInteger("#", i);
			soundNBT.setString("Name", soundList.get(i));
			listSound.appendTag(soundNBT);
		}
		nbt.setTag("SoundList", listSound);
		nbt.setFloat("Volume", volume);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		NBTTagList listSound = nbt.getTagList("SoundList", Constants.NBT.TAG_COMPOUND);
		String[] loadedSounds = new String[listSound.tagCount()];
		for(int i = 0; i < listSound.tagCount(); i++){
			NBTTagCompound soundNBT = listSound.getCompoundTagAt(i);
			loadedSounds[soundNBT.getInteger("#")] = soundNBT.getString("Name");
		}
		soundList = new LinkedList<String>(Arrays.<String>asList(loadedSounds));
		volume = nbt.getFloat("Volume");
	}
	
	public boolean addSoundToList(ResourceLocation event){
		if(!soundList.contains(event.toString())){
			soundList.add(event.toString());
			return true;
		}
		return false;
	}
	
	public boolean removeSoundFromList(String event){
		soundList.remove(event);
		return !soundList.contains(event);
	}
	
	public boolean isSoundInList(ResourceLocation res){
		return soundList.contains(res.toString());
	}

	/**
	 * @return the volume
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(float volume) {
		this.volume = volume;
	}

	public List<String> getSoundList() {
		return soundList;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("AddSound")){
			if(messageData.hasKey("Name")){
				addSoundToList(new ResourceLocation(messageData.getString("Name")));
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
		}
		if(messageId.equalsIgnoreCase("RemoveSound")){
			if(messageData.hasKey("Name")){
				removeSoundFromList(messageData.getString("Name"));
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
		}
	}
	
}
