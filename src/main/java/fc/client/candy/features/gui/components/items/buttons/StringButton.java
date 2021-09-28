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
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {
  private final Setting setting;
  
  public boolean isListening;
  
  private CurrentString currentString = new CurrentString("");
  
  public StringButton(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public static String removeLastChar(String str) {
    String output = "";
    if (str != null && str.length() > 0)
      output = str.substring(0, str.length() - 1); 
    return output;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : OyVey.colorManager.getColorWithAlpha(((Integer)((ClickGui)OyVey.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    if (this.isListening) {
      OyVey.textManager.drawStringWithShadow(this.currentString.getString() + OyVey.textManager.getIdleSign(), this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } else {
      OyVey.textManager.drawStringWithShadow((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? ("Prefix  " + ChatFormatting.GRAY) : "")) + this.setting.getValue(), this.x + 2.3F, this.y - 1.7F - OyVeyGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); 
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (this.isListening) {
      switch (keyCode) {
        case 1:
          return;
        case 28:
          enterString();
        case 14:
          setString(removeLastChar(this.currentString.getString()));
          break;
      } 
      if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
        setString(this.currentString.getString() + typedChar); 
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  private void enterString() {
    if (this.currentString.getString().isEmpty()) {
      this.setting.setValue(this.setting.getDefaultValue());
    } else {
      this.setting.setValue(this.currentString.getString());
    } 
    setString("");
    onMouseClick();
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
  
  public void setString(String newString) {
    this.currentString = new CurrentString(newString);
  }
  
  public static class CurrentString {
    private final String string;
    
    public CurrentString(String string) {
      this.string = string;
    }
    
    public String getString() {
      return this.string;
    }
  }
}
