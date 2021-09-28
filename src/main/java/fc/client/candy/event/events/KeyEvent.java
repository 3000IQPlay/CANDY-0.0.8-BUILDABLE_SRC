package fc.client.candy.event.events;

import fc.client.candy.event.EventStage;

public class KeyEvent extends EventStage {
  private final int key;
  
  public KeyEvent(int key) {
    this.key = key;
  }
  
  public int getKey() {
    return this.key;
  }
}
