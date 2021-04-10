package br.com.sefsoft.mvc.security;

import br.com.sefsoft.mvc.models.Perfil;
import br.com.sefsoft.mvc.models.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Service("userDetailsService")
public class UsuarioPrincipal implements UserDetails {
	private static final String ROLE_PREFIX = "ROLE_";

	private Integer id;

	private String nome;

	private String login;

	private String senha;

	private String cpf;

	private Perfil perfil;

	private boolean enabled;

	private boolean accountNonLocked;

	public static UsuarioPrincipal fromDomain(Usuario usuario) {

		UsuarioPrincipal usuarioEntitie = new UsuarioPrincipal();
		usuarioEntitie.setId(usuario.getId());
		usuarioEntitie.setNome(usuario.getNome());
		usuarioEntitie.setLogin(usuario.getLogin());
		usuarioEntitie.setSenha(usuario.getSenha());
		usuarioEntitie.setCpf(usuario.getCpf());
		usuarioEntitie.setPerfil(usuario.getPerfil());
		usuarioEntitie.setEnabled(usuario.isEnabled());

		return usuarioEntitie;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + this.getPerfil().getNome()));

		return authorities;
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.getLogin();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
