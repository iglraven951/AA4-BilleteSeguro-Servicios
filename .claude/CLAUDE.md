# Project: Sistema Bancario con Spring Data JPA

## Overview
Sistema de gestion bancaria desarrollado con Spring Boot y Spring Data JPA.
Implementa operaciones CRUD de cuentas, depositos, retiros y transferencias
con persistencia en base de datos H2.

## Tech Stack
- Language: Java 17
- Framework: Spring Boot 3.2.4
- Persistencia: Spring Data JPA + Hibernate
- Database: H2 (embebida, persistente en archivo)
- Build Tool: Maven

## Quick Commands
- Build: `mvn clean compile`
- Run: `mvn spring-boot:run`
- Test: `mvn test`
- Package: `mvn clean package`

## Project Structure (MVC + JPA Pattern)
```
src/main/java/com/trabajo/api/
├── ApiRestApplication.java    # Main class
├── model/                     # Entidades JPA
│   ├── Cuenta.java            # @Entity principal
│   ├── Movimiento.java        # @Entity relacionada (@ManyToOne)
│   ├── TipoCuenta.java        # Enum
│   ├── EstadoCuenta.java      # Enum
│   ├── TipoMovimiento.java    # Enum
│   └── TransferenciaRequest.java  # DTO
├── repository/                # Interfaces JPA Repository
│   ├── CuentaRepository.java  # extends JpaRepository
│   └── MovimientoRepository.java
├── service/                   # Logica de negocio
│   └── BancoService.java      # @Service @Transactional
└── controller/                # REST API
    ├── BancoController.java   # API endpoints
    └── VistaController.java   # Vistas Thymeleaf
```

## Database Relationship
```
CUENTA (1) -------- (*) MOVIMIENTO
  @OneToMany           @ManyToOne
  mappedBy="cuenta"    @JoinColumn(name="cuenta_id")
```

## REST Endpoints
| Method | URL                           | Description           |
|--------|-------------------------------|-----------------------|
| GET    | /api/cuentas                  | Listar cuentas        |
| GET    | /api/cuentas/{id}             | Obtener por ID        |
| POST   | /api/cuentas                  | Crear cuenta          |
| PUT    | /api/cuentas/{id}             | Actualizar cuenta     |
| POST   | /api/operaciones/deposito     | Depositar             |
| POST   | /api/operaciones/retiro       | Retirar               |
| POST   | /api/operaciones/transferencia| Transferir            |
| GET    | /api/movimientos/{numero}     | Historial movimientos |

## H2 Console
- URL: http://localhost:8089/h2-console
- JDBC URL: jdbc:h2:file:./data/bancodb
- User: sa
- Password: (vacio)

## Current Status
Proyecto completado con implementacion de Spring Data JPA:
- Entidades con relaciones @OneToMany/@ManyToOne
- Repositories con JpaRepository
- Configuracion completa en application.properties
- Anotaciones JPA: @Entity, @Table, @Column, @Enumerated, @PrePersist, etc.

## Important Notes
- Los datos se persisten en ./data/bancodb
- La relacion Cuenta-Movimiento es bidireccional
- Usar @Transactional en el servicio para operaciones atomicas
