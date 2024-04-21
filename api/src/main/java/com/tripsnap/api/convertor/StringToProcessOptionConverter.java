package com.tripsnap.api.convertor;

import com.tripsnap.api.domain.dto.ProcessOption;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class StringToProcessOptionConverter implements Formatter<ProcessOption> {

    @Override
    public ProcessOption parse(String text, Locale locale) throws ParseException {
        return ProcessOption.valueOf(text.toUpperCase());
    }

    @Override
    public String print(ProcessOption object, Locale locale) {
        return object.toString();
    }
}
