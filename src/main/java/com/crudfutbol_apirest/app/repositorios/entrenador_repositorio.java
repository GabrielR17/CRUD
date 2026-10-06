package com.crudfutbol_apirest.app.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crudfutbol_apirest.app.entidades.entrenador;

public interface entrenador_repositorio extends MongoRepository<entrenador, Long> {

}
