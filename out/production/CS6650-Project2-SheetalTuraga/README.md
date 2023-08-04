# CS6650 Project2: Multithreaded Key Value Store - Client Server using RMI for RPC Communication

## Introduction:
This project is about enabling client server communications using RMI for RPC communication

### Implementation:
The operations required here are PUT, GET, and DELETE have been implemented
Initial data is pre-populated using the client , 
The processed data is sent back to the client from the server and logged in their individual files.

###Files :
Client implements the client side code with a helper class called 'ClientLogger'
ServerManager brings together the server side code with Server.java implementing most of the procedures
provided in the ServerInterface

## Note:
While the ServerInterface is compiled correctly and the command 'rmiregistry &' does kick off the Registry, 
I faced compile time errors that I couldn't resolve before the deadline. 