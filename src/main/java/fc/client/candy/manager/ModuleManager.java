package fc.client.candy.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.event.events.Render2DEvent;
import fc.client.candy.event.events.Render3DEvent;
import fc.client.candy.features.Feature;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.client.ClickGui;
import fc.client.candy.features.modules.client.FontMod;
import fc.client.candy.features.modules.client.HUD;
import fc.client.candy.features.modules.client.HubEditor;
import fc.client.candy.features.modules.combat.AutoArmor;
import fc.client.candy.features.modules.combat.AutoCrystal;
import fc.client.candy.features.modules.combat.AutoTrap;
import fc.client.candy.features.modules.combat.AutoWeb;
import fc.client.candy.features.modules.combat.AutoXP;
import fc.client.candy.features.modules.combat.Blocker;
import fc.client.candy.features.modules.combat.CevBreaker;
import fc.client.candy.features.modules.combat.CityBoss;
import fc.client.candy.features.modules.combat.Criticals;
import fc.client.candy.features.modules.combat.Holefiller;
import fc.client.candy.features.modules.combat.Killaura;
import fc.client.candy.features.modules.combat.PistonCrystal;
import fc.client.candy.features.modules.combat.SelfBow;
import fc.client.candy.features.modules.combat.SelfFill;
import fc.client.candy.features.modules.combat.Selftrap;
import fc.client.candy.features.modules.combat.Surround;
import fc.client.candy.features.modules.hub.Welcomer;
import fc.client.candy.features.modules.misc.AntiChunkBan;
import fc.client.candy.features.modules.misc.AutoChest;
import fc.client.candy.features.modules.misc.AutoGG;
import fc.client.candy.features.modules.misc.BlockSpam;
import fc.client.candy.features.modules.misc.ChatSuffix;
import fc.client.candy.features.modules.misc.CodeStealer;
import fc.client.candy.features.modules.misc.PacketCanceller;
import fc.client.candy.features.modules.misc.PopAnnouncer;
import fc.client.candy.features.modules.misc.Spinner;
import fc.client.candy.features.modules.movement.BoatFly;
import fc.client.candy.features.modules.movement.HoleTP;
import fc.client.candy.features.modules.movement.ReverseStep;
import fc.client.candy.features.modules.player.FakePlayer;
import fc.client.candy.features.modules.player.Speedmine;
import fc.client.candy.features.modules.render.BurrowESP;
import fc.client.candy.features.modules.render.CityESP;
import fc.client.candy.features.modules.render.HoleESP;
import fc.client.candy.features.modules.render.NoRender;
import fc.client.candy.features.modules.render.Skeleton;
import fc.client.candy.features.modules.render.Wireframe;
import fc.client.candy.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class ModuleManager extends Feature {
  public ArrayList<Module> modules = new ArrayList<>();
  
  public List<Module> sortedModules = new ArrayList<>();
  
  public List<String> sortedModulesABC = new ArrayList<>();
  
  public Animation animationThread;
  
  public void init() {
    this.modules.add(new ClickGui());
    FontMod fontMod = new FontMod();
    this.modules.add(fontMod);
    fontMod.enable();
    this.modules.add(new HUD());
    this.modules.add(new HubEditor());
    this.modules.add(new FakePlayer());
    this.modules.add(new Speedmine());
    this.modules.add(new AutoGG());
    this.modules.add(new PacketCanceller());
    this.modules.add(new ChatSuffix());
//    this.modules.add(new DiscordRPCModule());
    this.modules.add(new AutoChest());
    this.modules.add(new Spinner());
    this.modules.add(new CodeStealer());
    this.modules.add(new PopAnnouncer());
    this.modules.add(new AntiChunkBan());
    this.modules.add(new BlockSpam());
    this.modules.add(new AutoCrystal());
    this.modules.add(new AutoWeb());
    this.modules.add(new PistonCrystal());
    this.modules.add(new SelfFill());
    this.modules.add(new Blocker());
    this.modules.add(new AutoArmor());
    this.modules.add(new AutoTrap());
    this.modules.add(new AutoXP());
    this.modules.add(new Surround());
    this.modules.add(new Selftrap());
    this.modules.add(new Killaura());
    this.modules.add(new Criticals());
    this.modules.add(new SelfBow());
    this.modules.add(new CityBoss());
    this.modules.add(new Holefiller());
    this.modules.add(new CevBreaker());
    this.modules.add(new NoRender());
    this.modules.add(new BurrowESP());
    this.modules.add(new CityESP());
    this.modules.add(new HoleESP());
    this.modules.add(new Skeleton());
    this.modules.add(new Wireframe());
    this.modules.add(new BoatFly());
    this.modules.add(new ReverseStep());
    this.modules.add(new HoleTP());
    this.modules.add(new Welcomer());
  }
  
  public Module getModuleByName(String name) {
    for (Module module : this.modules) {
      if (!module.getName().equalsIgnoreCase(name))
        continue; 
      return module;
    } 
    return null;
  }
  
  public <T extends Module> T getModuleByClass(Class<T> clazz) {
    for (Module module : this.modules) {
      if (!clazz.isInstance(module))
        continue; 
      return (T)module;
    } 
    return null;
  }
  
  public void enableModule(Class<Module> clazz) {
    Module module = getModuleByClass(clazz);
    if (module != null)
      module.enable(); 
  }
  
  public void disableModule(Class<Module> clazz) {
    Module module = getModuleByClass(clazz);
    if (module != null)
      module.disable(); 
  }
  
  public void enableModule(String name) {
    Module module = getModuleByName(name);
    if (module != null)
      module.enable(); 
  }
  
  public void disableModule(String name) {
    Module module = getModuleByName(name);
    if (module != null)
      module.disable(); 
  }
  
  public boolean isModuleEnabled(String name) {
    Module module = getModuleByName(name);
    return (module != null && module.isOn());
  }
  
  public boolean isModuleEnabled(Class<Module> clazz) {
    Module module = getModuleByClass(clazz);
    return (module != null && module.isOn());
  }
  
  public Module getModuleByDisplayName(String displayName) {
    for (Module module : this.modules) {
      if (!module.getDisplayName().equalsIgnoreCase(displayName))
        continue; 
      return module;
    } 
    return null;
  }
  
  public ArrayList<Module> getEnabledModules() {
    ArrayList<Module> enabledModules = new ArrayList<>();
    for (Module module : this.modules) {
      if (!module.isEnabled())
        continue; 
      enabledModules.add(module);
    } 
    return enabledModules;
  }
  
  public ArrayList<String> getEnabledModulesName() {
    ArrayList<String> enabledModules = new ArrayList<>();
    for (Module module : this.modules) {
      if (!module.isEnabled() || !module.isDrawn())
        continue; 
      enabledModules.add(module.getFullArrayString());
    } 
    return enabledModules;
  }
  
  public ArrayList<Module> getModulesByCategory(Module.Category category) {
    ArrayList<Module> modulesCategory = new ArrayList<>();
    this.modules.forEach(module -> {
          if (module.getCategory() == category)
            modulesCategory.add(module); 
        });
    return modulesCategory;
  }
  
  public List<Module.Category> getCategories() {
    return Arrays.asList(Module.Category.values());
  }
  
  public void onLoad() {
    this.modules.stream().filter(Module::listening).forEach(MinecraftForge.EVENT_BUS::register);
    this.modules.forEach(Module::onLoad);
  }
  
  public void onUpdate() {
    this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
  }
  
  public void onTick() {
    this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
  }
  
  public void onRender2D(Render2DEvent event) {
    this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
  }
  
  public void onRender3D(Render3DEvent event) {
    this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
  }
  
  public void sortModules(boolean reverse) {
    this.sortedModules = (List<Module>)getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> Integer.valueOf(this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1)))).collect(Collectors.toList());
  }
  
  public void sortModulesABC() {
    this.sortedModulesABC = new ArrayList<>(getEnabledModulesName());
    this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
  }
  
  public void onLogout() {
    this.modules.forEach(Module::onLogout);
  }
  
  public void onLogin() {
    this.modules.forEach(Module::onLogin);
  }
  
  public void onUnload() {
    this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
    this.modules.forEach(Module::onUnload);
  }
  
  public void onUnloadPost() {
    for (Module module : this.modules)
      module.enabled.setValue(Boolean.valueOf(false)); 
  }
  
  public void onKeyPressed(int eventKey) {
    if (eventKey == 0 || !Keyboard.getEventKeyState() || mc.currentScreen instanceof fc.client.candy.features.gui.OyVeyGui)
      return; 
    this.modules.forEach(module -> {
          if (module.getBind().getKey() == eventKey)
            module.toggle(); 
        });
  }
  
  private class Animation extends Thread {
    public Module module;
    
    public float offset;
    
    public float vOffset;
    
    ScheduledExecutorService service;
    
    public Animation() {
      super("Animation");
      this.service = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void run() {
      if ((HUD.getInstance()).renderingMode.getValue() == HUD.RenderingMode.Length) {
        for (Module module : ModuleManager.this.sortedModules) {
          String text = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
          module.offset = ModuleManager.this.renderer.getStringWidth(text) / ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).floatValue();
          module.vOffset = ModuleManager.this.renderer.getFontHeight() / ((Integer)(HUD.getInstance()).animationVerticalTime.getValue()).floatValue();
          if (module.isEnabled() && ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).intValue() != 1) {
            if (module.arrayListOffset <= module.offset || Util.mc.world == null)
              continue; 
            module.arrayListOffset -= module.offset;
            module.sliding = true;
            continue;
          } 
          if (!module.isDisabled() || ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).intValue() == 1)
            continue; 
          if (module.arrayListOffset < ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
            module.arrayListOffset += module.offset;
            module.sliding = true;
            continue;
          } 
          module.sliding = false;
        } 
      } else {
        for (String e : ModuleManager.this.sortedModulesABC) {
          Module module = OyVey.moduleManager.getModuleByName(e);
          String text = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
          module.offset = ModuleManager.this.renderer.getStringWidth(text) / ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).floatValue();
          module.vOffset = ModuleManager.this.renderer.getFontHeight() / ((Integer)(HUD.getInstance()).animationVerticalTime.getValue()).floatValue();
          if (module.isEnabled() && ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).intValue() != 1) {
            if (module.arrayListOffset <= module.offset || Util.mc.world == null)
              continue; 
            module.arrayListOffset -= module.offset;
            module.sliding = true;
            continue;
          } 
          if (!module.isDisabled() || ((Integer)(HUD.getInstance()).animationHorizontalTime.getValue()).intValue() == 1)
            continue; 
          if (module.arrayListOffset < ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
            module.arrayListOffset += module.offset;
            module.sliding = true;
            continue;
          } 
          module.sliding = false;
        } 
      } 
    }
    
    public void start() {
      System.out.println("Starting animation thread.");
      this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
    }
  }
}
