package com.ems;

import com.ems.controller.EmployeeController;
import com.ems.entity.Employee;
import com.ems.exception.InvalidInputException;
import com.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmailId("john.doe@example.com");
    }

    @Test
    void testCreateEmployee_Success() throws InvalidInputException {
        when(employeeRepository.findByEmailId(employee.getEmailId())).thenReturn(null);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee created = employeeController.createEmployee(employee);
        assertNotNull(created);
        assertEquals("John", created.getFirstName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testCreateEmployee_DuplicateEmail() {
        when(employeeRepository.findByEmailId(employee.getEmailId())).thenReturn(employee);
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            employeeController.createEmployee(employee);
        });
        assertTrue(thrown.getMessage().contains("already exists with this email id"));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        employee.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        var response = employeeController.getEmployeeById(1L);
        assertNotNull(response);
        assertEquals(employee, response.getBody());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(com.ems.exception.ResourceNotFoundException.class, () -> {
            employeeController.getEmployeeById(1L);
        });
        assertTrue(exception.getMessage().contains("Employee not found for this id"));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        employee.setId(1L);
        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setEmailId("jane.smith@example.com");
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedDetails);
        var response = employeeController.updateEmployee(1L, updatedDetails);
        assertNotNull(response);
        assertEquals("Jane", response.getBody().getFirstName());
        assertEquals("Smith", response.getBody().getLastName());
        assertEquals("jane.smith@example.com", response.getBody().getEmailId());
    }

    @Test
    void testUpdateEmployee_NotFound() {
        Employee updatedDetails = new Employee();
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(com.ems.exception.ResourceNotFoundException.class, () -> {
            employeeController.updateEmployee(1L, updatedDetails);
        });
        assertTrue(exception.getMessage().contains("Employee not found for this id"));
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        employee.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);
        var response = employeeController.deleteEmployee(1L);
        assertNotNull(response);
        assertTrue(response.get("deleted"));
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(com.ems.exception.ResourceNotFoundException.class, () -> {
            employeeController.deleteEmployee(1L);
        });
        assertTrue(exception.getMessage().contains("Employee not found for this id"));
    }

    @Test
    void testGetAllEmployees() {
        Employee emp1 = new Employee();
        emp1.setFirstName("Alice");
        emp1.setLastName("Wonderland");
        emp1.setEmailId("alice@example.com");
        Employee emp2 = new Employee();
        emp2.setFirstName("Bob");
        emp2.setLastName("Builder");
        emp2.setEmailId("bob@example.com");
        java.util.List<Employee> employees = java.util.Arrays.asList(emp1, emp2);
        when(employeeRepository.findAll()).thenReturn(employees);
        java.util.List<Employee> result = employeeController.getAllEmployees();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        assertEquals("Bob", result.get(1).getFirstName());
        verify(employeeRepository, times(1)).findAll();
    }
} 