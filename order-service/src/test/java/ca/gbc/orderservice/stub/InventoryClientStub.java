package ca.gbc.orderservice.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class InventoryClientStub {

  public static void stubInventoryCall(String skuCode, int quantity) {
    stubFor(get(urlPathEqualTo("/api/inventory"))
        .withQueryParam("skuCode", equalTo(skuCode))
        .withQueryParam("quantity", equalTo(String.valueOf(quantity)))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"available\": true}")));
  }


}
