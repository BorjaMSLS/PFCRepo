package proyecto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import com.google.gdata.client.Query;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclNamespace;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.dublincore.Date;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.lang.Object;

/*	La clase Gestion se encargará de implementar las acciones solicitadas 
	por el usuario a través de la clase Principal
*/
public class Gestion {

	protected BufferedReader entrada;			// Buffer de entrada por teclado
	protected String Buffer;					// String para almacenar los datos introducidos por teclado
	
	protected CalendarService miServicio;		// Objeto CalendarService para operar con los calendarios
	protected String Usuario;					// Usuario de google calendar
	protected String Pass;						// Contraseña del usuario
	
	
	protected URL urlAllCalendars;				// Todos los calendarios del usuario	
	protected String CalendarioPorDefectoID;	// ID del calendario por defecto
	protected URL CalendarioPorDefectoURL;		// URL completa del calendario por defecto


/*	Constructor por defecto: Inicialización de variables */	
	protected Gestion() throws Exception
	{
		entrada = new BufferedReader(new InputStreamReader(System.in));
		
		miServicio = new CalendarService("exampleCo-exampleApp-1.0");
		
		urlAllCalendars = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
		
		// En pruebas se establece este calendario por defecto y ya se validan las credenciales directamente
		/*CalendarioPorDefectoID = "mariovernardo10@gmail.com";
		miServicio.setUserCredentials("mariovernardo10@gmail.com", "mariovernardo");
		CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
		*/
	}

/*	Función que pide por teclado Usuario y contraseña, y los valida para iniciar sesión. */
	protected void estableceIdentidad() throws AuthenticationException, IOException
	{
		//	Usuario y contraseña almacenados por teclado
		System.out.println("Introduce el nombre de usuario y pulsa intro:");	
    	Buffer = entrada.readLine();
    	Usuario = Buffer;
    	
    	System.out.println("Introduce la contraseña y pulsa intro:");
    	Buffer = entrada.readLine();
    	Pass = Buffer;
		
    	//	Acceso validado para toda la sesión
    	miServicio.setUserCredentials(Usuario,Pass);
    	
		// Se establece el calendario principal como calendario por defecto
    	CalendarioPorDefectoID = Usuario;
    	CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
    	System.out.println("El calendario por defecto es: " + CalendarioPorDefectoURL);
    	
	}
	
/*	Función que lee de un archivo las credenciales del usuario y las para iniciar sesión. */
	protected void estableceIdentidad(String File) throws AuthenticationException, IOException
	{
		
		// Ruta del archivo:
		String ruta = File; //"C:\\Users\\Usuario-pc\\Desktop\\UserPass.txt"; 

		File archivo = new File(ruta);
		String linea = null;

		// Objetos necesarios para leer el archivo.
		FileReader lector = new FileReader(archivo);
		BufferedReader buff = new BufferedReader(lector);
    	
		// Se lee la línea que contiene usuario y contraseña
		linea = buff.readLine();
		
		buff.close();
		lector.close();
		
		String credenciales[] = linea.split(" ");
		
		Usuario = credenciales[0];
		Pass = credenciales[1];
		
		//	Acceso validado para toda la sesión
   
    	miServicio.setUserCredentials(Usuario,Pass);
    	
		// Se establece el calendario principal como calendario por defecto
    	CalendarioPorDefectoID = Usuario;
    	CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
    	System.out.println("El calendario por defecto es: " + CalendarioPorDefectoURL);
    	
	}

/*	Función que muestra todos los calendarios accesibles por el usuario. */	
	protected void muestraCalendariosVisibles() throws IOException, ServiceException
	{
		CalendarFeed resultado = miServicio.getFeed(urlAllCalendars, CalendarFeed.class);
		System.out.println("Tus calendarios son: "+resultado.getEntries().size());
		for (int i = 0; i < resultado.getEntries().size(); i++) 
		{
	          CalendarEntry Calendario = resultado.getEntries().get(i);
	          System.out.println("\t" + Calendario.getTitle().getPlainText());    
	    }
	}

/*	Función que muestra los permisos que tiene el usuario sobre un determinado calendario*/
	protected void muestraPermisos() throws IOException, ServiceException 
	{
		CalendarFeed resultado = miServicio.getFeed(urlAllCalendars, CalendarFeed.class);
		
		for (int i = 0; i < resultado.getEntries().size(); i++) 
		{
	          CalendarEntry Calendario = resultado.getEntries().get(i);
	          System.out.print(Calendario.getTitle().getPlainText() +
	          " \t Nivel de acceso: " + Calendario.getAccessLevel().getValue()); 
	          switch ( Calendario.getAccessLevel().getValue() ) 
				{
					  case "owner":
						  System.out.println(": Realizar cambios en eventos y administrar el uso compartido.");
						  break;
					  case "editor":
						  System.out.println(": Realizar cambios en eventos.");
						  break;
					  case "read":
						  System.out.println(": Consultar todos los detalles de los eventos.");
						  break;
					  case "freebusy":
						  System.out.println(": Ver libre/ocupado. Sin acceso a detalles de eventos.");
						  break;
				}
	    }
	}

/*	Función que establece un nuevo calendario para operar sobre él. */	
	protected void establecerCalendarioPorDefecto() throws IOException, ServiceException
	{
		System.out.println("Introduce el nombre del calendario y pulsa intro:");	
    	Buffer = entrada.readLine();
    		
		CalendarFeed resultado = miServicio.getFeed(urlAllCalendars, CalendarFeed.class);
		
		// Para cada calendario 
		for (int i = 0; i < resultado.getEntries().size(); i++) 
		{
			CalendarEntry Calendario = resultado.getEntries().get(i);
			
			// Si el nombre del calendario introducido por el usuario coincide con alguno de los existentes en la cuenta
			if (Buffer.equalsIgnoreCase(Calendario.getTitle().getPlainText()))
			{
				/* Se almacena su ID completo, que será del estilo:
				http://www.google.com/calendar/feeds/default/calendars/piqsstj8f2lvahrg0f2p3qv4ic%40group.calendar.google.com */
				String URL = Calendario.getId();
				
				/* Nos quedamos con la última parte de la URL completa que será el ID del calendario.
				Será del estilo: piqsstj8f2lvahrg0f2p3qv4ic%40group.calendar.google.com */
				int tam = URL.split("/").length;
				CalendarioPorDefectoID = URL.split("/")[tam-1];	
				
				/* A partir del ID obtenido, se forma la URL completa del calendario por defecto*/
		    	CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
		    	System.out.println("Se ha establecido: " + Buffer + " , como calendario por defecto. ");
		    	return;
			}	
	    }
		
		CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
    	System.out.println("El calendario: " + Buffer + " , no existe. ");
	}
	
