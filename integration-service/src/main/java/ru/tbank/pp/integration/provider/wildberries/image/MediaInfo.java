package ru.tbank.pp.integration.provider.wildberries.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaInfo {
    public List<Method> mediabasket_route_map;
}

