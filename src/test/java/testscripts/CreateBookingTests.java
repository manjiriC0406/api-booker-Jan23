package testscripts;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.request.createbooking.Bookingdates;
import pojo.request.createbooking.CreateBookingRequest;

public class CreateBookingTests {
	String token;
	int bookingId;
	CreateBookingRequest payload;
	
	@BeforeMethod
	public void generateToken() {
		System.out.println("Technocredits");
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";
		Response res = RestAssured.given()
//				.log().all()
				.headers("Content-Type","application/json")
				.body("{\r\n"
						+ "    \"username\" : \"admin\",\r\n"
						+ "    \"password\" : \"password123\"\r\n"
						+ "}")
				.when()
				.post("/auth");
//				.then().assertThat().statusCode(200)
//				.log().all()
//				.extract()
//				.response();
//		System.out.println(res.statusCode());
		Assert.assertEquals(res.statusCode(), 200);
//		System.out.println(res.asPrettyString());
		token = res.jsonPath().getString("token");
		System.out.println(token);
	} 
	@Test
	public void createBookingTests() {
		
		Bookingdates bookingdates = new Bookingdates();
		bookingdates.setCheckin("2013-02-23");
		bookingdates.setCheckout("2013-02-25");
		
		CreateBookingRequest payload = new CreateBookingRequest();
		payload.setFirstname("Manjiri");
		payload.setLastname("Kapre");
		payload.setDepositpaid(true);
		payload.setTotalprice(112);
		payload.setAdditionalneeds("Breakfast");
		payload.setBookingdates(bookingdates);
		
		Response res = RestAssured.given()
		.headers("Content-Type","application/json")
		.headers("Accept","application/json")
		.body(payload)
		.when()
		.post("/booking");
		
		
		System.out.println(res.statusCode());
		System.out.println(res.statusLine());
//		Assert.assertEquals(res.getStatusCode(), );
		
		bookingId = res.jsonPath().getInt("bookingid");
		System.out.println(bookingId);
		Assert.assertTrue(bookingId>0);
		
		Assert.assertEquals(res.jsonPath().getString("booking.firstname"), payload.getFirstname());
		Assert.assertEquals(res.jsonPath().getString("booking.lastname"), payload.getLastname());
		Assert.assertEquals(res.jsonPath().getInt("booking.totalprice"), payload.getTotalprice());
		Assert.assertEquals(res.jsonPath().getString("booking.bookingdates.checkin"), payload.getBookingdates().getCheckin());
		Assert.assertEquals(res.jsonPath().getString("booking.bookingdates.checkout"), payload.getBookingdates().getCheckout());
		Assert.assertEquals(res.jsonPath().getString("booking.additionalneeds"), payload.getAdditionalneeds());		
	}
	
	@Test(priority=1)
	public void getAllBookingTest() {
		int bookingId = 2522;
		Response res = RestAssured.given()
				.headers("Accept","application/json")
				.when()
				.get("/booking");
		
		Assert.assertEquals(res.getStatusCode(), 200);
		List<Integer> listOfBookingIds = res.jsonPath().getList("bookingid");
		Assert.assertTrue(listOfBookingIds.contains(bookingId));
	}
	
	@Test(priority=2)
	public void getBookingIdTest() {
		int bookingId = 2522;
		Response res = RestAssured.given()
				.headers("Accept","application/json")
				.when()
				.get("/booking/"+bookingId);
		
		Assert.assertEquals(res.getStatusCode(), 200);
		
	}
	
	@Test(priority=2)
	public void getBookingIdDeSerializedTest() {
		int bookingId = 2522;
		Response res = RestAssured.given()
				.headers("Accept","application/json")
				.when()
				.get("/booking/"+bookingId);
		
		Assert.assertEquals(res.getStatusCode(), 200);
		
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		
		Assert.assertTrue(responseBody.equals(payload));
		
	}
	
	@Test(enabled = false)
	public void createBookingTestsInPlaneMode() {
		
		String paylod = "{\r\n"
				+ "    \"username\" : \"admin\",\r\n"
				+ "    \"password\" : \"password123\"\r\n"
				+ "}";
		
		RequestSpecification reqSpec = RestAssured.given();
		reqSpec.baseUri("https://restful-booker.herokuapp.com");
		reqSpec.headers("Content-Type","application/json");
		reqSpec.body(paylod);
		Response res = reqSpec.post("/auth");
		
		System.out.println(res.statusCode());
		System.out.println(res.asPrettyString());
	}
}
