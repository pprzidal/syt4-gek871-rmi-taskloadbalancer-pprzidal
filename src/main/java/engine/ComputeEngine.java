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
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import compute.Compute;
import compute.Loadbalanceing;
import compute.Task;

public class ComputeEngine implements Compute {
    private static final Logger log = Logger.getLogger(ComputeEngine.class.getName());
    private static Loadbalanceing lbEngine;
    private static final String usage = "You should provide an \"lclb\" for Least Connections Load Balanceing or \"rrlb\" for Round Robin Load Balanceing.";

    public <T> T executeTask(Task<T> t) throws RemoteException {
        return ((Compute)lbEngine).executeTask(t);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        if(args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }
        if(args[0].equalsIgnoreCase("lclb")) lbEngine = new LeastConnectionsLB();
        else if(args[0].equalsIgnoreCase("rrlb")) lbEngine = new RoundRobinLB();
        else if(args[0].equalsIgnoreCase("wdlb")) lbEngine = new WeightedDistribution();
        else {
            System.err.println(usage);
            System.exit(1);
        }
        Compute c = new ComputeEngine();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Registry registry = LocateRegistry.createRegistry(1099);
            Compute stub1 = (Compute) UnicastRemoteObject.exportObject(c, 0);
            Loadbalanceing stub = (Loadbalanceing) UnicastRemoteObject.exportObject(lbEngine, 0);
            registry.rebind("Compute", c);
            registry.rebind("Loadbalancer", lbEngine);
            log.info("ComputeEngine bound\nWrite \"exit\" to exit this App");
            while(!reader.readLine().equals("exit"));
            log.info("exiting");
        } catch (Exception e) {
            log.log(Level.INFO, e.toString());
        } finally {
            try {
                UnicastRemoteObject.unexportObject(lbEngine, true);
                UnicastRemoteObject.unexportObject(c, true);
            } catch (NoSuchObjectException e) {
                System.err.println("unable to unexport");
                e.printStackTrace();
            }
        }
    }
}