//MemoryManager es todo lo contenido en la Practica 4 del Curso
//Administración de memoria virtual

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.jar.*;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.* ;
import java.net.* ;
//Use a linked list to store the process information, format:
// Name, Pages, references as an array of duples (page, offset)
class Process{
	String name;
	int pages;
	int references;
	int time;
	int[][] references_array; // new int[100][100]; [page, offset] 
	Process next;
}
public class MemoryManager {
	
private Process primero = null;
private Process ultimo = null;
private int[] memoriaf;
private int[] memoriav;
private int tam_memf=1024;
private int tam_memv=4096;
private int tam_p=64;
private int cantpagf;
private int cantpagv;
public int nref=0;


//Toma un tamano de pagina, un tamano de memoria virtual, un tamano de memoria fisica e inicializa la memoria
//Nos dice la cantidad de paginas fisicas y virtuales
	
public void init_memory(){
	cantpagf = tam_memf/tam_p;
	System.out.println("Cantidad de paginas fisicas: " + cantpagf);
	cantpagv = tam_memv/tam_p;
	System.out.println("Cantidad de paginas virtuales: " + cantpagv);
	memoriav = new int[cantpagv];
	for(int i = 0; i < cantpagv; i++){
		memoriav[i] = 0;
	}
	memoriaf = new int[cantpagf];
	for( int i = 0; i < cantpagf; i++){
	 	memoriaf[i] = 0;
	}
}



//Regresa el Numero Decimal dado en BINARIO
public String bin(int num){
	String binario = "";
	int i = 0;
	while(num > 0){
		binario = num%2 + binario;
		num = num/2;
		i++;
	}
	//reverse string
	String reverse = "";
	int j = 0;
	for(int k = i-1; k >= 0; k--){
		reverse = reverse + binario.charAt(k);
		j++;
	}
	return reverse;
}



//Regresa el numero decimal dado en HEXADECIMAL
public String hex(int num){
	String hexadecimal = "";
	int i = 0;
	while(num > 0){
		if(num%16 > 9){
			hexadecimal = (char)(num%16 + 'A' - 10) + hexadecimal;
		}else{
			hexadecimal = (char)(num%16 + '0') + hexadecimal;
		}
		num = num/16;
		i++;
	}
	//reverse string
	String reverse = "";
	int j = 0;
	for(int k = i-1; k >= 0; k--){
		reverse = reverse + hexadecimal.charAt(k);
		j++;
	}
	return reverse;
}


//Crea un nuevo proceso, pide al usuario el nombre, el numero de paginas y la referencia
// y lo asigna en la lista
public void newProcess(){
	System.out.println("Dame el nombre del proceso,  el numero de paginas y la cadena de referencias: \n");
	Scanner entrada = new Scanner(System.in);
	String linea = entrada.nextLine();
	parseInput(linea);
	}


//Se encarga de dividir la cadena para decira que paginas se les hace referencia,
//nombre del proceso y cantidad de paginas a almacenar en nref (Numero de referencias)
public void parseInput(String linea){
	String entradas[] = new String[150];
	int j=0, i=0;
	String tokens[]= new String[100];
	StringTokenizer st = new StringTokenizer(linea, " ");
	while(st.hasMoreTokens()){
		entradas[j]=st.nextToken();
		j++;
	}
	StringTokenizer st2;
	for (i = 2; i<j; i++){
		st2 = new StringTokenizer(entradas[i], ",");
		while(st2.hasMoreTokens()){
			tokens[i] = st2.nextToken();
			nref++;
		}
	}
	System.out.println("------");

	//Almacena el nodo en la lista
	crearNodo(entradas[0], tokens, Integer.parseInt(entradas[1]), (j-2)/2);
}

//Crea un nuevo nodo, pide al usuario el nombre, el numero de paginas y la referencia
private void crearNodo(String name, String tokens[], int pages, int references_time){
	Process p = new Process(); 
	p.references_array = new int[references_time][2]; 
	p.references = references_time; 
	p.name = name;
	p.pages = pages;
	p.time=references_time;
	// references array as an array of duples (page, offset)
	for(int i=0; i<p.references; i++){
		p.references_array[i][0] = Integer.parseInt(tokens[2*i+2]);
		p.references_array[i][1] = Integer.parseInt(tokens[2*i+3]);
	}
	if(primero == null){
		primero = p;
		primero.next = primero;
		ultimo = primero;
	}else{
		ultimo.next = p;
		p.next = primero;
		ultimo = p;
	}
}

//Recorre la lista 
public void recorrerLista() {
	Process actual;
	int esp,espaux;
	actual = primero;
	if (primero != null){
		do{
			System.out.println("Nombre: " + actual.name);
			System.out.println("Pagina " + actual.pages);
			System.out.println("Numero de refrencias: " + actual.references);
			for(int i=0; i<actual.references; i++){
				System.out.println("[ " + actual.references_array[i][0] + "," + actual.references_array[i][1] + "]");
			}
			actual = actual.next;
		}while(actual!=primero);
	}
	else
		System.out.println("La lista esta vacia");
}
	
//Ayuda a que en el proceso a trabajar se le quita una pagina porque anteriormente ya se
//le hizo referencia a ella, entonces no es necesario volver a hacerlo
private void decrementarMin(int index) {
	Process actual;
	int esp,espaux, i=0, band=0,j, asign=0;
	String pagina, desplazamiento;
	actual = primero;
	nref--;
	if (primero != null){
		do{
			if(i==index){
				System.out.println("Actual: " + actual.name);
				System.out.println("Tiempo: " + actual.time);
				if(memoriav[actual.references_array[actual.references-actual.time][0]]==0){
					System.out.println("Error de página");
					j=1;
					asign=0;
					do{
						if(memoriaf[j]==0){
							memoriaf[j]=actual.references_array[actual.references-actual.time][0];
							asign=1;
							memoriav[actual.references_array[actual.references-actual.time][0]]=j;
						}
						j++;
					}while(j<=cantpagf && asign==0);
					if(asign==0){
						memoriaf[1]=actual.references_array[actual.references-actual.time][0];
						memoriav[actual.references_array[actual.references-actual.time][0]]=1;
					}
				}
				System.out.println("Pagina :" + actual.references_array[actual.references-actual.time][0]);
				System.out.println("Desplazamiento: " + actual.references_array[actual.references-actual.time][1]);
				System.out.println("Marco de pagina: " + memoriav[actual.references_array[actual.references-actual.time][0]]);
				pagina=bin(actual.references_array[actual.references-actual.time][0]);
				desplazamiento=bin(actual.references_array[actual.references-actual.time][1]);
				System.out.println("Direccion virtual binario: " + pagina + desplazamiento);
				pagina=bin(memoriav[actual.references_array[actual.references-actual.time][0]]);
				System.out.println("Direccion fisica binario: " + pagina + desplazamiento);
				pagina=hex(actual.references_array[actual.references-actual.time][0]);
				desplazamiento=hex(actual.references_array[actual.references-actual.time][1]);
				System.out.println("Direccion virtual hexadecimal: " + pagina + desplazamiento);
				pagina=hex(memoriav[actual.references_array[actual.references-actual.time][0]]);
				System.out.println("Direccion fisica hexadecimal: " + pagina + desplazamiento);
				System.out.println("\n");
				actual.time--;
				if(actual.time==0){
					eliminarNodo(actual.name);
				}
				band=1;
			}
			if(band==0){
				actual = actual.next;
			}
			i++;
		}while(actual!=primero && band==0);
	}else
		System.out.println("La lista esta vacia");
}


//Elimina un Nodo	
public void eliminarNodo(String proceso){

	Process actual = primero;
	Process anterior = null;
	if(primero!=null){
		do{
			if(actual.name.equalsIgnoreCase(proceso)){ 
				if(ultimo==primero){
					primero=null;
					ultimo=null;
				}
				else{
					if(actual == primero){
						primero = primero.next;
						ultimo.next =  primero;
					}else 
						if(actual == ultimo){
							anterior.next = primero;
							ultimo = anterior;
						}else{
							anterior.next = actual.next;
							System.out.println("Nodo eliminado " + actual.name);
						}
				}
				System.out.println("Nodo eliminado " + actual.name);
				}
			anterior = actual;
			actual = actual.next;
		}while(actual!=primero && primero!=ultimo);
	}else
		System.out.println("La lista esta vacia");
}


//Sirve para saber cuanto tiempo tardara un proceso en ejecutarse
private int calcula_tiempoc(){
	int tiempo=0; 
	Process actual = primero;
	if(primero!=null){
		do{
			tiempo=tiempo+actual.time;
			actual = actual.next;
		}while(actual!=primero);
		System.out.println("Tiempo " + tiempo);
	}else
		System.out.println("La lista esta vacia");
	return tiempo;
}


//Busca el proceso que menos tiempo tarda en ejecutarse
private int buscaValMin(int max){
	Process actual = primero;
	int pMin=0, min=1000, i=0;
	if(primero!=null){
		do{
			if(actual.time<min){
				min=actual.time;
				pMin=i;
			}
			actual=actual.next;
			i++;
		}while(actual!=primero && i<=max);
	}else
		System.out.println("La lista esta vacia");
	return pMin;
}

//SFJ es el algoritmo (Shortest Job First) que es el proceso de ciclo de CPU mas corto
public void sjf(){

	int cuantum=-1,i, tiempo, cantp=0, proc, min;
	String min_nom[]= new String [150] ;
	tiempo= calcula_tiempoc();
	for(i=0;i<=tiempo;i++){
		if(i%3==0){
		   cuantum++;
	   }
	   min=buscaValMin(cuantum);
	   System.out.println("Minimo= "+min);
	   decrementarMin(min);
	   try {
			Thread.sleep(1000);
  		} catch (InterruptedException e) {

  		}
	}
}

//Main
public static void main(String[] args){
	int op;
	MemoryManager m1= new MemoryManager();
	System.out.println("Ingrese el tamano de la pagina");
	m1.tam_p = Integer.parseInt(System.console().readLine());
	System.out.println("Ingrese el tamano de la memoria fisica");
	m1.tam_memf = Integer.parseInt(System.console().readLine());
	System.out.println("Ingrese el tamano de la memoria virtual");
	m1.tam_memv = Integer.parseInt(System.console().readLine());
	m1.init_memory();
	do{
		System.out.println("Que quiere hacer?\n 1.-Proceso nuevo\n 2.-Despachar\n 3.-Salir\n");
		op = Integer.parseInt(System.console().readLine());
		switch (op)
		{
			case 1:
				m1.newProcess(); // Este deberiamos cambiarlo a que reciba los datos, no
				m1.recorrerLista();
				break;
			case 2:
				m1.sjf();
				break;
			case 3:
				break;
			default:
				break;
		}
	}while(op!=3);
	}
}

