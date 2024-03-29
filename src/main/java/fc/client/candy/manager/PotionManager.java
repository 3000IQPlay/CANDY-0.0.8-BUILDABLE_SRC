package fc.client.candy.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.features.Feature;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager extends Feature {
  private final Map<EntityPlayer, PotionList> potions = new ConcurrentHashMap<>();
  
  public List<PotionEffect> getOwnPotions() {
    return getPlayerPotions((EntityPlayer)mc.player);
  }
  
  public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
    PotionList list = this.potions.get(player);
    List<PotionEffect> potions = new ArrayList<>();
    if (list != null)
      potions = list.getEffects(); 
    return potions;
  }
  
  public PotionEffect[] getImportantPotions(EntityPlayer player) {
    PotionEffect[] array = new PotionEffect[3];
    for (PotionEffect effect : getPlayerPotions(player)) {
      Potion potion = effect.getPotion();
      switch (I18n.format(potion.getName(), new Object[0]).toLowerCase()) {
        case "strength":
          array[0] = effect;
        case "weakness":
          array[1] = effect;
        case "speed":
          array[2] = effect;
      } 
    } 
    return array;
  }
  
  public String getPotionString(PotionEffect effect) {
    Potion potion = effect.getPotion();
    return I18n.format(potion.getName(), new Object[0]) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.WHITE + Potion.getPotionDurationString(effect, 1.0F);
  }
  
  public String getColoredPotionString(PotionEffect effect) {
    return getPotionString(effect);
  }
  
  public static class PotionList {
    private final List<PotionEffect> effects = new ArrayList<>();
    
    public void addEffect(PotionEffect effect) {
      if (effect != null)
        this.effects.add(effect); 
    }
    
    public List<PotionEffect> getEffects() {
      return this.effects;
    }
  }
}
