package compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loadbalanceing extends Remote {
    void register(Compute stub) throws RemoteException;
    void unregister(Compute stub) throws RemoteException;
}
