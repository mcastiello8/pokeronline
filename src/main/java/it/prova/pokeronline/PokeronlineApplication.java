package it.prova.pokeronline;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.pokeronline.model.Ruolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.ruolo.RuoloService;
import it.prova.pokeronline.service.utente.UtenteService;

@SpringBootApplication
public class PokeronlineApplication implements CommandLineRunner{
	
	@Autowired
	private RuoloService ruoloServiceInstance;
	
	@Autowired
	private UtenteService utenteServiceInstance;
	
	public static void main(String[] args) {
		SpringApplication.run(PokeronlineApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", Ruolo.ROLE_ADMIN));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Special User", Ruolo.ROLE_SPECIAL_PLAYER) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Special User", Ruolo.ROLE_SPECIAL_PLAYER));
		}
		
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", Ruolo.ROLE_PLAYER) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", Ruolo.ROLE_PLAYER));
		}
		
		if (utenteServiceInstance.findByUsername("admin") == null) {
			Utente admin = new Utente("Matteo", "Castiello", "admin", "admin", LocalDate.now());
			admin.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN));
			admin.setCreditoResiduo(0.0);
			admin.setEsperienzaAccumulata(0.0);
			utenteServiceInstance.inserisciNuovo(admin);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(admin.getId());
		}
		
		if (utenteServiceInstance.findByUsername("SpecialPlayer") == null) {
			Utente specialPlayer = new Utente("Francesco", "Paradiso", "SpecialPlayer", "SpecialPlayer", LocalDate.now());
			specialPlayer.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Special User", Ruolo.ROLE_SPECIAL_PLAYER));
			specialPlayer.setCreditoResiduo(0.0);
			specialPlayer.setEsperienzaAccumulata(0.0);
			utenteServiceInstance.inserisciNuovo(specialPlayer);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(specialPlayer.getId());
		}
		
		if (utenteServiceInstance.findByUsername("ClassicPlayer") == null) {
			Utente classicPlayer = new Utente("Luigi", "Iorio", "ClassicPlayer", "ClassicPlayer", LocalDate.now());
			classicPlayer.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", Ruolo.ROLE_PLAYER));
			classicPlayer.setCreditoResiduo(0.0);
			classicPlayer.setEsperienzaAccumulata(0.0);
			utenteServiceInstance.inserisciNuovo(classicPlayer);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(classicPlayer.getId());
		}
		
		
		
		
	}

}