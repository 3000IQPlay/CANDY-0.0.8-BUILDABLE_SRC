package fc.client.candy.features.modules.world;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class StashLogger extends Module {
  private Setting<Boolean> donkey = register(new Setting("Donkey", Boolean.valueOf(true)));
  
  private Setting<Boolean> portal = register(new Setting("Portal", Boolean.valueOf(true)));
  
  private Setting<Boolean> bed = register(new Setting("Portal", Boolean.valueOf(true)));
  
  private Setting<Integer> shulker = register(new Setting("Shulker Count", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10)));
  
  private Setting<Integer> chest = register(new Setting("Chest Count", Integer.valueOf(7), Integer.valueOf(1), Integer.valueOf(30)));
  
  private Setting<Integer> obsidian = register(new Setting("Obsidian Count", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(10)));
  
  private Setting<Integer> range = register(new Setting("Range", Integer.valueOf(200), Integer.valueOf(10), Integer.valueOf(300)));
  
  private List<Chunk> chunks = new ArrayList<>();
  
  public StashLogger() {
    super("StashLogger", "When found stash Will log it position", Module.Category.MISC, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.world == null)
      return; 
    if (this.chunks.contains(new Chunk((World)mc.world, (int)Math.ceil(mc.player.posX), (int)Math.ceil(mc.player.posZ))))
      return; 
    int Shulker = 0;
    int Chest = 0;
    int Obsidian = 0;
    boolean foundPortal = false;
    boolean foundBed = false;
    boolean foundDonkey = false;
    for (int x = ((Integer)this.range.getValue()).intValue() * -1; x < ((Integer)this.range.getValue()).intValue(); x++) {
      for (int z = ((Integer)this.range.getValue()).intValue() * -1; z < ((Integer)this.range.getValue()).intValue(); z++) {
        for (int y = 0; y < 255; y++) {
          BlockPos currentPos = new BlockPos(x + mc.player.posX, y + mc.player.posY, z + mc.player.posZ);
          IBlockState block = getBlock(currentPos);
          if (block.getBlock().getRegistryName().getPath().contains("shulker"))
            Shulker++; 
          if (block.getBlock() == Blocks.CHEST || block
            .getBlock() == Blocks.TRAPPED_CHEST)
            Chest++; 
          if (block.getBlock() == Blocks.OBSIDIAN)
            Obsidian++; 
          if (block.getBlock() == Blocks.PORTAL && ((Boolean)this.portal.getValue()).booleanValue())
            foundPortal = true; 
          if (block.getBlock() == Blocks.BED && ((Boolean)this.bed.getValue()).booleanValue())
            foundBed = true; 
        } 
      } 
    } 
    if (((Boolean)this.donkey.getValue()).booleanValue())
      if (mc.world.loadedEntityList.stream().filter(e -> e instanceof net.minecraft.entity.passive.AbstractChestHorse)
        .min(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).orElse(null) != null)
        foundDonkey = true;  
    if (Shulker > ((Integer)this.shulker.getValue()).intValue() || Chest > ((Integer)this.chest
      .getValue()).intValue() || Obsidian > ((Integer)this.obsidian
      .getValue()).intValue() || foundPortal || foundBed || foundDonkey) {
      this.chunks.add(new Chunk((World)mc.world, (int)Math.ceil(mc.player.posX), (int)Math.ceil(mc.player.posZ)));
      SendMessage("Stash found at : x=" + mc.player.posX + " y=" + mc.player.posY + " z=" + mc.player.posZ);
    } 
  }
  
  private IBlockState getBlock(BlockPos b) {
    return mc.world.getBlockState(b);
  }
}
