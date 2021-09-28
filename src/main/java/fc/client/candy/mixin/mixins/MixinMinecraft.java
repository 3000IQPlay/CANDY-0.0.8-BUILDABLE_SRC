package fc.client.candy.mixin.mixins;

import fc.client.candy.OyVey;
import fc.client.candy.event.events.KeyEvent;
import fc.client.candy.features.modules.player.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft {
  @Inject(method = {"shutdownMinecraftApplet"}, at = {@At("HEAD")})
  private void stopClient(CallbackInfo callbackInfo) {
    unload();
  }
  
  @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
  public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
    unload();
  }
  
  @Inject(method = {"runTickKeyboard"}, at = {@At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE)})
  private void onKeyboard(CallbackInfo callbackInfo) {
    int i = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey(), n = i;
    if (Keyboard.getEventKeyState()) {
      KeyEvent event = new KeyEvent(i);
      MinecraftForge.EVENT_BUS.post((Event)event);
    } 
  }
  
  private void unload() {
    OyVey.LOGGER.info("Initiated client shutdown.");
    OyVey.onUnload();
    OyVey.LOGGER.info("Finished client shutdown.");
  }
  
  @Redirect(method = {"sendClickBlockToController"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
  private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
    return (!MultiTask.getInstance().isOn() && playerSP.isHandActive());
  }
  
  @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
  private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
    return (!MultiTask.getInstance().isOn() && playerControllerMP.getIsHittingBlock());
  }
}
