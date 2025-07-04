package com.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "employees")
@Data
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "first_name", nullable = false)
	@NotBlank(message = "First name is mandatory")
	private String firstName;
	@Column(name = "last_name", nullable = false)
	@NotBlank(message = "Last name is mandatory")
	private String lastName;
	@Column(name = "email_address", nullable = false, unique = true)
	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email should be valid")
	private String emailId;

}
