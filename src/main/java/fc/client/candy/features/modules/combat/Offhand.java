//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.combat;

import fc.client.candy.event.events.PacketEvent;
import fc.client.candy.event.events.ProcessRightClickBlockEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.Timer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
  private static Offhand instance;
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  private final Timer timer = new Timer();
  
  private final Timer secondTimer = new Timer();
  
  public Setting<Boolean> crystal = register(new Setting("Crystal", Boolean.valueOf(true)));
  
  public Setting<Float> crystalHealth = register(new Setting("CrystalHP", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> crystalHoleHealth = register(new Setting("CrystalHoleHP", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Boolean> gapple = register(new Setting("Gapple", Boolean.valueOf(true)));
  
  public Setting<Boolean> armorCheck = register(new Setting("ArmorCheck", Boolean.valueOf(true)));
  
  public Setting<Integer> actions = register(new Setting("Packets", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4)));
  
  public Mode2 currentMode = Mode2.TOTEMS;
  
  public int totems = 0;
  
  public int crystals = 0;
  
  public int gapples = 0;
  
  public int lastTotemSlot = -1;
  
  public int lastGappleSlot = -1;
  
  public int lastCrystalSlot = -1;
  
  public int lastObbySlot = -1;
  
  public int lastWebSlot = -1;
  
  public boolean holdingCrystal = false;
  
  public boolean holdingTotem = false;
  
  public boolean holdingGapple = false;
  
  public boolean didSwitchThisTick = false;
  
  private boolean second = false;
  
  private boolean switchedForHealthReason = false;
  
  public Offhand() {
    super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
    instance = this;
  }
  
  public static Offhand getInstance() {
    if (instance == null)
      instance = new Offhand(); 
    return instance;
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
    if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.objectMouseOver != null && event.pos == mc.objectMouseOver.getBlockPos()) {
      event.setCanceled(true);
      mc.player.setActiveHand(EnumHand.OFF_HAND);
      mc.playerController.processRightClick((EntityPlayer)mc.player, (World)mc.world, EnumHand.OFF_HAND);
    } 
  }
  
  public void onUpdate() {
    if (this.timer.passedMs(50L)) {
      if (mc.player != null && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
        mc.player.setActiveHand(EnumHand.OFF_HAND);
        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
      } 
    } else if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
      mc.gameSettings.keyBindUseItem.pressed = false;
    } 
    if (nullCheck())
      return; 
    doOffhand();
    if (this.secondTimer.passedMs(50L) && this.second) {
      this.second = false;
      this.timer.reset();
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (!fullNullCheck() && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && mc.gameSettings.keyBindUseItem.isKeyDown())
      if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
        CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
        if (packet2.getHand() == EnumHand.MAIN_HAND) {
          if (this.timer.passedMs(50L)) {
            mc.player.setActiveHand(EnumHand.OFF_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
          } 
          event.setCanceled(true);
        } 
      } else {
        CPacketPlayerTryUseItem packet;
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet = (CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L))
          event.setCanceled(true); 
      }  
  }
  
  public String getDisplayInfo() {
    if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
      return "Crystals"; 
    if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      return "Totems"; 
    if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE)
      return "Gapples"; 
    return null;
  }
  
  public void doOffhand() {
    this.didSwitchThisTick = false;
    this.holdingCrystal = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
    this.holdingTotem = (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
    this.holdingGapple = (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
    this.totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
    if (this.holdingTotem)
      this.totems += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum(); 
    this.crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum();
    if (this.holdingCrystal)
      this.crystals += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum(); 
    this.gapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum();
    if (this.holdingGapple)
      this.gapples += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum(); 
    doSwitch();
  }
  
  public void doSwitch() {
    int lastSlot;
    this.currentMode = Mode2.TOTEMS;
    if (((Boolean)this.gapple.getValue()).booleanValue() && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown()) {
      this.currentMode = Mode2.GAPPLES;
    } else if (this.currentMode != Mode2.CRYSTALS && ((Boolean)this.crystal.getValue()).booleanValue() && ((EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, true) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, true) > ((Float)this.crystalHealth.getValue()).floatValue())) {
      this.currentMode = Mode2.CRYSTALS;
    } 
    if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0)
      setMode(Mode2.TOTEMS); 
    if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, true) <= ((Float)this.crystalHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, true) <= ((Float)this.crystalHoleHealth.getValue()).floatValue())) {
      if (this.currentMode == Mode2.CRYSTALS)
        this.switchedForHealthReason = true; 
      setMode(Mode2.TOTEMS);
    } 
    if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, true) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, true) > ((Float)this.crystalHealth.getValue()).floatValue())) {
      setMode(Mode2.CRYSTALS);
      this.switchedForHealthReason = false;
    } 
    if (this.currentMode == Mode2.CRYSTALS && ((Boolean)this.armorCheck.getValue()).booleanValue() && (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR))
      setMode(Mode2.TOTEMS); 
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
      return; 
    Item currentOffhandItem = mc.player.getHeldItemOffhand().getItem();
    switch (this.currentMode) {
      case TOTEMS:
        if (this.totems <= 0 || this.holdingTotem)
          break; 
        this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
        lastSlot = getLastSlot(currentOffhandItem, this.lastTotemSlot);
        putItemInOffhand(this.lastTotemSlot, lastSlot);
        break;
      case GAPPLES:
        if (this.gapples <= 0 || this.holdingGapple)
          break; 
        this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
        lastSlot = getLastSlot(currentOffhandItem, this.lastGappleSlot);
        putItemInOffhand(this.lastGappleSlot, lastSlot);
        break;
      default:
        if (this.crystals <= 0 || this.holdingCrystal)
          break; 
        this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
        lastSlot = getLastSlot(currentOffhandItem, this.lastCrystalSlot);
        putItemInOffhand(this.lastCrystalSlot, lastSlot);
        break;
    } 
    for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
      InventoryUtil.Task task = this.taskList.poll();
      if (task != null) {
        task.run();
        if (task.isSwitching())
          this.didSwitchThisTick = true; 
      } 
    } 
  }
  
  private int getLastSlot(Item item, int slotIn) {
    if (item == Items.END_CRYSTAL)
      return this.lastCrystalSlot; 
    if (item == Items.GOLDEN_APPLE)
      return this.lastGappleSlot; 
    if (item == Items.TOTEM_OF_UNDYING)
      return this.lastTotemSlot; 
    if (InventoryUtil.isBlock(item, BlockObsidian.class))
      return this.lastObbySlot; 
    if (InventoryUtil.isBlock(item, BlockWeb.class))
      return this.lastWebSlot; 
    if (item == Items.AIR)
      return -1; 
    return slotIn;
  }
  
  private void putItemInOffhand(int slotIn, int slotOut) {
    if (slotIn != -1 && this.taskList.isEmpty()) {
      this.taskList.add(new InventoryUtil.Task(slotIn));
      this.taskList.add(new InventoryUtil.Task(45));
      this.taskList.add(new InventoryUtil.Task(slotOut));
      this.taskList.add(new InventoryUtil.Task());
    } 
  }
  
  public void setMode(Mode2 mode) {
    this.currentMode = (this.currentMode == mode) ? Mode2.TOTEMS : mode;
  }
  
  public enum Mode2 {
    TOTEMS, GAPPLES, CRYSTALS;
  }
}
