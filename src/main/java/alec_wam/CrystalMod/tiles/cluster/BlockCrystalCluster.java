package alec_wam.CrystalMod.tiles.cluster;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.cluster.BlockCrystalCluster.CustomBlockStateMapper;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster.ClusterData;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryBlockStateMapper;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalCluster extends EnumBlock<BlockCrystalCluster.EnumClusterType> implements ITileEntityProvider, ICustomModel {

	public static enum EnumClusterType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK;

		final int meta;
		
		EnumClusterType(){
			meta = ordinal();
		}
		
		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}
		
	}
	public static final PropertyEnum<EnumClusterType> TYPE = PropertyEnum.<EnumClusterType>create("type", EnumClusterType.class);

	public BlockCrystalCluster() {
		super(Material.GLASS, TYPE, EnumClusterType.class);
		setCreativeTab(CrystalMod.tabBlocks);
		setHardness(3.0f);
        setResistance(5.0f);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.GLASS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(){
		for(EnumClusterType type : EnumClusterType.values()){
			ModBlocks.initBasicModel(this, type.getMeta());
		}
		RenderTileCrystalCluster renderer = new RenderTileCrystalCluster();
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalCluster.class, renderer);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileCrystalCluster){
			TileCrystalCluster cluster = (TileCrystalCluster)tile;
			if(!world.isRemote){
				String power = "Power Output: (Max) "+cluster.getClusterData().getPowerOutput()+" / (Current) "+cluster.getPowerOutput();
				String health = "Health: (Current) "+cluster.getHealth() + " / (Max) " + TimeUtil.MINECRAFT_DAY_TICKS;
				String speed = "Regen: "+cluster.getClusterData().getRegenSpeed();
				ChatUtil.sendNoSpam(player, power, health, speed);
			}
			return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hX, hY, hZ);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(EnumClusterType type : EnumClusterType.values()){
			ItemStack stack = createCluster(new ClusterData(22, 1), TimeUtil.MINECRAFT_DAY_TICKS);
			stack.setItemDamage(type.getMeta());
			list.add(stack);
		}
	}
	
	public static ItemStack createCluster(ClusterData data, int health){
		ItemStack stack = new ItemStack(ModBlocks.crystalCluster);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Health", health);
		nbt.setTag("ClusterData", data.serializeNBT());
		ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
		if(ItemStackTools.isValid(stack)){
			if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
				NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK);
				int health = tileNBT.getInteger("Health");
				ClusterData data = new ClusterData(0, 0);
				data.deserializeNBT(tileNBT.getCompoundTag("ClusterData"));
				tooltip.add("Max Output: "+data.getPowerOutput()+Lang.localize("power.cu")+"/t");
				tooltip.add("Current Output: "+TileCrystalCluster.calculatePowerOutput(data.getPowerOutput(), health, TimeUtil.MINECRAFT_DAY_TICKS)+Lang.localize("power.cu")+"/t");
				
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				float healthPercent = (100.0f * (float)((float)health / (float)TimeUtil.MINECRAFT_DAY_TICKS));
				tooltip.add("Health: "+decimalFormat.format(healthPercent)+"%");
			}
		}
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCrystalCluster();
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	    if (willHarvest) {
	      return true;
	    }
	    return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te,
	      @Nullable ItemStack stack) {
	    super.harvestBlock(worldIn, player, pos, state, te, stack);
	    worldIn.setBlockToAir(pos);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
		if(world == null || pos == null)return super.getPickBlock(state, target, world, pos, player);
		return getNBTDrop(world, pos, world.getTileEntity(pos));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	    if (world == null || pos == null) {
	      return super.getDrops(world, pos, state, fortune);
	    }
    	return Lists.newArrayList(getNBTDrop(world, pos, world.getTileEntity(pos)));
	}
	
	public static final String TILE_NBT_STACK = "TileData";
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof TileCrystalCluster){
			TileCrystalCluster cluster = (TileCrystalCluster)tileEntity;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("Health", cluster.getHealth());
			nbt.setTag("ClusterData", cluster.getClusterData().serializeNBT());
			ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
		}
		return stack;
	}

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
        	if(tile !=null && tile instanceof TileCrystalCluster){
        		TileCrystalCluster cluster = (TileCrystalCluster)tile;
        		NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK);
        		if(tileNBT.hasKey("Health"))cluster.setHealth(tileNBT.getInteger("Health"));
        		if(tileNBT.hasKey("ClusterData")){
	        		ClusterData data = new ClusterData(0, 0);
					data.deserializeNBT(tileNBT.getCompoundTag("ClusterData"));
					cluster.setClusterData(data);
        		}
        		update = true;
        	}
        }
        if(update)BlockUtil.markBlockForUpdate(world, pos);
    }

	public class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
		}

	}

	
}
