package at.htl.jdbcfileimport.model;

import java.util.Objects;

public class Person {

    private String name;
    private String city;
    private String house;


    @Override
    public String toString() {
        return String.format("%s - house: %s, city: %s", name, house, city);
    }

}
