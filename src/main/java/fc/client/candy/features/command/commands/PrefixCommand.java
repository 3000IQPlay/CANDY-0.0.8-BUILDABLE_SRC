package fc.client.candy.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;

public class PrefixCommand extends Command {
  public PrefixCommand() {
    super("prefix", new String[] { "<char>" });
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + OyVey.commandManager.getPrefix());
      return;
    } 
    OyVey.commandManager.setPrefix(commands[0]);
    Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
  }
}
