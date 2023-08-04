package ClientServerRPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

/**
 * The Client that locates the registry and the server to marshall the parameters
 * to the Server to implement the procedure provided in the stub
 *
 */

public class Client {

  private static UUID uniqueIdentifierID() {
    UUID uniqueId = UUID.randomUUID();
    return uniqueId;
  }

  public static void main(String[] args) throws RemoteException {

    ClientLogger clientLogger = new ClientLogger();
    clientLogger.traverseArgs(args);
    try {
      Registry registry = LocateRegistry.getRegistry("LOCALHOST", clientLogger.portNumber);
      ServerInterface stub = (ServerInterface) registry.lookup("ServerInterface");
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(), "PUT", "k1", "v1"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k2", "v2"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k3", "v3"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k4", "v4"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k5", "v5"));

      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"GET", "k1", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"GET", "k2", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"GET", "k3", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"GET", "k4", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"GET", "k5", ""));

      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"DELETE", "k1", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"DELETE", "k2", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"DELETE", "k3", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"DELETE", "k4", ""));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"DELETE", "k5", ""));

      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k6", "v6"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k7", "v7"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k8", "v8"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k9", "v9"));
      clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),"PUT", "k10", "v10"));

      System.out.print("Please provide the operation key value: ");
      System.out.print("The PUT operation format: <PUT> <KEY> <VALUE>");
      System.out.print("The GET operation format: <GET> <KEY>");
      System.out.print("The DELETE operation format: <DELETE> <KEY>");

      String userInput = retrieveInputFromTerminal();

      String[] inputArray = userInput.split(" ");

      if(inputArray.length == 3 && inputArray[0].equalsIgnoreCase("PUT")) {
        clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),inputArray[0], inputArray[1], inputArray[2]));
      }
      if (inputArray.length == 2 && inputArray[0].equalsIgnoreCase("GET")) {
        clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),inputArray[0], inputArray[1],  inputArray[2]));
      } else if(inputArray.length == 2 && inputArray[0].equalsIgnoreCase("DELETE")){
        clientLogger.clientLog(stub.operationProcessor(uniqueIdentifierID(),inputArray[0], inputArray[1],  inputArray[2]));
      }

    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static String retrieveInputFromTerminal() throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    return bufferedReader.readLine();
  }

}
