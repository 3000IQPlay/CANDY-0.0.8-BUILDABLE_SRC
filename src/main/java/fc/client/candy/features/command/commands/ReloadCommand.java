package fc.client.candy.features.command.commands;

import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;

public class ReloadCommand extends Command {
  public ReloadCommand() {
    super("reload", new String[0]);
  }
  
  public void execute(String[] commands) {
    OyVey.reload();
  }
}
