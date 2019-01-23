package com.hms.hotelmanagementsystem.utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Pair {

    public Integer first,second;

    @JsonCreator
    public Pair(@JsonProperty("first") Integer first, @JsonProperty("second") Integer second) {
        this.first = first;
        this.second = second;
    }



}
