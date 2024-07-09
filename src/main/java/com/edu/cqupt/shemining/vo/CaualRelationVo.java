package com.edu.cqupt.shemining.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Data
public class CaualRelationVo {
    private String clinicalRepresentation;
    private String livingHabit;
    private String socialConnection;
    private List<String> causalRelationships;
}
