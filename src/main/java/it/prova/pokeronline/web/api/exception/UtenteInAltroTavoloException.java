package it.prova.pokeronline.web.api.exception;

public class UtenteInAltroTavoloException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public UtenteInAltroTavoloException(String message) {
		super(message);
	}

}
