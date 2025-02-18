package com.domnerka.workflow;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(String... args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("DB_URL", dotenv.get("DB_URL"));
    System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    System.setProperty("KEYCLOAK_BASE_URL", dotenv.get("KEYCLOAK_BASE_URL"));
    System.setProperty("KEYCLOAK_ISSUER_URI", dotenv.get("KEYCLOAK_ISSUER_URI"));
    System.setProperty("KEYCLOAK_CLIENT_SECRET", dotenv.get("KEYCLOAK_CLIENT_SECRET"));
    System.setProperty("CORS_ALLOWED_ORIGINS", dotenv.get("CORS_ALLOWED_ORIGINS"));

    SpringApplication.run(Application.class, args);
  }
}