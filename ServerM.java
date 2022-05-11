// Language: java

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Thread;


public class ServerM {
    private static final int PUERTO = 1100; //Si cambias aqui el puerto, recuerda cambiarlo en el cliente


    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Remote remote = UnicastRemoteObject.exportObject(new Interfaz() {
            MemoryManager m1=new MemoryManager();
        	/*
				Sobrescribir opcionalmente los metodos que escribimos en la interfaz
        	*/
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
            
            @Override
            public int getCarga() throws RemoteException {
                return m1.nref;
            }
        }, 1100);
        Registry registry = LocateRegistry.createRegistry(PUERTO);
       	System.out.println("Servidor escuchando en el puerto " + String.valueOf(PUERTO));
        registry.rebind("Despachador", remote); // Registrar calculadora
    }
}

