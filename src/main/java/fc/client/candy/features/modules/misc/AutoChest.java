package fc.client.candy.features.modules.misc;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class AutoChest extends Module {
  private Setting<Boolean> autoToggle = register(new Setting("Toggle", Boolean.valueOf(true)));
  
  public AutoChest() {
    super("AutoEChest", "Automatically place EnderChest on your Hole", Module.Category.MISC, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    int EChest = findMaterials(Blocks.ENDER_CHEST);
    if (EChest == -1) {
      autoToggle();
      return;
    } 
    BlockPos[] offset = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    int oldslot = mc.player.inventory.currentItem;
    mc.player.inventory.currentItem = EChest;
    mc.playerController.updateController();
    for (int i = 0; i < offset.length; i++) {
      BlockPos base = pos.add((Vec3i)offset[i]);
      if (getBlock(base).getBlock() != Blocks.AIR && 
        !(getBlock(base).getBlock() instanceof net.minecraft.block.BlockLiquid))
        if (getBlock(base.add(0, 1, 0)).getBlock() == Blocks.AIR)
          BlockUtil.placeBlock(base.add(0, 1, 0), EnumHand.MAIN_HAND, true, false, false);  
    } 
    mc.player.inventory.currentItem = oldslot;
    mc.playerController.updateController();
    if (((Boolean)this.autoToggle.getValue()).booleanValue())
      autoToggle(); 
  }
  
  private void autoToggle() {
    if (((Boolean)this.autoToggle.getValue()).booleanValue())
      disable(); 
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
