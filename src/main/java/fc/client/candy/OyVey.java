package fc.client.candy;

import fc.client.candy.features.Feature;
import fc.client.candy.features.modules.Module;
import fc.client.candy.manager.ColorManager;
import fc.client.candy.manager.CommandManager;
import fc.client.candy.manager.ConfigManager;
import fc.client.candy.manager.EventManager;
import fc.client.candy.manager.FileManager;
import fc.client.candy.manager.FriendManager;
import fc.client.candy.manager.HoleManager;
import fc.client.candy.manager.InventoryManager;
import fc.client.candy.manager.ModuleManager;
import fc.client.candy.manager.PacketManager;
import fc.client.candy.manager.PositionManager;
import fc.client.candy.manager.PotionManager;
import fc.client.candy.manager.ReloadManager;
import fc.client.candy.manager.RotationManager;
import fc.client.candy.manager.ServerManager;
import fc.client.candy.manager.SpeedManager;
import fc.client.candy.manager.TextManager;
import java.io.IOException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "candy", name = "Candy", version = "0.0.8")
public class OyVey {
  public static final String MODID = "candy";
  
  public static final String MODNAME = "Candy";
  
  public static final String MODVER = "0.0.8";
  
  public static final Logger LOGGER = LogManager.getLogger("Candy");
  
  public static CommandManager commandManager;
  
  public static FriendManager friendManager;
  
  public static ModuleManager moduleManager;
  
  public static PacketManager packetManager;
  
  public static ColorManager colorManager;
  
  public static HoleManager holeManager;
  
  public static InventoryManager inventoryManager;
  
  public static PotionManager potionManager;
  
  public static RotationManager rotationManager;
  
  public static PositionManager positionManager;
  
  public static SpeedManager speedManager;
  
  public static ReloadManager reloadManager;
  
  public static FileManager fileManager;
  
  public static ConfigManager configManager;
  
  public static ServerManager serverManager;
  
  public static EventManager eventManager;
  
  public static TextManager textManager;
  
  @Instance
  public static OyVey INSTANCE;
  
  private static boolean unloaded = false;
  
  public static void load() {
    LOGGER.info("\n\nLoading...");
    unloaded = false;
    if (reloadManager != null) {
      reloadManager.unload();
      reloadManager = null;
    } 
    textManager = new TextManager();
    commandManager = new CommandManager();
    friendManager = new FriendManager();
    moduleManager = new ModuleManager();
    rotationManager = new RotationManager();
    packetManager = new PacketManager();
    eventManager = new EventManager();
    speedManager = new SpeedManager();
    potionManager = new PotionManager();
    inventoryManager = new InventoryManager();
    serverManager = new ServerManager();
    fileManager = new FileManager();
    colorManager = new ColorManager();
    positionManager = new PositionManager();
    configManager = new ConfigManager();
    holeManager = new HoleManager();
    LOGGER.info("Managers loaded.");
    moduleManager.init();
    LOGGER.info("Modules loaded.");
    configManager.init();
    eventManager.init();
    LOGGER.info("EventManager loaded.");
    textManager.init(true);
    moduleManager.onLoad();
    LOGGER.info("successfully loaded!\n");
  }
  
  public static void unload(boolean unload) {
    LOGGER.info("\n\nUnloading...");
    if (unload) {
      reloadManager = new ReloadManager();
      reloadManager.init((commandManager != null) ? commandManager.getPrefix() : ".");
    }
    onUnload();
    eventManager = null;
    friendManager = null;
    speedManager = null;
    holeManager = null;
    positionManager = null;
    rotationManager = null;
    configManager = null;
    commandManager = null;
    colorManager = null;
    serverManager = null;
    fileManager = null;
    potionManager = null;
    inventoryManager = null;
    moduleManager = null;
    textManager = null;
    LOGGER.info("unloaded!\n");
  }
  
  public static void reload() {
    unload(false);
    load();
  }
  
  public static void onUnload() {
    if (!unloaded) {
      eventManager.onUnload();
      moduleManager.onUnload();
      configManager.saveConfig(configManager.config.replaceFirst("candy/", ""));
      moduleManager.onUnloadPost();
      unloaded = true;
    } 
  }
  
  @EventHandler
  public void init(FMLInitializationEvent event) {
    Display.setTitle("candy v.0.0.8");
    load();
  }
}
