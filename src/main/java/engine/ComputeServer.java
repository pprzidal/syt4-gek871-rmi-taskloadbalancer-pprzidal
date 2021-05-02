package engine;

import compute.Compute;
import compute.Loadbalanceing;
import compute.Task;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
            System.out.println("Waiting for exit");
            while(!sc.nextLine().equals("exit"));
            System.out.println("exiting");
        } catch(Exception e) {
            System.err.println("Fehler " + e);
        } finally {
            try {
                loadbalancer.unregister(stub);
                UnicastRemoteObject.unexportObject(server, false);
                System.out.println("exported the BS");
            } catch (Exception e) {
                System.err.println("Failed to unexportObject");
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        System.out.println("Hier");
        return t.execute();
    }
}
