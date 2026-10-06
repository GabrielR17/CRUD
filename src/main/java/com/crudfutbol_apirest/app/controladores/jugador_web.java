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
import com.crudfutbol_apirest.app.entidades.jugador;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.jugador_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/jugadores")
public class jugador_web {

	private static final String SECUENCIA_JUGADOR = "jugador_sequence";

	@Autowired
	private jugador_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping("/listar")
	public String listar(Model model) {
		Map<Long, String> clubesPorJugador = new HashMap<>();
		for (club c : clubRepositorio.findAll()) {
			for (jugador j : c.getJugadores()) {
				clubesPorJugador.merge(j.getId(), c.getNombre(), (a, b) -> a + ", " + b);
			}
		}
		model.addAttribute("listaJugadores", repositorio.findAll());
		model.addAttribute("clubesPorJugador", clubesPorJugador);
		return "jugador_listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("jugador", new jugador());
		return "jugador_form";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("jugador") jugador formJugador, BindingResult resultado,
			RedirectAttributes flash) {
		if (resultado.hasErrors()) {
			return "jugador_form";
		}
		if (formJugador.getId() == null) {
			formJugador.setId(secuenciaService.generarSecuencia(SECUENCIA_JUGADOR));
		}
		repositorio.save(formJugador);
		flash.addFlashAttribute("mensajeExito", "Jugador guardado correctamente");
		return "redirect:/jugadores/listar";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<jugador> j = repositorio.findById(id);
		if (j.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El jugador no existe");
			return "redirect:/jugadores/listar";
		}
		model.addAttribute("jugador", j.get());
		return "jugador_form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		Optional<jugador> j = repositorio.findById(id);
		if (j.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El jugador no existe");
			return "redirect:/jugadores/listar";
		}
		List<club> clubes = clubRepositorio.findByJugadorId(id);
		if (!clubes.isEmpty()) {
			flash.addFlashAttribute("mensajeError", club_repositorio.mensajeRestriccion(
					"el jugador " + j.get().getNombre() + " " + j.get().getApellido(), clubes));
			return "redirect:/jugadores/listar";
		}
		repositorio.deleteById(id);
		flash.addFlashAttribute("mensajeExito", "Jugador eliminado correctamente");
		return "redirect:/jugadores/listar";
	}

}
