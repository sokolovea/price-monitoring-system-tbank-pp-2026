package ru.tbank.pp.integration.provider.wildberries.image;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum MethodType {
    UNKNOWN,
    RANGE,
    MOD;

    @JsonCreator
    public static MethodType from(String value) {
        try {
            return MethodType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
