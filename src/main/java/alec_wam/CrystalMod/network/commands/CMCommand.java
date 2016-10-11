package alec_wam.CrystalMod.network.commands;

import net.minecraft.command.ICommandSender;

public interface CMCommand {
	public String getHelp();

    /**
     * 0 is allowed for everyone
     * 4 is most restrictive
     */
    public int getPermissionLevel();

    /**
     * @return true if this is a client-side command.
     */
    public boolean isClientSide();

    public String getCommand();

    public void execute(ICommandSender sender, String[] args);
}
