package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import javax.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		try {
			User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Can not found ID: " + id));
			return ResponseEntity.ok(user);
		} catch (EntityNotFoundException e) {
			logger.error("Can not found ID: " + id);
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error("Can not found username: " + username);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			logger.info("Username is already exists.", createUserRequest.getUsername());
			return ResponseEntity.badRequest().body("Username is already exists.");
		}
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if (createUserRequest.getPassword().length() < 7 || createUserRequest.getPassword() == null ||!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.debug("Password is less than 7 characters or password and confirm password do not match.", createUserRequest.getUsername());
			return ResponseEntity.badRequest().body("Password is less than 7 characters or password and confirm password do not match. Unable to create account.");
		}
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);
		logger.info("Successfully to create account.");
		return ResponseEntity.ok(user);
	}
	
}
