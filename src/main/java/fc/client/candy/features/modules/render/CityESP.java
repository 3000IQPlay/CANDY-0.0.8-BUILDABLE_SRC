package fc.client.candy.features.modules.render;

import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.RenderUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class CityESP extends Module {
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(5.0F), Float.valueOf(30.0F)));
  
  private Setting<Integer> red = register(new Setting("Red", Integer.valueOf(160), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> green = register(new Setting("Green", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(225)));
  
  private Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(225)));
  
  private Setting<Boolean> renderOL = register(new Setting("Outline", Boolean.valueOf(false)));
  
  private Setting<Integer> OLRed = register(new Setting("OL-Red", Integer.valueOf(190), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLGreen = register(new Setting("OL-Green", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLBlue = register(new Setting("OL-Blue", Integer.valueOf(190), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLAlpha = register(new Setting("OL-Alpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Float> width = register(new Setting("Line Width", Float.valueOf(1.1F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  public CityESP() {
    super("CityESP", "Draw Box", Module.Category.RENDER, true, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    List<EntityPlayer> players = mc.world.playerEntities;
    for (EntityPlayer player : players) {
      if (player.getDistance((Entity)mc.player) > ((Float)this.range.getValue()).floatValue())
        continue; 
      if (player.entityId != mc.player.entityId) {
        BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        for (int i = 0; i < offset.length; i++) {
          BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
          if (getBlock(pos).getBlock() == Blocks.AIR || 
            !(getBlock(pos).getBlock() instanceof net.minecraft.block.BlockLiquid))
            if (getBlock(pos.add((Vec3i)offset[i])).getBlock() == Blocks.OBSIDIAN)
              RenderUtil.drawBoxESP(pos.add((Vec3i)offset[i]), new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.green.getValue()).intValue()), ((Boolean)this.renderOL.getValue()).booleanValue(), new Color(((Integer)this.OLRed
                    .getValue()).intValue(), ((Integer)this.OLBlue.getValue()).intValue(), ((Integer)this.OLGreen.getValue()).intValue(), ((Integer)this.OLAlpha.getValue()).intValue()), ((Float)this.width.getValue()).floatValue(), ((Boolean)this.renderOL.getValue()).booleanValue(), true, ((Integer)this.alpha
                  .getValue()).intValue(), true);  
        } 
      } 
    } 
  }
  
  private IBlockState getBlock(BlockPos b) {
    return mc.world.getBlockState(b);
  }
}
