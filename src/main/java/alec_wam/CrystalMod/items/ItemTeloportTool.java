package alec_wam.CrystalMod.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.tools.grapple.EntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;

public class ItemTeloportTool extends Item implements ICustomModel {

	public ItemTeloportTool() {
		super();
		setMaxStackSize(1);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "telepearl");
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		final ModelResourceLocation locUnBound = new ModelResourceLocation("crystalmod:telepearl", "bound=false");
		final ModelResourceLocation locBound = new ModelResourceLocation("crystalmod:telepearl", "bound=true");
		ModelBakery.registerItemVariants(this, locUnBound, locBound);
		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
            	boolean bound = !ItemStackTools.isNullStack(stack) && stack.hasTagCompound() && ItemNBTHelper.verifyExistance(stack, "TeleportLocation");
            	return bound ? locBound : locUnBound;
            }
        });
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv){
		if(!ItemStackTools.isNullStack(stack) && stack.hasTagCompound()){
			TeleportLocation loc = getLocation(stack);
			if(loc !=null){
				list.add("Location:");
				list.add("X:"+((int)loc.getXCoord())+" Y:"+((int)loc.getYCoord())+" Z:"+((int)loc.getZCoord()));
				list.add("Dimension: "+loc.getDimensionName());
				list.add(TextFormatting.DARK_GRAY+"Sneak+Use to clear this location.");
			}
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if(getLocation(itemStackIn) !=null){
			if(playerIn.isSneaking()){
				this.removeLocation(itemStackIn);
				ChatUtil.sendChat(playerIn, "Removed Teleport Location");
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
			getLocation(itemStackIn).sendEntityToCoords(playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}else{
			if(!playerIn.isSneaking()){
				TeleportLocation loc = getCurrentLocation(playerIn);
				loc.setDimentionName(worldIn.provider.getDimensionType().getName());
				setLocation(itemStackIn, loc);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
		}
    }
	
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity){
		World world = player.getEntityWorld();
		if(getLocation(stack) !=null){
			if (entity instanceof EntityPlayer){
				EntityPlayer player2 = (EntityPlayer) entity;
				if (player2.isSneaking()){
					getLocation(stack).sendEntityToCoords(player2);
				}else{
					if (!world.isRemote){
						ChatUtil.sendNoSpam(player, player2.getDisplayName()+" needs to be sneaking inorder to teloport them");
					}
				}
				return true;
			}else if (entity instanceof EntityLiving){
				getLocation(stack).sendEntityToCoords(entity);
				return true;
			}
		}
		return super.onLeftClickEntity(stack, player, entity);
	}
	
	/**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly(Entity ent)
    {
        double d0 = ent.posX + (ent.getEntityWorld().rand.nextDouble() - 0.5D) * 32.0D;
        double d1 = ent.posY + (double)(ent.getEntityWorld().rand.nextInt(64) - 32);
        double d2 = ent.posZ + (ent.getEntityWorld().rand.nextDouble() - 0.5D) * 32.0D;
        return this.teleportTo(ent, d0, d1, d2);
    }
    
    /**
     * Teleport the enderman
     */
    @SuppressWarnings("deprecation")
	protected boolean teleportTo(Entity ent, double par1, double par3, double par5)
    {
        
        double d3 = ent.posX;
        double d4 = ent.posY;
        double d5 = ent.posZ;
        ent.posX =par1;
        ent.posY = par3;
        ent.posZ = par5;
        boolean flag = false;
        int i = MathHelper.floor(ent.posX);
        int j = MathHelper.floor(ent.posY);
        int k = MathHelper.floor(ent.posZ);

        BlockPos pos = new BlockPos(i, j, k);
        
        if (ent.getEntityWorld().isBlockLoaded(pos))
        {
            boolean flag1 = false;

            while (!flag1 && j > 0)
            {
            	IBlockState state = ent.getEntityWorld().getBlockState(pos.offset(EnumFacing.DOWN));
                Block block = state.getBlock();

                if (block.getMaterial(state).blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --ent.posY;
                    --j;
                }
            }

            if (flag1)
            {
            	ent.setPosition(ent.posX, ent.posY, ent.posZ);

                if (ent.getEntityWorld().getCollisionBoxes(ent, ent.getEntityBoundingBox()).isEmpty() && !ent.getEntityWorld().containsAnyLiquid(ent.getEntityBoundingBox()))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
        	ent.setPosition(d3, d4, d5);
            return false;
        }
        else
        {
            short short1 = 128;

            for (int l = 0; l < short1; ++l)
            {
                double d6 = (double)l / ((double)short1 - 1.0D);
                float f = (ent.getEntityWorld().rand.nextFloat() - 0.5F) * 0.2F;
                float f1 = (ent.getEntityWorld().rand.nextFloat() - 0.5F) * 0.2F;
                float f2 = (ent.getEntityWorld().rand.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (ent.posX - d3) * d6 + (ent.getEntityWorld().rand.nextDouble() - 0.5D) * (double)ent.width * 2.0D;
                double d8 = d4 + (ent.posY - d4) * d6 + ent.getEntityWorld().rand.nextDouble() * (double)ent.height;
                double d9 = d5 + (ent.posZ - d5) * d6 + (ent.getEntityWorld().rand.nextDouble() - 0.5D) * (double)ent.width * 2.0D;
                ent.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, (double)f, (double)f1, (double)f2);
            }

            ent.getEntityWorld().playSound(null, d3, d4, d5, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            ent.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            return true;
        }
    }
	
    public TeleportLocation getLocation(ItemStack stack) {
    	if(ItemStackTools.isNullStack(stack) || !stack.hasTagCompound() || !ItemNBTHelper.verifyExistance(stack, "TeleportLocation"))return null;
    	NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompoundTag("TeleportLocation");
		TeleportLocation destination = new TeleportLocation();
		destination.readFromNBT(nbt);
		return destination;
	}
    
    public void setLocation(ItemStack stack, TeleportLocation loc){
    	if(ItemStackTools.isNullStack(stack))return;
    	if(loc == null){
    		removeLocation(stack);
    		return;
    	}
    	NBTTagCompound nbt = new NBTTagCompound();
    	loc.writeToNBT(nbt);
    	ItemNBTHelper.getCompound(stack).setTag("TeleportLocation", nbt);
    }
    
    public void removeLocation(ItemStack stack){
    	if(ItemStackTools.isNullStack(stack) || !stack.hasTagCompound() || !ItemNBTHelper.verifyExistance(stack, "TeleportLocation"))return;
    	ItemNBTHelper.getCompound(stack).removeTag("TeleportLocation");
    }
    
	public TeleportLocation getCurrentLocation(EntityPlayer player){
		return new TeleportLocation(player.posX, player.posY, player.posZ, player.dimension, player.rotationPitch, player.rotationYaw, "");
	}
	
	public static class TeleportLocation {
		protected double xCoord;
		protected double yCoord;
		protected double zCoord;
		protected int dimension;
		protected float pitch;
		protected float yaw;
		protected String name;
		protected String dimentionName = "";
		protected boolean writeProtected = false;

		public TeleportLocation(){

		}

		public TeleportLocation(double x, double y, double z, int dimension){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = 0;
			this.yaw = 0;
		}

		public TeleportLocation(double x, double y, double z, int dimension, float pitch, float yaw){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = pitch;
			this.yaw = yaw;
		}

		public TeleportLocation(double x, double y, double z, int dimension, float pitch, float yaw, String name){
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.dimension = dimension;
			this.pitch = pitch;
			this.yaw = yaw;
			this.name = name;
		}

		public double getXCoord(){
			return xCoord;
		}

		public double getYCoord(){
			return yCoord;
		}

		public double getZCoord(){
			return zCoord;
		}

		public int getDimension() {return dimension;}

		public String getDimensionName() {
			return dimentionName;
		}

		public float getPitch() {return pitch;}

		public float getYaw() {return yaw;}

		public String getName() {return name;}

		public boolean getWriteProtected() {return writeProtected;}

		public void setXCoord(double x){
			xCoord = x;
		}

		public void setYCoord(double y){
			yCoord = y;
		}

		public void setZCoord(double z){
			zCoord = z;
		}

		public void setDimension(int d) {dimension = d;}

		public void setPitch(float p) {pitch = p;}

		public void setYaw(float y) {yaw = y;}

		public void setName(String s) {name = s;}

		public void setWriteProtected(boolean b) {writeProtected = b;}

		public void writeToNBT(NBTTagCompound compound) {
			compound.setDouble("X", xCoord);
			compound.setDouble("Y", yCoord);
			compound.setDouble("Z", zCoord);
			compound.setInteger("Dimension", dimension);
			compound.setFloat("Pitch", pitch);
			compound.setFloat("Yaw", yaw);
			compound.setString("Name", name);
			compound.setString("DimentionName", dimentionName);
			compound.setBoolean("WP", writeProtected);
		}

		public void readFromNBT(NBTTagCompound compound) {
			xCoord = compound.getDouble("X");
			yCoord = compound.getDouble("Y");
			zCoord = compound.getDouble("Z");
			dimension = compound.getInteger("Dimension");
			pitch = compound.getFloat("Pitch");
			yaw = compound.getFloat("Yaw");
			name = compound.getString("Name");
			dimentionName = compound.getString("DimentionName");
			writeProtected = compound.getBoolean("WP");
		}

		public void sendEntityToCoords(Entity entity){
			entity.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 0.1F, entity.getEntityWorld().rand.nextFloat() * 0.1F + 0.9F);

			teleportEntity(entity, this);
			entity.getEntityWorld().playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 0.1F, entity.getEntityWorld().rand.nextFloat() * 0.1F + 0.9F);
		}

		public void setDimentionName(String dimentionName) {
			this.dimentionName = dimentionName;
		}
		
		private static Entity teleportEntity(Entity entity, TeleportLocation destination)
		{
			if (entity == null || entity.getEntityWorld().isRemote) return entity;

			World startWorld = entity.getEntityWorld();
			World destinationWorld = DimensionManager.getWorld(destination.dimension);

			if (destinationWorld == null){
				ModLogger.warning("Destination world does not exist!");
				return entity;
			}

			Entity mount = entity.getRidingEntity();
			if (mount != null)
			{
				entity.dismountRidingEntity();
				mount = teleportEntity(mount, destination);
			}

			boolean interDimensional = startWorld.provider.getDimension() != destinationWorld.provider.getDimension();

			startWorld.updateEntityWithOptionalForce(entity, false);//added

			if ((entity instanceof EntityPlayerMP) && interDimensional)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;
				player.closeScreen();//added
				player.dimension = destination.dimension;
				player.connection.sendPacket(new SPacketRespawn(player.dimension, player.getEntityWorld().getDifficulty(), destinationWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
				((WorldServer)startWorld).getPlayerChunkMap().removePlayer(player);

				startWorld.playerEntities.remove(player);
				startWorld.updateAllPlayersSleepingFlag();
				int i = entity.chunkCoordX;
				int j = entity.chunkCoordZ;
				if ((entity.addedToChunk) && (((WorldServer)startWorld).getChunkProvider().chunkExists(i, j)))
				{
					startWorld.getChunkFromChunkCoords(i, j).removeEntity(entity);
					startWorld.getChunkFromChunkCoords(i, j).setModified(true);
				}
				startWorld.loadedEntityList.remove(entity);
				startWorld.onEntityRemoved(entity);
			}

			entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, destination.pitch);

			((WorldServer)destinationWorld).getChunkProvider().loadChunk((int)destination.xCoord >> 4, (int)destination.zCoord >> 4);

			destinationWorld.theProfiler.startSection("placing");
			if (interDimensional)
			{
				if (!(entity instanceof EntityPlayer))
				{
					NBTTagCompound entityNBT = new NBTTagCompound();
					entity.isDead = false;
					entityNBT.setString("id", EntityList.getEntityString(entity));
					entity.writeToNBT(entityNBT);
					entity.isDead = true;
					entity = EntityList.createEntityFromNBT(entityNBT, destinationWorld);
					if (entity == null)
					{
						ModLogger.warning("Failed to teleport entity to new location");
						return null;
					}
					entity.dimension = destinationWorld.provider.getDimension();
				}
				destinationWorld.spawnEntity(entity);
				entity.setWorld(destinationWorld);
			}
			entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

			destinationWorld.updateEntityWithOptionalForce(entity, false);
			entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

			if ((entity instanceof EntityPlayerMP))
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;
				if (interDimensional) {
					player.mcServer.getPlayerList().preparePlayer(player, (WorldServer) destinationWorld);
				}
				player.connection.setPlayerLocation(destination.xCoord, destination.yCoord, destination.zCoord, player.rotationYaw, player.rotationPitch);
			}

			destinationWorld.updateEntityWithOptionalForce(entity, false);

			if (((entity instanceof EntityPlayerMP)) && interDimensional)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;
				player.interactionManager.setWorld((WorldServer) destinationWorld);
				player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, (WorldServer) destinationWorld);
				player.mcServer.getPlayerList().syncPlayerInventory(player);

				for (PotionEffect potionEffect : (Iterable<PotionEffect>) player.getActivePotionEffects())
				{
					player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potionEffect));
				}

				player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
				FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, startWorld.provider.getDimension(), destinationWorld.provider.getDimension());
			}
			entity.setLocationAndAngles(destination.xCoord, destination.yCoord, destination.zCoord, destination.yaw, entity.rotationPitch);

			if (mount != null)
			{
				entity.startRiding(mount);
				if ((entity instanceof EntityPlayerMP)) {
					destinationWorld.updateEntityWithOptionalForce(entity, true);
				}
			}
			destinationWorld.theProfiler.endSection();
			entity.fallDistance = 0;
			return entity;
		}
	}
	
}
