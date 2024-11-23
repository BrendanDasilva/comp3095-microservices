package ca.gbc.orderservice;

import ca.gbc.orderservice.stub.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {


	@ServiceConnection
	static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:latest");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		postgresDB.start();
	}

	@Test
	void placeOrderTest() {
		String requestBody = """
				{
				"skuCode" : "SKU-001",
				"price" : 99.99,
				"quantity" : 5
				}
				""";

		// week 10 - mock a call to inventory-service
		InventoryClientStub.stubInventoryCall("SKU-001", 5);

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.body(Matchers.equalTo("Order Placed Successfully"));

	}

}
