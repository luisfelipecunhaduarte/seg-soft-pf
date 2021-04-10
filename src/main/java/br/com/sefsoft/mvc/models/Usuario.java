package br.com.sefsoft.mvc.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "usuario",
		indexes = {
				@Index(name = "idx_nome", columnList = "nome"),
				@Index(name = "idx_cpf", columnList = "cpf")
		})
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = PRIVATE)
public class Usuario {
	@Id
	@GeneratedValue
	Integer id;

	@NonNull
	@Setter
	String nome;

	@NonNull
	@Setter
	String login;

	@NonNull
	@Setter
	String senha;

	@NonNull
	String cpf;

	@NonNull
	@OneToOne
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	Perfil perfil;

	@Setter
	private boolean enabled;

	@Setter
	private int failedAttempt;

	@Setter
	private Date lastFailedLoginAttempt;

	@Setter
	@ElementCollection
	private List<Date> sucessLoginLog = new ArrayList<>();

	public void addSuccessLogin(Date sucessLoginLog) {
		this.sucessLoginLog.add(sucessLoginLog);
	}
}
