package fc.client.candy.features.modules.render;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;

public class NoRender extends Module {
  private Setting<Boolean> fire = register(new Setting("Fire", Boolean.valueOf(false)));
  
  private Setting<Boolean> hurtcam = register(new Setting("HurtCam", Boolean.valueOf(false)));
  
  private Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false)));
  
  private Setting<Boolean> blindness = register(new Setting("Blindness", Boolean.valueOf(false)));
  
  private Setting<Boolean> totem = register(new Setting("Totem", Boolean.valueOf(false)));
  
  private Setting<Boolean> sign = register(new Setting("Sign", Boolean.valueOf(false)));
  
  private Setting<Boolean> map = register(new Setting("Map", Boolean.valueOf(false)));
  
  private Setting<Boolean> bossHealth = register(new Setting("BossHealth", Boolean.valueOf(false)));
  
  private Setting<Boolean> table = register(new Setting("EnchantingTable", Boolean.valueOf(false)));
  
  private Setting<Boolean> beacon = register(new Setting("Beacon", Boolean.valueOf(false)));
  
  private Setting<Boolean> enderChest = register(new Setting("EnderChest", Boolean.valueOf(false)));
  
  public NoRender() {
    super("NoRender", "Yeah this is op", Module.Category.RENDER, true, false, false);
  }
  
  public boolean Fire() {
    return ((Boolean)this.fire.getValue()).booleanValue();
  }
  
  public boolean HurtCam() {
    return ((Boolean)this.hurtcam.getValue()).booleanValue();
  }
  
  public boolean armor() {
    return ((Boolean)this.armor.getValue()).booleanValue();
  }
  
  public boolean Blindness() {
    return ((Boolean)this.blindness.getValue()).booleanValue();
  }
  
  public boolean Totem() {
    return ((Boolean)this.totem.getValue()).booleanValue();
  }
  
  public boolean Sign() {
    return ((Boolean)this.sign.getValue()).booleanValue();
  }
  
  public boolean Map() {
    return ((Boolean)this.map.getValue()).booleanValue();
  }
  
  public boolean BossHealth() {
    return ((Boolean)this.bossHealth.getValue()).booleanValue();
  }
  
  public boolean EnchantingTable() {
    return ((Boolean)this.table.getValue()).booleanValue();
  }
  
  public boolean Beacon() {
    return ((Boolean)this.beacon.getValue()).booleanValue();
  }
  
  public boolean chest() {
    return ((Boolean)this.enderChest.getValue()).booleanValue();
  }
}
