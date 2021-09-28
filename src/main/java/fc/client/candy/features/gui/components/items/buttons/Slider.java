package fc.client.candy.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.gui.components.Component;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.RenderUtil;
import org.lwjgl.input.Mouse;

public class Slider extends Button {
  private final Number min;
  
  private final Number max;
  
  private final int difference;
  
  public Setting setting;
  
  public Slider(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.min = (Number)setting.getMin();
    this.max = (Number)setting.getMax();
    this.difference = this.max.intValue() - this.min.intValue();
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    dragSetting(mouseX, mouseY);
    RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? 290805077 : -2007673515);
    RenderUtil.drawRect(this.x, this.y, (((Number)this.setting.getValue()).floatValue() <= this.min.floatValue()) ? this.x : (this.x + (this.width + 7.4F) * partialMultiplier()), this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha((Integer) ((ClickGui) OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : OyVey.colorManager.getColorWithAlpha((Integer) ((ClickGui) OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()));
    OyVey.textManager.drawStringWithShadow(getName() + " " + ChatFormatting.GRAY + ((this.setting.getValue() instanceof Float) ? this.setting.getValue() : Double.valueOf(((Number)this.setting.getValue()).doubleValue())), this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), -1);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      setSettingFromX(mouseX); 
  }
  
  public boolean isHovering(int mouseX, int mouseY) {
    for (Component component : OyVeyGui.getClickGui().getComponents()) {
      if (!component.drag)
        continue; 
      return false;
    } 
    return (mouseX >= getX() && mouseX <= getX() + getWidth() + 8.0F && mouseY >= getY() && mouseY <= getY() + this.height);
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  private void dragSetting(int mouseX, int mouseY) {
    if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0))
      setSettingFromX(mouseX); 
  }
  
  public int getHeight() {
    return 14;
  }
  
  private void setSettingFromX(int mouseX) {
    float percent = (mouseX - this.x) / (this.width + 7.4F);
    if (this.setting.getValue() instanceof Double) {
      double result = ((Double)this.setting.getMin()).doubleValue() + (this.difference * percent);
      this.setting.setValue(Double.valueOf(Math.round(10.0D * result) / 10.0D));
    } else if (this.setting.getValue() instanceof Float) {
      float result = ((Float)this.setting.getMin()).floatValue() + this.difference * percent;
      this.setting.setValue(Float.valueOf(Math.round(10.0F * result) / 10.0F));
    } else if (this.setting.getValue() instanceof Integer) {
      this.setting.setValue(Integer.valueOf(((Integer)this.setting.getMin()).intValue() + (int)(this.difference * percent)));
    } 
  }
  
  private float middle() {
    return this.max.floatValue() - this.min.floatValue();
  }
  
  private float part() {
    return ((Number)this.setting.getValue()).floatValue() - this.min.floatValue();
  }
  
  private float partialMultiplier() {
    return part() / middle();
  }
}
