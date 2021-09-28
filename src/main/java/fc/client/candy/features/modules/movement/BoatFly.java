//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.movement;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.util.math.MathHelper;

public class BoatFly extends Module {
  private Setting<Float> UpSpeed = new Setting("Up Speed", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(30.0F));
  
  private Setting<Float> Speed = new Setting("Speed", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(30.0F));
  
  public BoatFly() {
    super("BoatFly", "BIG FLY", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    if (mc.player.ridingEntity instanceof net.minecraft.entity.item.EntityBoat) {
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      mc.player.ridingEntity.motionX *= 0.0D;
      mc.player.ridingEntity.motionY *= 0.0D;
      mc.player.ridingEntity.motionZ *= 0.0D;
      if (mc.player.movementInput.forwardKeyDown) {
        float x = MathHelper.sin(GetRotationYawForCalc(0.0F)) * ((Float)this.Speed.getValue()).floatValue();
        float z = MathHelper.cos(GetRotationYawForCalc(0.0F)) * ((Float)this.Speed.getValue()).floatValue();
        mc.player.ridingEntity.motionX += x;
        mc.player.ridingEntity.motionZ += z;
      } 
      if (mc.player.movementInput.backKeyDown) {
        float x = MathHelper.sin(GetRotationYawForCalc(0.0F)) * ((Float)this.Speed.getValue()).floatValue();
        float z = MathHelper.cos(GetRotationYawForCalc(0.0F)) * ((Float)this.Speed.getValue()).floatValue();
        mc.player.ridingEntity.motionX -= x;
        mc.player.ridingEntity.motionZ -= z;
      } 
      if (mc.player.movementInput.jump) {
        mc.player.ridingEntity.motionY = ((Float)this.UpSpeed.getValue()).floatValue();
      } else {
        mc.player.ridingEntity.motionY *= 0.0D;
      } 
    } 
  }
  
  private float GetRotationYawForCalc(float offset) {
    float rotationYaw = mc.player.rotationYaw + offset;
    if (mc.player.moveForward < 0.0F)
      rotationYaw += 180.0F; 
    float n = 1.0F;
    if (mc.player.moveForward < 0.0F) {
      n = -0.5F;
    } else if (mc.player.moveForward > 0.0F) {
      n = 0.5F;
    } 
    if (mc.player.moveStrafing > 0.0F)
      rotationYaw -= 90.0F * n; 
    if (mc.player.moveStrafing < 0.0F)
      rotationYaw += 90.0F * n; 
    return rotationYaw * 0.017453292F;
  }
}
