package de.othr.event_hub.api.jwt;

import java.io.Serializable;
import java.util.ArrayList;

import de.othr.event_hub.model.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;

    private ArrayList<Authority> authorities = new ArrayList<>();
    
}
