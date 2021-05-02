package engine;

import compute.Compute;
import compute.Loadbalanceing;
import compute.Task;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Queue;

public class LoadbalancerEngine implements Loadbalanceing, Compute {
    private Queue<Compute> computingServers = new ArrayDeque<>();

    @Override
    public void register(Compute stub) throws RemoteException {
        computingServers.add(stub);
    }

    @Override
    public void unregister(Compute stub) throws RemoteException {
        computingServers.remove(stub);
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        if(computingServers.size() == 0) {
            return null; //TODO messy
        }
        Compute a = computingServers.poll();
        computingServers.add(a);
        return a.executeTask(t);
    }
}
