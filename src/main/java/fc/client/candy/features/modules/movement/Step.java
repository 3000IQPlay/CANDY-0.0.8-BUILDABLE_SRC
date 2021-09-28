package fc.client.candy.features.modules.movement;

import fc.client.candy.OyVey;
import fc.client.candy.features.modules.Module;

public class Step extends Module {
  public Step() {
    super("Step", "Step.", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (OyVey.moduleManager.getModuleByName("speed").isOn())
      return; 
  }
}
