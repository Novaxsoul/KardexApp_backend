package kardexapi.services;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import kardexapi.entities.Category;


// Servicio para gestionar las categorías
@Path("/category")
@Consumes(value=MediaType.APPLICATION_JSON)
@Produces(value=MediaType.APPLICATION_JSON)
public class CategoryService {
	
	// Se obtiene el Entity_manager_factory creado en el archivo de persistencia
	private static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory("AppKardex");
	
	// Método para obtener todas las categorías. Recibe 2 parámetros para realizar la paginación
	@GET
    public Response getCategories(@QueryParam("limit") int limit, @QueryParam("offset") int offset) {
		
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		
		try {
			// Se obtienen las categorías con la sentencia definida en la entidad
			TypedQuery<Category> query = em.createNamedQuery("Category.findAll", Category.class);
			
			// Se obtiene la cantidad de categorías de la bd
			int count = ((Number)em.createNamedQuery("Category.findCount").getSingleResult()).intValue();
			
			// Si alguno de los parámetros de la paginación no se recibe, darles un valor por defecto
			if(offset == 0 || offset < 0) offset = 0;
			if(limit == 0 || limit < 0) limit = 5;
			
			// Se realiza la paginación
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			
			// Se crea un HashMap para guardar todos los datos que se enviarán en la respuesta
			HashMap<String, Object> map = new HashMap<>();
			
			// Se obtienen las categorías paginadas del query
			List<Category> cats = query.getResultList();
			
			// Se definen las variables para llevar el control de la paginación
			String next = "null";
			String previous = "null";
			
			// Si la consulta no es vacía, construir la url para la siguiente página
			if(!cats.isEmpty()) {
				int nextData = offset + limit;
				next = "/category/?limit=" + limit + "&offset=" + nextData;
			}
			
			// Si el offset no es nulo, construir la url para la página previa
			if(offset != 0) {
				int prevData = offset - limit;
				previous = "/category/?limit=" + limit + "&offset=" + prevData;
			}
			
			// Se colocan los resultados que se enviarán en la respuesta
			map.put("previous", previous);
			map.put("next", next);
			map.put("count", count);
			map.put("results", cats);
			
			// Se envía la respuesta
			return Response.ok(map,MediaType.APPLICATION_JSON).build();   
		} catch (NoResultException ex) {
			
			// En caso de error, enviar en la respuesta el error
			ex.printStackTrace();
			HashMap<String, Object> map = new HashMap<>();
			map.put("error", ex.getMessage());
			return Response.serverError().entity(map).build();
		} finally {
			em.close();
		}
		
       
    }  
	
	// Método para guardar una categorías. 
	// Recibe como parámetro una entidad con los datos de la categoría a guardar
	@POST
	public Response createCategory(Category cat) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		try {
			et = em.getTransaction();
			et.begin();
			Category categ = new Category();
			categ.setName(cat.getName());
			categ.setStatus(true);
			em.persist(categ);
			et.commit();
			
			return Response.ok(categ).build();
		} 
		catch(Exception ex) {
			if(et != null) {
				et.rollback();
			}
			ex.printStackTrace();
			HashMap<String, Object> map = new HashMap<>();
			map.put("error", ex.getMessage());
			return Response.serverError().entity(map).build();
		}
		finally {
			em.close();
		}
	}
	
	// Método para actualizar una categorías. 
	// Recibe como parámetro una entidad con los datos de la categoría a actualizar
	@PUT
	public Response updateCategory(Category cat) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		Category categ = null;
		try {
			et = em.getTransaction();
			et.begin();
			categ = em.find(Category.class, cat.getId());
			if (categ != null) {
				categ.setName(cat.getName());
				categ.setStatus(true);
				em.persist(categ);
				et.commit();
				
				return Response.ok(categ).build();
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontró la categoría con el id " + cat.getId();
				map.put("error", err);
				return Response.status(Response.Status.NOT_FOUND).entity(map).build();
			}
			
		} 
		catch(Exception ex) {
			if(et != null) {
				et.rollback();
			}
			ex.printStackTrace();
			HashMap<String, Object> map = new HashMap<>();
			map.put("error", ex.getMessage());
			return Response.serverError().entity(map).build();
		}
		finally {
			em.close();
		}
	}
	
	// Método para eliminar una categorías. 
	// Recibe como parámetro una entidad que contenga el id de la categoría a eliminar
	@DELETE
	public Response deleteCategory(Category cat) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		Category categ = null;
		try {
			et = em.getTransaction();
			et.begin();
			categ = em.find(Category.class, cat.getId());
			if (categ != null) {
				categ.setStatus(false);
				em.persist(categ);
				et.commit();
				
				return Response.ok(categ).build();
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontró la categoría con el id " + cat.getId();
				map.put("error", err);
				return Response.status(Response.Status.NOT_FOUND).entity(map).build();
			}
			
		} 
		catch(Exception ex) {
			if(et != null) {
				et.rollback();
			}
			ex.printStackTrace();
			HashMap<String, Object> map = new HashMap<>();
			map.put("error", ex.getMessage());
			return Response.serverError().entity(map).build();
		}
		finally {
			em.close();
		}
	}
	
}
