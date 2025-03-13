package com.examly.springapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = SpringappApplication.class)
@AutoConfigureMockMvc
class SpringappApplicationTests {

	@Autowired
	private MockMvc mockMvc;

@Test
@Order(1)
void testAddEmployeesToDatabase() throws Exception {
    // Create JSON data representing multiple Employee objects
    List<String> employeeData = Arrays.asList(
        "{\"id\": 1, \"name\": \"John Doe\", \"designation\": \"Manager\", \"salary\": 457}",
        "{\"id\": 2, \"name\": \"Jane Smith\", \"designation\": \"Developer\", \"salary\": 550}",
        "{\"id\": 3, \"name\": \"Mike Johnson\", \"designation\": \"Developer\", \"salary\": 600}",
        "{\"id\": 4, \"name\": \"Emily Brown\", \"designation\": \"Developer\", \"salary\": 480}",
        "{\"id\": 5, \"name\": \"David Lee\", \"designation\": \"Engineer\", \"salary\": 700}",
        "{\"id\": 6, \"name\": \"Sarah Clark\", \"designation\": \"Tester\", \"salary\": 520}"
    );

    // Iterate over the list of employee data and send a POST request for each employee
    for (String employeeJson : employeeData) {
        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }
}



	@Test
	@Order(2)
	void testgetAll() throws Exception {
		mockMvc.perform(get("/employees")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.name == 'Sarah Clark')]").exists())
				.andReturn();
	}

	@Test
@Order(3)
void testgetByID() throws Exception {
    mockMvc.perform(get("/employees/1")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.designation").value("Manager"))
            .andReturn();
}

@Test
@Order(4)
void testGetGroupBy() throws Exception {
    mockMvc.perform(get("/employees/groupBy/designation")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.Tester[0].id").value(6))
            .andExpect(jsonPath("$.Tester[0].name").value("Sarah Clark"))
            .andExpect(jsonPath("$.Tester[0].designation").value("Tester"))
            .andExpect(jsonPath("$.Tester[0].salary").value(520.0))
            .andExpect(jsonPath("$.Engineer[0].id").value(5))
            .andExpect(jsonPath("$.Engineer[0].name").value("David Lee"))
            .andExpect(jsonPath("$.Engineer[0].designation").value("Engineer"))
            .andExpect(jsonPath("$.Engineer[0].salary").value(700.0))
            .andExpect(jsonPath("$.Developer[0].id").value(2))
            .andExpect(jsonPath("$.Developer[0].name").value("Jane Smith"))
            .andExpect(jsonPath("$.Developer[0].designation").value("Developer"))
            .andExpect(jsonPath("$.Developer[0].salary").value(550.0))
            .andExpect(jsonPath("$.Developer[1].id").value(3))
            .andExpect(jsonPath("$.Developer[1].name").value("Mike Johnson"))
            .andExpect(jsonPath("$.Developer[1].designation").value("Developer"))
            .andExpect(jsonPath("$.Developer[1].salary").value(600.0))
            .andExpect(jsonPath("$.Developer[2].id").value(4))
            .andExpect(jsonPath("$.Developer[2].name").value("Emily Brown"))
            .andExpect(jsonPath("$.Developer[2].designation").value("Developer"))
            .andExpect(jsonPath("$.Developer[2].salary").value(480.0))
            .andExpect(jsonPath("$.Manager[0].id").value(1))
            .andExpect(jsonPath("$.Manager[0].name").value("John Doe"))
            .andExpect(jsonPath("$.Manager[0].designation").value("Manager"))
            .andExpect(jsonPath("$.Manager[0].salary").value(457.0))
            .andReturn();
}

@Test
@Order(5)
void testGetEmployeesByAttributeName() throws Exception {
    String attributeName = "name";
    String attributeValue = "David Lee";
    
    mockMvc.perform(get("/employees/findBy/{attribute}", attributeName)
            .param("value", attributeValue)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(5))
            .andExpect(jsonPath("$[0].name").value("David Lee"))
            .andExpect(jsonPath("$[0].designation").value("Engineer"))
            .andExpect(jsonPath("$[0].salary").value(700.0))
            .andReturn();
}

@Test
@Order(6)
void testGetEmployeesBySalaryRange() throws Exception {
    double minSalary = 500.0;
    double maxSalary = 800.0;
    
    mockMvc.perform(get("/employees/salaryRange")
            .param("minSalary", String.valueOf(minSalary))
            .param("maxSalary", String.valueOf(maxSalary))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[0].name").value("Jane Smith"))
            .andExpect(jsonPath("$[0].designation").value("Developer"))
            .andExpect(jsonPath("$[0].salary").value(550.0))
            .andExpect(jsonPath("$[1].id").value(3))
            .andExpect(jsonPath("$[1].name").value("Mike Johnson"))
            .andExpect(jsonPath("$[1].designation").value("Developer"))
            .andExpect(jsonPath("$[1].salary").value(600.0))
            .andExpect(jsonPath("$[2].id").value(5))
            .andExpect(jsonPath("$[2].name").value("David Lee"))
            .andExpect(jsonPath("$[2].designation").value("Engineer"))
            .andExpect(jsonPath("$[2].salary").value(700.0))
            .andExpect(jsonPath("$[3].id").value(6))
            .andExpect(jsonPath("$[3].name").value("Sarah Clark"))
            .andExpect(jsonPath("$[3].designation").value("Tester"))
            .andExpect(jsonPath("$[3].salary").value(520.0))
            .andReturn();
}


	@Test

	public void controllerfolder() {
		String directoryPath = "src/main/java/com/examly/springapp/controller"; // Replace with the path to your
																				// directory
		File directory = new File(directoryPath);
		assertTrue(directory.exists() && directory.isDirectory());
	}

	@Test
	public void controllerfile() {
		String filePath = "src/main/java/com/examly/springapp/controller/EmployeeController.java";
		File file = new File(filePath);
		assertTrue(file.exists() && file.isFile());
	}

	@Test
	public void testModelFolder() {
		String directoryPath = "src/main/java/com/examly/springapp/model"; // Replace with the path to your directory
		File directory = new File(directoryPath);
		assertTrue(directory.exists() && directory.isDirectory());
	}

	@Test
	public void testModelFile() {
		String filePath = "src/main/java/com/examly/springapp/model/Employee.java";
		File file = new File(filePath);
		assertTrue(file.exists() && file.isFile());
	}

	@Test
	public void testrepositoryfolder() {
		String directoryPath = "src/main/java/com/examly/springapp/repository"; // Replace with the path to your
																				// directory
		File directory = new File(directoryPath);
		assertTrue(directory.exists() && directory.isDirectory());
	}


}