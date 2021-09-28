package fc.client.candy.features.modules.render;

import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.RenderUtil;
import java.awt.Color;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP extends Module {
  private static HoleESP INSTANCE = new HoleESP();
  
  private final Setting<Integer> range = register(new Setting("RangeX", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
  
  private final Setting<Integer> rangeY = register(new Setting("RangeY", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Integer> safeRed = register(new Setting("BedrockRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> safeGreen = register(new Setting("BedrockGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> safeBlue = register(new Setting("BedrockBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> safeAlpha = register(new Setting("BedrockAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Boolean> future = register(new Setting("FutureRender", Boolean.valueOf(true)));
  
  public Setting<Boolean> fov = register(new Setting("InFov", Boolean.valueOf(true)));
  
  public Setting<Boolean> renderOwn = register(new Setting("RenderOwn", Boolean.valueOf(true)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> safecRed = register(new Setting("OL-BedrockRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> safecGreen = register(new Setting("OL-BedrockGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> safecBlue = register(new Setting("OL-BedrockBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> safecAlpha = register(new Setting("OL-BedrockAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public HoleESP() {
    super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static HoleESP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleESP(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onRender3D(Render3DEvent event) {
    assert mc.renderViewEntity != null;
    Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
    for (int x = playerPos.getX() - ((Integer)this.range.getValue()).intValue(); x < playerPos.getX() + ((Integer)this.range.getValue()).intValue(); x++) {
      for (int z = playerPos.getZ() - ((Integer)this.range.getValue()).intValue(); z < playerPos.getZ() + ((Integer)this.range.getValue()).intValue(); z++) {
        for (int y = playerPos.getY() + ((Integer)this.rangeY.getValue()).intValue(); y > playerPos.getY() - ((Integer)this.rangeY.getValue()).intValue(); y--) {
          BlockPos pos = new BlockPos(x, y, z);
          if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && (!pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) || ((Boolean)this.renderOwn.getValue()).booleanValue()) && (BlockUtil.isPosInFov(pos).booleanValue() || !((Boolean)this.fov.getValue()).booleanValue()))
            if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
              RenderUtil.drawBoxESP(((Boolean)this.future.getValue()).booleanValue() ? pos.down() : pos, new Color(((Integer)this.safeRed.getValue()).intValue(), ((Integer)this.safeGreen.getValue()).intValue(), ((Integer)this.safeBlue.getValue()).intValue(), ((Integer)this.safeAlpha.getValue()).intValue()), ((Boolean)this.outline.getValue()).booleanValue(), new Color(((Integer)this.safecRed.getValue()).intValue(), ((Integer)this.safecGreen.getValue()).intValue(), ((Integer)this.safecBlue.getValue()).intValue(), ((Integer)this.safecAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true);
            } else if (BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.down()).getBlock()) && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.east()).getBlock()) && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.west()).getBlock()) && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.south()).getBlock()) && BlockUtil.isBlockUnSafe(mc.world.getBlockState(pos.north()).getBlock())) {
              RenderUtil.drawBoxESP(((Boolean)this.future.getValue()).booleanValue() ? pos.down() : pos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.outline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true);
            }  
        } 
      } 
    } 
  }
}
