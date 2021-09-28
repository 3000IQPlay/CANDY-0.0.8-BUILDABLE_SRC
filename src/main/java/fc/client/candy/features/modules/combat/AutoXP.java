//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.combat;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class AutoXP extends Module {
  private int Ticktimer = 0;
  
  private int ArmorTimer = 0;
  
  private boolean isDone = false;
  
  private int fixprogress = 0;
  
  private final Setting<Integer> Tick = register(new Setting("XPDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
  
  private final Setting<Integer> Throw = register(new Setting("Throw", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(5)));
  
  private final Setting<Integer> ArmorDelay = register(new Setting("ArmorDelay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private final Setting<Integer> FixTo = register(new Setting("FixTo", Integer.valueOf(90), Integer.valueOf(10), Integer.valueOf(100)));
  
  private final Setting<Boolean> FastMode = register(new Setting("FastMode", Boolean.valueOf(true)));
  
  private final Setting<Boolean> Packet = register(new Setting("Packet", Boolean.valueOf(true)));
  
  private Map<Float, Float> Armor;
  
  public AutoXP() {
    super("AutoXP", "Automatically repairs your armors", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (mc.player == null)
      return; 
    this.Armor = new HashMap<>();
    if (findXP() == -1) {
      SendMessage("You don't have XP!");
      toggle();
    } 
    this.Ticktimer = 0;
    this.isDone = false;
    this.fixprogress = 0;
  }
  
  public void onDisable() {}
  
  public void onUpdate() {
    try {
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(0.0F, 90.0F, true));
      if (this.Ticktimer < ((Integer)this.Tick.getValue()).intValue()) {
        this.Ticktimer++;
      } else if (!this.isDone) {
        if (!setNeedFixArmor()) {
          SendMessage("Done! disabling...");
          this.isDone = true;
          return;
        } 
        int slot = findXP();
        if (slot == -1) {
          SendMessage("Missing XP! disabling...");
          this.isDone = true;
          return;
        } 
        mc.player.inventory.currentItem = slot;
        for (int i = 0; i < ((Integer)this.Throw.getValue()).intValue(); i++) {
          if (((Boolean)this.Packet.getValue()).booleanValue()) {
            mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
          } else {
            mc.playerController.processRightClick((EntityPlayer)mc.player, (World)mc.world, EnumHand.MAIN_HAND);
          } 
        } 
        this.Ticktimer = 0;
      } 
      if (this.isDone) {
        if (this.ArmorTimer < ((Integer)this.ArmorDelay.getValue()).intValue()) {
          this.ArmorTimer++;
          return;
        } 
        this.ArmorTimer = 0;
        float armor = ((Float)this.Armor.keySet().stream().toArray()[this.fixprogress]).floatValue();
        float temp = ((Float)this.Armor.values().stream().toArray()[this.fixprogress]).floatValue();
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, (int)temp, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, (int)armor, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        if (this.fixprogress == this.Armor.size()) {
          disable();
        } else {
          this.fixprogress++;
        } 
      } 
    } catch (Exception ex) {
      SendMessage("Error : " + ex.toString());
      disable();
    } 
  }
  
  public int findXP() {
    for (int i = 0; i < 9; i++) {
      ItemStack item = mc.player.inventory.getStackInSlot(i);
      if (item.getItem() == Items.EXPERIENCE_BOTTLE)
        return i; 
    } 
    return -1;
  }
  
  public boolean setNeedFixArmor() {
    boolean result = false;
    for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++) {
      ItemStack armor = (ItemStack)mc.player.inventoryContainer.getInventory().get(i);
      if (armor.getItem() instanceof net.minecraft.item.ItemArmor && i > 4 && i < 9) {
        float p = (armor.getMaxDamage() - armor.getItemDamage()) / armor.getMaxDamage() * 100.0F;
        if (p < ((Integer)this.FixTo.getValue()).intValue()) {
          result = true;
        } else if (((Boolean)this.FastMode.getValue()).booleanValue()) {
          int slot = getfreeslot();
          mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          this.Armor.put(Float.valueOf(i), Float.valueOf(slot));
        } 
      } 
    } 
    return result;
  }
  
  public int getfreeslot() {
    for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++) {
      if (i > 8 && i < 36)
        if (((ItemStack)mc.player.inventoryContainer.getInventory().get(i)).getItem() == Items.AIR)
          return i;  
    } 
    return -1;
  }
}