	/*	Sobrecarga del método establecerCalendarioPorDefecto() para poder pasarle un String. */	
	protected void establecerCalendarioPorDefecto(String calendario) throws IOException, ServiceException
	{   		
		CalendarFeed resultado = miServicio.getFeed(urlAllCalendars, CalendarFeed.class);
		
		// Para cada calendario 
		for (int i = 0; i < resultado.getEntries().size(); i++) 
		{
			CalendarEntry Calendario = resultado.getEntries().get(i);
			
			// Si el nombre del calendario introducido por el usuario coincide con alguno de los existentes en la cuenta
			if (calendario.equalsIgnoreCase(Calendario.getTitle().getPlainText()))
			{
				/* Se almacena su ID completo, que será del estilo:
				http://www.google.com/calendar/feeds/default/calendars/piqsstj8f2lvahrg0f2p3qv4ic%40group.calendar.google.com */
				String URL = Calendario.getId();
				
				/* Nos quedamos con la última parte de la URL completa que será el ID del calendario.
				Será del estilo: piqsstj8f2lvahrg0f2p3qv4ic%40group.calendar.google.com */
				int tam = URL.split("/").length;
				CalendarioPorDefectoID = URL.split("/")[tam-1];	
				
				/* A partir del ID obtenido, se forma la URL completa del calendario por defecto*/
		    	CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
		    	System.out.println("Se ha establecido: " + Buffer + " , como calendario por defecto. ");
		    	return;
			}	
	    }
		
		CalendarioPorDefectoURL = new URL("http://www.google.com/calendar/feeds/"+CalendarioPorDefectoID+"/private/full");
    	System.out.println("El calendario: " + Buffer + " , no existe. ");
	}
	
/* 	Función que almacena los parámetros de un nuevo evento a crear y llama a la función "creandoEvento" */
	protected void crearEvento() throws IOException, ServiceException
	{
		/* Parámetros del evento */
		String titulo = "";
		String contenido = "";
		String inicio = "T";
		String fin = "T";
		String lugar = "";

		System.out.println("Introduce el título del evento:");
		titulo = entrada.readLine();
		
		System.out.println("Introduce el contenido del evento:");
		contenido=entrada.readLine();
		
		System.out.println("Introduce la fecha de inicio en el siguiente formato: Año-Mes-Día");
		inicio = entrada.readLine();
		inicio+= "T";
			
		System.out.println("Introduce la hora de inicio en el siguiente formato: Horas:Minutos:Segundos");
		inicio+= entrada.readLine();
		
		System.out.println("Introduce la fecha de fin en el siguiente formato: Año-Mes-Día");
		fin = entrada.readLine();
		fin+= "T";
		
		System.out.println("Introduce la hora de fin en el siguiente formato: Horas:Minutos:Segundos");
		fin+= entrada.readLine();
		
		System.out.println("Introduce el lugar:");
		lugar = entrada.readLine();

		creandoEvento(titulo, contenido, inicio, fin, lugar);
			
	}
	
