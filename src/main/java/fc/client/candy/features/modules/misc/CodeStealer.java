package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.features.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CodeStealer extends Module {
  public CodeStealer() {
    super("CodeStealer", "Steal code", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0) {
      String suffix = "[PacketSendEvent]";
      if (event.getPacket() instanceof CPacketInput) {
        CPacketInput packet = (CPacketInput)event.getPacket();
        SendMessage(suffix + "Type:CPacketInput is jump :" + packet.isJumping() + " is sneak :" + packet.isSneaking());
      } 
      if (event.getPacket() instanceof CPacketPlayer) {
        CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        SendMessage(suffix + "Type:CPacketPlayer x:" + packet.x + " y:" + packet.y + " z:" + packet.z + " yaw:" + packet.yaw + " pitch:" + packet.pitch + " moving:" + packet.moving + " onGround:" + packet.onGround + " rotating:" + packet.rotating);
      } 
      if (event.getPacket() instanceof CPacketPlayerAbilities) {
        CPacketPlayerAbilities packet = (CPacketPlayerAbilities)event.getPacket();
        SendMessage(suffix + "Type:CPacketPlayerAbilities allowfly:" + packet.isAllowFlying() + " isflying:" + packet.isFlying() + " creativeMode:" + packet.isCreativeMode());
      } 
      if (event.getPacket() instanceof CPacketPlayerDigging) {
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        SendMessage(suffix + "Type:CPacketPlayerDigging Action:" + packet.getAction().name() + " Position:" + packet.getPosition() + " Facing:" + packet.getFacing());
      } 
      if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
        CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem)event.getPacket();
        SendMessage(suffix + " Type:CPacketPlayerTryUseItem Hand:" + packet.getHand());
      } 
      if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
        CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
        SendMessage(suffix + " Type:CPacketPlayerTryUseItemOnBlock BlockPos:" + packet.position + " FacingX:" + packet.facingX + " FacingY:" + packet.facingY + " FacingZ" + packet.facingZ);
      } 
      if (event.getPacket() instanceof CPacketEntityAction) {
        CPacketEntityAction packet = (CPacketEntityAction)event.getPacket();
        SendMessage(suffix + " Type:CPacketEntityAction Action:" + packet.getAction().name());
      } 
      if (event.getPacket() instanceof CPacketUseEntity) {
        CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
        SendMessage(suffix + " Type:CPacketUseEntity EntityID:" + packet.entityId + " Action:" + packet.action.name() + " hitVec:" + packet.hitVec);
      } 
    } 
  }
}
