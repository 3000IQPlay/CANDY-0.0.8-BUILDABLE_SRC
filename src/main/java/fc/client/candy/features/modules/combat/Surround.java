package fc.client.candy.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.Timer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {
  public static boolean isPlacing = false;
  
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(12), Integer.valueOf(1), Integer.valueOf(20)));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Boolean> noGhost = register(new Setting("PacketPlace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> center = register(new Setting("TPCenter", Boolean.valueOf(false)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Timer timer = new Timer();
  
  private final Timer retryTimer = new Timer();
  
  private final Set<Vec3d> extendingBlocks = new HashSet<>();
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private int isSafe;
  
  private BlockPos startPos;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private int lastHotbarSlot;
  
  private boolean isSneaking;
  
  private int placements = 0;
  
  private int extenders = 1;
  
  private int obbySlot = -1;
  
  private boolean offHand = false;
  
  public Surround() {
    super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    if (((Boolean)this.center.getValue()).booleanValue())
      OyVey.positionManager.setPositionPacket(this.startPos.getX() + 0.5D, this.startPos.getY(), this.startPos.getZ() + 0.5D, true, true, true); 
    this.retries.clear();
    this.retryTimer.reset();
  }
  
  public void onTick() {
    doFeetPlace();
  }
  
  public void onDisable() {
    if (nullCheck())
      return; 
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
  }
  
  public String getDisplayInfo() {
    switch (this.isSafe) {
      case 0:
        return ChatFormatting.RED + "Unsafe";
      case 1:
        return ChatFormatting.YELLOW + "Safe";
    } 
    return ChatFormatting.GREEN + "Safe";
  }
  
  private void doFeetPlace() {
    if (check())
      return; 
    if (!EntityUtil.isSafe((Entity)mc.player, 0, true)) {
      this.isSafe = 0;
      placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)mc.player, 0, true), true, false, false);
    } else if (!EntityUtil.isSafe((Entity)mc.player, -1, false)) {
      this.isSafe = 1;
      placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)mc.player, -1, false), false, false, true);
    } else {
      this.isSafe = 2;
    } 
    processExtendingBlocks();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void processExtendingBlocks() {
    if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
      Vec3d[] array = new Vec3d[2];
      int i = 0;
      Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
      while (iterator.hasNext()) {
        Vec3d vec3d = iterator.next();
        i++;
      } 
      int placementsBefore = this.placements;
      if (areClose(array) != null)
        placeBlocks(areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(areClose(array), 0, true), true, false, true); 
      if (placementsBefore < this.placements)
        this.extendingBlocks.clear(); 
    } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
      this.extendingBlocks.clear();
    } 
  }
  
  private Vec3d areClose(Vec3d[] vec3ds) {
    int matches = 0;
    for (Vec3d vec3d : vec3ds) {
      for (Vec3d pos : EntityUtil.getUnsafeBlockArray((Entity)mc.player, 0, true)) {
        if (vec3d.equals(pos))
          matches++; 
      } 
    } 
    if (matches == 2)
      return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1])); 
    return null;
  }
  
  private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
    boolean gotHelp = true;
    for (Vec3d vec3d : vec3ds) {
      gotHelp = true;
      BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
      switch (BlockUtil.isPositionPlaceable(position, false)) {
        case 1:
          if (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4) {
            placeBlock(position);
            this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
            this.retryTimer.reset();
            break;
          } 
          if (OyVey.speedManager.getSpeedKpH() != 0.0D || isExtending || this.extenders >= 1)
            break; 
          placeBlocks(mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
          this.extendingBlocks.add(vec3d);
          this.extenders++;
          break;
        case 2:
          if (!hasHelpingBlocks)
            break; 
          gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
        case 3:
          if (gotHelp)
            placeBlock(position); 
          if (!isHelping)
            break; 
          return true;
      } 
    } 
    return false;
  }
  
  private boolean check() {
    if (nullCheck())
      return true; 
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (obbySlot == -1 && eChestSot == -1)
      toggle(); 
    this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
    isPlacing = false;
    this.didPlace = false;
    this.extenders = 1;
    this.placements = 0;
    this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (isOff())
      return true; 
    if (this.retryTimer.passedMs(2500L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
      Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
      disable();
      return true;
    } 
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != this.obbySlot && mc.player.inventory.currentItem != echestSlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerTick.getValue()).intValue()) {
      int originalSlot = mc.player.inventory.currentItem;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (obbySlot == -1 && eChestSot == -1)
        toggle(); 
      isPlacing = true;
      mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
      mc.playerController.updateController();
      this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.noGhost.getValue()).booleanValue(), this.isSneaking);
      mc.player.inventory.currentItem = originalSlot;
      mc.playerController.updateController();
      this.didPlace = true;
      this.placements++;
    } 
  }
}
