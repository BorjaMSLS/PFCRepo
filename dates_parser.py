string_with_dates = """
   entries are due by January 4th, 2017 at 8:00pm
   crear evento Mola el dia 13 de Junio a la 1:00pm en mi casa en el calendario Prueba

 """

import datefinder

fname = "Orden.txt"
         
       
with open(fname, 'r') as f:
    raw_txt = f.read() # esto va ser un string con toda la info
    print "Contenido texto: ", raw_txt

print raw_txt

matches = datefinder.find_dates(raw_txt)


for match in matches:
    print match.isoformat('T')+'Z'
    