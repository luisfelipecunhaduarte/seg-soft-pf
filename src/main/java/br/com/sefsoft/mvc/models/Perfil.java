package br.com.sefsoft.mvc.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Perfil {
	@Id
	@GeneratedValue
	Integer id;

	@NonNull
	String nome;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Perfil perfil = (Perfil) o;
		return getId().equals(perfil.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

}
