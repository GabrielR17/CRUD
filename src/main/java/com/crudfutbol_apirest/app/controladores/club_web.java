package com.crudfutbol_apirest.app.controladores;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crudfutbol_apirest.app.entidades.club;
import com.crudfutbol_apirest.app.entidades.competicion;
import com.crudfutbol_apirest.app.entidades.jugador;
import com.crudfutbol_apirest.app.repositorios.asociacion_repositorio;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.competicion_repositorio;
import com.crudfutbol_apirest.app.repositorios.entrenador_repositorio;
import com.crudfutbol_apirest.app.repositorios.jugador_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@Controller
public class club_web {

	private static final String SECUENCIA_CLUB = "club_sequence";

	@Autowired
	private club_repositorio repositorio;

	@Autowired
	private entrenador_repositorio entrenadorRepositorio;

	@Autowired
	private asociacion_repositorio asociacionRepositorio;

	@Autowired
	private jugador_repositorio jugadorRepositorio;

	@Autowired
	private competicion_repositorio competicionRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	// ---------- Club ----------

	@GetMapping("/")
	public String index(Model model) {
		club nuevo = new club();
		model.addAttribute("club", nuevo);
		prepararFormulario(nuevo, model);
		return "index";
	}

	@GetMapping("/listar")
	public String listar(Model model) {
		model.addAttribute("listaClubes", repositorio.findAll());
		return "listar";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("club") club formClub, BindingResult resultado,
			@RequestParam(required = false) Long entrenadorId,
			@RequestParam(required = false) Long asociacionId,
			@RequestParam(required = false) List<Long> jugadorIds,
			@RequestParam(required = false) List<Long> competicionIds,
			Model model, RedirectAttributes flash) {
		// El formulario envia solo ids: se cargan los registros existentes
		formClub.setEntrenador(entrenadorId != null ? entrenadorRepositorio.findById(entrenadorId).orElse(null) : null);
		formClub.setAsociacion(asociacionId != null ? asociacionRepositorio.findById(asociacionId).orElse(null) : null);
		formClub.setJugadores(jugadorIds != null ? jugadorRepositorio.findAllById(jugadorIds) : null);
		formClub.setCompeticiones(competicionIds != null ? competicionRepositorio.findAllById(competicionIds) : null);

		String error = validarReglas(formClub);
		if (resultado.hasErrors() || error != null) {
			model.addAttribute("mensajeError", error);
			prepararFormulario(formClub, model);
			return "index";
		}
		if (formClub.getId() == null) {
			formClub.setId(secuenciaService.generarSecuencia(SECUENCIA_CLUB));
		}
		repositorio.save(formClub);
		flash.addFlashAttribute("mensajeExito", "Club " + formClub.getNombre() + " guardado correctamente");
		return "redirect:/listar";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<club> c = repositorio.findById(id);
		if (c.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El club no existe");
			return "redirect:/listar";
		}
		model.addAttribute("club", c.get());
		prepararFormulario(c.get(), model);
		return "index";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		// Eliminar el club no elimina sus entidades relacionadas: quedan libres
		repositorio.deleteById(id);
		flash.addFlashAttribute("mensajeExito", "Club eliminado. Su entrenador, jugadores y competiciones quedan sin asignar");
		return "redirect:/listar";
	}

