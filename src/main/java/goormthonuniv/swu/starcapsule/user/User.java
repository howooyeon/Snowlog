package goormthonuniv.swu.starcapsule.user;

import goormthonuniv.swu.starcapsule.refreshToken.RefreshToken;
import goormthonuniv.swu.starcapsule.snowball.Snowball;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @OneToOne(cascade = CascadeType.PERSIST)//User를 저장할 때 관련된 RefreshToken도 함께 저장
    @JoinColumn(name = "refresh_token_id")
    private RefreshToken refreshToken;

    @OneToOne
    @JoinColumn(name = "snowball_id")
    private Snowball snowball;

    @Builder
    public User(String email) {
        this.email = email;
    }

    @Builder
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
