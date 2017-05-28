package alec_wam.CrystalMod.tiles.spawner;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomSpawner extends BlockContainer {

	public BlockCustomSpawner() {
		super(Material.IRON);
		setCreativeTab(CrystalMod.tabBlocks);
		setHardness(10F);
		setResistance(2000F);
		setHarvestLevel("pickaxe", 3);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityCustomSpawner){
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)tile;
			ItemStack stack = playerIn.getHeldItem(hand);
			if(ItemStackTools.isValid(stack)){
				if(stack.getItem() instanceof ItemMobEssence){
					String name = ItemNBTHelper.getString(stack, ItemMobEssence.NBT_ENTITYNAME, "Pig");
					int killCount = ItemNBTHelper.getInteger(stack, ItemMobEssence.NBT_KILLCOUNT, 0);
					if(spawner.getBaseLogic().getEntityNameToSpawn().equals(name))return false;
					@SuppressWarnings("rawtypes")
					EntityEssenceInstance essence = ItemMobEssence.getEssence(name);
					if(essence !=null){
						if(killCount < essence.getNeededKills()) return false;
						spawner.getBaseLogic().setEntityName(name);
						spawner.isSetToSpawn = true;
						BlockUtil.markBlockForUpdate(worldIn, pos);
						if(!playerIn.capabilities.isCreativeMode){
							ItemNBTHelper.setInteger(stack, ItemMobEssence.NBT_KILLCOUNT, 0);
							playerIn.setHeldItem(hand, stack);
						}
						return true;
					}
					return false;
				} else if (stack.getItem() == Items.NETHER_STAR && spawner.getBaseLogic().requiresPlayer){
					spawner.getBaseLogic().requiresPlayer = false;
					BlockUtil.markBlockForUpdate(worldIn, pos);
					if(!playerIn.capabilities.isCreativeMode){
						playerIn.setHeldItem(hand, ItemUtil.consumeItem(stack));
					}
					return true;
				} else if(stack.getItem() == Item.getItemFromBlock(ModBlocks.crystal) && stack.getItemDamage() == CrystalBlockType.PURE.getMeta() && spawner.getBaseLogic().spawnSpeed == 1){
					spawner.getBaseLogic().setSpawnRate(2);
					BlockUtil.markBlockForUpdate(worldIn, pos);
					if(!playerIn.capabilities.isCreativeMode){
						playerIn.setHeldItem(hand, ItemUtil.consumeItem(stack));
					}
					return true;
				} else if(stack.getItem() == Item.getItemFromBlock(ModBlocks.crystalIngot) && stack.getItemDamage() == CrystalIngotBlockType.PURE.getMeta() && spawner.getBaseLogic().spawnSpeed == 2){
					spawner.getBaseLogic().setSpawnRate(3);
					BlockUtil.markBlockForUpdate(worldIn, pos);
					if(!playerIn.capabilities.isCreativeMode){
						playerIn.setHeldItem(hand, ItemUtil.consumeItem(stack));
					}
					return true;
				} else if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() == 1 && !spawner.getBaseLogic().ignoreSpawnRequirements){
					spawner.getBaseLogic().ignoreSpawnRequirements = true;
					BlockUtil.markBlockForUpdate(worldIn, pos);
					if(!playerIn.capabilities.isCreativeMode){
						playerIn.setHeldItem(hand, ItemUtil.consumeItem(stack));
					}
					return true;
				} 
			}else{
				List<String> list = Lists.newArrayList();
				if(!playerIn.isSneaking()){
					list.add(Lang.localize("msg.spawnerInfo1.txt") + ": ");
					EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(spawner.getBaseLogic().getEntityNameToSpawn());
					if(essence !=null){
						List<String> info = Lists.newArrayList();
						essence.addInfo(info);
						for(String line : info){
							list.add(" -"+line);
						}
					}
					list.add(Lang.localize("msg.spawnerInfo2.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().requiresPlayer);
					list.add(Lang.localize("msg.spawnerInfo3.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().ignoreSpawnRequirements);
					list.add(Lang.localize("msg.spawnerInfo4.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().spawnSpeed);
					list.add(Lang.localize("msg.spawnerInfo5.txt"));
				}else{
					list.add(Lang.localize("msg.spawnerInfo6.txt"));
					list.add(Lang.localize("msg.spawnerInfo7.txt"));
					list.add(Lang.localize("msg.spawnerInfo8.txt"));
					list.add(Lang.localize("msg.spawnerInfo9.txt"));
				}
				if(!worldIn.isRemote)ChatUtil.sendNoSpam(playerIn, list.toArray(new String[0]));
				return true;
			}
		}
		return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCustomSpawner();
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCustomSpawner)
		{
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)tile;
			spawner.getBaseLogic().powered = worldIn.isBlockIndirectlyGettingPowered(pos) > 0;
			IBlockState state2 = worldIn.getBlockState(pos);
			worldIn.notifyBlockUpdate(pos, state2, state2, 3);
			spawner.getBaseLogic().setSpawnRate(spawner.getBaseLogic().spawnSpeed);
		}
	}
	
	@Override
	public int getHarvestLevel(IBlockState state) {
		return 4;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_) {
		return Item.getItemFromBlock(ModBlocks.customSpawner);
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 1;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCustomSpawner && !world.isRemote)
		{
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)tile;
			float multiplyer = 0.05F;

			if (spawner.getBaseLogic().ignoreSpawnRequirements)
			{
				EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.GOLDEN_APPLE, 1, 1));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntity(item);
			}
			if (spawner.getBaseLogic().spawnSpeed > 1)
			{
				EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.PURE.getMeta()));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntity(item);
			}
			if (spawner.getBaseLogic().spawnSpeed > 2)
			{
				EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModBlocks.crystalIngot, 1, CrystalIngotBlockType.PURE.getMeta()));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntity(item);
			}
			if (!spawner.getBaseLogic().requiresPlayer)
			{
				EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.NETHER_STAR));
				item.motionX = (-0.5F + world.rand.nextFloat()) * multiplyer;
				item.motionY = (4 + world.rand.nextFloat()) * multiplyer;
				item.motionZ = (-0.5F + world.rand.nextFloat()) * multiplyer;
				world.spawnEntity(item);
			}
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

}
