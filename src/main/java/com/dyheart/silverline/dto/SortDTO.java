package com.dyheart.silverline.dto;

import java.io.Serializable;


public class SortDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String field;
    private String dir;

    public SortDTO() {
    }

    public SortDTO(String field, String dir) {
        this.field = field;
        this.dir = dir;
    }

    public SortDTO(String field) {
        this.field = field;
        this.dir = "asc"; // default sorting direction is assumed ascending
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return "SortDTO [field=" + field + ", dir=" + dir + "]";
    }
}
