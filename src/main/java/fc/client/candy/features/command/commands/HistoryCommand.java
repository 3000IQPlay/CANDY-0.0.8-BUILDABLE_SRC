package fc.client.candy.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.features.command.Command;
import fc.client.candy.util.PlayerUtil;
import java.util.List;
import java.util.UUID;

public class HistoryCommand extends Command {
  public HistoryCommand() {
    super("history", new String[] { "<player>" });
  }
  
  public void execute(String[] commands) {
    List<String> names;
    UUID uuid;
    if (commands.length == 1 || commands.length == 0)
      sendMessage(ChatFormatting.RED + "Please specify a player."); 
    try {
      uuid = PlayerUtil.getUUIDFromName(commands[0]);
    } catch (Exception e) {
      sendMessage("An error occured.");
      return;
    } 
    try {
      names = PlayerUtil.getHistoryOfNames(uuid);
    } catch (Exception e) {
      sendMessage("An error occured.");
      return;
    } 
    if (names != null) {
      sendMessage(commands[0] + "Â´s name history:");
      for (String name : names)
        sendMessage(name); 
    } else {
      sendMessage("No names found.");
    } 
  }
}
