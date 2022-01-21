package com.example.senensig.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Visita implements Serializable {
    private String fecha;
    private String idVisit;
    private String lugar;
    private Double temperaturaC;
}
