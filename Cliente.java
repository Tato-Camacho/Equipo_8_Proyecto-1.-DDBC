// La clase Cliente sirve para 

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

// Cliente Inicializa la IP dependiendo de donde queramos entrar y el puerto
public class Cliente {
	private static  String IP = ""; // Puedes cambiar a localhost
	private static  int PUERTO = 1100; //Si cambias aquí el puerto, recuerda cambiarlo en el servidor

// Main es el arreglo de cadenas que va a traer el Local Host mas otras 2 IP	
	public static void main(String[] args) throws RemoteException, NotBoundException {
        Scanner sc = new Scanner(System.in);
        int eleccion;
        float numero1, numero2, resultado = 0;
		String[] ips={"localhost","192.168.1.208", "192.168.1.103"};
		int i=0;
//El arreglo de cargas es el que se encarga de ordenar la carga de los servidores y sus IP's
		int[] cargas= new int[3];
		int imin=0;
        do {
			imin=0;
		
//[BALANCE DE CARGA]
// Este ciclo obtiene todas las cargas y las va guardando en su espacio correspondiente con la funcion getCarga
//Tambien se compara cual carga es la mas pequeña para guardar en imin
			for(i=0;i<=2;i++){
				PUERTO = 1100;
				Registry registry = LocateRegistry.getRegistry(ips[i], PUERTO);
				Interfaz interfaz = (Interfaz) registry.lookup("Despachador"); //Buscar en el registro...
				cargas[i]=interfaz.getCarga();
				System.out.println("Carga"+cargas[i]+"-"+i);
			}
			if(cargas[0]<=cargas[1]&&cargas[0]<=cargas[2]){
				imin=0;
				System.out.println("Servidor 192.168.1.113 ");
			}
			if(cargas[1]<=cargas[0]&&cargas[1]<=cargas[2]){
				imin=1;
				System.out.println("Servidor 192.168.1.208");
			}
			if(cargas[2]<=cargas[0]&&cargas[2]<=cargas[1]){
				imin=2;
				System.out.println("Servidor 192.168.1.103");
			}

// 
			IP = ips[imin];
			PUERTO = 1100;
			Registry registry = LocateRegistry.getRegistry(IP, PUERTO);
			Interfaz interfaz = (Interfaz) registry.lookup("Despachador"); //Buscar en el registro...
			System.out.println("Dame el nombre del proceso,  el numero de paginas y la cadena de referencias: \n");
			Scanner entrada = new Scanner(System.in);
			String linea = entrada.nextLine();
			interfaz.nuevoProceso(linea, "192.168.1.113 ");
			System.out.println("0 seguir, -1 salir");
			entrada = new Scanner(System.in);
			eleccion= entrada.nextInt();
        } while (eleccion != -1);
    }
}
