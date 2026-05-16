package com.trabajo.api.repository;

import com.trabajo.api.model.PlazoFijo;
import com.trabajo.api.model.EstadoPlazoFijo;
import com.trabajo.api.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlazoFijoRepository extends JpaRepository<PlazoFijo, Long> {

    List<PlazoFijo> findByCuenta(Cuenta cuenta);

    List<PlazoFijo> findByCuentaNumeroCuenta(String numeroCuenta);

    List<PlazoFijo> findByEstado(EstadoPlazoFijo estado);

    @Query("SELECT SUM(p.montoInicial) FROM PlazoFijo p WHERE p.estado = 'ACTIVO'")
    Double sumMontoActivoTotal();

    @Query("SELECT SUM(p.interesGenerado) FROM PlazoFijo p WHERE p.estado = 'ACTIVO'")
    Double sumInteresActivoTotal();

    @Query("SELECT COUNT(p) FROM PlazoFijo p WHERE p.estado = 'ACTIVO'")
    Long countActivos();
}
