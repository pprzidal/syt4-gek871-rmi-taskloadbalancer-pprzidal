package engine;

import com.sun.management.OperatingSystemMXBean;
import compute.*;

import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ComputeServer implements ComputeHealth {
    private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

    public static void main(String[] args){
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        ComputeHealth server = new ComputeServer(), stub = null;
        Loadbalanceing loadbalancer = null;
        try(Scanner sc = new Scanner(System.in)) {
            Registry registry = LocateRegistry.getRegistry("localhost");
            stub = (ComputeHealth) UnicastRemoteObject.exportObject(server, 0);
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

    @Override
    public Double systemHealth() throws RemoteException {
        double d = osBean.getProcessCpuLoad();
        System.out.println("Processor usage of this process: " + d);
        return d;
    }
}