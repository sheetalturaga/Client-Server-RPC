package ClientServerRPC;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The server receives the message, unpacks the marshalled parameters and implements the required
 * procedure
 */

public class Server extends Thread implements ServerInterface {
  protected int portNumber;
  static ServerLogger serverLogger = new ServerLogger();
  ReadWriteLock readWriteLock = new ReadWriteLock();
  public Map<UUID, OpsRequest> uniqueIdOpsReq = Collections.synchronizedMap(new HashMap<>());
  public Map<UUID, Map<Integer, Acknowledgement>> acknowledgementInProgress =
      Collections.synchronizedMap(new HashMap<>());
  public Map<UUID, Map<Integer, Acknowledgement>> acknowledgementSent =
      Collections.synchronizedMap(new HashMap<>());

  public Server() {

  }

  public String operationProcessor(String operation, String key, String value)
      throws RemoteException {
    String outputMessage = "";
    String keyValueFileName = "KeyValueFile_"+ portNumber+".txt";
    KeyMapManager keyMapManager = new KeyMapManager(keyValueFileName);
    try {
      if(operation.equalsIgnoreCase("GET")) {
        serverLogger.log("Implementing GET operation on key: "+ key);
        readWriteLock.lockingReadFunction();
        outputMessage += key + " : " + keyMapManager.presentInMap(key);
        readWriteLock.unlockingReadFunction();
      } else if(operation.equalsIgnoreCase("PUT")) {
        serverLogger.log("Implementing PUT operation on key, value: "+ key + ", "+ value);
        readWriteLock.unlockingWriteFunction();
        outputMessage += key + " : " + keyMapManager.implementPutInMap(key, value);
        readWriteLock.unlockingWriteFunction();
      } else {
        serverLogger.log("Implementing DELETE operation on key: "+ key);
        readWriteLock.unlockingWriteFunction();
        outputMessage += key + " : " + keyMapManager.implementDeleteInMap(key);
        readWriteLock.unlockingWriteFunction();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return outputMessage;
  }

  public String operationProcessor(UUID uniqueId, String operation, String key, String value)
      throws RemoteException {
    if(operation.equalsIgnoreCase("GET")) {
      return operationProcessor(operation, key, value);
    }
    storeOperationsAndReqs(uniqueId, operation, key, value);
    reqToPrepAcknowledgement(uniqueId, operation, key, value);
    boolean prepComplete = prepAcknowledgement(uniqueId, operation, key, value);
    if(!prepComplete) {
      return "Acknowledgement Preparation failed!";
    }
    reqToSend(uniqueId);
    boolean sentComplete = prepSending(uniqueId);
    if(!sentComplete) {
      return "Acknowledgement Sending failed!";
    }
    OpsRequest opsRequest = this.uniqueIdOpsReq.get(uniqueId);
    if(opsRequest == null) {
      throw new IllegalArgumentException("Message not available");
    }

    String message = this.operationProcessor(opsRequest.operation, opsRequest.key, opsRequest.value);
    this.uniqueIdOpsReq.remove(uniqueId);
    return message;
  }

  private void reqToPrepAcknowledgement(UUID uniqueId, String operation, String key, String value){
    this.acknowledgementInProgress.put(uniqueId, Collections.synchronizedMap(new HashMap<>()));
    confirmAcknowledgementPrep(uniqueId, operation, key, value, portNumber);
  }

  private void reqToSend(UUID uniqueId) {
    this.acknowledgementSent.put(uniqueId, Collections.synchronizedMap(new HashMap<>()));
    confirmAcknowledgementSent(uniqueId, portNumber);
  }

  private void confirmAcknowledgementPrep(UUID uniqueId, String operation, String key, String value,
      int server) {
    try {
      Acknowledgement acknowledgement = new Acknowledgement();
      acknowledgement.isAcknowledged = false;
      this.acknowledgementInProgress.get(uniqueId).put(server, acknowledgement);
      Registry registry = LocateRegistry.getRegistry(server);
      ServerInterface stub = (ServerInterface) registry.lookup("ServerInterface");
      stub.prepToSend(uniqueId, operation, key, value, portNumber);
    } catch (AccessException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    serverLogger.log("Acknowledgement Prep worked on :" + server);
  }

  private void confirmAcknowledgementSent(UUID uniqueId, int portNumber) {
    try {
      Acknowledgement acknowledgement = new Acknowledgement();
      acknowledgement.isAcknowledged = false;
      this.acknowledgementSent.get(uniqueId).put(portNumber, acknowledgement);
      Registry registry = LocateRegistry.getRegistry(portNumber);
      ServerInterface stub = (ServerInterface) registry.lookup("ServerInterface");
      stub.send(uniqueId, portNumber);
    } catch (AccessException e) {
      e.printStackTrace();
      serverLogger.log("Sending the acknowledgement couldn't be successful. Deleting the info from storage");
    } catch (NotBoundException e) {
      e.printStackTrace();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    serverLogger.log("Acknowledgement sent successfully on port: "+ portNumber);
  }


  public void send(UUID uniqueId, int portNumber) throws RemoteException {
    OpsRequest opsRequest = this.uniqueIdOpsReq.get(uniqueId);
    if(opsRequest == null) {
      throw new IllegalArgumentException("No such request is available");
    }
    this.operationProcessor(opsRequest.operation, opsRequest.key, opsRequest.value);
    this.uniqueIdOpsReq.remove(uniqueId);
    this.sendAcknowledgement(uniqueId, portNumber, AcknowledgementType.Dispatching);

  }

  private boolean prepAcknowledgement(UUID uniqueId, String operation, String key, String value){
    int numberOfRetries = 3;

    while (numberOfRetries != 0) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        serverLogger.log("Waiting failed");
      }
      int totalAcknowledgements = 0;
      numberOfRetries--;
      Map<Integer, Acknowledgement> acksOnPort = this.acknowledgementInProgress.get(uniqueId);
      if(acksOnPort.get(portNumber).isAcknowledged){
        totalAcknowledgements++;
      } else {
        confirmAcknowledgementPrep(uniqueId, operation, key, value, portNumber);
      }
      return true;
    }
    return false;
  }

  private boolean prepSending(UUID uniqueId){
    int numberOfRetries = 3;

    while (numberOfRetries != 0) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        serverLogger.log("Waiting failed");
      }
      int totalAcknowledgements = 0;
      numberOfRetries--;
      Map<Integer, Acknowledgement> acksOnPort = this.acknowledgementSent.get(uniqueId);
      if(acksOnPort.get(portNumber).isAcknowledged){
        totalAcknowledgements++;
      } else {
        confirmAcknowledgementSent(uniqueId, portNumber);

      }
      return true;
    }
    return false;
  }

