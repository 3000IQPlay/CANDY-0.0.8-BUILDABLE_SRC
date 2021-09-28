package fc.client.candy.features.command.commands;

import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;

public class UnloadCommand extends Command {
  public UnloadCommand() {
    super("unload", new String[0]);
  }
  
  public void execute(String[] commands) {
    OyVey.unload(true);
  }
}
