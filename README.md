# Distributed Computing "*RMI Task Loadbalancer*" 

## Aufgabenstellung
Die detaillierte [Aufgabenstellung](TASK.md) beschreibt die notwendigen Schritte zur Realisierung.

## Fragen

### Was ist RMI und welches Prinzip der verteilten Programmierung kommt dabei zur Anwendung?

### Was sind Stubs? Welche Aufgabe hat dabei das Proxy-Objekt?

### Was wird in der Registry gespeichert?

Die Namen der Stubs als String (eine art key) und die Stubs selber (eine art value).
Wobei die Namen unique sein sollen.

### Wie kommt das `Remote`-Interface zum Einsatz? Was ist bei der Definition von Methoden zu beachten?

Die Interfaces müssen von Remote erben und jede Methode muss die RemoteException werfen.

### Was ist bei der Weitergabe von Objekten unabdingbar?



### Welche Methoden des `UnicastRemoteObject` kommen bei der Server-Implementierung zum Einsatz?

exportObject() und unexportObject()

### Wie kann der Server ein sauberes Schließen ermöglichen? Was muss mit dem exportierten Objekt geschehen?

Hierzu muss einfach nur eine CLI Eingabe ermöglicht werden. Also wenn der User z.b. ``exit`` eingibt.
Dann müsste noch das exportierte Objekt unexportiert werden. Das wird am besten in einem finally Block, da dieser ja auch nach einer Exception oder nach vervollständigem try block ausgeführt wird.

## Implementierung

## Quellen
