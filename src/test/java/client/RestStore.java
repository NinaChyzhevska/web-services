package client;

import static client.RestClientFactory.petStore;

import org.apache.http.HttpStatus;

import io.restassured.response.Response;
import models.PurchaseOrder;

public class RestStore {

    private static final String STORE_ORDER = "/store/order";
    private static final String STORE_ORDER_ORDER_ID = "/store/order/{orderId}";
    private static final String STORE_INVENTORY = "/store/inventory";
    private static final String ORDER_ID = "orderId";

    public static Response postOrder(final PurchaseOrder purchaseOrder, final int statusCode) {
        return petStore()
            .body(purchaseOrder)
            .when()
            .post(STORE_ORDER)
            .then()
            .log().body()
            .statusCode(statusCode)
            .extract().response();
    }

    public static Response getOrder(final long orderId, final int statusCode) {
        return petStore()
            .pathParam(ORDER_ID, orderId)
            .when()
            .get(STORE_ORDER_ORDER_ID)
            .then()
            .log().body()
            .statusCode(statusCode)
            .extract().response();
    }

    public static void deleteOrder(final long orderId, final int statusCode) {
        petStore()
            .pathParam(ORDER_ID, orderId)
            .when()
            .delete(STORE_ORDER_ORDER_ID)
            .then()
            .log().body()
            .statusCode(statusCode);
    }

    public static Response getInventory(){
        return petStore()
            .when()
            .get(STORE_INVENTORY)
            .then()
            .log().body()
            .statusCode(HttpStatus.SC_OK)
            .extract().response();
    }
}
