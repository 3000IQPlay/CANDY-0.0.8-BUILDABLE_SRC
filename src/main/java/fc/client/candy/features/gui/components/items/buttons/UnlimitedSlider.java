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

public class UnlimitedSlider extends Button {
  public Setting setting;
  
  public UnlimitedSlider(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
    OyVey.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY)) {
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      if (isRight(mouseX)) {
        if (this.setting.getValue() instanceof Double) {
          this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() + 1.0D));
        } else if (this.setting.getValue() instanceof Float) {
          this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() + 1.0F));
        } else if (this.setting.getValue() instanceof Integer) {
          this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() + 1));
        } 
      } else if (this.setting.getValue() instanceof Double) {
        this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() - 1.0D));
      } else if (this.setting.getValue() instanceof Float) {
        this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() - 1.0F));
      } else if (this.setting.getValue() instanceof Integer) {
        this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() - 1));
      } 
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  public int getHeight() {
    return 14;
  }
  
  public void toggle() {}
  
  public boolean getState() {
    return true;
  }
  
  public boolean isRight(int x) {
    return (x > this.x + (this.width + 7.4F) / 2.0F);
  }
}
