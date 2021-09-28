//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "D:\Downloads\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

package fc.client.candy.features.modules.render;

import com.google.common.collect.Maps;
import fc.client.candy.event.events.Render2DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.EntityUtil;
import fc.client.candy.util.RenderUtil;
import fc.client.candy.util.Util;
import java.awt.Color;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class ArrowESP extends Module {
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> radius = register(new Setting("Placement", Integer.valueOf(45), Integer.valueOf(10), Integer.valueOf(200)));
  
  private final Setting<Float> size = register(new Setting("Size", Float.valueOf(10.0F), Float.valueOf(5.0F), Float.valueOf(25.0F)));
  
  private final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Float> outlineWidth = register(new Setting("Outline-Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  private final Setting<Integer> fadeDistance = register(new Setting("Range", Integer.valueOf(100), Integer.valueOf(10), Integer.valueOf(200)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private final EntityListener entityListener = new EntityListener();
  
  public ArrowESP() {
    super("ArrowESP", "Arrow tracers ", Module.Category.RENDER, true, false, false);
  }
  
  public void onRender2D(Render2DEvent event) {
    this.entityListener.render();
    mc.world.loadedEntityList.forEach(o -> {
          if (o instanceof EntityPlayer && isValid((EntityPlayer)o)) {
            EntityPlayer entity = (EntityPlayer)o;
            Vec3d pos = this.entityListener.getEntityLowerBounds().get(entity);
            if (pos != null && !isOnScreen(pos) && !RenderUtil.isInViewFrustrum((Entity)entity)) {
              Color color = EntityUtil.getColor((Entity)entity, ((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), (int)MathHelper.clamp(255.0F - 255.0F / ((Integer)this.fadeDistance.getValue()).intValue() * mc.player.getDistance((Entity)entity), 100.0F, 255.0F), true);
              int x = Display.getWidth() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
              int y = Display.getHeight() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
              float yaw = getRotations((EntityLivingBase)entity) - mc.player.rotationYaw;
              GL11.glTranslatef(x, y, 0.0F);
              GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
              GL11.glTranslatef(-x, -y, 0.0F);
              RenderUtil.drawTracerPointer(x, (y - ((Integer)this.radius.getValue()).intValue()), ((Float)this.size.getValue()).floatValue(), 2.0F, 1.0F, ((Boolean)this.outline.getValue()).booleanValue(), ((Float)this.outlineWidth.getValue()).floatValue(), color.getRGB());
              GL11.glTranslatef(x, y, 0.0F);
              GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
              GL11.glTranslatef(-x, -y, 0.0F);
            } 
          } 
        });
  }
  
  private boolean isOnScreen(Vec3d pos) {
    if (pos.x <= -1.0D)
      return false; 
    if (pos.y >= 1.0D)
      return false; 
    if (pos.x <= -1.0D)
      return false; 
    if (pos.z >= 1.0D)
      return false; 
    int n = (mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale;
    if (pos.x / n < 0.0D)
      return false; 
    int n2 = (mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale;
    if (pos.x / n2 > Display.getWidth())
      return false; 
    int n3 = (mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale;
    if (pos.y / n3 < 0.0D)
      return false; 
    int n4 = (mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale;
    return (pos.y / n4 <= Display.getHeight());
  }
  
  private boolean isValid(EntityPlayer entity) {
    return (entity != mc.player && (!entity.isInvisible() || ((Boolean)this.invisibles.getValue()).booleanValue()) && entity.isEntityAlive());
  }
  
  private float getRotations(EntityLivingBase ent) {
    double x = ent.posX - mc.player.posX;
    double z = ent.posZ - mc.player.posZ;
    return (float)-(Math.atan2(x, z) * 57.29577951308232D);
  }
  
  private static class EntityListener {
    private final Map<Entity, Vec3d> entityUpperBounds = Maps.newHashMap();
    
    private final Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();
    
    private void render() {
      if (!this.entityUpperBounds.isEmpty())
        this.entityUpperBounds.clear(); 
      if (!this.entityLowerBounds.isEmpty())
        this.entityLowerBounds.clear(); 
      for (Entity e : Util.mc.world.loadedEntityList) {
        Vec3d bound = getEntityRenderPosition(e);
        bound.add(new Vec3d(0.0D, e.height + 0.2D, 0.0D));
        Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z);
        Vec3d lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2.0D, bound.z);
        if (upperBounds == null || lowerBounds == null)
          continue; 
        this.entityUpperBounds.put(e, upperBounds);
        this.entityLowerBounds.put(e, lowerBounds);
      } 
    }
    
    private Vec3d getEntityRenderPosition(Entity entity) {
      double partial = Util.mc.timer.renderPartialTicks;
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - (Util.mc.getRenderManager()).viewerPosX;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - (Util.mc.getRenderManager()).viewerPosY;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - (Util.mc.getRenderManager()).viewerPosZ;
      return new Vec3d(x, y, z);
    }
    
    public Map<Entity, Vec3d> getEntityLowerBounds() {
      return this.entityLowerBounds;
    }
    
    private EntityListener() {}
  }
}
