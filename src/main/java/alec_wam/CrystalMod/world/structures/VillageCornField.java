package alec_wam.CrystalMod.world.structures;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.BlockCorn;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class VillageCornField extends StructureVillagePieces.Village
{
    public VillageCornField()
    {
    }

    public VillageCornField(StructureVillagePieces.Start start, int p_i45569_2_, Random rand, StructureBoundingBox p_i45569_4_, EnumFacing facing)
    {
        super(start, p_i45569_2_);
        this.setCoordBaseMode(facing);
        this.boundingBox = p_i45569_4_;
    }

    public static VillageCornField createPiece(StructureVillagePieces.Start start, List<StructureComponent> p_175852_1_, Random rand, int p_175852_3_, int p_175852_4_, int p_175852_5_, EnumFacing facing, int p_175852_7_)
    {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175852_3_, p_175852_4_, p_175852_5_, 0, 0, 0, 7, 4, 9, facing);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175852_1_, structureboundingbox) == null ? new VillageCornField(start, p_175852_7_, rand, structureboundingbox, facing) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
     * Mineshafts at the end, it adds Fences...
     */
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
    {
        if (this.averageGroundLvl < 0)
        {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 4 - 1, 0);
        }

        IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 6, 4, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getDefaultState(), Blocks.FARMLAND.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 0, 0, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 5, 0, 8, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);

        for (int i = 1; i <= 7; ++i)
        {
            int ageLL = MathHelper.getInt(randomIn, 0, 4);
            int ageLR = MathHelper.getInt(randomIn, 0, 4);
            int ageRL = MathHelper.getInt(randomIn, 0, 4);
            int ageRR = MathHelper.getInt(randomIn, 0, 4);
            IBlockState corn = ModBlocks.corn.getDefaultState().withProperty(BlockCorn.TOP, false);
            IBlockState cornTop = ModBlocks.corn.getDefaultState().withProperty(BlockCorn.TOP, true);
            this.setBlockState(worldIn, corn.withProperty(BlockCorn.AGE, ageLL), 1, 1, i, structureBoundingBoxIn);
            if(ageLL >= 1){
            	this.setBlockState(worldIn, cornTop.withProperty(BlockCorn.AGE, ageLL), 1, 2, i, structureBoundingBoxIn);                	
            }
            this.setBlockState(worldIn, corn.withProperty(BlockCorn.AGE, ageLR), 2, 1, i, structureBoundingBoxIn);
            if(ageLR >= 1){
            	this.setBlockState(worldIn, cornTop.withProperty(BlockCorn.AGE, ageLR), 2, 2, i, structureBoundingBoxIn);                	
            }
            
            this.setBlockState(worldIn, corn.withProperty(BlockCorn.AGE, ageRL), 4, 1, i, structureBoundingBoxIn);
            if(ageRL >= 1){
            	this.setBlockState(worldIn, cornTop.withProperty(BlockCorn.AGE, ageRL), 4, 2, i, structureBoundingBoxIn);                	
            }
            this.setBlockState(worldIn, corn.withProperty(BlockCorn.AGE, ageRR), 5, 1, i, structureBoundingBoxIn);
            if(ageRR >= 1){
            	this.setBlockState(worldIn, cornTop.withProperty(BlockCorn.AGE, ageRR), 5, 2, i, structureBoundingBoxIn);                	
            }
        }

        for (int j1 = 0; j1 < 9; ++j1)
        {
            for (int k1 = 0; k1 < 7; ++k1)
            {
                this.clearCurrentPositionBlocksUpwards(worldIn, k1, 4, j1, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, Blocks.DIRT.getDefaultState(), k1, -1, j1, structureBoundingBoxIn);
            }
        }

        return true;
    }
}
