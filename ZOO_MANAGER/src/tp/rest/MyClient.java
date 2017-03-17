package tp.rest;

import java.util.Scanner;
import tp.model.Animal;
import tp.model.Cage;
import tp.model.Center;
import tp.model.Position;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MyClient {
    private Service service;
    private JAXBContext jc;

    private static final QName qname = new QName("", "");
    private static final String url = "http://127.0.0.1:8084";

    public MyClient() {
        try {
            jc = JAXBContext.newInstance(Center.class, Cage.class, Animal.class, Position.class, String.class);
        } catch (JAXBException je) {
            System.out.println("Cannot create JAXBContext " + je);
        }
    }
    
    public Dispatch<Source> init(String url2, String method){
    	service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + url2);
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, method);
        return dispatcher;
    }
    
    public void add_cage(Cage cage) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/cages","POST");
        Source result = dispatcher.invoke(new JAXBSource(jc, cage));
        printSource(result);
    }

    public void add_animal(Animal animal) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/animals","POST");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    public void update_animal(Animal animal,String Id) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/animals/"+Id,"PUT");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    /*
    public void update_animals2(Center c) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/animals","PUT");
        Source result = dispatcher.invoke(new JAXBSource(jc, c));
        printSource(result);
    }*/
    
    public void update_animals(Cage c) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/animals","PUT");
        Source result = dispatcher.invoke(new JAXBSource(jc, c));
        printSource(result);
    }
    
    public void create_animal_WithId(Animal animal, UUID id) throws JAXBException {
    	Dispatch<Source> dispatcher = init("/animals/"+id,"POST");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    public void delete_animal(String id) throws JAXBException {	
    	Animal animal = new Animal();
    	Dispatch<Source> dispatcher = init("/animals/"+id,"DELETE");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    public void get_animals() throws JAXBException {
    	Animal animal = new Animal();
    	Dispatch<Source> dispatcher = init("/animals","GET");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    public void delete_animals() throws JAXBException {
    	Center center = new Center();
    	Dispatch<Source> dispatcher = init("/animals","DELETE");
        Source result = dispatcher.invoke(new JAXBSource(jc,center));
        printSource(result);
    }
    
    
    public void delete_animals_byCage(String cage) throws JAXBException {
    	Center center = new Center();
    	Dispatch<Source> dispatcher = init("/animals/byCage/"+cage,"DELETE");
        Source result = dispatcher.invoke(new JAXBSource(jc,center));
        printSource(result);
    }
    
    public void find_animal_byName(String name) throws JAXBException {
    	Center center = new Center();
    	Dispatch<Source> dispatcher = init("/find/byName/"+name,"GET");
        Source result = dispatcher.invoke(new JAXBSource(jc,center));
        printSource(result);
    }
    
    public void find_animal_atPosition(String Position) throws JAXBException {
    	Center center = new Center();
    	Dispatch<Source> dispatcher = init("/find/at/"+Position,"GET");
        Source result = dispatcher.invoke(new JAXBSource(jc,center));
        printSource(result);
    }
    
    public void find_animal_nearPosition(String Position) throws JAXBException {
    	Center center = new Center();
    	Dispatch<Source> dispatcher = init("/find/near/"+Position,"GET");
        Source result = dispatcher.invoke(new JAXBSource(jc,center));
        printSource(result);
    }
  

    public void printSource(Source s) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(s, new StreamResult(System.out));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    

    public static void main(String args[]) throws Exception {
        MyClient client = new MyClient();
        
        // Les Scénarios commencent à partir d'ici
        
        int choice;
        UUID idGalago = UUID.randomUUID();
        do {
            System.out.println("\nVeuiller selectionner un scénario : \n");
            Scanner scan = new Scanner(System.in);
            choice = scan.nextInt();

            switch (choice){
            
                case 1:
                	client.get_animals();
                    break;
                    
                case 2:
                	client.delete_animals();
                    break;
                    
                case 3:
                	client.get_animals();
                	break;
                	
                case 4:
                	client.add_cage(new Cage("Rouen", new Position(49.443889,1.103333), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Panda", "Rouen","spec",UUID.randomUUID()));
                	break;
                	
                case 5:
                	client.add_cage(new Cage("Paris", new Position(48.856578,2.35182), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Hocco unicorne", "Paris","spec",UUID.randomUUID()));
                	break;
                	
                case 6:
                	client.get_animals();
                	break;
                	
                case 7:
                	client.update_animals(new Cage( "Rouen", new Position(49.443889, 1.103333), 25,
                new LinkedList<>(Arrays.asList(
                        new Animal("Lagotriche à queue jaune", "Rouen", "Chipmunk", UUID.randomUUID())
                ))
        ));
                	break;
                	
                case 8:
                	client.get_animals();
                	break;
                	
                case 9:
                	client.add_cage(new Cage("Somalie", new Position(2.333333,48.85), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Océanite de Matsudaira", "Somalie","spec",UUID.randomUUID()));
                	break;
                	
                case 10:
                    client.add_animal(new Animal("Ara de Spix", "Rouen","spec",UUID.randomUUID()));
                	break;
                	
                case 11:
                	client.add_cage(new Cage("Bihorel", new Position(49.455278,1.116944), 100,  new LinkedList<>()));
                	
                    client.add_animal(new Animal("Galago de Rondo", "Bihorel","spec",idGalago));
                	break;
                	
                case 12:
                	client.add_cage(new Cage("Londres ", new Position(51.504872,-0.07857), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Palette des Sulu", "Londres","spec",UUID.randomUUID()));
                	break;
                	
                case 13:
                    client.add_animal(new Animal("Kouprey", "Paris","spec",UUID.randomUUID()));
                	break;
                	
                case 14:
                    client.add_animal(new Animal("Tuit­tuit", "Paris","spec",UUID.randomUUID()));
                	break;
                	
                case 15:
                	client.add_cage(new Cage("Canada", new Position(43.2,-80.38333), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Saïga", "Canada","spec",UUID.randomUUID()));
                	break;
                	
                case 16:
                	client.add_cage(new Cage("Porto­Vecchio", new Position(41.5895241,9.2627), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Inca de Bonaparte", "Porto­Vecchio","spec",UUID.randomUUID()));
                	break;
                	
                case 17:
                	client.get_animals();
                	break;
                	
                case 18:
                	client.add_cage(new Cage("Montreux", new Position(46.4307133,6.9113575), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Râle de Zapata", "Montreux","spec",UUID.randomUUID()));
                	break;
                	
                case 19:
                	client.add_cage(new Cage("Villers­Bocage", new Position(50.0218,2.3261), 100,  new LinkedList<>()));
                    client.add_animal(new Animal("Rhinocéros de Java", "Villers­Bocage","spec",UUID.randomUUID()));
                	break;
                	
                case 20:
                	for(int i = 0; i < 101; i++){
                		client.add_animal(new Animal("Dalmatiens", "usa","spec",UUID.randomUUID()));
                	}
                	break;
                	
                case 21:
                	client.get_animals();
                	break;
                	
                case 22:
                	client.delete_animals_byCage("Paris");
                	break;
                	
                case 23:
                	client.get_animals();
                	break;
                	
                case 24:
                	client.find_animal_byName("Galago de Rondo");
                	break;
                	
                case 25:
                	 client.delete_animal(idGalago.toString());
                	break;
                	
                case 26:
                	client.delete_animal(idGalago.toString());
                	break;
                	
                case 27:
                	client.get_animals();
                	break;
                	
                case 28:
                	client.find_animal_nearPosition("49.443889, 1.103333");
                	break;
                	
                case 29:
                	client.find_animal_atPosition("49.443889, 1.103333");
                	break;
                	
            }} while(choice < 100);
        
    }
}
