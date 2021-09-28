package fc.client.candy.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import fc.client.candy.features.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {
  public ReloadSoundCommand() {
    super("sound", new String[0]);
  }
  
  public void execute(String[] commands) {
    try {
      SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), new String[] { "sndManager", "sndManager" });
      sndManager.reloadSoundSystem();
      Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
    } catch (Exception e) {
      System.out.println(ChatFormatting.RED + "Could not restart sound manager: " + e.toString());
      e.printStackTrace();
      Command.sendMessage(ChatFormatting.RED + "Couldnt Reload Sound System!");
    } 
  }
}
