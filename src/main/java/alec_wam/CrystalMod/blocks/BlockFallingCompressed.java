package alec_wam.CrystalMod.blocks;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFallingCompressed extends BlockFalling implements ICustomModel {

	public static final PropertyEnum<FallingCompressedBlockType> TYPE = PropertyEnum.<FallingCompressedBlockType>create("type", FallingCompressedBlockType.class);
	
	public BlockFallingCompressed() {
		super(Material.SAND);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setHardness(0.5f);
		this.setHarvestLevel("shovel", 0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, FallingCompressedBlockType.GUNPOWDER));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for(FallingCompressedBlockType type : FallingCompressedBlockType.values()) {
			list.add(new ItemStack(this, 1, type.getMeta()));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	protected FallingCompressedBlockType fromMeta(int meta) {
		if(meta < 0 || meta >= FallingCompressedBlockType.values().length) {
			meta = 0;
		}

		return FallingCompressedBlockType.values()[meta];
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(FallingCompressedBlockType type : FallingCompressedBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
	@Override
 	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack held = playerIn.getHeldItem(hand);
		FallingCompressedBlockType type = state.getValue(TYPE);
		if(type == FallingCompressedBlockType.GUNPOWDER){			
			if(ItemStackTools.isValid(held) && held.getItem() == Items.FLINT_AND_STEEL){
				for(int i = 0; i < 8; i++){
					if(i == 0){
						IBlockState left = worldIn.getBlockState(pos.west());
						IBlockState leftSouth = worldIn.getBlockState(pos.west().south());
						IBlockState south = worldIn.getBlockState(pos.south());
						IBlockState down = worldIn.getBlockState(pos.down());
						IBlockState leftD = worldIn.getBlockState(pos.down().west());
						IBlockState leftDSouth = worldIn.getBlockState(pos.down().west().south());
						IBlockState southD = worldIn.getBlockState(pos.down().south());
						if(left.getBlock() == this && left.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(leftSouth.getBlock() == this && leftSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(leftD.getBlock() == this && leftD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(leftDSouth.getBlock() == this && leftDSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(southD.getBlock() == this && southD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() - 1, pos.getY() - 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 1){
						IBlockState right = worldIn.getBlockState(pos.east());
						IBlockState rightSouth = worldIn.getBlockState(pos.east().south());
						IBlockState south = worldIn.getBlockState(pos.south());
						IBlockState down = worldIn.getBlockState(pos.down());
						IBlockState rightD = worldIn.getBlockState(pos.down().east());
						IBlockState rightDSouth = worldIn.getBlockState(pos.down().east().south());
						IBlockState southD = worldIn.getBlockState(pos.down().south());
						if(right.getBlock() == this && right.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(rightSouth.getBlock() == this && rightSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(rightD.getBlock() == this && rightD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(rightDSouth.getBlock() == this && rightDSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(southD.getBlock() == this && southD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() - 1, pos.getY() - 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 2){
						IBlockState left = worldIn.getBlockState(pos.west());
						IBlockState leftNorth = worldIn.getBlockState(pos.west().north());
						IBlockState north = worldIn.getBlockState(pos.north());
						IBlockState down = worldIn.getBlockState(pos.down());
						IBlockState leftD = worldIn.getBlockState(pos.down().west());
						IBlockState leftDNorth = worldIn.getBlockState(pos.down().west().north());
						IBlockState northD = worldIn.getBlockState(pos.down().north());
						if(left.getBlock() == this && left.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(leftNorth.getBlock() == this && leftNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(leftD.getBlock() == this && leftD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(leftDNorth.getBlock() == this && leftDNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(northD.getBlock() == this && northD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 3){
						IBlockState right = worldIn.getBlockState(pos.east());
						IBlockState rightNorth = worldIn.getBlockState(pos.east().north());
						IBlockState north = worldIn.getBlockState(pos.north());
						IBlockState down = worldIn.getBlockState(pos.down());
						IBlockState rightD = worldIn.getBlockState(pos.down().east());
						IBlockState rightDNorth = worldIn.getBlockState(pos.down().east().north());
						IBlockState northD = worldIn.getBlockState(pos.down().north());
						if(right.getBlock() == this && right.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(rightNorth.getBlock() == this && rightNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(rightD.getBlock() == this && rightD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(rightDNorth.getBlock() == this && rightDNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(northD.getBlock() == this && northD.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() + 1, pos.getY() - 1, pos.getZ() - 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}

					//UP
					if(i == 4){
						IBlockState left = worldIn.getBlockState(pos.west());
						IBlockState leftSouth = worldIn.getBlockState(pos.west().south());
						IBlockState south = worldIn.getBlockState(pos.south());
						IBlockState up = worldIn.getBlockState(pos.up());
						IBlockState leftU = worldIn.getBlockState(pos.up().west());
						IBlockState leftUSouth = worldIn.getBlockState(pos.up().west().south());
						IBlockState southU = worldIn.getBlockState(pos.up().south());
						if(left.getBlock() == this && left.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(leftSouth.getBlock() == this && leftSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(leftU.getBlock() == this && leftU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(leftUSouth.getBlock() == this && leftUSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(southU.getBlock() == this && southU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() - 1, pos.getY() + 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 5){
						IBlockState right = worldIn.getBlockState(pos.east());
						IBlockState rightSouth = worldIn.getBlockState(pos.east().south());
						IBlockState south = worldIn.getBlockState(pos.south());
						IBlockState up = worldIn.getBlockState(pos.up());
						IBlockState rightU = worldIn.getBlockState(pos.up().east());
						IBlockState rightUSouth = worldIn.getBlockState(pos.up().east().south());
						IBlockState southU = worldIn.getBlockState(pos.up().south());
						if(right.getBlock() == this && right.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(rightSouth.getBlock() == this && rightSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(rightU.getBlock() == this && rightU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(rightUSouth.getBlock() == this && rightUSouth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(southU.getBlock() == this && southU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() - 1, pos.getY() + 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 6){
						IBlockState left = worldIn.getBlockState(pos.west());
						IBlockState leftNorth = worldIn.getBlockState(pos.west().north());
						IBlockState north = worldIn.getBlockState(pos.north());
						IBlockState up = worldIn.getBlockState(pos.up());
						IBlockState leftU = worldIn.getBlockState(pos.up().west());
						IBlockState leftUNorth = worldIn.getBlockState(pos.up().west().north());
						IBlockState northU = worldIn.getBlockState(pos.up().north());
						if(left.getBlock() == this && left.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(leftNorth.getBlock() == this && leftNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(leftU.getBlock() == this && leftU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(leftUNorth.getBlock() == this && leftUNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(northU.getBlock() == this && northU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
					if(i == 7){
						IBlockState right = worldIn.getBlockState(pos.east());
						IBlockState rightNorth = worldIn.getBlockState(pos.east().north());
						IBlockState north = worldIn.getBlockState(pos.north());
						IBlockState up = worldIn.getBlockState(pos.up());
						IBlockState rightU = worldIn.getBlockState(pos.up().east());
						IBlockState rightUNorth = worldIn.getBlockState(pos.up().east().north());
						IBlockState northU = worldIn.getBlockState(pos.up().north());
						if(right.getBlock() == this && right.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
							if(rightNorth.getBlock() == this && rightNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
										if(rightU.getBlock() == this && rightU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
											if(rightUNorth.getBlock() == this && rightUNorth.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
												if(northU.getBlock() == this && northU.getValue(TYPE) == FallingCompressedBlockType.GUNPOWDER){
													worldIn.createExplosion(playerIn, pos.getX() + 1, pos.getY() + 1, pos.getZ() - 1, 5.0f, true);
													if(!playerIn.capabilities.isCreativeMode){
														held.damageItem(1, playerIn);
													}
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}			
		}
        return false;
    }
	
	@Override
	public Material getMaterial(IBlockState state){
		return Material.SAND;
	}
    
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		return 0.5f;
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		return SoundType.SAND;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		FallingCompressedBlockType type = state.getValue(TYPE);
		if(type == FallingCompressedBlockType.GUNPOWDER){
			return 60;
		}
        return net.minecraft.init.Blocks.FIRE.getFlammability(this);
    }

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return getFlammability(world, pos, face) > 0;
    }

	@SuppressWarnings("deprecation")
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		FallingCompressedBlockType type = state.getValue(TYPE);
		if(type == FallingCompressedBlockType.GUNPOWDER){
			return 30;
		}
        return net.minecraft.init.Blocks.FIRE.getEncouragement(this);
    }
	
    public static enum FallingCompressedBlockType implements IStringSerializable, IEnumMeta {
		GUNPOWDER("gunpowder"),
		SUGAR("sugar");

		private final String unlocalizedName;
		public final int meta;

		FallingCompressedBlockType(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }
    	
    }

}
