package br.com.sefsoft.mvc.controllers;

import br.com.sefsoft.mvc.controllers.utils.UUIDValid;
import br.com.sefsoft.mvc.models.Usuario;
import br.com.sefsoft.mvc.rules.UsuarioAgreggate;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class UsuarioController {
	UsuarioAgreggate usuarioAgreggate;

	@GetMapping
	public ResponseEntity<Page<Usuario>> listarUsuarios(@PageableDefault
                                                        Pageable page) {
		var usuarios = this
				.usuarioAgreggate
				.findAll(page);
		return ResponseEntity.ok(usuarios);
	}

	@PostMapping
	public ResponseEntity<UsuarioAgreggate.UsuarioCriadoPresenter> criaUsuario(@RequestBody @Valid UsuarioAgreggate.CreateUsuarioParams usuario) {
		var usuarioCriado = this.usuarioAgreggate.create(usuario);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(usuarioCriado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Usuario> updateUsuario(@RequestBody @Valid UsuarioAgreggate.UpdateUsuarioParams updateUsuario,
	                                             @PathVariable(name = "id") @UUIDValid Integer id) {
		var usuarioAtualizado = this.usuarioAgreggate
				.update(updateUsuario, id);
		return ResponseEntity
				.ok(usuarioAtualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteUsuario(@PathVariable @UUIDValid Integer id) {
		this.usuarioAgreggate
				.deleteUsuario(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getById(@PathVariable Integer id) {
		var usuario = this.usuarioAgreggate
				.getUsuarioById(id);
		return ResponseEntity.ok(usuario);
	}
}
