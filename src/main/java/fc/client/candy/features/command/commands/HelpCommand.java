package fc.client.candy.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;

public class HelpCommand extends Command {
  public HelpCommand() {
    super("help");
  }
  
  public void execute(String[] commands) {
    sendMessage("Commands: ");
    for (Command command : OyVey.commandManager.getCommands())
      sendMessage(ChatFormatting.GRAY + OyVey.commandManager.getPrefix() + command.getName()); 
  }
}
