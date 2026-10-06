package com.crudfutbol_apirest.app.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crudfutbol_apirest.app.entidades.competicion;

public interface competicion_repositorio extends MongoRepository<competicion, Long> {

}
