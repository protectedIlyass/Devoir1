package tp.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.http.HTTPException;

import tp.model.Animal;
import tp.model.AnimalNotFoundException;
import tp.model.Cage;
import tp.model.Center;
import tp.model.Position;

@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
public class MyServiceTP implements Provider<Source> {

    public final static String url = "http://127.0.0.1:8084/";

    public static void main(String args[]) {
        Endpoint e = Endpoint.create(HTTPBinding.HTTP_BINDING, new MyServiceTP());

        e.publish(url);
        System.out.println("Service started, listening on " + url);
        // pour arrêter : e.stop();
    }

    private JAXBContext jc;

    @javax.annotation.Resource(type = Object.class)
    protected WebServiceContext wsContext;

    private Center center = new Center(new LinkedList<>(), new Position(49.30494d, 1.2170602d), "Biotropica");

    public MyServiceTP() {
        try {
            jc = JAXBContext.newInstance(Center.class, Cage.class, Animal.class, Position.class);
        } catch (JAXBException je) {
            System.out.println("Exception " + je);
            throw new WebServiceException("Cannot create JAXBContext", je);
        }

        // Fill our center with some animals
        Cage usa = new Cage(
                "usa",
                new Position(49.305d, 1.2157357d),
                25,
                new LinkedList<>(Arrays.asList(
                        new Animal("Tic", "usa", "Chipmunk", UUID.randomUUID()),
                        new Animal("Tac", "usa", "Chipmunk", UUID.randomUUID())
                ))
        );

        Cage amazon = new Cage(
                "amazon",
                new Position(49.305142d, 1.2154067d),
                15,
                new LinkedList<>(Arrays.asList(
                        new Animal("Canine", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("Incisive", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("Molaire", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("De lait", "amazon", "Piranha", UUID.randomUUID())
                ))
        );

        center.getCages().addAll(Arrays.asList(usa, amazon));
    }

    public Source invoke(Source source) {
        MessageContext mc = wsContext.getMessageContext();
        String path = (String) mc.get(MessageContext.PATH_INFO);
        String method = (String) mc.get(MessageContext.HTTP_REQUEST_METHOD);

        // determine the targeted ressource of the call
        try {
            // no target, throw a 404 exception.
            if (path == null) {
                throw new HTTPException(404);
            }
            // "/animals" target - Redirect to the method in charge of managing this sort of call.
            else if (path.startsWith("animals")) {
                String[] path_parts = path.split("/");
                switch (path_parts.length){
                    case 1 :
                        return this.animalsCrud(method, source);
                    case 2 :
                        return this.animalCrud(method, source, path_parts[1]);
                    case 3 :
                        return this.animalOthers(method, source, path_parts[2]);
                    default:
                        throw new HTTPException(404);
                }
            }
            
            // "/cages" target - Redirect to the method in charge of managing this sort of call.
            else if (path.startsWith("cages")) {
                String[] path_parts = path.split("/");
                switch (path_parts.length){
                    case 1 :
                        return this.cagesCrud(method, source);
                    case 2 :
                        return this.cageCrud(method, source, path_parts[1]);
                    default:
                        throw new HTTPException(404);
                }
            }
            
 else if (path.startsWith("find/")) {
            	
            	String[] path_parts = path.split("/");
            	if("byName".equals(path_parts[1])){
            		try {
						return new JAXBSource(this.jc, center.findAnimalById(center.findAnimalByName(path_parts[2]).getId()));
					} catch (AnimalNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new HTTPException(503);
					}
            	}
            	else if("at".equals(path_parts[1])){
            		
            		String[] coord = path_parts[2].split(",");
            		Position p = new Position();
            		p.setLatitude(Double.parseDouble(coord[0]));
            		p.setLongitude(Double.parseDouble(coord[1]));
            		 return new JAXBSource(this.jc,this.center.getCages()
                      .stream()
                      .filter(cage -> cage.getPosition().equals(p))
                      .findFirst()
                      .orElseThrow(() -> new HTTPException(404)));
            		
            		
            	}
            		else if("near".equals(path_parts[1])){
            		
            		String[] coord = path_parts[2].split(",");
            		Position p = new Position();
            		p.setLatitude(Double.parseDouble(coord[0]));
            		p.setLongitude(Double.parseDouble(coord[1]));
            		 return new JAXBSource(this.jc,this.center.getCages()
                      .stream()
                      .filter(cage -> cage.getPosition().near(p))
                      .findFirst()
                      .orElseThrow(() -> new HTTPException(404)));
            		
            		
            	}
            	
            	else{
            		throw new HTTPException(503);
            	}
            
            }
            else if ("coffee".equals(path)) {
                throw new HTTPException(418);
            }
            else {
                throw new HTTPException(404);
            }
        } catch (JAXBException | AnimalNotFoundException e) {
            throw new HTTPException(500);
        }
    }

  

	private Source cageCrud(String method, Source source, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private Source cagesCrud(String method, Source source) throws JAXBException{
		if("GET".equals(method)){
            return new JAXBSource(this.jc, this.center);
        }
        else if("POST".equals(method)){
            Cage cage = unmarshalCage(source);
            this.center.add_cage(cage);
            return new JAXBSource(this.jc, this.center);
        }
        else if("PUT".equals(method)){
        	Center c = unmarshalCenter(source);
        	
        	this.center = c;        
        	
        	return new JAXBSource(this.jc, this.center);
        }
        else{
            throw new HTTPException(405);
        }
	}



	/**
     * Method bound to calls on /animals/{something}
	 * @throws AnimalNotFoundException 
     */
    private Source animalCrud(String method, Source source, String animal_id) throws JAXBException, AnimalNotFoundException {
        if("GET".equals(method)){
            try {
                return new JAXBSource(this.jc, center.findAnimalById(UUID.fromString(animal_id)));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        
        if("POST".equals(method)){
            try {
            	Animal animal = unmarshalAnimal(source);
                return new JAXBSource(this.jc, center.createAnimalById(animal, UUID.fromString(animal_id)));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        
        if("DELETE".equals(method)){
            try {
                return new JAXBSource(this.jc, center.deleteAnimalById(UUID.fromString(animal_id)));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        
        else if("PUT".equals(method)){
        	
      	  Animal new_animal = unmarshalAnimal(source);
      	  Animal old_animal = center.findAnimalById(UUID.fromString(animal_id));
      	  //supprimer l'ancien animal
      	  this.center.getCages()
            .stream()
            .filter(cage -> cage.getName().equals(old_animal.getCage()))
            .findFirst()
            .orElseThrow(() -> new HTTPException(404)).getResidents().remove(old_animal);
      	  //ajouter le nouvel animal
      	   this.center.addAnimal(this.center.getCages()
                    				.stream()
                    					.filter(cage -> cage.getName().equals(new_animal.getCage()))
                    						.findFirst()
                    							.orElseThrow(() -> new HTTPException(404)),new_animal);
                    
            
        
          return new JAXBSource(this.jc, this.center);
              
      }

        else{
            throw new HTTPException(405);
        }
    }

    /**
     * Method bound to calls on /animals
     */
    private Source animalsCrud(String method, Source source) throws JAXBException {
        if("GET".equals(method)){
            return new JAXBSource(this.jc, this.center);
        }
        
        else if("POST".equals(method)){
        	Animal animal = unmarshalAnimal(source);
            Cage cibledCage= this.center.getCages()
    				.stream()
					.filter(cage -> cage.getName().equals(animal.getCage()))
						.findFirst()
							.orElseThrow(() -> new HTTPException(404));
            if(!cibledCage.isFull()){
            	this.center.addAnimal(cibledCage,animal);
            }
            
                    
            return new JAXBSource(this.jc, this.center);
        }
        else if("PUT".equals(method)){
     
        	Cage c = unmarshalCage(source);
        	this.center.getCages()
            .stream()
            .filter(cage -> cage.getName().equals(c.getName()))
            .findFirst()
            .orElseThrow(() -> new HTTPException(404))
            .setResidents(c.getResidents());
        	
        	return new JAXBSource(this.jc, this.center);
        }
        
        else if("DELETE".equals(method)){
        	
            this.center.getCages().clear();
        	
        	 Cage usa = new Cage("usa",new Position(49.305d, 1.2157357d),25,null );           
             Cage amazon = new Cage("amazon",new Position(49.305142d, 1.2154067d),15,null);

             this.center.getCages().addAll(Arrays.asList(usa, amazon));
         
            return new JAXBSource(this.jc, this.center);                
        }
        
        else{
            throw new HTTPException(405);
        }
    }
    
    private Source animalOthers(String method, Source source, String param) throws JAXBException {
    	if("DELETE".equals(method)){     
        	Cage c =  this.center.getCages()
            .stream()
            .filter(cage -> cage.getName().equals(param))
            .findFirst()
            .orElseThrow(() -> new HTTPException(404));
        	c.getResidents().clear();
        	
        	return new JAXBSource(this.jc, this.center);
        }
    	else{
            throw new HTTPException(405);
        }
	}
    

    private Animal unmarshalAnimal(Source source) throws JAXBException {
        return (Animal) this.jc.createUnmarshaller().unmarshal(source);
    }
    
    private Center unmarshalCenter(Source source) throws JAXBException {
        return (Center) this.jc.createUnmarshaller().unmarshal(source);
    }
    
	private Cage unmarshalCage(Source source) throws JAXBException{
		return (Cage) this.jc.createUnmarshaller().unmarshal(source);
	}
}
