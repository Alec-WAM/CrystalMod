package alec_wam.CrystalMod.tiles.machine.worksite.gui;

import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerWorksiteAnimalControl extends ContainerWorksite {

	public WorksiteAnimalFarm worksite;
	public int maxPigs;
	public int maxSheep;
	public int maxCows;
	public int maxChickens;

	public ContainerWorksiteAnimalControl(EntityPlayer player, WorksiteAnimalFarm farm) {
		super(player, farm);
		this.worksite = farm;
		maxPigs = farm.maxPigCount;
		maxSheep = farm.maxSheepCount;
		maxCows = farm.maxCowCount;
		maxChickens = farm.maxChickenCount;
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("pigs", maxPigs);
		tag.setInteger("cows", maxCows);
		tag.setInteger("sheep", maxSheep);
		tag.setInteger("chickens", maxChickens);
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		maxCows = tag.getInteger("cows");
		maxPigs = tag.getInteger("pigs");
		maxChickens = tag.getInteger("chickens");
		maxSheep = tag.getInteger("sheep");
		if (!player.getEntityWorld().isRemote) {
			worksite.maxCowCount = maxCows;
			worksite.maxPigCount = maxPigs;
			worksite.maxChickenCount = maxChickens;
			worksite.maxSheepCount = maxSheep;
			worksite.markDirty();// mark dirty so it get saved to nbt
		}
		refreshGui();
	}

	public void sendSettingsToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("cows", maxCows);
		tag.setInteger("pigs", maxPigs);
		tag.setInteger("chickens", maxChickens);
		tag.setInteger("sheep", maxSheep);
		sendDataToServer(tag);
	}

	@Override
	public void detectAndSendChanges() {
		boolean send = false;
		if (maxPigs != worksite.maxPigCount) {
			maxPigs = worksite.maxPigCount;
			send = true;
		}
		if (maxChickens != worksite.maxChickenCount) {
			maxChickens = worksite.maxChickenCount;
			send = true;
		}
		if (maxSheep != worksite.maxSheepCount) {
			maxSheep = worksite.maxSheepCount;
			send = true;
		}
		if (maxCows != worksite.maxCowCount) {
			maxCows = worksite.maxCowCount;
			send = true;
		}

		if (send) {
			sendInitData();
		}
	}

}
