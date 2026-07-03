package com.selenium.qa.core;

public enum Navegador {

    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;  


    public static boolean esSoportado(String navegador) {
        try {
            valueOf(navegador.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}



