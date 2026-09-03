package com.perfectkode.bikri.common.config;


import com.perfectkode.bikri.common.utils.StringToOrderStatusConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToOrderStatusConverter stringToOrderStatusConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(stringToOrderStatusConverter);
    }
}
