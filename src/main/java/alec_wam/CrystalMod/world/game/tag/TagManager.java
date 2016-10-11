package alec_wam.CrystalMod.world.game.tag;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CompressedDataInput;
import alec_wam.CrystalMod.network.CompressedDataOutput;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.client.DownloadedTextures;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class TagManager {

	private static TagManager instance;
	
	public static TagManager getInstance(){
		if(instance == null){
			instance = new TagManager();
		}
		return instance;
	}
	
	public boolean enabled;
	
	public List<PlayerData> players;
	public List<PlayerData> currentTaggers;
	
	public TagManager(){
		instance = this;
		enabled = false;
		MinecraftForge.EVENT_BUS.register(this);
		players = new ArrayList<PlayerData>();
		currentTaggers = new ArrayList<PlayerData>();
		/*lastWinners = new ArrayList<PlayerData>();
		currentLeaders = new ArrayList<PlayerData>();
		lastTagger = new HashMap<String, String>();*/
	}
	
	public PlayerData getData(String name){
		Iterator<PlayerData> ii = players.iterator();
		while(ii.hasNext()){
			PlayerData data = ii.next();
			if(data.playerName.equals(name))return data;
		}
		return null;
	}
	
	public boolean joinGame(String player){
		
		if(getData(player) == null){
			PlayerData data = new PlayerData(player);
			this.players.add(data);
			updateData(data, PacketTagPlayerData.TYPE_ADD_PLAYER);
			messageAll(TextFormatting.GREEN+""+data.playerName+" has joined this game of tag!");
			EntityPlayer player2 = getPlayer(data);
			if(player2 !=null){
				sendDataList(player2, PacketTagPlayerData.TYPE_UPDATE_LIST);
			}
			return true;
		}
		
		return false;
	}
	
	public void leaveGame(String player){
		PlayerData data = getData(player);
		if(data !=null){
			removePlayer(data);
			messageAll(TextFormatting.RED+data.playerName+" has left this game of tag.");
			return;
		}
	}
	
	public void removePlayer(PlayerData data){
		this.players.remove(data);
		if(isTagger(data)){
			this.currentTaggers.remove(data);
		}
		updateData(data, PacketTagPlayerData.TYPE_REMOVE_PLAYER);
	}
	
	public EntityPlayer getPlayer(PlayerData data){
		if(data == null)return null;
		EntityPlayer player = CrystalMod.proxy.getPlayerForUsername(data.playerName);
		return player;
	}
	
	public void messageAll(String message){
		messageAll(message, players);
	}
	
	public void messageAll(String message, List<PlayerData> players){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			Iterator<PlayerData> ii = players.iterator();
			while(ii.hasNext()){
				PlayerData data = ii.next();
				EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(data.playerName);
				if(player !=null){
					ChatUtil.sendChat(player, message);
				}
			}
		}
	}
	
	public boolean noTagBacks(){
		return this.players.size() > 2 /*&& (this.round !=null ? this.round.tagBacks == false : true)*/;
	}
	
	public boolean wasLastTagger(PlayerData data, PlayerData data2){
		if(data !=null && data2 !=null && !Strings.isNullOrEmpty(data.lastTagger)){
			return data.lastTagger.equals(data2.playerName);
		}
		return false;
	}
	
	public boolean tagPlayer(String player, String player2){
		PlayerData data = getData(player);
		if(data !=null){
			PlayerData data2 = getData(player2);
			if(data2 == null || !isTagger(data2))return false;
			if(!noTagBacks() || !wasLastTagger(data, data2)){
				data2.score++;
				data.lastTagger = player2;
				removeTagger(data2);
				
				if(!isTagger(data)){
					setTagger(data);
				}
				updateData(data, PacketTagPlayerData.TYPE_UPDATE_PLAYER);
				updateData(data2, PacketTagPlayerData.TYPE_UPDATE_PLAYER);
				sendDataListToAll(currentTaggers, PacketTagPlayerData.TYPE_UPDATE_TAGGERS);
				return true;
			}
		}
		return false;
	}
	
	public void sendDataListToAll(List<PlayerData> data, int type){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(players.isEmpty())return;
			try {
				PacketTagPlayerData packet = new PacketTagPlayerData(type, compressDataList(data));
				Iterator<PlayerData> ii = players.iterator();
				while(ii.hasNext()){
					PlayerData data2 = ii.next();
					EntityPlayer player = getPlayer(data2);
					if(player !=null && player instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(packet, (EntityPlayerMP)player);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			ModLogger.warning("Trying to send datalist to all on the client side. Ignoring type = "+type);
		}
	}
	
	public void sendDataList(EntityPlayer player, int type){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(players.isEmpty())return;
			try {
				PacketTagPlayerData packet = new PacketTagPlayerData(type, compressDataList(players));
				if(player !=null && player instanceof EntityPlayerMP){
					CrystalModNetwork.sendTo(packet, (EntityPlayerMP)player);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			ModLogger.warning("Trying to send datalist to "+player.getName()+"on the client side. Ignoring type = "+type);
		}
	}
	
	public void updateData(PlayerData data, int type){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			try {
				PacketTagPlayerData packet = new PacketTagPlayerData(type, compressData(data));
				Iterator<PlayerData> ii = players.iterator();
				while(ii.hasNext()){
					PlayerData data2 = ii.next();
					EntityPlayer player = getPlayer(data2);
					if(player !=null && player instanceof EntityPlayerMP){
						CrystalModNetwork.sendTo(packet, (EntityPlayerMP)player);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			ModLogger.warning("Trying to update data on the client side. Ignoring");
		}
	}
	
	public int getBestScore(){
		int score = 0;
		Iterator<PlayerData> ii = players.iterator();
		while(ii.hasNext()){
			PlayerData data = ii.next();
			if(data.score > score)score = data.score;
		}
		return score;
	}
	
	public List<PlayerData> getWinners(){
		List<PlayerData> winners = new ArrayList<PlayerData>();
		Iterator<PlayerData> ii = players.iterator();
		while(ii.hasNext()){
			PlayerData data = ii.next();
			if(data.score >= getBestScore() && this.getBestScore() > 0)winners.add(data);
		}
		return winners;
	}
	
	public boolean isWinning(PlayerData data){
		return getWinners().contains(data);
	}
	
	public void clearScores(){
		Iterator<PlayerData> ii = players.iterator();
		while(ii.hasNext()){
			PlayerData data = ii.next();
			data.lastTagger = "";
			data.score = 0;
			data.timeIt = 0;
			data.timesIT = 0;
		}
		this.currentTaggers.clear();
	}
	
	public void setTagger(PlayerData data) {
		if(data == null || isTagger(data))return;
		currentTaggers.add(data);
		messageAll(TextFormatting.LIGHT_PURPLE+data.playerName+" is now it!");
		data.timesIT++;
	}
	
	public void removeTagger(PlayerData data) {
		if(data == null || !isTagger(data))return;
		currentTaggers.remove(data);
	}
	
	public boolean isTagger(PlayerData data) {
		if(data == null)return false;
		Iterator<PlayerData> ii = currentTaggers.iterator();
		while(ii.hasNext()){
			PlayerData data2 = ii.next();
			if(data2.playerName.equals(data.playerName))return true;
		}
		return false;
	}
	
	public List<PlayerData> getNonTaggers(){
		List<PlayerData> nonTaggers = new ArrayList<PlayerData>();
		Iterator<PlayerData> ii = players.iterator();
		while(ii.hasNext()){
			PlayerData data = ii.next();
			if(!isTagger(data))nonTaggers.add(data);
		}
		return nonTaggers;
	}
	
	public static byte[] compressDataList(List<PlayerData> data) throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try{
			cdo.writeInt(data.size());
			Iterator<PlayerData> ii = data.iterator();
			while(ii.hasNext()){
				PlayerData data2 = ii.next();
				data2.toBytes(cdo);
			}
			return cdo.getCompressed();
		}finally{
			cdo.close();
		}
	}
	
	public static byte[] compressData(PlayerData data) throws IOException {
		CompressedDataOutput cdo = new CompressedDataOutput();
		try {
			data.toBytes(cdo);
			return cdo.getCompressed();
		} finally {
			cdo.close();
		}
	}
	
	public static List<PlayerData> decompressDataList(byte[] compressed) throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try{
			List<PlayerData> data = Lists.newArrayList();
			int count = cdi.readInt();
			for(int d = 0; d < count; d++){
				data.add(PlayerData.fromBytes(cdi));
			}
			return data;
		}finally{
			cdi.close();
		}
	}
	
	public static PlayerData decompressData(byte[] compressed) throws IOException {
		CompressedDataInput cdi = new CompressedDataInput(compressed);
		try{
			return PlayerData.fromBytes(cdi);
		}finally{
			cdi.close();
		}
	}
	
	public static class PlayerData implements Comparable<PlayerData> {
		public String playerName;
		public String lastTagger;
		public int score;
		public int timesIT;
		public long timeIt;
		private UUID playeruuid;
		
		public PlayerData(EntityPlayer player){
			this.playerName = player.getName();
			this.lastTagger = "";
			this.score = 0;
			this.timeIt = 0L;
			this.timesIT = 0;
		}
		public PlayerData(String player){
			this.playerName = player;
			this.lastTagger = "";
			this.score = 0;
			this.timeIt = 0L;
			this.timesIT = 0;
		}
		public PlayerData(){
			this.playerName = "<ERROR>";
			this.lastTagger = "";
			this.score = 0;
			this.timeIt = 0L;
			this.timesIT = 0;
		}
		
		public void copyValues(PlayerData data){
			this.playerName = data.playerName;
			this.lastTagger = data.lastTagger;
			this.score = data.score;
			this.timesIT = data.timesIT;
			this.timeIt = data.timeIt;
		}
		
		public UUID getUUID(){
			if(playeruuid == null){
				playeruuid = ProfileUtil.getUUID(playerName);
			}
			return playeruuid;
		}
		
		public boolean equals(Object obj){
			if(obj !=null && obj instanceof PlayerData){
				return this.playerName.equals(((PlayerData)obj).playerName);
			}
			return false;
		}
		public int compareTo(PlayerData data)
	    {
			if(this.score < data.score || this.timesIT < data.timesIT || this.timeIt < data.timeIt){
				return 1;
			}
			if(this.score > data.score || this.timesIT > data.timesIT || this.timeIt > data.timeIt){
				return -1;
			}
			else{
				return this.playerName.compareTo(playerName);
			}
	    }
		
		public void toBytes(CompressedDataOutput cdo) throws IOException {
			cdo.writeUTF(playerName);
			if(!Strings.isNullOrEmpty(lastTagger)){
				cdo.writeUTF(lastTagger);
			}else{
				cdo.writeUTF("");
			}
			cdo.writeInt(score);
			cdo.writeInt(timesIT);
			cdo.writeLong(timesIT);
		}
		
		public static PlayerData fromBytes(CompressedDataInput cdi) throws IOException {
			PlayerData data = new PlayerData(cdi.readUTF());
			data.lastTagger = cdi.readUTF();
			data.score = cdi.readInt();
			data.timesIT = cdi.readInt();
			data.timeIt = cdi.readLong();
			return data;
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void eventHandler(RenderGameOverlayEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontrenderer = mc.fontRendererObj;
		String playerUsername = mc.thePlayer.getName();
		PlayerData playerData = getData(playerUsername);
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i = scaledresolution.getScaledWidth();
		//int j = scaledresolution.getScaledHeight();
		
		//Tessellator tessellator = Tessellator.instance;
		int taggerColor = /*this.currentTagger !=null && this.currentTagger.score >=this.getBestScore() && this.getBestScore() > 0?*/ 0x00DE94;//: Color.YELLOW.getRGB();
		boolean TAB = mc.gameSettings.keyBindPlayerList.isKeyDown();
		if(!event.isCancelable() && this.enabled && (TAB /*|| winScreenDelay > 0*/) && playerData !=null){
			if(event.getType() == ElementType.PLAYER_LIST && TAB){
				if(event instanceof RenderGameOverlayEvent.Pre){
					event.setCanceled(true);
				}
			}
			
			int ntSize = getNonTaggers().size();
			
			//long newTime = mc.theWorld.getWorldInfo().getWorldTime();
			int k = scaledresolution.getScaledWidth();
			int l = scaledresolution.getScaledHeight();
			GlStateManager.pushMatrix();
			//GlStateManager.enableBlend();

			mc.renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/overlay/tag/teamsScores2.png"));
			int numLines = (ntSize > currentTaggers.size() ? ntSize : currentTaggers.size());
			int guiHeight = 68 + 9 * numLines;

			GlStateManager.color(1f, 1f, 1f, 1f);
			int m = k / 2 - 156;
			int n = l / 2 - guiHeight / 2 ;
			
			Gui.drawModalRectWithCustomSizedTexture(m, n, 100, 0, 312, 66, 512, 256);
			for(int p = 0; p < numLines; p++)
			Gui.drawModalRectWithCustomSizedTexture(m, n + 66 + 9 * p, 100, 71, 312, 9, 512, 256);
			Gui.drawModalRectWithCustomSizedTexture(m, n + 66 + numLines * 9, 100, 168, 312, 12, 512, 256);
			
			boolean showZombieScore = false;
			if(showZombieScore)
			{
				Gui.drawModalRectWithCustomSizedTexture(m + 103, n + 51, 412, 0, 29, 11, 512, 256);
				Gui.drawModalRectWithCustomSizedTexture(m + 254, n + 51, 412, 0, 29, 11, 512, 256);
			}
			//Gui.func_146110_a(m, n, 100, 0, 312, 66, 512, 256);
			/*boolean timeBool = this.round !=null && this.round.infiTime == false && (this.round.timeLimit > 0 && this.round.timeLimit - this.time < 0); 
			if(!midGame() ||  timeBool)
			{
				mc.ingameGUI.drawString(fontrenderer, getWinMessage(), m + 10, n + 20, 0xffffff);
				int secondsLeft = (winScreenDelay) / 20;
				int realTime = secondsLeft;
				int minutesLeft = secondsLeft / 60;
				secondsLeft = secondsLeft % 60;
				EnumChatFormatting color = realTime <= 10 ? ChatHandler.RED : realTime <= 30 ? ChatHandler.GOLD : ChatHandler.WHITE;
				String delay = (this.wantsAutoStart ? "Next Round in " : "Ending Game in ") + (secondsLeft < 10 ? "0" + secondsLeft : secondsLeft);
				if((this.winScreenDelay > 0))mc.ingameGUI.drawString(fontrenderer, delay, m + 302 - fontrenderer.getStringWidth(delay), n + 20, 0xffffff);
			}
			else
			{
				int secondsLeft = ((int)(round !=null ? round.timeLimit : time)-(int)time) / 20;
				int realTime = secondsLeft;
				int minutesLeft = secondsLeft / 60;
				int hoursLeft = minutesLeft / 60;
				secondsLeft = secondsLeft % 60;
				minutesLeft = minutesLeft % 60;
				EnumChatFormatting color = realTime <= 10 ? ChatHandler.DARK_RED : realTime <= 30 ? ChatHandler.RED : realTime <= 60 ? ChatHandler.GOLD : ChatHandler.WHITE;
				String timeLeft = "Time Left: "+color + (hoursLeft > 0 ? hoursLeft+":" : "") + (minutesLeft < 10 ? "0" + minutesLeft : minutesLeft) + ":" + (secondsLeft < 10 ? "0" + secondsLeft : secondsLeft);
				if(round !=null && (round.infiTime || round.timeLimit <=0))timeLeft = "Time Left: "+"Infinite";
				if(timeout)timeLeft = "Timeout";
				mc.ingameGUI.drawString(fontrenderer, timeLeft, m + 10, n + 20, 0xffffff);
				String score = "Score Limit : " + (round !=null ? (this.round.infiScore || this.round.scoreLimit <=0) ? "Infinite" : round.scoreLimit : 0);
				mc.ingameGUI.drawString(fontrenderer, score, m + 302 - fontrenderer.getStringWidth(score), n + 20, 0xffffff);
			}*/
			
			for(int p = 0; p < 2; p++)
			{
				String[] names = new String[]{"Player"+(ntSize > 1 ? "s" : "") + (ntSize > 1 ? " ("+(ntSize)+")" : ""), "Tagger"+(currentTaggers.size() > 1 ? "s" : "")+ (currentTaggers.size() > 1 ? " ("+currentTaggers.size()+")" : "")};
				fontrenderer.drawString(TextFormatting.BOLD+names[p], m + 10 + 151 * p, n + 39, p == 0 ? Color.WHITE.getRGB() : Color.YELLOW.getRGB());
				
				
				List<PlayerData> list = p == 0 ? getNonTaggers() : currentTaggers;
				//int p3 = 0;
				for(int p2 = 0; p2 < list.size(); p2++)
				{
					PlayerData data = list.get(p2);
					GlStateManager.pushMatrix();
					mc.renderEngine.bindTexture(DownloadedTextures.getSpecialResource(data.getUUID()).getFace2d());
					GlStateManager.color(1f, 1f, 1f, 1f);
					
					Gui.drawModalRectWithCustomSizedTexture(m + 12 + 151 * p, n + 67 + 9 * p2, 0, 0, 8, 8, 8, 8);
					GlStateManager.popMatrix();
					
					int textCol = p == 1 ? (0x00DE94) : isWinning(data) ? Color.YELLOW.getRGB() : 0xffffff;
					mc.ingameGUI.drawString(fontrenderer, data.playerName, m + 10 + 12 + 151 * p, n + 67 + 9 * p2, textCol/*0xffffff*/);
					mc.ingameGUI.drawCenteredString(fontrenderer, "" + (list.isEmpty() ? 0 : data.score), m + 111 + 151 * p, n + 67 + 9 * p2, 0xffffff);
					mc.ingameGUI.drawCenteredString(fontrenderer, "" + (list.isEmpty() ? 0 : data.timesIT), m + 127 + 151 * p, n + 67 + 9 * p2, 0xffffff);
					
					int itsecondsLeft = (int) (list.isEmpty() ? 0 : data.timeIt);
					int itminutesLeft = itsecondsLeft / 60;
					int ithoursLeft = itminutesLeft / 60;
					
					itsecondsLeft = itsecondsLeft % 60;
					itminutesLeft = itminutesLeft % 60;
					String ittimeLeft = (ithoursLeft > 0 ? ithoursLeft+":" : "") + (itminutesLeft < 10 ? "0" + itminutesLeft : itminutesLeft) + ":" + (itsecondsLeft < 10 ? "0" + itsecondsLeft : itsecondsLeft);

					GlStateManager.pushMatrix();
					GlStateManager.translate(m + 143 + 151 * p, n + 67 + 9 * p2 + 2, 0);
					GlStateManager.scale(0.6, 0.6, 0);
					mc.ingameGUI.drawCenteredString(fontrenderer, "" + ittimeLeft, 0, 0, 0xffffff);
					GlStateManager.popMatrix();
				}
			}
			//GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
		
		if(!event.isCancelable() && event.getType() == ElementType.HOTBAR){
			
			if(this.enabled && Minecraft.getMinecraft().thePlayer != null && (players.size() > 0) && playerData !=null)
			{
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.disableDepth();
				GlStateManager.depthMask(false);
				GlStateManager.blendFunc(770, 771);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableAlpha();
				
				mc.renderEngine.bindTexture(new ResourceLocation("crystalmod:textures/gui/overlay/tag/teamsScores.png"));
				
				Tessellator tessellator = Tessellator.getInstance();
		        VertexBuffer worldrenderer = tessellator.getBuffer();
		        
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		        worldrenderer.pos((double)i / 2 - 43, (double)27D, (double)-90D).tex(85D / 256D, 27D / 256D).endVertex();
		        worldrenderer.pos((double)i / 2 + 43, (double)27D, (double)-90D).tex(171D / 256D, 27D / 256D).endVertex();
		        worldrenderer.pos((double)i / 2 + 43, (double)0, (double)-90D).tex(171D / 256D, 0D / 256D).endVertex();
		        worldrenderer.pos((double)i / 2 - 43, (double)0, (double)-90D).tex(85D / 256D, 0D / 256D).endVertex();
		        tessellator.draw();
				
				
				//If we are in a two team gametype, draw the team scores at the top of the screen
				if(players.size() >= 1)
				{
					
					//Draw team 1 colour bit
					int colour = Color.WHITE.getRGB();	
					if(this.getBestScore() > 0)colour = Color.YELLOW.getRGB();
					
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			        float r = ((colour >> 16) & 0xff) / 256F;
			        float g = ((colour >> 8) & 0xff) / 256F;
			        float b = (colour & 0xff) / 256F;
			        worldrenderer.pos((double)i / 2 - 43, (double)27D, (double)-90D).tex(0D / 256D, 125D / 256D).color(r, g, b, 1.0F).endVertex();
			        worldrenderer.pos((double)i / 2 - 19, (double)27D, (double)-90D).tex(24D / 256D, 125D / 256D).color(r, g, b, 1.0F).endVertex();
			        worldrenderer.pos((double)i / 2 - 19, (double)0, (double)-90D).tex(24D / 256D, 98D / 256D).color(r, g, b, 1.0F).endVertex();
			        worldrenderer.pos((double)i / 2 - 43, (double)0, (double)-90D).tex(0D / 256D, 98D / 256D).color(r, g, b, 1.0F).endVertex();
			        tessellator.draw();
					
					//Draw team 2 colour bit
					colour = Color.WHITE.getRGB();	
					
					if(isWinning(playerData))colour = Color.YELLOW.getRGB();
					
					GlStateManager.enableBlend();
					GlStateManager.disableAlpha();
			        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			        GlStateManager.shadeModel(GL11.GL_SMOOTH);
					
			        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			        
			        float f1 = ((colour >> 16) & 0xff) / 256F;
			        float f2 = ((colour >> 8) & 0xff) / 256F;
			        float f3 = (colour & 0xff) / 256F;
			        worldrenderer.pos((double)i / 2 + 19, (double)27D, (double)-90D).tex(62D / 256D, 125D / 256D).color(f1, f2, f3, 1.0F).endVertex();
			        worldrenderer.pos((double)i / 2 + 43, (double)27D, (double)-90D).tex(86D / 256D, 125D / 256D).color(f1, f2, f3, 1.0F).endVertex();
			        
			        if(isTagger(playerData))colour = taggerColor;
			        
			        float f5 = ((colour >> 16) & 0xff) / 256F;
			        float f6 = ((colour >> 8) & 0xff) / 256F;
			        float f7 = (colour & 0xff) / 256F;
			        worldrenderer.pos((double)i / 2 + 43, (double)0, (double)-90D).tex(86D / 256D, 98D / 256D).color(f5, f6, f7, 1.0F).endVertex();
			        worldrenderer.pos((double)i / 2 + 19, (double)0, (double)-90D).tex(62D / 256D, 98D / 256D).color(f5, f6, f7, 1.0F).endVertex();
			        tessellator.draw();
					
					GlStateManager.shadeModel(GL11.GL_FLAT);
					GlStateManager.disableBlend();
					GlStateManager.enableAlpha();
					
					if(!currentTaggers.isEmpty()){
						int x = i / 2 - 65;
						Iterator<PlayerData> ii = currentTaggers.iterator();
						while(ii.hasNext()){
							PlayerData data = ii.next();
						    if(data.playerName.equalsIgnoreCase(playerUsername))continue;	
							mc.renderEngine.bindTexture(DownloadedTextures.getSpecialResource(data.getUUID()).getFace2d());
							GL11.glPushMatrix();
							GL11.glColor4d(1, 1, 1, 1);
							GL11.glTranslated(x, 5, 0);
							GL11.glScaled(2.0D, 2.0D, 0);
							
							Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 8, 8, 8, 8);
							GL11.glPopMatrix();
							x-=20;
						}
					}
					GlStateManager.depthMask(true);
					GlStateManager.enableDepth();
					GlStateManager.enableAlpha();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					
					//Draw the team scores
					fontrenderer.drawString(getBestScore() + "", i / 2 - 27 - fontrenderer.getStringWidth(getBestScore() + "")/2, 9, 0x000000);
					fontrenderer.drawString(getBestScore() + "", i / 2 - 28 - fontrenderer.getStringWidth(getBestScore() + "")/2, 8, 0xffffff);
					
					fontrenderer.drawString(getData(playerUsername).score + "", i / 2 + 30 - fontrenderer.getStringWidth(playerData.score + "")/2, 9, 0x000000);
					fontrenderer.drawString(getData(playerUsername).score + "", i / 2 + 29 - fontrenderer.getStringWidth(playerData.score + "")/2, 8, 0xffffff);
				}
				
				/*int secondsLeft = !this.midGame() ? 0 : ((int)(round !=null ? round.timeLimit : time)-(int)time) / 20;
				int realTime = secondsLeft;
				int minutesLeft = secondsLeft / 60;
				int hoursLeft = minutesLeft / 60;
				secondsLeft = secondsLeft % 60;
				minutesLeft = minutesLeft % 60;
				EnumChatFormatting color = realTime <= 10 ? ChatHandler.DARK_RED : realTime <= 30 ? ChatHandler.RED : realTime <= 60 ? ChatHandler.GOLD : ChatHandler.WHITE;
				String timeLeft = color + (hoursLeft > 0 ? hoursLeft+":" : "") + (minutesLeft < 10 ? "0" + minutesLeft : minutesLeft) + ":" + (secondsLeft < 10 ? "0" + secondsLeft : secondsLeft);
				if(round !=null && (round.infiTime || round.timeLimit <=0))timeLeft = "Infinite";
				if(timeout)timeLeft = "Timeout";
				mc.fontRenderer.drawString(timeLeft, i / 2 - mc.fontRenderer.getStringWidth(timeLeft) / 2, /*30*//*1, 0xffffff);*/
				
				GlStateManager.depthMask(true);
				GlStateManager.enableDepth();
				GlStateManager.enableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                
				
				fontrenderer.drawString(playerData.timesIT + "", i / 2 - 7, 9, 0x000000);
				int itsecondsLeft = (int) playerData.timeIt;
				int itminutesLeft = (int)playerData.timeIt / 60;
				int ithoursLeft = itminutesLeft / 60;
				itsecondsLeft = itsecondsLeft % 60;
				itminutesLeft = itminutesLeft % 60;
				String ittimeLeft = (ithoursLeft > 0 ? ithoursLeft : "") + (ithoursLeft > 0 ? ":" : "") + (itminutesLeft < 10 ? "0" + itminutesLeft : itminutesLeft) + ":" + (itsecondsLeft < 10 ? "0" + itsecondsLeft : itsecondsLeft);
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(i / 2 - 7, 18, 0);
				GlStateManager.scale(0.6, 0.6, 0);
				fontrenderer.drawString(ittimeLeft + "", 0, 0, 0x000000);
				GlStateManager.popMatrix();
				
				GlStateManager.popMatrix();
			}
		}
	}
	
	@SubscribeEvent
	public void serverTick(ServerTickEvent event){
		if(event.phase == Phase.END){
			//update
		}
	}
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent.EntityInteract event){
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();
		if(target !=null && player !=null && !player.worldObj.isRemote){
			if(target instanceof EntityPlayer){
			   EntityPlayer tPlayer = (EntityPlayer) target;
               tagPlayer(tPlayer.getName(), player.getName());
			}
			else if(target instanceof EntityLiving){
				   EntityLiving tEnt = (EntityLiving) target;
				   if(tEnt.hasCustomName()){
					  if(getData(tEnt.getCustomNameTag()) !=null)
	                  tagPlayer(tEnt.getCustomNameTag(), player.getName());
				   }
			}
		}
	}
	
	public void renderFlag(int color, float flagAngle){
		GlStateManager.pushMatrix();
		
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
		double height = 1.8;
		
        //GlStateManager.translate(0.3, -height/2, -0.1);
		
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/iron_block.png"));
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        double offset = 0.45;
        worldrenderer.pos(offset, height, offset).tex(0, 1).endVertex();
        worldrenderer.pos(1-offset, height, offset).tex(1, 1).endVertex();
        worldrenderer.pos(1-offset, 0D, offset).tex(1, 0).endVertex();
        worldrenderer.pos(offset, 0D, offset).tex(0, 0).endVertex();
        
        worldrenderer.pos(1-offset, height, offset).tex(0, 1).endVertex();
        worldrenderer.pos(1-offset, height, 1-offset).tex(1, 1).endVertex();
        worldrenderer.pos(1-offset, 0D, 1-offset).tex(1, 0).endVertex();
        worldrenderer.pos(1-offset, 0D, offset).tex(0, 0).endVertex();
        
        worldrenderer.pos(offset, 0D, 1-offset).tex(0, 1).endVertex();
        worldrenderer.pos(1-offset, 0D, 1-offset).tex(1, 1).endVertex();
        worldrenderer.pos(1-offset, height, 1-offset).tex(1, 0).endVertex();
        worldrenderer.pos(offset, height, 1-offset).tex(0, 0).endVertex();
        
        worldrenderer.pos(offset, 0D, offset).tex(0, 1).endVertex();
        worldrenderer.pos(offset, 0D, 1-offset).tex(1, 1).endVertex();
        worldrenderer.pos(offset, height, 1-offset).tex(1, 0).endVertex();
        worldrenderer.pos(offset, height, offset).tex(0, 0).endVertex();
        
        worldrenderer.pos(offset, 0, offset).tex(0, 1).endVertex();
        worldrenderer.pos(1-offset, 0, offset).tex(1, 1).endVertex();
        worldrenderer.pos(1-offset, 0, 1-offset).tex(1, 0).endVertex();
        worldrenderer.pos(offset, 0, 1-offset).tex(0, 0).endVertex();
        
        worldrenderer.pos(1-offset, height, offset).tex(0, 1).endVertex();
        worldrenderer.pos(offset, height, offset).tex(1, 1).endVertex();
        worldrenderer.pos(offset, height, 1-offset).tex(1, 0).endVertex();
        worldrenderer.pos(1-offset, height, 1-offset).tex(0, 0).endVertex();
        
        tessellator.draw();
        
        GlStateManager.pushMatrix();
        
        //FLAG ROT
        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.rotate(flagAngle, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        //FLAG END
        
        /*GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);*/
        //GlStateManager.shadeModel(GL11.GL_SMOOTH);
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/wool_colored_white.png"));
        
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int colour = color;
        float r = ((colour >> 16) & 0xff) / 256F;
        float g = ((colour >> 8) & 0xff) / 256F;
        float b = (colour & 0xff) / 256F;
        worldrenderer.pos(offset+0.05, 0.6, offset).tex(0, 1).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05, 0.6, -0.3).tex(1, 1).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05, 0, -0.3).tex(1, 0).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05, 0, offset).tex(0, 0).color(r, g, b, 1.0F).endVertex();
        
        worldrenderer.pos(offset+0.05+0.00001, 0, offset).tex(0, 1).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05+0.00001, 0, -0.3).tex(1, 1).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05+0.00001, 0.6, -0.3).tex(1, 0).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(offset+0.05+0.00001, 0.6, offset).tex(0, 0).color(r, g, b, 1.0F).endVertex();
        tessellator.draw();
        
        //GlStateManager.shadeModel(GL11.GL_FLAT);
		/*GlStateManager.disableBlend();
		GlStateManager.enableAlpha();*/
		GlStateManager.popMatrix();
		
        GlStateManager.popMatrix();
	}
}
