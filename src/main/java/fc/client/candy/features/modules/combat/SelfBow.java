package fc.client.candy.features.modules.combat;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.MathUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SelfBow extends Module {
  private Setting<Integer> bowDelay = register(new Setting("Bow Delay", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(10)));
  
  private int timer = 0;
  
  public SelfBow() {
    super("SelfBow", "Shot arrow", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    this.timer = 0;
  }
  
  public void onTick() {
    if (((Integer)this.bowDelay.getValue()).intValue() < this.timer) {
      this.timer++;
      return;
    } 
    if (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
      float[] angle = MathUtil.calcAngle(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ), (new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, 1.0D, 0.0D));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], angle[0], true));
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
      mc.player.stopActiveHand();
    } 
  }
}
