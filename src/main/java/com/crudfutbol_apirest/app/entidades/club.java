package com.crudfutbol_apirest.app.entidades;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.NotBlank;

// Documento raiz (en JPA seria @Entity). Es el dueno de las relaciones: guarda solo los ids de las
// demas entidades con @DocumentReference, y ellas no guardan referencia al club (relaciones unidireccionales).
// @DocumentReference(lazy = true) seria el equivalente a FetchType.LAZY; aqui se carga en EAGER para que
// la API REST pueda serializar el club completo a JSON.
@Document(collection = "clubesfutbol")
public class club {

	@Id
	private Long id;

	@NotBlank(message = "El nombre del club es obligatorio")
	private String nombre;

	// Equivale a @OneToOne: un entrenador solo puede estar en un club (se valida en los controladores)
	@DocumentReference
	private entrenador entrenador;

	// Equivale a @ManyToOne: varios clubes comparten la misma asociacion
	@DocumentReference
	private asociacion asociacion;

	// Equivale a @OneToMany: un jugador solo puede pertenecer a un club (se valida en los controladores)
	@DocumentReference
	private List<jugador> jugadores = new ArrayList<>();

	// Equivale a @ManyToMany: un club juega varias competiciones y una competicion tiene varios clubes
	@DocumentReference
	private List<competicion> competiciones = new ArrayList<>();

	public club() {
	}

	public club(String nombre) {
		this.nombre = nombre;
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

	public entrenador getEntrenador() {
		return entrenador;
	}

	public void setEntrenador(entrenador entrenador) {
		this.entrenador = entrenador;
	}

	public asociacion getAsociacion() {
		return asociacion;
	}

	public void setAsociacion(asociacion asociacion) {
		this.asociacion = asociacion;
	}

	public List<jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<jugador> jugadores) {
		this.jugadores = jugadores != null ? new ArrayList<>(jugadores) : new ArrayList<>();
	}

	public List<competicion> getCompeticiones() {
		return competiciones;
	}

	public void setCompeticiones(List<competicion> competiciones) {
		this.competiciones = competiciones != null ? new ArrayList<>(competiciones) : new ArrayList<>();
	}

}
