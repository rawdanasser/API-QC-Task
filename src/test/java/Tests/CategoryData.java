package Tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import APIParameter.Categories;

public class CategoryData {

	
	String id;
	String name;
	
	public String PostBody(String name ,String id) throws JsonProcessingException 
	{
		Categories category = new Categories();
		category.setname(name);
		category.Setid(id);
		ObjectMapper objectMapper = new ObjectMapper();
		String CreateCategory = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(category);
		System.out.println(CreateCategory);
		return CreateCategory;
		
		
	}
	
	
}
