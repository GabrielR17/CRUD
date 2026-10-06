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

import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.entidades.entrenador;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.entrenador_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/entrenadores")
public class entrenador_web {

	private static final String SECUENCIA_ENTRENADOR = "entrenador_sequence";

	@Autowired
	private entrenador_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping("/listar")
	public String listar(Model model) {
		Map<Long, String> clubesPorEntrenador = new HashMap<>();
		for (club c : clubRepositorio.findAll()) {
			if (c.getEntrenador() != null) {
				clubesPorEntrenador.merge(c.getEntrenador().getId(), c.getNombre(), (a, b) -> a + ", " + b);
			}
		}
		model.addAttribute("listaEntrenadores", repositorio.findAll());
		model.addAttribute("clubesPorEntrenador", clubesPorEntrenador);
		return "entrenador_listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("entrenador", new entrenador());
		return "entrenador_form";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("entrenador") entrenador formEntrenador, BindingResult resultado,
			RedirectAttributes flash) {
		if (resultado.hasErrors()) {
			return "entrenador_form";
		}
		if (formEntrenador.getId() == null) {
			formEntrenador.setId(secuenciaService.generarSecuencia(SECUENCIA_ENTRENADOR));
		}
		repositorio.save(formEntrenador);
		flash.addFlashAttribute("mensajeExito", "Entrenador guardado correctamente");
		return "redirect:/entrenadores/listar";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<entrenador> e = repositorio.findById(id);
		if (e.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El entrenador no existe");
			return "redirect:/entrenadores/listar";
		}
		model.addAttribute("entrenador", e.get());
		return "entrenador_form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		Optional<entrenador> e = repositorio.findById(id);
		if (e.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El entrenador no existe");
			return "redirect:/entrenadores/listar";
		}
		List<club> clubes = clubRepositorio.findByEntrenadorId(id);
		if (!clubes.isEmpty()) {
			flash.addFlashAttribute("mensajeError", club_repositorio.mensajeRestriccion(
					"el entrenador " + e.get().getNombre() + " " + e.get().getApellido(), clubes));
			return "redirect:/entrenadores/listar";
		}
		repositorio.deleteById(id);
		flash.addFlashAttribute("mensajeExito", "Entrenador eliminado correctamente");
		return "redirect:/entrenadores/listar";
	}

}
