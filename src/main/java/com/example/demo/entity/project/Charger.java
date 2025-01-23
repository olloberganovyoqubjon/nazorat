package com.example.demo.entity.project;


import com.example.demo.entity.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Charger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "control_id")
    @JsonBackReference
    private Control control;

    @ManyToOne
    private Users users;

    private Boolean chargeable = false;

    private Boolean opened = false;

    private Integer stage;

    private String chargerName;
}
