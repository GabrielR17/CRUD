package com.crudfutbol_apirest.app.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// En JPA el club tendria @OneToMany hacia sus jugadores. Aqui es un documento independiente y el club
// guarda la lista de ids con @DocumentReference; @DocumentReference(lazy = true) equivaldria a FetchType.LAZY.
// Que un jugador pertenezca a un solo club se valida en los controladores (MongoDB no tiene FK).
@Document(collection = "jugadores")
public class jugador {

	@Id
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	private String apellido;

	@Min(value = 1, message = "El numero minimo es 1")
	@Max(value = 99, message = "El numero maximo es 99")
	private int numero;

	@NotBlank(message = "La posicion es obligatoria")
	private String posicion;

	// Jackson usa este constructor, asi el club puede referenciarlo en JSON solo con {"id": n}
	@JsonCreator
	public jugador() {
	}

	public jugador(String nombre, String apellido, int numero, String posicion) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.numero = numero;
		this.posicion = posicion;
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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

}
