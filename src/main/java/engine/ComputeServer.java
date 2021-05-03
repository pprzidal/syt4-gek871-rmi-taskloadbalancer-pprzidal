package engine;

import compute.*;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ComputeServer implements Compute {
    public static void main(String[] args){
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        Compute server = new ComputeServer(), stub = null;
        Loadbalanceing loadbalancer = null;
        try(Scanner sc = new Scanner(System.in)) {
            Registry registry = LocateRegistry.getRegistry("localhost");
            stub = (Compute) UnicastRemoteObject.exportObject(server, 0);
            loadbalancer = (Loadbalanceing) registry.lookup("Loadbalancer");
            loadbalancer.register(stub);
            System.out.println("Waiting for \"exit\"");
            while(!sc.nextLine().equals("exit"));
            System.out.println("exiting");
        } catch(Exception e) {
            System.err.println("Fehler " + e);
        } finally {
            try {
                loadbalancer.unregister(stub);
                UnicastRemoteObject.unexportObject(server, true);
            } catch (Exception e) {
                System.err.println("Failed to unexportObject");
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        return t.execute();
    }
}