package compute;

import java.rmi.RemoteException;

public interface ComputeHealth extends Compute{
    Double systemHealth() throws RemoteException;
}
