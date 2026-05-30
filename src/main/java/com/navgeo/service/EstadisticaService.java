package com.navgeo.service;

import com.navgeo.entity.*;
import com.navgeo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final EstadisticaVisitaRepository   visitaRepo;
    private final EstadisticaRutaRepository     estadRutaRepo;
    private final EstadisticaParaderoRepository estadParaderoRepo;
    private final RutaRepository                rutaRepo;
    private final ParaderoRepository            paraderoRepo;

    /**
     * Registra una visita al sitio para el día actual.
     * Si ya existe un registro de hoy, incrementa el contador.
     * Si no existe, crea uno nuevo.
     */
    @Transactional
    public void registrarVisita() {
        LocalDate hoy = LocalDate.now();
        EstadisticaVisita registro = visitaRepo.findByFecha(hoy)
                .orElseGet(() -> {
                    EstadisticaVisita nuevo = new EstadisticaVisita();
                    nuevo.setFecha(hoy);
                    nuevo.setTotalVisitas(0);
                    return nuevo;
                });
        registro.setTotalVisitas(registro.getTotalVisitas() + 1);
        visitaRepo.save(registro);
    }

    /**
     * Registra una consulta a una ruta específica para el día actual.
     */
    @Transactional
    public void registrarConsultaRuta(Long rutaId) {
        LocalDate hoy = LocalDate.now();
        EstadisticaRuta registro = estadRutaRepo
                .findByRutaIdAndFecha(rutaId, hoy)
                .orElseGet(() -> {
                    EstadisticaRuta nuevo = new EstadisticaRuta();
                    nuevo.setRuta(rutaRepo.getReferenceById(rutaId));
                    nuevo.setFecha(hoy);
                    nuevo.setConsultas(0);
                    return nuevo;
                });
        registro.setConsultas(registro.getConsultas() + 1);
        estadRutaRepo.save(registro);
    }

    /**
     * Registra una consulta a un paradero específico para el día actual.
     */
    @Transactional
    public void registrarConsultaParadero(Long paraderoId) {
        LocalDate hoy = LocalDate.now();
        EstadisticaParadero registro = estadParaderoRepo
                .findByParaderoIdAndFecha(paraderoId, hoy)
                .orElseGet(() -> {
                    EstadisticaParadero nuevo = new EstadisticaParadero();
                    nuevo.setParadero(paraderoRepo.getReferenceById(paraderoId));
                    nuevo.setFecha(hoy);
                    nuevo.setConsultas(0);
                    return nuevo;
                });
        registro.setConsultas(registro.getConsultas() + 1);
        estadParaderoRepo.save(registro);
    }

    /**
     * Retorna un resumen de métricas para el dashboard del admin.
     */
    public Map<String, Object> obtenerResumenDashboard() {
        Map<String, Object> resumen = new HashMap<>();

        resumen.put("visitasHoy",
                visitaRepo.findByFecha(LocalDate.now())
                        .map(EstadisticaVisita::getTotalVisitas)
                        .orElse(0));

        resumen.put("rutasTop",
                estadRutaRepo.findTop4ByOrderByConsultasDesc());

        resumen.put("totalRutas",
                rutaRepo.count());

        resumen.put("totalParaderos",
                paraderoRepo.count());

        return resumen;
    }
}