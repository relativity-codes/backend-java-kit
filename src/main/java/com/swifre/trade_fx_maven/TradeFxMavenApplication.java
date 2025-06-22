package com.swifre.trade_fx_maven;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.modulith.runtime.ModulithRuntimeHints;

import com.swifre.trade_fx_maven.user.entity.User;
import com.swifre.trade_fx_maven.user.enums.UserType;
import com.swifre.trade_fx_maven.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ImportRuntimeHints(ModulithRuntimeHints.class)
public class TradeFxMavenApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeFxMavenApplication.class, args);
	}

	/**
	 * CommandLineRunner bean to populate some initial data into the H2 database
	 * when the application starts. This is useful for testing.
	 * 
	 * @param repository The UserRepository injected by Spring.
	 * @return A CommandLineRunner instance.
	 */
	@Bean
	public CommandLineRunner demoData(UserRepository repository, PasswordEncoder passwordEncoder) {
		return (args) -> {
			// Save a few users (now including password)
			User user1 = new User("relativity-codes", "ukweheverest@gmail.com", passwordEncoder.encode("password123!"),
					UserType.SUPER_ADMIN);

			System.out.println("Checking if user with username " + user1.getUsername() + " already exists...");
			if (repository.findByUsername(user1.getUsername()) != null) {
				System.out.println("User with username " + user1.getUsername() + " already exists.");
			} else {
				System.out.println("Saving user: " + user1);
				repository.save(user1);
			}

			// Fetch all users
			System.out.println("Super Admin Found:");
			System.out.println("---------------------------------------------");
			User user = repository.findByUsername(user1.getUsername()).orElse(null);
			System.out.println(user);
			System.out.println();

			// Fetch users by username
			System.out.println("User found with findByUsername('relativity-codes'):");
			System.out.println("---------------------------------------------");
			User relativity = repository.findByUsername("relativity-codes").orElse(null);
			System.out.println(relativity);
			System.out.println();
		};
	}
}
