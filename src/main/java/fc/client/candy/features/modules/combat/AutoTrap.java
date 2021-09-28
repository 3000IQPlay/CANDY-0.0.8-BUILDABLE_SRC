package fc.client.candy.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.MathUtil;
import fc.client.candy.util.Timer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTrap extends Module {
  public static boolean isPlacing = false;
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> antiScaffold = register(new Setting("AntiScaffold", Boolean.valueOf(false)));
  
  private final Setting<Boolean> antiStep = register(new Setting("AntiStep", Boolean.valueOf(false)));
  
  private final Timer timer = new Timer();
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private final Timer retryTimer = new Timer();
  
  public EntityPlayer target;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private boolean isSneaking;
  
  private int lastHotbarSlot;
  
  private int placements = 0;
  
  private boolean smartRotate = false;
  
  private BlockPos startPos = null;
  
  public AutoTrap() {
    super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      return; 
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.retries.clear();
  }
  
  public void onTick() {
    if (fullNullCheck())
      return; 
    this.smartRotate = false;
    doTrap();
  }
  
  public String getDisplayInfo() {
    if (this.target != null)
      return this.target.getName(); 
    return null;
  }
  
  public void onDisable() {
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
  }
  
  private void doTrap() {
    if (check())
      return; 
    doStaticTrap();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void doStaticTrap() {
    List<Vec3d> placeTargets = EntityUtil.targets(this.target.getPositionVector(), ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), false, false, false, ((Boolean)this.raytrace.getValue()).booleanValue());
    placeList(placeTargets);
  }
  
  private void placeList(List<Vec3d> list) {
    list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
    list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
    for (Vec3d vec3d3 : list) {
      BlockPos position = new BlockPos(vec3d3);
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if (placeability == 1 && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4)) {
        placeBlock(position);
        this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
        this.retryTimer.reset();
        continue;
      } 
      if (placeability != 3)
        continue; 
      placeBlock(position);
    } 
  }
  
  private boolean check() {
    isPlacing = false;
    this.didPlace = false;
    this.placements = 0;
    int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (obbySlot2 == -1)
      toggle(); 
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (isOff())
      return true; 
    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (obbySlot == -1) {
      Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
      disable();
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.target = getTarget(10.0D, true);
    return (this.target == null || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()));
  }
  
  private EntityPlayer getTarget(double range, boolean trapped) {
    EntityPlayer target = null;
    double distance = Math.pow(range, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, range) || (trapped && EntityUtil.isTrapped(player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), false, false, false)) || OyVey.speedManager.getPlayerSpeed(player) > 10.0D)
        continue; 
      if (target == null) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) >= distance)
        continue; 
      target = player;
      distance = mc.player.getDistanceSq((Entity)player);
    } 
    return target;
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(5.0D)) {
      isPlacing = true;
      int originalSlot = mc.player.inventory.currentItem;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (obbySlot == -1 && eChestSot == -1)
        toggle(); 
      if (this.smartRotate) {
        mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
        mc.playerController.updateController();
        this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, true, this.isSneaking);
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
      } else {
        mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
        mc.playerController.updateController();
        this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), true, this.isSneaking);
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
      } 
      this.didPlace = true;
      this.placements++;
    } 
  }
}
