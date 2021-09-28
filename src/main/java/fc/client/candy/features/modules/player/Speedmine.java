package fc.client.candy.features.modules.player;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.BlockEvent;
import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.RenderUtil;
import fc.client.candy.util.Timer;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {
  private static Speedmine INSTANCE = new Speedmine();
  
  private final Timer timer = new Timer();
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET));
  
  public Setting<Float> damage = register(new Setting("Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.DAMAGE)));
  
  public Setting<Float> startDamage = register(new Setting("Start Damage", Float.valueOf(0.2F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.BREAKER)));
  
  public Setting<Float> endDamage = register(new Setting("End Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.BREAKER)));
  
  public Setting<Integer> breakDelay = register(new Setting("Break Delay", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(10), v -> (this.mode.getValue() == Mode.BREAKER)));
  
  public Setting<Boolean> webSwitch = register(new Setting("WebSwitch", Boolean.valueOf(false)));
  
  public Setting<Boolean> doubleBreak = register(new Setting("DoubleBreak", Boolean.valueOf(false)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Float> lineWidth = register(new Setting("Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  public BlockPos currentPos;
  
  public IBlockState currentBlockState;
  
  private int breakTick = 0;
  
  private BlockPos lastBreak = null;
  
  private EnumFacing facing = null;
  
  private boolean before = false;
  
  public Speedmine() {
    super("Speedmine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  public static Speedmine getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Speedmine(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onTick() {
    if (this.currentPos != null)
      if (!mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
        this.currentPos = null;
        this.currentBlockState = null;
      } else if (((Boolean)this.webSwitch.getValue()).booleanValue() && this.currentBlockState.getBlock() == Blocks.WEB && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe) {
        InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
      }  
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    mc.playerController.blockHitDelay = 0;
    if (this.mode.getValue() == Mode.BREAKER)
      if (this.lastBreak != null)
        if (this.before) {
          if (!(getBlock(this.lastBreak) instanceof net.minecraft.block.BlockAir))
            breakerBreak(); 
        } else if (getBlock(this.lastBreak) instanceof net.minecraft.block.BlockAir) {
          this.before = true;
        }   
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && this.currentPos != null && this.currentBlockState.getBlock() == Blocks.OBSIDIAN) {
      Color color = new Color(this.timer.passedMs((int)(2000.0F * OyVey.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0F * OyVey.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
      RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
    } 
  }
  
  @SubscribeEvent
  public void onBlockEvent(BlockEvent event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1F)
      mc.playerController.isHittingBlock = true; 
    if (event.getStage() == 4) {
      if (BlockUtil.canBreak(event.pos)) {
        mc.playerController.isHittingBlock = false;
        switch ((Mode)this.mode.getValue()) {
          case PACKET:
            if (this.currentPos == null) {
              this.currentPos = event.pos;
              this.currentBlockState = mc.world.getBlockState(this.currentPos);
              this.timer.reset();
            } 
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            event.setCanceled(true);
            break;
          case DAMAGE:
            if (mc.playerController.curBlockDamageMP < ((Float)this.damage.getValue()).floatValue())
              break; 
            mc.playerController.curBlockDamageMP = 1.0F;
            break;
          case BREAKER:
            breakerAlgo(event);
            breakerBreak();
          case INSTANT:
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            mc.playerController.onPlayerDestroyBlock(event.pos);
            mc.world.setBlockToAir(event.pos);
            break;
        } 
      } 
      BlockPos above;
      if (((Boolean)this.doubleBreak.getValue()).booleanValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0D) {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
        mc.playerController.onPlayerDestroyBlock(above);
        mc.world.setBlockToAir(above);
      } 
    } 
  }
  
  private void breakerAlgo(BlockEvent event) {
    if (this.lastBreak == null || event.pos.getX() != this.lastBreak.getX() || event.pos.getY() != this.lastBreak.getY() || event.pos.getZ() != this.lastBreak.getZ()) {
      mc.player.swingArm(EnumHand.MAIN_HAND);
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
      this.before = true;
      this.lastBreak = event.pos;
      this.facing = event.facing;
    } 
    if (((Integer)this.breakDelay.getValue()).intValue() <= this.breakTick++) {
      breakerBreak();
      event.setCanceled(true);
      this.breakTick = 0;
    } 
  }
  
  private void breakerBreak() {
    mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.lastBreak, this.facing));
  }
  
  private Block getBlock(BlockPos b) {
    return mc.world.getBlockState(b).getBlock();
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    PACKET, DAMAGE, INSTANT, BREAKER;
  }
}
