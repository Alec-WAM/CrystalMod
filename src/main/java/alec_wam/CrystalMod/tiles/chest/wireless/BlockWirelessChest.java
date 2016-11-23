package alec_wam.CrystalMod.tiles.chest.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.tiles.pipes.RaytraceResult;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;

import com.google.common.collect.Lists;

public class BlockWirelessChest extends BlockContainer implements ICustomModel 
{
    public BlockWirelessChest()
    {
        super(Material.IRON);

        this.setHardness(3.0F);
        this.setCreativeTab(CrystalMod.tabBlocks);
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel(){
    	ModItems.initBasicModel(Item.getItemFromBlock(this));
    	RenderTileWirelessChest renderer = new RenderTileWirelessChest();
    	ClientRegistry.bindTileEntitySpecialRenderer(TileWirelessChest.class, renderer);
    	ClientProxy.registerItemRenderCustom(getRegistryName().toString(), renderer);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	int code = ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0);
    	
    	String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
		if(UUIDUtils.isUUID(owner)){
			String username = ProfileUtil.getUsername(UUIDUtils.fromString(owner));
			tooltip.add("Owner: " + username);
		}
		String color1 = ItemUtil.getDyeName(WirelessChestHelper.getDye1(code));
		String color2 = ItemUtil.getDyeName(WirelessChestHelper.getDye2(code));
		String color3 = ItemUtil.getDyeName(WirelessChestHelper.getDye3(code));
		tooltip.add("Code: " + color1 + " / " + color2 + " / " + color3);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing direction, float p_180639_6_, float p_180639_7_, float p_180639_8_)
    {
        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileWirelessChest))
        {
            return true;
        }
        
        TileWirelessChest chest = (TileWirelessChest)te;
        
        boolean owner = chest.isOwner(player.getUniqueID());
        
        if(!owner){
        	if(chest.isBoundToPlayer()){
        		if(!world.isRemote)
        		ChatUtil.sendChat(player, "You do not own this chest, "+ProfileUtil.getUsername(chest.getPlayerBound())+" does.");
        	}
        	return true;
        }
        
        if(!world.isRemote){
        	
        	if(stack !=null && ItemUtil.itemStackMatchesOredict(stack, "gemDiamond")){
        		if(!chest.isBoundToPlayer()){
	        		if (!player.capabilities.isCreativeMode)
	                	stack.stackSize--;
	                chest.bindToPlayer(player.getUniqueID());
	                CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "MarkDirty"), chest);
	        		return true;
        		}
        	}
        	EnumDyeColor color = ItemUtil.getDyeColor(stack);
        	RaytraceResult result = doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), player);
        	if(result !=null && color !=null){
        		if(result.component !=null){
        			CollidableComponent comp = result.component;
        			if(comp.data !=null && comp.data instanceof Integer){
        				int index = (Integer) comp.data;
        				if(index > 0){
        					int meta = color.getMetadata();
        					boolean hitSuccess = false;
        	                int oldId = chest.code;
        	                int id = oldId;
        	                int color1 = WirelessChestHelper.getColor1(id);
        	                int color2 = WirelessChestHelper.getColor2(id);
        	                int color3 = WirelessChestHelper.getColor3(id);

        	                if (index == 3)
        	                {
        	                    color3 = meta;
        	                    hitSuccess = true;
        	                }
        	                else if (index == 2)
        	                {
        	                    color2 = meta;
        	                    hitSuccess = true;
        	                }
        	                else if (index == 1)
        	                {
        	                    color1 = meta;
        	                    hitSuccess = true;
        	                }

        	                id = (color1) | (color2 << 4) | (color3 << 8);

        	                if (oldId != id)
        	                {
        	                	if (!player.capabilities.isCreativeMode)
        	                    	stack.stackSize--;
        	                    chest.setCode(id);
        	                    CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "MarkDirty"), chest);
        	                }

        	                if (hitSuccess)
        	                {
        	                    return true;
        	                }
        				}
        			}
        		}
	        }
        }

        if (world.isSideSolid(pos.add(0, 1, 0), EnumFacing.DOWN))
        {
            return true;
        }

        if (world.isRemote)
        {
            return true;
        }

        player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileWirelessChest();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
    	for(EnumDyeColor dye : EnumDyeColor.values()){
    		ItemStack stack = new ItemStack(this, 1, 0);
    		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, WirelessChestHelper.getDefaultCode(dye));
    		list.add(stack);
	    }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState blockState)
    {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this);
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
	
	public static ItemStack createNBTStack(int code, UUID owner){
		ItemStack stack = new ItemStack(ModBlocks.wirelessChest);
		if(owner !=null)ItemNBTHelper.setString(stack, WirelessChestHelper.NBT_OWNER, UUIDUtils.fromUUID(owner));
		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, code);
		return stack;
	}
	
	protected ItemStack getNBTDrop(IBlockAccess world, BlockPos pos, TileEntity tileEntity) {
		ItemStack stack = new ItemStack(this);
		if(tileEntity !=null && tileEntity instanceof TileWirelessChest){
			TileWirelessChest chest = (TileWirelessChest)tileEntity;
			if(chest.isBoundToPlayer())ItemNBTHelper.setString(stack, WirelessChestHelper.NBT_OWNER, UUIDUtils.fromUUID(chest.getPlayerBound()));
			ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, chest.code);
		}else {
			ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE,  0);
		}
		return stack;
	}

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState blockState)
    {
        super.onBlockAdded(world, pos, blockState);
        world.notifyBlockOfStateChange(pos, this);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase entityliving, ItemStack itemStack)
    {
        byte chestFacing = 0;
        int facing = MathHelper.floor_double((entityliving.rotationYaw * 4F) / 360F + 0.5D) & 3;
        if (facing == 0)
        {
            chestFacing = 2;
        }
        if (facing == 1)
        {
            chestFacing = 5;
        }
        if (facing == 2)
        {
            chestFacing = 3;
        }
        if (facing == 3)
        {
            chestFacing = 4;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileWirelessChest)
        {
        	TileWirelessChest teic = (TileWirelessChest) te;
            teic.setFacing(chestFacing);
            if(itemStack.hasTagCompound()){
            	teic.setCode(ItemNBTHelper.getInteger(itemStack, WirelessChestHelper.NBT_CODE, 0));
            	String owner = ItemNBTHelper.getString(itemStack, WirelessChestHelper.NBT_OWNER, WirelessChestHelper.PUBLIC_OWNER);
            	if(UUIDUtils.isUUID(owner)){
            		teic.bindToPlayer(UUIDUtils.fromString(owner));
            	}
            }
            BlockUtil.markBlockForUpdate(world, pos);
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
    	super.breakBlock(world, pos, blockState);
    }

    

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
        return 10000F;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory)
        {
            return Container.calcRedstoneFromInventory((IInventory) te);
        }
        return 0;
    }

    private static final EnumFacing[] validRotationAxes = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };

    @Override
    public EnumFacing[] getValidRotations(World worldObj, BlockPos pos)
    {
        return validRotationAxes;
    }

    @Override
    public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis)
    {
        if (worldObj.isRemote)
        {
            return false;
        }
        if (axis == EnumFacing.UP || axis == EnumFacing.DOWN)
        {
            TileEntity tileEntity = worldObj.getTileEntity(pos);
            if (tileEntity instanceof TileWirelessChest)
            {
            	TileWirelessChest icte = (TileWirelessChest) tileEntity;
                icte.rotateAround();
            }
            return true;
        }
        return false;
    }
    
    //RayTrace
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (!(te instanceof TileWirelessChest)) {
			return null;
		}
		AxisAlignedBB minBB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

		List<RaytraceResult> results = doRayTraceAll(world, pos.getX(),
				pos.getY(), pos.getZ(), player);
		Iterator<RaytraceResult> iter = results.iterator();
		while (iter.hasNext()) {
			CollidableComponent component = iter.next().component;
			if (component == null) {
				iter.remove();
			}
		}

		RaytraceResult hit = RaytraceResult.getClosestHit(EntityUtil.getEyePosition(player), results);
		if (hit != null && hit.component != null && hit.component.bound != null) {
			minBB = hit.component.bound;
		} else {
			minBB = new AxisAlignedBB(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		}
		return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY()
				+ minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX,
				pos.getY() + minBB.maxY, pos.getZ() + minBB.maxZ);
    }
    
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d origin, Vec3d direction) {

      RaytraceResult raytraceResult = doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), origin, direction, null);
      net.minecraft.util.math.RayTraceResult ret = null;
      if (raytraceResult != null) {
        ret = raytraceResult.rayTraceResult;
        if (ret != null) {
          ret.hitInfo = raytraceResult.component;
        }
      }

      return ret;
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer playerIn, World worldIn, BlockPos pos)
    {
    	TileEntity te = worldIn.getTileEntity(pos);
    	if(te !=null && te instanceof TileWirelessChest){
    		TileWirelessChest chest = (TileWirelessChest)te;
    		if(!chest.isOwner(EntityPlayer.getUUID(playerIn.getGameProfile()))){
    			return -1;
    		}
    	}
    	return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos);
    }
    
    public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z,
				entityPlayer);
		if (allHits == null) {
			return null;
		}
		Vec3d origin = EntityUtil.getEyePosition(entityPlayer);
		return RaytraceResult.getClosestHit(origin, allHits);
	}

	public List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z,
			EntityPlayer entityPlayer) {
		double pitch = Math.toRadians(entityPlayer.rotationPitch);
		double yaw = Math.toRadians(entityPlayer.rotationYaw);

		double dirX = -Math.sin(yaw) * Math.cos(pitch);
		double dirY = -Math.sin(pitch);
		double dirZ = Math.cos(yaw) * Math.cos(pitch);

		double reachDistance = CrystalMod.proxy
				.getReachDistanceForPlayer(entityPlayer);

		Vec3d origin = EntityUtil.getEyePosition(entityPlayer);
		Vec3d direction = origin.addVector(dirX * reachDistance, dirY
				* reachDistance, dirZ * reachDistance);
		return doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
	}

	private RaytraceResult doRayTrace(World world, int x, int y, int z,
			Vec3d origin, Vec3d direction, EntityPlayer entityPlayer) {
		List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, origin,
				direction, entityPlayer);
		if (allHits == null) {
			return null;
		}
		return RaytraceResult.getClosestHit(origin, allHits);
	}

	protected List<RaytraceResult> doRayTraceAll(World world, int x, int y,
			int z, Vec3d origin, Vec3d direction, EntityPlayer player) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileWirelessChest)) {
			return null;
		}
		TileWirelessChest chest = (TileWirelessChest) te;
		List<RaytraceResult> hits = new ArrayList<RaytraceResult>();

		if (player == null) {
			player = CrystalMod.proxy.getClientPlayer();
		}

		Collection<CollidableComponent> components = new ArrayList<CollidableComponent>(chest.getCollidableComponents());
		for (CollidableComponent component : components) {
			setBlockBounds((float) component.bound.minX,
					(float) component.bound.minY, (float) component.bound.minZ,
					(float) component.bound.maxX, (float) component.bound.maxY,
					(float) component.bound.maxZ);
			@SuppressWarnings("deprecation")
			RayTraceResult hitPos = super.collisionRayTrace(
					world.getBlockState(pos), world, pos, origin, direction);
			if (hitPos != null) {
				hits.add(new RaytraceResult(component, hitPos));
			}
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);

		return hits;
	}

	private AxisAlignedBB bounds;

	private void setBlockBounds(double f, double g, double h, double i,
			double j, double k) {
		bounds = new AxisAlignedBB(f, g, h, i, j, k);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		if(bounds == null){
			return new AxisAlignedBB(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		}
		return bounds;
	}
}
