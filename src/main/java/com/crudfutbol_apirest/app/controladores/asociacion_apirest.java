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

import com.crudfutbol_apirest.app.entidades.asociacion;
import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.repositorios.asociacion_repositorio;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asociaciones")
public class asociacion_apirest {

	private static final String SECUENCIA_ASOCIACION = "asociacion_sequence";

	@Autowired
	private asociacion_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping
	public List<asociacion> listar() {
		return repositorio.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<asociacion> obtenerPorId(@PathVariable Long id) {
		return repositorio.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<asociacion> crear(@Valid @RequestBody asociacion nueva) {
		nueva.setId(secuenciaService.generarSecuencia(SECUENCIA_ASOCIACION));
		return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.save(nueva));
	}

	@PutMapping("/{id}")
	public ResponseEntity<asociacion> actualizar(@PathVariable Long id, @Valid @RequestBody asociacion datos) {
		if (!repositorio.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		datos.setId(id);
		return ResponseEntity.ok(repositorio.save(datos));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<asociacion> a = repositorio.findById(id);
		if (a.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<club> clubes = clubRepositorio.findByAsociacionId(id);
		if (!clubes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje",
					club_repositorio.mensajeRestriccion("la asociacion " + a.get().getNombre(), clubes)));
		}
		repositorio.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