  public void prepToSend(UUID uniqueId, String operation, String key, String value, int portNumber) {
    if(this.acknowledgementInProgress.containsKey(uniqueId)) {
      sendAcknowledgement(uniqueId, portNumber, AcknowledgementType.Preparing);
    }
    this.storeOperationsAndReqs(uniqueId, operation, key, value);
    sendAcknowledgement(uniqueId, portNumber, AcknowledgementType.Preparing);
  }

  private void sendAcknowledgement(UUID uniqueId, int portNumber, AcknowledgementType type) {
    try {
      Registry registry = LocateRegistry.getRegistry(portNumber);
      ServerInterface stub = (ServerInterface) registry.lookup("ServerInterface");
      stub.acknowledgementToClient(uniqueId, portNumber, type);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    }
  }

    public void acknowledgementToClient(UUID uniqueId, int portNumber, AcknowledgementType type) {
    if(type == AcknowledgementType.Dispatching) {
      this.acknowledgementSent.get(uniqueId).get(portNumber).isAcknowledged = true;
    } else if (type == AcknowledgementType.Preparing){
      this.acknowledgementInProgress.get(uniqueId).get(portNumber).isAcknowledged = true;
    }
    serverLogger.log("Acknowledgement received on port: "+ portNumber);
  }


  public void storeOperationsAndReqs(UUID uniqueId, String operation, String key, String value) {
    OpsRequest opsRequest = new OpsRequest();
    opsRequest.operation = operation;
    opsRequest.key = key;
    opsRequest.value = value;

    this.uniqueIdOpsReq.put(uniqueId, opsRequest);
  }

}

class OpsRequest {
  String operation;
  String key;
  String value;
}

class Acknowledgement {
  boolean isAcknowledged;
}

