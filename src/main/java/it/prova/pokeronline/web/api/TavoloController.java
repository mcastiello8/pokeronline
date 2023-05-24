package it.prova.pokeronline.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.tavolo.TavoloService;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.PermessoNegatoException;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;

@RestController
@RequestMapping("/api/tavolo")
public class TavoloController {
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private TavoloService tavoloService;
	
	
	@GetMapping
	public List<TavoloDTO> getAll() {
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.listAllTavoli(), true);
	}
	
	@GetMapping("/{id}")
	public TavoloDTO findById(@PathVariable(value = "id", required = true) long id) {
		Tavolo tavolo = tavoloService.caricaSingoloTavoloConUtenza(id);

		if (tavolo == null)
			throw new TavoloNotFoundException("Tavolo con id: " + id + "non trovato");

		return TavoloDTO.buildTavoloDTOFromModel(tavolo, true);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TavoloDTO createNew(@Valid @RequestBody TavoloDTO tavoloInput) {
		if (tavoloInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		
		Tavolo TavoloInserito = tavoloService.inserisciNuovo(tavoloInput.buildFromModel());
		
		return TavoloDTO.buildTavoloDTOFromModel(TavoloInserito, true);
	}
	
	@PutMapping("/{id}")
	public TavoloDTO update(@Valid @RequestBody TavoloDTO tavoloInput, @PathVariable(required = true) Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		Tavolo tavolo = tavoloService.caricaSingoloTavolo(id);

		if (tavolo == null)
			throw new TavoloNotFoundException("Tavolo con id: " + id + "non trovato");
		
		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) != null || utenteLoggato.getId() == tavolo.getUtenteCreazione().getId()) {
			
			
			Tavolo tavoloAggiornato = tavoloService.aggiorna(tavoloInput.buildFromModel());
			return TavoloDTO.buildTavoloDTOFromModel(tavoloAggiornato, true);
			
		} else {
			throw new PermessoNegatoException("Non hai i permessi per modificare questo elemento!");
		}
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {
		
		Tavolo tavolo = tavoloService.caricaSingoloTavolo(id);
		
		if (tavolo == null)
			throw new TavoloNotFoundException("Tavolo con id: " + id + "non trovato");
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		
		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) != null || utenteLoggato.getId() == tavolo.getUtenteCreazione().getId()) {
			
			tavoloService.rimuovi(id);
			
		} else {
			throw new PermessoNegatoException("Non hai i permessi per eliminare questo elemento!");
		}
	}
	
	@PostMapping("/search")
	public List<TavoloDTO> search(@RequestBody TavoloDTO example) {
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.findByExample(example.buildFromModel()), false);
	}

}