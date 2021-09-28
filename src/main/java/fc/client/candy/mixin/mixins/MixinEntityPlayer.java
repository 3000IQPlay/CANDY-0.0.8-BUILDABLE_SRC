package fc.client.candy.mixin.mixins;

import com.mojang.authlib.GameProfile;
import fc.client.candy.OyVey;
import fc.client.candy.features.modules.player.TpsSync;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {
  public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
    super(worldIn);
  }
  
  @Inject(method = {"getCooldownPeriod"}, at = {@At("HEAD")}, cancellable = true)
  private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
    if (TpsSync.getInstance().isOn() && ((Boolean)(TpsSync.getInstance()).attack.getValue()).booleanValue())
      callbackInfoReturnable.setReturnValue(Float.valueOf((float)(1.0D / ((EntityPlayer)EntityPlayer.class.cast(this)).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue() * 20.0D * OyVey.serverManager.getTpsFactor()))); 
  }
}
