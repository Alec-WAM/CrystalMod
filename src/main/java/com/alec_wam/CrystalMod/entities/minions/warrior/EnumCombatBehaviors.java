package com.alec_wam.CrystalMod.entities.minions.warrior;

import com.alec_wam.CrystalMod.util.Lang;

public enum EnumCombatBehaviors
{
	METHOD_MELEE_AND_RANGED(100, "ai.combat.meleeandranged"),
	METHOD_MELEE_ONLY(101, "ai.combat.meleeonly"),
	METHOD_RANGED_ONLY(102, "ai.combat.rangedonly"),
	METHOD_DO_NOT_FIGHT(103, "ai.combat.donotfight"),
	TRIGGER_ALWAYS(201, "ai.combat.trigger.always"),
	TRIGGER_PLAYER_TAKE_DAMAGE(202, "ai.combat.trigger.playertakedamage"),
	TRIGGER_PLAYER_DEAL_DAMAGE(203, "ai.combat.trigger.playerattacks"),
	TARGET_PASSIVE_MOBS(301, "ai.combat.target.passive"),
	TARGET_HOSTILE_MOBS(302, "ai.combat.target.hostile"),
	TARGET_PASSIVE_OR_HOSTILE_MOBS(303, "ai.combat.target.either");

	private int numericId;
	private String parserId;

	EnumCombatBehaviors(int numericId, String parserId)
	{
		this.numericId = numericId;
		this.parserId = parserId;
	}

	public int getNumericId()
	{
		return numericId;
	}

	public String getParsedText()
	{
		return Lang.localize(parserId);
	}

	public static EnumCombatBehaviors getById(int id)
	{
		for (EnumCombatBehaviors method : values())
		{
			if (method.getNumericId() == id)
			{
				return method;
			}
		}

		return null;
	}
}
