package it.prova.pokeronline.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utente")
public class Utente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "cognome")
	private String cognome;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Enumerated(EnumType.STRING)
	private StatoUtente stato = StatoUtente.CREATO;

	@Column(name = "dataCreazione")
	private LocalDate dataCreazione;

	@Column(name = "creditoResiduo")
	private Double creditoResiduo;

	@Column(name = "esperienzaAccumulata")
	private Double esperienzaAccumulata;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tavolo_id")
	private Tavolo tavolo;

	@ManyToMany
	@JoinTable(name = "utente_ruolo", joinColumns = @JoinColumn(name = "utente_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ruolo_id", referencedColumnName = "ID"))
	private Set<Ruolo> ruoli = new HashSet<>();

	public Utente() {
	}

	public Utente(Long id, String nome, String cognome, String username, String password, StatoUtente stato,
			LocalDate dataCreazione, Double creditoResiduo, Double esperienzaAccumulata, Tavolo tavolo,
			Set<Ruolo> ruoli) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.password = password;
		this.stato = stato;
		this.dataCreazione = dataCreazione;
		this.creditoResiduo = creditoResiduo;
		this.esperienzaAccumulata = esperienzaAccumulata;
		this.tavolo = tavolo;
		this.ruoli = ruoli;
	}

	public Utente(String username, String password, String nome, String cognome, LocalDate dataCreazione,
			Double creditoResiduo, Double esperienzaAccumulata) {
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.password = password;
		this.dataCreazione = dataCreazione;
		this.creditoResiduo = creditoResiduo;
		this.esperienzaAccumulata = esperienzaAccumulata;
	}

	public Utente(Long id, String username, String password, String nome, String cognome, Double esperienzaAccumulata,
			Double creditoResiduo, LocalDate dataCreazione, StatoUtente stato) {
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.password = password;
		this.dataCreazione = dataCreazione;
		this.creditoResiduo = creditoResiduo;
		this.esperienzaAccumulata = esperienzaAccumulata;
		this.stato = stato;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public StatoUtente getStato() {
		return stato;
	}

	public void setStato(StatoUtente stato) {
		this.stato = stato;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dateCreated) {
		this.dataCreazione = dateCreated;
	}

	public Double getCreditoResiduo() {
		return creditoResiduo;
	}

	public void setCreditoResiduo(Double creditoResiduo) {
		this.creditoResiduo = creditoResiduo;
	}

	public Double getEsperienzaAccumulata() {
		return esperienzaAccumulata;
	}

	public void setEsperienzaAccumulata(Double esperienzaAccumulata) {
		this.esperienzaAccumulata = esperienzaAccumulata;
	}

	public Tavolo getTavolo() {
		return tavolo;
	}

	public void setTavolo(Tavolo tavolo) {
		this.tavolo = tavolo;
	}

	public Set<Ruolo> getRuoli() {
		return ruoli;
	}

	public void setRuoli(Set<Ruolo> ruoli) {
		this.ruoli = ruoli;
	}

	public boolean isAdmin() {
		for (Ruolo ruoloItem : ruoli) {
			if (ruoloItem.getCodice().equals(Ruolo.ROLE_ADMIN))
				return true;
		}
		return false;
	}

	public boolean isSpecialPlayer() {
		for (Ruolo ruoloItem : ruoli) {
			if (ruoloItem.getCodice().equals(Ruolo.ROLE_SPECIAL_PLAYER))
				return true;
		}
		return false;
	}

}
