package fc.client.candy.features.modules.render;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;

public class SmallShield extends Module {
  private static SmallShield INSTANCE = new SmallShield();
  
  public Setting<Float> offX = register(new Setting("OffHandX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public Setting<Float> offY = register(new Setting("OffHandY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public Setting<Float> mainX = register(new Setting("MainHandX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public Setting<Float> mainY = register(new Setting("MainHandY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public SmallShield() {
    super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static SmallShield getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new SmallShield(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
