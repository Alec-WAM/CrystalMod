package alec_wam.CrystalMod.util;

import java.util.List;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatUtil
{
    private static final int DELETION_ID = 2525277;
    private static int lastAdded;

    private static void sendNoSpamMessages(ITextComponent[] messages)
    {
        GuiNewChat chat = Minecraft.getInstance().ingameGUI.getChatGUI();
        for (int i = DELETION_ID + messages.length - 1; i <= lastAdded; i++)
        {
            chat.deleteChatLine(i);
        }
        for (int i = 0; i < messages.length; i++)
        {
            chat.printChatMessageWithOptionalDeletion(messages[i], DELETION_ID + i);
        }
        lastAdded = DELETION_ID + messages.length - 1;
    }

    /**
     * Returns a standard {@link TextComponentString} for the given {@link String}
     * .
     * 
     * @param s
     *        The string to wrap.
     * 
     * @return An {@link ITextComponent} containing the string.
     */
    public static ITextComponent wrap(String s)
    {
        return new TextComponentString(s);
    }

    /**
     * @see #wrap(String)
     */
    public static ITextComponent[] wrap(String... s)
    {
        ITextComponent[] ret = new ITextComponent[s.length];
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = wrap(s[i]);
        }
        return ret;
    }

    /**
     * Returns a translatable chat component for the given string and format
     * args.
     * 
     * @param s
     *        The string to format
     * @param args
     *        The args to apply to the format
     */
    public static ITextComponent wrapFormatted(String s, Object... args)
    {
        return new TextComponentTranslation(s, args);
    }

    /**
     * Simply sends the passed lines to the player in a chat message.
     * 
     * @param player
     *        The player to send the chat to
     * @param lines
     *        The lines to send
     */
    public static void sendChat(EntityPlayer player, String... lines)
    {
        sendChat(player, wrap(lines));
    }

    public static void sendChat(EntityPlayer player, List<String> list)
    {
    	for(int i = 0; i < list.size(); i++)
        sendChat(player, wrap(list.get(i)));
    }
    
    /**
     * Sends all passed chat components to the player.
     * 
     * @param player
     *        The player to send the chat lines to.
     * @param lines
     *        The {@link ITextComponent chat components} to send.yes
     */
    public static void sendChat(EntityPlayer player, ITextComponent... lines)
    {
        for (ITextComponent c : lines)
        {
            player.sendMessage(c);
        }
    }

    /**
     * Same as {@link #sendNoSpamClient(ITextComponent...)}, but wraps the
     * Strings automatically.
     * 
     * @param lines
     *        The chat lines to send
     * 
     * @see #wrap(String)
     */
    public static void sendNoSpamClient(String... lines)
    {
        sendNoSpamClient(wrap(lines));
    }

    /**
     * Skips the packet sending, unsafe to call on servers.
     * 
     * @see #sendNoSpam(EntityPlayerMP, ITextComponent...)
     */
    public static void sendNoSpamClient(ITextComponent... lines)
    {
        sendNoSpamMessages(lines);
    }

    /**
     * @see #wrap(String)
     * @see #sendNoSpam(EntityPlayer, ITextComponent...)
     */
    public static void sendNoSpam(EntityPlayer player, String... lines)
    {
        sendNoSpam(player, wrap(lines));
    }

    /**
     * First checks if the player is instanceof {@link EntityPlayerMP} before
     * casting.
     * 
     * @see #sendNoSpam(EntityPlayerMP, ITextComponent...)
     */
    public static void sendNoSpam(EntityPlayer player, ITextComponent... lines)
    {
        if (player instanceof EntityPlayerMP)
        {
            sendNoSpam((EntityPlayerMP) player, lines);
        }
    }

    /**
     * @see #wrap(String)
     * @see #sendNoSpam(EntityPlayerMP, ITextComponent...)
     */
    public static void sendNoSpam(EntityPlayerMP player, String... lines)
    {
        sendNoSpam(player, wrap(lines));
    }

    /**
     * Sends a chat message to the client, deleting past messages also sent via
     * this method.
     * 
     * Credit to RWTema for the idea
     * 
     * @param player
     *        The player to send the chat message to
     * @param lines
     *        The chat lines to send.
     */
    public static void sendNoSpam(EntityPlayerMP player, ITextComponent... lines)
    {
        if (lines.length > 0)
           CrystalModNetwork.sendTo(player, new PacketNoSpamChat(lines));
    }

    /**
     * @author tterrag1098
     * 
     *         Ripped from EnderCore (and slightly altered)
     */
    public static class PacketNoSpamChat extends AbstractPacket
    {

        private ITextComponent[] chatLines;

        public PacketNoSpamChat()
        {
            chatLines = new ITextComponent[0];
        }

        private PacketNoSpamChat(ITextComponent... lines)
        {
            // this is guaranteed to be >1 length by accessing methods
            this.chatLines = lines;
        }
        
    	public static PacketNoSpamChat decode(PacketBuffer buffer) {
    		ITextComponent[] chatLines = new ITextComponent[buffer.readInt()];
            for (int i = 0; i < chatLines.length; i++)
            {
                chatLines[i] = ITextComponent.Serializer.fromJson(buffer.readString(Integer.MAX_VALUE));
            }
    		return new PacketNoSpamChat(chatLines);
    	}

    	@Override
    	public void writeToBuffer(PacketBuffer buffer) {
    		buffer.writeInt(chatLines.length);
            for (ITextComponent c : chatLines)
            {
            	buffer.writeString(ITextComponent.Serializer.toJson(c));
            }
    	}

		@Override
		public void handleClient(EntityPlayer player) {
			sendNoSpamMessages(chatLines);
		}

		@Override
		public void handleServer(EntityPlayerMP player) {
			
		}
    }
}
