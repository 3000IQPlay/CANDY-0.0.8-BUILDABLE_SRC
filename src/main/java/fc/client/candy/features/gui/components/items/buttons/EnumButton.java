package fc.client.candy.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton extends Button {
  public Setting setting;
  
  public EnumButton(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    OyVey.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + (this.setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : this.setting.currentEnumName()), this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); 
  }
  
  public int getHeight() {
    return 14;
  }
  
  public void toggle() {
    this.setting.increaseEnum();
  }
  
  public boolean getState() {
    return true;
  }
}
