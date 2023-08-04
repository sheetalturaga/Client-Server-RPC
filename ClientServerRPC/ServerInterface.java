package ClientServerRPC;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * The interface that defines the procedure call that is implemented on the remote server
 */

public interface ServerInterface extends Remote {
   String operationProcessor(UUID uniqueID, String operation, String keyToAdd, String valToAdd) throws RemoteException;
   void prepToSend(UUID uniqueId, String operation, String key, String value, int portNumber);
   void acknowledgementToClient(UUID uniqueId, int portNumber, AcknowledgementType type);
   void send(UUID uniqueId, int portNumber) throws RemoteException;

}
