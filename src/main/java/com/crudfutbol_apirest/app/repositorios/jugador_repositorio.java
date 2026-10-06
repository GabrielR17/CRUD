package com.crudfutbol_apirest.app.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crudfutbol_apirest.app.entidades.jugador;

public interface jugador_repositorio extends MongoRepository<jugador, Long> {

}
