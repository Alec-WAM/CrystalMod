package alec_wam.CrystalMod.tiles.jar;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.cluster.RenderTileCrystalCluster;
import alec_wam.CrystalMod.tiles.machine.INBTDrop;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockJar extends BlockContainer implements ICustomModel {

	public BlockJar() {
		super(Material.GLASS);
		setHardness(0.8F);
		setResistance(0.5F);
		setCreativeTab(CreativeTabs.BREWING);
		setSoundType(SoundType.GLASS);
	}

	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModBlocks.initBasicModel(this);
		RenderTileJar<TileJar> renderer = new RenderTileJar<TileJar>();
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileJar.class, renderer);
	}
	
	public static final AxisAlignedBB JAR_AABB = new AxisAlignedBB(0.19, 0.0, 0.19, 0.82, 0.8, 0.82);
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		return JAR_AABB;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
		if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
			NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK);
			if(tileNBT.getBoolean("IsShulker")){
				tooltip.add(Lang.localize("tooltip.jar.shulker"));
			}
			if(tileNBT.hasKey("Potion")){
				PotionType type = PotionUtils.getPotionTypeFromNBT(tileNBT);
				if(type !=PotionTypes.EMPTY){
					for (PotionEffect potioneffect : type.getEffects())
		            {
		                String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
		                Potion potion = potioneffect.getPotion();

		                if (potioneffect.getAmplifier() > 0)
		                {
		                    s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
		                }

		                if (potioneffect.getDuration() > 20)
		                {
		                    s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, 1.0F) + ")";
		                }

		                if (potion.isBadEffect())
		                {
		                    tooltip.add(TextFormatting.RED + s1);
		                }
		                else
		                {
		                	tooltip.add(TextFormatting.BLUE + s1);
		                }
		            }
					tooltip.add(Lang.localizeFormat("tooltip.jar.contains", new Object[]{""+tileNBT.getInteger("Count"), "3"}));
				}
			}
		}
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileJar)) return false;
		TileJar jar = (TileJar)tile;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(held.getItem() == Items.SHULKER_SHELL && !jar.isShulkerLamp()){
				jar.setShulkerLamp(true);
				world.checkLightFor(EnumSkyBlock.BLOCK, pos);
				BlockUtil.markBlockForUpdate(world, pos);
				return true;
			}
			else if(held.getItem() == Items.ITEM_FRAME && facing.getAxis().isHorizontal()){
				if(!jar.hasLabel(facing)){
					jar.setHasLabel(facing, true);
					if(!player.capabilities.isCreativeMode){
						player.setHeldItem(hand, ItemUtil.consumeItem(held));
					}
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			else if(held.getItem() == Items.POTIONITEM){
				PotionType type = PotionUtils.getPotionFromItem(held);
				if(type.getEffects().size() > 0 && (jar.getPotion() == type || jar.getPotion() == PotionTypes.EMPTY)){
					if(jar.getPotionCount() < 3){
						if(jar.getPotion() == PotionTypes.EMPTY){
							jar.setPotionType(type);
						}
						jar.setPotionCount(jar.getPotionCount()+1);
						player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
						BlockUtil.markBlockForUpdate(world, pos);
						return true;
					}
				}
			} else if(held.getItem() == Items.GLASS_BOTTLE){
				if(jar.getPotion() !=PotionTypes.EMPTY && jar.getPotionCount() > 0){
					player.setHeldItem(hand, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), jar.getPotion()));
					jar.setPotionCount(jar.getPotionCount()-1);
					if(jar.getPotionCount() <= 0){
						jar.setPotionType(PotionTypes.EMPTY);
					}
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			
		}
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileJar();
	}
	
	@Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileJar) {
        	TileJar jar = (TileJar)tile;
        	if(jar.isShulkerLamp()){
        		return 15;
        	}
        }

        return super.getLightValue(state, world, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileJar) {
        	TileJar jar = (TileJar) tile;
            return jar.getPotionCount();
        }

        return 0;
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
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
		super.breakBlock(world, pos, state);
		if(!world.isRemote){
			CrystalModNetwork.sendToClients((WorldServer)world, pos, new PacketTileMessage(pos, "#LightUpdate#"));
		}
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileJar){
			TileJar jar = (TileJar)tile;
			if(jar.isShulkerLamp() && !player.capabilities.isCreativeMode){
				if(!world.isRemote){
					EntityShulkerBullet bullet = new EntityShulkerBullet(world);
					bullet.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1 + 0.5, pos.getZ() + 0.5, bullet.rotationYaw, bullet.rotationPitch);
					ReflectionHelper.setPrivateValue(EntityShulkerBullet.class, bullet, player, 1);
					ReflectionHelper.setPrivateValue(EntityShulkerBullet.class, bullet, EnumFacing.UP, 2);
					try {
						Method method = EntityShulkerBullet.class.getDeclaredMethod("selectNextMoveDirection", EnumFacing.Axis.class);
						if(method == null){
							method = EntityShulkerBullet.class.getDeclaredMethod("func_184569_a", EnumFacing.Axis.class);
						}
						if(method !=null){
							method.setAccessible(true);
							method.invoke(bullet, EnumFacing.Axis.Y);
						} else {
							throw new Exception("Unable to find the selectNextMoveDirection method in "+EntityShulkerBullet.class.getCanonicalName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					world.spawnEntity(bullet);
				}
				jar.setShulkerLamp(false);
			}
		}

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
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
		super.onBlockHarvested(world, pos, state, player);
    }
	
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileJar)) return;
		TileJar jar = (TileJar)tile;
		ItemStack held = playerIn.getHeldItemMainhand();
		if(ItemStackTools.isValid(held) && held.getItem() == Items.ITEM_FRAME){
			RayTraceResult ray = EntityUtil.getPlayerLookedObject(playerIn);
			if(ray.sideHit !=null){
				if(jar.hasLabel(ray.sideHit)){
					jar.setHasLabel(ray.sideHit, false);
					if(!playerIn.capabilities.isCreativeMode){
						ItemUtil.dropItemOnSide(worldIn, pos, new ItemStack(Items.ITEM_FRAME), ray.sideHit);
					}
					BlockUtil.markBlockForUpdate(worldIn, pos);
				}
			}
		}
    }	
	
	public static final String TILE_NBT_STACK = "TileData";
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this, 1, damageDropped(world.getBlockState(pos)));
		if(tileEntity !=null && tileEntity instanceof INBTDrop){
			INBTDrop machine = (INBTDrop)tileEntity;
			NBTTagCompound nbt = new NBTTagCompound();
			machine.writeToStack(nbt);
			if(!nbt.hasNoTags()){
				ItemNBTHelper.getCompound(stack).setTag(TILE_NBT_STACK, nbt);
			}
		}
		return stack;
	}

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
        TileEntity tile = world.getTileEntity(pos);
        boolean update = false;
        if(ItemNBTHelper.verifyExistance(stack, TILE_NBT_STACK)){
        	if(tile !=null && tile instanceof INBTDrop){
        		INBTDrop machine = (INBTDrop)tile;
        		machine.readFromStack(ItemNBTHelper.getCompound(stack).getCompoundTag(TILE_NBT_STACK));
        		update = true;
        	}
        }
        if(update){
        	world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }

}
