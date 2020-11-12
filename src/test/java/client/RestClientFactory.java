package client;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import io.restassured.specification.RequestSpecification;

public class RestClientFactory {

    private static final String PET_STORE_BASE_URL = "https://petstore.swagger.io/v2";

    public static RequestSpecification petStore() {
        return given()
            .log().uri()
            .relaxedHTTPSValidation()
            .request().with()
            .baseUri(PET_STORE_BASE_URL)
            .contentType(JSON)
            .response().with();
    }
}
