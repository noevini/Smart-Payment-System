package com.smartpaymentsystem.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InsightResponseDTO {

    private String summary;
    private List<String> risks;
    private List<String> recommendations;
    private String confidence;

}