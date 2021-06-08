package engine;

import compute.Compute;
import compute.ComputeHealth;
import compute.Loadbalanceing;
import compute.Task;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

public class WeightedDistribution implements Compute, Loadbalanceing {
    private Set<ComputeHealth> set = new HashSet<>();

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        ComputeHealth ch = null;
        double lowest = Double.MAX_VALUE;
        synchronized (this) {
            System.out.println("Searching for lowest");
            for (ComputeHealth cc : set) {
                double local = cc.systemHealth();
                System.out.println(local);
                if (local <= lowest) {
                    lowest = local;
                    ch = cc;
                }
            }
            System.out.println("lowest is:" + lowest);
        }
        return ch.executeTask(t);
    }

    @Override
    public synchronized void register(Compute stub) throws RemoteException {
        set.add((ComputeHealth) stub);
    }

    @Override
    public synchronized void unregister(Compute stub) throws RemoteException {
        set.remove((ComputeHealth) stub);
    }
}
