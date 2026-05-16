package com.trabajo.api.pattern.microservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * PATRON DE MICROSERVICIOS: DTO (Data Transfer Object)
 * ============================================================================
 *
 * PROPOSITO:
 * Los DTOs transportan datos entre procesos/capas, reduciendo el numero de
 * llamadas y desacoplando la representacion interna de la externa.
 *
 * APLICACION EN ESTE PROYECTO:
 * ApiResponse estandariza todas las respuestas de la API REST, proporcionando:
 * - Estructura consistente para exito y error
 * - Metadata (timestamp, version)
 * - Mensajes amigables para el cliente
 *
 * BENEFICIOS:
 * 1. Respuestas consistentes en toda la API
 * 2. Facilita el manejo de errores en el frontend
 * 3. Incluye metadata util para debugging
 * 4. Desacopla la logica de negocio de la presentacion
 *
 * PRINCIPIO SOLID APLICADO:
 * - Single Responsibility (S): Solo representa respuestas de API
 * - Interface Segregation (I): Metodos estaticos especificos
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private String errorCode;
    private LocalDateTime timestamp;
    private String apiVersion;

    // Constructor privado - usar metodos estaticos
    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.apiVersion = "1.0.0";
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Crea una respuesta exitosa con datos.
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = "Operacion exitosa";
        response.data = data;
        return response;
    }

    /**
     * Crea una respuesta exitosa con mensaje personalizado.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    /**
     * Crea una respuesta de error.
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = message;
        response.errorCode = "GENERAL_ERROR";
        return response;
    }

    /**
     * Crea una respuesta de error con codigo.
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = message;
        response.errorCode = errorCode;
        return response;
    }

    /**
     * Crea una respuesta de recurso no encontrado.
     */
    public static <T> ApiResponse<T> notFound(String recurso) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = recurso + " no encontrado";
        response.errorCode = "NOT_FOUND";
        return response;
    }

    /**
     * Crea una respuesta de validacion fallida.
     */
    public static <T> ApiResponse<T> validationError(String mensaje) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = mensaje;
        response.errorCode = "VALIDATION_ERROR";
        return response;
    }

    // ==================== GETTERS Y SETTERS ====================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
