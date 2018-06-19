package alec_wam.CrystalMod.world.crystex;

import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock;
import alec_wam.CrystalMod.blocks.crystexium.CrystexiumBlock.CrystexiumBlockType;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class CrystexiumSpikeStructure extends StructureComponentTemplate {

	public static enum SpikeType {
		PLAIN, BLUE, RED, GREEN, DARK, PURE;
	}
	
	public int size = 0;
	public SpikeType type = SpikeType.PLAIN;
	public CrystexiumSpikeStructure()
    {
    }

    public CrystexiumSpikeStructure(TemplateManager manager, BlockPos blockPos, SpikeType type, int size)
    {
    	super(0);
    	this.type = type;
    	this.size = size;
        this.templatePosition = blockPos;
        this.loadTemplate(manager);
    }

    private void loadTemplate(TemplateManager p_191081_1_)
    {
        Template template = p_191081_1_.getTemplate((MinecraftServer)null, CrystalMod.resourceL("crystex/spike_"+size));
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(Rotation.NONE).setReplacedBlock(Blocks.AIR);
        this.setup(template, this.templatePosition, placementsettings);
    }
	
	@Override
	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if(templatePosition == null || worldIn == null || template == null)return false;
		
		//ModLogger.info("Placing "+type+" Spike at "+templatePosition+" Size: "+size);
		
		this.placeSettings.setBoundingBox(structureBoundingBoxIn);
		this.template.addBlocksToWorld(worldIn, this.templatePosition, new ITemplateProcessor(){

			@Override
			public BlockInfo processBlock(World worldIn, BlockPos pos, BlockInfo blockInfoIn) {
				IBlockState currentState = worldIn.getBlockState(pos);
				if(currentState.getBlock() == ModBlocks.crystexiumBlock){
					if(type == SpikeType.BLUE){
						return new BlockInfo(blockInfoIn.pos, ModBlocks.blueCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.NORMAL), blockInfoIn.tileentityData);
					} 
					if(type == SpikeType.RED){
						return new BlockInfo(blockInfoIn.pos, ModBlocks.redCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.NORMAL), blockInfoIn.tileentityData);
					} 
					if(type == SpikeType.GREEN){
						return new BlockInfo(blockInfoIn.pos, ModBlocks.greenCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.NORMAL), blockInfoIn.tileentityData);
					} 
					if(type == SpikeType.DARK){
						return new BlockInfo(blockInfoIn.pos, ModBlocks.darkCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.NORMAL), blockInfoIn.tileentityData);
					} 
					if(type == SpikeType.PURE){
						return new BlockInfo(blockInfoIn.pos, ModBlocks.pureCrystexiumBlock.getDefaultState().withProperty(CrystexiumBlock.TYPE, CrystexiumBlockType.NORMAL), blockInfoIn.tileentityData);
					} 
				}
				return blockInfoIn;
			}

		}, this.placeSettings, 18);
		return true;
	}

	@Override
	protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, World p_186175_3_, Random p_186175_4_, StructureBoundingBox p_186175_5_) {
		
	}

}
