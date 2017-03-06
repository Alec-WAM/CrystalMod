package alec_wam.CrystalMod.world.structures;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.BlockCrystal;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.DungeonHooks;

public class FusionTempleStructure extends StructureComponentTemplate {

	
	public FusionTempleStructure()
    {
    }

    public FusionTempleStructure(TemplateManager manager, BlockPos blockPos)
    {
    	super(0);
        this.templatePosition = blockPos;
        this.loadTemplate(manager);
    }

    private void loadTemplate(TemplateManager p_191081_1_)
    {
        Template template = p_191081_1_.getTemplate((MinecraftServer)null, CrystalMod.resourceL("fusiontemple"));
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(Rotation.NONE).setReplacedBlock(Blocks.STRUCTURE_VOID);
        this.setup(template, this.templatePosition, placementsettings);
    }
    
    public boolean hasSetupDetails = false;
    public CrystalType templeType = CrystalType.BLUE;
    public int pedistalChance;
    public float difficultyChance;
    
    public void setupVariables(World world, Random random){
    	pedistalChance = MathHelper.getInt(random, 1, 7);
    	templeType = CrystalType.byMetadata(MathHelper.getInt(random, 0, 3));
    	difficultyChance = 0.0f;

		if(world.getDifficulty() !=EnumDifficulty.PEACEFUL){
			if(world.getDifficulty() == EnumDifficulty.EASY){
				difficultyChance = 0.25f;
			}
			if(world.getDifficulty() == EnumDifficulty.NORMAL){
				difficultyChance = 0.75f;
			}
			if(world.getDifficulty() == EnumDifficulty.HARD){
				difficultyChance = 1.0f;
			}
		}
        //ModLogger.info("Placeing Temple {Pos= "+this.templatePosition+", Type= "+templeType.getName()+"}");
    }
	
	@Override
	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if(!hasSetupDetails){
			setupVariables(worldIn, randomIn);
			hasSetupDetails = true;
		}
		BlockPos blockpos = templatePosition;
		this.placeSettings.setBoundingBox(structureBoundingBoxIn);
		this.template.addBlocksToWorld(worldIn, this.templatePosition, new ITemplateProcessor(){

			@Override
			public BlockInfo processBlock(World worldIn, BlockPos pos, BlockInfo blockInfoIn) {
				//Warning this ignores integrity
				IBlockState currentState = blockInfoIn.blockState;
				if(currentState.getBlock() == ModBlocks.crystal){
					BlockCrystal.CrystalBlockType type = (BlockCrystal.CrystalBlockType) currentState.getValue(BlockCrystal.TYPE);
					if(type == BlockCrystal.CrystalBlockType.BLUE_BRICK){
						if(templeType == CrystalType.RED){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.RED_BRICK), blockInfoIn.tileentityData);
						} 
						else if(templeType == CrystalType.GREEN){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.GREEN_BRICK), blockInfoIn.tileentityData);
						}
						else if(templeType == CrystalType.DARK){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.DARK_BRICK), blockInfoIn.tileentityData);
						}
					}
					if(type == BlockCrystal.CrystalBlockType.BLUE_CHISELED){
						if(templeType == CrystalType.RED){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.RED_CHISELED), blockInfoIn.tileentityData);
						} 
						else if(templeType == CrystalType.GREEN){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.GREEN_CHISELED), blockInfoIn.tileentityData);
						}
						else if(templeType == CrystalType.DARK){
							return new BlockInfo(blockInfoIn.pos, currentState.withProperty(BlockCrystal.TYPE, BlockCrystal.CrystalBlockType.DARK_CHISELED), blockInfoIn.tileentityData);
						}
					}
				}

				return blockInfoIn;
			}

		}, this.placeSettings, 18);
		Map<BlockPos, String> map = template.getDataBlocks(blockpos, placeSettings);
		for (Entry<BlockPos, String> entry : map.entrySet())
		{
			String data = entry.getValue();
			BlockPos dataPos = entry.getKey();
			if(data.equals("Spawner")){
				worldIn.setBlockToAir(dataPos);

				if(randomIn.nextFloat() < difficultyChance){
					worldIn.setBlockState(dataPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
					TileEntity tileentity = worldIn.getTileEntity(dataPos);

					if (tileentity instanceof TileEntityMobSpawner)
					{
						((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityId(DungeonHooks.getRandomDungeonMob(randomIn));
					}
				}
			}
			if(data.equals("CenterPedistal")){
				worldIn.setBlockState(dataPos, ModBlocks.fusionPedistal.getDefaultState(), 2);
			}
			if(data.equals("Pedistal")){
				worldIn.setBlockToAir(dataPos);
				if(randomIn.nextInt(pedistalChance) == 0){
					worldIn.setBlockState(dataPos, ModBlocks.pedistal.getDefaultState(), 2);
				}
			}
			if (data.startsWith("Chest"))
			{
				IBlockState iblockstate = Blocks.CHEST.getDefaultState();

				if ("ChestWest".equals(data))
				{
					iblockstate = iblockstate.withProperty(BlockChest.FACING, EnumFacing.WEST);
				}
				else if ("ChestEast".equals(data))
				{
					iblockstate = iblockstate.withProperty(BlockChest.FACING, EnumFacing.EAST);
				}
				else if ("ChestSouth".equals(data))
				{
					iblockstate = iblockstate.withProperty(BlockChest.FACING, EnumFacing.SOUTH);
				}
				else if ("ChestNorth".equals(data))
				{
					iblockstate = iblockstate.withProperty(BlockChest.FACING, EnumFacing.NORTH);
				}

				worldIn.setBlockState(dataPos, iblockstate, 2);
				TileEntity tileentity = worldIn.getTileEntity(dataPos);

				if (tileentity instanceof TileEntityChest)
				{
					ResourceLocation[] chestTypes = {LootTableList.CHESTS_IGLOO_CHEST, LootTableList.CHESTS_SIMPLE_DUNGEON, LootTableList.CHESTS_STRONGHOLD_CORRIDOR};
					((TileEntityChest)tileentity).setLootTable(chestTypes[MathHelper.getInt(randomIn, 0, chestTypes.length-1)], randomIn.nextLong());
				}
			}
		}
		return true;
	}

	@Override
	protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, World p_186175_3_, Random p_186175_4_, StructureBoundingBox p_186175_5_) {
		
	}

}
