package com.lenda.challenge.spring;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.ofInstant;

@Configuration
public class MongoConfiguration {

    @Bean
    public CustomConversions provideCustomConversions() {
        return new CustomConversions(Lists.newArrayList(
                DateToZonedDateTimeConverter.INSTANCE,
                ZonedDateTimeToDateConverter.INSTANCE,
                MapToOffsetDateTimeConverter.INSTANCE,
                OffsetDateTimeToDateConverter.INSTANCE,
                DateToOffsetDateTimeConverter.INSTANCE));
    }

    private enum DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

        INSTANCE;

        @Override
        public OffsetDateTime convert(Date source) {
            return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), systemDefault());
        }
    }

    private enum OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

        INSTANCE;

        @Override
        public Date convert(OffsetDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    private enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        INSTANCE;

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : ofInstant(source.toInstant(), systemDefault());
        }
    }

    private enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        INSTANCE;

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    private enum MapToOffsetDateTimeConverter implements Converter<Map<String, String>, OffsetDateTime> {

        INSTANCE;

        @Override
        public OffsetDateTime convert(Map<String, String> source) {
            try {
                return source == null ? null : OffsetDateTime.ofInstant(
                        new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").parse(source.get("datetime")).toInstant(),
                        ZoneOffset.ofHours(Integer.valueOf(source.get("offset"))));
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
