import sys, os, httplib2, time, datetime

from apiclient import *
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage
import datefinder



try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/calendar-python-quickstart.json
SCOPES = 'https://www.googleapis.com/auth/calendar'
CLIENT_SECRET_FILE = 'client_secret.json'
APPLICATION_NAME = 'Google Calendar API Python Quickstart'

'''
:Decription: Listado de algunas fucniones que gestionan los eventos de Google calendar
En verde en las que quiero que te fijes al ppio pa que cojas la idea


protected void estableceIdentidad(String File) throws AuthenticationException, IOException
protected void muestraCalendariosVisibles() throws IOException, ServiceException
protected void muestraPermisos() throws IOException, ServiceException 
protected void establecerCalendarioPorDefecto() throws IOException, ServiceException

:protected void establecerCalendarioPorDefecto(String calendario) throws IOException, ServiceException

:protected void creandoEvento(String titulo, String contenido, String inicio, String fin, String lugar) throws IOException, ServiceException

protected void listarTodosEventos() throws IOException, ServiceException
protected void listarTodosEventos() throws IOException, ServiceException
protected void listarEvText(String Texto) throws ServiceException,IOException
protected void listarEvDate(String datos) throws ServiceException,IOException 

:protected void modificandoEvento(String titulo, String contenido, DateTime inicioTime, DateTime finTime, Where lugarWhere, String eventoId) throws IOException, ServiceException

'''
def find_between(s, first, last):
    """
    :Description: Este metodo devuelve una subcadena encontrada en ** s ** entre las cadenas ** first ** y ** last **.
    Si ** primer ** o ** ultimo ** no se encuentran en ** s **, se devolvera una cadena vacia
    """
    try:
        start = s.index( first) + len(first)
        end = s.index( last, start)
        return s[start:end]
    except ValueError:
        return None

def format_time(FechaHora):
    """
    :Description: Este metodo devuelve la fecha formateada
    """
    try:
        return FechaHora[0:FechaHora.index('T')], FechaHora[FechaHora.index('T')+1:(FechaHora.index('T')+len(FechaHora)-FechaHora.index('+'))]
    except ValueError:
        return None    
    
def find_ending(s, first):
    """
    :Description: Este metodo devuelve una subcadena encontrada en ** s ** entre la cadena ** first ** y ** y el final de la cadena
    """
    try:
        start = s.index( first) + len(first)
        return s[start:len(s)]
    except ValueError:
        return None
    
def get_credentials():
    """
    Gets valid user credentials from storage.

    If nothing has been stored, or if the stored credentials are invalid,
    the OAuth2 flow is completed to obtain the new credentials.

    Returns:
        Credentials, the obtained credential.
    """
    SCOPES = 'https://www.googleapis.com/auth/calendar'
    CLIENT_SECRET_FILE = 'client_secret.json'
    APPLICATION_NAME = 'Google Calendar API Quickstart'
    
    home_dir = os.path.expanduser('~')
    print home_dir
    credential_dir = os.path.join(home_dir, '.credentials')
    if not os.path.exists(credential_dir):
        os.makedirs(credential_dir)
    credential_path = os.path.join(credential_dir,
                                   'calendar-python-quickstart.json')

    store = Storage(credential_path)
    credentials = store.get()
    if not credentials or credentials.invalid:
        flow = client.flow_from_clientsecrets(CLIENT_SECRET_FILE, SCOPES)
        flow.user_agent = APPLICATION_NAME
        credentials = tools.run_flow(flow, store, flags)
        print('Storing credentials to ' + credential_path)
    return credentials
    		
def crear_evento(texto):
    # Sintaxis esperada en el comando: Crear evento <titulo> [el dia <dia>] a la(s) <hora> [en <lugar>] en el calendario <calendario> 
    # Obtencion de los parametros necesarios en esta funcion
    
    # creo un diccionario con los parametros del evento
    datos = {}
	
    titulo_evento = find_between( texto, "crear evento ", "el dia ") 
    dia = find_between( texto,  "el dia ", "a la ")
    hora = find_between( texto,  "a la ", "en ")
    lugar = find_between( texto,  "en ", ' al calendario ')
    calendario = find_ending( texto,  "al calendario ")
    #lugar = find_ending( texto,  "en ")
    if not calendario:
        calendario = 'primary'
        lugar = find_ending( texto,  "en ")
    else:
        lugar = find_between( texto,  "en ", ' al calendario ')
    if not lugar:
        lugar = ''
        hora = find_ending( texto,  "a la ")  
        
    else:
        hora = find_between( texto,  "a la ", "en ")
    if not hora:
        raise Exception('\nEl comando: "' + texto + '" incompleto. Especifica la hora ')
    
    if not dia: 
        dia = time.strftime("Hoy")
        titulo_evento = find_between( texto, "crear evento ", "a la ")
  

    datos['titulo_evento'] = titulo_evento
    datos['dia'] = dia
    datos['hora'] = hora
    datos['lugar'] = lugar
    datos['calendario'] = calendario # dejo este por defecto. anades tu la syntaxis para recoger el nombre del calendario
    
    # de aqui hay que sacar tambien el calendario, y si no se dice, se cogera el primary por defecto
    return datos


