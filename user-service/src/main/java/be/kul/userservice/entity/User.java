package be.kul.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class User {
    @Id
    @JsonIgnore //Ignore id in json serialization
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;

    public User() {
    }

    public User(UUID id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id.toString();
    }
}