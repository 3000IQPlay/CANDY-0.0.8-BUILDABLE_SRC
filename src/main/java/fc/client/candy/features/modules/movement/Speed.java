package fc.client.candy.features.modules.movement;

import fc.client.candy.features.modules.Module;

public class Speed extends Module {
  public Speed() {
    super("Speed", "Speed.", Module.Category.MOVEMENT, true, false, false);
  }
  
  public String getDisplayInfo() {
    return "Strafe";
  }
}
