package fc.client.candy.manager;

import fc.client.candy.features.Feature;
import fc.client.candy.features.modules.client.HUD;
import fc.client.candy.util.Timer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;

public class ServerManager extends Feature {
  private final float[] tpsCounts = new float[10];
  
  private final DecimalFormat format = new DecimalFormat("##.00#");
  
  private final Timer timer = new Timer();
  
  private float TPS = 20.0F;
  
  private long lastUpdate = -1L;
  
  private String serverBrand = "";
  
  public void onPacketReceived() {
    this.timer.reset();
  }
  
  public boolean isServerNotResponding() {
    return this.timer.passedMs(((Integer)(HUD.getInstance()).lagTime.getValue()).intValue());
  }
  
  public long serverRespondingTime() {
    return this.timer.getPassedTimeMs();
  }
  
  public void update() {
    long currentTime = System.currentTimeMillis();
    if (this.lastUpdate == -1L) {
      this.lastUpdate = currentTime;
      return;
    } 
    long timeDiff = currentTime - this.lastUpdate;
    float tickTime = (float)timeDiff / 20.0F;
    if (tickTime == 0.0F)
      tickTime = 50.0F; 
    float tps;
    if ((tps = 1000.0F / tickTime) > 20.0F)
      tps = 20.0F; 
    System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
    this.tpsCounts[0] = tps;
    double total = 0.0D;
    for (float f : this.tpsCounts)
      total += f; 
    if ((total /= this.tpsCounts.length) > 20.0D)
      total = 20.0D; 
    this.TPS = Float.parseFloat(this.format.format(total));
    this.lastUpdate = currentTime;
  }
  
  public void reset() {
    Arrays.fill(this.tpsCounts, 20.0F);
    this.TPS = 20.0F;
  }
  
  public float getTpsFactor() {
    return 20.0F / this.TPS;
  }
  
  public float getTPS() {
    return this.TPS;
  }
  
  public String getServerBrand() {
    return this.serverBrand;
  }
  
  public void setServerBrand(String brand) {
    this.serverBrand = brand;
  }
  
  public int getPing() {
    if (fullNullCheck())
      return 0; 
    try {
      return ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime();
    } catch (Exception e) {
      return 0;
    } 
  }
}
