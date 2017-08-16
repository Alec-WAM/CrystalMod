package alec_wam.CrystalMod.tiles.cases;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class TileEntityCaseNoteblock extends TileEntityCaseBase {

	private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(new SoundEvent[] {SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS});
	private SoundEvent getInstrument(int p_185576_1_)
    {
        if (p_185576_1_ < 0 || p_185576_1_ >= INSTRUMENTS.size())
        {
            p_185576_1_ = 0;
        }

        return INSTRUMENTS.get(p_185576_1_);
    }
	
	@Override
	public void onOpened() {
		if (getWorld().getBlockState(getPos().up()).getMaterial() == Material.AIR)
		{
			Material material = getWorld().getBlockState(getPos().down()).getMaterial();
			int i = 0;

			if (material == Material.ROCK)
			{
				i = 1;
			}

			if (material == Material.SAND)
			{
				i = 2;
			}

			if (material == Material.GLASS)
			{
				i = 3;
			}

			if (material == Material.WOOD)
			{
				i = 4;
			}
			int note = MathHelper.getInt(getWorld().rand, 0, 24);
			note = MathHelper.clamp(note, 0, 24);

			net.minecraftforge.event.world.NoteBlockEvent.Play e = new net.minecraftforge.event.world.NoteBlockEvent.Play(getWorld(), getPos(), getWorld().getBlockState(getPos()), note, i);
			if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return;
			i = e.getInstrument().ordinal();
			note = e.getVanillaNoteId();
			float f = (float)Math.pow(2.0D, (note - 12) / 12.0D);
			if(!getWorld().isRemote){
				getWorld().playSound((EntityPlayer)null, pos, getInstrument(i), SoundCategory.RECORDS, 3.0F, f);
			}
			else getWorld().spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, note / 24.0D, 0.0D, 0.0D, new int[0]);
		}

		if(!getWorld().isRemote){
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Open"), this);
		}
	}

	@Override
	public void onClosed() {
		
	}

}
