@TypeDefs({
        @TypeDef(
                name = "EncryptedString",
                typeClass = EncryptedStringType.class,
                parameters = {
                        @Parameter(name = "encryptorRegisteredName", value = "HibernateStringEncryptor")
                }
        ),
        @TypeDef(
                name = "EncryptedBytes",
                typeClass = EncryptedBinaryType.class,
                parameters = {
                        @Parameter(name = "encryptorRegisteredName", value = "HibernateByteEncryptor")
                }
        ),
        @TypeDef(
                name = "EncryptedBigDecimal",
                typeClass = EncryptedBigDecimalAsStringType.class,
                parameters = {
                        @Parameter(name = "encryptorRegisteredName", value = "HibernateStringEncryptor")
                }
        ),
        @TypeDef(
                name = "JsonB",
                typeClass = JsonBType.class
        )
})

package com.lenda.challenge.model.postgres;

import com.lenda.challenge.hibernate.JsonBType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jasypt.hibernate4.type.EncryptedBigDecimalAsStringType;
import org.jasypt.hibernate4.type.EncryptedBinaryType;
import org.jasypt.hibernate4.type.EncryptedStringType;
