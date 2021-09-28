package fc.client.candy.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.setting.Bind;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.ColorUtil;
import fc.client.candy.util.RenderUtil;
import java.io.IOException;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {
  private final Setting setting;
  
  public boolean isListening;
  
  public BindButton(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    int color = ColorUtil.toARGB(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue(), 255);
    RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515) : (!isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())));
    if (this.isListening) {
      OyVey.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), -1);
    } else {
      OyVey.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); 
    try {
      OyVey.configManager.saveSettings(this.setting.getFeature());
    } catch (IOException iOException) {}
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (this.isListening) {
      Bind bind = new Bind(keyCode);
      if (bind.toString().equalsIgnoreCase("Escape"))
        return; 
      if (bind.toString().equalsIgnoreCase("Delete"))
        bind = new Bind(-1); 
      this.setting.setValue(bind);
      onMouseClick();
    } 
  }
  
  public int getHeight() {
    return 14;
  }
  
  public void toggle() {
    this.isListening = !this.isListening;
  }
  
  public boolean getState() {
    return !this.isListening;
  }
}
