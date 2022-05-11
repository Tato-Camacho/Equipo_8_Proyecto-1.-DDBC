//Interfaz sirve principalmente para que tanto el CLIENTE como el SERVIDOR
//que funciones se pueden realizar 

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.lang.Thread;

/*
	Declarar firma de métodos que serán sobrescritos
*/
public interface Interfaz extends Remote {
   void nuevoProceso(String cadena, String ipc) throws RemoteException;
   int getCarga() throws RemoteException;
}
