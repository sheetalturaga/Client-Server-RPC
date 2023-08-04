package ClientServerRPC;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class traverse the commandline arguments to retrieve the port number
 * provided when the client file is run also logs the client's activity
 */
public class ClientLogger {
  public int portNumber;

  public void traverseArgs(String[] args) {
    if(args[0].equalsIgnoreCase("EXIT")) {
      System.exit(portNumber);
    } else {
      portNumber = Integer.parseInt(args[0]);
    }
  }

  public void clientLog(String message) {
    System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()
        + "-" + message));
  }

}
