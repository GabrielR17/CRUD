package com.crudfutbol_apirest.app.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clubes")
public class club_apirest {

	private static final String SECUENCIA_CLUB = "club_sequence";

	@Autowired
	private club_repositorio repositorio;

	@Autowired
	private entrenador_repositorio entrenadorRepositorio;

	@Autowired
	private asociacion_repositorio asociacionRepositorio;

	@Autowired
	private jugador_repositorio jugadorRepositorio;

	@Autowired
	private competicion_repositorio competicionRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping
	public List<club> listar() {
		return repositorio.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<club> obtenerPorId(@PathVariable Long id) {
		return repositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody club nuevoClub) {
		ResponseEntity<?> error = resolverReferencias(nuevoClub, null);
		if (error != null) {
			return error;
		}
		nuevoClub.setId(secuenciaService.generarSecuencia(SECUENCIA_CLUB));
		return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.save(nuevoClub));
	}

	// Reemplaza el club completo: lo que no venga en el JSON queda sin asignar
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody club datosClub) {
		if (!repositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		ResponseEntity<?> error = resolverReferencias(datosClub, id);
		if (error != null) {
			return error;
		}
		datosClub.setId(id);
		return ResponseEntity.ok(repositorio.save(datosClub));
	}

	// Eliminar el club no elimina sus entidades relacionadas: quedan libres
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (!repositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repositorio.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// El JSON trae las referencias solo con su id: se cargan los registros reales, verificando que existan
	// (404) y que se cumplan las reglas uno a uno y uno a muchos (409). Devuelve null si todo es valido.
	private ResponseEntity<?> resolverReferencias(club datos, Long clubId) {
		if (datos.getEntrenador() != null) {
			Long entrenadorId = datos.getEntrenador().getId();
			Optional<entrenador> e = entrenadorId != null ? entrenadorRepositorio.findById(entrenadorId) : Optional.empty();
			if (e.isEmpty()) {
				return respuesta(HttpStatus.NOT_FOUND, "No existe el entrenador con id " + entrenadorId);
			}
			Optional<club> otro = repositorio.otroClubConEntrenador(entrenadorId, clubId);
			if (otro.isPresent()) {
				return respuesta(HttpStatus.CONFLICT, "El entrenador con id " + entrenadorId
						+ " ya pertenece al club " + otro.get().getNombre());
			}
			datos.setEntrenador(e.get());
		}

		if (datos.getAsociacion() != null) {
			Long asociacionId = datos.getAsociacion().getId();
			Optional<asociacion> a = asociacionId != null ? asociacionRepositorio.findById(asociacionId) : Optional.empty();
			if (a.isEmpty()) {
				return respuesta(HttpStatus.NOT_FOUND, "No existe la asociacion con id " + asociacionId);
			}
			datos.setAsociacion(a.get());
		}

		List<jugador> jugadores = new ArrayList<>();
		for (jugador ref : datos.getJugadores()) {
			Optional<jugador> j = ref.getId() != null ? jugadorRepositorio.findById(ref.getId()) : Optional.empty();
			if (j.isEmpty()) {
				return respuesta(HttpStatus.NOT_FOUND, "No existe el jugador con id " + ref.getId());
			}
			Optional<club> otro = repositorio.otroClubConJugador(ref.getId(), clubId);
			if (otro.isPresent()) {
				return respuesta(HttpStatus.CONFLICT, "El jugador con id " + ref.getId()
						+ " ya pertenece al club " + otro.get().getNombre());
			}
			jugadores.add(j.get());
		}
		datos.setJugadores(jugadores);

		List<competicion> competiciones = new ArrayList<>();
		for (competicion ref : datos.getCompeticiones()) {
			Optional<competicion> comp = ref.getId() != null ? competicionRepositorio.findById(ref.getId()) : Optional.empty();
			if (comp.isEmpty()) {
				return respuesta(HttpStatus.NOT_FOUND, "No existe la competicion con id " + ref.getId());
			}
			competiciones.add(comp.get());
		}
		datos.setCompeticiones(competiciones);
		return null;
	}

	private ResponseEntity<Map<String, String>> respuesta(HttpStatus estado, String mensaje) {
		return ResponseEntity.status(estado).body(Map.of("mensaje", mensaje));
	}

}
