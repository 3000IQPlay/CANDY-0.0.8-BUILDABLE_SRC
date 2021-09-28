package fc.client.candy.features.modules.misc;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SwapEsc extends Module {
  public Setting<Integer> delay = new Setting("Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10));
  
  private int timer = 0;
  
  private int stage = 0;
  
  private int swapTo = 0;
  
  public SwapEsc() {
    super("SwapESC", "WTF?", Module.Category.MISC, true, false, false);
  }
  
  public void onEnable() {
    if (mc.player == null)
      return; 
    this.timer = 0;
    this.stage = 0;
  }
  
  public void onUpdate() {
    if (mc.player == null) {
      disable();
      return;
    } 
    needPearl();
    if (this.swapTo == -1) {
      disable();
      return;
    } 
    int fruit = findFruit();
    int pearl = findPearl();
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, fruit, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, pearl, 0, ClickType.SWAP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, fruit, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    disable();
  }
  
  public int findFruit() {
    NonNullList<ItemStack> nonNullList = mc.player.inventoryContainer.getInventory();
    for (int i = 0; i < nonNullList.size(); i++) {
      if (((ItemStack)nonNullList.get(i)).getItem() == Items.CHORUS_FRUIT)
        return i; 
    } 
    return -1;
  }
  
  public int findPearl() {
    NonNullList<ItemStack> nonNullList = mc.player.inventoryContainer.getInventory();
    for (int i = 0; i < nonNullList.size(); i++) {
      if (((ItemStack)nonNullList.get(i)).getItem() == Items.ENDER_PEARL)
        return i; 
    } 
    return -1;
  }
  
  public void needPearl() {
    int p = -1;
    int c = -1;
    for (int i = 0; i < 9; i++) {
      NonNullList<ItemStack> nonNullList = mc.player.inventory.mainInventory;
      if (((ItemStack)nonNullList.get(i)).getItem() == Items.ENDER_PEARL)
        p = 1; 
      if (((ItemStack)nonNullList.get(i)).getItem() == Items.CHORUS_FRUIT)
        c = 1; 
    } 
    if (p == -1 && c == 1) {
      this.swapTo = 1;
      return;
    } 
    if (p == 1 && c == -1) {
      this.swapTo = 0;
      return;
    } 
    this.swapTo = -1;
  }
}
