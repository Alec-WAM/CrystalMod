package alec_wam.CrystalMod.blocks;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCompressed extends EnumBlock<BlockCompressed.CompressedBlockType> {

	public static final PropertyEnum<CompressedBlockType> TYPE = PropertyEnum.<CompressedBlockType>create("type", CompressedBlockType.class);
	
	public BlockCompressed() {
		super(Material.ROCK, TYPE, CompressedBlockType.class);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, CompressedBlockType.FLINT));
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(CompressedBlockType type : CompressedBlockType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
	
	@Override
 	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack held = playerIn.getHeldItem(hand);
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){			
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
						if(left.getBlock() == this && left.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(leftSouth.getBlock() == this && leftSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(leftD.getBlock() == this && leftD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(leftDSouth.getBlock() == this && leftDSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(southD.getBlock() == this && southD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(right.getBlock() == this && right.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(rightSouth.getBlock() == this && rightSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(rightD.getBlock() == this && rightD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(rightDSouth.getBlock() == this && rightDSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(southD.getBlock() == this && southD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(left.getBlock() == this && left.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(leftNorth.getBlock() == this && leftNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(leftD.getBlock() == this && leftD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(leftDNorth.getBlock() == this && leftDNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(northD.getBlock() == this && northD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(right.getBlock() == this && right.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(rightNorth.getBlock() == this && rightNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(down.getBlock() == this && down.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(rightD.getBlock() == this && rightD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(rightDNorth.getBlock() == this && rightDNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(northD.getBlock() == this && northD.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(left.getBlock() == this && left.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(leftSouth.getBlock() == this && leftSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(leftU.getBlock() == this && leftU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(leftUSouth.getBlock() == this && leftUSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(southU.getBlock() == this && southU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(right.getBlock() == this && right.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(rightSouth.getBlock() == this && rightSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(south.getBlock() == this && south.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(rightU.getBlock() == this && rightU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(rightUSouth.getBlock() == this && rightUSouth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(southU.getBlock() == this && southU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(left.getBlock() == this && left.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(leftNorth.getBlock() == this && leftNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(leftU.getBlock() == this && leftU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(leftUNorth.getBlock() == this && leftUNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(northU.getBlock() == this && northU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
						if(right.getBlock() == this && right.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
							if(rightNorth.getBlock() == this && rightNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
								if(north.getBlock() == this && north.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
									if(up.getBlock() == this && up.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
										if(rightU.getBlock() == this && rightU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
											if(rightUNorth.getBlock() == this && rightUNorth.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
												if(northU.getBlock() == this && northU.getValue(TYPE) == CompressedBlockType.GUNPOWDER){
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
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return Material.SAND;
		}
		if(type == CompressedBlockType.FLINT){
			return Material.ROCK;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return Material.ROCK;
		}
		return super.getMaterial(state);
	}
    
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
		CompressedBlockType type = blockState.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return 0.5f;
		}
		if(type == CompressedBlockType.FLINT){
			return 4.0F;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return 5.0F;
		}
		return super.getBlockHardness(blockState, worldIn, pos);
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
		IBlockState state = world.getBlockState(pos);
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.FLINT){
			return 6.0F;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return 6.0F;
		}
		return super.getExplosionResistance(world, pos, exploder, explosion);
    }
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
    {
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return SoundType.SAND;
		}
		if(type == CompressedBlockType.FLINT){
			return SoundType.STONE;
		}
		if(type == CompressedBlockType.CHARCOAL){
			return SoundType.STONE;
		}
		return super.getSoundType(state, world, pos, entity);
    }
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return 60;
		}
        return net.minecraft.init.Blocks.FIRE.getFlammability(this);
    }

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return getFlammability(world, pos, face) > 0;
    }

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
		IBlockState state = world.getBlockState(pos);
		CompressedBlockType type = state.getValue(TYPE);
		if(type == CompressedBlockType.GUNPOWDER){
			return 30;
		}
        return net.minecraft.init.Blocks.FIRE.getEncouragement(this);
    }
	
    public static enum CompressedBlockType implements IStringSerializable, IEnumMeta {
		FLINT("flint"),
		GUNPOWDER("gunpowder"),
		CHARCOAL("charcoal");

		private final String unlocalizedName;
		public final int meta;

		CompressedBlockType(String name) {
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
