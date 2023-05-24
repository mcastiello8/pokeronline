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

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.CreditoInsufficenteException;
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
	public void gioca(Long id, String username) {
		Tavolo tavoloReload = repository.findById(id).orElse(null);

		if (tavoloReload == null) {
			throw new TavoloNotFoundException("Il tavolo in cui si cerca di entrare non esiste");
		}

		Utente utenteLoggato = utenteService.findByUsername(username);

		if (tavoloReload.getCifraMinima() > utenteLoggato.getCreditoResiduo()) {
			throw new CreditoInsufficenteException("Credito insufficente Per giocare");
		}

		List<Tavolo> listaTavoli = (List<Tavolo>) repository.findAll();

		for (Tavolo elementoTavolo : listaTavoli) {
			if (elementoTavolo.getId() != id) {
				for (Utente elementoUtente : elementoTavolo.getGiocatori())
					if (elementoUtente.getId().equals(utenteLoggato.getId())) {
						throw new UtenteInAltroTavoloException("Stai giocando ad un altro tavolo non puoi unirti");
					}
			}
		}

		tavoloReload.getGiocatori().add(utenteLoggato);

		boolean maggiore = false;
		double segno = Math.random();
		Double credito = 0.0;

		if (segno >= 0.5) {
			maggiore = true;
		} else {
			maggiore = false;
		}

		if (maggiore) {
			credito = (utenteLoggato.getCreditoResiduo() + Math.random() * 1000);
		} else {
			credito = (utenteLoggato.getCreditoResiduo() - Math.random() * 1000);
		}

		if (utenteLoggato.getCreditoResiduo() <= 0) {
			utenteLoggato.setCreditoResiduo(0.0);
			throw new CreditoInsufficenteException("Credito insufficente");
		}
		
		utenteLoggato.setCreditoResiduo(credito);
		utenteRepository.save(utenteLoggato);

		repository.save(tavoloReload);
	}

	@Override
	public List<Tavolo> findByExample(Tavolo example) {
		return repository.findByExample(example);
	}

	@Override
	public Page<Tavolo> findByExampleNativeWithPagination(Tavolo example, Integer pageNo, Integer pageSize,
	String sortBy) {
	return repository.findByExampleNativeWithPagination(example.getDenominazione(), example.getEsperienzaMin(),
	example.getCifraMinima(), example.getDataCreazione(),
	PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
	}

}