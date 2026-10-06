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
import com.crudfutbol_apirest.app.entidades.competicion;
import com.crudfutbol_apirest.app.repositorios.club_repositorio;
import com.crudfutbol_apirest.app.repositorios.competicion_repositorio;
import com.crudfutbol_apirest.app.servicios.SequenceGeneratorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/competiciones")
public class competicion_web {

	private static final String SECUENCIA_COMPETICION = "competicion_sequence";

	@Autowired
	private competicion_repositorio repositorio;

	@Autowired
	private club_repositorio clubRepositorio;

	@Autowired
	private SequenceGeneratorService secuenciaService;

	@GetMapping("/listar")
	public String listar(Model model) {
		Map<Long, String> clubesPorCompeticion = new HashMap<>();
		for (club c : clubRepositorio.findAll()) {
			for (competicion comp : c.getCompeticiones()) {
				clubesPorCompeticion.merge(comp.getId(), c.getNombre(), (a, b) -> a + ", " + b);
			}
		}
		model.addAttribute("listaCompeticiones", repositorio.findAll());
		model.addAttribute("clubesPorCompeticion", clubesPorCompeticion);
		return "competicion_listar";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("competicion", new competicion());
		return "competicion_form";
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("competicion") competicion formCompeticion,
			BindingResult resultado, RedirectAttributes flash) {
		if (resultado.hasErrors()) {
			return "competicion_form";
		}
		if (formCompeticion.getId() == null) {
			formCompeticion.setId(secuenciaService.generarSecuencia(SECUENCIA_COMPETICION));
		}
		repositorio.save(formCompeticion);
		flash.addFlashAttribute("mensajeExito", "Competicion guardada correctamente");
		return "redirect:/competiciones/listar";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Optional<competicion> comp = repositorio.findById(id);
		if (comp.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "La competicion no existe");
			return "redirect:/competiciones/listar";
		}
		model.addAttribute("competicion", comp.get());
		return "competicion_form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		Optional<competicion> comp = repositorio.findById(id);
		if (comp.isEmpty()) {
			flash.addFlashAttribute("mensajeError", "La competicion no existe");
			return "redirect:/competiciones/listar";
		}
		List<club> clubes = clubRepositorio.findByCompeticionId(id);
		if (!clubes.isEmpty()) {
			flash.addFlashAttribute("mensajeError",
					club_repositorio.mensajeRestriccion("la competicion " + comp.get().getNombre(), clubes));
			return "redirect:/competiciones/listar";
		}
		repositorio.deleteById(id);
		flash.addFlashAttribute("mensajeExito", "Competicion eliminada correctamente");
		return "redirect:/competiciones/listar";
	}

}
