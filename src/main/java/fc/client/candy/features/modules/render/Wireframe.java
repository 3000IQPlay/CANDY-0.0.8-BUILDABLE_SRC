package fc.client.candy.features.modules.render;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wireframe extends Module {
  private static Wireframe INSTANCE = new Wireframe();
  
  public final Setting<Float> alpha = register(new Setting("PAlpha", Float.valueOf(255.0F), Float.valueOf(0.1F), Float.valueOf(255.0F)));
  
  public final Setting<Float> cAlpha = register(new Setting("CAlpha", Float.valueOf(255.0F), Float.valueOf(0.1F), Float.valueOf(255.0F)));
  
  public final Setting<Float> lineWidth = register(new Setting("PLineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  public final Setting<Float> crystalLineWidth = register(new Setting("CLineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  public Setting<RenderMode> mode = register(new Setting("PMode", RenderMode.SOLID));
  
  public Setting<RenderMode> cMode = register(new Setting("CMode", RenderMode.SOLID));
  
  public Setting<Boolean> players = register(new Setting("Players", Boolean.FALSE));
  
  public Setting<Boolean> playerModel = register(new Setting("PlayerModel", Boolean.FALSE));
  
  public Setting<Boolean> crystals = register(new Setting("Crystals", Boolean.FALSE));
  
  public Setting<Boolean> crystalModel = register(new Setting("CrystalModel", Boolean.FALSE));
  
  public Wireframe() {
    super("Wireframe", "Draws a wireframe esp around other players.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static Wireframe getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new Wireframe(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
    (event.getEntityPlayer()).hurtTime = 0;
  }
  
  public enum RenderMode {
    SOLID, WIREFRAME;
  }
}
