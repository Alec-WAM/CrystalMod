package alec_wam.CrystalMod.tiles.darkinfection;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInfected extends EnumBlock<BlockInfected.InfectedBlockType> {

	public static final PropertyEnum<InfectedBlockType> TYPE = PropertyEnum.<InfectedBlockType>create("type", InfectedBlockType.class);
	
	public BlockInfected(){
		super(Material.ROCK, TYPE, InfectedBlockType.class);
		this.setHardness(2F);
		this.setCreativeTab(CrystalMod.tabBlocks);
        this.setTickRandomly(true);
	}
	
	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
		IBlockState state = worldIn.getBlockState(pos);
		if(state.getValue(TYPE) != InfectedBlockType.CASING){
			if(entityIn !=null && entityIn instanceof EntityLivingBase){
				EntityLivingBase living = (EntityLivingBase)entityIn;
				if(EntityUtil.rand.nextInt(16) == 0){
					if(!living.isPotionActive(MobEffects.WITHER))living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 20*4, 0));
				}
			}
		}
    }
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
		if(state.getValue(TYPE) != InfectedBlockType.CASING){
			return Lists.newArrayList();
		}
		return super.getDrops(world, pos, state, fortune);
	}
	
	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
		if(state.getValue(TYPE) != InfectedBlockType.CASING){
			return true;
		}
		return super.canSilkHarvest(world, pos, state, player);
    }
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
		if(state.getValue(TYPE) == InfectedBlockType.CASING){
			ItemStack held = player.getHeldItemMainhand();
			if(ItemStackTools.isValid(held)){
				if(held.getItem() == ModItems.megaCrystalPickaxe){
					String color = ItemNBTHelper.getString(held, "Color", "");
	            	if(color.equalsIgnoreCase("pure")){
	            		return 200F;
	            	}
				}
			}
			return -1.0F;
		}
		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		if(blockState.getValue(TYPE) == InfectedBlockType.CASING){
			return 200F;
		}
        return this.blockHardness;
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(TYPE) == InfectedBlockType.CASING){
			return 2000F;
		}
		return super.getExplosionResistance(world, pos, exploder, explosion);
	}
	
	@Override
	public float getExplosionResistance(Entity exploder)
    {
        return this.blockResistance / 5.0F;
    }
	
	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
    {
		if(state.getValue(TYPE) == InfectedBlockType.CASING){
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand)
    {
		if(state.getValue(TYPE) != InfectedBlockType.CASING){
			spawnParticles(worldIn, pos);
		}
    }
	
	private void spawnParticles(World worldIn, BlockPos pos)
    {
        Random random = worldIn.rand;
        double d0 = 0.0625D;

       double d1 = (double)((float)pos.getX() + random.nextFloat());
       double d2 = (double)((float)pos.getY() + random.nextFloat());
       double d3 = (double)((float)pos.getZ() + random.nextFloat());

       if (!worldIn.getBlockState(pos.up()).isOpaqueCube())
       {
    	   d2 = (double)pos.getY() + 0.0625D + 1.0D;
       }

       if (d1 < (double)pos.getX() || d1 > (double)(pos.getX() + 1) || d2 < 0.0D || d2 > (double)(pos.getY() + 1) || d3 < (double)pos.getZ() || d3 > (double)(pos.getZ() + 1))
       {
    	   float f = random.nextFloat() * 0.6F + 0.4F;
    	   worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d1, d2, d3, f * 0.2, f * 0.2, f * 0.2, new int[0]);
       }
    }
	
	public static enum InfectedBlockType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta {
		NORMAL, CHISLED, CASING;

		final int meta;
		
		InfectedBlockType(){
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
	
}
