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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import kardexapi.entities.Kardex;
import kardexapi.entities.Product;

//Servicio para gestionar el kardex
@Path("/kardex")
@Consumes(value=MediaType.APPLICATION_JSON)
@Produces(value=MediaType.APPLICATION_JSON)
public class KardexService {
	
	// Se obtiene el Entity_manager_factory creado en el archivo de persistencia
	private static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory("AppKardex");
	
	// M�todo para obtener todos los movimientos. Recibe 2 par�metros para realizar la paginaci�n
	@GET
    public Response getKardex(@QueryParam("limit") int limit, @QueryParam("offset") int offset) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		
		try {
			
			// Se obtienen los movimientos con la sentencia definida en la entidad
			TypedQuery<Kardex> query = em.createNamedQuery("Kardex.findAll", Kardex.class);
			
			// Se obtiene la cantidad de productos de la bd
			int count = ((Number)em.createNamedQuery("Kardex.findCount").getSingleResult()).intValue();
			
			// Si alguno de los par�metros de la paginaci�n no se recibe, darles un valor por defecto
			if(offset == 0 || offset < 0) offset = 0;
			if(limit == 0 || limit < 0) limit = 5;
			
			// Se realiza la paginaci�n
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			
			// Se crea un HashMap para guardar todos los datos que se enviar�n en la respuesta
			HashMap<String, Object> map = new HashMap<>();
			
			// Se obtienen los movimientos paginados del query
			List<Kardex> kardx = query.getResultList();
			
			// Se definen las variables para llevar el control de la paginaci�n
			String next = "null";
			String previous = "null";
			
			// Si la consulta no es vac�a, construir la url para la siguiente p�gina
			if(!kardx.isEmpty()) {
				int nextData = offset + limit;
				next = "/kardex/?limit=" + limit + "&offset=" + nextData;
			}
			
			// Si el offset no es nulo, construir la url para la p�gina previa
			if(offset != 0) {
				int prevData = offset - limit;
				previous = "/kardex/?limit=" + limit + "&offset=" + prevData;
			}
			
			// Se colocan los resultados que se enviar�n en la respuesta
			map.put("previous", previous);
			map.put("next", next);
			map.put("count", count);
			map.put("results", kardx);
			
			// Se env�a la respuesta
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
	
	// M�todo para guardar una categor�as. 
	// Recibe como par�metro una entidad con los datos del movimiento a guardar
	// Y el id del producto asignado al movimiento
	@POST
	public Response createKardex(Kardex kard, @QueryParam("prod") int prod_id) {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		EntityTransaction et = null;
		try {
			et = em.getTransaction();
			et.begin();
			
			// Se obtiene el producto
			Product product = em.find(Product.class, prod_id);
			
			// Si el producto existe
			if(product != null) {
				
				// Validar que si se va a realizar una venta, si haya stock del producto
				if(product.getCant() <= 0 && kard.getTransType() == 2) {
					HashMap<String, Object> map = new HashMap<>();
					String err = "No se puede realizar la venta, el producto no tiene stock";
					map.put("error", err);
					return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
				} else {
					// Validar que las unidades de la venta no superen a las unidades en stock
					if(kard.getTransType() == 2 && product.getCant() - kard.getUnits() < 0) {
						HashMap<String, Object> map = new HashMap<>();
						String err = "No se puede realizar la venta, la cantidad a vender supera el stock";
						map.put("error", err);
						return Response.status(Response.Status.BAD_REQUEST).entity(map).build();
					} else {
						
						// Se crea el movimiento
						Kardex kardex = new Kardex();
						kardex.setTransType(kard.getTransType());
						kardex.setUnits(kard.getUnits());
						kardex.setUnitCost(kard.getUnitCost());
						
						// Se obtiene el costo total del movimiento
						float totalCost = kard.getUnitCost() * kard.getUnits();
						
						kardex.setTotalCost(totalCost);
						
						kardex.setTransDate(kard.getTransDate());
						
						kardex.setProd(product);
						
						// Si se realiza una compra, se suman las unidades compradas al stock del producto
						if(kard.getTransType() == 1) {
							product.setCant(product.getCant() + kard.getUnits());
						} else {
							// Si se realiza una venta, se restan las unidades vendidas al stock del producto
							product.setCant(product.getCant() - kard.getUnits());
						}
						
						em.persist(product);
						em.persist(kardex);
						et.commit();
						
						return Response.ok(kardex).build();
					}
				}
				
				
			} else {
				HashMap<String, Object> map = new HashMap<>();
				String err = "No se encontr� el producto con el id " + prod_id;
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
