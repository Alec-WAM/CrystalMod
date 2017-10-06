package alec_wam.CrystalMod.tiles.machine.dna;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.entities.disguise.DisguiseHandler;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDNA extends Item implements ICustomModel {

	public ItemDNA(){
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "dnaitem");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(DNAItemType type : DNAItemType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + DNAItemType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < DNAItemType.values().length; ++i)
        {
        	if(i == DNAItemType.SAMPLE_FULL.getMetadata() || i == DNAItemType.FILLED_SYRINGE.getMetadata()){
        		for(UUID uuid : new UUID[]{PlayerUtil.Alec_WAM, PlayerUtil.AH9902}){
        			ItemStack stack = new ItemStack(itemIn, 1, i);
        			PlayerDNA.savePlayerDNA(stack, uuid);
                    subItems.add(stack);
        		}
        	}else {
                subItems.add(new ItemStack(itemIn, 1, i));
        	}
        }
    }

	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return stack.getMetadata() == DNAItemType.CURE.getMetadata();
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack)
    {
		if(stack.getMetadata() == DNAItemType.EMPTY_SYRINGE.getMetadata() || stack.getMetadata() == DNAItemType.FILLED_SYRINGE.getMetadata()){
			return 16;
		}
		if(stack.getMetadata() == DNAItemType.SAMPLE_EMPTY.getMetadata()){
			return 64;
		}
		if(stack.getMetadata() == DNAItemType.SAMPLE_FULL.getMetadata()){
			return 1;
		}
		return super.getItemStackLimit(stack);
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advTooltips)
	{
		super.addInformation(stack, player, list, advTooltips);
		if(stack.getMetadata() == DNAItemType.SAMPLE_FULL.getMetadata() || stack.getMetadata() == DNAItemType.FILLED_SYRINGE.getMetadata()){
			if(PlayerDNA.loadPlayerDNA(stack) !=null){
				UUID dna = PlayerDNA.loadPlayerDNA(stack);
				String username = ProfileUtil.getUsername(dna);
				if(username !=ProfileUtil.ERROR){
					list.add(username);
				} else {
					list.add(UUIDUtils.fromUUID(dna));
				}
			}
		}
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = player.getHeldItem(hand);
		//Grab DNA from a bed
		UUID bedDNA = collectDNAFromBed(world, pos, player, stack);
		if(bedDNA !=null){
			ItemStack sample = new ItemStack(this, 1, DNAItemType.SAMPLE_FULL.getMetadata());
			PlayerDNA.savePlayerDNA(sample, bedDNA);
			if(ItemStackTools.getStackSize(stack) == 1){
				player.setHeldItem(hand, sample);
			}
			else {
				ItemUtil.givePlayerItem(player, sample);
				if(!player.capabilities.isCreativeMode){
					player.setHeldItem(hand, ItemUtil.consumeItem(stack));
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
    {
		ItemStack stack = player.getHeldItem(hand);
		if(stack.getMetadata() == DNAItemType.CURE.getMetadata()){
			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
			if(exPlayer !=null){
				if(!worldIn.isRemote){
					exPlayer.setPlayerDisguiseUUID(null);
					exPlayer.setMini(false);
					DisguiseHandler.updateSize(player, false);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("NullUUID", true);
					nbt.setBoolean("Mini", false);
					PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
					CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
					CrystalModNetwork.sendToAll(message);
				}
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		if(stack.getMetadata() == DNAItemType.SAMPLE_EMPTY.getMetadata()){
			UUID playerDNA = null;
			RayTraceResult ray = EntityUtil.getPlayerLookedObject(player);
			if(ray !=null && ray.entityHit !=null){
				if(ray.entityHit instanceof EntityPlayer){
					EntityPlayer otherPlayer = (EntityPlayer)ray.entityHit;
					if(!otherPlayer.capabilities.disableDamage){
						if(EntityUtil.isSneakSuccessful(player, otherPlayer)){
							playerDNA = EntityPlayer.getUUID(otherPlayer.getGameProfile());							
						}
					}
				}
				//Tamed Horses carry DNA
				if(ray.entityHit instanceof AbstractHorse){
					AbstractHorse horse = (AbstractHorse)ray.entityHit;
					if(horse.getOwnerUniqueId() !=null){
						playerDNA = horse.getOwnerUniqueId();	
					}
				}
			}
			if(playerDNA !=null){
				ItemStack sample = new ItemStack(this, 1, DNAItemType.SAMPLE_FULL.getMetadata());
				PlayerDNA.savePlayerDNA(sample, playerDNA);
				if(ItemStackTools.getStackSize(stack) == 1){
					player.setHeldItem(hand, sample);
				}
				else {
					ItemUtil.givePlayerItem(player, sample);
					if(!player.capabilities.isCreativeMode){
						player.setHeldItem(hand, ItemUtil.consumeItem(stack));
					}
				}
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);	
			}
		}
		
		if(stack.getMetadata() == DNAItemType.EMPTY_SYRINGE.getMetadata()){
			ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
			if(playerEx !=null){
				EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
				ItemStack otherStack = player.getHeldItem(otherHand);
				if(ItemStackTools.isValid(otherStack) && otherStack.getItem() == Item.getItemFromBlock(Blocks.RED_MUSHROOM)){
					if(!worldIn.isRemote){
						boolean changed = !playerEx.isMini();
						playerEx.setMini(changed);
						DisguiseHandler.updateSize(player, changed);
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setBoolean("Mini", changed);
						PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
						CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
						CrystalModNetwork.sendToAll(message);
					}
					player.swingArm(hand);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		
		if(stack.getMetadata() == DNAItemType.FILLED_SYRINGE.getMetadata()){
			UUID playerDNA = PlayerDNA.loadPlayerDNA(stack);
			if(playerDNA !=null){
				ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
				if(playerEx !=null){
					if ((playerDNA == null) || (UUIDUtils.areEqual(playerDNA, player.getUniqueID())))
					{
						if(playerEx.getPlayerDisguiseUUID() == null){
							return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
						}
						if(!worldIn.isRemote){								
							playerEx.setPlayerDisguiseUUID(null);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setBoolean("NullUUID", true);
							PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
							CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
							CrystalModNetwork.sendToAll(message);
						}
						player.swingArm(hand);
						if(!player.capabilities.isCreativeMode){
							ItemStack empty = new ItemStack(ModItems.dnaItems, 1, DNAItemType.EMPTY_SYRINGE.getMetadata());
							if(ItemStackTools.getStackSize(stack) == 1){
								player.setHeldItem(hand, empty);
							}
							else {
								ItemUtil.givePlayerItem(player, empty);
								player.setHeldItem(hand, ItemUtil.consumeItem(stack));
							}
						}
					}
					else
					{
						if(playerEx.getPlayerDisguiseUUID() !=null){
							if(UUIDUtils.areEqual(playerEx.getPlayerDisguiseUUID(), playerDNA)){
								return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
							}
						}
						if(!worldIn.isRemote){
							playerEx.setPlayerDisguiseUUID(playerDNA);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setTag("UUID", NBTUtil.createUUIDTag(playerDNA));
							PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
							CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
							CrystalModNetwork.sendToAll(message);
						}
						player.swingArm(hand);
						if(!player.capabilities.isCreativeMode){
							ItemStack empty = new ItemStack(ModItems.dnaItems, 1, DNAItemType.EMPTY_SYRINGE.getMetadata());
							if(ItemStackTools.getStackSize(stack) == 1){
								player.setHeldItem(hand, empty);
							}
							else {
								ItemUtil.givePlayerItem(player, empty);
								player.setHeldItem(hand, ItemUtil.consumeItem(stack));
							}
						}
					}
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
	
	public UUID collectDNAFromBed(World world, BlockPos pos, EntityPlayer player, ItemStack stack){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.BED || state.getBlock().isBed(state, world, pos, player)){
			//Is a bed
			List<EntityPlayer> bedPlayers = Lists.newArrayList();
			for(EntityPlayer otherPlayer : world.playerEntities){
				BlockPos bedPos = otherPlayer.getBedLocation(world.provider.getDimension());
				if(bedPos !=null && bedPos.equals(pos)){
					bedPlayers.add(otherPlayer);
				}
			}
			
			if(bedPlayers.size() > 0){
				EntityPlayer bedPlayer = bedPlayers.get(0);
				return EntityPlayer.getUUID(bedPlayer.getGameProfile());
			}
		}
		return null;
	}
	
	public static enum DNAItemType implements IStringSerializable, IEnumMetaItem
    {
        SAMPLE_EMPTY(0, "sample_empty"),
        SAMPLE_FULL(1, "sample_full"),
		EMPTY_SYRINGE(2, "syringe_empty"),
        FILLED_SYRINGE(3, "syringe_full"),
        CURE(4, "cure");

        private static final DNAItemType[] METADATA_LOOKUP = new DNAItemType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private DNAItemType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        @Override
		public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        @Override
		public String toString()
        {
            return this.unlocalizedName;
        }

        public static DNAItemType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        @Override
		public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (DNAItemType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
