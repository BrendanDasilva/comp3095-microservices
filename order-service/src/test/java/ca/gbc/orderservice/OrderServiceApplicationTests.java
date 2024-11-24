package ca.gbc.orderservice;

import ca.gbc.orderservice.stub.InventoryClientStub;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import static org.hamcrest.MatcherAssert.assertThat;

	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	@TestPropertySource(properties = "wiremock.server.port=8089")
	class OrderServiceApplicationTests {


		@ServiceConnection
		static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

		@LocalServerPort
		private Integer port;

		@BeforeEach
		void setUp() {
			RestAssured.baseURI = "http://localhost/";
			RestAssured.port = port;
			WireMockServer wireMockServer = new WireMockServer(8089);
			wireMockServer.start();
			WireMock.configureFor("localhost", 8089);
		}

		static {
			postgreSQLContainer.start();
		}

	@Test
	void shouldSubmitOrder() {
		String submitOrderJson = """
				{
				"skuCode" : "samsung_tv_2024",
				"price" : 5000,
				"quantity" : 10
				}
				""";

		// week 10 - mock a call to inventory-service
		InventoryClientStub.stubInventoryCall("samsung_tv_2024", 10);

		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));

	}

}
