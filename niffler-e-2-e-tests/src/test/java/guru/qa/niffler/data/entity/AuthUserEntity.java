package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {

    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(AuthUserJson json) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setId(json.getId());
        aue.setUsername(json.getUsername());
        aue.setPassword(json.getPassword());
        aue.setEnabled(json.isEnabled());
        aue.setAccountNonExpired(json.isAccountNonExpired());
        aue.setAccountNonLocked(json.isAccountNonLocked());
        aue.setCredentialsNonExpired(json.isCredentialsNonExpired());
        return aue;
    }
}
