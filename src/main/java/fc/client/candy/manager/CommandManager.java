package fc.client.candy.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.features.Feature;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.command.commands.BindCommand;
import fc.client.candy.features.command.commands.ConfigCommand;
import fc.client.candy.features.command.commands.FriendCommand;
import fc.client.candy.features.command.commands.HelpCommand;
import fc.client.candy.features.command.commands.ModuleCommand;
import fc.client.candy.features.command.commands.PrefixCommand;
import fc.client.candy.features.command.commands.ReloadCommand;
import fc.client.candy.features.command.commands.ReloadSoundCommand;
import fc.client.candy.features.command.commands.UnloadCommand;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommandManager extends Feature {
  private final ArrayList<Command> commands = new ArrayList<>();
  
  private String clientMessage = "<Candy>";
  
  private String prefix = ".";
  
  public CommandManager() {
    super("Command");
    this.commands.add(new BindCommand());
    this.commands.add(new ModuleCommand());
    this.commands.add(new PrefixCommand());
    this.commands.add(new ConfigCommand());
    this.commands.add(new FriendCommand());
    this.commands.add(new HelpCommand());
    this.commands.add(new ReloadCommand());
    this.commands.add(new UnloadCommand());
    this.commands.add(new ReloadSoundCommand());
  }
  
  public static String[] removeElement(String[] input, int indexToDelete) {
    LinkedList<String> result = new LinkedList<>();
    for (int i = 0; i < input.length; i++) {
      if (i != indexToDelete)
        result.add(input[i]); 
    } 
    return result.<String>toArray(input);
  }
  
  private static String strip(String str, String key) {
    if (str.startsWith(key) && str.endsWith(key))
      return str.substring(key.length(), str.length() - key.length()); 
    return str;
  }
  
  public void executeCommand(String command) {
    String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    String name = parts[0].substring(1);
    String[] args = removeElement(parts, 0);
    for (int i = 0; i < args.length; i++) {
      if (args[i] != null)
        args[i] = strip(args[i], "\""); 
    } 
    for (Command c : this.commands) {
      if (!c.getName().equalsIgnoreCase(name))
        continue; 
      c.execute(parts);
      return;
    } 
    Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
  }
  
  public Command getCommandByName(String name) {
    for (Command command : this.commands) {
      if (!command.getName().equals(name))
        continue; 
      return command;
    } 
    return null;
  }
  
  public ArrayList<Command> getCommands() {
    return this.commands;
  }
  
  public String getClientMessage() {
    return this.clientMessage;
  }
  
  public void setClientMessage(String clientMessage) {
    this.clientMessage = clientMessage;
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
