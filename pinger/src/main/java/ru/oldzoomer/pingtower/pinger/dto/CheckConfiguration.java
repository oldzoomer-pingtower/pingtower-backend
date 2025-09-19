package ru.oldzoomer.pingtower.pinger.dto;

import lombok.Data;

@Data
public class CheckConfiguration {
    private String id;
    private String type;
    private String resourceUrl;
    private long frequency;
    private int timeout;
    private Integer expectedStatusCode;
    private long expectedResponseTime;
    private boolean validateSsl;
}