package alec_wam.CrystalMod.tiles.spawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import alec_wam.CrystalMod.entities.mob.enderman.EntityCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.EntityCrystalPigZombie;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMobEssence extends Item implements ICustomModel{

	public static final String NBT_ENTITYNAME = "Entity";
	public static final String NBT_KILLCOUNT = "Kills";
	public static final EntityEssenceInstance<EntityPig> DEFAULT_PIG = new EntityEssenceInstance<EntityPig>("Pig", EntityPig.class, 16);
	
	@SuppressWarnings("rawtypes")
	private static Map<String, EntityEssenceInstance> entityRegistry = new TreeMap<String, EntityEssenceInstance>();
	
	
	@SuppressWarnings({ "rawtypes" })
	public static EntityEssenceInstance addEntity(EntityEssenceInstance instance){
		entityRegistry.put(instance.getID(), instance);
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public static EntityEssenceInstance getEssence(String id){
		return entityRegistry.get(id);
	}
	
	@SuppressWarnings("rawtypes")
	public static EntityEssenceInstance getEntityEssence(Entity entity){
		for(EntityEssenceInstance<?> e : entityRegistry.values()){
			if(e.isValid(entity)){
				return e;
			}
		}
		return null;
	}
	
	public ItemMobEssence(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "mobessence");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemRenderMobEssence());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		String name = ItemNBTHelper.getString(stack, NBT_ENTITYNAME, "Pig");
		@SuppressWarnings("rawtypes")
		EntityEssenceInstance essence = getEssence(name);
		if(essence !=null){
			essence.addInfo(list);
			int currentKills = ItemNBTHelper.getInteger(stack, NBT_KILLCOUNT, 0);
			list.add(currentKills+ " / "+essence.getNeededKills()+" Kills");
		}
		list.add(Lang.localize("info.mobessence1.txt"));
		list.add(Lang.localize("info.mobessence2.txt"));
		list.add(Lang.localize("info.mobessence3.txt"));
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		Set<String> names = entityRegistry.keySet();
		for(String name : names){
			list.add(createStack(name));
		}
	}
	
	public static ItemStack createStack(String name){
		ItemStack stack = new ItemStack(ModItems.mobEssence);
		ItemNBTHelper.setString(stack, NBT_ENTITYNAME, name);
		return stack;
	}
	
	public static void initDefaultMobs(){
		addEntity(new EntityEssenceInstance<EntityCreeper>("Creeper", EntityCreeper.class, 16));
		addEntity(new EntityEssenceInstance<EntitySkeleton>("Skeleton", EntitySkeleton.class, 16));
		addEntity(new EntityEssenceInstance<EntitySkeleton>("WitherSkeleton", EntitySkeleton.class, 32){
			
			public float getRenderScale(TransformType type){
				return (type == TransformType.GUI || type == TransformType.FIXED) ? 1.4F : super.getRenderScale(type);
			}
			
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -1.2, 0);
			}
			
			public void preSpawn(final EntitySkeleton entity){
				entity.tasks.addTask(4, new EntityAIAttackMelee(entity, 1.2D, false)
			    {
			        public void resetTask()
			        {
			            super.resetTask();
			            entity.setSwingingArms(false);
			        }
			        
			        public void startExecuting()
			        {
			            super.startExecuting();
			            entity.setSwingingArms(true);
			        }
			    });
				entity.func_189768_a(SkeletonType.WITHER);
				entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
				entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
			}
			
			public boolean useInitialSpawn(){
				return false;
			}
			
			public void addInfo(List<String> list){
				list.add(Lang.translateToLocal("entity." + "WitherSkeleton" + ".name"));
			}
		});
		addEntity(new EntityEssenceInstance<EntitySkeleton>("Stray", EntitySkeleton.class, 16){
			public void preSpawn(final EntitySkeleton entity){
				entity.func_189768_a(SkeletonType.STRAY);
			}
			
			public void addInfo(List<String> list){
				list.add(Lang.translateToLocal("entity." + "Stray" + ".name"));
			}
		});
		addEntity(new EntityEssenceInstance<EntitySpider>("Spider", EntitySpider.class, 16));
		addEntity(new EntityEssenceInstance<EntityCaveSpider>("CaveSpider", EntityCaveSpider.class, 16));
		addEntity(new EntityEssenceInstance<EntityZombie>("Zombie", EntityZombie.class, 16));
		addEntity(new EntityEssenceInstance<EntityZombie>("Husk", EntityZombie.class, 16){
			@SuppressWarnings("deprecation")
			public void preSpawn(EntityZombie zombie){
				zombie.func_189778_a(ZombieType.HUSK);
			}
			
			public void addInfo(List<String> list){
				list.add(ZombieType.HUSK.func_190145_d().getUnformattedText());
			}
		});
		addEntity(new EntityEssenceInstance<EntityZombie>("Zombie.Child", EntityZombie.class, 8){
			public void preSpawn(EntityZombie zombie){
				zombie.setChild(true);
			}
			
			public void addInfo(List<String> list){
				super.addInfo(list);
				list.add("Child");
			}
			
			@Override
			public boolean isValid(Entity entity){
				return super.isValid(entity) && entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild();
			}
		});
		addEntity(new EntityEssenceInstance<EntityWitch>("Witch", EntityWitch.class, 16){
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -1.2, 0);
			}
			
			public float getRenderScale(TransformType type){
				return 1.4F;
			}
		});
		
		addEntity(new EntityEssenceInstance<EntitySlime>("Slime", EntitySlime.class, 16){
			public EntitySlime createRenderEntity(World world){
				EntitySlime slime = super.createRenderEntity(world);
				NBTTagCompound nbt = new NBTTagCompound();
				slime.writeEntityToNBT(nbt);
				nbt.setInteger("Size", 3-1);
				slime.readEntityFromNBT(nbt);
				return slime;
			}
			
			public float getRenderScale(TransformType type){
				return 1.4F;
			}
		});
		addEntity(new EntityEssenceInstance<EntitySilverfish>("Silverfish", EntitySilverfish.class, 20));
		addEntity(new EntityEssenceInstance<EntityBat>("Bat", EntityBat.class, 8){
			public EntityBat createRenderEntity(World world){
				EntityBat bat = super.createRenderEntity(world);
				bat.setIsBatHanging(false);
				return bat;
			}
		});
		addEntity(new EntityEssenceInstance<EntitySquid>("Squid", EntitySquid.class, 8){
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, 0, 0);
			}
		});
		addEntity(new EntityEssenceInstance<EntityGuardian>("Guardian", EntityGuardian.class, 32));
		addEntity(new EntityEssenceInstance<EntityWolf>("Wolf", EntityWolf.class, 10));
		addEntity(new EntityEssenceInstance<EntityOcelot>("Ocelot", EntityOcelot.class, 10));
		addEntity(new EntityEssenceInstance<EntityRabbit>("Rabbit", EntityRabbit.class, 8){
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -0.5, 0);
			}
			
			public float getRenderScale(TransformType type){
				return super.getRenderScale(type)*2F;
			}
		});
		addEntity(new EntityEssenceInstance<EntityPolarBear>("PolarBear", EntityPolarBear.class, 8));
		addEntity(new EntityEssenceInstance<EntitySnowman>("Snowman", EntitySnowman.class, 16));
		addEntity(new EntityEssenceInstance<EntityVillager>("Villager", EntityVillager.class, 8));
		addEntity(new EntityEssenceInstance<EntityIronGolem>("IronGolem", EntityIronGolem.class, 8){
			public float getRenderScale(TransformType type){
				return type == TransformType.GUI ? 1.2f : super.getRenderScale(type);
			}
		});
		
		addEntity(new EntityEssenceInstance<EntitySheep>("Sheep", EntitySheep.class, 16));
		addEntity(DEFAULT_PIG);
		addEntity(new EntityEssenceInstance<EntityCow>("Cow", EntityCow.class, 16));
		addEntity(new EntityEssenceInstance<EntityCrystalCow>("CrystalCow", EntityCrystalCow.class, 16));
		addEntity(new EntityEssenceInstance<EntityMooshroom>("Mooshroom", EntityMooshroom.class, 8));
		addEntity(new EntityEssenceInstance<EntityChicken>("Chicken", EntityChicken.class, 16));
		addEntity(new EntityEssenceInstance<EntityHorse>("Horse", EntityHorse.class, 4));
		
		//NETHER
		addEntity(new EntityEssenceInstance<EntityGhast>("Ghast", EntityGhast.class, 8){
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -0.5, 0);
			}
			
			public float getRenderScale(TransformType type){
				return super.getRenderScale(type)/3F;
			}
		});
		addEntity(new EntityEssenceInstance<EntityBlaze>("Blaze", EntityBlaze.class, 16));
		addEntity(new EntityEssenceInstance<EntityPigZombie>("PigZombie", EntityPigZombie.class, 16));
		addEntity(new EntityEssenceInstance<EntityPigZombie>("PigZombie.Child", EntityPigZombie.class, 8){
			public void preSpawn(EntityPigZombie zombie){
				zombie.setChild(true);
			}
			
			public void addInfo(List<String> list){
				super.addInfo(list);
				list.add("Child");
			}
			
			@Override
			public boolean isValid(Entity entity){
				return super.isValid(entity) && entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild();
			}
		});
		addEntity(new EntityEssenceInstance<EntityCrystalPigZombie>("CrystalPigZombie", EntityCrystalPigZombie.class, 16));
		addEntity(new EntityEssenceInstance<EntityMagmaCube>("MagmaCube", EntityMagmaCube.class, 16){
			public EntityMagmaCube createRenderEntity(World world){
				EntityMagmaCube cube = super.createRenderEntity(world);
				NBTTagCompound nbt = new NBTTagCompound();
				cube.writeEntityToNBT(nbt);
				nbt.setInteger("Size", 3-1);
				cube.readEntityFromNBT(nbt);
				return cube;
			}
			
			public float getRenderScale(TransformType type){
				return 1.4F;
			}
		});
		
		//END
		addEntity(new EntityEssenceInstance<EntityEnderman>("Enderman", EntityEnderman.class, 20){
			public float getRenderScale(TransformType type){
				return 1.2F;
			}
			
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -1.2, 0);
			}
		});
		addEntity(new EntityEssenceInstance<EntityCrystalEnderman>("CrystalEnderman", EntityCrystalEnderman.class, 30){
			public float getRenderScale(TransformType type){
				return 1.2F;
			}
			
			public Vec3d getRenderOffset(TransformType type){
				return new Vec3d(0, -1.2, 0);
			}
		});
		addEntity(new EntityEssenceInstance<EntityEndermite>("Endermite", EntityEndermite.class, 8));
		addEntity(new EntityEssenceInstance<EntityShulker>("Skulker", EntityShulker.class, 10));
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		if (!player.canPlayerEdit(pos.offset(side), side, stack))
        {
            return EnumActionResult.FAIL;
        }
		if (!player.isSneaking()) return EnumActionResult.PASS;
		IBlockState iblockstate = world.getBlockState(pos);
		pos = pos.offset(side);
        double d0 = 0.0D;

        if (side == EnumFacing.UP && iblockstate.getBlock() instanceof BlockFence) //Forge: Fix Vanilla bug comparing state instead of block
        {
            d0 = 0.5D;
        }
        
		String name = ItemNBTHelper.getString(stack, "Entity", "Pig");
		EntityEssenceInstance<?> instance = entityRegistry.get(name);
		if(instance !=null){
			EntityLivingBase entity = instance.createEntity(world);
			double sX = pos.getX() + 0.5;
			double sY = pos.getY() + d0;
			double sZ = pos.getZ() + 0.5;
			if (entity == null) {
				return EnumActionResult.PASS;
			}
			entity.setLocationAndAngles(sX, sY, sZ, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0F);
			entity.rotationYawHead = entity.rotationYaw;
			entity.renderYawOffset = entity.rotationYaw;
			if (!world.isRemote) {
				if(instance.useInitialSpawn() && entity instanceof EntityLiving)((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(((EntityLiving)entity))), (IEntityLivingData)null);
                world.spawnEntityInWorld(entity);
                if(entity instanceof EntityLiving)((EntityLiving)entity).playLivingSound();
				if (!player.capabilities.isCreativeMode)
				{
					ItemStackTools.incStackSize(stack, -1);
				}
			}
			return EnumActionResult.SUCCESS;
		}
		
		return EnumActionResult.PASS;
	}
}
