package proyecto;

import com.csvreader.CsvWriter;
import com.google.gdata.client.*;
import com.google.gdata.client.calendar.*;
import com.google.gdata.data.*;
import com.google.gdata.data.acl.*;
import com.google.gdata.data.calendar.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;

import java.net.*;
import java.text.ParseException;
import java.io.*;

import sample.util.*;
	

/* Clase que contiene el método main. Se basa en un Menú que contiene las diferentes opciones 
 * disponibles para operar con los calendarios. En función de la opción que seleccione el usuario 
 * implementará la acción correspondiente apoyándose en la clase Gestion. */
public class Principal {

	static Gestion gestion;
	static boolean ComandoRapido;
	static String DefaultFile = "ArchivoPorDefecto.csv";
	
	public static void main(String[] pepe) 
	{
		try{	
			
			// Se crean las carpetas que almacenarán el archivo de Login y los csv
			File file = new File("csvFiles");

			if (!file.exists()) {
				if (file.mkdir()) {
					String PathCompleto = "csvFiles\\" + DefaultFile;
					// Cabecera del archivo
					String cabecera = "NombreEvento;Desde;Hasta;Lugar;Descripcion";

					// Crear archivo
					CsvWriter csv = new CsvWriter(PathCompleto );
					csv.write(cabecera+"\n");
					csv.close();
					System.out.println("Directorio csvFiles creado.");
				} else {
					System.out.println("No se ha podido crear el directorio csvFiles.");
				}
			}
			
			file = new File("Login");

			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Directorio Login creado.");
				} else {
					System.out.println("No se ha podido crear el directorio Login.");
				}
			}
			
			ComandoRapido = false;
			gestion = new Gestion();
			
			// Para pruebas con el eclipse:
			String args[] = new String[2];
			args[0] = "Login\\UserPass.txt";
			args[1] = "gclistev -f 2014-10-29";//gcimp -fc Escritura.csv calendariopruebas";
			// hasta aquí
			/* Para ejecutar desde MS-DOS se comenta lo de arriba, y en la definición del main se
			 * cambia String[] pepe por String[] args*/
			
			if (args.length > 0) { //si hay fichero
				gestion.estableceIdentidad(args[0]);
			
				if (args.length > 1){ // si hay comando
					String com = args[1];
					for (int i = 2; i < args.length; i++)
					{
						com += " " + args[i];
					}
					
					ComandoRapido = Opciones(com);
				}
			}
			String Buffer = "";		
			BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

			while(!Buffer.equals("salir") && !ComandoRapido){
				
				/* ************     MENÚ    ********** */
				System.out.println();
				System.out.println();
				System.out.println("Introduce la orden y pulsa intro:");
				System.out.println(".gcid------>Establecer identidad\n" +
									".gccls---->Muestra calendarios visibles\n" +
									".gcsp----->Muestra permisos-----\n" +
									".gccd----->Establece calendario por defecto\n" +
									".gcmkev--->Crear evento\n" +//todas opciones juntas
									".gclistev->Listar Eventos\n"+
									".gcrmev--->Eliminar evento\n" +
									".gcedev--->Modificar evento\n" +
									".gcexp---->Exportar evento----\n" +
									".gcimp--->Importar evento----\n" +
									".help --->Ayuda----\n" +
									".salir\n");

	        	Buffer = entrada.readLine();
	        	Opciones(Buffer);
				
			}
			
		}
			catch(Exception ex){
				System.err.println("Excepción: " + ex.getMessage());
				System.out.println("La acción no se  ha podido llevar a cabo");
				return;
			}
	}

/*	Función que llama al método de la clase Gestión a partir de la orden introducida por teclado. */
public static boolean Opciones (String Buff ) throws IOException, ServiceException, ParseException
{
	String[] parametros;
	
	if(Buff.contains(" ")){
		
		System.out.println("com   " + Buff);
		parametros = Buff.split(" ",6);//aqui falla algo, no lo entiendo
		
			for (int i =0; i < parametros.length ; i++)
				System.out.println(parametros[i]);
		}
	
	else{
			parametros = new String[1];;
			parametros[0] = Buff;
		}
	
	switch ( parametros[0] ) 
	{
		  case "gcid":
			  gestion.estableceIdentidad();
			  ComandoRapido = true;
			  break;
		  case "gccls":
			  gestion.muestraCalendariosVisibles();
			  ComandoRapido = true;
			   break;
		  case "gcsp":
			  gestion.muestraPermisos();
			  ComandoRapido = true;
			   break;		
		  case "gccd":
			  gestion.establecerCalendarioPorDefecto();
			  ComandoRapido = true;
			   break;
		  case "gcmkev":
			  if ( parametros.length == 1)
			  {
				  gestion.crearEvento();
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-t")){
				  gestion.crearEvTitulo(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-f")){
				  gestion.crearEvFecha(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-h")){
				  gestion.crearEvHora(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if(parametros[1].equals("-tfh")){
				  String fecha = parametros[3];
				  gestion.crearEvTitFecHor(parametros[2], parametros[3], parametros[4]);
				  ComandoRapido = true;
			  }
			  break;
		  case "gcrmev":
			  gestion.eliminarEvento();
			  ComandoRapido = true;
			  break;
		  case "gcedev":
			  gestion.modificarEvento();
			  ComandoRapido = true;
			  break;
		  case "gcexp":
			  if ( parametros.length == 1)
			  {
				  gestion.exportarEventos(DefaultFile);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-f")){
				  gestion.exportarEventos(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-fc")){
				  String FicheroCalendario = parametros[2];
				  String[] calendario = FicheroCalendario.split(" ",2);
				  gestion.establecerCalendarioPorDefecto(calendario[1]);
				  gestion.exportarEventos(calendario[0]);
				  ComandoRapido = true;
			  }
			  break;
		  case "gcimp":
			  if ( parametros.length == 1)
			  {
			  	gestion.importarEventos(DefaultFile);
			  	ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-f")){
				  gestion.importarEventos(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-fc")){
				  String FicheroCalendario = parametros[2];
				  String[] calendario = FicheroCalendario.split(" ",2);
				  gestion.establecerCalendarioPorDefecto(calendario[1]);
				  gestion.importarEventos(calendario[0]);
				  ComandoRapido = true;
			  }
			  break;
		  case "gclistev":
			  if ( parametros.length == 1)
			  {
				  gestion.listarTodosEventos();
				  ComandoRapido=true;
			  }
			  else if ( parametros[1].equals("-t") || parametros[1].equals("-l")){
				  gestion.listarEvText(parametros[2]);
				  ComandoRapido = true;
			  }
			  else if ( parametros[1].equals("-f")){
				  gestion.listarEvDate(parametros[2]);
				  ComandoRapido = true;
			  }
			  break;
		  case "help":
			  gestion.Ayuda();
			  ComandoRapido = true;
			  break;
		  case "salir":
			  break;
		  default:
			   System.out.println("No se reconoce el comando: " + Buff + " Inténtalo de nuevo." );
			   break;
		  }
	return ComandoRapido;
	}
}




