package fc.client.candy.features.modules.combat;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class CevBreaker extends Module {
  private List<BlockPos> placeList = new ArrayList<>();
  
  private boolean placing = false;
  
  private boolean placedCrystal = false;
  
  private boolean breaking = false;
  
  private boolean broke = false;
  
  private EntityPlayer _target = null;
  
  private BlockPos b_crystal = null;
  
  private BlockPos breakPos = null;
  
  private int attempts = 0;
  
  private Setting<type> targetType = register(new Setting("Target", type.NEAREST));
  
  private Setting<mode> breakMode = register(new Setting("Break Mode", mode.Vanilla));
  
  private Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private Setting<Integer> startDelay = register(new Setting("Start Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> breakDelay = register(new Setting("Break Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> crystalDelay = register(new Setting("Crystal Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> breakAttempts = register(new Setting("Break Attempts", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(10)));
  
  private int timer = 0;
  
  public enum type {
    NEAREST, LOOKING;
  }
  
  public enum mode {
    Vanilla, Packet;
  }
  
  public CevBreaker() {
    super("CevBreaker", "Attack Ceil", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    init();
  }
  
  private void init() {
    this.placeList = new ArrayList<>();
    this._target = null;
    this.b_crystal = null;
    this.placedCrystal = false;
    this.placing = false;
    this.breaking = false;
    this.broke = false;
    this.timer = 0;
    this.attempts = 0;
  }
  
  public void onTick() {
    int pix = findItem(Items.DIAMOND_PICKAXE);
    int crystal = findItem(Items.END_CRYSTAL);
    int obby = findMaterials(Blocks.OBSIDIAN);
    if (pix == -1 || crystal == -1 || obby == -1) {
      SendMessage("Missing Materials! disabling....");
      disable();
      return;
    } 
    if (this._target == null) {
      if (this.targetType.getValue() == type.NEAREST)
        this
          
          ._target = mc.world.playerEntities.stream().filter(p -> (p.entityId != mc.player.entityId)).min(Comparator.comparing(p -> Float.valueOf(p.getDistance((Entity)mc.player)))).orElse(null);
      if (this._target == null) {
        disable();
        return;
      } 
    } 
    if (this.placeList.size() == 0 && !this.placing) {
      searchSpace();
      if (this.placeList.size() == 0) {
        SendMessage("Not found space! disabling...");
        disable();
        return;
      } 
    } 
    if (!this.placedCrystal) {
      if (this.timer < ((Integer)this.startDelay.getValue()).intValue()) {
        this.timer++;
        return;
      } 
      this.timer = 0;
      doPlace(obby, crystal);
    } else if (!this.breaking) {
      if (this.timer < ((Integer)this.breakDelay.getValue()).intValue()) {
        this.timer++;
        return;
      } 
      this.timer = 0;
      if (this.breakMode.getValue() == mode.Vanilla) {
        mc.player.inventory.currentItem = pix;
        mc.playerController.updateController();
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.playerController.onPlayerDamageBlock(this.breakPos, EnumFacing.DOWN);
      } else {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
      } 
      this.breaking = true;
    } else if (this.breaking && !this.broke) {
      if (getBlock(this.breakPos) == Blocks.AIR)
        this.broke = true; 
    } else if (this.broke) {
      if (this.timer < ((Integer)this.crystalDelay.getValue()).intValue()) {
        this.timer++;
        return;
      } 
      this.timer = 0;
      Entity bcrystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof net.minecraft.entity.item.EntityEnderCrystal).min(Comparator.comparing(c -> Float.valueOf(c.getDistance((Entity)this._target)))).orElse(null);
      if (bcrystal == null) {
        if (this.attempts < ((Integer)this.breakAttempts.getValue()).intValue()) {
          this.attempts++;
          return;
        } 
        SendMessage("Not found crystal! retrying...");
        this.placedCrystal = false;
        this.placeList.add(this.breakPos);
        this.breaking = false;
        this.broke = false;
        this.attempts = 0;
        return;
      } 
      mc.player.connection.sendPacket((Packet)new CPacketUseEntity(bcrystal));
      this.placedCrystal = false;
      this.placeList.add(this.breakPos);
      this.breaking = false;
      this.broke = false;
      this.attempts = 0;
    } 
  }
  
  private void doPlace(int obby, int crystal) {
    this.placing = true;
    if (this.placeList.size() != 0) {
      int oldslot = mc.player.inventory.currentItem;
      mc.player.inventory.currentItem = obby;
      mc.playerController.updateController();
      BlockUtil.placeBlock(this.placeList.get(0), EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), false, false);
      this.placeList.remove(0);
      mc.player.inventory.currentItem = oldslot;
    } else if (!this.placedCrystal) {
      int oldslot = mc.player.inventory.currentItem;
      if (crystal != 999)
        mc.player.inventory.currentItem = crystal; 
      mc.playerController.updateController();
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.b_crystal, EnumFacing.UP, (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
      mc.player.inventory.currentItem = oldslot;
      this.placedCrystal = true;
    } 
  }
  
  private void searchSpace() {
    BlockPos ppos = mc.player.getPosition();
    BlockPos tpos = new BlockPos(this._target.posX, this._target.posY, this._target.posZ);
    this.placeList = new ArrayList<>();
    BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    if (getBlock(new BlockPos(tpos.getX(), tpos.getY() + 3, tpos.getZ())) != Blocks.AIR || 
      getBlock(new BlockPos(tpos.getX(), tpos.getY() + 4, tpos.getZ())) != Blocks.AIR)
      return; 
    List<BlockPos> posList = new ArrayList<>();
    for (int i = 0; i < offset.length; i++) {
      BlockPos offsetPos = tpos.add((Vec3i)offset[i]);
      Block block = getBlock(offsetPos);
      if (block != Blocks.AIR && !(block instanceof net.minecraft.block.BlockLiquid))
        posList.add(offsetPos); 
    } 
    BlockPos base = posList.stream().max(Comparator.comparing(b -> Double.valueOf(this._target.getDistance(b.getX(), b.getY(), b.getZ())))).orElse(null);
    if (base == null)
      return; 
    this.placeList.add(base);
    this.placeList.add(base.add(0, 1, 0));
    this.placeList.add(base.add(0, 2, 0));
    this.placeList.add(tpos.add(0, 2, 0));
    this.breakPos = tpos.add(0, 2, 0);
    this.b_crystal = tpos.add(0, 2, 0);
  }
  
  private int findMaterials(Block b) {
    for (int i = 0; i < 9; i++) {
      if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && (
        (ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == b)
        return i; 
    } 
    return -1;
  }
  
  private int findItem(Item item) {
    if (item == Items.END_CRYSTAL && mc.player
      .getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
      return 999; 
    for (int i = 0; i < 9; i++) {
      if (mc.player.inventory.getStackInSlot(i).getItem() == item)
        return i; 
    } 
    return -1;
  }
  
  private Block getBlock(BlockPos b) {
    return mc.world.getBlockState(b).getBlock();
  }
}
