package fc.client.candy.features.modules.misc;

import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class BlockSpam extends Module {
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(2.0F), Float.valueOf(15.0F)));
  
  private Setting<Float> Yrange = register(new Setting("YRange", Float.valueOf(3.0F), Float.valueOf(1.0F), Float.valueOf(10.0F)));
  
  private Setting<Integer> maxPlace = register(new Setting("Max Place", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(10)));
  
  private Setting<Boolean> noLiquid = register(new Setting("No Liquid", Boolean.valueOf(true)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Float> lineWidth = register(new Setting("Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private BlockPos currentPos = null;
  
  public BlockSpam() {
    super("BlockSpam", "spamming block", Module.Category.MISC, true, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && this.currentPos != null) {
      Color color = new Color(230, 0, 0);
      RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
      this.currentPos = null;
    } 
  }
  
  public void onTick() {
    if (nullCheck())
      return; 
    if (!(mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemBlock) && !(mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemSkull))
      return; 
    List<BlockPos> queue = new ArrayList<>();
    int r = (int)Math.ceil(((Float)this.range.getValue()).floatValue());
    int ry = (int)Math.ceil(((Float)this.Yrange.getValue()).floatValue());
    for (int x = r * -1; x < r; x++) {
      for (int y = ry * -1; y < 1; y++) {
        for (int z = r * -1; z < r; z++) {
          BlockPos pos = (new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(x, y, z);
          if (BlockUtil.getFirstFacing(pos) != null)
            if ((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getDistance(pos.getX(), pos.getY(), pos.getZ()) >= 3.0D)
              if (getBlock(pos) == Blocks.AIR || (
                getBlock(pos) instanceof net.minecraft.block.BlockLiquid && !((Boolean)this.noLiquid.getValue()).booleanValue()))
                queue.add(pos);   
        } 
      } 
    } 
    for (int i = 0; i < ((Integer)this.maxPlace.getValue()).intValue(); i++) {
      if (queue.size() == 0)
        return; 
      BlockPos nearest = queue.stream().min(Comparator.comparing(b -> Double.valueOf(mc.player.getDistance(b.getX(), b.getY(), b.getZ())))).orElse(null);
      for (Entity e : mc.world.loadedEntityList) {
        if (e.getDistance(nearest.getX(), nearest.getY(), nearest.getZ()) < 2.0D);
      } 
      BlockUtil.placeBlock(nearest, EnumHand.MAIN_HAND, true, false, false);
      this.currentPos = nearest;
    } 
  }
  
  public void onDisable() {
    this.currentPos = null;
  }
  
  private Block getBlock(BlockPos b) {
    return mc.world.getBlockState(b).getBlock();
  }
}
