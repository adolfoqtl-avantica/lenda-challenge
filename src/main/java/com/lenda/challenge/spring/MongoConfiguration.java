package com.lenda.challenge.spring;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.bson.BsonType;
import org.bson.Document;
import org.bson.Transformer;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.ofInstant;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoClientOptions provideMongoClientOptions() {
        Map<BsonType, Class<?>> replacements = Maps.newHashMap();
        replacements.put(BsonType.DATE_TIME, Instant.class);
        BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap(replacements);
        return MongoClientOptions.builder()
                .codecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromProviders(new DocumentCodecProvider(bsonTypeClassMap, new DocumentToDBRefTransformer())),
                        MongoClient.getDefaultCodecRegistry()))
                .build();
    }

    @Bean
    public CustomConversions provideCustomConversions() {
        return new CustomConversions(Lists.newArrayList(
                DateToZonedDateTimeConverter.INSTANCE,
                ZonedDateTimeToDateConverter.INSTANCE,
                MapToOffsetDateTimeConverter.INSTANCE));
    }

    public enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        INSTANCE;

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : ofInstant(source.toInstant(), systemDefault());
        }
    }

    public enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        INSTANCE;

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public enum MapToOffsetDateTimeConverter implements Converter<Map<String, String>, OffsetDateTime> {

        INSTANCE;

        @Override
        public OffsetDateTime convert(Map<String, String> source) {
            return source == null ? null : OffsetDateTime.from(new Date(source.get("timestamp")).toInstant());
        }
    }

    public static class DocumentToDBRefTransformer implements Transformer {

        DocumentToDBRefTransformer() {
        }

        public Object transform(Object value) {
            if(value instanceof Document) {
                Document document = (Document)value;
                if(document.containsKey("$id") && document.containsKey("$ref")) {
                    return new DBRef((String)document.get("$db"), (String)document.get("$ref"), document.get("$id"));
                }
            }

            return value;
        }

        public boolean equals(Object o) {
            return this == o?true:o != null && this.getClass() == o.getClass();
        }

        public int hashCode() {
            return 0;
        }
    }
}
