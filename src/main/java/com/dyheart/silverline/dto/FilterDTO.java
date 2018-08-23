package com.dyheart.silverline.dto;

import java.io.Serializable;


public class FilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logic;
    private String field;
    private String operator;
    private String value;

    public FilterDTO() {
        super();
    }

    public FilterDTO(String logic, String field, String operator, String value) {
        super();
        this.logic = logic;
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public String getLogic() {
        return logic;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FilterDTO [logic=" + logic + ", field=" + field + ", operator="
                + operator + ", value=" + value + "]";
    }
}
