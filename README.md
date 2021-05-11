# Distributed Computing "*RMI Task Loadbalancer*" 

## Aufgabenstellung
Die detaillierte [Aufgabenstellung](TASK.md) beschreibt die notwendigen Schritte zur Realisierung.
Die Fragestellungen sind in [research](research.md) drinnen.

## Implementierung

<!-- TODO noch alles hinschreiben -->

- [ ] Java RMI-Tutorial um "sauberes Schließen" erweitern

Wir haben also einen ComputeServer der nicht sauber geschlossen werden kann.
Dieses Problem lässt sich lösen indem man den User eine Eingabe machen lässt und danach die Objekte unexported.
Es würde sich also folgende Zeile dafür anbieten:

``while(!sc.nextLine().equals("exit"));``

Wobei der Scanner von dem ``InputStream`` ``System.in`` liest und in einem try-with-resources definiert ist und das Interface ``Closeable`` implementiert.

Im finally wird dann noch das objekt unexported.

- [ ] Implementierung eines neuen Tasks (z.B. Fibonacci)

Hierzu muss einfach eine andere Klasse gemacht werden die den Task und Serializable implementiert.
Und da es Task implementiert und nicht abstrakt ist muss es eben auch die execute Methode überschreiben.

Den Fibonacci Algorithmus hab ich einfach vom Prof. Borko genommen ([1]). Aber wenn man will kann man ihn ja auch selber implementieren sollte nicht allzu schwer sein.
Man muss eben nur darauf achten einen BigInteger zu verwenden weil wenn man nur int oder long verwendet wird es bei größeren Zahlen zu einem Integer Overflow kommen.

Das Serializable braucht man da man es ja übers Netzwerk übertragen will und will das der marshallar / unmarshallar dann aus den Bytes wieder das korrekte Objekt zusammenbaut.
Wobei Serializable ein Marker Interface ist also man keine Methoden überschreiben muss wenn man es implementiert.

Im [ComputePi](src/main/java/client/ComputePi.java) hab ich dann noch einen 3. CLI Parameter hinzugefügt welcher vom User angegeben werden kann ob er Pi oder Fibonacci berechnen will.
Wenn man also "localhost 1000 pi" eingibt oder "localhost 1000 PI" dann wird Pi auf 1000 Pi Nachkommastellen berechnet.
Hier ist der Code dazu:

```java
Task<?> task = null;
if(args[2].equalsIgnoreCase("pi")) task = new Pi(Integer.parseInt(args[1]));
else if(args[2].equalsIgnoreCase("fib")) task = new Fibonacci(Integer.parseInt(args[1]));
else {
    System.err.println("3rd Argument must be either \"pi\" or \"fib\".");
    System.exit(1);
}
System.out.println(comp.executeTask(task));
```

- [ ] Implementierung eines Loadbalancer-Interfaces (register/unregister)

Wir wollen also nicht nur einen Server haben dem alle Clients Anfragen schicken und damit grillen. Sondern dazwischen einen Loadbalancer haben der alle Client Anfragen annimmt und dann an einen ComputeServer weiterleitet und dieses Ergebnis dann an den Client weiterleitet.
Für den Client sieht es also nun so aus als würde der Loadbalancer die Anfrage bearbeiten obwohl es ja eigentlich ein ComputeServer macht.

Wobei sich die ComputeServer beim Loadbalancer registieren bzw. wenn sie "fertig" sind auch beim Loadbalancer abmelden also unregistrieren.

Nun müssen wir also wie gefordert ein Interface [Loadbalanceing](src/main/java/compute/Loadbalanceing.java) machen, welche diese zwei Methoden register und unregister mit dem Parameter Compute stub.
Wobei dieses Loadbalanceing Interface von dem Remote Interface erbt und alle Methoden die RemoteException werfen.

Nun muss der Loadbalancer 2 Objekte exportieren und zwar einen Loadbalanceing (also LeasConnectionsLB oder RoundRobinLB) und einen Compute (ComputeEngine).

Nun würde man in der executeTask folgende Zeile schreiben:

```java
return ((Compute)lbEngine).executeTask(t);
```

Den Cast braucht man nun da lbEngine als Datentyp Loadbalanceing hat. Nun ist es so das es eine weitere Klasse namens RoundRobinLB gibt.
Diese Klasse implemntiert Compute und Loadbalanceing.

Nun könnte man z.b. in dieser Klasse eine Queue als Attribut haben. Wobei hier einfach register an die Queue "added" bzw. unregister einfach "removed".
In der executeTask würde man dann das vorderste Element nehmen mit poll (um es auch aus der Queue zu bekommen im gegensatz zu peek) und es in ein Compute reinspeichern und dann mit add wieder ans Ende der Queue speichern.
Und dann einfach nur auf das Compute executeTask ausführen mit dem Paramter T und das Ergebnis zurückgeben.
Die ganze Methode sieht so aus:

```java
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
```

- [ ] Überlegungen zum Design und mögliche Implementierung weiterer Loadbalancing-Methoden (Weighted Distribution oder Least Connections)

Ich habe den Least Connections Algorithmus gewählt weil er ein bisschen einfacher ist.

## Quellen

[1]     "github mborko code-examples"; [link](https://github.com/mborko/code-examples/blob/master/java/Fibonacci/src/main/java/fibonacci/Fibonacci.java); 11.05.2021