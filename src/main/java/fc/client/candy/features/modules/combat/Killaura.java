package fc.client.candy.features.modules.combat;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.UpdateWalkingPlayerEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.DamageUtil;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.MathUtil;
import fc.client.candy.util.Timer;
import fc.client.candy.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {
  public static Entity target;
  
  private final Timer timer = new Timer();
  
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  public Setting<Boolean> delay = register(new Setting("HitDelay", Boolean.valueOf(true)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  public Setting<Boolean> onlySharp = register(new Setting("SwordOnly", Boolean.valueOf(true)));
  
  public Setting<Float> raytrace = register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), "Wall Range."));
  
  public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
  
  public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
  
  public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
  
  public Setting<Boolean> vehicles = register(new Setting("Entities", Boolean.valueOf(false)));
  
  public Setting<Boolean> projectiles = register(new Setting("Projectiles", Boolean.valueOf(false)));
  
  public Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true)));
  
  public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  public Killaura() {
    super("Killaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
  }
  
  public void onTick() {
    if (!((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  private void doKillaura() {
    if (((Boolean)this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon((EntityPlayer)Util.mc.player)) {
      target = null;
      return;
    } 
    int wait = !((Boolean)this.delay.getValue()).booleanValue() ? 0 : (int)(DamageUtil.getCooldownByWeapon((EntityPlayer)Util.mc.player) * (((Boolean)this.tps.getValue()).booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F));
    if (!this.timer.passedMs(wait))
      return; 
    target = getTarget();
    if (target == null)
      return; 
    if (((Boolean)this.rotate.getValue()).booleanValue())
      OyVey.rotationManager.lookAtEntity(target); 
    EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), true);
    this.timer.reset();
  }
  
  private Entity getTarget() {
    Entity target = null;
    double distance = ((Float)this.range.getValue()).floatValue();
    double maxHealth = 36.0D;
    for (Entity entity : Util.mc.world.playerEntities) {
      if (((!((Boolean)this.players.getValue()).booleanValue() || !(entity instanceof EntityPlayer)) && (!((Boolean)this.animals.getValue()).booleanValue() || !EntityUtil.isPassive(entity)) && (!((Boolean)this.mobs.getValue()).booleanValue() || !EntityUtil.isMobAggressive(entity)) && (!((Boolean)this.vehicles.getValue()).booleanValue() || !EntityUtil.isVehicle(entity)) && (!((Boolean)this.projectiles.getValue()).booleanValue() || !EntityUtil.isProjectile(entity))) || (entity instanceof net.minecraft.entity.EntityLivingBase && 
        EntityUtil.isntValid(entity, distance)))
        continue; 
      if (!Util.mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && Util.mc.player.getDistanceSq(entity) > MathUtil.square(((Float)this.raytrace.getValue()).floatValue()))
        continue; 
      if (target == null) {
        target = entity;
        distance = Util.mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
        continue;
      } 
      if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
        target = entity;
        break;
      } 
      if (Util.mc.player.getDistanceSq(entity) < distance) {
        target = entity;
        distance = Util.mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
      } 
      if (EntityUtil.getHealth(entity) < maxHealth) {
        target = entity;
        distance = Util.mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
      } 
    } 
    return target;
  }
  
  public String getDisplayInfo() {
    if (target instanceof EntityPlayer)
      return target.getName(); 
    return null;
  }
}
