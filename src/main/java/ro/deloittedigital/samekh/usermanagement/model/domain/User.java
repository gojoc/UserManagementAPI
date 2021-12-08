package ro.deloittedigital.samekh.usermanagement.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private UUID id;

    @Email
    @NotBlank
    @Length(max = 128)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Length(max = 64)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Length(max = 32)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Length(max = 32)
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Length(max = 16)
    @Column(nullable = false, unique = true)
    private String username;

    @Length(max = 16)
    private String gender;

    @PastOrPresent
    private LocalDate birthday;
}
