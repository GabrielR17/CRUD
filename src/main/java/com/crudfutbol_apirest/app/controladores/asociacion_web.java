package com.crudfutbol_apirest.app.controladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crudfutbol_apirest.app.entidades.asociacion;
import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.repositorios.asociacion_repositorio;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/asociaciones")
public class asociacion_web {

	private static final String SECUENCIA_ASOCIACION = "asociacion_sequence";

	@Autowired
	private asociacion_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping("/listar")
	public String listar(Model model) {
		Map<Long, String> clubesPorAsociacion = new HashMap<>();
		for (club c : clubRepositorio.findAll()) {
			if (c.getAsociacion() != null) {
				clubesPorAsociacion.merge(c.getAsociacion().getId(), c.getNombre(), (a, b) -> a + ", " + b);
			}
		}
		model.addAttribute("listaAsociaciones", repositorio.findAll());
		model.addAttribute("clubesPorAsociacion", clubesPorAsociacion);
		return "asociacion_listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("asociacion", new asociacion());
		return "asociacion_form";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("asociacion") asociacion formAsociacion, BindingResult resultado,
			RedirectAttributes flash) {
		if (resultado.hasErrors()) {
			return "asociacion_form";
		}
		if (formAsociacion.getId() == null) {
			formAsociacion.setId(secuenciaService.generarSecuencia(SECUENCIA_ASOCIACION));
		}
		repositorio.save(formAsociacion);
		flash.addFlashAttribute("mensajeExito", "Asociacion guardada correctamente");
		return "redirect:/asociaciones/listar";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<asociacion> a = repositorio.findById(id);
		if (a.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "La asociacion no existe");
			return "redirect:/asociaciones/listar";
		}
		model.addAttribute("asociacion", a.get());
		return "asociacion_form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		Optional<asociacion> a = repositorio.findById(id);
		if (a.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "La asociacion no existe");
			return "redirect:/asociaciones/listar";
		}
		List<club> clubes = clubRepositorio.findByAsociacionId(id);
		if (!clubes.isEmpty()) {
			flash.addFlashAttribute("mensajeError",
					club_repositorio.mensajeRestriccion("la asociacion " + a.get().getNombre(), clubes));
			return "redirect:/asociaciones/listar";
		}
		repositorio.deleteById(id);
		flash.addFlashAttribute("mensajeExito", "Asociacion eliminada correctamente");
		return "redirect:/asociaciones/listar";
	}

}
