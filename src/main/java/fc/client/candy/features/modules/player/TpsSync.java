package fc.client.candy.features.modules.player;

import fc.client.candy.features.modules.Module;
import fc.client.candy.features.setting.Setting;

public class TpsSync extends Module {
  private static TpsSync INSTANCE = new TpsSync();
  
  public Setting<Boolean> attack = register(new Setting("Attack", Boolean.FALSE));
  
  public Setting<Boolean> mining = register(new Setting("Mine", Boolean.TRUE));
  
  public TpsSync() {
    super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  public static TpsSync getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TpsSync(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
