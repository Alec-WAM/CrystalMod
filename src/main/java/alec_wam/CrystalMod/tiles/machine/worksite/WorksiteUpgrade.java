package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.HashSet;
import java.util.Set;

public enum WorksiteUpgrade
{
	/**
	 * DO NOT EVER CHANGE ENUM ORDERING, WILL FUBAR LOAD/SAVE VALUES FOR ALL WORKSITES, AS THEY ARE RESTORED VIA ORDINAL
	 */
	SIZE_MEDIUM         (0  , new int[]{}   , new int[]{}),
	SIZE_LARGE          (1  , new int[]{0}  , new int[]{0}),
	QUARRY_MEDIUM       (2  , new int[]{}   , new int[]{}),
	QUARRY_LARGE        (3  , new int[]{2}  , new int[]{2}),
	ENCHANTED_TOOLS_1   (4  , new int[]{}   , new int[]{}),
	ENCHANTED_TOOLS_2   (5  , new int[]{4, 5}  , new int[]{4, 5}),
	ENCHANTED_TOOLS_3   (6  , new int[]{5, 6}  , new int[]{5, 6}),
	TOOL_QUALITY_1      (7  , new int[]{}   , new int[]{}),
	TOOL_QUALITY_2      (8  , new int[]{7, 8}  , new int[]{7, 8}),
	TOOL_QUALITY_3      (9  , new int[]{8, 9}, new int[]{8, 9}),
	BASIC_CHUNK_LOADER  (10  , new int[]{}   , new int[]{}),
	QUARRY_CHUNK_LOADER(11  , new int[]{}   , new int[]{}),
	QUARRY_SILK_TOUCH(12  , new int[]{}   , new int[]{}),
	QUARRY_ONLY_ORE(13  , new int[]{}   , new int[]{});

	private int flag = 0;//because enumsets are fubar'd when called in its owning enum (classCastException -- enum is not an enum!)

	private Set<Integer> exclusive;
	private Set<Integer> overrides;

	private WorksiteUpgrade(int flag, int[] exc, int[] ovr)
	{
		this.flag = flag;
		this.exclusive = new HashSet<Integer>(exc.length);
		this.overrides = new HashSet<Integer>(ovr.length);
		for(int i = 0; i < exc.length; i++){exclusive.add(exc[i]);}
		for(int i = 0; i < ovr.length; i++){overrides.add(ovr[i]);}
	}

	/**
	 * Should be used to return localization key for this upgrade.  Should be used in a worksite-upgrade-list GUI (or other...)
	 * @return
	 */
	public String unlocalizedName(){return ItemWorksiteUpgrade.getStack(this).getUnlocalizedName();}

	/**
	 * return true if THIS should override INPUT (input will be removed, this will be applied)
	 * @param ug
	 * @return
	 */
	public boolean overrides(WorksiteUpgrade ug){return overrides.contains(ug.flag);}

	/**
	 * return true if THIS prevents the INPUT from being applied (input will not be applied, no change to upgrades)
	 * @param ug
	 * @return
	 */
	public boolean exclusive(WorksiteUpgrade ug){return exclusive.contains(ug.flag);}

	public int flag(){return flag;}

}
