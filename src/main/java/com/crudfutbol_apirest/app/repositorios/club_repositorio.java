package com.crudfutbol_apirest.app.repositorios;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.crudfutbol_apirest.app.entidades.club;

public interface club_repositorio extends MongoRepository<club, Long> {

	// Con @DocumentReference el club guarda solo el id de cada referencia, asi que se filtra
	// directamente por ese valor (en los arrays, Mongo busca el id entre los elementos).

	@Query("{ 'entrenador': ?0 }")
	List<club> findByEntrenadorId(Long entrenadorId);

	@Query("{ 'asociacion': ?0 }")
	List<club> findByAsociacionId(Long asociacionId);

	@Query("{ 'jugadores': ?0 }")
	List<club> findByJugadorId(Long jugadorId);

	@Query("{ 'competiciones': ?0 }")
	List<club> findByCompeticionId(Long competicionId);

	// Reglas uno a uno y uno a muchos: club distinto de clubId que ya tiene ese entrenador o jugador
	default Optional<club> otroClubConEntrenador(Long entrenadorId, Long clubId) {
		return findByEntrenadorId(entrenadorId).stream().filter(c -> !Objects.equals(c.getId(), clubId)).findFirst();
	}

	default Optional<club> otroClubConJugador(Long jugadorId, Long clubId) {
		return findByJugadorId(jugadorId).stream().filter(c -> !Objects.equals(c.getId(), clubId)).findFirst();
	}

	// Mensaje de la restriccion al eliminar (simula la foreign key)
	static String mensajeRestriccion(String descripcion, List<club> clubes) {
		String nombres = clubes.stream().map(club::getNombre).collect(Collectors.joining(", "));
		return clubes.size() == 1
				? "No se puede eliminar " + descripcion + ": pertenece al club " + nombres
						+ ". Primero debe quitarse de ese club."
				: "No se puede eliminar " + descripcion + ": pertenece a los clubes " + nombres
						+ ". Primero debe quitarse de esos clubes.";
	}

}
