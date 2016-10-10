package com.alec_wam.CrystalMod.tiles.machine.power.converter;

public enum PowerUnits {

	CU,
	RF;
	
	public int conversionRation = 1;
	
	public int convertTo(PowerUnits external, int amount){
		return (amount * conversionRation) / external.conversionRation;
	}
	
}