	@GetMapping("/detalle/{id}")
	public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<club> c = repositorio.findById(id);
		if (c.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "El club no existe");
			return "redirect:/listar";
		}
		model.addAttribute("club", c.get());
		return "detalle";
	}

	// ---------- Quitar del club (desasigna, no elimina el registro) ----------

	@GetMapping("/club/{id}/entrenador/quitar")
	public String quitarEntrenador(@PathVariable Long id, RedirectAttributes flash) {
		repositorio.findById(id).ifPresent(c -> {
			c.setEntrenador(null);
			repositorio.save(c);
			flash.addFlashAttribute("mensajeExito", "Entrenador quitado del club");
		});
		return "redirect:/detalle/" + id;
	}

	@GetMapping("/club/{id}/asociacion/quitar")
	public String quitarAsociacion(@PathVariable Long id, RedirectAttributes flash) {
		repositorio.findById(id).ifPresent(c -> {
			c.setAsociacion(null);
			repositorio.save(c);
			flash.addFlashAttribute("mensajeExito", "Asociacion quitada del club");
		});
		return "redirect:/detalle/" + id;
	}

	@GetMapping("/club/{id}/jugador/quitar/{jugadorId}")
	public String quitarJugador(@PathVariable Long id, @PathVariable Long jugadorId, RedirectAttributes flash) {
		repositorio.findById(id).ifPresent(c -> {
			c.getJugadores().removeIf(j -> jugadorId.equals(j.getId()));
			repositorio.save(c);
			flash.addFlashAttribute("mensajeExito", "Jugador quitado del club");
		});
		return "redirect:/detalle/" + id;
	}

	@GetMapping("/club/{id}/competicion/quitar/{competicionId}")
	public String quitarCompeticion(@PathVariable Long id, @PathVariable Long competicionId,
			RedirectAttributes flash) {
		repositorio.findById(id).ifPresent(c -> {
			c.getCompeticiones().removeIf(comp -> competicionId.equals(comp.getId()));
			repositorio.save(c);
			flash.addFlashAttribute("mensajeExito", "Competicion quitada del club");
		});
		return "redirect:/detalle/" + id;
	}

	// ---------- Auxiliares ----------

	// Reglas uno a uno (entrenador) y uno a muchos (jugadores); devuelve null si se cumplen
	private String validarReglas(club c) {
		if (c.getEntrenador() != null) {
			Optional<club> otro = repositorio.otroClubConEntrenador(c.getEntrenador().getId(), c.getId());
			if (otro.isPresent()) {
				return "El entrenador " + c.getEntrenador().getNombre() + " " + c.getEntrenador().getApellido()
						+ " ya pertenece al club " + otro.get().getNombre();
			}
		}
		for (jugador j : c.getJugadores()) {
			Optional<club> otro = repositorio.otroClubConJugador(j.getId(), c.getId());
			if (otro.isPresent()) {
				return "El jugador " + j.getNombre() + " " + j.getApellido() + " ya pertenece al club "
						+ otro.get().getNombre();
			}
		}
		return null;
	}

	// Opciones del formulario: entrenadores y jugadores libres (mas los del propio club) y todas las
	// asociaciones y competiciones, junto con los ids que ya estan seleccionados
	private void prepararFormulario(club c, Model model) {
		Set<Long> entrenadoresOcupados = new HashSet<>();
		Set<Long> jugadoresOcupados = new HashSet<>();
		for (club otro : repositorio.findAll()) {
			if (Objects.equals(otro.getId(), c.getId())) {
				continue;
			}
			if (otro.getEntrenador() != null) {
				entrenadoresOcupados.add(otro.getEntrenador().getId());
			}
			otro.getJugadores().forEach(j -> jugadoresOcupados.add(j.getId()));
		}
		model.addAttribute("entrenadores", entrenadorRepositorio.findAll().stream()
				.filter(e -> !entrenadoresOcupados.contains(e.getId())).toList());
		model.addAttribute("jugadores", jugadorRepositorio.findAll().stream()
				.filter(j -> !jugadoresOcupados.contains(j.getId())).toList());
		model.addAttribute("asociaciones", asociacionRepositorio.findAll());
		model.addAttribute("competiciones", competicionRepositorio.findAll());

		model.addAttribute("entrenadorSel", c.getEntrenador() != null ? c.getEntrenador().getId() : null);
		model.addAttribute("asociacionSel", c.getAsociacion() != null ? c.getAsociacion().getId() : null);
		model.addAttribute("jugadoresSel", c.getJugadores().stream().map(jugador::getId).toList());
		model.addAttribute("competicionesSel", c.getCompeticiones().stream().map(competicion::getId).toList());
	}

}
