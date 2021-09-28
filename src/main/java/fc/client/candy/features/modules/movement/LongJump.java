//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.movement;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;

public class LongJump extends Module {
  public Setting<Float> power = register(new Setting("Power", Float.valueOf(5.0F), Float.valueOf(2.0F), Float.valueOf(10.0F)));
  
  private double m_x = 0.0D;
  
  private double m_z = 0.0D;
  
  public LongJump() {
    super("LongJump", "How", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onEnable() {
    if (mc.player == null)
      return; 
    if (!mc.player.onGround)
      disable(); 
    mc.player.jump();
    mc.player.movementInput.moveForward++;
    this.m_x = mc.player.motionX * ((Float)this.power.getValue()).floatValue() * 1.5D;
    this.m_z = mc.player.motionZ * ((Float)this.power.getValue()).floatValue() * 1.5D;
  }
  
  public void onUpdate() {
    if (mc.player.onGround) {
      mc.player.motionX = this.m_x;
      mc.player.motionZ = this.m_z;
    } else {
      mc.player.motionX *= 0.0D;
      mc.player.motionZ *= 0.0D;
      disable();
    } 
  }
}
