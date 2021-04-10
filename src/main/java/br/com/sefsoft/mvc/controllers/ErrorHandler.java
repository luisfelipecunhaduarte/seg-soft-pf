package br.com.sefsoft.mvc.controllers;

import br.com.sefsoft.mvc.exceptions.ParamsNotValidException;
import br.com.sefsoft.mvc.exceptions.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
	final MessageSource messageSource;

	public ErrorHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> paramsHandler(MethodArgumentNotValidException ex){
		List<String> msgs = new ArrayList<>();
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		fieldErrors.forEach(fieldError -> {
			String mensagem = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			msgs.add(String.format("%s %s", fieldError.getField(), mensagem));
		});
		return ResponseEntity.badRequest().body(msgs);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<List<String>> handleUserNotFoundException(UserNotFoundException ex) {
		List<String> msgs = new ArrayList<>();
		msgs.add(ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(msgs);
	}

	@ExceptionHandler(ParamsNotValidException.class)
	public ResponseEntity<List<String>> handleParamsNotValidException(ParamsNotValidException ex) {
		List<String> msgs = new ArrayList<>();
		msgs.add(ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(msgs);
	}
}
