package fc.client.candy.features.modules.misc;

import fc.client.candy.features.modules.Module;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoDupe extends Module {
  private int timer = 0;
  
  private List<BlockPos> rb = new ArrayList<>();
  
  public AutoDupe() {
    super("AutoDupe", "We should use pyro", Module.Category.MISC, true, false, false);
  }
  
  public void onEnable() {
    if (mc.player == null)
      return; 
    this.rb = new ArrayList<>();
    this.timer = 0;
    Entity cart = mc.world.loadedEntityList.stream().filter(e -> e instanceof net.minecraft.entity.item.EntityMinecart).min(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).orElse(null);
    if (cart == null) {
      SendMessage("Not found minecart! disabling...");
      toggle();
      return;
    } 
  }
  
  public void onUpdate() {
    if (this.timer < 60)
      this.timer++; 
    this.timer = 0;
    Entity cart = mc.world.loadedEntityList.stream().filter(e -> e instanceof net.minecraft.entity.item.EntityMinecart).filter(e -> checkCart(e)).min(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).orElse(null);
    if (cart == null) {
      SendMessage("Unloaded chunk! reloading...");
      return;
    } 
    mc.playerController.interactWithEntity((EntityPlayer)mc.player, cart, EnumHand.MAIN_HAND);
    this.rb.add(cart.getPosition());
  }
  
  public boolean checkCart(Entity e) {
    SendMessage("Checking...");
    SendMessage(e.getPosition().toString());
    if (this.rb.indexOf(e.getPosition()) == -1)
      return true; 
    return false;
  }
}
