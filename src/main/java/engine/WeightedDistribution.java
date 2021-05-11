package engine;

import compute.Compute;
import compute.ComputeHealth;
import compute.Loadbalanceing;
import compute.Task;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeightedDistribution implements Compute, Loadbalanceing {
    private Set<ComputeHealth> map = new HashSet<>();

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        ComputeHealth ch = null;
        double lowest = Double.MAX_VALUE;
        synchronized (this) {
            for (ComputeHealth cc : map) {
                double local = cc.systemHealth();
                if (local <= lowest) {
                    lowest = local;
                    ch = cc;
                }
            }
        }
        System.out.println(lowest);
        return ch.executeTask(t);
    }

    @Override
    public void register(Compute stub) throws RemoteException {
        map.add((ComputeHealth) stub);
    }

    @Override
    public void unregister(Compute stub) throws RemoteException {
        map.remove((ComputeHealth) stub);
    }
}
