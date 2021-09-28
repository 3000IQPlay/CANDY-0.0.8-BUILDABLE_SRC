//package fc.client.candy.features.modules.misc;
//
//import club.minnced.discord.rpc.DiscordEventHandlers;
//import club.minnced.discord.rpc.DiscordRPC;
//import club.minnced.discord.rpc.DiscordRichPresence;
//import club.minnced.discord.rpc.DiscordUser;
//import fc.client.candy.features.modules.Module;
//
//public class DiscordRPCModule extends Module {
//  private DiscordRPC rpc;
//
//  String appid;
//
//  private Thread _thread;
//
//  public DiscordRPCModule() {
//    super("DiscordRPC", "look", Module.Category.MISC, true, false, true);
//    this.rpc = DiscordRPC.INSTANCE;
//    this.appid = "822820474006143027";
//    this._thread = null;
//  }
//
//  public void onEnable() {
//    DiscordRPC lib = DiscordRPC.INSTANCE;
//    String steamId = "";
//    DiscordEventHandlers handlers = new DiscordEventHandlers();
//    handlers.ready = (user -> System.out.println("Ready!"));
//    lib.Discord_Initialize(this.appid, handlers, true, steamId);
//    DiscordRichPresence presence = new DiscordRichPresence();
//    lib.Discord_UpdatePresence(presence);
//    presence.largeImageKey = "icon";
//    presence.largeImageText = "Ver 0.0.8";
//    presence.startTimestamp = System.currentTimeMillis() / 2000L;
//    this._thread = new Thread(() -> {
//          while (!Thread.currentThread().isInterrupted()) {
//            lib.Discord_RunCallbacks();
//            String state = "Loading...";
//            String deal = "";
//            if (mc.player != null) {
//              if (mc.world != null)
//                state = "Playing single world";
//              if (mc.getCurrentServerData() != null && mc.world != null)
//                state = "Playing " + (mc.getCurrentServerData()).serverIP;
//            }
//            if (mc.player != null)
//              deal = mc.player.getDisplayNameString();
//            presence.details = state;
//            presence.state = deal;
//            lib.Discord_UpdatePresence(presence);
//            try {
//              Thread.sleep(2000L);
//            } catch (InterruptedException interruptedException) {}
//          }
//        }"RPC-Callback-Handler");
//    this._thread.start();
//  }
//
//  public void onDisable() {
//    if (this._thread != null)
//      this._thread.interrupt();
//  }
//
//  public void onUpdate() {}
//}
