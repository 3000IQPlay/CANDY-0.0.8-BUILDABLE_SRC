package fc.client.candy.features.modules.render;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;

public class HandChams extends Module {
  private static HandChams INSTANCE = new HandChams();
  
  public Setting<RenderMode> mode = register(new Setting("Mode", RenderMode.SOLID));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
  
  public HandChams() {
    super("HandChams", "Changes your hand color.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static HandChams getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new HandChams(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public enum RenderMode {
    SOLID, WIREFRAME;
  }
}