	protected void	crearEvTitulo( String titulo) throws IOException, ServiceException{
		creandoEvento(titulo, "", "T", "T", "");
	}
	
	protected void	crearEvFecha( String fecha) throws IOException, ServiceException, ParseException{
		// fecha tendrá el formato: 2014-05-06. Al inicio se añade la hora
		String inicio = fecha + "T00:00:00";
		
		// Se calcula el fin para que el evento dure un día
		String fin;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Se crea un objeto Calendar ( de JAVA)
		Calendar c = Calendar.getInstance();
		// Se inserta la fecha
		c.setTime(sdf.parse(fecha));
		// Se añade un día
		c.add(Calendar.DATE, 1);
		// se almacena en fin
		fin = sdf.format(c.getTime()); 
		// Se añade la hora
		fin = fin + "T00:00:00";

		creandoEvento("Todo el día", "", inicio, fin, "");
	}

	protected void	crearEvHora( String hora) throws IOException, ServiceException, ParseException{
		// hora tendrá el formato: 22:30:00
		String inicio = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		inicio = inicio.substring(0,10) + " " + hora;
		
		String fin;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		// Se inserta la fecha
		c.setTime(sdf.parse(inicio));
		// Se añade un día
		c.add(Calendar.HOUR, 1);
		// se almacena en fin
		fin = sdf.format(c.getTime()); 
		// se da formato
		inicio = inicio.replace(" ", "T");
		fin = fin.replace(" ", "T");

		creandoEvento("Una hora", "", inicio, fin, "");	
	}

