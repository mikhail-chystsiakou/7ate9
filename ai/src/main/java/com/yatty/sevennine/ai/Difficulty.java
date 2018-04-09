package com.yatty.sevennine.ai;

import java.util.Random;

public enum Difficulty {
    EASY ("Petya", "Vanya", "Vasya"),
    MEDIUM ("Petr", "Ivan", "Vasiliy"),
    HARD ("Petr-Petrovich", "Ivan-Vasilyevich", "Vasiliy-Ivanovich");
    protected String[] names;
    
    Difficulty(String ... names) {
        this.names = names;
    }
    
    public String getName() {
        int nameIndex = new Random().nextInt() % names.length;
        return names[nameIndex];
    }
}
