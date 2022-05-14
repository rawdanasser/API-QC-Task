package Tests;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import Data.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Categories_TC {

	ConfigFileReader ConfigFileReader;
	CategoryData Catergory;
	String CSV_File;                                                           //Create a string to store the csv path
	CSVReader reader;                                                          //Create object to read the csv data
	String[] csvcell;                                                          //Create array to save the csv cells data


	@BeforeTest
	public void GetTheUserId()
	{
		ConfigFileReader = new ConfigFileReader();
		baseURI = ConfigFileReader.getApplicationUrl();
	}
	/**********************************************************************************************************/
	// Test for getting data and check the status code
	@Test(priority = 1,enabled = true)
	public void TC_GET_status_code()
	{

		given().
		header("Content-Type", "application/json")
		.get("/categories").
		then().
		statusCode(200);
	}
	/**********************************************************************************************************/
	// Test for getting Limit and check the response size
	@Test(priority = 2,enabled = true)
	public void TC_GET_LIMIT_check_status_code_and_lenght()
	{

		given().
		header("Content-Type", "application/json").
		queryParam("$limit", 20).
		get("/categories").
		then().
		statusCode(200).
		body("data.size()", equalTo(20))
		.log().all();
	}
	/**********************************************************************************************************/
	// Test the limit by hitting the API with invalid data
	@Test(priority = 3,enabled = true)
	public void TC_GET_LIMIT_bad_request()
	{

		given().
		header("Content-Type", "application/json").
		queryParam("$limit", "five").
		get("/categories").
		then().
		statusCode(500)
		.log().all();
	}
	/**********************************************************************************************************/
	// Test for getting Skip and check the response size
	@Test(priority = 4,enabled = true)
	public void TC_GET_SKIP_status_code_and_lenght()
	{

		given().
		header("Content-Type", "application/json").
		queryParam("$skip", 4300).
		get("/categories").
		then().statusCode(200).
		body("data.size()", equalTo(9)).
		log().all();
	}
	/**********************************************************************************************************/
	// Test the skip parameter by hitting the API with invalid data
	@Test(priority = 5,enabled = true)
	public void TC_GET_SKIP_bad_request()
	{

		given().
		header("Content-Type", "application/json").
		queryParam("$skip", "two").
		get("/categories").
		then().statusCode(500);
	}
	/**********************************************************************************************************/
	// Test for getting request with each attribute
	@Test(priority = 6,enabled = true)
	public void TC_GET_request_with_Valid_Param()
	{

		given().
		header("Content-Type", "application/json").
		queryParam("name", "Gift Ideas").
		get("/categories").
		then().statusCode(200).
		log().all();
	}
	/**********************************************************************************************************/
	// Test for hitting the API with invalid ID
	@Test(priority = 7,enabled = true)
	public void TC_GET_request_with_Not_exist_id()
	{

		given().
		header("Content-Type", "application/json").
		get("/categories/38526547654").
		then().statusCode(404).
		log().all();
	}
	/**********************************************************************************************************/
	// Test for posting data and check the response then delete the posted request 
	@Test(priority = 8)
	public void TC_Post_valid_request() throws CsvValidationException, JsonProcessingException, IOException 
	{

		CSV_File = System.getProperty("user.dir")+"/src/test/java/Data/PostData.csv";
		Catergory = new CategoryData();

		//while loop will be executed till the last value in csv file
		reader = new CSVReader(new FileReader(CSV_File));                      //Create a new object reads data from the csv file
		while((csvcell= reader.readNext()) != null)                            //Create a while loop to read all the csv cells data
		{
			String name = csvcell[0];
			String id = csvcell[1];                                
			given().
			header("Content-Type", "application/json").
			contentType(ContentType.JSON).
			accept(ContentType.JSON).
			body(Catergory.PostBody(name, id)).
			when().
			post("/categories").
			then().
			statusCode(201);
			
			//Check that the post request posted the data successfully
			given().
			header("Content-Type", "application/json").
			queryParam("name", name).
			get("/categories").
			then().
			statusCode(200).
			body("data.size()", equalTo(1)).log().all();
			
			//Delete the posted data
			given().queryParam("name", name)
			.delete("/categories").
			then().
			statusCode(200);
		}
			
	}
	
	/**********************************************************************************************************/
	//Post request without name
	@Test(enabled = true,priority =9)
	void TC_Post_request_with_no_NAME() {

		JSONObject request = new JSONObject();
		request.put("id", "111");

		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		post("/categories").
		then().
		statusCode(400);	

	}
	/**********************************************************************************************************/
	//Post request without ID
	@Test(enabled = true,priority =10)
	void TC_Post_request_with_no_ID() {

		JSONObject request = new JSONObject();
		request.put("name", "testCategoryName");

		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		post("/categories").
		then().
		statusCode(400);	
	}
	/**********************************************************************************************************/
	//Post request without data
	@Test(enabled = true,priority =11)
	void TC_Post_request_with_no_body() {
		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		when().
		post("/categories").
		then().
		statusCode(400);
	}
	/**********************************************************************************************************/
    // Delete invalid data
	@Test(enabled = true,priority =12)
	void TC_Delete_request_with_not_exist_id() {
		given().
		delete("/categories/1111111111").
		then().statusCode(404).log().all();

	}
	/**********************************************************************************************************/
	//Patch with valis name and ID
	@Test(enabled = true,priority =13)
	void TC_Patch_valid_request() {
		//create new category and return its ID 

		String storeId = beforePatchRequest();
		
		JSONObject request = new JSONObject();

		request.put("id", "555");
		request.put("name", "patched testCategoryName");	
		
		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		patch("http://localhost:3030/stores/"+storeId).
		then().
		statusCode(200).log().all();
		afterPatchRequest(storeId);

	}
	/**********************************************************************************************************/
	//Patch request with ID only
	@Test(enabled = true,priority =14)
	void TC_Patch_request_with_ID() {
		
		//create new category and return its ID 
		String categoryId = beforePatchRequest();	
		
		JSONObject request = new JSONObject();
		request.put("id", "555");
		
		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		patch("/categories/"+categoryId).
		then().
		statusCode(200).log().all();
		afterPatchRequest(categoryId);

	}
	/**********************************************************************************************************/
	//Patch request with name only
	@Test(enabled = true,priority =15)
	void TC_Patch_request_with_Name() {
		
		//create new category and return its ID 
		String categoryId = beforePatchRequest();
		
		JSONObject request = new JSONObject();
		request.put("name", "patched testCategoryName");
		
		given().
		header("Content-Type", "application/json").
		contentType(ContentType.JSON).
		accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		patch("/categories/"+categoryId).
		then().
		statusCode(200).log().all();
		afterPatchRequest(categoryId);

	}
	/**********************************************************************************************************/
	//Path request with invalid ID
	@Test(enabled = true,priority =16)
	void TC_Patch_request_with_not_exist_ID() {

		given().
		patch("/categories/1111111").
		then().
		statusCode(404);

	}
	
	/**********************************************************************************************************/
	/**********************************************************************************************************/
	//Method to post a new request and return the request ID
	public String beforePatchRequest ()
	{
		JSONObject request = new JSONObject();

		request.put("name", "testCategory1Name");
		request.put("id", "1113");


		Response response = given().
				header("Content-Type", "application/json").
				contentType(ContentType.JSON).
				accept(ContentType.JSON).
				body(request.toJSONString()).
				when().
				post("/categories");

		return response.jsonPath().getString("id");
	}
	
	//Method to Delete a request with specific ID
	public void afterPatchRequest (String categoryId)
	{
		given().
		delete("/categories/"+categoryId).
		then().statusCode(200);

	}
	
	
	
	
}
