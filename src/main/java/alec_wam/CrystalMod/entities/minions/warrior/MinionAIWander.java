package alec_wam.CrystalMod.entities.minions.warrior;

import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import net.minecraft.nbt.NBTTagCompound;

public class MinionAIWander extends AIBase<EntityMinionWarrior>{

	private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;
    private int executionChance;
    private boolean mustUpdate;
	
    public MinionAIWander(double speedIn){
    	this(speedIn, 120);
    }
    
    public MinionAIWander(double speedIn, int chance)
    {
        this.speed = speedIn;
        this.executionChance = chance;
    }
    
    public boolean shouldExecute(EntityMinionWarrior minion)
    {
        /*if (!this.mustUpdate)
        {
            /*if (minion.getAge() >= 100)
            {
                return false;
            }*/

        	/*Random rand = minion.getEntityWorld() != null ? minion.getEntityWorld().rand : minion.getRNG();
        	
            if (rand == null || rand.nextInt(120) != 0)
            {
                return false;
            }
        }

        Vec3d vec3 = MinionRandomPositionGenerator.findRandomTarget(minion, 10, 7);

        if (vec3 == null)
        {
            return false;
        }
        else
        {
            this.xPosition = vec3.xCoord;
            this.yPosition = vec3.yCoord;
            this.zPosition = vec3.zCoord;
            minion.getNavigator().setPath(null, 0.0D);
            this.mustUpdate = false;
            return true;
        }*/
    	return true;
    }
    
	@Override
	public void onUpdateCommon(EntityMinionWarrior minion) {
	}

	@Override
	public void onUpdateClient(EntityMinionWarrior minion) {
	}

	@Override
	public void onUpdateServer(EntityMinionWarrior minion) {
		if(minion.getMovementState() != EnumMovementState.GUARD)return;
		/*if(shouldExecute(minion)){
			if(minion.getNavigator().noPath()){
				minion.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed);
			}
		}*/
	}

	@Override
	public void reset(EntityMinionWarrior minion) {
	}

	@Override
	public void writeToNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {
		nbt.setDouble("xPosition", this.xPosition);
		nbt.setDouble("yPosition", this.yPosition);
		nbt.setDouble("zPosition", this.zPosition);
		nbt.setDouble("speed", this.speed);
		nbt.setInteger("executionChance", this.executionChance);
		nbt.setBoolean("mustUpdate", this.mustUpdate);
	}

	@Override
	public void readFromNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {
		this.xPosition = nbt.getDouble("xPosition");
		this.yPosition = nbt.getDouble("yPosition");
		this.zPosition = nbt.getDouble("zPosition");
		this.speed = nbt.getDouble("speed");
		this.executionChance = nbt.getInteger("executionChance");
		this.mustUpdate = nbt.getBoolean("mustUpdate");
	}
	
	public void makeUpdate()
    {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance)
    {
        this.executionChance = newchance;
    }
    
    public void setSpeed(double speed){
    	this.speed = speed;
    }
    
    public double getSpeed(){
    	return speed;
    }

}
