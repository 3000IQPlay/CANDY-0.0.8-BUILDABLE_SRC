package fc.client.candy.features.modules.render;

import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class BurrowESP extends Module {
  private Setting<Float> range = register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(5.0F), Float.valueOf(30.0F)));
  
  private Setting<Integer> red = register(new Setting("Red", Integer.valueOf(160), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> green = register(new Setting("Green", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(225)));
  
  private Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(225)));
  
  private Setting<Boolean> renderOL = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private Setting<Integer> OLRed = register(new Setting("OL-Red", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLGreen = register(new Setting("OL-Green", Integer.valueOf(210), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLBlue = register(new Setting("OL-Blue", Integer.valueOf(170), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> OLAlpha = register(new Setting("OL-Alpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Float> width = register(new Setting("Line Width", Float.valueOf(1.1F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  private Setting<Boolean> fillter_obsidian = register(new Setting("Obsidian Only", Boolean.valueOf(true)));
  
  private List<BlockPos> blocks = new ArrayList<>();
  
  public BurrowESP() {
    super("BurrowESP", "NAHH", Module.Category.RENDER, true, false, false);
  }
  
  public void onUpdate() {
    this.blocks = new ArrayList<>();
    List<EntityPlayer> players = mc.world.playerEntities;
    for (Entity player : players) {
      if (mc.player.getDistance(player) < ((Float)this.range.getValue()).floatValue()) {
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        if (getBlock(pos).getBlock() != Blocks.AIR && 
          !(getBlock(pos).getBlock() instanceof net.minecraft.block.BlockLiquid))
          if (((Boolean)this.fillter_obsidian.getValue()).booleanValue() && 
            getBlock(pos).getBlock() == Blocks.OBSIDIAN)
            this.blocks.add(pos);  
      } 
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    for (BlockPos block : this.blocks)
      RenderUtil.drawBoxESP(block, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.green.getValue()).intValue()), ((Boolean)this.renderOL.getValue()).booleanValue(), new Color(((Integer)this.OLRed
            .getValue()).intValue(), ((Integer)this.OLBlue.getValue()).intValue(), ((Integer)this.OLGreen.getValue()).intValue(), ((Integer)this.OLAlpha.getValue()).intValue()), ((Float)this.width.getValue()).floatValue(), ((Boolean)this.renderOL.getValue()).booleanValue(), true, ((Integer)this.alpha
          .getValue()).intValue(), true); 
  }
  
  private IBlockState getBlock(BlockPos b) {
    return mc.world.getBlockState(b);
  }
}
