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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import kardexapi.entities.Product;
import kardexapi.entities.Category;

//Servicio para gestionar los productos
@Path("/product")
@Consumes(value=MediaType.APPLICATION_JSON)
@Produces(value=MediaType.APPLICATION_JSON)
public class ProductService {
	
	// Se obtiene el Entity_manager_factory creado en el archivo de persistencia
	private static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory("AppKardex");
	
	// Método para obtener todos los productos. Recibe 2 parámetros para realizar la paginación
	@GET
    public Response getProducts(@QueryParam("limit") int limit, @QueryParam("offset") int offset) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		
		try {
			// Se obtienen los productos con la sentencia definida en la entidad
			TypedQuery<Product> query = em.createNamedQuery("Product.findAll", Product.class);
			
			// Se obtiene la cantidad de productos de la bd
			int count = ((Number)em.createNamedQuery("Product.findCount").getSingleResult()).intValue();
			
			// Si alguno de los parámetros de la paginación no se recibe, darles un valor por defecto
			if(offset == 0 || offset < 0) offset = 0;
			if(limit == 0 || limit < 0) limit = 5;
			
			// Se realiza la paginación
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			
			// Se crea un HashMap para guardar todos los datos que se enviarán en la respuesta
			HashMap<String, Object> map = new HashMap<>();
			
			// Se obtienen las categorías paginadas del query
			List<Product> prods = query.getResultList();
			
			// Se definen las variables para llevar el control de la paginación
			String next = "null";
			String previous = "null";
			
			// Si la consulta no es vacía, construir la url para la siguiente página
			if(!prods.isEmpty()) {
				int nextData = offset + limit;
				next = "/product/?limit=" + limit + "&offset=" + nextData;
			}
			
			// Si el offset no es nulo, construir la url para la página previa
			if(offset != 0) {
				int prevData = offset - limit;
				previous = "/product/?limit=" + limit + "&offset=" + prevData;
			}
			
			// Se colocan los resultados que se enviarán en la respuesta
			map.put("previous", previous);
			map.put("next", next);
			map.put("count", count);
			map.put("results", prods);
			
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
	// Recibe como parámetro una entidad con los datos del producto a guardar
	// Y el id de la categoría asignada al producto
	@POST
	public Response createProduct(Product prod, @QueryParam("categ") int cat_id) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		try {
			et = em.getTransaction();
			et.begin();
			
			
			Category cat = em.find(Category.class, cat_id);
			
			if(cat != null) {
				Product product = new Product();
				product.setName(prod.getName());
				product.setCant(prod.getCant());
				product.setCateg(cat);
				
				product.setStatus(true);
				
				em.persist(product);
				et.commit();
				
				return Response.ok(product).build();
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontró la categoría con el id " + cat_id;
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
	
	// Método para actualizar un producto. 
	// Recibe como parámetro una entidad con los datos del producto a actualizar
	// Y el id de la categoría asignada al producto
	@PUT
	public Response updateProduct(Product prod, @QueryParam("categ") int cat_id) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		Product product = null;
		try {
			et = em.getTransaction();
			et.begin();
			product = em.find(Product.class, prod.getId());
			
			if(product != null) {
				
				Category cat = em.find(Category.class, cat_id);
				
				if(cat != null) {
					product.setName(prod.getName());
					product.setCant(prod.getCant());
					product.setCateg(cat);
					product.setStatus(true);
					em.persist(product);
					et.commit();
					
					return Response.ok(product).build();
				} else {
					HashMap<String, Object> map = new HashMap<>();
					String err = "No se encontró la categoría con el id " + cat_id;
					map.put("error", err);
					return Response.status(Response.Status.NOT_FOUND).entity(map).build();
				}
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontró el producto con el id " + prod.getId();
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
	
	// Método para eliminar un producto. 
	// Recibe como parámetro una entidad que contenga el id del producto a eliminar
	@DELETE
	public Response deleteProduct(Product prod) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		Product product = null;
		try {
			et = em.getTransaction();
			et.begin();
			product = em.find(Product.class, prod.getId());
			
			if(product != null) {
				
				product.setStatus(false);
				em.persist(product);
				et.commit();
				
				return Response.ok(product).build();
		
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontró el producto con el id " + prod.getId();
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
