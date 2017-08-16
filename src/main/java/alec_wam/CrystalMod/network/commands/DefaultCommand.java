package alec_wam.CrystalMod.network.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class DefaultCommand implements ICommand {
    protected final Map<String, CMCommand> commands = new HashMap<String, CMCommand>();

    public DefaultCommand() {
        registerCommand(new CmdHelp());
    }

    protected void registerCommand(CMCommand command) {
        commands.put(command.getCommand(), command);
    }

    public void showHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.BLUE + getName() + " <subcommand> <args>"));
        for (Map.Entry<String, CMCommand> me : commands.entrySet()) {
            sender.sendMessage(new TextComponentString("    " + me.getKey() + " " + me.getValue().getHelp()));
        }
    }

    class CmdHelp implements CMCommand {
        @Override
        public String getHelp() {
            return "";
        }

        @Override
        public int getPermissionLevel() {
            return 0;
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public String getCommand() {
            return "help";
        }

        @Override
        public void execute(ICommandSender sender, String[] args) {
            showHelp(sender);
        }
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getName() + " <subcommand> <args> (try '" + getName() + " help' for more info)";
    }

    @Override
    public List<String> getAliases() {
    	return Lists.newArrayList();
    }

    @Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    	World world = sender.getEntityWorld();
        if (args.length <= 0) {
            if (!world.isRemote) {
                showHelp(sender);
            }
        } else {
            CMCommand command = commands.get(args[0]);
            if (command == null) {
                if (!world.isRemote) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown CMCD command: " + args[0]));
                }
            } else {
                if (world.isRemote) {
                    // We are client-side. Only do client-side commands.
                    if (command.isClientSide()) {
                        command.execute(sender, args);
                    }
                } else {
                    // Server-side.
                    if (!sender.canUseCommand(command.getPermissionLevel(), getName())) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Command is not allowed!"));
                    } else {
                        command.execute(sender, args);
                    }
                }
            }
        }
    }

    @Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int p_82358_2_) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
