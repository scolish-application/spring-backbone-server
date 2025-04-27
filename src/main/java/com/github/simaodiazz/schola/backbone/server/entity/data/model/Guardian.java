package com.github.simaodiazz.schola.backbone.server.entity.data.model;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "O grau de parentesco é obrigatório")
    @Column(nullable = false)
    private String relationship;

    @NotBlank(message = "O NIF é obrigatório")
    @Pattern(regexp = "^[0-9]{9}$", message = "O NIF deve conter 9 dígitos")
    @Column(nullable = false)
    private String nif;

    @Email(message = "Email inválido")
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "O número de telefone deve conter 9 dígitos")
    @Column(nullable = false)
    private String phone;

    @Column
    private String occupation;

    @Column
    private String workPhone;

    @Column
    private boolean isPrimaryGuardian;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

}