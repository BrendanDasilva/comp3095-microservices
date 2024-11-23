package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:latest");

	@LocalServerPort
	private Integer port;

	@Autowired
	private InventoryRepository inventoryRepository;

	static {
		postgresDB.start();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;

		// Initialize the database with test data
		inventoryRepository.deleteAll(); // Clear previous data
		inventoryRepository.save(new Inventory(null, "SKU-001", 10));
		inventoryRepository.save(new Inventory(null, "SKU-002", 0));
	}

	@Test
	void testIsInStock_ValidSkuAndSufficientQuantity() {
		RestAssured.given()
			.queryParam("skuCode", "SKU-001")
			.queryParam("quantity", 5)
			.when()
			.get("/api/inventory")
			.then()
			.statusCode(200)
			.body(equalTo("true")); // Response should be true
	}

	@Test
	void testIsInStock_ValidSkuAndInsufficientQuantity() {
		RestAssured.given()
			.queryParam("skuCode", "SKU-001")
			.queryParam("quantity", 20)
			.when()
			.get("/api/inventory")
			.then()
			.statusCode(200)
			.body(equalTo("false")); // Response should be false
	}

	@Test
	void testIsInStock_InvalidSkuCode() {
		RestAssured.given()
			.queryParam("skuCode", "INVALID-SKU")
			.queryParam("quantity", 5)
			.when()
			.get("/api/inventory")
			.then()
			.statusCode(200)
			.body(equalTo("false")); // Response should be false
	}
}