/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import compute.Compute;
import compute.Loadbalanceing;
import compute.Task;

public class ComputeEngine implements Compute {
    private static Loadbalanceing lbEngine = new LoadbalancerEngine();

    public <T> T executeTask(Task<T> t) throws RemoteException {
        return ((Compute)lbEngine).executeTask(t);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        Compute c = new ComputeEngine();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Registry registry = LocateRegistry.createRegistry(1099);
            Compute stub1 = (Compute) UnicastRemoteObject.exportObject(c, 0);
            Loadbalanceing stub = (Loadbalanceing) UnicastRemoteObject.exportObject(lbEngine, 0);
            registry.rebind("Compute", c);
            registry.rebind("Loadbalancer", lbEngine);
            System.out.println("ComputeEngine bound");
            while(!reader.readLine().equals("exit"));
            System.out.println("exiting");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        } finally {
            try {
                UnicastRemoteObject.unexportObject(lbEngine, false);
                UnicastRemoteObject.unexportObject(c, false);
                System.out.println("exported the BS");
            } catch (NoSuchObjectException e) {
                System.err.println("unable to unexport");
                e.printStackTrace();
            }
        }
    }
}

class LoadbalancerEngine implements Loadbalanceing, Compute {
    private Queue<Compute> computingServers = new ArrayDeque<>();

    @Override
    public void register(Compute stub) throws RemoteException {
        computingServers.add(stub);
        System.out.println(Arrays.toString(computingServers.toArray()));
    }

    @Override
    public void unregister(Compute stub) throws RemoteException {
        computingServers.remove(stub);
        System.out.println(Arrays.toString(computingServers.toArray()));
    }

    @Override
    public <T> T executeTask(Task<T> t) throws RemoteException {
        System.out.println(computingServers.size());
        if(computingServers.size() == 0) {
            System.out.println("is 0");
            return null; //TODO messy
        }
        Compute a = computingServers.poll();
        computingServers.add(a);
        return a.executeTask(t);
    }
}