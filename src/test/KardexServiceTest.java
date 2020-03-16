package test;


import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import kardexapi.services.KardexService;;

public class KardexServiceTest extends JerseyTest {
	
	@Override
	protected Application configure() {
		 return new ResourceConfig(KardexService.class);
	}

	@Test
	public void testGetKardex() {
		Response response = target("/kardex").request().get();
		Assert.assertEquals("Debería retornar un estado 200", 200, response.getStatus());
		Assert.assertNotNull("Debería retornar una lista de productos", response.getEntity().toString());
	}

	@Test
	public void testCreateKardex1() {
		Response output = target("/kardex").queryParam("prod", "1").request().post(Entity.json("{\"transType\":1,\"units\":50,\"unitCost\":1200,\"transDate\":\"2020-03-17\" }"));
		Assert.assertEquals("Debería retornar un estado 200", 200, output.getStatus());
	}
	
	@Test
	public void testCreateKardex2() {
		Response output = target("/kardex").queryParam("prod", "0").request().post(Entity.json("{\"transType\":1,\"units\":50,\"unitCost\":1200,\"transDate\":\"2020-03-17\" }"));
		Assert.assertEquals("Debería retornar un estado 404", 404, output.getStatus());
	}
	
	@Test
	public void testCreateKardex3() {
		Response output = target("/kardex").queryParam("prod", "1").request().post(Entity.json("{\"transType\":2,\"units\":9999,\"unitCost\":1200,\"transDate\":\"2020-03-17\" }"));
		Assert.assertEquals("Debería retornar un estado 400", 400, output.getStatus());
	}

}
