package engine;

import compute.*;
import java.rmi.RemoteException;
import java.util.*;

public class RoundRobinLB implements Loadbalanceing, Compute {
    private Queue<Compute> computingServers = new ArrayDeque<>();

    @Override
    public synchronized void register(Compute stub) throws RemoteException {
        computingServers.add(stub);
    }

    @Override
    public synchronized void unregister(Compute stub) throws RemoteException {
        computingServers.remove(stub);
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        if(computingServers.size() != 0) {
            Compute a = null;
            synchronized (this) {
                a = computingServers.poll();
                computingServers.add(a);
            }
            return a.executeTask(t);
        }
        return null; //TODO vllt. ändern. Problem wenn es keine Server zum bearbeiten der Anfrage gibt. vllt. den Loadbalancer die Anfrage machen lassen
    }
}
