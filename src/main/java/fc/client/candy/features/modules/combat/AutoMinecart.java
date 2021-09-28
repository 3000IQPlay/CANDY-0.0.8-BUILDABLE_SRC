//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.InventoryUtil;
import fc.client.candy.util.MathUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AutoMinecart extends Module {
  private final Setting<Boolean> web = register(new Setting("Web", Boolean.FALSE));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.FALSE));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.FALSE));
  
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(4)));
  
  private final Setting<Integer> delay = register(new Setting("Carts", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(50)));
  
  public Setting<Float> minHP = register(new Setting("MinHP", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(36.0F)));
  
  int wait;
  
  int waitFlint;
  
  int originalSlot;
  
  private boolean check;
  
  public AutoMinecart() {
    super("AutoMinecart", "Places and explodes minecarts on other players.", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      toggle(); 
    this.wait = 0;
    this.waitFlint = 0;
    this.originalSlot = mc.player.inventory.currentItem;
    this.check = true;
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      toggle(); 
    int i = InventoryUtil.findStackInventory(Items.TNT_MINECART);
    for (int j = 0; j < 9; j++) {
      if (mc.player.inventory.getStackInSlot(j).getItem() == Items.AIR && i != -1) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
        mc.playerController.updateController();
      } 
    } 
    int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
    int tntSlot = InventoryUtil.getItemHotbar(Items.TNT_MINECART);
    int flintSlot = InventoryUtil.getItemHotbar(Items.FLINT_AND_STEEL);
    int railSlot = InventoryUtil.findHotbarBlock(Blocks.ACTIVATOR_RAIL);
    int picSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
    if (tntSlot == -1 || railSlot == -1 || flintSlot == -1 || picSlot == -1 || (((Boolean)this.web.getValue()).booleanValue() && webSlot == -1)) {
      Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No (tnt minecart/activator rail/flint/pic/webs) in hotbar disabling...");
      toggle();
    } 
    EntityPlayer target;
    if ((target = getTarget()) == null)
      return; 
    BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
    Vec3d hitVec = (new Vec3d((Vec3i)pos)).add(0.0D, -0.5D, 0.0D);
    if (mc.player.getDistanceSq(pos) <= MathUtil.square(6.0D)) {
      this.check = true;
      if (mc.world.getBlockState(pos).getBlock() != Blocks.ACTIVATOR_RAIL && !mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty()) {
        InventoryUtil.switchToHotbarSlot(flintSlot, false);
        BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean)this.packet.getValue()).booleanValue());
      } 
      if (mc.world.getBlockState(pos).getBlock() != Blocks.ACTIVATOR_RAIL && mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.up())).isEmpty() && mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.down())).isEmpty()) {
        InventoryUtil.switchToHotbarSlot(railSlot, false);
        BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean)this.packet.getValue()).booleanValue());
        this.wait = 0;
      } 
      if (((Boolean)this.web.getValue()).booleanValue() && this.wait != 0 && mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL && !target.isInWeb && (BlockUtil.isPositionPlaceable(pos.up(), false) == 1 || BlockUtil.isPositionPlaceable(pos.up(), false) == 3) && mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos.up())).isEmpty()) {
        InventoryUtil.switchToHotbarSlot(webSlot, false);
        BlockUtil.placeBlock(pos.up(), EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), false);
      } 
      if (mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL) {
        InventoryUtil.switchToHotbarSlot(tntSlot, false);
        for (int u = 0; u < ((Integer)this.blocksPerTick.getValue()).intValue(); u++)
          BlockUtil.rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean)this.packet.getValue()).booleanValue()); 
      } 
      if (this.wait < ((Integer)this.delay.getValue()).intValue()) {
        this.wait++;
        return;
      } 
      this.check = false;
      this.wait = 0;
      InventoryUtil.switchToHotbarSlot(picSlot, false);
      if (mc.world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL && !mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty())
        mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP); 
      InventoryUtil.switchToHotbarSlot(flintSlot, false);
      if (mc.world.getBlockState(pos).getBlock().getBlockState().getBaseState().getMaterial() != Material.FIRE && !mc.world.getEntitiesWithinAABB(EntityMinecartTNT.class, new AxisAlignedBB(pos)).isEmpty())
        BlockUtil.rightClickBlock(pos.down(), hitVec, EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean)this.packet.getValue()).booleanValue()); 
    } 
  }
  
  public String getDisplayInfo() {
    if (this.check)
      return ChatFormatting.GREEN + "Placing"; 
    return ChatFormatting.RED + "Breaking";
  }
  
  public void onDisable() {
    mc.player.inventory.currentItem = this.originalSlot;
  }
  
  private EntityPlayer getTarget() {
    EntityPlayer target = null;
    double distance = Math.pow(6.0D, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, 6.0D) || player.isInWater() || player.isInLava() || !EntityUtil.isTrapped(player, false, false, false, false, false) || player.getHealth() + player.getAbsorptionAmount() > ((Float)this.minHP.getValue()).floatValue())
        continue; 
      if (target == null) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) >= distance)
        continue; 
      target = player;
      distance = mc.player.getDistanceSq((Entity)player);
    } 
    return target;
  }
}
