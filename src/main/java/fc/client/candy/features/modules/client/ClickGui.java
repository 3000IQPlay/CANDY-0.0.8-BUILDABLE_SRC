package fc.client.candy.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.event.events.ClientEvent;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
  private static ClickGui INSTANCE = new ClickGui();
  
  public Setting<String> prefix = register(new Setting("Prefix", "."));
  
  public Setting<Boolean> customFov = register(new Setting("CustomFov", Boolean.valueOf(false)));
  
  public Setting<Float> fov = register(new Setting("Fov", Float.valueOf(150.0F), Float.valueOf(-180.0F), Float.valueOf(180.0F)));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(230), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(90), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> hoverAlpha = register(new Setting("Alpha", Integer.valueOf(140), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> topRed = register(new Setting("SecondRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> topGreen = register(new Setting("SecondGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> topBlue = register(new Setting("SecondBlue", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> alpha = register(new Setting("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false)));
  
  public Setting<rainbowMode> rainbowModeHud = register(new Setting("HRainbowMode", rainbowMode.Static, v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<rainbowModeArray> rainbowModeA = register(new Setting("ARainbowMode", rainbowModeArray.Static, v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowHue = register(new Setting("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Float> rainbowBrightness = register(new Setting("Brightness ", Float.valueOf(150.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Float> rainbowSaturation = register(new Setting("Saturation", Float.valueOf(150.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  private OyVeyGui click;
  
  public ClickGui() {
    super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  public static ClickGui getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ClickGui(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (((Boolean)this.customFov.getValue()).booleanValue())
      mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, ((Float)this.fov.getValue()).floatValue()); 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
      if (event.getSetting().equals(this.prefix)) {
        OyVey.commandManager.setPrefix((String)this.prefix.getPlannedValue());
        Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OyVey.commandManager.getPrefix());
      } 
      OyVey.colorManager.setColor(((Integer)this.red.getPlannedValue()).intValue(), ((Integer)this.green.getPlannedValue()).intValue(), ((Integer)this.blue.getPlannedValue()).intValue(), ((Integer)this.hoverAlpha.getPlannedValue()).intValue());
    } 
  }
  
  public void onEnable() {
    mc.displayGuiScreen((GuiScreen)OyVeyGui.getClickGui());
  }
  
  public void onLoad() {
    OyVey.colorManager.setColor(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.hoverAlpha.getValue()).intValue());
    OyVey.commandManager.setPrefix((String)this.prefix.getValue());
  }
  
  public void onTick() {
    if (!(mc.currentScreen instanceof OyVeyGui))
      disable(); 
  }
  
  public enum rainbowModeArray {
    Static, Up;
  }
  
  public enum rainbowMode {
    Static, Sideway;
  }
}
