package ClientServerRPC;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerLogger {
  public int serverPortNumber;

  public ServerLogger(){
  }

  public void traverseArgs(String[] args) {
    if(args == null) {
      String message = "Port number not provided.";
      throw new IllegalArgumentException(message);
    }
    serverPortNumber = Integer.parseInt(args[0]);
  }

  public void log(String message) {
    System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()
        + "-" + message));
  }

}
