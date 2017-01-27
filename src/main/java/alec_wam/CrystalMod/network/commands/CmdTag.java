package alec_wam.CrystalMod.network.commands;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import mezz.jei.Internal;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.handler.MissingItemHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketExtendedPlayer;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.StringUtils;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.world.game.tag.TagManager;
import alec_wam.CrystalMod.world.game.tag.TagManager.PlayerData;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class CmdTag extends AbstractCMCommand{

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
		return "tag";
	}

	@Override
    public boolean isClientSide() {
        return false;
    }
	
	public static Color getColor(String input){
		Color color = Color.WHITE;
		
		if(input.equalsIgnoreCase("black"))color = Color.DARK_GRAY.darker().darker();
		if(input.equalsIgnoreCase("blue"))color = Color.BLUE;
		if(input.equalsIgnoreCase("cyan"))color = Color.CYAN;
		if(input.equalsIgnoreCase("dgray"))color = Color.DARK_GRAY;
		if(input.equalsIgnoreCase("gray"))color = Color.GRAY;
		if(input.equalsIgnoreCase("green"))color = Color.GREEN;
		if(input.equalsIgnoreCase("lgray"))color = Color.LIGHT_GRAY;
		if(input.equalsIgnoreCase("magenta"))color = Color.MAGENTA;
		if(input.equalsIgnoreCase("orange"))color = Color.ORANGE;
		if(input.equalsIgnoreCase("pink"))color = Color.PINK;
		if(input.equalsIgnoreCase("red"))color = Color.RED;
		if(input.equalsIgnoreCase("yellow"))color = Color.YELLOW;
		
		return color;
	}
	
	@Override
	public void execute(ICommandSender sender, String[] args) {
		 if (sender instanceof EntityPlayer) {
	            EntityPlayer player = (EntityPlayer) sender;
				if(TagManager.getInstance() == null)
				{
					sender.addChatMessage(new TextComponentString("Teams mod is broken. You will need to look at the server side logs to see what's wrong"));
					return;
				}
				
				if(args.length <= 1){
					ModLogger.info("gui");
					/*GuiHandler.openWorksiteGui(player, GuiHandler.GUI_ID_TAG_ROUNDCHOOSE, 0, 0, 0);
					sender.addChatMessage(new ChatComponentText("Opening GUI"));*/
					//Minecraft.getMinecraft().displayGuiScreen(new GuiSelectRound(Minecraft.getMinecraft().currentScreen));
					return;
			    }
				
				if(args.length > 1 && args[1].equalsIgnoreCase("jei")){
					//Internal.getHelpers().reload();
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
				
				//TagRound round = new TagRound(GameType.ADVENTURE, (int)TagRound.MINUTE*5, 10);
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
				
				if(args.length > 1 && args[1].equalsIgnoreCase("join")){
					if(args.length > 2){
						/*if(Utils.isInternetAvailable() && !Utils.doesAccountExist(args[2])){
							ChatHandler.sendMessageToPlayer(args[2]+" is not a player. Are You sure?", player);
						}*/
						TagManager.getInstance().joinGame(args[2]);
					}
					else {
						
						TagManager.getInstance().joinGame(player.getName());
					}
					return;
			    }
				if(args.length > 1 && args[1].equalsIgnoreCase("leave")){
					if(args.length > 2)TagManager.getInstance().leaveGame(args[2]);
					else TagManager.getInstance().leaveGame(player.getName());
					return;
			    }
				
				if(args.length > 1 && args[1].equalsIgnoreCase("find")){
					if(args.length > 2){
						ChatUtil.sendChat(player, ""+CrystalMod.proxy.getPlayerForUsername(args[2]));
					}
					else {
						ChatUtil.sendChat(player, ""+CrystalMod.proxy.getPlayerForUsername(player.getName()));
					}
					return;
				}
				
				///OP
				if(/*CrystalMod.proxy.isOp(player.getGameProfile())*/true){
					if(args.length > 1){
						String command = args[1];
						if(command.equalsIgnoreCase("on")){
							TagManager.getInstance().enabled = true;
							ChatUtil.sendChat(player, "Tag enabled = "+TagManager.getInstance().enabled);
							return;
					    }
						/*if(command.equalsIgnoreCase("timeout")){
							TagManager.getInstance().timeout = true;
							//ChatHandler.sendMessageToPlayer("Tag enabled = "+TagManager.instance.enabled, player);
							TagManager.getInstance().messageAll("Timeout by "+player.getCommandSenderName());
							return;
					    }*/
						/*if(command.equalsIgnoreCase("timein")){
							TagManager.getInstance().timeout = false;
							//ChatHandler.sendMessageToPlayer("Tag enabled = "+TagManager.getInstance().enabled, player);
							TagManager.getInstance().messageAll("Timein by "+player.getCommandSenderName());
							return;
					    }*/
						if(command.equalsIgnoreCase("off")){
							TagManager.getInstance().enabled = false;
							ChatUtil.sendChat(player, "Tag enabled = "+TagManager.getInstance().enabled);
							Iterator<PlayerData> ii = TagManager.getInstance().players.iterator();
							while(ii.hasNext()){
								PlayerData data = ii.next();
								TagManager.getInstance().leaveGame(data.playerName);
							}
							TagManager.getInstance().clearScores();
							return;
					    }
						/*if(command.equalsIgnoreCase("start")){
							TagManager.getInstance().enabled = true;
							if(TagManager.getInstance().midGame)TagManager.getInstance().endRound(TagManager.getInstance().wantsAutoStart == false);
							TagManager.getInstance().round = round;
							TagManager.getInstance().joinGame(player.getCommandSenderName());
							TagManager.getInstance().startRound();
							return;
					    }*/
						/*if(command.equalsIgnoreCase("endRound")){
							TagManager.getInstance().endRound(TagManager.getInstance().wantsAutoStart == false);
							return;
					    }*/
						/*if(command.equalsIgnoreCase("stop")){
							TagManager.getInstance().time = TagManager.getInstance().round.timeLimit;
							return;
					    }*/
						if(command.equalsIgnoreCase("clear")){
							TagManager.getInstance().clearScores();
							ChatUtil.sendChat(player, "Clearing Tag List");
							return;
					    }
						/*if(command.equalsIgnoreCase("autoTeam")){
							boolean oldBal = TagManager.getInstance().wantsAutoBalance;
							TagManager.getInstance().wantsAutoBalance = !oldBal;
							ChatHandler.sendMessageToPlayer("Tag autobalance = "+TagManager.getInstance().wantsAutoBalance, player);
							return;
					    }
						if(command.equalsIgnoreCase("autoRound")){
							boolean oldBal = TagManager.getInstance().wantsAutoStart;
							TagManager.getInstance().wantsAutoStart = !oldBal;
							ChatHandler.sendMessageToPlayer("Tag autostart = "+TagManager.getInstance().wantsAutoStart, player);
							return;
					    }*/
				    }
					/*if(args.length > 2 && args[1].equalsIgnoreCase("roundDelay")){
						int time;
						try{
							time = Integer.parseInt(args[2]);
						}catch(NumberFormatException e){
							ChatHandler.sendMessageToPlayer("The time given was not a number", player);
							ChatHandler.sendMessageToPlayer("/cm tag roundDelay <seconds>", player);
							return;
						}
						TagManager.getInstance().winScreenStart = time*(int)TagRound.SECOND;
						ChatHandler.sendMessageToPlayer("Tag roundDelay = "+TagManager.getInstance().winScreenStart/20+"s", player);
						return;
				    }*/
					PlayerData data2 = args.length > 2 ? TagManager.getInstance().getData(args[2]) : null;
					
					if(args.length >= 5 && args[1].equalsIgnoreCase("data") && data2 !=null){
						if(args[3].equalsIgnoreCase("score")){
							data2.score = Integer.parseInt(args[4]);
							ChatUtil.sendChat(player, "Setting "+data2.playerName+"'s score to "+data2.score);
							return;
						}
						if(args[3].equalsIgnoreCase("it")){
							data2.timesIT = Integer.parseInt(args[4]);
							ChatUtil.sendChat(player, "Setting "+data2.playerName+"'s times it to "+data2.timesIT);
							return;
						}
						if(args[3].equalsIgnoreCase("time")){
							data2.timeIt = Integer.parseInt(args[4])*TimeUtil.SECOND;
							ChatUtil.sendChat(player, "Setting "+data2.playerName+"'s time it to "+TimeUtil.getSeconds((int)data2.timeIt));
							return;
						}
				    }
					ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(args.length >= 4 && args[1].equalsIgnoreCase("flag") && extPlayer !=null){
						String arg = args[3];
						if(args[2].equalsIgnoreCase("has")){
							boolean value = false;
							if(arg.equalsIgnoreCase("t"))value = true;
							if(arg.equalsIgnoreCase("f"))value = false;
							if(arg.equalsIgnoreCase("y"))value = true;
							if(arg.equalsIgnoreCase("n"))value = false;
							if(arg.equalsIgnoreCase("true"))value = true;
							if(arg.equalsIgnoreCase("false"))value = false;
							
							ChatUtil.sendChat(player, ""+value);
							
							extPlayer.setHasFlag(value);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setString("Command", "Flag");
							nbt.setBoolean("hasFlag", value);
							if(player instanceof EntityPlayerMP)
							CrystalModNetwork.sendTo(new PacketExtendedPlayer(nbt), (EntityPlayerMP) player);
							return;
						}
						if(args[2].equalsIgnoreCase("color")){
							int color = getColor(arg).getRGB();
							extPlayer.setFlagColor(color);
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setString("Command", "Flag");
							nbt.setInteger("FlagColor", color);
							if(player instanceof EntityPlayerMP)
							CrystalModNetwork.sendTo(new PacketExtendedPlayer(nbt), (EntityPlayerMP) player);
							return;
						}
				    }
					
					if(args.length > 2 && args[1].equalsIgnoreCase("tag")){
						if(args.length > 3){
						    if(data2 !=null){
						    	PlayerData pData = TagManager.getInstance().getData(args[2]);
								if(pData !=null){
									PlayerData p2Data = TagManager.getInstance().getData(args[3]);
									if(pData !=null){
										if(TagManager.getInstance().noTagBacks()){
											if(TagManager.getInstance().wasLastTagger(pData, p2Data)){
												ChatUtil.sendChat(player, TextFormatting.RED+args[2]+" cannot tag "+args[3]+" that would be cheating.");
												return;
											}
										}
									}
								}
							    TagManager.getInstance().tagPlayer(args[2], args[3]);
							}
							else{
								ChatUtil.sendChat(player, args[2]+" is not playing tag.");
							}
							return;
						}
						if(data2 !=null){
							TagManager.getInstance().tagPlayer(args[2], player.getName());
						}
						else{
							ChatUtil.sendChat(player, args[2]+" is not playing tag.");
						}
						return;
				    }
					
					if(args.length > 2 && args[1].equalsIgnoreCase("it")){
						if(data2 !=null){
						   if(!TagManager.getInstance().isTagger(data2))TagManager.getInstance().setTagger(data2);
						   else ChatUtil.sendChat(player, args[2]+ "is already it.");
						}
						else{
							ChatUtil.sendChat(player, args[2]+" is not playing tag.");
						}
						return;
				    }
					if(args.length > 2 && args[1].equalsIgnoreCase("unit")){
						if(data2 !=null){
						   if(TagManager.getInstance().isTagger(data2))TagManager.getInstance().removeTagger(data2);
						   else ChatUtil.sendChat(player, args[2]+ "is not it.");
						}
						else{
							ChatUtil.sendChat(player, args[2]+" is not playing tag.");
						}
						return;
				    }
			    }
		 }
	}

}
