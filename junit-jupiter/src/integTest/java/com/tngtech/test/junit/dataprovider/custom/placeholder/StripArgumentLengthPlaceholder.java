package com.tngtech.test.junit.dataprovider.custom.placeholder;

import java.util.List;

import com.tngtech.junit.dataprovider.placeholder.ArgumentPlaceholder;

class StripArgumentLengthPlaceholder extends ArgumentPlaceholder {
    private final int maxLength;

    StripArgumentLengthPlaceholder(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected String formatAll(List<Object> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.size(); i++) {
            String formattedParameter = format(arguments.get(i));
            if (formattedParameter.length() > maxLength) {
                stringBuilder.append(formattedParameter.substring(0, maxLength - 5));
                stringBuilder.append("...");
                stringBuilder.append(formattedParameter.substring(formattedParameter.length() - 2));
            } else {
                stringBuilder.append(formattedParameter);
            }
            if (i < arguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}