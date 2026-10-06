package com.crudfutbol_apirest.app.entidades;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// En JPA seria @ManyToMany (clubes <-> competiciones) con una tabla intermedia. Aqui es un documento
// independiente y cada club guarda la lista de ids con @DocumentReference, que hace el papel de esa tabla.
// @DocumentReference(lazy = true) equivaldria a FetchType.LAZY.
@Document(collection = "competiciones")
public class competicion {

	@Id
	private Long id;

	@NotBlank(message = "El nombre de la competicion es obligatorio")
	private String nombre;

	@Min(value = 0, message = "El monto del premio no puede ser negativo")
	private int montoPremio;

	@NotNull(message = "La fecha de inicio es obligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaInicio;

	@NotNull(message = "La fecha de fin es obligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaFin;

	// Jackson usa este constructor, asi el club puede referenciarla en JSON solo con {"id": n}
	@JsonCreator
	public competicion() {
	}

	public competicion(String nombre, int montoPremio, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.montoPremio = montoPremio;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	@JsonIgnore
	@AssertTrue(message = "La fecha de fin no puede ser anterior a la fecha de inicio")
	public boolean isFechasValidas() {
		return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
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

	public int getMontoPremio() {
		return montoPremio;
	}

	public void setMontoPremio(int montoPremio) {
		this.montoPremio = montoPremio;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

}
