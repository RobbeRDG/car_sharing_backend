package be.kul.userservice.users.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    public String getId() {
        return id.toString();
    }

    public void setId(String userId) {
        id = userId;
    }
}