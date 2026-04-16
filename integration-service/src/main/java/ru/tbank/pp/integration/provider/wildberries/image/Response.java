package ru.tbank.pp.integration.provider.wildberries.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    public MediaInfo recommend;
}
