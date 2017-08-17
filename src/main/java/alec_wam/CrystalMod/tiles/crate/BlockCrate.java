package alec_wam.CrystalMod.tiles.crate;

import java.util.Locale;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrate extends EnumBlock<BlockCrate.CrateType> implements ICustomModel, ITileEntityProvider {

	public static enum CrateType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK;

		final int meta;
		
		CrateType(){
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

		public static CrateType byMetadata(int meta) {
			return values()[meta % values().length];
		}
		
	}
	
	/**Click Times**/
	public static final int STARTING_TIME = 20,	CONTINOUS_TIME = 5,	DOUBLE_CLICK_TIME = 10,	FILL_TIME = 20,	DELAY_PICKUP_TIME = 20,	EMPLY_CRATE_TIME = 5;
    public static final PropertyEnum<CrateType> TYPE = PropertyEnum.<CrateType>create("type", CrateType.class);

	public BlockCrate() {
		super(Material.WOOD, TYPE, CrateType.class);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
    
	@Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TYPE, BlockStateFacing.facingProperty);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void initModel(){
    	ModelLoader.setCustomStateMapper(this, new CustomBlockStateMapper());
		for(CrateType type : CrateType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCrate(meta);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileCrate){
			TileCrate crate = (TileCrate)tile;
			if(player.isSneaking() && crate.getLastClicked() == CONTINOUS_TIME){
				crate.setLastClicked(CONTINOUS_TIME + 1);

				ItemStack stored = crate.getStack();
				if(ItemStackTools.isValid(stored)){
					ItemStack item2 = ItemStackTools.getEmptyStack();
					if (ItemStackTools.getStackSize(stored) > 1){
						item2 = ItemUtil.copy(stored, ItemStackTools.getStackSize(stored)-1);
					}
					ItemStackTools.setStackSize(stored, 1);

					if (ItemStackTools.isEmpty(spawnItem(player, stored))){
						crate.setStack(item2);
					}
					return 0.0f;
				}
			}
		}
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof TileCrate){
			TileCrate crate = (TileCrate)tile;
			crate.resetLastClicked();
		
			if (crate.getLastClicked() > 0){
				return -1;
			}
		}
		
		return super.getBlockHardness(state, world, pos);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(worldIn.isRemote)return true;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileCrate){
			TileCrate crate = (TileCrate)tile;
			ItemStack playerItem = player.getHeldItem(hand);
			ItemStack stored = crate.getStack();
			boolean changed = false;
			boolean isFront = Config.crates_useAllSides || side == crate.facing;
			if(crate.isTimerActive()){
				crate.resetTimer();
				if(ItemStackTools.isValid(stored)){
					if(crate.getMode() == 0 && ItemStackTools.isValid(playerItem) && playerItem.getItem() == Item.getItemFromBlock(this) && playerItem.getMetadata() > crate.getBlockMetadata()){
						playerItem = playerItem.copy();
						if(!player.capabilities.isCreativeMode && ItemStackTools.isValid(spawnItem(player, new ItemStack(this, 1, crate.getBlockMetadata())))){
							playerItem = player.getHeldItem(hand);
						} else {
							final ItemStack oldStored = crate.getStack();
							final EnumFacing oldFacing = crate.facing;
							IBlockState newState = getStateFromMeta(playerItem.getMetadata());
							worldIn.setBlockState(pos, newState, 1);
							SoundType soundtype = getSoundType(newState, worldIn, pos, player);
		                    worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
							TileEntity newTile = worldIn.getTileEntity(pos);
							if(newTile !=null && newTile instanceof TileCrate){
								((TileCrate)newTile).setStack(oldStored);
								((TileCrate)newTile).facing = oldFacing;
							}
							BlockUtil.markBlockForUpdate(worldIn, pos);
							if(!player.capabilities.isCreativeMode){
								playerItem = ItemUtil.consumeItem(playerItem);
								player.setHeldItem(hand, playerItem);
							}
							return true;
						}
					} else if(crate.getMode() == 1 && isFront && (ItemStackTools.isEmpty(playerItem) || ItemUtil.canCombine(playerItem, stored))){
						for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
							ItemStack original = player.inventory.getStackInSlot(slot);
							ItemStack newItem = crate.addItem(original);
							if(original !=newItem){
								changed = true;
							}
							player.inventory.setInventorySlotContents(slot, newItem);
						}
					} else if(ItemStackTools.isValid(playerItem) && isFront){
						if (playerItem.getItem() == Item.getItemFromBlock(this) && playerItem.getMetadata() > crate.getBlockMetadata()) {
							crate.setClick(0, 0, DOUBLE_CLICK_TIME);
							
							playerItem = crate.addItem(playerItem);
							changed = true;
						} else if(ItemStackTools.isEmpty(stored) || ItemUtil.canCombine(playerItem, stored)){
							crate.setClick(0, 1, DOUBLE_CLICK_TIME);
							
							playerItem = crate.addItem(playerItem);
							changed = true;
						}
					}
				}
			} else if(ItemStackTools.isValid(playerItem) && isFront){
				crate.setClick(0, 1, DOUBLE_CLICK_TIME);
				
				playerItem = crate.addItem(playerItem);
				changed = true;
			}
			
			if(changed){
				player.setHeldItem(hand, playerItem);
				return true;
			}
		}
        return super.onBlockActivated(worldIn, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileCrate){
			TileCrate crate = (TileCrate)tile;
			if(crate.isAbrupted()){
				ItemStack stored = crate.getStack();
				ItemStack held = player.getHeldItemMainhand();
				RayTraceResult ray = ToolUtil.rayTrace(world, player, false);
				boolean inFront = Config.crates_useAllSides || ray.sideHit == crate.facing;
				if(ItemStackTools.isValid(stored) && inFront){
					if (crate.isTimerActive() && crate.getSelectedSlot() != player.inventory.currentItem && ItemStackTools.isEmpty(held) && crate.getMode() == 2 && !player.isSneaking()){
						crate.resetTimer();
					}else{
						if (ItemStackTools.isEmpty(held) && !player.isSneaking()){
							crate.setClick(player.inventory.currentItem, 2, FILL_TIME);
						}else{
							crate.resetTimer();
						}
						
						if (player.isSneaking()) {
							ItemStack item2 = stored.copy();
							ItemStackTools.incStackSize(item2, -1);
							ItemStackTools.setStackSize(stored, 1);
							dropItemInWorld(crate, player, stored, crate.getWorld().rand.nextFloat());
							stored = item2;
							if (ItemStackTools.isEmpty(stored)){
								crate.setStack(ItemStackTools.getEmptyStack());
							}else{
								crate.setStack(stored);
							}
						}else{
							int size = ItemStackTools.getStackSize(stored);
							if(Config.crates_leaveOneItem && size == 0 && !ItemStackTools.isEmpty(stored))size++;
							int maxStackSize = Math.min(size, stored.getMaxStackSize());
							ItemStack item2 = stored.copy();
							ItemStackTools.incStackSize(item2, -maxStackSize);
							ItemStackTools.setStackSize(stored, maxStackSize);
							
							dropItemInWorld(crate, player, stored, crate.getWorld().rand.nextFloat());
							stored = item2;
							if (ItemStackTools.isEmpty(stored)){
								crate.setStack(ItemStackTools.getEmptyStack());
							}else{
								crate.setStack(stored);
							}
						}
						if (ItemStackTools.isEmpty(crate.getStack())){
							crate.resetTimer();
						}
						crate.setLastClicked(STARTING_TIME);
					}
				}else{
					crate.resetTimer();
					crate.setLastClicked(EMPLY_CRATE_TIME);
				}
			}
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity == null || !(entity instanceof TileCrate)) return;
		TileCrate crate = (TileCrate) entity;
		
		if (!world.isRemote){
			ItemUtil.spawnItemInWorldWithoutMotion(world, crate.getStack(), pos);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile !=null && tile instanceof IFacingTile){
        	EnumFacing face = BlockMachine.getFacingFromEntity(pos, placer, true);
        	((IFacingTile)tile).setFacing(face.getIndex());
        	BlockUtil.markBlockForUpdate(world, pos);
        }
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
		TileEntity te = world.getTileEntity(pos);
        if(te !=null && te instanceof IFacingTile){
        	IFacingTile tile = (IFacingTile)te;
        	int next = tile.getFacing();
        	next++;
        	next%=6;
        	tile.setFacing(next);
        	BlockUtil.markBlockForUpdate(world, pos);
        	return true;
        }
        return false;
    }
    
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos)
    {
    	return EnumFacing.VALUES;
    }

	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		int powerInput = 0;
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof TileCrate){
			TileCrate crate = (TileCrate)tile;
			if(ItemStackTools.isValid(crate.getStack())){
				ItemStack stored = crate.getStack();
				int result = (int) Math.round((double) ItemStackTools.getStackSize(stored) / (double) crate.getCrateSize() * 15.0D);
				
				if (result == 0) result = 1;
				if (result == 15 && ItemStackTools.getStackSize(stored) != crate.getCrateSize()) result = 14;
				
				return Math.max(powerInput, result);
			}
		}
		
		return powerInput;
	}
	
	public ItemStack spawnItem(EntityPlayer player, ItemStack item){
		if (player.getEntityWorld().isRemote) return item;
		
		int currentSlot = player.inventory.currentItem;
		int inventorySize = player.inventory.mainInventory.size();
		int maxStackSize = Math.min(player.inventory.getInventoryStackLimit(), item.getMaxStackSize());
		
		int pass = 0;
		int slot = 0;
		
		while (ItemStackTools.isValid(item)) {
			if (pass == 0){
				slot = currentSlot;
			}else if (slot == currentSlot){
				slot++;
			}
			
			ItemStack playerItem = player.inventory.getStackInSlot(slot);
			
			if ((pass == 0 || pass == 2) && (ItemStackTools.isEmpty(playerItem))){
				playerItem = item.copy();
				
				if (ItemStackTools.getStackSize(item) > maxStackSize){
					ItemStackTools.setStackSize(playerItem, maxStackSize);
					ItemStackTools.incStackSize(item, -maxStackSize);
				}else{
					item = ItemStackTools.getEmptyStack();
				}
				
				playerItem.setAnimationsToGo(5);
			}else if ((pass == 0 || pass == 1) && ItemUtil.canCombine(item, playerItem) && ItemStackTools.getStackSize(playerItem) < maxStackSize){
				if (ItemStackTools.getStackSize(item) + ItemStackTools.getStackSize(playerItem) > maxStackSize){
					int len = Math.min(ItemStackTools.getStackSize(item), maxStackSize - ItemStackTools.getStackSize(playerItem));
					ItemStackTools.incStackSize(playerItem, len);
					ItemStackTools.incStackSize(item, -len);
				}else{
					if(ItemStackTools.isValid(playerItem)){
						ItemStackTools.incStackSize(playerItem, ItemStackTools.getStackSize(item));
					} else {
						playerItem = item;
					}
					item = ItemStackTools.getEmptyStack();
				}
				
				playerItem.setAnimationsToGo(5);
			}
			
			player.inventory.mainInventory.set(slot, playerItem);
			
			if (pass == 0){
				pass = 1;
				slot = 0;
				continue;
			}
			
			if (slot >= inventorySize - 1){
				if (pass > 2){
					break;
				}else{
					slot = -1;
				}
				pass++;
			}
			slot++;
		}
		
		return item;
	}
	
	public static void dropItemInWorld(TileCrate source, EntityPlayer player, ItemStack stack, double speedfactor)
	{
		EnumFacing facing = EnumFacing.getFront(source.getFacing());
		double stackCoordX = source.getPos().getX() + 0.5 + (facing.getFrontOffsetX() * 0.6);
		double stackCoordY = source.getPos().getY() + 0.5 + (facing.getFrontOffsetY() * 0.6);
		double stackCoordZ = source.getPos().getZ() + 0.5 + (facing.getFrontOffsetZ() * 0.6);
		EntityItem droppedEntity = new EntityItem(source.getWorld(), stackCoordX, stackCoordY, stackCoordZ, stack);
		
		Vec3d motion = new Vec3d(facing.getFrontOffsetX() * speedfactor, facing.getFrontOffsetY() * speedfactor, facing.getFrontOffsetZ() * speedfactor);
		motion.normalize();
		droppedEntity.motionX = motion.xCoord;
		droppedEntity.motionY = motion.yCoord;
		droppedEntity.motionZ = motion.zCoord;
		double offset = 0.25D;
		droppedEntity.setVelocity(motion.xCoord * offset, motion.yCoord * offset, motion.zCoord * offset);
		
		/*if (player != null)
		{
			Vec3d motion = new Vec3d(player.posX - stackCoordX, player.posY - stackCoordY, player.posZ - stackCoordZ);
			motion.normalize();
			droppedEntity.motionX = motion.xCoord;
			droppedEntity.motionY = motion.yCoord;
			droppedEntity.motionZ = motion.zCoord;
			double offset = 0.25D;
			droppedEntity.setVelocity(motion.xCoord * offset, motion.yCoord * offset, motion.zCoord * offset);
		}*/
		//droppedEntity.motionX *= speedfactor;
		//droppedEntity.motionY *= speedfactor;
		//droppedEntity.motionZ *= speedfactor;

		if(!source.getWorld().isRemote)source.getWorld().spawnEntity(droppedEntity);
	}
    
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null) {
        	if(te instanceof IFacingTile){
        		int facing = ((IFacingTile)te).getFacing();
        		face = EnumFacing.getFront(facing);
        	}
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }
	
	public static class CustomBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			CrateType type = state.getValue(TYPE);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
}
