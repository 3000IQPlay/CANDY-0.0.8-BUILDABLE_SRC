package fc.client.candy.features.modules.combat;

import fc.client.candy.event.events.UpdateWalkingPlayerEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.Timer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Selftrap extends Module {
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Integer> disableTime = register(new Setting("DisableTime", Integer.valueOf(200), Integer.valueOf(50), Integer.valueOf(300)));
  
  private final Setting<Boolean> disable = register(new Setting("AutoDisable", Boolean.valueOf(true)));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false)));
  
  private final Timer offTimer = new Timer();
  
  private final Timer timer = new Timer();
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private final Timer retryTimer = new Timer();
  
  private int blocksThisTick = 0;
  
  private boolean isSneaking;
  
  private boolean hasOffhand = false;
  
  public Selftrap() {
    super("Selftrap", "Lure your enemies in!", Module.Category.COMBAT, true, false, true);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.offTimer.reset();
  }
  
  public void onTick() {
    if (isOn() && (((Integer)this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean)this.rotate.getValue()).booleanValue()))
      doHoleFill(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (isOn() && event.getStage() == 0 && ((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue())
      doHoleFill(); 
  }
  
  public void onDisable() {
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.retries.clear();
    this.hasOffhand = false;
  }
  
  private void doHoleFill() {
    if (check())
      return; 
    for (BlockPos position : getPositions()) {
      int placeability = BlockUtil.isPositionPlaceable(position, false);
      if (placeability == 1 && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4)) {
        placeBlock(position);
        this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
      } 
      if (placeability != 3)
        continue; 
      placeBlock(position);
    } 
  }
  
  private List<BlockPos> getPositions() {
    ArrayList<BlockPos> positions = new ArrayList<>();
    positions.add(new BlockPos(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ));
    int placeability = BlockUtil.isPositionPlaceable(positions.get(0), false);
    switch (placeability) {
      case 0:
        return new ArrayList<>();
      case 3:
        return positions;
      case 1:
        if (BlockUtil.isPositionPlaceable(positions.get(0), false, false) == 3)
          return positions; 
      case 2:
        positions.add(new BlockPos(mc.player.posX + 1.0D, mc.player.posY + 1.0D, mc.player.posZ));
        positions.add(new BlockPos(mc.player.posX + 1.0D, mc.player.posY + 2.0D, mc.player.posZ));
        break;
    } 
    positions.sort(Comparator.comparingDouble(Vec3i::getY));
    return positions;
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.blocksThisTick < ((Integer)this.blocksPerTick.getValue()).intValue()) {
      boolean smartRotate = (((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue());
      int originalSlot = mc.player.inventory.currentItem;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (obbySlot == -1 && eChestSot == -1)
        toggle(); 
      mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
      mc.playerController.updateController();
      this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      mc.player.inventory.currentItem = originalSlot;
      mc.playerController.updateController();
      this.timer.reset();
      this.blocksThisTick++;
    } 
  }
  
  private boolean check() {
    if (fullNullCheck()) {
      disable();
      return true;
    } 
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (obbySlot == -1 && eChestSot == -1)
      toggle(); 
    this.blocksThisTick = 0;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (!EntityUtil.isSafe((Entity)mc.player)) {
      this.offTimer.reset();
      return true;
    } 
    if (((Boolean)this.disable.getValue()).booleanValue() && this.offTimer.passedMs(((Integer)this.disableTime.getValue()).intValue())) {
      disable();
      return true;
    } 
    return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
  }
}
