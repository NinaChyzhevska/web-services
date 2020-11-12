package tests;

import static client.RestPet.postPet;
import static client.RestStore.deleteOrder;
import static client.RestStore.getInventory;
import static client.RestStore.getOrder;
import static client.RestStore.postOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import models.Category;
import models.Pet;
import models.PurchaseOrder;

@Epic("Pet shop API testing")
@Feature("Store feature")
public class StoreTest {

    public static final String AVAILABLE = "available";
    private static Pet savedDog;

    @BeforeAll
    public static void createDog() {
        Pet dog = new Pet();
        dog.setCategory(new Category(1, "dogs"));
        dog.setName("Gav");
        dog.setPhotoUrls(Collections.singletonList("someUrl"));
        dog.setStatus(AVAILABLE);

        savedDog = postPet(dog, HttpStatus.SC_OK).as(Pet.class);
    }

    @Test
    public void createAndFindPurchaseOrderTest() {
        PurchaseOrder order = createPurchaseOrder();
        PurchaseOrder savedOrder = getOrder(order.getId(), HttpStatus.SC_OK).as(PurchaseOrder.class);
        assertEquals(order.getPetId(), savedOrder.getPetId());
        assertEquals(order.getQuantity(), savedOrder.getQuantity());
        assertEquals(order.getStatus(), savedOrder.getStatus());
        assertEquals(order.isComplete(), savedOrder.isComplete());
    }

    @Test
    @Link("https://petstore.swagger.io/#/store/deleteOrder")
    public void deleteExistingPurchaseOrderByIdTest() {
        PurchaseOrder order = createPurchaseOrder();
        deleteOrder(order.getId(), HttpStatus.SC_OK);
        getOrder(order.getId(), HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Link("https://petstore.swagger.io/#/store/deleteOrder")
    public void deleteNotExistingPurchaseOrderByIdTest() {
        deleteOrder(999999, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Link("https://petstore.swagger.io/#/store/getInventory")
    public void petInventoriesByStatusTest() {
        Map<String, String> inventories = getInventory().as(HashMap.class);
        assertTrue(inventories.containsKey(AVAILABLE));
    }

    @Test
    public void verifyNotExistingOrderIdTest() {
        getOrder(-13123, HttpStatus.SC_NOT_FOUND);
    }

    private PurchaseOrder createPurchaseOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setPetId(savedDog.getId());
        order.setQuantity(1);
        order.setShipDate(new Date());
        order.setStatus("placed");

        return postOrder(order, HttpStatus.SC_OK).as(PurchaseOrder.class);
    }
}
