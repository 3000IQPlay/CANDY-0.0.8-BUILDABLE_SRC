package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.modules.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {
  public ChatSuffix() {
    super("ChatSuffix", "Im hungry yet.", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketChatMessage) {
      if (((CPacketChatMessage)event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage)event.getPacket()).getMessage().startsWith(Command.getCommandPrefix()))
        return; 
      String msg = ((CPacketChatMessage)event.getPacket()).getMessage();
      String newMsg = msg + " / " + toUnicode("candy");
      if (newMsg.length() > 255)
        return; 
      ((CPacketChatMessage)event.getPacket()).message = newMsg;
    } 
  }
  
  public String toUnicode(String s) {
    return s.toLowerCase()
      .replace("a", "ᴀ")
      .replace("b", "ʙ")
      .replace("c", "ᴄ")
      .replace("d", "ᴅ")
      .replace("e", "ᴇ")
      .replace("f", "ꜰ")
      .replace("g", "ɢ")
      .replace("h", "ʜ")
      .replace("i", "ɪ")
      .replace("j", "ᴊ")
      .replace("k", "ᴋ")
      .replace("l", "ʟ")
      .replace("m", "ᴍ")
      .replace("n", "ɴ")
      .replace("o", "ᴏ")
      .replace("p", "ᴘ")
      .replace("q", "ǫ")
      .replace("r", "ʀ")
      .replace("s", "ꜱ")
      .replace("t", "ᴛ")
      .replace("u", "ᴜ")
      .replace("v", "ᴠ")
      .replace("w", "ᴡ")
      .replace("x", "ˣ")
      .replace("y", "ʏ")
      .replace("z", "ᴢ");
  }
}