	protected void	crearEvTitFecHor( String titulo, String fecha, String hora) throws IOException, ServiceException, ParseException{
		System.out.println();
		System.out.println("auiq" + titulo);
		System.out.println(fecha);
		System.out.println(hora);
		// fecha tendrá el formato: 2014-05-06. Al inicio se añade la hora
		String inicio = fecha + "T00:00:00";
				
		// Se calcula el fin para que el evento dure un día
		String fin;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Se crea un objeto Calendar ( de JAVA)
		Calendar c = Calendar.getInstance();
		// Se inserta la fecha
		c.setTime(sdf.parse(fecha));
		// Se añade un día
		c.add(Calendar.DATE, 1);
		// se almacena en fin
		fin = sdf.format(c.getTime()); 
		// Se añade la hora
		fin = fin + "T00:00:00";

		
		// hora tendrá el formato: 22:30:00
		String iniciohora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		iniciohora = iniciohora.substring(0,10) + " " + hora;
		
		String finhora;
		SimpleDateFormat sdfhora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar chora = Calendar.getInstance();
		// Se inserta la fecha
		chora.setTime(sdfhora.parse(iniciohora));
		// Se añade un día
		chora.add(Calendar.HOUR, 1);
		// se almacena en fin
		finhora = sdfhora.format(chora.getTime()); 
		// se da formato
		iniciohora = iniciohora.replace(" ", "T");
		finhora = finhora.replace(" ", "T");
		//System.out.println(iniciohora);
		iniciohora = iniciohora.replace(" ", inicio);
		finhora = finhora.replace(" ", fin);
		//System.out.println(iniciohora);
		System.out.println(titulo);
		System.out.println(iniciohora);
		System.out.println(finhora + "aqui");
		creandoEvento(titulo, "", iniciohora, finhora, "");	
	}

	
/*	Función que a partir de los parámetros introducidos por el usuario inserta un nuevo evento en el calendario. */	
	protected void creandoEvento(String titulo, String contenido, String inicio, String fin, String lugar) throws IOException, ServiceException
	{
		// Crear evento
		CalendarEventEntry miEvento = new CalendarEventEntry();
		// Establecer titulo y contenido
		miEvento.setTitle(new PlainTextConstruct(titulo));
		miEvento.setContent(new PlainTextConstruct(contenido));
		
		// Si la fecha y hora no se han introducido, inicio y fin valdrán "T", que se sustituirá por la fecha hora actual
		if ( inicio.equals("T") || fin.equals("T"))
		{
			inicio = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			fin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			inicio = inicio.replace(" ", "T");
			fin = fin.replace(" ", "T");
		}
		
		// Establecer fecha y hora
		DateTime inicioTime = com.google.gdata.data.DateTime.parseDateTime(inicio);
		DateTime finTime = com.google.gdata.data.DateTime.parseDateTime(fin);
		When eventTimes = new When();
		eventTimes.setStartTime(inicioTime);
		eventTimes.setEndTime(finTime);
		miEvento.addTime(eventTimes);
		
		// Establecer lugar
		miEvento.addLocation(new Where("","",lugar));
		
		// Añadir evento al calendario correspondiente
		System.out.println("El calendario por defecto es: " + CalendarioPorDefectoURL);
		miServicio.insert(CalendarioPorDefectoURL, miEvento);
		
}

/*	Función que muestra al usuario todos los eventos correspondientes a un determinado calendario. */	
	protected void listarTodosEventos() throws IOException, ServiceException
	{
		System.out.println("El calendario por defecto es: " + CalendarioPorDefectoURL);
		CalendarFeed resultado = miServicio.getFeed(CalendarioPorDefectoURL, CalendarFeed.class);
		for(int i=0; i<resultado.getEntries().size();i++)
		{
			CalendarEventFeed myFeed = miServicio.getFeed(CalendarioPorDefectoURL, CalendarEventFeed.class);
			CalendarEventEntry entra = myFeed.getEntries().get(i);
			CalendarEntry evento = resultado.getEntries().get(i);
			String tituloEvento = evento.getTitle().getPlainText();
			List <When> whens = entra.getTimes();
			DateTime fecha=whens.get(0).getStartTime();
			//String Lugar=evento.addLocation(new Where("","",lugar));
			List<Where> lugar = evento.getLocations();
			System.out.println(tituloEvento+"\t"+fecha+"\t"+lugar);
		}
	}


protected void listarEvText(String Texto) throws ServiceException,IOException {
		
		CalendarQuery myQuery = new CalendarQuery(CalendarioPorDefectoURL);

	    myQuery.setFullTextQuery(Texto);
	    
	    // Send the request and receive the response:
	    CalendarEventFeed resultFeed = miServicio.query(myQuery,
	        CalendarEventFeed.class);

	    System.out.println(" Lista de eventos para "
	        + Texto + ":");
	    System.out.println();
	    for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	      CalendarEventEntry entry = resultFeed.getEntries().get(i);
	      System.out.print("\t" + entry.getTitle().getPlainText());
	      List <When> whens = entry.getTimes();
		  DateTime fecha=whens.get(0).getStartTime();
		  System.out.println("\t"+fecha);
	    }
	    System.out.println();
	  }

