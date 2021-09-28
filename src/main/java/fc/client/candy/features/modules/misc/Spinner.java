package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Spinner extends Module {
  private Setting<Float> speed = register(new Setting("Rotate Speed", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(30.0F)));
  
  public Spinner() {
    super("Spinner", "Go burrrr", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer p = (CPacketPlayer)event.getPacket();
      p.yaw += ((Float)this.speed.getValue()).floatValue();
    } 
  }
}
