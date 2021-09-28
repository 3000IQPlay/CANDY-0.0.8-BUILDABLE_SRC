package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketCanceller extends Module {
  private Setting<Boolean> input = register(new Setting("Input", Boolean.valueOf(false)));
  
  private Setting<Boolean> player = register(new Setting("Player", Boolean.valueOf(false)));
  
  private Setting<Boolean> abilities = register(new Setting("Abilities", Boolean.valueOf(false)));
  
  private Setting<Boolean> digging = register(new Setting("Digging", Boolean.valueOf(false)));
  
  private Setting<Boolean> useitem = register(new Setting("TryUseItem", Boolean.valueOf(false)));
  
  private Setting<Boolean> useitemOnblock = register(new Setting("TryUseItemOnBlock", Boolean.valueOf(false)));
  
  private Setting<Boolean> entity = register(new Setting("EntityAction", Boolean.valueOf(false)));
  
  private Setting<Boolean> useEntity = register(new Setting("UseEntity", Boolean.valueOf(false)));
  
  private Setting<Boolean> vehicle = register(new Setting("Vehicle", Boolean.valueOf(false)));
  
  private Setting<Boolean> chat = register(new Setting("SendChat", Boolean.valueOf(false)));
  
  private Setting<Boolean> animation = register(new Setting("Animation", Boolean.valueOf(false)));
  
  public PacketCanceller() {
    super("PacketCanceller", "Cancel packets", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0) {
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketInput && ((Boolean)this.input.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && ((Boolean)this.player.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerAbilities && ((Boolean)this.abilities.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerDigging && ((Boolean)this.digging.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItem && ((Boolean)this.useitem.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock && ((Boolean)this.useitemOnblock.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketEntityAction && ((Boolean)this.entity.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketUseEntity && ((Boolean)this.useEntity.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketVehicleMove && ((Boolean)this.vehicle.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketChatMessage && ((Boolean)this.chat.getValue()).booleanValue())
        event.setCanceled(true); 
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation && ((Boolean)this.animation.getValue()).booleanValue())
        event.setCanceled(true); 
    } 
  }
}
