package fc.client.candy.features.modules.player;

import fc.client.candy.features.modules.Module;
import fc.client.candy.util.InventoryUtil;
import net.minecraft.item.ItemExpBottle;

public class FastPlace extends Module {
  public FastPlace() {
    super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (InventoryUtil.holdingItem(ItemExpBottle.class))
      mc.rightClickDelayTimer = 0; 
  }
}
