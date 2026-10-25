package com.example.smartcfp.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_topics", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "topic", nullable = false)
    private Set<String> topics = new HashSet<>();

    protected User() {
    }

    public User(String name, String email,String password){
        this.name=name;
        this.email=email;
        this.password=password;
    }

    public long getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public String getEmail(){
        return  email;
    }

    public String getPassword(){
        return  password;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setEmail(String email) {
        this.email=email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTopics(Set<String> topics) {
        this.topics.clear();
        this.topics.addAll(topics);
    }

}
