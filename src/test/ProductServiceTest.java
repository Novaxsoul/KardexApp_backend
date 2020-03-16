package test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import kardexapi.services.ProductService;

public class ProductServiceTest extends JerseyTest {
	
	@Override
	protected Application configure() {
		 return new ResourceConfig(ProductService.class);
	}

	@Test
	public void testGetProducts() {
		Response response = target("/product").request().get();
		Assert.assertEquals("Debería retornar un estado 200", 200, response.getStatus());
		Assert.assertNotNull("Debería retornar una lista de productos", response.getEntity().toString());
	}

	@Test
	public void testCreateProduct1() {
		Response output = target("/product").queryParam("categ", "1").request().post(Entity.json("{\"name\":\"pruebaTestJunit\",\"cant\":50}"));
		Assert.assertEquals("Debería retornar un estado 200", 200, output.getStatus());
	}
	
	@Test
	public void testCreateProduct2() {
		Response output = target("/product").queryParam("categ", "0").request().post(Entity.json("{\"name\":\"pruebaTestJunitError\",\"cant\":50}"));
		Assert.assertEquals("Debería retornar un estado 404", 404, output.getStatus());
	}

	@Test
	public void testUpdateProduct1() {
		Response output = target("/product").queryParam("categ", "1").request().put(Entity.json("{\"id\":1,\"name\":\"pruebaTestJunitMod\",\"cant\":50}"));
		Assert.assertEquals("Debería retornar un estado 200", 200, output.getStatus());
	}
	
	@Test
	public void testUpdateProduct2() {
		Response output = target("/product").queryParam("categ", "1").request().put(Entity.json("{\"id\":0,\"name\":\"pruebaTestJunitError\",\"cant\":50}"));
		Assert.assertEquals("Debería retornar un estado 404", 404, output.getStatus());
	}
	
	@Test
	public void testUpdateProduct3() {
		Response output = target("/product").queryParam("categ", "0").request().put(Entity.json("{\"id\":1,\"name\":\"pruebaTestJunitError\",\"cant\":50}"));
		Assert.assertEquals("Debería retornar un estado 404", 404, output.getStatus());
	}

}
