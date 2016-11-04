package alec_wam.CrystalMod.api.tools;

public class AttackData {

	public final float baseDamage;
	public float earlyAttackDamage;
	public float knockback;
	public double rangeBoost;
	public boolean cancelDamage;
	
	public AttackData(float damage){
		this.baseDamage = damage;
	}
	
}
