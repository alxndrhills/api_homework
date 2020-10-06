package ru.bellintegrator;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

public class APITests {

    @Test
    public void avatarFileNameTest() {

        Response response = given()
                .spec(Specifications.requestSpec())
                .when()
                .get("/api/users?page=2")
                .then()
                //.log().all()
                .spec(Specifications.responseSpec200())
                .extract().response();

        JsonPath jsonResponse = response.jsonPath();
        List<HashMap<String, Object>> data = jsonResponse.get("data");

        String[] fileNames = new String[data.size()];

        for (int i = 0; i < data.size(); i++) {
            fileNames[i] = data.get(i).get("avatar").toString(); // put file names into an array of String
        }

        String expectedFileName = "128.jpg";
        Assert.assertTrue(Arrays.stream(fileNames).allMatch(x -> x.endsWith(expectedFileName)), "Имя файла не совпадает");
    }

    @Test
    public void successfulRegistrationTest() {

        //Specifications.installSpec(Specifications.requestSpec(), Specifications.responseSpec200());
        Map<String, String> data = new HashMap<String, String>();
        data.put("email", "eve.holt@reqres.in");
        data.put("password", "pistol");

        Response response = given()
                .spec(Specifications.requestSpec())
                .body(data)
                .when()
                .post("/api/register")
                .then()
                //.log().all()
                .spec(Specifications.responseSpec200())
                .extract().response();

        JsonPath jsonResponse = response.jsonPath();

        Assert.assertNotNull(jsonResponse.get("id"), "Неуспешная регистрация");
        Assert.assertNotNull(jsonResponse.get("token"), "Неуспешная регистрация");
    }

    @Test
    public void unsuccessfulRegistrationTest() {

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", "i_love_cats@meow.com");

        Response response = given()
                .spec(Specifications.requestSpec())
                .body(data)
                .when()
                .post("/api/register")
                .then()
                //.log().all()
                .spec(Specifications.responseSpec400())
                .extract().response();

        JsonPath jsonResponse = response.jsonPath();
        Assert.assertNotNull(jsonResponse.get("error"), "Регистрация должна быть неуспешной");
    }

    @Test
    public void sortedByYearTest() {

        Response response = given()
                .spec(Specifications.requestSpec())
                .when()
                .get("/api/unknown")
                .then()
                .contentType(ContentType.JSON)
                //.log().all()
                .spec(Specifications.responseSpec200())
                .extract().response();

        JsonPath jsonResponse = response.jsonPath();
        List<HashMap<String, Object>> data = jsonResponse.get("data");

        int[] years = new int[data.size()];

        for (int i = 0; i < data.size(); i++) {
            years[i] = Integer.parseInt(data.get(i).get("year").toString()); // parse year from String to int
        }

        int[] expectedYears = {2000, 2001, 2002, 2003, 2004, 2005};
        Assert.assertEquals(years, expectedYears, "Данные не отсортированы по годам");
    }
}
