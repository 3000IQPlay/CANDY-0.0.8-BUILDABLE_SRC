package fc.client.candy.manager;

import fc.client.candy.features.Feature;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.EntityUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleManager extends Feature {
  private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
  
  private final List<BlockPos> midSafety = new ArrayList<>();
  
  private List<BlockPos> holes = new ArrayList<>();
  
  public void update() {
    if (!fullNullCheck())
      this.holes = calcHoles(); 
  }
  
  public List<BlockPos> getHoles() {
    return this.holes;
  }
  
  public List<BlockPos> getMidSafety() {
    return this.midSafety;
  }
  
  public List<BlockPos> getSortedHoles() {
    this.holes.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
    return getHoles();
  }
  
  public List<BlockPos> calcHoles() {
    ArrayList<BlockPos> safeSpots = new ArrayList<>();
    this.midSafety.clear();
    List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), 6.0F, 6, false, true, 0);
    for (BlockPos pos : positions) {
      if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      boolean isSafe = true;
      boolean midSafe = true;
      for (BlockPos offset : surroundOffset) {
        Block block = mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
        if (BlockUtil.isBlockUnSolid(block))
          midSafe = false; 
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL)
          isSafe = false; 
      } 
      if (isSafe)
        safeSpots.add(pos); 
      if (!midSafe)
        continue; 
      this.midSafety.add(pos);
    } 
    return safeSpots;
  }
  
  public boolean isSafe(BlockPos pos) {
    boolean isSafe = true;
    BlockPos[] arrayOfBlockPos;
    int i;
    byte b;
    for (arrayOfBlockPos = surroundOffset, i = arrayOfBlockPos.length, b = 0; b < i; ) {
      BlockPos offset = arrayOfBlockPos[b];
      Block block = mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
      if (!BlockUtil.isBlockUnSafe(block)) {
        b++;
        continue;
      } 
      isSafe = false;
    } 
    return isSafe;
  }
}
