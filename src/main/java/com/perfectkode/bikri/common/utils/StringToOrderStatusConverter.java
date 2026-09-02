package com.perfectkode.bikri.common.utils;

import com.perfectkode.bikri.order.model.OrderStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToOrderStatusConverter implements Converter<String, OrderStatus> {

    @Override
    public OrderStatus convert(@NonNull String source) {
        if (source.trim().isEmpty()) {
            return null;
        }
        try {
            // Case-insensitive parsing
            return OrderStatus.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Throwing IllegalArgumentException allows Spring MVC to wrap it in a MethodArgumentTypeMismatchException
            throw new IllegalArgumentException("Invalid order status: " + source);
        }
    }
}
