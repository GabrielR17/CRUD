package com.crudfutbol_apirest.app.controladores;

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

import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.entidades.jugador;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.jugador_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jugadores")
public class jugador_apirest {

	private static final String SECUENCIA_JUGADOR = "jugador_sequence";

	@Autowired
	private jugador_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping
	public List<jugador> listar() {
		return repositorio.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<jugador> obtenerPorId(@PathVariable Long id) {
		return repositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<jugador> crear(@Valid @RequestBody jugador nuevo) {
		nuevo.setId(secuenciaService.generarSecuencia(SECUENCIA_JUGADOR));
		return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.save(nuevo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<jugador> actualizar(@PathVariable Long id, @Valid @RequestBody jugador datos) {
		if (!repositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		datos.setId(id);
		return ResponseEntity.ok(repositorio.save(datos));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<jugador> j = repositorio.findById(id);
		if (j.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<club> clubes = clubRepositorio.findByJugadorId(id);
		if (!clubes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", club_repositorio
					.mensajeRestriccion("el jugador " + j.get().getNombre() + " " + j.get().getApellido(), clubes)));
		}
		repositorio.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
