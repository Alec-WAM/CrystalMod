package alec_wam.CrystalMod.network.commands;

public class CommandCrystalMod extends DefaultCommand {

	public CommandCrystalMod(){
		super();
		registerCommand(new CmdTag());
	}
	
	@Override
	public String getName() {
		return "crystalmod";
	}
}
