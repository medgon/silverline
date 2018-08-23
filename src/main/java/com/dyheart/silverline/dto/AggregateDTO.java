package com.dyheart.silverline.dto;

import java.io.Serializable;


public class AggregateDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String field;
    private String aggregate;

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public String getAggregate() {
        return aggregate;
    }

    @Override
    public String toString() {
        return "AggregateDTO [field=" + field + ", aggregate=" + aggregate + "]";
    }
}
