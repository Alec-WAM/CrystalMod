package alec_wam.CrystalMod.client.sound;

import alec_wam.CrystalMod.tiles.fusion.TileFusionPedistal;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FusionRunningSound extends PositionedSound implements ITickableSound {

	public TileFusionPedistal pedistal;
	
	protected boolean donePlaying;
	
	public FusionRunningSound(TileFusionPedistal pedistal) {
		super(ModSounds.fusionRunning, SoundCategory.BLOCKS);
        this.pedistal = pedistal;
        xPosF = pedistal.getPos().getX() + 0.5F;
        yPosF = pedistal.getPos().getY() + 0.5F;
        zPosF = pedistal.getPos().getZ() + 0.5F;
        repeat = true;
        repeatDelay = 0;
	}

	@Override
	public void update() {
		if(pedistal == null || pedistal.isInvalid() || !pedistal.isCrafting.getValue()){
			donePlaying = true;
			return;
		}
		if(pedistal.craftingProgress.getValue() >=200){
			donePlaying = true;
			return;
		}
		this.xPosF = (float)(pedistal.getPos().getX() + 0.5f);
		this.yPosF = (float)(pedistal.getPos().getY() + 0.5f);
		this.zPosF = (float)(pedistal.getPos().getZ() + 0.5f);
		int time = 200;
		if(pedistal.craftingProgress.getValue() > 30){
			float progressPitch = (1.0f * (pedistal.craftingProgress.getValue() / time));
			this.volume = 5.0F / (progressPitch);
		}else  this.volume = 0.0F;
	}

	@Override
	public boolean isDonePlaying() {
		return donePlaying;
	}

}
