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

    void addField(Long field) {
        this.field.add(field);
    }
}