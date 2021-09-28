//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.combat;

import fc.client.candy.features.Feature;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfFill extends Module {
  private final Setting<Boolean> silent = register(new Setting("Silent", Boolean.TRUE));
  
  private final Setting<type> block = register(new Setting("Block", type.Obsidian));
  
  private final Setting<Float> offset = register(new Setting("Offset", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(30.0F)));
  
  public enum type {
    Obsidian, EnderChest;
  }
  
  public SelfFill() {
    super("SelfFill", "SelfFills yourself in a hole.", Module.Category.COMBAT, true, false, true);
  }
  
  public void onEnable() {
    if (Feature.nullCheck())
      return; 
    int ob = findMaterials(Blocks.OBSIDIAN);
    int chest = findMaterials(Blocks.ENDER_CHEST);
    if (ob == -1 && this.block.getValue() == type.Obsidian) {
      SendMessage("Missing Obby! disabling...");
      disable();
      return;
    } 
    if (chest == -1 && this.block.getValue() == type.EnderChest) {
      SendMessage("Missing Chest! disabling...");
      disable();
      return;
    } 
    BlockPos originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    SendMessage("Attempting to trigger a rubberband!");
    float old = mc.timer.tickLength;
    if (((Boolean)this.silent.getValue()).booleanValue())
      mc.timer.tickLength = 1.0F; 
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ);
    mc.player.inventory.currentItem = (this.block.getValue() == type.Obsidian) ? ob : chest;
    mc.playerController.updateController();
    BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, true, true, false);
    mc.player.setPosition(mc.player.posX, mc.player.posY - 1.16610926093821D, mc.player.posZ);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + ((Float)this.offset.getValue()).floatValue(), mc.player.posZ, true));
    mc.timer.tickLength = old;
    disable();
  }
  
  private int findMaterials(Block b) {
    for (int i = 0; i < 9; i++) {
      if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && (
        (ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == b)
        return i; 
    } 
    return -1;
  }
}
