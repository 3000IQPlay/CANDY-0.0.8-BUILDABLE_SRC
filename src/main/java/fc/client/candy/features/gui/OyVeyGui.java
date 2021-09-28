package fc.client.candy.features.gui;

import fc.client.candy.OyVey;
import fc.client.candy.features.Feature;
import fc.client.candy.features.gui.components.Component;
import fc.client.candy.features.gui.components.items.Item;
import fc.client.candy.features.gui.components.items.buttons.Button;
import fc.client.candy.features.gui.components.items.buttons.ModuleButton;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.HubEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class OyVeyGui extends GuiScreen {
  public String moving = "";
  
  private static OyVeyGui INSTANCE = new OyVeyGui();
  
  private final ArrayList<Component> components = new ArrayList<>();
  
  private Component hubComponent = null;
  
  private static OyVeyGui oyveyGui;
  
  public float c_w;
  
  public float m_x;
  
  public float m_y;
  
  public Module horver_module;
  
  public void onGuiClosed() {
    ((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).disable();
  }
  
  public OyVeyGui() {
    setInstance();
    load();
  }
  
  public static OyVeyGui getInstance() {
    if (INSTANCE == null)
      INSTANCE = new OyVeyGui(); 
    return INSTANCE;
  }
  
  public static OyVeyGui getClickGui() {
    return getInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  private void load() {
    int x = -84;
    for (Module.Category category : OyVey.moduleManager.getCategories()) {
      if (category != Module.Category.HUB) {
        x += 90;
        this.components.add(new Component(category.getName(), x, 4, true) {
              public void setupItems() {
                counter1 = new int[] { 1 };
                OyVey.moduleManager.getModulesByCategory(category).forEach(module -> {
                      if (!module.hidden)
                        addButton((Button)new ModuleButton(module)); 
                    });
              }
            });
        continue;
      } 
      this.hubComponent = new Component(category.getName(), 90, 4, true) {
          public void setupItems() {
            counter1 = new int[] { 1 };
            OyVey.moduleManager.getModulesByCategory(Module.Category.HUB).forEach(module -> {
                  OyVey.LOGGER.info("Loaded Module -> " + module.getName());
                  if (!module.hidden) {
                    OyVey.LOGGER.info("Loaded Module has draw");
                    addButton((Button)new ModuleButton(module));
                  } 
                });
          }
        };
    } 
    this.hubComponent.getItems().sort(Comparator.comparing(Feature::getName));
    this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
  }
  
  public void updateModule(Module module) {
    if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
      for (Item item : this.hubComponent.getItems()) {
        if (!(item instanceof ModuleButton))
          continue; 
        ModuleButton button = (ModuleButton)item;
        Module mod = button.getModule();
        if (module == null || !module.equals(mod))
          continue; 
        button.initSettings();
      } 
    } else {
      for (Component component : this.components) {
        for (Item item : component.getItems()) {
          if (!(item instanceof ModuleButton))
            continue; 
          ModuleButton button = (ModuleButton)item;
          Module mod = button.getModule();
          if (module == null || !module.equals(mod))
            continue; 
          button.initSettings();
        } 
      } 
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
      checkMouseWheel();
      this.hubComponent.drawScreen(mouseX, mouseY, partialTicks);
    } else {
      checkMouseWheel();
      drawDefaultBackground();
      this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
    OyVey.moduleManager.getModulesByCategory(Module.Category.HUB).forEach(m -> m.onMouseClicked(mouseX, mouseY, clickedButton));
    if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
      this.hubComponent.mouseClicked(mouseX, mouseY, clickedButton);
    } else {
      this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    } 
  }
  
  public void mouseClickMove(int mouseX, int mouseY, int clickedButton, long p_mouseClickMove_4_) {
    OyVey.moduleManager.getModulesByCategory(Module.Category.HUB).forEach(m -> m.onMouseClickMove(mouseX, mouseY, clickedButton));
  }
  
  public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    OyVey.moduleManager.getModulesByCategory(Module.Category.HUB).forEach(m -> m.onMouseReleased(mouseX, mouseY, releaseButton));
    if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
      this.hubComponent.mouseReleased(mouseX, mouseY, releaseButton);
    } else {
      this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    } 
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public final ArrayList<Component> getComponents() {
    return this.components;
  }
  
  public void checkMouseWheel() {
    int dWheel = Mouse.getDWheel();
    if (dWheel < 0) {
      if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
        this.hubComponent.setY(this.hubComponent.getY() - 10);
      } else {
        this.components.forEach(component -> component.setY(component.getY() - 10));
      } 
    } else if (dWheel > 0) {
      if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
        this.hubComponent.setY(this.hubComponent.getY() + 10);
      } else {
        this.components.forEach(component -> component.setY(component.getY() + 10));
      } 
    } 
  }
  
  public int getTextOffset() {
    return -6;
  }
  
  public Component getComponentByName(String name) {
    for (Component component : this.components) {
      if (!component.getName().equalsIgnoreCase(name))
        continue; 
      return component;
    } 
    return null;
  }
  
  public void keyTyped(char typedChar, int keyCode) throws IOException {
    super.keyTyped(typedChar, keyCode);
    if (((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled()) {
      this.hubComponent.onKeyTyped(typedChar, keyCode);
    } else {
      this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    } 
  }
  
  private float GetCalcforSin(float offset) {
    return offset * 0.017453292F;
  }
}
