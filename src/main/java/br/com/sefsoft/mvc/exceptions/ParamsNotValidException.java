package br.com.sefsoft.mvc.exceptions;

public class ParamsNotValidException extends RuntimeException {
	public ParamsNotValidException(String msg) {
		super(msg);
	}
}
