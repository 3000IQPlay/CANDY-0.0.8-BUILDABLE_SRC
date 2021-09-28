package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiChunkBan extends Module {
  public AntiChunkBan() {
    super("AntiChunkBan", "AAAAAA", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    Packet p = event.getPacket();
    if (p instanceof net.minecraft.network.play.server.SPacketBlockChange) {
      event.setCanceled(true);
    } else if (p instanceof net.minecraft.network.play.server.SPacketUpdateTileEntity) {
      event.setCanceled(true);
    } else if (p instanceof net.minecraft.network.play.server.SPacketMultiBlockChange) {
      event.setCanceled(true);
    } else if (p instanceof SPacketChunkData) {
      SPacketChunkData data = (SPacketChunkData)p;
      Chunk chunk = mc.world.getChunk(data.getChunkX(), data.getChunkZ());
      if (chunk.isLoaded())
        event.setCanceled(true); 
    } else if (p instanceof net.minecraft.network.play.server.SPacketBlockAction) {
      event.setCanceled(true);
    } 
  }
}
