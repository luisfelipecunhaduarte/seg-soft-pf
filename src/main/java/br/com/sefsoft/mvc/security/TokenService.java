package br.com.sefsoft.mvc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class TokenService {
	@Value(value = "${jwt.expiration:}")
	private String expiration;

	@Value(value = "${jwt.secret}")
	private String secret;

	public String gerarToken(Authentication authentication) {

		UsuarioPrincipal usuario = (UsuarioPrincipal) authentication.getPrincipal();

		Date now = new Date();

		var jwtBuilder = Jwts.builder()
				.setIssuer("Quantich Varejo Tecnologia")
				.setSubject(usuario.getId().toString())
				.setIssuedAt(now);

		if (!expiration.isBlank()) {
			Date dataExpiracao = Date.from(now.toInstant().plusSeconds(Long.parseLong(expiration)));
			jwtBuilder.setExpiration(dataExpiracao);
		} else {
			jwtBuilder.setExpiration(null);
		}

		return jwtBuilder.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public boolean isToken(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Integer getIdUsuario(String token) {
		Claims body = Jwts.parser()
				.setSigningKey(this.secret)
				.parseClaimsJws(token).getBody();

		return Integer.valueOf(body.getSubject());
	}

	public Integer getIdUsuario(HttpServletRequest request) {
		String token = request.getHeader("Authorization").substring(7);
		return this.getIdUsuario(token);
	}
}
