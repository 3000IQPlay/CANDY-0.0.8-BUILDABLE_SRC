package fc.client.candy.features.modules.combat;

import fc.client.candy.OyVey;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.misc.AutoGG;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.MathUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class PistonCrystal extends Module {
  private Setting<redstone> redstoneType = register(new Setting("Redstone", redstone.Both));
  
  private Setting<Integer> start_delay = register(new Setting("Start Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> place_delay = register(new Setting("Place Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> crystal_delay = register(new Setting("Crystal Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> break_delay = register(new Setting("Break Delay", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(10)));
  
  private Setting<Integer> break_attempts = register(new Setting("Break Attempts", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(10)));
  
  private Setting<types> target_type = register(new Setting("Target", types.Looking));
  
  private Setting<Integer> MaxY = register(new Setting("MaxY", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5)));
  
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(5.2F), Float.valueOf(1.0F), Float.valueOf(15.0F)));
  
  private Setting<mode> trap = register(new Setting("Trap Mode", mode.Smart));
  
  private Setting<Boolean> packetPlace = register(new Setting("PacketPlace", Boolean.valueOf(false)));
  
  private Setting<arm> swingArm = register(new Setting("Swing Arm", arm.MainHand));
  
  private Setting<Boolean> antiweakness = register(new Setting("AntiWeakness", Boolean.valueOf(false)));
  
  private Setting<Boolean> toggle = register(new Setting("Toggle", Boolean.valueOf(true)));
  
  public EntityPlayer target = null;
  
  private boolean r_redstone = false;
  
  private int b_stage = 0;
  
  private BlockPos b_crystal = null;
  
  private BlockPos b_piston = null;
  
  private BlockPos b_redStone = null;
  
  private boolean p_crystal = false;
  
  private boolean p_piston = false;
  
  private boolean p_redstone = false;
  
  private boolean s_crystal = false;
  
  private boolean u_crystal = false;
  
  private int attempts = 0;
  
  private int crystalId = 0;
  
  private int trapprogress = 0;
  
  private int timer = 0;
  
  private boolean autoGG = false;
  
  private int debug_stage = -1;
  
  private List<BlockPos> c_crystal = null;
  
  private List<BlockPos> c_piston = null;
  
  private List<BlockPos> c_redStone = null;
  
  private boolean isTorch = false;
  
  private enum types {
    Nearest, Looking;
  }
  
  private enum arm {
    MainHand, OffHand, None;
  }
  
  private enum redstone {
    Block, Torch, Both;
  }
  
  private enum mode {
    Smart, Force, None;
  }
  
  public PistonCrystal() {
    super("PistonCrystal", "Sleeping... :3", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    Init();
    this.autoGG = false;
  }
  
  public void onTick() {
    try {
      int oldslot = mc.player.inventory.currentItem;
      int pickaxe = findItem(Items.DIAMOND_PICKAXE);
      int crystal = findItem(Items.END_CRYSTAL);
      int piston = findMaterials((Block)Blocks.PISTON);
      if (piston == -1)
        piston = findMaterials((Block)Blocks.STICKY_PISTON); 
      int redstone = findMaterials(Blocks.REDSTONE_TORCH);
      this.isTorch = true;
      if (this.redstoneType.getValue() == PistonCrystal.redstone.Block || (this.redstoneType.getValue() == PistonCrystal.redstone.Both && redstone == -1)) {
        redstone = findMaterials(Blocks.REDSTONE_BLOCK);
        this.isTorch = false;
      } 
      int obsidian = findMaterials(Blocks.OBSIDIAN);
      int sword = findItem(Items.DIAMOND_SWORD);
      if (((Boolean)this.antiweakness.getValue()).booleanValue() && sword == -1)
        sword = pickaxe; 
      if (pickaxe == -1 || crystal == -1 || piston == -1 || redstone == -1 || obsidian == -1) {
        SendMessage("Missing Materials! disabling...");
        disable();
        return;
      } 
      this.debug_stage = 0;
      if (this.target == null) {
        if (this.target_type.getValue() == types.Nearest)
          this
            .target = mc.world.playerEntities.stream().filter(p -> (p.entityId != mc.player.entityId)).min(Comparator.comparing(p -> Float.valueOf(mc.player.getDistance((Entity)p)))).orElse(null);
        if (this.target_type.getValue() == types.Looking);
        if (this.target == null) {
          disable();
          return;
        } 
      } 
      this.debug_stage = 1;
      if (this.b_crystal == null || this.b_piston == null || this.b_redStone == null) {
        searchSpace();
        if (this.b_crystal == null || this.b_piston == null || this.b_redStone == null) {
          if (((Boolean)this.toggle.getValue()).booleanValue()) {
            SendMessage("Not found space! disabling...");
            disable();
          } 
          return;
        } 
      } 
      this.debug_stage = 2;
      if (getRange(this.b_crystal) > ((Float)this.range.getValue()).floatValue() || 
        getRange(this.b_piston) > ((Float)this.range.getValue()).floatValue() || 
        getRange(this.b_redStone) > ((Float)this.range.getValue()).floatValue()) {
        SendMessage("Out of range! disabling...");
        if (((Boolean)this.toggle.getValue()).booleanValue())
          disable(); 
        return;
      } 
      this.debug_stage = 3;
      boolean doTrap = (this.trap.getValue() == mode.Force || (this.trap.getValue() == mode.Smart && OyVey.holeManager.isSafe(mc.player.getPosition()) && this.b_piston.getY() == mc.player.getPosition().getY() + 1));
      if (doTrap && mc.world.getBlockState(new BlockPos(this.target.posX, this.target.posY + 2.0D, this.target.posZ)).getBlock() == Blocks.AIR) {
        if (this.timer < ((Integer)this.place_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        mc.player.inventory.currentItem = obsidian;
        mc.playerController.updateController();
        BlockPos first = new BlockPos((Math.floor(this.target.posX) - this.b_crystal.getX()) * 1.0D + this.target.posX, this.b_piston.getY(), (Math.floor(this.target.posZ) - this.b_crystal.getZ()) * 1.0D + this.target.posZ);
        if (this.trapprogress == 0 || this.trapprogress == 1) {
          BlockPos pos = first;
          if (this.trapprogress == 1)
            pos = new BlockPos(first.getX(), first.getY() + 1, first.getZ()); 
          BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        } else {
          BlockUtil.placeBlock(new BlockPos(this.target.posX, this.target.posY + 2.0D, this.target.posZ), EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        } 
        this.trapprogress++;
        return;
      } 
      this.debug_stage = 4;
      ((AutoGG)OyVey.moduleManager.getModuleByClass(AutoGG.class)).addTargetedPlayer(this.target.getName());
      this.debug_stage = 5;
      if (getBlock(this.b_piston.add(0, -1, 0)).getBlock() == Blocks.AIR) {
        mc.player.inventory.currentItem = obsidian;
        mc.playerController.updateController();
        if (this.timer < ((Integer)this.place_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        BlockUtil.placeBlock(this.b_piston.add(0, -1, 0), EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        return;
      } 
      if (getBlock(this.b_redStone.add(0, -1, 0)).getBlock() == Blocks.AIR && this.isTorch) {
        mc.player.inventory.currentItem = obsidian;
        mc.playerController.updateController();
        if (this.timer < ((Integer)this.place_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        BlockUtil.placeBlock(this.b_redStone.add(0, -1, 0), EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        return;
      } 
      this.debug_stage = 6;
      if (this.r_redstone) {
        if (getBlock(this.b_redStone).getBlock() == Blocks.AIR) {
          this.r_redstone = false;
          this.b_stage = 0;
          this.p_crystal = false;
          this.p_redstone = false;
          return;
        } 
        mc.player.inventory.currentItem = pickaxe;
        mc.playerController.updateController();
        if (this.b_stage == 0) {
          mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.b_redStone, EnumFacing.DOWN));
          this.b_stage = 1;
        } else if (this.b_stage == 1) {
          mc.playerController.onPlayerDamageBlock(this.b_redStone, EnumFacing.DOWN);
        } 
        return;
      } 
      this.debug_stage = 7;
      if (!this.p_piston) {
        if (this.timer < ((Integer)this.place_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        mc.player.inventory.currentItem = piston;
        mc.playerController.updateController();
        float[] angle = MathUtil.calcAngle(new Vec3d((Vec3i)this.b_piston), new Vec3d((Vec3i)this.b_crystal));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0] + 180.0F, angle[1], true));
        BlockUtil.placeBlock(this.b_piston, EnumHand.MAIN_HAND, false, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        this.p_piston = true;
      } 
      this.debug_stage = 8;
      if (!this.p_crystal) {
        if (this.timer < ((Integer)this.crystal_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        if (crystal != 999)
          mc.player.inventory.currentItem = crystal; 
        mc.playerController.updateController();
        AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.b_crystal, EnumFacing.UP, (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
        this.p_crystal = true;
      } 
      this.debug_stage = 9;
      if (!this.p_redstone) {
        if (this.timer < ((Integer)this.place_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        mc.player.inventory.currentItem = redstone;
        mc.playerController.updateController();
        BlockUtil.placeBlock(this.b_redStone, EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
        this.p_redstone = true;
      } 
      this.debug_stage = 10;
      if (this.p_crystal && this.p_piston && this.p_redstone && !this.u_crystal) {
        if (this.timer < ((Integer)this.break_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        Entity t_crystal = mc.world.loadedEntityList.stream().filter(p -> p instanceof net.minecraft.entity.item.EntityEnderCrystal).min(Comparator.comparing(c -> Float.valueOf(this.target.getDistance(c)))).orElse(null);
        if (t_crystal == null) {
          if (this.attempts < ((Integer)this.break_attempts.getValue()).intValue()) {
            this.attempts++;
            return;
          } 
          this.attempts = 0;
          SendMessage("Not found crystal! retrying...");
          this.r_redstone = true;
          this.b_stage = 0;
          return;
        } 
        this.crystalId = t_crystal.entityId;
        if (((Boolean)this.antiweakness.getValue()).booleanValue()) {
          mc.player.inventory.currentItem = sword;
          mc.playerController.updateController();
        } 
        mc.player.connection.sendPacket((Packet)new CPacketUseEntity(t_crystal));
        if (this.swingArm.getValue() != arm.None)
          mc.player.swingArm((this.swingArm.getValue() == arm.MainHand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND); 
        this.u_crystal = true;
      } 
      this.debug_stage = 11;
      if (this.u_crystal) {
        if (this.timer < ((Integer)this.break_delay.getValue()).intValue()) {
          this.timer++;
          return;
        } 
        this.timer = 0;
        Init();
        return;
      } 
      mc.player.inventory.currentItem = oldslot;
      mc.playerController.updateController();
    } catch (Exception ex) {
      SendMessage("Has Error! : " + ex.toString());
      SendMessage("Stage : " + this.debug_stage);
      SendMessage("Trying to init...");
      Init();
      return;
    } 
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
  
  private void searchSpace() {
    BlockPos floored_pos = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
    BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    this.debug_stage = -2;
    for (int y = 0; y < ((Integer)this.MaxY.getValue()).intValue() + 1; y++) {
      for (int i = 0; i < offset.length; i++)
        sp(offset[i], y, floored_pos); 
    } 
    this.debug_stage = -3;
    this.b_crystal = this.c_crystal.stream().min(Comparator.comparing(b -> Double.valueOf(mc.player.getDistance(b.getX(), b.getY(), b.getZ())))).orElse(null);
    this.b_piston = this.c_piston.stream().min(Comparator.comparing(b -> Double.valueOf(this.b_crystal.distanceSq((Vec3i)b)))).orElse(null);
    if (this.b_piston != null)
      this.b_redStone = this.c_redStone.stream().filter(b -> (this.b_piston.getDistance(b.getX(), b.getY(), b.getZ()) < 2.0D)).min(Comparator.comparing(b -> Double.valueOf(this.b_crystal.distanceSq((Vec3i)b)))).orElse(null); 
    this.debug_stage = -4;
    if (this.b_crystal == null)
      return; 
    if (getBlock(this.b_crystal.add(0, 1, 0)).getBlock() == Blocks.PISTON_HEAD && 
      getBlock(this.b_redStone).getBlock() == getRedStoneBlock()) {
      this.r_redstone = true;
      this.b_stage = 0;
    } 
    this.debug_stage = -5;
  }
  
  private void sp(BlockPos offset, int offset_y, BlockPos enemy_pos) {
    BlockPos mypos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    boolean v_crystal = false;
    boolean v_piston = false;
    boolean v_redstone = false;
    BlockPos pre_crystal = new BlockPos(enemy_pos.getX() + offset.getX(), enemy_pos.getY() + offset.getY() + offset_y, enemy_pos.getZ() + offset.getZ());
    BlockPos pre_piston = new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 2);
    BlockPos pre_redstone = new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 3);
    if (checkBlock(getBlock(pre_crystal).getBlock()) && 
      isAir(getBlock(pre_crystal.add(0, 1, 0)).getBlock()) && 
      isAir(getBlock(pre_crystal.add(0, 2, 0)).getBlock()))
      v_crystal = true; 
    if (isAir(getBlock(pre_piston).getBlock()))
      v_piston = true; 
    if (this.isTorch) {
      BlockPos[] t_offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
      List<BlockPos> pre_redstone_place = new ArrayList<>();
      BlockPos tmp = null;
      for (int o = 0; o < t_offset.length; o++) {
        BlockPos pre_redstone_offset = pre_piston.add((Vec3i)t_offset[o]);
        if (isAir(getBlock(pre_redstone_offset).getBlock()) && (pre_redstone_offset
          .getX() != mypos.getX() || (pre_redstone_offset
          .getY() != mypos.getY() && pre_redstone_offset.getY() != mypos.getY() + 1) || pre_redstone_offset
          .getZ() != mypos.getZ()) && pre_crystal
          .getDistance(pre_redstone_offset.getX(), pre_redstone_offset.getY(), pre_redstone_offset.getZ()) > 1.0D)
          pre_redstone_place.add(pre_redstone_offset); 
      } 
      if (getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.AIR && 
        getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.OBSIDIAN)
        pre_redstone_place.add(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3)); 
      if (getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 2)).getBlock() == Blocks.AIR && 
        getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.OBSIDIAN)
        pre_redstone_place.add(new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 2)); 
      pre_redstone = pre_redstone_place.stream().min(Comparator.comparing(b -> Double.valueOf(mc.player.getDistance(b.getX(), b.getY(), b.getZ())))).orElse(null);
      if (pre_redstone != null)
        v_redstone = true; 
    } else if (isAir(getBlock(pre_redstone).getBlock())) {
      v_redstone = true;
    } 
    if (pre_piston.getX() == mypos.getX() && (pre_piston
      .getY() == mypos.getY() || pre_piston.getY() == mypos.getY() + 1) && pre_piston
      .getZ() == mypos.getZ())
      v_piston = false; 
    if (pre_redstone != null && 
      pre_redstone.getX() == mypos.getX() && (pre_redstone
      .getY() == mypos.getY() || pre_redstone.getY() == mypos.getY() + 1) && pre_redstone
      .getZ() == mypos.getZ())
      v_redstone = false; 
    if (mypos.getDistance(pre_piston.getX(), mypos.getY(), pre_piston.getZ()) < 3.1D && pre_piston
      .getY() > mypos.getY() + 1)
      v_piston = false; 
    if (getBlock(pre_crystal.add(0, 1, 0)).getBlock() == Blocks.PISTON_HEAD && (
      getBlock(pre_redstone).getBlock() == Blocks.REDSTONE_BLOCK || getBlock(pre_redstone).getBlock() == Blocks.REDSTONE_TORCH)) {
      v_piston = true;
      v_crystal = true;
      v_redstone = true;
    } 
    if (v_crystal && v_piston && v_redstone) {
      this.c_crystal.add(pre_crystal);
      this.c_piston.add(pre_piston);
      this.c_redStone.add(pre_redstone);
    } 
  }
  
  private void Init() {
    this.target = null;
    this.b_crystal = null;
    this.b_piston = null;
    this.b_redStone = null;
    this.c_crystal = new ArrayList<>();
    this.c_piston = new ArrayList<>();
    this.c_redStone = new ArrayList<>();
    this.p_crystal = false;
    this.p_piston = false;
    this.p_redstone = false;
    this.u_crystal = false;
    this.attempts = 0;
    this.r_redstone = false;
    this.b_stage = 0;
    this.trapprogress = 0;
    this.timer = 0;
    this.crystalId = 0;
    this.debug_stage = -1;
  }
  
  private float getRange(BlockPos t) {
    return (float)mc.player.getDistance(t.getX(), t.getY(), t.getZ());
  }
  
  private boolean isAir(Block b) {
    return (b == Blocks.AIR);
  }
  
  private boolean checkBlock(Block b) {
    return (b == Blocks.OBSIDIAN || b == Blocks.BEDROCK);
  }
  
  private IBlockState getBlock(BlockPos o) {
    return mc.world.getBlockState(o);
  }
  
  private double f(double v) {
    return Math.floor(v);
  }
  
  private Block getRedStoneBlock() {
    return this.isTorch ? Blocks.REDSTONE_TORCH : Blocks.REDSTONE_BLOCK;
  }
}