def eliminar_evento(texto, cred, prot):
    
    #Sintaxis: Eliminar <titulo del evento>
    
    evento = find_ending(texto, "eliminar ")
    print evento
    
    
    if evento == None:
        raise Exception('El evento no existe')
        
    else:
        service = discovery.build('calendar', 'v3', http=prot)
        
        service.events().delete(calendarId='9mph0mch2j6gpcmpd3m7tcn4c8@group.calendar.google.com', eventID = 'eventID').execute()
        
   


def consultar_calendario(texto, cred, prot):   

    #Description: asumo que tendra una syntaxis 'consultar <nombre_calendario> el <fecha>'
    #Ejemplo: consultar Principal 3 de Abril
    
    datos = {}

    calendario = find_between(texto, "consultar el calendario", " el dia")
    
    if not calendario:
        datos['calendario']  = 'primary'
        
    else:
        datos['calendario'] = calendario
        
        
    
    service = discovery.build('calendar', 'v3', http=prot)

    page_token = None
    
    while True:
        calendar_list = service.calendarList().list(pageToken=page_token, maxResults=1).execute()        
        
        for calendar_list_entry in calendar_list['items']:
            CalendID = calendar_list_entry['id']
            
        if not page_token:
            break
    
    print CalendID
    
    now = datetime.datetime.utcnow().isoformat() + 'Z' # 'Z' indicates UTC time
    service = discovery.build('calendar', 'v3', http=prot)
    eventsResult = service.events().list(calendarId=CalendID, timeMin=now, maxResults=1, singleEvents=True, orderBy='startTime').execute()
    events = eventsResult.get('items', [])
    if not events:
        print('No upcoming events found.')
    for event in events:
        start = event['start'].get('dateTime', event['start'].get('date'))
        startF = format_time(start)[1]
        end = event['end'].get('dateTime', event['end'].get('date'))
        endF = format_time(end)[1]
        print 'El proximo evento es ', event['summary'], ' empieza a las ', startF, ' y finaliza las ', endF
    
    return datos
        
        
    #texto.find("el dia") #No entiendo esto, tal vez es que como era un ejemplo solo pusiste lo de buscar dia y ya no?

    
def main(argv=None):
        
    '''
    #Command line options.
    '''
    # Leer texto. Que este en la misma carpeta que este script
    
    fname = "Orden.txt"
    
    
    if os.path.isfile(os.path.join(os.getcwd(),fname)):
        credentials = get_credentials()
        http = credentials.authorize(httplib2.Http())
        
    else:
        print "Fichero no existe"            
       
    with open(fname, 'r') as f:
        raw_txt = f.read() # esto va ser un string con toda la info
        print "Contenido texto: ", raw_txt
    
    
    #cosas utiles: 
    matches = datefinder.find_dates(raw_txt)
    for match in matches:
        print match.isoformat('T')+'Z'
    
    service = discovery.build('calendar', 'v3', http=http)
    calendar_list = service.calendarList().list().execute()

    for calendar_list_entry in calendar_list['items']:
        print "\n\npara este calendario", calendar_list_entry['summary'], " los parametros son:"
        for k,v in calendar_list_entry.iteritems():
            print k,v


    # Parsear info. Buscar palabras clave que pueden ser las acciones: consultar calendario, crear evento, ...
    if 'consultar' in raw_txt:
        info = consultar_calendario(raw_txt, credentials, http)
        
        
    elif 'crear evento' in raw_txt:
        
        
        info = crear_evento(raw_txt)
        

        
        event = {'summary':info['titulo_evento'],'start': {'dateTime': info['dia'],'timeZone': 'America/Los_Angeles'},'end': {'dateTime': info['dia'],'timeZone': 'America/Los_Angeles'}}
        print event
        
        page_token = None
        while True:
          calendar_list = service.calendarList().list(pageToken=page_token).execute()
          for calendar_list_entry in calendar_list['items']:
              if calendar_list_entry['summary'] == info['calendario']:
                eventoCreado = service.events().insert(calendarId=calendar_list_entry['id'], body=event).execute()
                break
        
          page_token = calendar_list.get('nextPageToken')
          if not page_token:
            break

    elif 'eliminar' in raw_txt:
        info = eliminar_evento(raw_txt, credentials, http)
        
        
    else:
        print "error. No se reconoce macomando"
    
    #print "Lista con parametros:"
    
    #for item in info:
    #   print "param: ", item
    
    # Y aqui sera donde creemos un objecto de otra clase Calendar y llamemos a la funcion que sea
    # Es escribir lo de Mario en Python con la nueva API
        
        
    return 0


if __name__ == '__main__':
    sys.exit(main())   
