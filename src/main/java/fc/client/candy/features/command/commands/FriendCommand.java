package fc.client.candy.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;
import fc.client.candy.manager.FriendManager;

public class FriendCommand extends Command {
  public FriendCommand() {
    super("friend", new String[] { "<add/del/name/clear>", "<name>" });
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      if (OyVey.friendManager.getFriends().isEmpty()) {
        sendMessage("Friend list empty D:.");
      } else {
        String f = "Friends: ";
        for (FriendManager.Friend friend : OyVey.friendManager.getFriends()) {
          try {
            f = f + friend.getUsername() + ", ";
          } catch (Exception exception) {}
        } 
        sendMessage(f);
      } 
      return;
    } 
    if (commands.length == 2) {
      switch (commands[0]) {
        case "reset":
          OyVey.friendManager.onLoad();
          sendMessage("Friends got reset.");
          return;
      } 
      sendMessage(commands[0] + (OyVey.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
      return;
    } 
    if (commands.length >= 2) {
      switch (commands[0]) {
        case "add":
          OyVey.friendManager.addFriend(commands[1]);
          sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
          return;
        case "del":
          OyVey.friendManager.removeFriend(commands[1]);
          sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
          return;
      } 
      sendMessage("Unknown Command, try friend add/del (name)");
    } 
  }
}
