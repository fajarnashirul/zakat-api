package com.nashirul.zakat.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @NonNull
    @Column(name = "email")
    private String email;
    @NonNull
    @Column(name = "password")
    private String password;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name= "users_roles",
            joinColumns={@JoinColumn(name= "user_id", referencedColumnName= "id")},
            inverseJoinColumns={@JoinColumn(name= "role_id", referencedColumnName= "id")})
    private List<Role> roles = new ArrayList<>();
}
