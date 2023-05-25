package it.prova.pokeronline.service.tavolo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.CreditoInsufficenteException;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;
import it.prova.pokeronline.web.api.exception.UtenteInAltroTavoloException;

@Service
@Transactional(readOnly = true)
public class TavoloServiceImpl implements TavoloService {

	@Autowired
	private TavoloRepository repository;

	@Autowired
	private UtenteService utenteService;

	@Autowired
	private UtenteRepository utenteRepository;

	@Override
	public List<Tavolo> listAllTavoli() {
		return (List<Tavolo>) repository.findAll();
	}

	@Override
	public List<Tavolo> listAllElementsEager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tavolo> findByUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tavolo caricaSingoloTavolo(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Tavolo caricaSingoloTavoloConUtenza(Long id) {
		return repository.findByIdEager(id);
	}

	@Override
	@Transactional
	public Tavolo aggiorna(Tavolo tavoloInstance) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		tavoloInstance.setId(utenteLoggato.getId());
		tavoloInstance.setUtenteCreazione(utenteService.findByUsername(username));
		return repository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public Tavolo inserisciNuovo(Tavolo tavoloInstance) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		tavoloInstance.setUtenteCreazione(utenteService.findByUsername(username));
		tavoloInstance.setDataCreazione(LocalDate.now());
		return repository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);

	}

	@Override
	public List<Tavolo> findByEsperienzaMinimaLessThan() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		Double esperienzaAccumulata = utenteLoggato.getEsperienzaAccumulata();

		return repository.findByEsperienzaMinLessThan(esperienzaAccumulata);
	}
	
	@Override
	@Transactional
	public TavoloDTO uniscitiAlTavolo(Long idTavolo) {

		Tavolo tavolo = repository.findById(idTavolo).orElse(null);

		if (tavolo == null)
			throw new TavoloNotFoundException("Tavolo non trovato");

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);

		if (tavolo.getGiocatori().contains(utenteInSessione))
			throw new TavoloNotFoundException("Non puoi sederti al tavolo");

		if (utenteInSessione.getCreditoResiduo() < tavolo.getCifraMinima())
			throw new CreditoInsufficenteException("Non c'hai i soldi");

		if (utenteInSessione.getEsperienzaAccumulata() < tavolo.getEsperienzaMin())
			throw new TavoloNotFoundException("Non hai abbastanza esperienza");

		tavolo.getGiocatori().add(utenteInSessione);

		return TavoloDTO.buildTavoloDTOFromModel(tavolo);

	}

	@Override
	@Transactional
	public void gioca(Long idTavolo, String username) {

		Tavolo tavolo = repository.findById(idTavolo).orElse(null);

		if (tavolo == null)
			throw new IdNotNullForInsertException("");

		username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);

		if (!tavolo.getGiocatori().contains(utenteInSessione))
			throw new TavoloNotFoundException("Eh bro");

		if (utenteInSessione.getCreditoResiduo() == null || utenteInSessione.getCreditoResiduo() == 0d) {
			throw new CreditoInsufficenteException("Poveraccio");
		}

		double segno = Math.random();
		if (segno < 0.5)
			segno = segno * -1;
		int somma = (int) (Math.random() * 500);
		int totale = (int) (segno * somma);

		Integer esperienzaGuadagnata = 0;

		if (totale > 0) {
			esperienzaGuadagnata += 5;
			if (totale > 200)
				esperienzaGuadagnata += 6;
			if (totale > 400)
				esperienzaGuadagnata += 7;
			if (totale > 499)
				esperienzaGuadagnata += 8;
		}

		if (totale <= 0) {
			esperienzaGuadagnata += 4;
			if (totale < -200)
				esperienzaGuadagnata += 3;
			if (totale < -400)
				esperienzaGuadagnata += 2;
			if (totale < -499)
				esperienzaGuadagnata += 1;
		}

		utenteInSessione.setEsperienzaAccumulata(esperienzaGuadagnata + utenteInSessione.getEsperienzaAccumulata());

		Double creditoDaInserire = utenteInSessione.getCreditoResiduo() + totale;

		if (creditoDaInserire < 0) {
			creditoDaInserire = 0D;
		}

		utenteInSessione.setCreditoResiduo(creditoDaInserire);


	}

	@Override
	public List<Tavolo> findByExample(Tavolo example) {
		return repository.findByExample(example);
	}

	@Override
	public Page<Tavolo> findByExampleNativeWithPagination(Tavolo example, Integer pageNo, Integer pageSize,
	String sortBy) {
	if (pageNo == null || pageNo < 0) {
	            pageNo = 0; // Impostare a zero se non è specificato o è un valore negativo
	        }
	        if (pageSize == null || pageSize < 1) {
	            pageSize = 10; // Impostare a un valore di default (es. 10) se non è specificato o è un valore non valido
	        }
	        if (sortBy == null || sortBy.isEmpty()) {
	            sortBy = "id"; // Impostare un campo di ordinamento di default (es. "id") se non è specificato o è vuoto
	        }
	return repository.findByExampleNativeWithPagination(example.getDenominazione(), example.getEsperienzaMin(),
	example.getCifraMinima(), example.getDataCreazione(),
	PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
	}

}