package com.lenda.challenge.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.lenda.challenge.spring.SpringBeanFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Set;

public class JsonBType implements UserType {

    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = SpringBeanFactory.getBean(ObjectMapper.class);
        }
        return objectMapper;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.JAVA_OBJECT};
    }

    @Override
    public Class returnedClass() {
        return JsonEntity.class;
    }

    @Override
    public boolean equals(Object a, Object b) throws HibernateException {
        try {
            if (a != null && b != null && Collection.class.isAssignableFrom(a.getClass()) && Collection.class.isAssignableFrom(b.getClass())) {
                Set<Object> xs = Sets.newHashSet();
                Set<Object> ys = Sets.newHashSet();
                for (Object o : Collection.class.cast(a)) {
                    xs.add(getObjectMapper().valueToTree(o));
                }
                for (Object o : Collection.class.cast(b)) {
                    ys.add(getObjectMapper().valueToTree(o));
                }
                return xs.equals(ys);
            } else {
                return (a == b) || (a != null && b != null && getObjectMapper().valueToTree(a).equals(getObjectMapper().valueToTree(b)));
            }
        } catch (Exception e) {
            throw new HibernateException("Error while doing equal check.");
        }
    }

    @Override
    public int hashCode(Object obj) throws HibernateException {
        try {
            if (obj == null) {
                return 0;
            }
            return getObjectMapper().valueToTree(obj).hashCode();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String json = rs.getString(names[0]);
        if (json == null) {
            return null;
        }
        try {
            return parseJson(json.getBytes("UTF-8"));
        } catch (IOException e) {
            try {
                return parseJson(String.class.cast(new ObjectInputStream(new ByteArrayInputStream(rs.getBytes(names[0]))).readObject()).getBytes());
            } catch (Exception e2) {
                throw new HibernateException("Error while reading JSON data.");
            }
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            try {
                st.setObject(index, getObjectMapper().writeValueAsString(value), Types.OTHER);
            } catch (JsonProcessingException e) {
                throw new HibernateException("Error while writing JSON data.");
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        try {
            return parseJson(getObjectMapper().writeValueAsBytes(value));
        } catch (IOException e) {
            throw new HibernateException("Error while deep copying JSON data.");
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return (original == null) ? null : deepCopy(original);
    }

    private Object parseJson(byte[] json) throws IOException {
        if (json == null) {
            return null;
        }
        return getObjectMapper().readValue(json, returnedClass());
    }
}
