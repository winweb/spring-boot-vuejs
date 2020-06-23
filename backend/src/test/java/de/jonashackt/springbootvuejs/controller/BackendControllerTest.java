package de.jonashackt.springbootvuejs.controller;

import de.jonashackt.springbootvuejs.SpringBootVuejsApplication;
import de.jonashackt.springbootvuejs.domain.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = SpringBootVuejsApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class BackendControllerTest {

	@LocalServerPort
	private int port;

	private static final Logger LOG = LoggerFactory.getLogger(BackendControllerTest.class);

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
    public void init() {

		LOG.info("init");

        RestAssured.baseURI = "http://localhost";

        RestAssured.port = port;

		RestAssured.basePath = "/app";

		LOG.info(">>> URL: {}:{}{}", RestAssured.baseURI, RestAssured.port, RestAssured.basePath);
    }

	@After
	public void flush() {
	}

    @Order(1)
	@Test
	public void saysHello() {
		LOG.info(">>> saysHello");

		when()
			.get("http://localhost:8098/app/api/hello")
		.then()
			.statusCode(HttpStatus.SC_OK)
			.assertThat()
				.body(is(equalTo(BackendController.HELLO_TEXT)));
	}

	@Order(2)
	@Test
	public void reTestSaysHello() throws Exception {
		LOG.info(">>> reTestSaysHello");

		try {
			final Response response = when()
					.get("http://localhost:8098/app/api/hello");

			LOG.info(">>> response {}", response.toString());

			final String body = when()
					.get("http://localhost:8098/app/api/hello")
					.body().toString();

			LOG.info(">>> body {}", body.toString());

			given()
			.when()
				.get("http://localhost:8098/app/api/hello")
			.then()
				.statusCode(HttpStatus.SC_OK)
			.assertThat()
				.body(is(equalTo(BackendController.HELLO_TEXT)));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws ClientProtocolException, IOException {
		LOG.info(">>> givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson");

		// Given
		String jsonMimeType = "text/plain";
		HttpUriRequest request = new HttpGet( "http://localhost:8098/app/api/hello" );

		// When
		HttpResponse response = HttpClientBuilder.create().build().execute( request );

		// Then
		String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

		LOG.debug(">>> mimeType: {}", jsonMimeType);

		assertThat(mimeType,is(jsonMimeType) );
	}

	@Test
    public void addNewUserAndRetrieveItBack() {
		LOG.info(">>> addNewUserAndRetrieveItBack");

		User norbertSiegmund = new User("Norbert", "Siegmund");

        Long userId =
            given()
                .pathParam("firstName", "Norbert")
                .pathParam("lastName", "Siegmund")
            .when()
                .post("/api/user/{lastName}/{firstName}")
            .then()
                .statusCode(is(HttpStatus.SC_CREATED))
			.extract()
				.body().as(Long.class);

	    User responseUser =
            given()
				.pathParam("id", userId)
			.when()
				.get("/api/user/{id}")
			.then()
				.statusCode(HttpStatus.SC_OK)
			.assertThat()
				.extract().as(User.class);

	    // Did Norbert came back?
        assertThat(responseUser.getFirstName(), is("Norbert"));
        assertThat(responseUser.getLastName(), is("Siegmund"));
    }


	@Test
	public void user_api_should_give_http_404_not_found_when_user_not_present_in_db() {
		LOG.info(">>> user_api_should_give_http_404_not_found_when_user_not_present_in_db");

		Long someId = 200L;
		given()
			.pathParam("id", someId)
		.when()
			.get("/api/user/{id}")
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND)
		;
	}

	@Test
	public void secured_api_should_react_with_unauthorized_per_default() {
		LOG.info(">>> secured_api_should_react_with_unauthorized_per_default");

		given()
		.when()
			.get("/api/secured")
		.then()
			.statusCode(HttpStatus.SC_UNAUTHORIZED)
		;
	}

	@Test
	public void secured_api_should_give_http_200_when_authorized() {
		LOG.info(">>> secured_api_should_give_http_200_when_authorized");

		given()
		.when()
			.get("/api/all")
		.then()
			.statusCode(HttpStatus.SC_OK);
		;

		LOG.debug("case 2");

		given()
		.auth()
			.basic("sina", "miller")
		.when()
			.get("/api/secured")
		.then()
			.statusCode(HttpStatus.SC_OK)
		.assertThat()
			.body(is(equalTo(BackendController.SECURED_TEXT)))
		;
	}
}
