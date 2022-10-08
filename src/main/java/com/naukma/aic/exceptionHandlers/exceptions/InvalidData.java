package com.naukma.aic.exceptionHandlers.exceptions;

import java.util.Map;

public class InvalidData extends RuntimeException {
    public InvalidData(Map<String, String> invalidParams) {
        super(buildResult(invalidParams));
    }

    private static String buildResult(Map<String, String> invalidParams) {
        StringBuilder sb = new StringBuilder("Invalid params: \n");
        for (Map.Entry<String, String> pair : invalidParams.entrySet()) {
            sb.append(pair.getKey()).append(":").append(pair.getValue());
        }

        return sb.toString();
    }
}
