package fc.client.candy.manager;

import fc.client.candy.features.Feature;
import fc.client.candy.util.MathUtil;
import fc.client.candy.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotationManager extends Feature {
  private float yaw;
  
  private float pitch;
  
  public void updateRotations() {
    this.yaw = mc.player.rotationYaw;
    this.pitch = mc.player.rotationPitch;
  }
  
  public void restoreRotations() {
    mc.player.rotationYaw = this.yaw;
    mc.player.rotationYawHead = this.yaw;
    mc.player.rotationPitch = this.pitch;
  }
  
  public void setPlayerRotations(float yaw, float pitch) {
    mc.player.rotationYaw = yaw;
    mc.player.rotationYawHead = yaw;
    mc.player.rotationPitch = pitch;
  }
  
  public void setPlayerYaw(float yaw) {
    mc.player.rotationYaw = yaw;
    mc.player.rotationYawHead = yaw;
  }
  
  public void lookAtPos(BlockPos pos) {
    float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));
    setPlayerRotations(angle[0], angle[1]);
  }
  
  public void lookAtVec3d(Vec3d vec3d) {
    float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
    setPlayerRotations(angle[0], angle[1]);
  }
  
  public void lookAtVec3d(double x, double y, double z) {
    Vec3d vec3d = new Vec3d(x, y, z);
    lookAtVec3d(vec3d);
  }
  
  public void lookAtEntity(Entity entity) {
    float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
    setPlayerRotations(angle[0], angle[1]);
  }
  
  public void setPlayerPitch(float pitch) {
    mc.player.rotationPitch = pitch;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public float getPitch() {
    return this.pitch;
  }
  
  public void setPitch(float pitch) {
    this.pitch = pitch;
  }
  
  public int getDirection4D() {
    return RotationUtil.getDirection4D();
  }
  
  public String getDirection4D(boolean northRed) {
    return RotationUtil.getDirection4D(northRed);
  }
}
