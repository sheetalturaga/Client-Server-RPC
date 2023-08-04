package ClientServerRPC;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * The server manager that creates the RMI registry
 * and handles multiple clients using Threads
 */

public class ServerManager extends Thread {

  static ServerLogger serverLogger = new ServerLogger();
  static Server server = new Server();

  public static void main(String[] args) throws Exception {
    try {
      serverLogger.traverseArgs(args);
      ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
      Registry registry = LocateRegistry.createRegistry(serverLogger.serverPortNumber);
      registry.bind("ServerInterface", stub);
      serverLogger.log("Server running successfully on port: " + serverLogger.serverPortNumber);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Thread thread = new Thread();
    thread.start();
  }
}
