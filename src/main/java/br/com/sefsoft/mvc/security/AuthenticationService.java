package br.com.sefsoft.mvc.security;

import br.com.sefsoft.mvc.exceptions.UserNotFoundException;
import br.com.sefsoft.mvc.models.Usuario;
import br.com.sefsoft.mvc.rules.UsuarioAgreggate;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService implements UserDetailsService {
	UsuarioAgreggate usuarioAgreggate;

	@Override
	public UserDetails loadUserByUsername(String username) {

		Optional<Usuario> usuario = usuarioAgreggate.findByLogin(username);

		if (usuario.isPresent()) {
			return UsuarioPrincipal.fromDomain(usuario.get());
		}

		throw new UserNotFoundException();
	}

	public UserDetails loadUserById(Integer idUsuario) {
		var user = usuarioAgreggate.getUsuarioById(idUsuario);
		return UsuarioPrincipal.fromDomain(user);
	}
}