package br.com.sefsoft.mvc.exceptions;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
		super("Usuario não encontrado");
	}
}
