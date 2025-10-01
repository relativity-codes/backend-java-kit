package com.standard.commerce_maven;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.standard.commerce_maven.auth.AuthModule;
import com.standard.commerce_maven.user.UserModule;
import com.standard.commerce_maven.user.entity.User;
import com.standard.commerce_maven.user.enums.UserType;
import com.standard.commerce_maven.user.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@EnableWebMvc
@Import({UserModule.class, AuthModule.class})
public class CommerceMavenApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommerceMavenApplication.class, args);
	}

	/**
	 * CommandLineRunner bean to populate some initial data into the H2 database
	 * when the application starts. This is useful for testing.
	 * 
	 * @param repository The UserRepository injected by Spring.
	 * @return A CommandLineRunner instance.
	 */
	@SuppressWarnings("unused")
	@Bean
	public CommandLineRunner demoData(UserRepository repository, PasswordEncoder passwordEncoder) {
		return (args) -> {
			// Save a few users (now including password)
			User user1 = new User("relativity-codes", "ukweheverest@gmail.com", passwordEncoder.encode("password123!"),
					UserType.SUPER_ADMIN);

			System.out.println("\n\n\nChecking if user with username " + user1.getUsername() + " already exists in the database...");
			if (repository.findByUsername(user1.getUsername()).isPresent()) {
				System.out.println("User with username " + user1.getUsername() + " already exists.");
			} else {
				System.out.println("Saving user: " + user1.toString());
				repository.save(user1);
			}

			// Fetch all users
			System.out.println("\n\n\nFinding Super Admin:");
			System.out.println("---------------------------------------------");
			User user = repository.findByUsername(user1.getUsername()).orElse(null);
			if (user != null) {
				System.out.println(user.toString());
			} else {
				System.out.println("User not found");
			}

			// Fetch users by username
			System.out.println("\n\n\nFinding User with username 'relativity-codes':");
			System.out.println("---------------------------------------------");
			User relativity = repository.findByUsername("relativity-codes").orElse(null);
			if (relativity != null) {
				System.out.println(relativity.toString());
			} else {
				System.out.println("User not found");
			}
		};
	}
}
