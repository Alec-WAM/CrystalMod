package alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.PipePart;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.item.TileEntityPipeItem;
import alec_wam.CrystalMod.tiles.pipes.liquid.TileEntityPipeLiquid;
import alec_wam.CrystalMod.tiles.pipes.power.cu.TileEntityPipePowerCU;
import alec_wam.CrystalMod.tiles.pipes.power.rf.TileEntityPipePowerRF;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPipe extends EnumBlock<BlockPipe.PipeType> implements ICustomModel {

	public static enum PipeType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		ITEM(TileEntityPipeItem.class), FLUID(TileEntityPipeLiquid.class), ESTORAGE(TileEntityPipeEStorage.class), 
		POWERCU(TileEntityPipePowerCU.class, true), POWERRF(TileEntityPipePowerRF.class, true);

		public final Class<? extends TileEntityPipe> clazz;
		private final int meta;
		private boolean tiered;
		private int tiers;

		PipeType(Class<? extends TileEntityPipe> clazz){
			meta = ordinal();
			tiered = false;
			tiers = 0;
			this.clazz = clazz;
		}

		PipeType(Class<? extends TileEntityPipe> clazz, boolean tiered){
			this(clazz);
			this.tiered = tiered;
			tiers = 4;
		}

		PipeType(Class<? extends TileEntityPipe> clazz, int tiers){
			this(clazz, true);
			this.tiers = tiers;
		}

		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}

		public boolean isTiered(){
			return tiered;
		}

		public int getNumberOfTiers(){
			return tiers;
		}

		public Class<? extends TileEntityPipe> getTileClass(){
			return clazz;
		}

	}
	public static final PropertyEnum<PipeType> TYPE = PropertyEnum.<PipeType>create("type", PipeType.class);
	public BlockPipe() {
		super(Material.IRON, TYPE, PipeType.class);
		this.setHardness(1.5f);
		this.setResistance(10.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		this.setDefaultState(blockState.getBaseState());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
				return ModelPipeBaked.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
		ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
		//for(PipeType type : PipeType.values())
		// Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this), type.getMeta(), itemModelResourceLocation);
		for(PipeType type : PipeType.values())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), itemModelResourceLocation);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, new TileEntityPipeRenderer());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(PipeType type : PipeType.values()) {
			ItemStack stack = new ItemStack(this, 1, type.getMeta());
			if(type.isTiered()){
				for(int t = 0; t < type.getNumberOfTiers(); t++){
					ItemStack stack2 = new ItemStack(this, 1, type.getMeta());
					ItemNBTHelper.setInteger(stack2, "Tier", t);
					list.add(stack2);
				}
			}else list.add(stack);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity, boolean bool) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (!(te instanceof TileEntityPipe)) {
			return;
		}
		TileEntityPipe con = (TileEntityPipe) te;
		Collection<CollidableComponent> bounds = con.getCollidableComponents();
		for (CollidableComponent bnd : bounds) {
			setBlockBounds((float)bnd.bound.minX, (float)bnd.bound.minY, (float)bnd.bound.minZ, (float)bnd.bound.maxX, (float)bnd.bound.maxY, (float)bnd.bound.maxZ);
			super.addCollisionBoxToList(state, worldIn, pos, mask, list, collidingEntity, bool);
		}

		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}

	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state) {
		PipeType type = state.getValue(TYPE);
		try{
			return type.getTileClass().newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		return new FakeState(state, world, pos, (tile !=null && tile instanceof TileEntityPipe) ? (TileEntityPipe)tile : null);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	private final Random rand = new Random();

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
		IBlockState state = world.getBlockState(pos);
		if (state == null || state.getBlock() != this) {
			return false;
		}
		state = state.getBlock().getActualState(state, world, pos);
		int i = 4;
		TextureAtlasSprite tex = null;
		TileEntity tile = world.getTileEntity(pos);
		if(Util.notNullAndInstanceOf(tile, TileEntityPipe.class)){
			TileEntityPipe pipe = ((TileEntityPipe)tile);
			tex = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(pipe.getPipeType().getCoreTexture(pipe));

			if(tex == null){
				return false;
			}
		}
		for (int j = 0; j < i; ++j) {
			for (int k = 0; k < i; ++k) {
				for (int l = 0; l < i; ++l) {
					double d0 = pos.getX() + (j + 0.5D) / i;
					double d1 = pos.getY() + (k + 0.5D) / i;
					double d2 = pos.getZ() + (l + 0.5D) / i;
					ParticleDigging fx = (ParticleDigging) new ParticleDigging.Factory().createParticle(-1, world, d0, d1, d2, d0 - pos.getX() - 0.5D,
							d1 - pos.getY() - 0.5D, d2 - pos.getZ() - 0.5D, 0);
					fx.setBlockPos(pos);
					fx.setParticleTexture(tex);
					effectRenderer.addEffect(fx);
				}
			}
		}

		return true;
	}

	@Override
	public boolean addLandingEffects(IBlockState iblockstate, net.minecraft.world.WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate2, EntityLivingBase entity, int numberOfParticles )
	{
		IBlockState coverState = null;
		TileEntity tile = worldObj.getTileEntity(blockPosition);
		if(Util.notNullAndInstanceOf(tile, TileEntityPipe.class)){
			TileEntityPipe pipe = ((TileEntityPipe)tile);
			CoverData data = pipe.getCoverData(EnumFacing.UP);
			if(data !=null && data.getBlockState() !=null){
				coverState = data.getBlockState();
			}
			if(coverState == null){
				return false;
			}
		}

		worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] {Block.getStateId(coverState)});
		return true;
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager effectRenderer)
	{
		BlockPos pos = target.getBlockPos();
		EnumFacing side = target.sideHit;
		IBlockState iblockstate = worldObj.getBlockState(pos);

		TextureAtlasSprite sprite = null;

		TileEntity tile = worldObj.getTileEntity(pos);
		if(Util.notNullAndInstanceOf(tile, TileEntityPipe.class)){
			TileEntityPipe pipe = ((TileEntityPipe)tile);
			sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(pipe.getPipeType().getCoreTexture(pipe));

			CoverData data = pipe.getCoverData(side);
			if(data !=null && data.getBlockState() !=null){
				if(data.getBlockState().getBlock().addHitEffects(data.getBlockState(), new PipeWorldWrapper(worldObj, pos, side), target, effectRenderer)){
					return true;
				}
				sprite = RenderUtil.getTexture(data.getBlockState());
			}
			if(sprite == null){
				return false;
			}
		}

		if(iblockstate == null)return false;

		Block block = iblockstate.getBlock();

		if (block.getRenderType(iblockstate) != EnumBlockRenderType.INVISIBLE)
		{
			AxisAlignedBB blockBounds = block.getBoundingBox(iblockstate, new PipeWorldWrapper(worldObj, pos, side), pos);
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			double d0 = i + this.rand.nextDouble() * (blockBounds.maxX - blockBounds.minX - f * 2.0F) + f + blockBounds.minX;
			double d1 = j + this.rand.nextDouble() * (blockBounds.maxY - blockBounds.minY - f * 2.0F) + f + blockBounds.minY;
			double d2 = k + this.rand.nextDouble() * (blockBounds.maxZ - blockBounds.minZ - f * 2.0F) + f + blockBounds.minZ;

			if (side == EnumFacing.DOWN)
			{
				d1 = j + blockBounds.minY - f;
			}

			if (side == EnumFacing.UP)
			{
				d1 = j + blockBounds.maxY + f;
			}

			if (side == EnumFacing.NORTH)
			{
				d2 = k + blockBounds.minZ - f;
			}

			if (side == EnumFacing.SOUTH)
			{
				d2 = k + blockBounds.maxZ + f;
			}

			if (side == EnumFacing.WEST)
			{
				d0 = i + blockBounds.minX - f;
			}

			if (side == EnumFacing.EAST)
			{
				d0 = i + blockBounds.maxX + f;
			}
			ParticleDigging fx = (ParticleDigging) Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1,
					d2, 0, 0, 0, 0);
			fx.init().multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
			fx.setParticleTexture(sprite);
			effectRenderer.addEffect(fx);
			return true;
		}
		return super.addHitEffects(state, worldObj, target, effectRenderer);
	}

	@SuppressWarnings("deprecation")
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		final TileEntityPipe te = (TileEntityPipe) world.getTileEntity(pos);
		if(te == null) return getSoundType();
		SoundType custom = new SoundType(getSoundType().getVolume(), getSoundType().getPitch(), null, null, null, null, null){
			public float getVolume()
		    {
		        return this.volume;
		    }

		    public float getPitch()
		    {
		        return this.pitch;
		    }

		    public SoundEvent getBreakSound()
		    {
		    	//TODO Figure out entity specific cover break sound
		    	return getSoundType().getBreakSound();
		    }

		    public SoundEvent getStepSound()
		    {
		    	CoverData data = te.getCoverData(EnumFacing.UP);
				if(data !=null && data.getBlockState() !=null){
					return data.getBlockState().getBlock().getSoundType(data.getBlockState(), new PipeWorldWrapper(world, pos, EnumFacing.UP), pos, entity).getStepSound();
				}
		        return getSoundType().getStepSound();
		    }

		    public SoundEvent getPlaceSound()
		    {
		        return getSoundType().getPlaceSound();
		    }

		    public SoundEvent getHitSound()
		    {
		    	if(entity instanceof EntityPlayer){
			    	RaytraceResult result = te.getClosest((EntityPlayer)entity);
					if(result !=null && result.component !=null){
						if(result.component.data !=null && result.component.data instanceof PipePart){
							PipePart part = (PipePart) result.component.data;
							if(part == PipePart.COVER){
								CoverData data = te.getCoverData(result.rayTraceResult.sideHit);
								if(data.getBlockState() !=null){
									return data.getBlockState().getBlock().getSoundType(data.getBlockState(), new PipeWorldWrapper(world, pos, result.rayTraceResult.sideHit), pos, entity).getHitSound();
								}
							}
						}
					}
		    	}
		        return getSoundType().getHitSound();
		    }

		    public SoundEvent getFallSound()
		    {
		    	CoverData data = te.getCoverData(EnumFacing.UP);
				if(data !=null && data.getBlockState() !=null){
					return data.getBlockState().getBlock().getSoundType(data.getBlockState(), new PipeWorldWrapper(world, pos, EnumFacing.UP), pos, entity).getFallSound();
				}
		        return getSoundType().getFallSound();
		    }
		};
        return custom;
    }
	
	@Override
	public boolean canRenderInLayer(IBlockState state, final BlockRenderLayer layer) {
		return true;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
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

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isTranslucent(IBlockState state) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntityPipe te = (TileEntityPipe) world.getTileEntity(pos);
		if (te == null) {
			return true;
		}

		boolean breakBlock = true;
		List<ItemStack> drop = new ArrayList<ItemStack>();
		BlockPos dropPos = pos;

		List<RaytraceResult> results = doRayTraceAll(world, pos.getX(), pos.getY(), pos.getZ(), player);
		RaytraceResult.sort(EntityUtil.getEyePosition(player), results);
		search : for(RaytraceResult closest : results){
			if (closest != null) {
				if (closest.component != null) {
					if (closest.component.dir != null) {
						EnumFacing dir = closest.component.dir;
						if (closest.component.data instanceof PipePart) {
							PipePart part = (PipePart) closest.component.data;
							if (part == PipePart.ATTACHMENT) {
								final AttachmentData data = te.getAttachmentData(dir);
								ItemStack attach = new ItemStack(ModItems.pipeAttachmant);
								ItemNBTHelper.setString(attach, "ID", data.getID());
								drop.add(attach);
								te.setAttachment(dir, null);
								breakBlock = false;
								dropPos = pos.offset(dir);
								break search;
							}
							if(part == PipePart.COVER){
								breakBlock = false;
								final CoverData coverData = te.getCoverData(dir);
								te.setCover(dir, null);
								ItemStack cover = ItemPipeCover.getCover(coverData);
								if (ItemStackTools.isValid(cover)) {
									drop.add(cover);
								}
								dropPos = pos.offset(dir);
								break search;
							}
						}
					} 
				}
			}
		}

		if (breakBlock) {
			breakConduit(te, player);
			drop.addAll(te.getDrops());
		}

		//breakBlock = te.getCovers().isEmpty();

		if (!breakBlock) {
			world.notifyBlockUpdate(pos, state, state, 3);
		}

		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			for (ItemStack st : drop) {
				ItemUtil.spawnItemInWorldWithoutMotion(world, st, dropPos);
			}
		}

		if (breakBlock) {
			world.setBlockToAir(pos);
			return true;
		}
		return false;
	}

	private boolean breakConduit(TileEntityPipe te, EntityPlayer player) {
		if(!te.getWorld().isRemote){
			te.onRemoved();
			te.markDirty();
		}
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileEntityPipe)) {
			return;
		}
		TileEntityPipe te = (TileEntityPipe) tile;

		/*for(ItemStack stack : te.getDrops()){
    	  ItemUtil.spawnItemInWorldWithRandomMotion(world, stack, pos);
      }*/

		te.onRemoved();
		te.markDirty();
		world.removeTileEntity(pos);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		worldIn.setBlockToAir(pos);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			ItemStack pick = ((TileEntityPipe)tile).getPickBlock(target, player);
			if(pick !=null){
				return pick;
			}
			else{
				return ((TileEntityPipe)tile).getPipeDropped();
			}
		}
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		/*List<ItemStack> ret = Lists.newArrayList();
        TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityPipe)) {
        	ret.add(((TileEntityPipe)tile).getPipeDropped());
        	return ret;
        }*/
		return super.getDrops(world, pos, state, fortune);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(worldIn, pos, state);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			((TileEntityPipe)tile).onAdded();
			((TileEntityPipe)tile).markDirty();
			BlockUtil.markBlockForUpdate(worldIn, pos);
		}
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = ((TileEntityPipe) tile);
			CoverData data = pipe.getCoverData(side);
			if(data !=null){
				if(data.getBlockState() !=null){
					return data.getBlockState().getBlock().isBlockSolid(new PipeBlockAccessWrapper(worldIn, pos, side), pos, side);
				}
			}
		}
		return super.isBlockSolid(worldIn, pos, side);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(worldIn, pos, state, rand);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = ((TileEntityPipe) tile);
			for(EnumFacing dir : EnumFacing.VALUES){
				final CoverData coverData = pipe.getCoverData(dir);
				if(coverData !=null){
					Block block = coverData.getBlockState().getBlock();
					if(block !=null){
						block.updateTick(new PipeWorldWrapper(worldIn, pos, dir), pos, coverData.getBlockState(), rand);
					}
				}
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null && pipe.getCoverData(side) !=null && pipe.getCoverData(side).getBlockState() !=null && pipe.getCoverData(side).getBlockState().getBlock() !=null){
				IBlockState cState = pipe.getCoverData(side).getBlockState();
				return cState.isSideSolid(new PipeBlockAccessWrapper(world, pos, side), pos, side);
			}
		}
		return super.isSideSolid(state, world, pos, side);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(pipe.getCoverData(EnumFacing.UP) !=null && pipe.getCoverData(EnumFacing.UP).getBlockState() !=null && pipe.getCoverData(EnumFacing.UP).getBlockState().getBlock() !=null){
				IBlockState cState = pipe.getCoverData(EnumFacing.UP).getBlockState();
				Block block = cState.getBlock();
				block.onEntityCollidedWithBlock(new PipeWorldWrapper(worldIn, pos, EnumFacing.UP), pos, cState, entityIn);
			}
		}
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData up = pipe.getCoverData(EnumFacing.UP);
			if(up !=null && up.getBlockState() !=null && up.getBlockState().getBlock() !=null){
				Block block = up.getBlockState().getBlock();
				block.onFallenUpon(new PipeWorldWrapper(worldIn, pos, EnumFacing.UP), pos, entityIn, fallDistance);
			}
		}
	}

	@Override
	public void onLanded(World worldIn, Entity entityIn)
	{
		BlockPos pos = new BlockPos(entityIn).down();
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData up = pipe.getCoverData(EnumFacing.UP);
			if(up !=null && up.getBlockState() !=null && up.getBlockState().getBlock() !=null){
				Block block = up.getBlockState().getBlock();
				block.onLanded(new PipeWorldWrapper(worldIn, pos, EnumFacing.UP), entityIn);
			}
		}
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData up = pipe.getCoverData(EnumFacing.UP);
			if(up !=null && up.getBlockState() !=null && up.getBlockState().getBlock() !=null){
				Block block = up.getBlockState().getBlock();
				block.onEntityWalk(new PipeWorldWrapper(worldIn, pos, EnumFacing.UP), pos, entityIn);
			}
		}
		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData up = pipe.getCoverData(EnumFacing.UP);
			if(up !=null && up.getBlockState() !=null && up.getBlockState().getBlock() !=null){
				Block block = up.getBlockState().getBlock();
				return block.canPlaceTorchOnTop(up.getBlockState(), new PipeBlockAccessWrapper(world, pos, EnumFacing.UP), pos);
			}
		}
		return super.canPlaceTorchOnTop(state, world, pos);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.EntityLiving.SpawnPlacementType type)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			CoverData up = pipe.getCoverData(EnumFacing.UP);
			if(up !=null && up.getBlockState() !=null && up.getBlockState().getBlock() !=null){
				Block block = up.getBlockState().getBlock();
				return block.canCreatureSpawn(up.getBlockState(), new PipeBlockAccessWrapper(world, pos, EnumFacing.UP), pos, type);
			}
		}
		return super.canCreatureSpawn(state, world, pos, type);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null){
				if(pipe.getAttachmentData(side.getOpposite()) !=null){
					AttachmentData attachment = pipe.getAttachmentData(side.getOpposite());
					if(attachment.canConnectRedstone(pipe, side.getOpposite())){
						return true;
					}
				}
				if(pipe.getCoverData(side.getOpposite()) !=null && pipe.getCoverData(side.getOpposite()).getBlockState() !=null && pipe.getCoverData(side.getOpposite()).getBlockState().getBlock() !=null){
					IBlockState cState = pipe.getCoverData(side.getOpposite()).getBlockState();
					Block block = cState.getBlock();
					return block.canConnectRedstone(cState, new PipeBlockAccessWrapper(world, pos, side.getOpposite()), pos, side);
				}
			}
		}
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		int ret = 0;
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			for(EnumFacing side : EnumFacing.VALUES){
				if(pipe.getCoverData(side) !=null && pipe.getCoverData(side).getBlockState() !=null && pipe.getCoverData(side).getBlockState().getBlock() !=null){
					IBlockState cState = pipe.getCoverData(side).getBlockState();
					ret+=cState.getLightValue(new PipeBlockAccessWrapper(world, pos, side), pos);
				}
			}
		}
		return Math.min(ret, 15);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, net.minecraftforge.common.IPlantable plantable)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(pipe.getCoverData(side) !=null && pipe.getCoverData(side).getBlockState() !=null && pipe.getCoverData(side).getBlockState().getBlock() !=null){
				IBlockState cState = pipe.getCoverData(side).getBlockState();
				Block block = cState.getBlock();
				return block.canSustainPlant(cState, new PipeBlockAccessWrapper(world, pos, side), pos, side, plantable);
			}
		}
		return super.canSustainPlant(state, world, pos, side, plantable);
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null && pipe.getCoverData(side.getOpposite()) !=null && pipe.getCoverData(side.getOpposite()).getBlockState() !=null && pipe.getCoverData(side.getOpposite()).getBlockState().getBlock() !=null){
				IBlockState cState = pipe.getCoverData(side.getOpposite()).getBlockState();
				Block block = cState.getBlock();
				return block.shouldCheckWeakPower(cState, new PipeBlockAccessWrapper(world, pos, side.getOpposite()), pos, side);
			}
		}
		return super.shouldCheckWeakPower(state, world, pos, side);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null){
				if(pipe.getAttachmentData(side.getOpposite()) !=null){
					AttachmentData attachment = pipe.getAttachmentData(side.getOpposite());
					int power = attachment.getRedstonePower(pipe, side.getOpposite(), true);
					if(power > 0){
						return power;
					}
				}
				if(pipe.getCoverData(side.getOpposite()) !=null && pipe.getCoverData(side.getOpposite()).getBlockState() !=null && pipe.getCoverData(side.getOpposite()).getBlockState().getBlock() !=null){
					IBlockState cState = pipe.getCoverData(side.getOpposite()).getBlockState();
					return cState.getStrongPower(new PipeBlockAccessWrapper(world, pos, side.getOpposite()), pos, side);
				}
			}
		}
		return super.getStrongPower(state, world, pos, side);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null){
				if(pipe.getAttachmentData(side.getOpposite()) !=null){
					AttachmentData attachment = pipe.getAttachmentData(side.getOpposite());
					int power = attachment.getRedstonePower(pipe, side.getOpposite(), false);
					if(power > 0){
						return power;
					}
				}
				if(pipe.getCoverData(side.getOpposite()) !=null && pipe.getCoverData(side.getOpposite()).getBlockState() !=null && pipe.getCoverData(side.getOpposite()).getBlockState().getBlock() !=null){
					IBlockState cState = pipe.getCoverData(side.getOpposite()).getBlockState();
					return cState.getWeakPower(new PipeBlockAccessWrapper(world, pos, side.getOpposite()), pos, side);
				}
			}
		}
		return super.getWeakPower(state, world, pos, side);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(face !=null && pipe.getCoverData(face) !=null){
				CoverData data = pipe.getCoverData(face);
				if(data.getBlockState() == null || data.getBlockState().getBlock() == null)return super.doesSideBlockRendering(state, world, pos, face);
				return data.getBlockState().doesSideBlockRendering(new PipeBlockAccessWrapper(world, pos, face), pos, face);
			}
		}
		return super.doesSideBlockRendering(state, world, pos, face);
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(side !=null && pipe.getCoverData(side) !=null){
				CoverData data = pipe.getCoverData(side);
				if(data.getBlockState() == null || data.getBlockState().getBlock() == null)return super.isFireSource(world, pos, side);
				return data.getBlockState().getBlock().isFireSource(new PipeWorldWrapper(world, pos, side), pos, side);
			}
		}
		return super.isFireSource(world, pos, side);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(face !=null && pipe.getCoverData(face) !=null){
				CoverData data = pipe.getCoverData(face);
				if(data.getBlockState() == null || data.getBlockState().getBlock() == null)return super.getFlammability(world, pos, face);
				return data.getBlockState().getBlock().getFlammability(new PipeBlockAccessWrapper(world, pos, face), pos, face);
			}
		}
		return super.getFlammability(world, pos, face);
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(face !=null && pipe.getCoverData(face) !=null){
				CoverData data = pipe.getCoverData(face);
				if(data.getBlockState() == null || data.getBlockState().getBlock() == null)return super.isFlammable(world, pos, face);
				return data.getBlockState().getBlock().isFlammable(new PipeBlockAccessWrapper(world, pos, face), pos, face);
			}
		}
		return super.isFlammable(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = (TileEntityPipe) tile;
			if(face !=null && pipe.getCoverData(face) !=null){
				CoverData data = pipe.getCoverData(face);
				if(data.getBlockState() == null || data.getBlockState().getBlock() == null)return super.getFireSpreadSpeed(world, pos, face);
				return data.getBlockState().getBlock().getFireSpreadSpeed(new PipeBlockAccessWrapper(world, pos, face), pos, face);
			}
		}
		return super.getFireSpreadSpeed(world, pos, face);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			((TileEntityPipe)tile).onAdded();
			((TileEntityPipe)tile).markDirty();
			BlockUtil.markBlockForUpdate(worldIn, pos);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity conduit = world.getTileEntity(pos);
		if (conduit instanceof TileEntityPipe) {

			if(((TileEntityPipe) conduit).onNeighborBlockChange(null)){
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos otherPos) {
		super.neighborChanged(state, world, pos, neighborBlock, otherPos);
		TileEntity tile = world.getTileEntity(pos);
		if (tile !=null && (tile instanceof TileEntityPipe)) {
			TileEntityPipe pipe = ((TileEntityPipe) tile);
			pipe.onNeighborBlockChange(neighborBlock);
			for(EnumFacing dir : EnumFacing.VALUES){
				final CoverData coverData = pipe.getCoverData(dir);
				if(coverData !=null){
					IBlockState cState = coverData.getBlockState();
					Block block = cState.getBlock();
					if(block !=null){
						IBlockState realState = cState.getActualState(new PipeBlockAccessWrapper(world, pos, dir), pos);
						realState.neighborChanged(new PipeWorldWrapper(world, pos, dir), pos, neighborBlock, otherPos);
					}
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if ((tile instanceof TileEntityPipe)) {
			if(((TileEntityPipe)tile).onActivated(world, player, hand, player.getHeldItem(hand), side, new Vec3d(hitX, hitY, hitZ))){
				tile.markDirty();
				return true;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if (!(te instanceof TileEntityPipe)) {
			return null;
		}
		AxisAlignedBB minBB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);



		List<RaytraceResult> results = doRayTraceAll(world, pos.getX(), pos.getY(), pos.getZ(), player);
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
		}else{
			minBB = new AxisAlignedBB(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		}
		return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY() + minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX, pos.getY() + minBB.maxY,
				pos.getZ() + minBB.maxZ);
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
		TileEntityPipe te = (TileEntityPipe) worldIn.getTileEntity(pos);
		if (te != null) {
			RaytraceResult result = te.getClosest(playerIn);
			if(result !=null && result.component !=null){
				if(result.component.data !=null && result.component.data instanceof PipePart){
					PipePart part = (PipePart) result.component.data;
					if(part == PipePart.COVER){
						EnumFacing dir = result.component.dir;
						CoverData data = te.getCoverData(dir);
						if(data !=null && data.getBlockState() !=null && data.getBlockState().getBlock() !=null){
							return data.getBlockState().getPlayerRelativeBlockHardness(playerIn, new PipeWorldWrapper(worldIn, pos, dir), pos);
						}
					}
				}
			}
		}
		return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos);
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn){
		super.onBlockClicked(worldIn, pos, playerIn);
		TileEntityPipe te = (TileEntityPipe) worldIn.getTileEntity(pos);
		if (te != null) {
			RaytraceResult result = te.getClosest(playerIn);
			if(result !=null && result.component !=null){
				if(result.component.data !=null && result.component.data instanceof PipePart){
					PipePart part = (PipePart) result.component.data;
					if(part == PipePart.COVER){
						EnumFacing dir = result.component.dir;
						CoverData data = te.getCoverData(dir);
						if(data !=null && data.getBlockState() !=null && data.getBlockState().getBlock() !=null){
							data.getBlockState().getBlock().onBlockClicked(new PipeWorldWrapper(worldIn, pos, dir), pos, playerIn);
						}
					}
				}
			}
		}
	}

	public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityLivingBase entity) {
		List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, entity);
		if (allHits == null) {
			return null;
		}
		Vec3d origin = EntityUtil.getEyePosition(entity);
		return RaytraceResult.getClosestHit(origin, allHits);
	}

	public List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, EntityLivingBase entity) {
		double pitch = Math.toRadians(entity.rotationPitch);
		double yaw = Math.toRadians(entity.rotationYaw);

		double dirX = -Math.sin(yaw) * Math.cos(pitch);
		double dirY = -Math.sin(pitch);
		double dirZ = Math.cos(yaw) * Math.cos(pitch);

		double reachDistance = EntityUtil.getReachDistance(entity);

		Vec3d origin = EntityUtil.getEyePosition(entity);
		Vec3d direction = origin.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);
		return doRayTraceAll(world, x, y, z, origin, direction, entity);
	}

	private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3d origin, Vec3d direction, EntityLivingBase entity) {
		List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, origin, direction, entity);
		if (allHits == null) {
			return null;
		}
		return RaytraceResult.getClosestHit(origin, allHits);
	}

	protected List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, Vec3d origin, Vec3d direction, EntityLivingBase entity) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityPipe)) {
			return null;
		}
		TileEntityPipe bundle = (TileEntityPipe) te;
		List<RaytraceResult> hits = new ArrayList<RaytraceResult>();

		if (entity == null) {
			entity = CrystalMod.proxy.getClientPlayer();
		}

		Collection<CollidableComponent> components = new ArrayList<CollidableComponent>(bundle.getCollidableComponents());
		for (CollidableComponent component : components) {
			setBlockBounds((float)component.bound.minX, (float)component.bound.minY, (float)component.bound.minZ, (float)component.bound.maxX, (float)component.bound.maxY, (float)component.bound.maxZ);
			@SuppressWarnings("deprecation")
			RayTraceResult hitPos = super.collisionRayTrace(world.getBlockState(pos), world, pos, origin, direction);
			if (hitPos != null) {
				hits.add(new RaytraceResult(component, hitPos));
			}
		}

		setBlockBounds(0, 0, 0, 1, 1, 1);

		return hits;
	}

	private AxisAlignedBB bounds;
	private void setBlockBounds(double f, double g, double h, double i, double j, double k) {
		bounds = new AxisAlignedBB(f, g, h, i, j, k);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bounds;
	}
}
