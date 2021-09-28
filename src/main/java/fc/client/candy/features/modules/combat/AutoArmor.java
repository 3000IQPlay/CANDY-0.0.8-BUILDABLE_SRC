//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.combat;

import fc.client.candy.OyVey;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.Timer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Module {
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
  
  private final Setting<Boolean> curse = register(new Setting("Vanishing", Boolean.valueOf(false)));
  
  private final Setting<Boolean> mendingTakeOff = register(new Setting("AutoMend", Boolean.valueOf(false)));
  
  private final Setting<Integer> closestEnemy = register(new Setting("Enemy", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> repair = register(new Setting("Repair%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> actions = register(new Setting("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12)));
  
  private final Timer timer = new Timer();
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  private final List<Integer> doneSlots = new ArrayList<>();
  
  boolean flag;
  
  public AutoArmor() {
    super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
  }
  
  public void onLogin() {
    this.timer.reset();
  }
  
  public void onDisable() {
    this.taskList.clear();
    this.doneSlots.clear();
    this.flag = false;
  }
  
  public void onLogout() {
    this.taskList.clear();
    this.doneSlots.clear();
  }
  
  public void onTick() {
    if (fullNullCheck() || (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)))
      return; 
    if (this.taskList.isEmpty()) {
      if (((Boolean)this.mendingTakeOff.getValue()).booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.world.playerEntities.stream().noneMatch(e -> (e != mc.player && !OyVey.friendManager.isFriend(e.getName()) && mc.player.getDistance((Entity)e) <= ((Integer)this.closestEnemy.getValue()).intValue())) && !this.flag) {
        int takeOff = 0;
        for (Map.Entry<Integer, ItemStack> armorSlot : getArmor().entrySet()) {
          ItemStack stack = armorSlot.getValue();
          float percent = ((Integer)this.repair.getValue()).intValue() / 100.0F;
          int dam = Math.round(stack.getMaxDamage() * percent);
          int goods;
          if (dam >= (goods = stack.getMaxDamage() - stack.getItemDamage()))
            continue; 
          takeOff++;
        } 
        if (takeOff == 4)
          this.flag = true; 
        if (!this.flag) {
          ItemStack itemStack1 = mc.player.inventoryContainer.getSlot(5).getStack();
          if (!itemStack1.isEmpty) {
            float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
            int dam2 = Math.round(itemStack1.getMaxDamage() * f);
            int goods2;
            if (dam2 < (goods2 = itemStack1.getMaxDamage() - itemStack1.getItemDamage()))
              takeOffSlot(5); 
          } 
          ItemStack itemStack2 = mc.player.inventoryContainer.getSlot(6).getStack();
          if (!itemStack2.isEmpty) {
            float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
            int dam3 = Math.round(itemStack2.getMaxDamage() * f);
            int goods3;
            if (dam3 < (goods3 = itemStack2.getMaxDamage() - itemStack2.getItemDamage()))
              takeOffSlot(6); 
          } 
          ItemStack itemStack3 = mc.player.inventoryContainer.getSlot(7).getStack();
          float percent = ((Integer)this.repair.getValue()).intValue() / 100.0F;
          int dam = Math.round(itemStack3.getMaxDamage() * percent);
          int goods;
          if (!itemStack3.isEmpty && dam < (goods = itemStack3.getMaxDamage() - itemStack3.getItemDamage()))
            takeOffSlot(7); 
          ItemStack itemStack4 = mc.player.inventoryContainer.getSlot(8).getStack();
          if (!itemStack4.isEmpty) {
            float f = ((Integer)this.repair.getValue()).intValue() / 100.0F;
            int dam4 = Math.round(itemStack4.getMaxDamage() * f);
            int goods4;
            if (dam4 < (goods4 = itemStack4.getMaxDamage() - itemStack4.getItemDamage()))
              takeOffSlot(8); 
          } 
        } 
        return;
      } 
      this.flag = false;
      ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
      int slot4;
      if (helm.getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
        getSlotOn(5, slot4); 
      int slot3;
      ItemStack chest;
      if ((chest = mc.player.inventoryContainer.getSlot(6).getStack()).getItem() == Items.AIR && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
        getSlotOn(6, slot3); 
      int slot2;
      ItemStack legging;
      if ((legging = mc.player.inventoryContainer.getSlot(7).getStack()).getItem() == Items.AIR && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
        getSlotOn(7, slot2); 
      int slot;
      ItemStack feet;
      if ((feet = mc.player.inventoryContainer.getSlot(8).getStack()).getItem() == Items.AIR && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, ((Boolean)this.curse.getValue()).booleanValue(), true)) != -1)
        getSlotOn(8, slot); 
    } 
    if (this.timer.passedMs((int)(((Integer)this.delay.getValue()).intValue() * OyVey.serverManager.getTpsFactor()))) {
      if (!this.taskList.isEmpty())
        for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
          InventoryUtil.Task task = this.taskList.poll();
          if (task != null)
            task.run(); 
        }  
      this.timer.reset();
    } 
  }
  
  private void takeOffSlot(int slot) {
    if (this.taskList.isEmpty()) {
      int target = -1;
      for (Iterator<Integer> iterator = InventoryUtil.findEmptySlots(true).iterator(); iterator.hasNext(); ) {
        int i = ((Integer)iterator.next()).intValue();
        if (this.doneSlots.contains(Integer.valueOf(target)))
          continue; 
        target = i;
        this.doneSlots.add(Integer.valueOf(i));
      } 
      if (target != -1) {
        this.taskList.add(new InventoryUtil.Task(slot));
        this.taskList.add(new InventoryUtil.Task(target));
        this.taskList.add(new InventoryUtil.Task());
      } 
    } 
  }
  
  private void getSlotOn(int slot, int target) {
    if (this.taskList.isEmpty()) {
      this.doneSlots.remove(Integer.valueOf(target));
      this.taskList.add(new InventoryUtil.Task(target));
      this.taskList.add(new InventoryUtil.Task(slot));
      this.taskList.add(new InventoryUtil.Task());
    } 
  }
  
  private Map<Integer, ItemStack> getArmor() {
    return getInventorySlots(5, 8);
  }
  
  private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
    HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
    while (current <= last) {
      fullInventorySlots.put(Integer.valueOf(current), mc.player.inventoryContainer.getInventory().get(current));
      current++;
    } 
    return fullInventorySlots;
  }
}
