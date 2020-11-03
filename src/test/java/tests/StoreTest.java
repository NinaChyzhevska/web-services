package tests;

import config.Config;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.Category;
import models.Pet;
import models.PurchaseOrder;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Pet shop API testing")
@Feature("Store feature")
public class StoreTest {

    private static Pet savedDog;

    @BeforeAll
    public static void createDog() {
        Category category = new Category();
        category.setId(1);
        category.setName("dogs");

        Pet dog = new Pet();
        dog.setCategory(category);
        dog.setName("Gav");
        dog.setPhotoUrls(Collections.singletonList("someUrl"));
        dog.setStatus(Config.PET_STATUS);

        savedDog = given()
                .log().uri()
                .baseUri(Config.PETSTORE_BASE_URL)
                .body(dog)
                .contentType(JSON)
                .when()
                .post(Config.CREATE_PET)
                .then()
                .log().body()
                .statusCode(HttpStatus.SC_OK)
                .extract().response().as(Pet.class);
    }

    @Test
    public void createAndFindPurchaseOrderTest() {
        PurchaseOrder order = createPurchaseOrder();
        PurchaseOrder savedOrder = findPurchaseOrder(order.getId());

        assertEquals(order.getPetId(),savedOrder.getPetId());
        assertEquals(order.getQuantity(),savedOrder.getQuantity());
        assertEquals(order.getStatus(),savedOrder.getStatus());
        assertEquals(order.isComplete(),savedOrder.isComplete());
    }

    @Test
    @Link("https://petstore.swagger.io/#/store/deleteOrder")
    public void deletePurchaseOrderByIdTest() {
        PurchaseOrder order = createPurchaseOrder();
        deletePurchaseOrder(order.getId());
        findPurchaseOrder(order.getId(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Link("https://petstore.swagger.io/#/store/getInventory")
    public void petInventoriesByStatusTest() {
        createDog();
        Map<String, String> inventories = given()
                .log().uri()
                .baseUri(Config.PETSTORE_BASE_URL)
                .contentType(JSON)
                .when()
                .get(Config.GET_STORE_INVENTORY)
                .then()
                .log().body()
                .statusCode(HttpStatus.SC_OK)
                .extract().response().as(HashMap.class);

        assertTrue(inventories.containsKey(Config.PET_STATUS));
    }

    @Test
    public void verifyNotExistingOrderIdTest() {
        findPurchaseOrder(-13123, HttpStatus.SC_NOT_FOUND);
    }

    private PurchaseOrder createPurchaseOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setPetId(savedDog.getId());
        order.setQuantity(1);
        order.setShipDate(new Date());
        order.setStatus("placed");

        return given()
                .log().uri()
                .baseUri(Config.PETSTORE_BASE_URL)
                .body(order)
                .contentType(JSON)
                .when()
                .post(Config.CREATE_PURCHASE_ORDER)
                .then()
                .log().body()
                .statusCode(HttpStatus.SC_OK)
                .extract().response().as(PurchaseOrder.class);
    }

    private PurchaseOrder findPurchaseOrder(long orderId) {
        return findPurchaseOrder(orderId, HttpStatus.SC_OK).as(PurchaseOrder.class);
    }

    private Response findPurchaseOrder(long orderId, int statusCode) {
        return given()
                .log().uri()
                .baseUri(Config.PETSTORE_BASE_URL)
                .pathParam("orderId", orderId)
                .contentType(JSON)
                .when()
                .get(Config.PURCHASE_ORDER_BY_ID)
                .then()
                .log().body()
                .statusCode(statusCode)
                .extract().response();
    }

    private void deletePurchaseOrder(long orderID) {
        given()
                .log().uri()
                .baseUri(Config.PETSTORE_BASE_URL)
                .pathParam("orderId", orderID)
                .contentType(JSON)
                .when()
                .delete(Config.PURCHASE_ORDER_BY_ID)
                .then()
                .log().body()
                .statusCode(HttpStatus.SC_OK);
    }
}
