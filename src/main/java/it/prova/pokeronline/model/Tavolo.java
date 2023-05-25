package it.prova.pokeronline.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tavolo")
public class Tavolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "esperienzaMin")
    private Double esperienzaMin;

   
    @Column(name = "cifraMinima")
    private Double cifraMinima;

    @Column(name = "denominazione")
    private String denominazione;

    @Column(name = "dataCreazione")
    private LocalDate dataCreazione;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Utente> giocatori = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="utente_id")
    private Utente utenteCreazione;

    public Tavolo() {
    }

    public Tavolo(Long id, Double esperienzaMin, Double cifraMinima, String denominazione, LocalDate dataCreazione, Double creditoResiduo, Double esperienzaAccumulata, Set<Utente> giocatori, Utente utenteCreazione) {
        this.id = id;
        this.esperienzaMin = esperienzaMin;
        this.cifraMinima = cifraMinima;
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.giocatori = giocatori;
        this.utenteCreazione = utenteCreazione;
    }

    public Tavolo(Double esperienzaMin, Double cifraMinima, String denominazione, LocalDate dataCreazione, Utente utenteCreazione) {
        this.esperienzaMin = esperienzaMin;
        this.cifraMinima = cifraMinima;
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.utenteCreazione = utenteCreazione;
    }

    public Tavolo(Long id, Double esperienzaMin, Double cifraMinima, String denominazione, LocalDate dataCreazione) {
    	this.id = id;
        this.esperienzaMin = esperienzaMin;
        this.cifraMinima = cifraMinima;
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getEsperienzaMin() {
        return esperienzaMin;
    }

    public void setEsperienzaMin(Double esperienzaMin) {
        this.esperienzaMin = esperienzaMin;
    }

    public Double getCifraMinima() {
        return cifraMinima;
    }

    public void setCifraMinima(Double cifraMinima) {
        this.cifraMinima = cifraMinima;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDate dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Set<Utente> getGiocatori() {
        return giocatori;
    }

    public void setGiocatori(Set<Utente> giocatori) {
        this.giocatori = giocatori;
    }

    public Utente getUtenteCreazione() {
        return utenteCreazione;
    }

    public void setUtenteCreazione(Utente utenteCreazione) {
        this.utenteCreazione = utenteCreazione;
    }

}
