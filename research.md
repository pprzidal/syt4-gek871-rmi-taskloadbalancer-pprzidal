## Fragen

### Was ist RMI und welches Prinzip der verteilten Programmierung kommt dabei zur Anwendung?

``Remote Method Invocation`` bezeichnet das Aufrufen einer Methode eines Java Objektes welches sich nicht am selben Rechner befindet wie der Rechner vondem die Methode aufgerufen wird.
Das kann vorallem sinnvoll bei aufwendigen Rechenoperationen sein die auf einem nicht sehr leistungsfähigem Gerät durchgeführt werden sollen.
Anstatt hier zu versuchen den Algorithmus zu optimieren um es auch auf dem schwachen Device schnell ausführen zu können kann man auf RMI setzen.

RMI ist quasi die Java Implementierung von RPC.

### Was sind Stubs? Welche Aufgabe hat dabei das Proxy-Objekt?

Ein Stub-Objekt ist ein (client-seitiger) Stellvertreter
für das entfernte Objekt beim RMI-Server.

### Was wird in der Registry gespeichert?

Die Namen der Stubs als String (eine art key) und die Stubs selber (eine art value).
Wobei die Namen unique sein sollen.

### Wie kommt das `Remote`-Interface zum Einsatz? Was ist bei der Definition von Methoden zu beachten?

Die Interfaces (``Compute`` und ``Loadbalanceing``) müssen von Remote erben und jede Methode muss die RemoteException werfen.

### Was ist bei der Weitergabe von Objekten unabdingbar?

Sie zu exportieren. Wenn sie nicht in der Registry sind kann sie der Client bzw. Aufrufer nicht sehen. Dann wären es nur lokale Objekte.

### Welche Methoden des `UnicastRemoteObject` kommen bei der Server-Implementierung zum Einsatz?

``exportObject()`` und ``unexportObject()``

### Wie kann der Server ein sauberes Schließen ermöglichen? Was muss mit dem exportierten Objekt geschehen?

Hierzu muss einfach nur eine CLI Eingabe ermöglicht werden. Also wenn der User z.b. ``exit`` eingibt.
Dann müsste noch das exportierte Objekt unexportiert werden. Das wird am besten in einem finally Block, da dieser ja auch nach einer Exception oder nach vervollständigem try block ausgeführt wird.
Mehr Details siehe Abschnitt ``Java RMI-Tutorial um "sauberes Schließen"`` erweitern" im [README.md](README.md).

# Quellen

* [1]     "java RMI Vortrag Borko"
* [2]     "RMI uni marburg"; [online](https://www.informatik.uni-marburg.de/~mathes/download/k6.pdf)
* [3]     ""