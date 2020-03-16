package test;


import org.junit.Assert;
import org.junit.Test;

import kardexapi.services.CategoryService;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import javax.ws.rs.client.Entity;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;


public class CategoryServiceTest extends JerseyTest {
	
	
	@Override
	protected Application configure() {
		 return new ResourceConfig(CategoryService.class);
	}
	
	@Test
	public void testGetCategories() {
		Response response = target("/category").request().get();
		Assert.assertEquals("Debería retornar un estado 200", 200, response.getStatus());
		Assert.assertNotNull("Debería retornar una lista de categorías", response.getEntity().toString());
	}

	@Test
	public void testCreateCategory() {
		Response output = target("/category").request().post(Entity.json("{\"name\":\"pruebaTestJunit\",\"status\":true}"));
		Assert.assertEquals("Debería retornar un estado 200", 200, output.getStatus());
	}

	@Test
	public void testUpdateCategory() {
		Response output = target("/category").request().put(Entity.json("{\"id\":1,\"name\":\"pruebaTestJunitMod\",\"status\":true}"));
		Assert.assertEquals("Debería retornar un estado 200", 200, output.getStatus());
	}

	@Test
	public void testUpdateCategory2() {
		Response output = target("/category").request().put(Entity.json("{\"id\":0,\"name\":\"pruebaTestJunitError\",\"status\":true}"));
		Assert.assertEquals("Debería retornar un estado 404", 404, output.getStatus());
	}

}
