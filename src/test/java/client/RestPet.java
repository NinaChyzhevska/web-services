package client;

import static client.RestClientFactory.petStore;

import io.restassured.response.Response;
import models.Pet;

public class RestPet {

    private static final String PET = "/pet";

    public static Response postPet(final Pet pet, final int statusCode) {
        return petStore()
            .body(pet)
            .when()
            .post(PET)
            .then()
            .log().body()
            .statusCode(statusCode)
            .extract().response();
    }
}
