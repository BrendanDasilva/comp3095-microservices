package ca.gbc.productservice;

import com.mongodb.client.MongoClient;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

// Tells SpringBoot to look for a main configuration class (@SpringBootApplication)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	// This annotation is used in combination with TestContainers to automatically configure the connection
	// to the Test MongoDBContainer
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@LocalServerPort // takes the RANDOM_PORT and injects it here
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}


	@Test
	void createProductTest() {
		String requestBody = """
				{
				  "name": "Samsung TV",
				  "description": "Samsung TV - Model 2024",
				  "price" : "2000.00"
				}
				""";

		// BDD -0 Behavioural Driven Development (Given, When, Then)
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Samsung TV"))
				.body("description", Matchers.equalTo("Samsung TV - Model 2024"))
				.body("price", Matchers.equalTo(2000.00f));
	}


	@Test
	void getAllProductTest() {
		String requestBody = """
				{
				  "name": "Samsung TV",
				  "description": "Samsung TV - Model 2024",
				  "price" : "2000.00"
				}
				""";

		// BDD -0 Behavioural Driven Development (Given, When, Then)
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Samsung TV"))
				.body("description", Matchers.equalTo("Samsung TV - Model 2024"))
				.body("price", Matchers.equalTo(2000.00f));

		RestAssured.given()
				.contentType("application/json")
				.when()
				.get("/api/product")
				.then()
				.log().all()
				.statusCode(200)
				.body("size()", Matchers.greaterThan(0));
	}


// Implement the other 2 tests for ICE-2
	@Test
	void updateProductTest() {
		String createRequestBody = """
				{
				"name": "Samsung TV",
				"description": "Samsung TV - Model 2024",
				"price" : "2000.00"
				}
				""";

		String productId = RestAssured.given()
				.contentType("application/json")
				.body(createRequestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.extract()
				.path("id");

		// Update the product
		String updateRequestBody = """
				{
				"name": "Samsung TV",
				"description": "Samsung TV - Model 2024",
				"price" : "1000.00"
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(updateRequestBody)
				.when()
				.put("/api/product/" + productId)
				.then()
				.log().all()
				.statusCode(204);

	}


	@Test
	void deleteProductTest() {
		String createRequestBody = """
				{
				"name": "Samsung TV",
				"description": "Samsung TV - Model 2024",
				"price" : "2000.00"
				}
				""";

		String productId = RestAssured.given()
				.contentType("application/json")
				.body(createRequestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.extract()
				.path("id");

		// Delete the product
		RestAssured.given()
				.when()
				.delete("/api/product/" + productId)
				.then()
				.log().all()
				.statusCode(204);

		// Verify product no longer exists
		RestAssured.given()
				.contentType("application/json")
				.when()
				.get("/api/product")
				.then()
				.log().all()
				.statusCode(200)
				.body("id", Matchers.not(Matchers.hasItem(productId)));
	}

}
