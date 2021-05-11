package engine;

import compute.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class LeastConnectionsLB implements Loadbalanceing, Compute {
    //mappt pro stub die anzahl an aktiven connections die er gerade bearbeitet
    private final Map<Compute, Integer> m = new HashMap<>();

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        if(m.size() == 0) return null; //TODO schlecht. wenn es keine ComputeServer gibt die sich beim LoadBalancer gemeldet haben dann kann die anfrage nicht bearbeitet werden.
        int least = Integer.MAX_VALUE;
        Compute compute = null;
        synchronized (this) { //TODO nochmal nachdenken ob man hier locken muss
            for (Map.Entry<Compute, Integer> c : m.entrySet()) { //TODO vielleicht mit EntrySet<Compute, Integer> arbeiten
                System.out.println(c);
                int numConn = c.getValue();
                if (numConn <= least) {
                    least = numConn;
                    compute = c.getKey();
                }
            }
            System.out.println("Took: localhost:" + compute + " because " + least + " is the least");
            System.out.println("Now: localhost:" + compute + " has " + (least + 1) + " connections");
            //TODO compute könnte null sein!!
            m.replace(compute, least, least + 1); //TODO das geht auch besser
        }
        T retVal = compute.executeTask(t);
        synchronized (this) { //TODO muss ich hier locken?
            System.out.println("Now: " + compute + " has " + least + " connections");
            m.replace(compute, least + 1, least);
        }
        return retVal;
    }

    @Override
    public synchronized void register(Compute stub) throws RemoteException {
        //TODO debug statement das sich ein neuer server angemeldet hat
        m.put(stub, 0);
    }

    @Override
    public synchronized void unregister(Compute stub) throws RemoteException {
        //TODO debug statement das sich ein server abgemeldet hat
        m.remove(stub);
    }
}
