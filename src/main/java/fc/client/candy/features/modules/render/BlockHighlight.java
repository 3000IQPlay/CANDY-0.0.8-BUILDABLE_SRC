package fc.client.candy.features.modules.render;

import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.ColorUtil;
import fc.client.candy.util.RenderUtil;
import java.awt.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BlockHighlight extends Module {
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Integer> cAlpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public BlockHighlight() {
    super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    RayTraceResult ray = mc.objectMouseOver;
    if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
      BlockPos blockpos = ray.getBlockPos();
      RenderUtil.drawBlockOutline(blockpos, ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), false);
    } 
  }
}
