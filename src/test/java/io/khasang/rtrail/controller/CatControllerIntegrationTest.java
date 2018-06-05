package io.khasang.rtrail.controller;

import io.khasang.rtrail.entity.Cat;
import org.junit.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class CatControllerIntegrationTest {
    private static final String ROOT = "http://localhost:8080/cat";
    private static final String ADD = "/add";
    private static final String GET = "/get";
    private static final String DELETE = "/delete";
    private static final String ALL = "/all";
    private static final String NAME = "/name";
    private static final String UPDATE = "/update";

    @Before
    public void init() {
        System.out.println("init");
    }

    @BeforeClass
    public static void globalInit() {
        System.out.println("global init");
    }

    @Test
    public void addCat() {
        Cat cat = createdCat();

        RestTemplate template = new RestTemplate();
        ResponseEntity<Cat> responseEntity = template.exchange(
                ROOT + GET + "/{id}",
                HttpMethod.GET,
                null,
                Cat.class,
                cat.getId()
        );

        assertEquals("OK", responseEntity.getStatusCode().getReasonPhrase());
        Cat receivedCat = responseEntity.getBody();
        assertNotNull(receivedCat);
        assertNotNull(receivedCat.getDescription());
        assertEquals(cat.getId(), receivedCat.getId());
    }

    @Test
    public void getCatById() {
        Cat cat = createdCat();

        RestTemplate template = new RestTemplate();
        ResponseEntity<Cat> responseEntity = template.exchange(
                ROOT + GET + "/{id}",
                HttpMethod.GET,
                null,
                Cat.class,
                cat.getId()
        );

        Cat receivedCat = responseEntity.getBody();
        assertNotNull(receivedCat);
        assertNotNull(receivedCat.getId());
        assertEquals(cat.getId(), receivedCat.getId());
    }

    @Test
    public void deleteCat() {
        Cat cat = createdCat();

        RestTemplate template = new RestTemplate();
        ResponseEntity<Cat> responseEntity = template.exchange(
                ROOT + DELETE + "/?id={id}",
                HttpMethod.DELETE,
                null,
                Cat.class,
                cat.getId()
        );

        assertEquals("OK", responseEntity.getStatusCode().getReasonPhrase());
        Cat deletedCat = responseEntity.getBody();
        assertNotNull(deletedCat.getName());
        ResponseEntity<Cat> responseForDeleteCat = template.exchange(
                ROOT + GET + "/{id}",
                HttpMethod.GET,
                null,
                Cat.class,
                deletedCat.getId()
        );

        assertEquals("OK", responseEntity.getStatusCode().getReasonPhrase());
        assertNull(responseForDeleteCat.getBody());
    }

    @Test
    public void getAllCats() {
        createdCat();
        createdCat();

        RestTemplate template = new RestTemplate();
        ResponseEntity<List<Cat>> responseEntity = template.exchange(
                ROOT + GET + ALL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Cat>>() {
                }
        );

        List<Cat> catList = responseEntity.getBody();
        assertNotNull(catList.get(0));
        assertNotNull(catList.get(1));
    }

    @Test
    public void getCatByName() {
        Cat cat = createdCat();

        RestTemplate template = new RestTemplate();
        ResponseEntity<List<Cat>> responseEntity = template.exchange(
                ROOT + GET + NAME + "/{name}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Cat>>() {
                },
                cat.getName()
        );

        List<Cat> catList = responseEntity.getBody();
        assertNotNull(catList);
        for (Cat receivedCat : catList) {
            assertEquals(cat.getName(), receivedCat.getName());
        }
    }

    @Test
    public void updateCat() {
        String newCatName = "Boris";
        String newCatDescription = "knows the Boris cat energy secret";
        int newColorID = 3;

        Cat cat = createdCat();

        cat.setName(newCatName);
        cat.setDescription(newCatDescription);
        cat.setColorID(newColorID);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Cat> httpEntity = new HttpEntity<>(cat, headers);

        RestTemplate template = new RestTemplate();
        ResponseEntity<Cat> responseEntity = template.exchange(
                ROOT + UPDATE,
                HttpMethod.PUT,
                httpEntity,
                Cat.class
        );

        assertEquals("OK", responseEntity.getStatusCode().getReasonPhrase());
        Cat receivedCat = responseEntity.getBody();
        assertNotNull(receivedCat);
        assertEquals(newCatName, receivedCat.getName());
        assertEquals(newCatDescription, receivedCat.getDescription());
        assertEquals(newColorID, receivedCat.getColorID());
    }

    private Cat createdCat() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        Cat cat = prefillCat("Barsik");

        HttpEntity<Cat> httpEntity = new HttpEntity<>(cat, headers);
        RestTemplate template = new RestTemplate();
        Cat createdCat = template.exchange(
                ROOT + ADD,
                HttpMethod.POST,
                httpEntity,
                Cat.class
        ).getBody();

        assertNotNull(createdCat);
        assertEquals(cat.getName(), createdCat.getName());
        return createdCat;
    }

    private Cat prefillCat(String name) {
        Cat cat = new Cat();
        cat.setName(name);
        cat.setDescription("happy");
        cat.setColorID(2);
        return cat;
    }

    @After
    public void clean() {
        System.out.println("clean");
    }

    @AfterClass
    public static void globalClean() {
        System.out.println("global clean");
    }
}