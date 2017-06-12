import sys, os

def main(argv=None):
   
   
    fname = "Orden.txt"
    try:
        if os.path.isfile(os.path.join(os.getcwd(),fname)): 
            print "fichero existe. Vamos a parsear"
        else:
            print "fichero noexiste"
        sys.exit()
       
    except Exception, e:
        print "An exception has occured", e

if __name__ == '__main__':
    sys.exit(main()) 
    