protected void listarEvDate(String datos) throws ServiceException,IOException {
		
		String fechas[] = datos.split(" ");
		System.out.println(fechas[0]);
		System.out.println(fechas[1]);
		String pepe = "2014-07-30T12:00:00";
		String luis = "2014-07-31T14:00:00";
		System.out.println("Introduce el nuevo inicio con el siguiente formato AAAA-MM-DDTHH:MM:SS");
		pepe = entrada.readLine();
		DateTime startTime = com.google.gdata.data.DateTime.parseDateTime(pepe);
		System.out.println("aqui");	
		DateTime endTime = com.google.gdata.data.DateTime.parseDateTime(luis);
		
		System.out.println("aqui");
		CalendarQuery myQuery = new CalendarQuery(CalendarioPorDefectoURL);
		
		myQuery.setMinimumStartTime(startTime);
	    myQuery.setMaximumStartTime(endTime);
	    
	    // Send the request and receive the response:
	    CalendarEventFeed resultFeed = miServicio.query(myQuery, CalendarEventFeed.class);
	    
	    System.out.println("Eventos desde " + startTime.toString() + " hasta "
	        + endTime.toString() + ":");
	    System.out.println();
	    for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	     // CalendarEventEntry entra = resultFeed.getEntries().get(i);
		 // System.out.print("\t" + entra.getTitle().getPlainText());
	      CalendarEventEntry entry = resultFeed.getEntries().get(i);
	      System.out.println("\t" + entry.getTitle().getPlainText());
	    }
	    System.out.println();
	}

/*	Función que elimina un evento del calendario a partir de su título. */	
	protected void eliminarEvento() throws IOException, ServiceException
	{
		String eventoABorrar;
		String fechaEliminar;
		
System.out.println("Introduce el título del evento a eliminar:");
		eventoABorrar = entrada.readLine();

		System.out.println("Introduce la fecha del evento a eliminar:");
		fechaEliminar =entrada.readLine();
		
		CalendarFeed resultado = miServicio.getFeed(CalendarioPorDefectoURL, CalendarFeed.class);
		
		for(int i=0; i < resultado.getEntries().size()-1; i++)
		{
			CalendarEventFeed myFeed = miServicio.getFeed(CalendarioPorDefectoURL, CalendarEventFeed.class);
			CalendarEventEntry entra = myFeed.getEntries().get(i);
			CalendarEntry evento = resultado.getEntries().get(i);
			String tituloEvento = evento.getTitle().getPlainText();
			List <When> whens = entra.getTimes();
			DateTime fechaABorrar=whens.get(0).getStartTime();
			String fechaAcomparar = fechaABorrar.toString();
			String fechas[]=fechaAcomparar.split("T",2);
			//fechas[0] ="2014-07-31";
			//fechaEliminar ="2014-07-31";
			System.out.println(tituloEvento);
			System.out.println("1"+fechas[0]);
			System.out.println("2"+fechaEliminar);
			if(tituloEvento.equals(eventoABorrar)&&(fechaEliminar.equals(fechas[0])))
			{
				System.out.println("entro");
				//NO LO Borra
				evento.delete();
			}			
		}
	}

