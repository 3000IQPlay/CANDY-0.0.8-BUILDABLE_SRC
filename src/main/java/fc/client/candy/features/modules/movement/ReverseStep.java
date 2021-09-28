package fc.client.candy.features.modules.movement;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class ReverseStep extends Module {
  private static ReverseStep INSTANCE = new ReverseStep();
  
  private final Setting<Boolean> twoBlocks = register(new Setting("2Blocks", Boolean.FALSE));
  
  public ReverseStep() {
    super("ReverseStep", "ReverseStep.", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  public static ReverseStep getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ReverseStep(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    IBlockState touchingState = mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).down(2));
    IBlockState touchingState2 = mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).down(3));
    if (mc.player.isInLava() || mc.player.isInWater())
      return; 
    if (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN) {
      if (mc.player.onGround)
        mc.player.motionY--; 
    } else if (((((Boolean)this.twoBlocks.getValue()).booleanValue() && touchingState2.getBlock() == Blocks.BEDROCK) || (((Boolean)this.twoBlocks.getValue()).booleanValue() && touchingState2.getBlock() == Blocks.OBSIDIAN)) && mc.player.onGround) {
      mc.player.motionY--;
    } 
  }
}
