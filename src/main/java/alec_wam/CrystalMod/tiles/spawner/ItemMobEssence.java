package alec_wam.CrystalMod.tiles.spawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
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
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.entities.animals.EntityCrystalCow;
import alec_wam.CrystalMod.entities.mob.enderman.EntityCrystalEnderman;
import alec_wam.CrystalMod.entities.mob.zombiePigmen.EntityCrystalPigZombie;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.Lang;

public class ItemMobEssence extends Item implements ICustomModel{

	public static final String NBT_ENTITYNAME = "Entity";
	public static final EntityEssenceInstance<EntityPig> DEFAULT_PIG = new EntityEssenceInstance<EntityPig>(EntityPig.class);
	
	@SuppressWarnings("rawtypes")
	private static Map<String, EntityEssenceInstance> entityRegistry = new HashMap<String, EntityEssenceInstance>();
	
	@SuppressWarnings("rawtypes")
	private static Map<Class, String> classToID = new HashMap<Class, String>();
	
	
	@SuppressWarnings({ "rawtypes" })
	public static EntityEssenceInstance addEntity(String id, EntityEssenceInstance instance){
		entityRegistry.put(id, instance);
		classToID.put(instance.getEntityClass(), id);
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public static EntityEssenceInstance getEssence(String id){
		return entityRegistry.get(id);
	}
	
	@SuppressWarnings("rawtypes")
	public static String getEssenceID(Class clazz){
		return classToID.get(clazz);
	}
	
	
	public ItemMobEssence(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "mobEssence");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRender(getRegistryName().getResourcePath(), new ItemRenderMobEssence());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		String name = ItemNBTHelper.getString(stack, NBT_ENTITYNAME, "Pig");
		@SuppressWarnings("rawtypes")
		EntityEssenceInstance essence = getEssence(name);
		if(essence !=null){
			essence.addInfo(list);
		}
		list.add(Lang.localize("info.mobEssence1.txt"));
		list.add(Lang.localize("info.mobEssence2.txt"));
		list.add(Lang.localize("info.mobEssence3.txt"));
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
		addEntity("Creeper", new EntityEssenceInstance<EntityCreeper>(EntityCreeper.class));
		addEntity("Skeleton", new EntityEssenceInstance<EntitySkeleton>(EntitySkeleton.class));
		addEntity("WitherSkeleton", new EntityEssenceInstance<EntitySkeleton>(EntitySkeleton.class){
			
			public float getRenderScale(TransformType type){
				return 1.4F;
			}
			
			public Vec3d getRenderOffset(){
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
				list.add(Lang.localize("entity." + "WitherSkeleton" + ".name"));
			}
		});
		addEntity("Stray", new EntityEssenceInstance<EntitySkeleton>(EntitySkeleton.class){
			public void preSpawn(final EntitySkeleton entity){
				entity.func_189768_a(SkeletonType.STRAY);
			}
			
			public void addInfo(List<String> list){
				list.add(Lang.translateToLocal("entity." + "Stray" + ".name"));
			}
		});
		addEntity("Spider", new EntityEssenceInstance<EntitySpider>(EntitySpider.class));
		addEntity("CaveSpider", new EntityEssenceInstance<EntityCaveSpider>(EntityCaveSpider.class));
		addEntity("Zombie", new EntityEssenceInstance<EntityZombie>(EntityZombie.class));
		addEntity("Husk", new EntityEssenceInstance<EntityZombie>(EntityZombie.class){
			@SuppressWarnings("deprecation")
			public void preSpawn(EntityZombie zombie){
				zombie.func_189778_a(ZombieType.HUSK);
			}
			
			public void addInfo(List<String> list){
				list.add(ZombieType.HUSK.func_190145_d().getUnformattedText());
			}
		});
		addEntity("Zombie.Child", new EntityEssenceInstance<EntityZombie>(EntityZombie.class){
			public void preSpawn(EntityZombie zombie){
				zombie.setChild(true);
			}
			
			public void addInfo(List<String> list){
				super.addInfo(list);
				list.add("Child");
			}
		});
		addEntity("Witch", new EntityEssenceInstance<EntityWitch>(EntityWitch.class){
			public Vec3d getRenderOffset(){
				return new Vec3d(0, -1.2, 0);
			}
			
			public float getRenderScale(TransformType type){
				return 1.4F;
			}
		});
		
		addEntity("Slime", new EntityEssenceInstance<EntitySlime>(EntitySlime.class){
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
		addEntity("Silverfish", new EntityEssenceInstance<EntitySilverfish>(EntitySilverfish.class));
		addEntity("Bat", new EntityEssenceInstance<EntityBat>(EntityBat.class){
			public EntityBat createRenderEntity(World world){
				EntityBat bat = super.createRenderEntity(world);
				bat.setIsBatHanging(false);
				return bat;
			}
		});
		addEntity("Squid", new EntityEssenceInstance<EntitySquid>(EntitySquid.class){
			public Vec3d getRenderOffset(){
				return new Vec3d(0, 0, 0);
			}
		});
		addEntity("Guardian", new EntityEssenceInstance<EntityGuardian>(EntityGuardian.class));
		addEntity("Wolf", new EntityEssenceInstance<EntityWolf>(EntityWolf.class));
		addEntity("Ocelot", new EntityEssenceInstance<EntityOcelot>(EntityOcelot.class));
		addEntity("Rabbit", new EntityEssenceInstance<EntityRabbit>(EntityRabbit.class){
			public Vec3d getRenderOffset(){
				return new Vec3d(0, -0.5, 0);
			}
			
			public float getRenderScale(TransformType type){
				return super.getRenderScale(type)*2F;
			}
		});
		addEntity("PolarBear", new EntityEssenceInstance<EntityPolarBear>(EntityPolarBear.class));
		addEntity("Snowman", new EntityEssenceInstance<EntitySnowman>(EntitySnowman.class));
		addEntity("Villager", new EntityEssenceInstance<EntityVillager>(EntityVillager.class));
		addEntity("IronGolem", new EntityEssenceInstance<EntityIronGolem>(EntityIronGolem.class){
			public float getRenderScale(TransformType type){
				return type == TransformType.GUI ? 1.2f : super.getRenderScale(type);
			}
		});
		
		addEntity("Sheep", new EntityEssenceInstance<EntitySheep>(EntitySheep.class));
		addEntity("Pig", DEFAULT_PIG);
		addEntity("Cow", new EntityEssenceInstance<EntityCow>(EntityCow.class));
		addEntity("CrystalCow", new EntityEssenceInstance<EntityCrystalCow>(EntityCrystalCow.class));
		addEntity("Mooshroom", new EntityEssenceInstance<EntityMooshroom>(EntityMooshroom.class));
		addEntity("Chicken", new EntityEssenceInstance<EntityChicken>(EntityChicken.class));
		addEntity("Horse", new EntityEssenceInstance<EntityHorse>(EntityHorse.class));
		
		//NETHER
		addEntity("Ghast", new EntityEssenceInstance<EntityGhast>(EntityGhast.class){
			public Vec3d getRenderOffset(){
				return new Vec3d(0, -0.5, 0);
			}
			
			public float getRenderScale(TransformType type){
				return super.getRenderScale(type)/3F;
			}
		});
		addEntity("Blaze", new EntityEssenceInstance<EntityEndermite>(EntityEndermite.class));
		addEntity("PigZombie", new EntityEssenceInstance<EntityPigZombie>(EntityPigZombie.class));
		addEntity("PigZombie.Child", new EntityEssenceInstance<EntityPigZombie>(EntityPigZombie.class){
			public void preSpawn(EntityPigZombie zombie){
				zombie.setChild(true);
			}
			
			public void addInfo(List<String> list){
				super.addInfo(list);
				list.add("Child");
			}
		});
		addEntity("CrystalPigZombie", new EntityEssenceInstance<EntityCrystalPigZombie>(EntityCrystalPigZombie.class));
		addEntity("MagmaCube", new EntityEssenceInstance<EntityMagmaCube>(EntityMagmaCube.class){
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
		addEntity("Enderman", new EntityEssenceInstance<EntityEnderman>(EntityEnderman.class){
			public float getRenderScale(TransformType type){
				return 1.2F;
			}
			
			public Vec3d getRenderOffset(){
				return new Vec3d(0, -1.2, 0);
			}
		});
		addEntity("CrystalEnderman", new EntityEssenceInstance<EntityCrystalEnderman>(EntityCrystalEnderman.class){
			public float getRenderScale(TransformType type){
				return 1.2F;
			}
			
			public Vec3d getRenderOffset(){
				return new Vec3d(0, -1.2, 0);
			}
		});
		addEntity("Endermite", new EntityEssenceInstance<EntityEndermite>(EntityEndermite.class));
		
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
					stack.stackSize--;
				}
			}
			return EnumActionResult.SUCCESS;
		}
		
		return EnumActionResult.PASS;
	}
}
