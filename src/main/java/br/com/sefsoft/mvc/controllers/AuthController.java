package br.com.sefsoft.mvc.controllers;

import br.com.sefsoft.mvc.rules.UsuarioAgreggate;
import br.com.sefsoft.mvc.security.TokenService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final UsuarioAgreggate usuarioAgreggate;
	private final TokenService tokenService;

	@PostMapping
	public ResponseEntity<TokenDTO> autenticar(@RequestBody @Valid LoginForm login) {
		UsernamePasswordAuthenticationToken dadosLogin = login.toUserNamePasswordAuthToken();
		try {
			var authentication = authenticationManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);

			AtomicReference<TokenDTO> tokenDTO = new AtomicReference<>();
			usuarioAgreggate.findByLogin(login.getLogin())
				.ifPresent(user -> {
					usuarioAgreggate.update(
							new UsuarioAgreggate.UpdateUsuarioParams(
									user.getNome(),
									null,
									0,
									null,
									new Date()
							),
							user.getId()
					);

					user.getSucessLoginLog().sort(Comparator.reverseOrder());
					var lastLogin = user.getSucessLoginLog().stream().findFirst().orElse(null);
					tokenDTO.set(new TokenDTO(token, TOKEN.BEARER, user.getLastFailedLoginAttempt(), lastLogin));
				});
			return ResponseEntity.ok().body(tokenDTO.get());
		} catch (AuthenticationException ex) {
			usuarioAgreggate.findByLogin(login.getLogin()).ifPresent(user -> {
				usuarioAgreggate.update(
						new UsuarioAgreggate.UpdateUsuarioParams(
								user.getNome(),
								null,
								user.getFailedAttempt() + 1,
								new Date(),
								null
						),
						user.getId()
				);
			});
			ex.printStackTrace();
			throw new IllegalArgumentException("Credenciais Inválidas!");
		}
	}

	@AllArgsConstructor
	@Getter
	@FieldDefaults(makeFinal = true, level = PRIVATE)
	public static class LoginForm {
		@NotBlank
		@NotNull
		String login;
		@NotBlank
		@NotNull
		String senha;

		public UsernamePasswordAuthenticationToken toUserNamePasswordAuthToken() {
			return new UsernamePasswordAuthenticationToken(login, senha);
		}
	}

	@AllArgsConstructor
	@Getter
	@FieldDefaults(makeFinal = true, level = PRIVATE)
	public static class TokenDTO {
		String token;
		TOKEN tipo;
		Date lastFailedLoginAttempt;
		Date lastSucessLoginAttempt;
	}

	public enum TOKEN {
		BEARER("BEARER");

		private final String tipo;

		TOKEN(String tipo) {
			this.tipo = tipo;
		}

		public String getTipo() {
			return this.tipo;
		}
	}
}