/*	Función que almacena el parámetro a modificar de un evento ya existente en el calendario. */	
	protected void modificarEvento() throws IOException, ServiceException
	{
		String titulo;
		String contenido;
		String inicio;
		String fin;
		String lugar;
		Where lugarWhere;
		
		String eventoAModificar;
		System.out.println("Introduce el título del evento a modificar: ");
		
		eventoAModificar = entrada.readLine();
		
		CalendarFeed resultado = miServicio.getFeed(CalendarioPorDefectoURL, CalendarFeed.class);
		
		String eventoId;
		
		// Se recorre el calendario en busca del evento solicitado
		for(int i=0; i < resultado.getEntries().size(); i++)
		{
			CalendarEntry evento = resultado.getEntries().get(i);
			String tituloEvento = evento.getTitle().getPlainText();
			
			// Si el evento existe, se almacenan sus parámetros
			if(tituloEvento.equals(eventoAModificar))
			{
				eventoId = evento.getId();
				
				titulo = evento.getTitle().getPlainText();
				contenido = evento.getPlainTextContent();
				
				CalendarEventFeed myFeed = miServicio.getFeed(CalendarioPorDefectoURL, CalendarEventFeed.class);
				CalendarEventEntry entra = myFeed.getEntries().get(i); 
				
				List <When> whens = entra.getTimes(); 
				DateTime inicioTime = whens.get(0).getStartTime(); 
				DateTime finTime = whens.get(0).getEndTime(); 
				
				lugarWhere=evento.getLocations().get(0);
				System.out.println(titulo+contenido+inicioTime+finTime+lugarWhere);
				
				System.out.println(" Introduce el numero correspondiente al parámetro a modificar:\n"+"1.titulo\n"+"2.contenido\n"+"3.inicio\n"+"4.fin\n"+"5.lugar\n"+"6.salir");
				String opcion = entrada.readLine();
				
				// Se almacena el parámetro que el usuario desea modificar y se llama a la función "modificandoEvento"
				switch(opcion)
				{
					case "1":
						System.out.println("Introduce el nuevo titulo");
						titulo = entrada.readLine();
						modificandoEvento(titulo, contenido, inicioTime, finTime, lugarWhere, eventoId);
						break;
					case "2":
						System.out.println("Introduce el nuevo contenido");
						contenido = entrada.readLine();
						modificandoEvento(titulo, contenido, inicioTime, finTime, lugarWhere, eventoId);
						break;
					case "3":
						System.out.println("Introduce el nuevo inicio con el siguiente formato AAAA-MM-DDTHH:MM:SS");
						inicio = entrada.readLine();
						inicioTime = com.google.gdata.data.DateTime.parseDateTime(inicio);
						modificandoEvento(titulo, contenido, inicioTime, finTime, lugarWhere, eventoId);
						break;
					case "4":
						System.out.println("introduce el nuevo fin con el siguiente formato AAAA-MM-DDTHH:MM:SS");
						fin = entrada.readLine();
						finTime = com.google.gdata.data.DateTime.parseDateTime(fin);
						modificandoEvento(titulo, contenido, inicioTime, finTime, lugarWhere, eventoId);
						break;
					case "5":
						System.out.println("introduce el nuevo lugar");
						lugar = entrada.readLine();
						lugarWhere=new Where ("","",lugar);
						modificandoEvento(titulo, contenido, inicioTime, finTime, lugarWhere, eventoId);
						break;
					case "6":
						break;
					default:
						System.out.println(" No se reconoce el parámetro a modificar: " + Buffer);
						break;
				}
			}			
		}
		
		System.out.println("El evento " + eventoAModificar + " no existe en este calendario.");
	}

/*	Función que modifica el parámetro de un determinado evento solicitado por el usuario. */
	protected void modificandoEvento(String titulo, String contenido, DateTime inicioTime, DateTime finTime, Where lugarWhere, String eventoId) throws IOException, ServiceException
	{
		
		CalendarFeed resultado = miServicio.getFeed(CalendarioPorDefectoURL, CalendarFeed.class);
		
		// Se crea el evento a partir de los nuevos parámetros
		CalendarEventEntry miEvento = new CalendarEventEntry();
		miEvento.setTitle(new PlainTextConstruct(titulo));
		miEvento.setContent(new PlainTextConstruct(contenido));
		When eventTimes = new When();
		eventTimes.setStartTime(inicioTime);
		eventTimes.setEndTime(finTime);	
		miEvento.addTime(eventTimes);
		miEvento.addLocation(lugarWhere);
		miServicio.insert(CalendarioPorDefectoURL, miEvento);
		
		// Si todo es correcto, se busca el evento a modificar y se elimina
		for(int i=0; i < resultado.getEntries().size(); i++)
		{
			CalendarEntry evento = resultado.getEntries().get(i);
			if(evento.getId().equals(eventoId))
			{
				evento.delete();
				System.out.println("Evento modificado.");
			}
		}
	}

