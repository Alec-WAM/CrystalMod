package alec_wam.CrystalMod.entities.disguise;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.items.IEnumMetaItem;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDisguise extends Item implements ICustomModel {

	public ItemDisguise(){
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "disguise");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(EnumDisguiseType type : EnumDisguiseType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getName()));
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + EnumDisguiseType.byMetadata(i).getName();
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advTooltips)
	{
		super.addInformation(stack, player, list, advTooltips);
		if(getBoundUUID(stack) !=null){
			UUID bound = getBoundUUID(stack);
			String username = ProfileUtil.getUsername(bound);
			if(username !=ProfileUtil.ERROR){
				list.add(username);
			} else {
				list.add(UUIDUtils.fromUUID(bound));
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        subItems.add(new ItemStack(itemIn, 1, 0));
        
        List<UUID> defaultUUIDs = Lists.newArrayList();
        defaultUUIDs.add(PlayerUtil.Alec_WAM);
        defaultUUIDs.add(PlayerUtil.AH9902);
        defaultUUIDs.add(PlayerUtil.Kilowag1453);
        defaultUUIDs.add(PlayerUtil.long_shot99);
        defaultUUIDs.add(ProfileUtil.getUUID("Etho"));
        for(UUID uuid : defaultUUIDs){
        	ItemStack stack = new ItemStack(itemIn, 1, 1);
        	setBoundUUID(stack, uuid);
        	subItems.add(stack);
        }
        
        subItems.add(new ItemStack(itemIn, 1, 2));
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
	   return 60;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BOW;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int countdown)
	{
		if(!(entity instanceof EntityPlayer)){
			return;
		}
		EntityPlayer player = (EntityPlayer)entity;
		if (!player.getEntityWorld().isRemote)
		{
			ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
			int level = 1;
			if (countdown == Math.max((level - 1) * 4, 1))
			{
				DisguiseType currentDiguise = playerEx.getCurrentDiguise();
				if(stack.getItemDamage() == 0){
					playerEx.setCurrentDiguise(DisguiseType.NONE);
					//Clears Last and Current
					playerEx.setPlayerDisguiseUUID(null);
					playerEx.setPlayerDisguiseUUID(null);
					DisguiseHandler.updateSize(player, DisguiseType.NONE);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setByte("Type", (byte)0);
					nbt.setBoolean("LastNullUUID", true);
					PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
					CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
					CrystalModNetwork.sendToAll(message);
					return;
				}
				if (stack.getItemDamage() == 1)
				{
					UUID boundUUID = getBoundUUID(stack);
					if ((boundUUID == null) || (UUIDUtils.areEqual(boundUUID, player.getUniqueID())))
					{
						if (currentDiguise == DisguiseType.PLAYER)
						{
							playerEx.setCurrentDiguise(DisguiseType.NONE);
							DisguiseHandler.updateSize(player, DisguiseType.NONE);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setByte("Type", (byte)0);
							nbt.setBoolean("NullUUID", true);
							PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
							CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
							CrystalModNetwork.sendToAll(message);
							return;
						} else if(currentDiguise == DisguiseType.MINI){
							UUID uuid = playerEx.getLastPlayerDisguiseUUID();
							playerEx.setPlayerDisguiseUUID(uuid);
							NBTTagCompound nbt = new NBTTagCompound();
							if(uuid !=null)nbt.setTag("UUID", NBTUtil.createUUIDTag(uuid));
							else nbt.setBoolean("NullUUID", true);
							PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
							CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
							CrystalModNetwork.sendToAll(message);
						}
					}
					else
					{
						if(currentDiguise == DisguiseType.MINI){
							UUID uuid = boundUUID;
							playerEx.setPlayerDisguiseUUID(uuid);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setTag("UUID", NBTUtil.createUUIDTag(uuid));
							PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
							CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
							CrystalModNetwork.sendToAll(message);
							return;
						}
						
						UUID lastUUID = playerEx.getLastPlayerDisguiseUUID();
						UUID currentUUID = playerEx.getPlayerDisguiseUUID();
						if(currentUUID !=null && UUIDUtils.areEqual(currentUUID, boundUUID)){
							if(lastUUID !=null){
								playerEx.setPlayerDisguiseUUID(lastUUID);
								NBTTagCompound nbt = new NBTTagCompound();
								nbt.setTag("UUID", NBTUtil.createUUIDTag(lastUUID));
								PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
								CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
								CrystalModNetwork.sendToAll(message);
								return;
							}
						}
						
						playerEx.setPlayerDisguiseUUID(boundUUID);
						playerEx.setCurrentDiguise(DisguiseType.PLAYER);
						DisguiseHandler.updateSize(player, DisguiseType.PLAYER);
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setByte("Type", (byte)DisguiseType.PLAYER.ordinal());
						nbt.setTag("UUID", NBTUtil.createUUIDTag(boundUUID));
						PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
						CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
						CrystalModNetwork.sendToAll(message);
						return;
					}
				}
				if(stack.getItemDamage() == 2){
					if (currentDiguise == DisguiseType.PLAYER || currentDiguise == DisguiseType.NONE)
					{
						playerEx.setCurrentDiguise(DisguiseType.MINI);
						DisguiseHandler.updateSize(player, DisguiseType.MINI);
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setByte("Type", (byte)DisguiseType.MINI.ordinal());
						if(playerEx.getPlayerDisguiseUUID() !=null){
							nbt.setTag("UUID", NBTUtil.createUUIDTag(playerEx.getPlayerDisguiseUUID()));
						} else {
							nbt.setBoolean("NullUUID", true);
						}
						PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
						CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
						CrystalModNetwork.sendToAll(message);
						ChatUtil.sendNoSpam(player, "Mini");
						return;
					}else{
						DisguiseType lastDisguise = playerEx.getLastDiguise();
						if(lastDisguise == null){
							lastDisguise = DisguiseType.NONE;
						}
						playerEx.setCurrentDiguise(lastDisguise);
						DisguiseHandler.updateSize(player, lastDisguise);
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setByte("Type", (byte)lastDisguise.ordinal());
						if(lastDisguise == DisguiseType.PLAYER){
							if(playerEx.getLastPlayerDisguiseUUID() !=null)nbt.setTag("UUID", NBTUtil.createUUIDTag(playerEx.getLastPlayerDisguiseUUID()));
							else {
								nbt.setBoolean("NullUUID", true);
							}
						} 
						PacketEntityMessage message = new PacketEntityMessage(player, "DisguiseSync", nbt);
						CrystalModNetwork.sendTo(message, (EntityPlayerMP)player);
						CrystalModNetwork.sendToAll(message);
						return;
					}
				}
			}
		}
	}
	
	public static void setBoundUUID(ItemStack stack, UUID uuid){
		ItemNBTHelper.getCompound(stack).setTag("BoundUUID", NBTUtil.createUUIDTag(uuid));
	}
	
	public static UUID getBoundUUID(ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "BoundUUID")){
			return NBTUtil.getUUIDFromTag(stack.getTagCompound().getCompoundTag("BoundUUID"));
		}
		return null;
	}
	
	public static enum EnumDisguiseType implements IStringSerializable, IEnumMetaItem
    {
		EMPTY, PLAYER, MINI;

		@Override
		public int getMetadata() {
			return ordinal();
		}

		public static EnumDisguiseType byMetadata(int i) {
			int index = i;
			if(index < 0 || index >=values().length){
				index = 0;
			}
			return values()[index];
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	
    }
}
