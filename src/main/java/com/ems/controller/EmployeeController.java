package com.ems.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.entity.Employee;
import com.ems.exception.InvalidInputException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.EmployeeRepository;

/**
 * REST controller for managing Employee resources.
 */
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	@Autowired
	private EmployeeRepository employeeRepository;

	@Value("${spring.profiles.active}")
		private String env;

	/**
	 * Retrieve all employees from the database.
	 * 
	 * @return list of all employees
	 */
	@GetMapping("/employees")
	public List<Employee> getAllEmployees() {
		logger.info("Fetching all employees");
		return employeeRepository.findAll();
	}

	/**
	 * Retrieve an employee by their ID.
	 * 
	 * @param employeeId the ID of the employee to retrieve
	 * @return the employee if found
	 * @throws ResourceNotFoundException if employee is not found
	 */
	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") Long employeeId)
			throws ResourceNotFoundException {
		logger.info("Fetching employee with id: {}", employeeId);
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id - " + employeeId));
		return ResponseEntity.ok().body(employee);
	}

	/**
	 * Create a new employee.
	 * 
	 * @param employee the employee to create
	 * @return the created employee
	 * @throws InvalidInputException if an employee with the same email already
	 *                               exists
	 */
	@PostMapping("/employees")
	public Employee createEmployee(@Valid @RequestBody Employee employee) throws InvalidInputException {
		logger.info("Creating employee with email: {}", employee.getEmailId());
		Employee existingEmployee = employeeRepository.findByEmailId(employee.getEmailId());
		if (existingEmployee != null) {
			logger.warn("Attempt to create duplicate employee with email: {}", employee.getEmailId());
			throw new InvalidInputException("Employee already exists with this email id :: " + employee.getEmailId());
		}
		return employeeRepository.save(employee);
	}

	/**
	 * Update an existing employee by ID.
	 * 
	 * @param employeeId      the ID of the employee to update
	 * @param employeeDetails the new employee details
	 * @return the updated employee
	 * @throws ResourceNotFoundException if employee is not found
	 */
	@PutMapping("/employees/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable(value = "id") Long employeeId,
			@Valid @RequestBody Employee employeeDetails) throws ResourceNotFoundException {
		logger.info("Updating employee with id: {}", employeeId);
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

		employee.setEmailId(employeeDetails.getEmailId());
		employee.setLastName(employeeDetails.getLastName());
		employee.setFirstName(employeeDetails.getFirstName());
		final Employee updatedEmployee = employeeRepository.save(employee);
		logger.info("Updated employee with id: {}", employeeId);
		return ResponseEntity.ok(updatedEmployee);
	}

	/**
	 * Delete an employee by ID.
	 * 
	 * @param employeeId the ID of the employee to delete
	 * @return a map indicating if the deletion was successful
	 * @throws ResourceNotFoundException if employee is not found
	 */
	@DeleteMapping("/employees/{id}")
	public Map<String, Boolean> deleteEmployee(@PathVariable(value = "id") Long employeeId)
			throws ResourceNotFoundException {
		logger.info("Deleting employee with id: {}", employeeId);
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

		employeeRepository.delete(employee);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		logger.info("Deleted employee with id: {}", employeeId);
		return response;
	}

	@GetMapping("/employees/env")
	public String getEnvironment() {

		return "Environment: " + env;
	}
}
