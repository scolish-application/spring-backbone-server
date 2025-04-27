package com.github.simaodiazz.schola.backbone.server.entity.data.model;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "O NIF é obrigatório")
    @Pattern(regexp = "^[0-9]{9}$", message = "O NIF deve conter 9 dígitos")
    @Column(nullable = false, unique = true)
    private String nif;

    @Past(message = "A data de nascimento deve estar no passado")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Email(message = "Email inválido")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "O número de telefone deve conter 9 dígitos")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<Guardian> guardians = new ArrayList<>();

    @Column(nullable = false)
    private String schoolClass;

    @Column
    private String emergencyContactName;

    @Column
    @Pattern(regexp = "^[0-9]{9}$", message = "O número de emergência deve conter 9 dígitos")
    private String emergencyContactPhone;

    @Column
    private boolean specialNeeds;

    @Column
    private String medicalInformation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}