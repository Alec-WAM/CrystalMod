package com.alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.EnumSet;

import javax.vecmath.Vector4f;

import net.minecraft.util.math.BlockPos;


public interface IWorkSite
{

	/**
	 * workers should call this before calling doWork() to make sure that the site
	 * actually has work to do.
	 */
	public boolean hasWork();

	/**
	 * called by workers to validate work-type when IWorker.canWorkAt(IWorkSite) is called
	 * workers should be responsible for maintaining their own list of acceptable work types
	 */
	public WorkType getWorkType();

	public BlockPos getWorkBoundsMin();

	public BlockPos getWorkBoundsMax();

	public boolean userAdjustableBlocks();

	public boolean hasWorkBounds();

	public boolean renderBounds();

	public int getBoundsMaxWidth();

	public int getBoundsMaxHeight();

	public void setBounds(BlockPos p1, BlockPos p2);

	public void setWorkBoundsMax(BlockPos max);

	public void setWorkBoundsMin(BlockPos min);

	/**
	 * Called from container when a user adjusts work bounds for a block.
	 * Tile should take the opportunity to revalidate the selection and/or offset bounds
	 * for tile special placement/offset/whatever
	 */
	public void onBoundsAdjusted();

	/**
	 * Called from container AFTER bounds have been adjusted.  Tile should take this opportunity
	 * to reseat any chunkloading or re-init any scan stuff
	 */
	public void onPostBoundsAdjusted();

	public EnumSet<WorksiteUpgrade> getUpgrades();

	public EnumSet<WorksiteUpgrade> getValidUpgrades();

	/**
	 * Add the input upgrade to the present upgrade set.  Apply any necessary bonuses at this time.<br>
	 * Calling this method with an upgrade that is already present has undefined results.
	 * @param upgrade
	 */
	public void addUpgrade(WorksiteUpgrade upgrade);

	/**
	 * Remove the input upgrade from the present upgrade set.  Remove any bonuses that it had applied.<br>
	 * Calling this method with an upgrade that is not present has undefined results.
	 * @param upgrade
	 */
	public void removeUpgrade(WorksiteUpgrade upgrade);

	public void onBlockBroken();

	public Vector4f boundsColor();

	public static enum WorkType
	{
		MINING("work_type.mining"),
		FARMING("work_type.farming"),
		FORESTRY("work_type.forestry"),
		NONE("work_type.none");
		public final String regName;
		WorkType(String regName){this.regName=regName;}
	}

	public static final class WorksiteImplementation
	{

		private WorksiteImplementation(){}

		public static double getEnergyPerActivation(double efficiencyBonusFactor)
		{
			return 50 - efficiencyBonusFactor;
		}	

		public static double getEfficiencyFactor(EnumSet<WorksiteUpgrade> upgrades)
		{
			double efficiencyBonusFactor = 0.d;
			if(upgrades.contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)){efficiencyBonusFactor+=1;}
			if(upgrades.contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)){efficiencyBonusFactor+=3;}
			if(upgrades.contains(WorksiteUpgrade.ENCHANTED_TOOLS_3)){efficiencyBonusFactor+=5;}
			if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_1)){efficiencyBonusFactor+=5;}
			if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_2)){efficiencyBonusFactor+=15;}
			if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_3)){efficiencyBonusFactor+=25;}
			return efficiencyBonusFactor;
		}

	}

}
