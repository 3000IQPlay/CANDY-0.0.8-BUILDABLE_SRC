package fc.client.candy.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.OyVey;
import fc.client.candy.features.command.Command;
import fc.client.candy.features.modules.Module;
import fc.client.candy.features.modules.combat.PistonCrystal;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;

public class PopCounter extends Module {
  public static HashMap<String, Integer> TotemPopContainer = new HashMap<>();
  
  private static PopCounter INSTANCE = new PopCounter();
  
  public PopCounter() {
    super("PopCounter", "Counts other players totem pops.", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  public static PopCounter getInstance() {
    if (INSTANCE == null)
      INSTANCE = new PopCounter(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onEnable() {
    TotemPopContainer.clear();
  }
  
  public void onDeath(EntityPlayer player) {
    if (TotemPopContainer.containsKey(player.getName())) {
      int l_Count = ((Integer)TotemPopContainer.get(player.getName())).intValue();
      TotemPopContainer.remove(player.getName());
      if (l_Count == 1) {
        Command.sendMessage(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totem!");
      } else {
        Command.sendMessage(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totems!");
      } 
    } 
  }
  
  public void onTotemPop(EntityPlayer player) {
    if (fullNullCheck())
      return; 
    if (mc.player.equals(player))
      return; 
    int l_Count = 1;
    if (TotemPopContainer.containsKey(player.getName())) {
      l_Count = ((Integer)TotemPopContainer.get(player.getName())).intValue();
      TotemPopContainer.put(player.getName(), Integer.valueOf(++l_Count));
    } else {
      TotemPopContainer.put(player.getName(), Integer.valueOf(l_Count));
    } 
    if (l_Count == 1) {
      Command.sendMessage(ChatFormatting.RED + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totem.");
    } else {
      Command.sendMessage(ChatFormatting.RED + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.RED + " Totems.");
    } 
    if (((PistonCrystal)OyVey.moduleManager.getModuleByClass(PistonCrystal.class)).isEnabled() && ((PistonCrystal)OyVey.moduleManager.getModuleByClass(PistonCrystal.class)).target != null)
      if (player.entityId == ((PistonCrystal)OyVey.moduleManager.getModuleByClass(PistonCrystal.class)).target.entityId && ((PopAnnouncer)OyVey.moduleManager
        .getModuleByClass(PopAnnouncer.class)).isEnabled())
        ((PopAnnouncer)OyVey.moduleManager.getModuleByClass(PopAnnouncer.class)).doAnnounce(player);  
  }
}
