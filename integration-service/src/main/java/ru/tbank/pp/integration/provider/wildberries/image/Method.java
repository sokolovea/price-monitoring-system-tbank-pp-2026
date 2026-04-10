package ru.tbank.pp.integration.provider.wildberries.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Method {
    public String method;
    public List<Host> hosts;
}

