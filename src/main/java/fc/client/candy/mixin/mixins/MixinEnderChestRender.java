package fc.client.candy.mixin.mixins;

import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityEnderChestRenderer.class})
public class MixinEnderChestRender {
  @Inject(method = {"render"}, at = {@At("INVOKE")}, cancellable = true)
  private void renderEnderChest(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
    System.out.println("Call event yet.");
  }
}
