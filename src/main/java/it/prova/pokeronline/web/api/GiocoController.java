package it.prova.pokeronline.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.dto.UtenteDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.tavolo.TavoloService;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.UtenteNotFoundException;

@RestController
@RequestMapping("/api/game")
public class GiocoController {
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private TavoloService tavoloService;
	
	
	@GetMapping("/ricaricaCredito/{soldi}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public UtenteDTO addCredito(@PathVariable(value = "soldi", required = true) Double soldi) {
				
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		
		Double nuovoCredito = soldi;
		
		utenteLoggato.setCreditoResiduo(nuovoCredito);
		
		utenteService.aggiorna(utenteLoggato);
		
		return UtenteDTO.buildUtenteDTOFromModel(utenteLoggato);
		
	}
	
	@GetMapping("/ricerca")
	public List<TavoloDTO> tavoliConEsperienza(){
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.findByEsperienzaMinimaLessThan());
	}
	
	@GetMapping("/gioca/{idTavolo}")
	@ResponseStatus(HttpStatus.OK)
	public void gioca(@PathVariable(value = "idTavolo", required = true) Long id) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		tavoloService.gioca(id, username);
	}
	
	@GetMapping("/lastGame")
	public TavoloDTO lastGame() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		
		Tavolo result = utenteService.dammiLastGame(utenteLoggato);
		
		return TavoloDTO.buildTavoloDTOFromModel(result);
	}
	
	@GetMapping("/abbandona")
	@ResponseStatus(HttpStatus.OK)
	public void abbandona() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato == null) {
			throw new UtenteNotFoundException("Utente non trovato");
		}
		
		utenteService.abbandonaPartita(utenteLoggato);
	}
	
}