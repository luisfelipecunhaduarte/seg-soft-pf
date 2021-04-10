package br.com.sefsoft.mvc.rules;

import br.com.sefsoft.mvc.exceptions.UserNotFoundException;
import br.com.sefsoft.mvc.models.Perfil;
import br.com.sefsoft.mvc.models.Usuario;
import br.com.sefsoft.mvc.repositories.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class UsuarioAgreggate {
	public static final int MAX_FAILED_ATTEMPTS = 3;
	UsuarioRepository usuarioRepository;

	public Page<Usuario> findAll(Pageable pageable) {
		return this.usuarioRepository
				.findAll(pageable);
	}

	/*
	Bcrypt utiliza salt, implementação:
	public String encode(CharSequence rawPassword) {
		if (rawPassword == null) {
			throw new IllegalArgumentException("rawPassword cannot be null");
		}

		String salt;
		if (random != null) {
			salt = BCrypt.gensalt(version.getVersion(), strength, random);
		} else {
			salt = BCrypt.gensalt(version.getVersion(), strength);
		}
		return BCrypt.hashpw(rawPassword.toString(), salt);
	}
	 */
	@Transactional
	public UsuarioCriadoPresenter create(CreateUsuarioParams usuario) {
		var usuarioNovo = new Usuario(usuario.getNome(),
				usuario.getLogin(),
				new BCryptPasswordEncoder().encode(usuario.getSenha()),
				usuario.getCpf(),
				new Perfil("ADM"));
		usuarioNovo.setEnabled(true);
		var usuarioCriado = this.usuarioRepository.save(usuarioNovo);

		usuarioCriado
				.getSucessLoginLog()
				.sort(Collections.reverseOrder());

		var lastLogin = usuarioCriado.getSucessLoginLog().stream()
				.findFirst()
				.orElse(null);

		return new UsuarioCriadoPresenter(usuarioCriado.getId(),
				usuarioCriado.getNome(),
				usuarioCriado.getLogin(),
				usuarioCriado.getCpf(),
				usuarioCriado.getPerfil(),
				usuarioCriado.getLastFailedLoginAttempt(),
				lastLogin);
	}

	@Transactional
	public Usuario update(UpdateUsuarioParams updateUsuario, Integer idDoUsuario) {
		var usuario = this.usuarioRepository.findById(idDoUsuario)
				.orElseThrow(UserNotFoundException::new);
		usuario.setNome(updateUsuario.getNome());

		if (updateUsuario.getFailedAttempt() >= MAX_FAILED_ATTEMPTS) {
			usuario.setEnabled(false);
		}
		usuario.setFailedAttempt(updateUsuario.getFailedAttempt());
		if (updateUsuario.getLasFailedLoginAttempt() != null) {
			usuario.setLastFailedLoginAttempt(updateUsuario.getLasFailedLoginAttempt());
		}

		if (updateUsuario.getLastSucessLogin() != null) {
			usuario.addSuccessLogin(updateUsuario.getLastSucessLogin());
		}

		if (updateUsuario.getSenha() != null) {
			usuario.setSenha(new BCryptPasswordEncoder().encode(updateUsuario.getSenha()));
		}
		return this.usuarioRepository.save(usuario);
	}

	@Transactional
	public void deleteUsuario(Integer idDoUsuario) {
		this.usuarioRepository
			.findById(idDoUsuario)
			.ifPresent(this.usuarioRepository::delete);
	}

	public Usuario getUsuarioById(Integer idDoUsuario) {
		return this.usuarioRepository
				.findById(idDoUsuario)
				.orElseThrow(UserNotFoundException::new);
	}

	public Optional<Usuario> findByLogin(String username) {
		return this.usuarioRepository
				.findByLogin(username);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@FieldDefaults(level = PRIVATE)
	public static class CreateUsuarioParams {
		@NotBlank(message = "Nome não deve ser branco")
		@NotNull(message = "Nome não deve ser nulo")
		String nome;

		@NotBlank(message = "CPF não deve ser branco")
		@NotNull(message = "CPF não deve ser nulo")
		String cpf;

		@NotBlank(message = "Login não deve ser branco")
		@NotNull(message = "Login não deve ser nulo")
		String login;

		@NotBlank(message = "Senha não deve ser branco")
		@NotNull(message = "Senha não deve ser nulo")
		String senha;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@FieldDefaults(level = PRIVATE)
	public static class UpdateUsuarioParams {
		@NotBlank(message = "Nome não deve ser branco")
		@NotNull(message = "Nome não deve ser nulo")
		String nome;

		String senha;

		int failedAttempt;

		Date lasFailedLoginAttempt;

		Date lastSucessLogin;
	}

	@AllArgsConstructor
	@Data
	public static class UsuarioCriadoPresenter {
		Integer id;
		String nome;
		String login;
		String cpf;
		Perfil perfil;
		Date lastFailedLoginAttempt;
		Date lastSuccesLogin;
	}
}
