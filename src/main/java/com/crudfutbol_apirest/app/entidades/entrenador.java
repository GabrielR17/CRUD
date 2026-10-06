package com.crudfutbol_apirest.app.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// En JPA el club tendria @OneToOne hacia el entrenador. Aqui es un documento independiente y el club
// guarda solo su id con @DocumentReference; @DocumentReference(lazy = true) equivaldria a FetchType.LAZY.
// Como MongoDB no tiene FK ni UNIQUE en la relacion, que este en un solo club se valida en los controladores.
@Document(collection = "entrenadores")
public class entrenador {

	@Id
	private Long id;

	@NotBlank(message = "El nombre del entrenador es obligatorio")
	private String nombre;

	@NotBlank(message = "El apellido del entrenador es obligatorio")
	private String apellido;

	@Min(value = 18, message = "La edad minima es 18")
	@Max(value = 99, message = "La edad maxima es 99")
	private int edad;

	@NotBlank(message = "La nacionalidad es obligatoria")
	private String nacionalidad;

	// Jackson usa este constructor, asi el club puede referenciarlo en JSON solo con {"id": n}
	@JsonCreator
	public entrenador() {
	}

	public entrenador(String nombre, String apellido, int edad, String nacionalidad) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
		this.nacionalidad = nacionalidad;
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

}
