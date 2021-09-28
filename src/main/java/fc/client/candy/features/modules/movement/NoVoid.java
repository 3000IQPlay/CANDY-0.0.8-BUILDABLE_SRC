package fc.client.candy.features.modules.movement;

import fc.client.candy.features.modules.Module;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class NoVoid extends Module {
  public NoVoid() {
    super("NoVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (!mc.player.noClip && mc.player.posY <= 0.0D) {
      RayTraceResult trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(mc.player.posX, 0.0D, mc.player.posZ), false, false, false);
      if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        return; 
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      if (mc.player.getRidingEntity() != null)
        mc.player.getRidingEntity().setVelocity(0.0D, 0.0D, 0.0D); 
    } 
  }
}
