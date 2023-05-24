package it.prova.pokeronline.web.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.UtenteDTO;
import it.prova.pokeronline.dto.UtenteDTOAggiornamento;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.security.dto.UtenteInfoJWTResponseDTO;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.UtenteNotFoundException;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {

	@Autowired
	private UtenteService utenteService;

	// questa mi serve solo per capire se solo ADMIN vi ha accesso
	@GetMapping("/testSoloAdmin")
	public String test() {
		return "OK";
	}

	@GetMapping(value = "/userInfo")
	public ResponseEntity<UtenteInfoJWTResponseDTO> getUserInfo() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Utente utenteLoggato = utenteService.findByUsername(username);
		List<String> ruoli = utenteLoggato.getRuoli().stream().map(item -> item.getCodice())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new UtenteInfoJWTResponseDTO(utenteLoggato.getNome(), utenteLoggato.getCognome(),
				utenteLoggato.getUsername(), ruoli));
	}
	
	@GetMapping
	public List<UtenteDTO> getAll() {
		return UtenteDTO.createUtenteDTOListFromModelList(utenteService.listAllUtenti());
	}
	
	@GetMapping("/{id}")
	public UtenteDTO findById(@PathVariable(value = "id", required = true) long id) {
		Utente utente = utenteService.caricaSingoloUtenteConRuoli(id);

		if (utente == null)
			throw new UtenteNotFoundException("Utente not found con id: " + id);

		return UtenteDTO.buildUtenteDTOFromModel(utente);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UtenteDTO createNew(@Valid @RequestBody UtenteDTO utenteInput) {
		if (utenteInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		Utente utenteInserito = utenteService.inserisciNuovo(utenteInput.buildUtenteModel(true));
		return UtenteDTO.buildUtenteDTOFromModel(utenteInserito);
	}
	

	@GetMapping("/abilita/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void cambiaStato(@PathVariable(value = "id", required = true) long id) {
		
		Utente utente = utenteService.caricaSingoloUtenteConRuoli(id);

		if (utente == null)
			throw new UtenteNotFoundException("Utente not found con id: " + id);
		
		utenteService.changeUserAbilitation(id);
	}
	
	@PutMapping("/{id}")
	public UtenteDTO update(@Valid @RequestBody UtenteDTOAggiornamento utenteInput, @PathVariable(required = true) Long id) {
		Utente utente = utenteService.caricaSingoloUtente(id);

		if (utente == null)
			throw new UtenteNotFoundException("Utente not found con id: " + id);

		utenteInput.setId(id);
		Utente utenteAggiornato = utenteService.aggiorna(utenteInput.buildUtenteModel(true));
		return UtenteDTO.buildUtenteDTOFromModel(utenteAggiornato);
	}
	
	@PostMapping("/search")
	public List<UtenteDTO> search(@RequestBody UtenteDTO example) {
		return UtenteDTO.createUtenteDTOListFromModelList(utenteService.findByExample(example.buildUtenteModel(false)));
	}
}