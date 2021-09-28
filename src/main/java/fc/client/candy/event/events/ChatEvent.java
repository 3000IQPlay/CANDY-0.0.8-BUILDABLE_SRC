package fc.client.candy.event.events;

import fc.client.candy.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ChatEvent extends EventStage {
  private final String msg;
  
  public ChatEvent(String msg) {
    this.msg = msg;
  }
  
  public String getMsg() {
    return this.msg;
  }
}
