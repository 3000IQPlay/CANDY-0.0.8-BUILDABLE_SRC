package fc.client.candy.features.gui.components;

import fc.client.candy.OyVey;
import fc.client.candy.features.Feature;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.gui.components.items.Item;
import fc.client.candy.features.gui.components.items.buttons.Button;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.util.ColorUtil;
import fc.client.candy.util.RenderUtil;
import java.util.ArrayList;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {
  public static int[] counter1 = new int[] { 1 };
  
  private final ArrayList<Item> items = new ArrayList<>();
  
  public boolean drag;
  
  private int x;
  
  private int y;
  
  private int x2;
  
  private int y2;
  
  private int width;
  
  private int height;
  
  private boolean open;
  
  private boolean hidden = false;
  
  private boolean opening = false;
  
  private float animetion = 0.0F;
  
  public Component(String name, int x, int y, boolean open) {
    super(name);
    this.x = x;
    this.y = y;
    this.width = 88;
    this.height = 18;
    this.open = open;
    setupItems();
  }
  
  public void setupItems() {}
  
  private void drag(int mouseX, int mouseY) {
    if (!this.drag)
      return; 
    this.x = this.x2 + mouseX;
    this.y = this.y2 + mouseY;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drag(mouseX, mouseY);
    counter1 = new int[] { 1 };
    float totalItemHeight = this.open ? (getTotalItemHeight() - 2.0F) : -4.5F;
    int color = ColorUtil.toARGB(((Integer)(ClickGui.getInstance()).topRed.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topGreen.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topBlue.getValue()).intValue(), 255);
    if (this.open && !this.opening) {
      RenderUtil.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height) + totalItemHeight, color);
    } else if (this.opening) {
      if (this.open) {
        this.animetion += (0.0F - this.animetion) / 2.0F;
        RenderUtil.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height) + this.animetion, color);
        if (0.0F - this.animetion < 5.0F) {
          this.open = false;
          this.opening = false;
        } 
      } else {
        this.animetion += (getTotalItemHeight() - this.animetion) / 2.0F;
        RenderUtil.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height) + this.animetion, color);
        if (getTotalItemHeight() - this.animetion < 5.0F) {
          this.open = true;
          this.opening = false;
        } 
      } 
    } else {
      RenderUtil.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height) + totalItemHeight, color);
    } 
    OyVey.textManager.drawStringWithShadow(getName(), this.x + ((this.width - OyVey.textManager.getStringWidth(getName())) / 2), this.y - 3.0F - OyVeyGui.getClickGui().getTextOffset(), -1);
    RenderUtil.drawLine(this.x, (this.y + OyVey.textManager.getFontHeight() + 6), (this.x + this.width), (this.y + OyVey.textManager.getFontHeight() + 6), 2.0F, OyVey.colorManager.getColorWithAlpha(200));
    if (this.open || this.opening) {
      float y = (getY() + getHeight()) - 3.0F;
      for (Item item : getItems()) {
        if (item.getY() < (this.y + this.height) + this.animetion || !this.opening) {
          counter1[0] = counter1[0] + 1;
          if (item.isHidden())
            continue; 
          item.setLocation(this.x + 2.0F, y);
          item.setWidth(getWidth() - 4);
          item.drawScreen(mouseX, mouseY, partialTicks);
          y += item.getHeight() + 1.5F;
        } 
      } 
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
      this.x2 = this.x - mouseX;
      this.y2 = this.y - mouseY;
      OyVeyGui.getClickGui().getComponents().forEach(component -> {
            if (component.drag)
              component.drag = false; 
          });
      this.drag = true;
      return;
    } 
    if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
      this.opening = !this.opening;
      this.animetion = this.open ? (getTotalItemHeight() - 2.0F) : -4.5F;
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      return;
    } 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
  }
  
  public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    if (releaseButton == 0)
      this.drag = false; 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (!this.open)
      return; 
    getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
  }
  
  public void addButton(Button button) {
    this.items.add(button);
  }
  
  public int getX() {
    return this.x;
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public boolean isHidden() {
    return this.hidden;
  }
  
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  public boolean isOpen() {
    return this.open;
  }
  
  public final ArrayList<Item> getItems() {
    return this.items;
  }
  
  private boolean isHovering(int mouseX, int mouseY) {
    return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (this.open ? 2 : 0));
  }
  
  private float getTotalItemHeight() {
    float height = 0.0F;
    for (Item item : getItems())
      height += item.getHeight() + 1.5F; 
    return height;
  }
}
