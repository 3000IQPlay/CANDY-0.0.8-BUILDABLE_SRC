package fc.client.candy.features.modules.misc;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifier extends Module {
  private static ChatModifier INSTANCE = new ChatModifier();
  
  public Setting<Boolean> clean = register(new Setting("NoChatBackground", Boolean.valueOf(false), "Cleans your chat"));
  
  public Setting<Boolean> infinite = register(new Setting("InfiniteChat", Boolean.valueOf(false), "Makes your chat infinite."));
  
  public boolean check;
  
  public ChatModifier() {
    super("BetterChat", "Modifies your chat", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  public static ChatModifier getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ChatModifier(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketChatMessage) {
      String s = ((CPacketChatMessage)event.getPacket()).getMessage();
      this.check = !s.startsWith(OyVey.commandManager.getPrefix());
    } 
  }
}
