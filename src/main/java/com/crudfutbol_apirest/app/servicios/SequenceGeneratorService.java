package com.crudfutbol_apirest.app.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.crudfutbol_apirest.app.entidades.DatabaseSequence;

@Service
public class SequenceGeneratorService {

	@Autowired
	private MongoOperations mongoOperations;

	public long generarSecuencia(String nombreSecuencia) {
		DatabaseSequence contador = mongoOperations.findAndModify(
				Query.query(Criteria.where("_id").is(nombreSecuencia)),
				new Update().inc("seq", 1),
				FindAndModifyOptions.options().returnNew(true).upsert(true),
				DatabaseSequence.class);
		return contador != null ? contador.getSeq() : 1;
	}

}
