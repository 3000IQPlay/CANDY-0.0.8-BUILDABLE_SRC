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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends Module {
  public static boolean isPlacing = false;
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> disable = register(new Setting("AutoDisable", Boolean.valueOf(false)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> lowerbody = register(new Setting("Feet", Boolean.valueOf(true)));
  
  private final Setting<Boolean> upperBody = register(new Setting("Face", Boolean.valueOf(false)));
  
  private final Timer timer = new Timer();
  
  public EntityPlayer target;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private boolean isSneaking;
  
  private int lastHotbarSlot;
  
  private int placements = 0;
  
  private boolean smartRotate = false;
  
  private BlockPos startPos = null;
  
  public AutoWeb() {
    super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      return; 
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.lastHotbarSlot = mc.player.inventory.currentItem;
  }
  
  public void onTick() {
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
    switchItem(true);
  }
  
  private void doTrap() {
    if (check())
      return; 
    doWebTrap();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void doWebTrap() {
    List<Vec3d> placeTargets = getPlacements();
    placeList(placeTargets);
  }
  
  private List<Vec3d> getPlacements() {
    ArrayList<Vec3d> list = new ArrayList<>();
    Vec3d baseVec = this.target.getPositionVector();
    if (((Boolean)this.lowerbody.getValue()).booleanValue())
      list.add(baseVec); 
    if (((Boolean)this.upperBody.getValue()).booleanValue())
      list.add(baseVec.add(0.0D, 1.0D, 0.0D));
    return list;
  }
  
  private void placeList(List<Vec3d> list) {
    list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
    list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
    for (Vec3d vec3d3 : list) {
      BlockPos position = new BlockPos(vec3d3);
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if (placeability != 3 && placeability != 1)
        continue; 
      placeBlock(position);
    } 
  }
  
  private boolean check() {
    isPlacing = false;
    this.didPlace = false;
    this.placements = 0;
    int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
    if (isOff())
      return true; 
    if (((Boolean)this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    if (obbySlot == -1) {
      Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Webs in hotbar disabling...");
      toggle();
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    switchItem(true);
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.target = getTarget(10.0D);
    return (this.target == null || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()));
  }
  
  private EntityPlayer getTarget(double range) {
    EntityPlayer target = null;
    double distance = Math.pow(range, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, range) || player.isInWeb || OyVey.speedManager.getPlayerSpeed(player) > 30.0D)
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
    if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(6.0D) && switchItem(false)) {
      isPlacing = true;
      this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  private boolean switchItem(boolean back) {
    boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, InventoryUtil.Switch.NORMAL, BlockWeb.class);
    this.switchedItem = value[0];
    return value[1];
  }
}
