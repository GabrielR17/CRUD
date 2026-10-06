package com.crudfutbol_apirest.app.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crudfutbol_apirest.app.entidades.asociacion;

public interface asociacion_repositorio extends MongoRepository<asociacion, Long> {

}
