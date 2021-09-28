package fc.client.candy.mixin;

import fc.client.candy.OyVey;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class OyVeyLoader implements IFMLLoadingPlugin {
  private static boolean isObfuscatedEnvironment = false;
  
  public OyVeyLoader() {
    MixinBootstrap.init();
    Mixins.addConfiguration("mixins.candy.json");
    MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    OyVey.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
  }
  
  public String[] getASMTransformerClass() {
    return new String[0];
  }
  
  public String getModContainerClass() {
    return null;
  }
  
  public String getSetupClass() {
    return null;
  }
  
  public void injectData(Map<String, Object> data) {
    isObfuscatedEnvironment = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
  }
  
  public String getAccessTransformerClass() {
    return null;
  }
}
