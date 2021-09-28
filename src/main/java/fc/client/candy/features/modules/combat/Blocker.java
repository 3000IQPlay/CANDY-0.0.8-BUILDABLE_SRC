package fc.client.candy.features.modules.combat;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import java.util.Comparator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Blocker extends Module {
  private Setting<Boolean> piston = register(new Setting("Piston", Boolean.valueOf(true)));
  
  private Setting<Boolean> cev = register(new Setting("CevBreaker", Boolean.valueOf(true)));
  
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(6.1F), Float.valueOf(1.0F), Float.valueOf(10.0F)));
  
  private Setting<Boolean> packetPlace = register(new Setting("PacketPlace", Boolean.valueOf(true)));
  
  private BlockPos b_piston = null;
  
  private BlockPos b_cev = null;
  
  public Blocker() {
    super("Blocker", "Block gs attack lmao", Module.Category.COMBAT, true, false, false);
  }
  
  public void onTick() {
    if (mc.player == null)
      return; 
    try {
      int ob = findMaterials(Blocks.OBSIDIAN);
      if (ob == -1)
        return; 
      BlockPos p_pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
      if (((Boolean)this.piston.getValue()).booleanValue()) {
        BlockPos[] offset = { new BlockPos(2, 1, 0), new BlockPos(-2, 1, 0), new BlockPos(0, 1, 2), new BlockPos(0, 1, -2) };
        for (int y = 0; y < 4; y++) {
          for (int i = 0; i < offset.length; i++) {
            BlockPos pre_piston = p_pos.add((Vec3i)offset[i].add(0, y, 0));
            if (getBlock(pre_piston).getBlock() == Blocks.PISTON || 
              getBlock(pre_piston).getBlock() == Blocks.STICKY_PISTON)
              this.b_piston = pre_piston; 
          } 
        } 
        if (this.b_piston != null) {
          if (getBlock(this.b_piston).getBlock() == Blocks.AIR) {
            if (mc.player.getDistance(this.b_piston.getX(), this.b_piston.getY(), this.b_piston.getZ()) > ((Float)this.range.getValue()).floatValue())
              return; 
            int oldslot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = ob;
            mc.playerController.updateController();
            BlockUtil.placeBlock(this.b_piston, EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
            mc.player.inventory.currentItem = oldslot;
            mc.playerController.updateController();
          } 
          if (getBlock(this.b_piston).getBlock() == Blocks.OBSIDIAN || mc.player.getDistance(this.b_piston.getX(), this.b_piston.getY(), this.b_piston.getZ()) > ((Float)this.range.getValue()).floatValue())
            this.b_piston = null; 
        } 
      } 
      if (((Boolean)this.cev.getValue()).booleanValue()) {
        BlockPos p_player = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        Entity crystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof net.minecraft.entity.item.EntityEnderCrystal).filter(c -> (c.posY > p_player.getY())).filter(c -> (mc.player.getDistance(c.posX, mc.player.posY, c.posZ) < 1.0D)).min(Comparator.comparing(c -> Double.valueOf(mc.player.getDistanceSq(c.posX, c.posY, c.posZ)))).orElse(null);
        if (getBlock(new BlockPos(p_player.getX(), p_player.getY() + 2, p_player.getZ())).getBlock() == Blocks.OBSIDIAN && crystal != null)
          this.b_cev = new BlockPos(crystal.posX, crystal.posY, crystal.posZ); 
        if (this.b_cev != null && 
          getBlock(this.b_cev).getBlock() == Blocks.AIR) {
          if (mc.player.getDistance(this.b_cev.getX(), this.b_cev.getY(), this.b_cev.getZ()) > ((Float)this.range.getValue()).floatValue())
            return; 
          if (crystal == null && (new BlockPos(mc.player.posX, this.b_cev
              .getY(), mc.player.posZ)).getDistance(this.b_cev.getX(), this.b_cev.getY(), this.b_cev.getZ()) < 1.0D) {
            int oldslot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = ob;
            mc.playerController.updateController();
            BlockUtil.placeBlock(this.b_cev.add(0, -1, 0), EnumHand.MAIN_HAND, true, false, false);
            BlockUtil.placeBlock(this.b_cev, EnumHand.MAIN_HAND, true, false, false);
            mc.player.inventory.currentItem = oldslot;
            mc.playerController.updateController();
            this.b_cev = null;
          } 
        } 
      } 
    } catch (Exception ex) {
      this.b_cev = null;
      this.b_piston = null;
    } 
  }
  
  private int findMaterials(Block b) {
    for (int i = 0; i < 9; i++) {
      if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock)
        if (((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == b)
          return i;  
    } 
    return -1;
  }
  
  public BlockPos getPistonPos() {
    return this.b_piston;
  }
  
  private IBlockState getBlock(BlockPos o) {
    return mc.world.getBlockState(o);
  }
}
