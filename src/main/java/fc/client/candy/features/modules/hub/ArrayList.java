package fc.client.candy.features.modules.hub;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.event.events.Render2DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.ColorUtil;
import fc.client.candy.util.MathUtil;
import fc.client.candy.util.RenderUtil;

public class ArrayList extends Module {
  private Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
  
  public Setting<Integer> _x = register(new Setting("X", Integer.valueOf(this.renderer.scaledWidth - 50), Integer.valueOf(0), Integer.valueOf(this.renderer.scaledWidth)));
  
  public Setting<Integer> _y = register(new Setting("Y", Integer.valueOf(this.renderer.scaledHeight + 5), Integer.valueOf(0), Integer.valueOf(this.renderer.scaledHeight)));
  
  private int x = 0;
  
  private int y = 0;
  
  private int width = 0;
  
  private int height = 0;
  
  public ArrayList() {
    super("ArrayList", "oops", Module.Category.HUB, true, false, false);
  }
  
  public void onTick() {
    String s = MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
    this.x = ((Integer)this._x.getValue()).intValue() - 2;
    this.y = ((Integer)this._y.getValue()).intValue() - 2;
    this.width = ((Integer)this._x.getValue()).intValue() + OyVey.textManager.getStringWidth(s) + 4;
    this.height = ((Integer)this._y.getValue()).intValue() + OyVey.textManager.getFontHeight() + 4;
  }
  
  public void onRender2D(Render2DEvent event) {
    renderHub();
    renderList();
  }
  
  public void renderList() {
    int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && !((Boolean)this.renderingUp.getValue()).booleanValue()) ? 14 : 0;
    for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); k++) {
      Module module = OyVey.moduleManager.sortedModules.get(k);
      String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
      RenderUtil.drawRect((this.renderer.scaledWidth - 5 - this.renderer.getStringWidth(str)), (1 + j * 10), (this.renderer.getStringWidth(str) + 5), (3 + j * 10), ColorUtil.toARGB(10, 10, 10, 100));
      this.renderer.drawString(str, ((Integer)this._x.getValue()).intValue(), (((Integer)this._y.getValue()).intValue() + 2 + j * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(k + 1 * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
      j++;
    } 
  }
  
  public void renderHub() {
    if (isOpenHubEditor());
  }
}
