package br.com.sefsoft.mvc.security;

import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
public class AuthenticationTokenFilter extends OncePerRequestFilter {

	TokenService tokenService;

	AuthenticationService authenticationService;

	public AuthenticationTokenFilter(TokenService tokenService, AuthenticationService authenticationService) {
		this.tokenService = tokenService;
		this.authenticationService = authenticationService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

		String token = recuperarToken(request);

		boolean isToken = tokenService.isToken(token);

		if (isToken) {
			autenticarCliente(token);
		}

		filterChain.doFilter(request, response);

	}

	void autenticarCliente(String token) {

		Integer idUsuario = tokenService.getIdUsuario(token);

		UsuarioPrincipal usuario = (UsuarioPrincipal) authenticationService.loadUserById(idUsuario);

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	String recuperarToken(HttpServletRequest request) {

		String token = request.getHeader("Authorization");

		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}

		return token.substring(7);

	}

}
