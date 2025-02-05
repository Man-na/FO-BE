package com.manna.fobe.user.entity;

import com.manna.fobe.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "`user`")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class User extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    public enum Role {
        USER, ADMIN
    }
}
