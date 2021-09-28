package fc.client.candy.features.modules.hub;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.Render2DEvent;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.ColorUtil;
import fc.client.candy.util.MathUtil;
import fc.client.candy.util.RenderUtil;

public class Welcomer extends Module {
  public Setting<Integer> _x = register(new Setting("X", Integer.valueOf(this.renderer.scaledWidth / 2), Integer.valueOf(0), Integer.valueOf(this.renderer.scaledWidth)));
  
  public Setting<Integer> _y = register(new Setting("Y", Integer.valueOf(this.renderer.scaledHeight / 2), Integer.valueOf(0), Integer.valueOf(this.renderer.scaledHeight)));
  
  private int x = 0;
  
  private int y = 0;
  
  private int width = 0;
  
  private int height = 0;
  
  public Welcomer() {
    super("Welcomer", "morning , afternoon", Module.Category.HUB, true, false, false);
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
    renderGreeter();
  }
  
  public void renderGreeter() {
    int width = this.renderer.scaledWidth;
    String text = MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
        this.renderer.drawString(text, ((Integer)this._x.getValue()).intValue(), ((Integer)this._y.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
      } else {
        int[] counter1 = { 1 };
        char[] stringToCharArray = text.toCharArray();
        float i = 0.0F;
        for (char c : stringToCharArray) {
          this.renderer.drawString(String.valueOf(c), ((Integer)this._x.getValue()).intValue(), ((Integer)this._y.getValue()).intValue(), ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          i += this.renderer.getStringWidth(String.valueOf(c));
          counter1[0] = counter1[0] + 1;
        } 
      } 
    } else {
      this.renderer.drawString(text, ((Integer)this._x.getValue()).intValue(), ((Integer)this._y.getValue()).intValue(), this.color, true);
    } 
  }
  
  public void renderHub() {
    if (isOpenHubEditor()) {
      String s = MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
      RenderUtil.drawRect(this.x, this.y, this.width, this.height, 
          ((getGUIInstance()).moving == "welcomer") ? this.background1 : this.background);
    } 
  }
  
  public void onMouseClicked(int mouseX, int mouseY, int clickedButton) {
    if (this.x < mouseX && this.width > mouseX && this.y < mouseY && this.height > mouseY)
      if ((getGUIInstance()).moving == "")
        (getGUIInstance()).moving = "welcomer";  
  }
  
  public void onMouseClickMove(int mouseX, int mouseY, int clickedButton) {
    if ((getGUIInstance()).moving == "welcomer") {
      String s = MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
      this._x.setValue(Integer.valueOf(mouseX));
      this._y.setValue(Integer.valueOf(mouseY));
      this.x = ((Integer)this._x.getValue()).intValue() - 2;
      this.y = ((Integer)this._y.getValue()).intValue() - 2;
      this.width = ((Integer)this._x.getValue()).intValue() + OyVey.textManager.getStringWidth(s) + 4;
      this.height = ((Integer)this._y.getValue()).intValue() + OyVey.textManager.getFontHeight() + 4;
    } 
  }
  
  public void onMouseReleased(int mouseX, int mouseY, int releasedButton) {
    if ((getGUIInstance()).moving == "welcomer")
      (getGUIInstance()).moving = ""; 
  }
}
