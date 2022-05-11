// Language: java

// El servidor 
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Thread;

//Se asigna el puerto que debe ser el mismo que se asigna en el del cliente
public class ServerM {
    private static final int PUERTO = 1100; // (Si cambias aqui el puerto, recuerda cambiarlo en el cliente)

//Remote nos ayudara a sobre escribir en la Interfaz
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Remote remote = UnicastRemoteObject.exportObject(new Interfaz() {
  // M1 (Memory Manarger) es la Practica 4 que en este caso la usamos como un objeto 
		MemoryManager m1=new MemoryManager();
        	/*
				Sobrescribir opcionalmente los metodos que escribimos en la interfaz
        	*/
//Hacemos Override a todas las funciones de la Interfaz
//Se pasa la cadena del Proceso y el IP del Clielnte y realiza el despacho del Proceso
//Inicia la memoria del M1 y parseInput divide el proceso
//RecorrerLista sirve para recorrer y ver la lista de procesos
//Se crea un nuevo Hilo llamado Proceso donde se carga M1
	    @Override
            public void nuevoProceso(String cadena, String ipc) throws RemoteException {
               System.out.println("Cliente:"+ipc); 
               m1.init_memory();
               System.out.println(cadena); 
               m1.parseInput(cadena);
               m1.recorrerLista();
               Thread hilo = new Proceso(m1);
               hilo.start();
            };
		
//El Override de getCarga es qye m1 tiene un num de referencias que es la cantidad que un numero hara referecia a ciertas paginas            
            @Override
            public int getCarga() throws RemoteException {
                return m1.nref;
            }
        }, 1100);
	    
//Se queda escuchando el servidor en caso de que alguien se conecte	    
//Rebind se utiliza para conectar con la Interfaz	    
        Registry registry = LocateRegistry.createRegistry(PUERTO);
       	System.out.println("Servidor escuchando en el puerto " + String.valueOf(PUERTO));
        registry.rebind("Despachador", remote); // Registrar calculadora
    }
}

