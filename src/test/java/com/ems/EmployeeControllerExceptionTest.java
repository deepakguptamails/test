package com.ems;

import com.ems.entity.Employee;
import com.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class EmployeeControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        // Simulate not found
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found for this id :: 1"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateEmployee_DuplicateEmail() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmailId("john.doe@example.com");
        // Simulate duplicate email
        when(employeeRepository.findByEmailId("john.doe@example.com")).thenReturn(employee);

        String employeeJson = "{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"emailId\": \"john.doe@example.com\"}";

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee already exists with this email id :: john.doe@example.com"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
} 