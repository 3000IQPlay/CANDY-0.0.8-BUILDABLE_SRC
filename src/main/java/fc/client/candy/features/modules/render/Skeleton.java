package fc.client.candy.features.modules.render;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.BlockUtil;
import java.util.HashMap;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton extends Module {
  private static final HashMap<EntityPlayer, float[][]> entities = (HashMap)new HashMap<>();
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Skeleton() {
    super("Skeleton", "Draws a skeleton inside the player.", Module.Category.RENDER, false, false, false);
  }
  
  public static void addEntity(EntityPlayer e, ModelPlayer model) {
    entities.put(e, new float[][] { { model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ }, { model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ }, { model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ }, { model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ }, { model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ } });
  }
  
  private Vec3d getVec3(Render3DEvent event, EntityPlayer e) {
    float pt = event.getPartialTicks();
    double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * pt;
    double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * pt;
    double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * pt;
    return new Vec3d(x, y, z);
  }
  
  public void onRender3D(Render3DEvent event) {
    if (fullNullCheck())
      return; 
    startEnd(true);
    GL11.glEnable(2903);
    GL11.glDisable(2848);
    entities.keySet().removeIf(this::doesntContain);
    mc.world.playerEntities.forEach(e -> drawSkeleton(event, e));
    Gui.drawRect(0, 0, 0, 0, 0);
    startEnd(false);
  }
  
  private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
    if (!BlockUtil.isPosInFov(new BlockPos(e.posX, e.posY, e.posZ)).booleanValue())
      return; 
    if (e.isInvisible() && !((Boolean)this.invisibles.getValue()).booleanValue())
      return; 
    float[][] entPos = entities.get(e);
    if (entPos != null && e.isEntityAlive() && !e.isDead && e != mc.player && !e.isPlayerSleeping()) {
      GL11.glPushMatrix();
      GL11.glEnable(2848);
      GL11.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      if (OyVey.friendManager.isFriend(e.getName())) {
        GlStateManager.color(0.0F, 191.0F, 230.0F, ((Integer)this.alpha.getValue()).intValue());
      } else {
        GlStateManager.color(((Integer)(ClickGui.getInstance()).red.getValue()).intValue() / 255.0F, ((Integer)(ClickGui.getInstance()).green.getValue()).intValue() / 255.0F, ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue() / 255.0F, ((Integer)this.alpha.getValue()).intValue());
      } 
      Vec3d vec = getVec3(event, e);
      double x = vec.x - (mc.getRenderManager()).renderPosX;
      double y = vec.y - (mc.getRenderManager()).renderPosY;
      double z = vec.z - (mc.getRenderManager()).renderPosZ;
      GL11.glTranslated(x, y, z);
      float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
      GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
      GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
      float yOff = e.isSneaking() ? 0.6F : 0.75F;
      GL11.glPushMatrix();
      GL11.glTranslated(-0.125D, yOff, 0.0D);
      if (entPos[3][0] != 0.0F)
        GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
      if (entPos[3][1] != 0.0F)
        GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
      if (entPos[3][2] != 0.0F)
        GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -yOff, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslated(0.125D, yOff, 0.0D);
      if (entPos[4][0] != 0.0F)
        GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
      if (entPos[4][1] != 0.0F)
        GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
      if (entPos[4][2] != 0.0F)
        GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -yOff, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
      GL11.glPushMatrix();
      GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
      if (entPos[1][0] != 0.0F)
        GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
      if (entPos[1][1] != 0.0F)
        GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
      if (entPos[1][2] != 0.0F)
        GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
      if (entPos[2][0] != 0.0F)
        GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
      if (entPos[2][1] != 0.0F)
        GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
      if (entPos[2][2] != 0.0F)
        GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
      if (entPos[0][0] != 0.0F)
        GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, 0.3D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
      GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, yOff, 0.0D);
      GL11.glBegin(3);
      GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
      GL11.glVertex3d(0.125D, 0.0D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, yOff, 0.0D);
      GL11.glBegin(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, 0.55D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
      GL11.glBegin(3);
      GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
      GL11.glVertex3d(0.375D, 0.0D, 0.0D);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
    } 
  }
  
  private void startEnd(boolean revert) {
    if (revert) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GL11.glEnable(2848);
      GlStateManager.disableDepth();
      GlStateManager.disableTexture2D();
      GL11.glHint(3154, 4354);
    } else {
      GlStateManager.disableBlend();
      GlStateManager.enableTexture2D();
      GL11.glDisable(2848);
      GlStateManager.enableDepth();
      GlStateManager.popMatrix();
    } 
    GlStateManager.depthMask((!revert));
  }
  
  private boolean doesntContain(EntityPlayer entityPlayer) {
    return !mc.world.playerEntities.contains(entityPlayer);
  }
}
