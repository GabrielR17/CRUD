package com.crudfutbol_apirest.app;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.crudfutbol_apirest.app.entidades.asociacion;
import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.entidades.competicion;
import com.crudfutbol_apirest.app.entidades.entrenador;
import com.crudfutbol_apirest.app.entidades.jugador;
import com.crudfutbol_apirest.app.repositorios.asociacion_repositorio;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.competicion_repositorio;
import com.crudfutbol_apirest.app.repositorios.entrenador_repositorio;
import com.crudfutbol_apirest.app.repositorios.jugador_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

@SpringBootApplication
public class CrudFutbolApirestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudFutbolApirestApplication.class, args);
	}

	// Datos de ejemplo: solo se insertan si todas las colecciones estan vacias
	@Bean
	CommandLineRunner datosEjemplo(club_repositorio clubes, asociacion_repositorio asociaciones,
			competicion_repositorio competiciones, entrenador_repositorio entrenadores,
			jugador_repositorio jugadores, SequenceGeneratorService secuencias) {
		return args -> {
			if (clubes.count() > 0 || asociaciones.count() > 0 || competiciones.count() > 0
					|| entrenadores.count() > 0 || jugadores.count() > 0) {
				return;
			}

			// 1. Asociacion compartida por ambos clubes
			asociacion fcf = new asociacion("FCF (Federacion Colombiana de Futbol)", "Colombia", "Ramon Jesurun");
			fcf.setId(secuencias.generarSecuencia("asociacion_sequence"));
			asociaciones.save(fcf);

			// 2. Competiciones
			List<competicion> listaCompeticiones = List.of(
					new competicion("Superliga", 500000, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 5)),
					new competicion("Copa Postobon", 1500000, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 11, 30)),
					new competicion("Copa Libertadores", 20000000, LocalDate.of(2026, 2, 10), LocalDate.of(2026, 11, 28)));
			for (competicion comp : listaCompeticiones) {
				comp.setId(secuencias.generarSecuencia("competicion_sequence"));
				competiciones.save(comp);
			}

			// 3. Entrenadores
			entrenador gamero = guardarEntrenador(entrenadores, secuencias, new entrenador("Alberto", "Gamero", 61, "Colombiana"));
			entrenador repetto = guardarEntrenador(entrenadores, secuencias, new entrenador("Pablo", "Repetto", 52, "Uruguaya"));

			// 4. Jugadores
			List<jugador> jugadoresMillonarios = List.of(
					guardarJugador(jugadores, secuencias, new jugador("Alvaro", "Montero", 1, "Portero")),
					guardarJugador(jugadores, secuencias, new jugador("Daniel", "Ruiz", 8, "Mediocampista")),
					guardarJugador(jugadores, secuencias, new jugador("Leonardo", "Castro", 9, "Delantero")));
			List<jugador> jugadoresSantaFe = List.of(
					guardarJugador(jugadores, secuencias, new jugador("Andres", "Mosquera", 1, "Portero")),
					guardarJugador(jugadores, secuencias, new jugador("Hugo", "Rodallega", 9, "Delantero")),
					guardarJugador(jugadores, secuencias, new jugador("Fabian", "Gonzalez", 4, "Defensa")));

			// 5. Clubes referenciando los registros anteriores
			club millonarios = new club("Millonarios");
			millonarios.setEntrenador(gamero);
			millonarios.setAsociacion(fcf);
			millonarios.setJugadores(jugadoresMillonarios);
			millonarios.setCompeticiones(listaCompeticiones);
			millonarios.setId(secuencias.generarSecuencia("club_sequence"));
			clubes.save(millonarios);

			club santaFe = new club("Santa Fe");
			santaFe.setEntrenador(repetto);
			santaFe.setAsociacion(fcf);
			santaFe.setJugadores(jugadoresSantaFe);
			santaFe.setCompeticiones(listaCompeticiones);
			santaFe.setId(secuencias.generarSecuencia("club_sequence"));
			clubes.save(santaFe);

			// Registros sin asignar, para probar la asignacion desde el formulario del club
			guardarEntrenador(entrenadores, secuencias, new entrenador("Hernan", "Torres", 55, "Colombiana"));
			guardarJugador(jugadores, secuencias, new jugador("Juan", "Perez", 10, "Mediocampista"));
			guardarJugador(jugadores, secuencias, new jugador("Carlos", "Rodriguez", 7, "Delantero"));
		};
	}

	private entrenador guardarEntrenador(entrenador_repositorio repositorio, SequenceGeneratorService secuencias,
			entrenador e) {
		e.setId(secuencias.generarSecuencia("entrenador_sequence"));
		return repositorio.save(e);
	}

	private jugador guardarJugador(jugador_repositorio repositorio, SequenceGeneratorService secuencias, jugador j) {
		j.setId(secuencias.generarSecuencia("jugador_sequence"));
		return repositorio.save(j);
	}

}
