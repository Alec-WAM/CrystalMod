package alec_wam.CrystalMod.network.commands;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.blocks.underwater.BlockCoral;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.MissingItemHandler;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.world.generation.FusionTempleFeature;
import alec_wam.CrystalMod.world.structures.CrystalWell;
import mezz.jei.Internal;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CmdDebug extends AbstractCMCommand{

	@Override
	public String getHelp() {
		return "";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getCommand() {
		return "debug";
	}

	@Override
	public boolean isClientSide() {
		return false;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			if(args.length <= 1){
				ModLogger.info("gui");
				/*GuiHandler.openWorksiteGui(player, GuiHandler.GUI_ID_TAG_ROUNDCHOOSE, 0, 0, 0);
					sender.addChatMessage(new ChatComponentText("Opening GUI"));*/
				//Minecraft.getMinecraft().displayGuiScreen(new GuiSelectRound(Minecraft.getMinecraft().currentScreen));
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("jei")){
				if(Loader.isModLoaded("jei"))Internal.getHelpers().reload();
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("fail")){
				ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
				if(exPlayer !=null){
					exPlayer.hasFailed = true;
				}
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("math")){
				/*boolean top = true;
					int age = 4;
					int compTop = (top ? 1 : 0);
			    	int compAge = age;
			    	int compressed = (compAge << 1) | compTop;
			    	ModLogger.info("Compressed: "+compressed);
			    	int afterTop = (compressed & 1);
			    	int afterAge = compressed >> 1;
			    	ModLogger.info("After: "+afterTop+" "+afterAge);*/
				int compTop = 1;
				int compAge = 3;
				int compressed = (compAge << 2) | compTop;
				ModLogger.info("Compressed: "+compressed);
				int afterTop = compressed & 3;
				int afterAge = compressed >> 2;
				ModLogger.info("After: "+afterTop+" "+afterAge);
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("coral")){
				BlockPos pos = new BlockPos(player);
				World world = player.getEntityWorld();
				int size = 6;
				if(args.length > 2){
					try{
						size = Integer.parseInt(args[2]);
					}catch(Exception e){}
				}

				BlockCoral.generateCoralCluster(world, pos, size, false, true);
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("diving") && player.capabilities.isCreativeMode){
				ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
				ItemUtil.addEnchantment(helmet, Enchantments.RESPIRATION, 3);
				ItemUtil.givePlayerItem(player, helmet);
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("well")){
				BlockPos pos = new BlockPos(player).offset(EnumFacing.DOWN);
				int type = 0;
				if(args.length > 2){
					if(args[2].equalsIgnoreCase("blue")){
						type = 0;
					}
					else if(args[2].equalsIgnoreCase("red")){
						type = 1;
					}
					else if(args[2].equalsIgnoreCase("green")){
						type = 2;
					}
					else if(args[2].equalsIgnoreCase("dark")){
						type = 3;
					} else{
						sender.sendMessage(new TextComponentString(args[2]+" is not a valid well type. Try [blue, red, green, or dark]"));
						return;
					}
				}

				CrystalWell.generateOverworldWell(player.getEntityWorld(), pos, EntityUtil.rand, type);
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("endwell")){
				BlockPos pos = new BlockPos(player).offset(EnumFacing.DOWN);
				CrystalWell.generateEndWell(player.getEntityWorld(), pos, EntityUtil.rand);
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("netherwell")){
				BlockPos pos = new BlockPos(player).offset(EnumFacing.DOWN);
				CrystalWell.generateNetherWell(player.getEntityWorld(), pos, EntityUtil.rand);
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("temple")){
				BlockPos pos = FusionTempleFeature.fusionTempleGen.getClosestStrongholdPos(sender.getEntityWorld(), sender.getPosition(), false);
				ChatUtil.sendChat(player, ""+(pos == null ? "null" : pos.toString()));
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("itemnbt")){
				ChatUtil.sendChat(player, (ItemStackTools.isValid(player.getHeldItemMainhand()) && player.getHeldItemMainhand().hasTagCompound() ? player.getHeldItemMainhand().getTagCompound().toString() : "empty"));
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("essence")){
				ItemMobEssence.initDefaultMobs();
				return;
			}
			
			if(args.length > 1 && args[1].equalsIgnoreCase("clearxp")){
				player.removeExperienceLevel(Integer.MAX_VALUE);
				return;
			}
			
			if(args.length > 1 && args[1].equalsIgnoreCase("angle")){
				if(args.length > 3){
					double x = 0.0D;
					double y = 0.0D;
					try{
						x = Double.parseDouble(args[2]);
						y = Double.parseDouble(args[3]);
					}catch(Exception e){}
					
					double angle = (float)(Math.atan2(y, x) * 180.0D / Math.PI);
					ChatUtil.sendChat(player, "Angle: "+angle);
				}
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("dumpItems")){
				List<String> list = Lists.newArrayList();
				for(Entry<ResourceLocation, Item> res : MissingItemHandler.remapItems.entrySet()){
					list.add(""+res.getKey().toString()+"["+res.getValue().getRegistryName()+"]");
				}
				ModLogger.info("Missing Items: "+StringUtils.makeReadable(list));
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("name")){
				if(args.length > 2){
					UUID uuid = UUIDUtils.fromString(args[2]);
					if(uuid !=null){
						String username = ProfileUtil.getUsername(uuid);
						ChatUtil.sendChat(player, UUIDUtils.fromUUID(uuid)+"'s username is "+username);
					}
				}else{
					ChatUtil.sendChat(player, "Missing Username");
				}	
				return;
			}

			if(args.length > 1 && args[1].equalsIgnoreCase("uuid")){
				if(args.length > 2){
					String name = args[2];
					if(!Strings.isNullOrEmpty(name)){
						UUID uuid = ProfileUtil.getUUID(name);
						ChatUtil.sendChat(player, name+"'s uuid is "+UUIDUtils.fromUUID(uuid));
					}
				}else{
					ChatUtil.sendChat(player, "Missing UUID");
				}	
				return;
			}
		}
	}

}
