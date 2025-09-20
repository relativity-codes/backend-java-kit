package com.elwalkre.commerce_maven.migration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("migrate") // Only runs when this profile is active
public class FlywayMigrationRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://trade-fx-12556.j77.aws-us-east-1.cockroachlabs.cloud:26257/defaultdb?sslmode=verify-full&sslrootcert='cert/root.crt'",
                        "exrelativity", "lkyKYV_FFZ2Cp4NKhCYwYQ")
                .locations("classpath:db/migration") // Flyway default folder
                .load();
        System.out.println("🔄 Starting Flyway migration...");
        flyway.migrate();
        System.out.println("✅ Flyway migration executed.");
    }
}
