package alec_wam.CrystalMod.world.crystex;

import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.world.crystex.CrystexiumSpikeStructure.SpikeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenCrystexiumSpike extends MapGenStructure {

	@Override
	public String getStructureName() {
		return CrystalMod.resource("crystexiumspike");
	}

	@Override
	public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean p_180706_3_) {
		this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, 16, 8, 14357617, false, 100, p_180706_3_);
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		int distance = 16;
		int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= distance - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= distance - 1;
        }

        int k = chunkX / distance;
        int l = chunkZ / distance;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * distance;
        l = l * distance;
        k = k + random.nextInt(distance - 8);
        l = l + random.nextInt(distance - 8);

        if (i == k && j == l)
        {
            return true;
        }

        return false;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		return new Start(this.world, this.rand, chunkX, chunkZ);
	}
	
	public static class Start extends StructureStart
    {
        public Start()
        {
        }
        
        public Start(World worldIn, Random random, int chunkX, int chunkZ)
        {
            super(chunkX, chunkZ);
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
            BlockPos generatePos = worldIn.getTopSolidOrLiquidBlock(blockpos);
            if(generatePos.getY() >= 12){
            	generatePos = generatePos.offset(EnumFacing.DOWN, 11);
            }
            SpikeType type = SpikeType.PLAIN;
            int typeNum = MathHelper.getInt(random, 0, SpikeType.values().length-1);
            type = SpikeType.values()[typeNum];
            int size = MathHelper.getInt(random, 0, 6);
            CrystexiumSpikeStructure temple = new CrystexiumSpikeStructure(worldIn.getSaveHandler().getStructureTemplateManager(), generatePos, type, size);
            this.components.add(temple);
            this.updateBoundingBox();
        }
    }

}
