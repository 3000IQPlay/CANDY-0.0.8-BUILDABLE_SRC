package fc.client.candy.manager;

import fc.client.candy.features.gui.components.Component;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.util.ColorUtil;
import java.awt.Color;

public class ColorManager {
  private float red = 1.0F;
  
  private float green = 1.0F;
  
  private float blue = 1.0F;
  
  private float alpha = 1.0F;
  
  private Color color = new Color(this.red, this.green, this.blue, this.alpha);
  
  public Color getColor() {
    return this.color;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }
  
  public int getColorAsInt() {
    return ColorUtil.toRGBA(this.color);
  }
  
  public int getColorAsIntFullAlpha() {
    return ColorUtil.toRGBA(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 255));
  }
  
  public int getColorWithAlpha(int alpha) {
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue())
      return ColorUtil.rainbow(Component.counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(); 
    return ColorUtil.toRGBA(new Color(this.red, this.green, this.blue, alpha / 255.0F));
  }
  
  public void setColor(float red, float green, float blue, float alpha) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
    updateColor();
  }
  
  public void updateColor() {
    setColor(new Color(this.red, this.green, this.blue, this.alpha));
  }
  
  public void setColor(int red, int green, int blue, int alpha) {
    this.red = red / 255.0F;
    this.green = green / 255.0F;
    this.blue = blue / 255.0F;
    this.alpha = alpha / 255.0F;
    updateColor();
  }
  
  public void setRed(float red) {
    this.red = red;
    updateColor();
  }
  
  public void setGreen(float green) {
    this.green = green;
    updateColor();
  }
  
  public void setBlue(float blue) {
    this.blue = blue;
    updateColor();
  }
  
  public void setAlpha(float alpha) {
    this.alpha = alpha;
    updateColor();
  }
}