/*	Función que almacena todos los eventos correspondientes a un determinado calendario en un archivo csv. */	
    protected void exportarEventos(String NombreArchivo) throws IOException, ServiceException
	{
    	String pathCompleto = "csvFiles\\" + NombreArchivo;
		// Cabecera del archivo
		String cabecera = "NombreEvento;Desde;Hasta;Lugar;Descripcion";
		String titulo;
		String contenido;

		// Crear archivo
		CsvWriter csv = new CsvWriter(pathCompleto);
		csv.write(cabecera+"\n");
		
		CalendarFeed resultado = miServicio.getFeed(CalendarioPorDefectoURL, CalendarFeed.class);
		CalendarEventFeed myFeed = miServicio.getFeed(CalendarioPorDefectoURL, CalendarEventFeed.class);

		// Se almacenan los parámetros necesarios para cada evento del calendario 
		for(int i=0; i < resultado.getEntries().size(); i++)
		{
			CalendarEntry entradas = resultado.getEntries().get(i);
			
			//Título y contenido
			titulo = entradas.getTitle().getPlainText();
			contenido = entradas.getPlainTextContent();

			// Fechas-horas
			CalendarEventEntry entra = myFeed.getEntries().get(i);
			List <When> whens = entra.getTimes();
			DateTime StartTime = whens.get(0).getStartTime();
			DateTime EndTime = whens.get(0).getEndTime();
			
			// Lugar
			String lugar = entradas.getLocations().get(0).getValueString();
			
	        // Se crea la línea a guardar
			String evento =  titulo + ";" + StartTime+ ";" + EndTime + ";" + lugar + ";" + contenido;
			csv.endRecord();  
			
			// Se escribe el evento en el archivo
			csv.write(evento);
			System.out.println(evento);			
		}
		
		csv.close();		
	}

/*	Función que importa los eventos almacenados en un archivo csv al calendario establecido por defecto en ese momento*/
	protected void importarEventos(String NombreArchivo) throws IOException, ServiceException
	{
		String pathCompleto = "csvFiles\\" + NombreArchivo;
		// Se abre el archivo a leer
		CsvReader csv = new CsvReader(pathCompleto);
		String csvFile = pathCompleto;
		
		BufferedReader br = null;
		String line = "";
		try 
		{
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			System.out.println("Cabecera: " + line);
			while ((line = br.readLine()) != null) 
			{
				String contenido = "";
				String lugar = "";
				// Se muestran el evento y sus parámetros
				String[] datos =line.split(";");
	
				System.out.print(datos.length);
				System.out.print(datos[0]+"\t");
				String titulo = datos[0];
				System.out.print(datos[1]+"\t");
				String inicio = datos[1];
				System.out.print(datos[2]+"\t");
				String fin = datos[2];
				if (datos.length > 3){
					System.out.print(datos[3]+"\t");
					lugar = datos[3];
				}
				
				if (datos.length > 4){
					System.out.print(datos[4]+"\t");
					System.out.println();
					contenido = datos[4];
				}

				// Se crea el evento en el calendario correspondiente
				creandoEvento(titulo, contenido, inicio, fin, lugar);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File Not Found");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Formato de fichero no válido");
			System.exit(1);
		}		
		csv.close();
		
	}

protected void Ayuda(){

	System.out.println( "gcid: \t\t Establecer identidad\n\n" +
						"gccls: \t\t Muestra calendarios visibles\n\n" +
						"gcsp: \t\t Muestra permisos\n\n" +
						"gccd: \t\t Establece calendario por defecto\n\n" +
						"gcmkev: \t\t Crear evento\n" +
						"gcmkev -t Título \t Crear evento por titulo\n" +
						"gcmkev -f AAAA-MM-DD \t Crear evento por fecha\n" +
						"gcmkev -h HH:MM:SS \t Crear evento por hora\n" +
						"gcmkev -tfh Titulo AAAA-MM-DD HH:MM:SS \t Crear evento por titulo fecha hora\n\n"+
						"gclistev: \t\t Listar eventos\n" +
						"gclistev -t Título \t Listar eventos por titulo\n" +
						"gclistev -f fInicio fFin Listar eventos comprendidos en un rango de fechas\n" +
						"gclistev -l Lugar \t Listar eventos por lugar\n\n" +
						"gcrmev: \t\t Eliminar evento\n\n" +
						"gcedev: \t\t Modificar evento\n\n" +
						"gcimp: \t\t\t Importar eventos\n"  +
						"gcimp -f  Archivo \t Importar eventos del archivo seleccionado\n"  +
						"gcimp -fc Arch Calend \t Importar eventos del archivo seleccionado al calendario seleccionado\n\n" +
						"gcexp: \t\t\t Exportar eventos\n"  +
						"gcexp -f  Archivo \t Exportar eventos al archivo seleccionado\n"  +
						"gcexp -fc Arch Calend \t Exportar eventos al archivo seleccionado del calendario seleccionado\n"
			);
	
}

}
