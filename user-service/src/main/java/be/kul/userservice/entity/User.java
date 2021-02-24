package be.kul.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(nullable = false)
    private String id;

    public String getId() {
        return id.toString();
    }

    public void setId(String userId) {
        id = userId;
    }
}