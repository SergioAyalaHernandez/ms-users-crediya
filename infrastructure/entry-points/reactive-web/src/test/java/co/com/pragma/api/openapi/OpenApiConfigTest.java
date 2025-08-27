package co.com.pragma.api.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

  @Test
  void testCustomOpenAPI() {
    // Arrange
    OpenApiConfig config = new OpenApiConfig();

    // Act
    OpenAPI openAPI = config.customOpenAPI();

    // Assert
    assertNotNull(openAPI, "El objeto OpenAPI no debería ser nulo");
    assertNotNull(openAPI.getInfo(), "La información del API no debería ser nula");

    Info info = openAPI.getInfo();
    assertEquals("API CrediYa", info.getTitle(), "El título debe ser 'API CrediYa'");
    assertEquals("1.0.0", info.getVersion(), "La versión debe ser '1.0.0'");
    assertEquals("Documentación de la API con OpenAPI - Arquitectura Limpia",
            info.getDescription(), "La descripción no coincide con la esperada");
  }
}