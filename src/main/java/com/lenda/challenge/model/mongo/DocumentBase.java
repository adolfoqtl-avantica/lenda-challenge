package com.lenda.challenge.model.mongo;

import javax.persistence.Id;
import java.math.BigInteger;

public abstract class DocumentBase {

    @Id
    private BigInteger id;

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || !(this.id == null || obj == null || !(this.getClass().equals(obj.getClass())))
                && this.id.equals(DocumentBase.class.cast(obj).getId());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
