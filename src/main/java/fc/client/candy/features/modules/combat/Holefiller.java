package fc.client.candy.features.modules.combat;

import fc.client.candy.OyVey;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Holefiller extends Module {
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(6.1F), Float.valueOf(1.0F), Float.valueOf(15.0F)));
  
  private Setting<Float> hole_range = register(new Setting("Hole Range", Float.valueOf(3.0F), Float.valueOf(1.0F), Float.valueOf(10.0F)));
  
  private Setting<Boolean> holeonly = register(new Setting("Hole Only", Boolean.valueOf(true)));
  
  private Setting<Boolean> packetPlace = register(new Setting("Packet Place", Boolean.valueOf(false)));
  
  public Holefiller() {
    super("HoleFiller", "Fuck other hole", Module.Category.COMBAT, true, false, false);
  }
  
  public void onTick() {
    if (mc.player == null)
      return; 
    int ob = findMaterials(Blocks.OBSIDIAN);
    if (ob == -1)
      return; 
    int oldslot = mc.player.inventory.currentItem;
    BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    if (((Boolean)this.holeonly.getValue()).booleanValue())
      for (int i = 0; i < offset.length; i++) {
        BlockPos pos = (new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add((Vec3i)offset[i]);
        if (getBlock(pos).getBlock() == Blocks.AIR)
          return; 
      }  
    List<EntityPlayer> targets = mc.world.playerEntities;
    for (int t = 0; t < targets.size(); t++) {
      EntityPlayer target = targets.get(t);
      Boolean isInHole = Boolean.valueOf(true);
      for (int i = 0; i < offset.length; i++) {
        BlockPos pos = (new BlockPos(target.posX, target.posY, target.posZ)).add((Vec3i)offset[i]);
        if (getBlock(pos).getBlock() == Blocks.AIR)
          isInHole = Boolean.valueOf(false); 
      } 
      if (target.entityId != mc.player.entityId && !isInHole.booleanValue()) {
        BlockPos hole = OyVey.holeManager.calcHoles().stream().filter(h -> (mc.player.getDistance(h.getX(), h.getY(), h.getZ()) > 1.0D)).min(Comparator.comparing(h -> Double.valueOf(target.getDistance(h.getX(), h.getY(), h.getZ())))).orElse(null);
        if (hole != null)
          if (mc.player.getDistance(hole.getX(), hole.getY(), hole.getZ()) < ((Float)this.range.getValue()).floatValue() && target.getDistance(hole.getX(), hole.getY(), hole.getZ()) < ((Float)this.range.getValue()).floatValue()) {
            SendMessage("Found hole! filling...");
            mc.player.inventory.currentItem = ob;
            mc.playerController.updateController();
            BlockUtil.placeBlock(hole, EnumHand.MAIN_HAND, true, ((Boolean)this.packetPlace.getValue()).booleanValue(), false);
          }  
      } 
    } 
    mc.player.inventory.currentItem = oldslot;
  }
  
  private int findMaterials(Block b) {
    for (int i = 0; i < 9; i++) {
      if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock)
        if (((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == b)
          return i;  
    } 
    return -1;
  }
  
  private IBlockState getBlock(BlockPos b) {
    return mc.world.getBlockState(b);
  }
}
