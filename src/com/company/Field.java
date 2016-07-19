package com.company;

import java.util.*;

class Field {
    private List<Long> field;

    Field() {
        this.field = new ArrayList<>();
    }

    List<Long> getField() {
        return field;
    }

    void setField(Long field) {
        if (Number.class.isAssignableFrom(field.getClass()))
            this.field.add(field);
        else
            System.out.println("Some wrong number got from DB = " + field);
    }
}