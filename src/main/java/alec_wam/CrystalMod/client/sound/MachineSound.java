package alec_wam.CrystalMod.client.sound;

import javax.annotation.Nonnull;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MachineSound extends PositionedSound implements ITickableSound {

  private boolean donePlaying;

  public MachineSound(@Nonnull ResourceLocation sound, BlockPos pos, float volume, float pitch) {
	  this(sound, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, volume, pitch);
  }
  
  public MachineSound(@Nonnull ResourceLocation sound, float x, float y, float z, float volume, float pitch) {
    super(sound, SoundCategory.BLOCKS);
    this.xPosF = x;
    this.yPosF = y;
    this.zPosF = z;
    this.volume = volume;
    this.pitch = pitch;
    this.repeat = true;
  }

  @Override
  public void update() {
  }

  @Override
  public boolean isDonePlaying() {
    return donePlaying;
  }

  public void endPlaying() {
    donePlaying = true;
  }

  public void startPlaying() {
    donePlaying = false;
  }

  public MachineSound setVolume(float vol) {
    this.volume = vol;
    return this;
  }

  public MachineSound setPitch(float pitch) {
    this.pitch = pitch;
    return this;
  }

}
