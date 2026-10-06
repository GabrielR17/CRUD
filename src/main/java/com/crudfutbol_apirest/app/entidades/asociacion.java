package com.crudfutbol_apirest.app.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotBlank;

// En JPA el club tendria @ManyToOne hacia la asociacion (muchos clubes -> una asociacion). Aqui es un
// documento independiente y cada club guarda solo su id con @DocumentReference, asi no se duplican sus datos.
// @DocumentReference(lazy = true) equivaldria a FetchType.LAZY.
@Document(collection = "asociaciones")
public class asociacion {

	@Id
	private Long id;

	@NotBlank(message = "El nombre de la asociacion es obligatorio")
	private String nombre;

	@NotBlank(message = "El pais es obligatorio")
	private String pais;

	@NotBlank(message = "El presidente es obligatorio")
	private String presidente;

	// Jackson usa este constructor, asi el club puede referenciarla en JSON solo con {"id": n}
	@JsonCreator
	public asociacion() {
	}

	public asociacion(String nombre, String pais, String presidente) {
		this.nombre = nombre;
		this.pais = pais;
		this.presidente = presidente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getPresidente() {
		return presidente;
	}

	public void setPresidente(String presidente) {
		this.presidente = presidente;
	}

}
