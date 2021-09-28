package fc.client.candy.features.gui.components.items.buttons;

import fc.client.candy.OyVey;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.gui.components.Component;
import fc.client.candy.features.gui.components.items.Item;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button extends Item {
  private boolean state;
  
  public Button(String name) {
    super(name);
    this.height = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    OyVey.textManager.drawStringWithShadow(getName(), this.x + 2.3F, this.y - 2.0F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isHovering(mouseX, mouseY))
      onMouseClick(); 
  }
  
  public void onMouseClick() {
    this.state = !this.state;
    toggle();
    mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }
  
  public void toggle() {}
  
  public boolean getState() {
    return this.state;
  }
  
  public int getHeight() {
    return 14;
  }
  
  public boolean isHovering(int mouseX, int mouseY) {
    for (Component component : OyVeyGui.getClickGui().getComponents()) {
      if (!component.drag)
        continue; 
      return false;
    } 
    return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + this.height);
  }
}
