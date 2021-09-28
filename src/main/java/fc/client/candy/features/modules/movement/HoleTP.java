package fc.client.candy.features.modules.movement;

import fc.client.candy.OyVey;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import java.util.Comparator;
import net.minecraft.util.math.BlockPos;

public class HoleTP extends Module {
  private final Setting<Float> range = register(new Setting("Range", Float.valueOf(0.5F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  public HoleTP() {
    super("HoleTP", "like TP", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    BlockPos hole = OyVey.holeManager.calcHoles().stream().min(Comparator.comparing(p -> Double.valueOf(mc.player.getDistance(p.getX(), p.getY(), p.getZ())))).orElse(null);
    if (hole != null) {
      if (mc.player.getDistance(hole.getX(), hole.getY(), hole.getZ()) < ((Float)this.range.getValue()).floatValue() + 1.5D) {
        mc.player.setPosition(hole.getX() + 0.5D, hole.getY(), hole.getZ() + 0.5D);
        mc.player.setPosition(hole.getX() + 0.5D, hole.getY(), hole.getZ() + 0.5D);
        SendMessage("Accepting teleport...");
      } else {
        SendMessage("Out of range! disabling...");
      } 
    } else {
      SendMessage("Not found hole! disabling...");
    } 
    disable();
  }
}
