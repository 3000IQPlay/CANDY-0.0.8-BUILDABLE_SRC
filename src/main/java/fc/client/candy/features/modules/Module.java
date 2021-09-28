package fc.client.candy.features.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.event.events.ClientEvent;
import fc.client.candy.event.events.Render2DEvent;
import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.Feature;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.gui.OyVeyGui;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.modules.client.HUD;
import fc.client.candy.features.modules.client.HubEditor;
import fc.client.candy.features.setting.Bind;
import fc.client.candy.features.setting.Setting;
import fc.client.candy.util.ColorUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Module extends Feature {
  private final String description;
  
  private final Category category;
  
  public Setting<Boolean> enabled = register(new Setting("Enabled", Boolean.valueOf(false)));
  
  public Setting<Boolean> drawn = register(new Setting("Drawn", Boolean.valueOf(true)));
  
  public Setting<Bind> bind = register(new Setting("Keybind", new Bind(-1)));
  
  public Setting<String> displayName;
  
  public boolean hasListener;
  
  public boolean alwaysListening;
  
  public boolean hidden;
  
  public float arrayListOffset = 0.0F;
  
  public float arrayListVOffset = 0.0F;
  
  public float offset;
  
  public float vOffset;
  
  public boolean sliding;
  
  public int x;
  
  public int y;
  
  public int color;
  
  public int background;
  
  public int background1;
  
  public Module hubEditor;
  
  public Module(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
    super(name);
    this.displayName = register(new Setting("DisplayName", name));
    this.description = description;
    this.category = category;
    this.hasListener = hasListener;
    this.hidden = hidden;
    this.alwaysListening = alwaysListening;
  }
  
  public boolean isSliding() {
    return this.sliding;
  }
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public void onToggle() {}
  
  public void onLoad() {}
  
  public void onTick() {}
  
  public void onLogin() {
    this.color = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue());
    this.background = ColorUtil.toRGBA(235, 235, 220, 100);
    this.background1 = ColorUtil.toRGBA(195, 195, 180, 100);
    this.hubEditor = OyVey.moduleManager.getModuleByClass(HubEditor.class);
  }
  
  public void onLogout() {}
  
  public void onUpdate() {}
  
  public void onRender2D(Render2DEvent event) {}
  
  public void onRender3D(Render3DEvent event) {}
  
  public void onUnload() {}
  
  public void onMouseClicked(int mouseX, int mouseY, int clickedButton) {}
  
  public void onMouseClickMove(int mouseX, int mouseY, int clickedButton) {}
  
  public void onMouseReleased(int mouseX, int mouseY, int releasedButton) {}
  
  public OyVeyGui getGUIInstance() {
    return OyVeyGui.getClickGui();
  }
  
  public String getDisplayInfo() {
    return null;
  }
  
  public boolean isOn() {
    return ((Boolean)this.enabled.getValue()).booleanValue();
  }
  
  public boolean isOff() {
    return !((Boolean)this.enabled.getValue()).booleanValue();
  }
  
  public void setEnabled(boolean enabled) {
    if (enabled) {
      enable();
    } else {
      disable();
    } 
  }
  
  public void enable() {
    this.enabled.setValue(Boolean.TRUE);
    onToggle();
    onEnable();
    if (((Boolean)(HUD.getInstance()).notifyToggles.getValue()).booleanValue()) {
      TextComponentString text = new TextComponentString(OyVey.commandManager.getClientMessage() + " " + ChatFormatting.GREEN + getDisplayName() + " toggled on.");
      mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
    } 
    if (isOn() && this.hasListener && !this.alwaysListening)
      MinecraftForge.EVENT_BUS.register(this); 
  }
  
  public void disable() {
    if (this.hasListener && !this.alwaysListening)
      MinecraftForge.EVENT_BUS.unregister(this); 
    this.enabled.setValue(Boolean.valueOf(false));
    if (((Boolean)(HUD.getInstance()).notifyToggles.getValue()).booleanValue()) {
      TextComponentString text = new TextComponentString(OyVey.commandManager.getClientMessage() + " " + ChatFormatting.RED + getDisplayName() + " toggled off.");
      mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
    } 
    onToggle();
    onDisable();
  }
  
  public void toggle() {
    ClientEvent event = new ClientEvent(!isEnabled() ? 1 : 0, this);
    MinecraftForge.EVENT_BUS.post((Event)event);
    if (!event.isCanceled())
      setEnabled(!isEnabled()); 
  }
  
  public void SendMessage(String s) {
    mc.player.sendMessage((ITextComponent)new Command.ChatMessage(OyVey.commandManager.getClientMessage() + " " + s));
  }
  
  public String getDisplayName() {
    return (String)this.displayName.getValue();
  }
  
  public void setDisplayName(String name) {
    Module module = OyVey.moduleManager.getModuleByDisplayName(name);
    Module originalModule = OyVey.moduleManager.getModuleByName(name);
    if (module == null && originalModule == null) {
      Command.sendMessage(getDisplayName() + ", name: " + getName() + ", has been renamed to: " + name);
      this.displayName.setValue(name);
      return;
    } 
    Command.sendMessage(ChatFormatting.RED + "A module of this name already exists.");
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public boolean isDrawn() {
    return ((Boolean)this.drawn.getValue()).booleanValue();
  }
  
  public void setDrawn(boolean drawn) {
    this.drawn.setValue(Boolean.valueOf(drawn));
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public String getInfo() {
    return null;
  }
  
  public Bind getBind() {
    return (Bind)this.bind.getValue();
  }
  
  public void setBind(int key) {
    this.bind.setValue(new Bind(key));
  }
  
  public boolean isOpenHubEditor() {
    return (mc.currentScreen instanceof OyVeyGui && ((HubEditor)OyVey.moduleManager.getModuleByClass(HubEditor.class)).isEnabled());
  }
  
  public boolean listening() {
    return ((this.hasListener && isOn()) || this.alwaysListening);
  }
  
  public String getFullArrayString() {
    return getDisplayName() + ChatFormatting.GRAY + ((getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
  }
  
  public enum Category {
    COMBAT("Combat"),
    MISC("Misc"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    CLIENT("Client"),
    HUB("Hub");
    
    private final String name;
    
    Category(String name) {
      this.name = name;
    }
    
    public String getName() {
      return this.name;
    }
  }
}
