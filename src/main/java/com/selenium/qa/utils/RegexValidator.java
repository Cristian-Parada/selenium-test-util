package com.selenium.qa.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexValidator {
	
	
	
	/**
     * Valida si un texto coincide COMPLETAMENTE con el patrón dado.
     */
    public static boolean matches(String text, String regex) {
        if (text == null || regex == null) return false;
        return Pattern.compile(regex).matcher(text).matches();
    }
	
	
    /**
     * Valida si un texto CONTIENE el patrón (coincidencia parcial).
     */
    public static boolean contains(String text, String regex) {
        if (text == null || regex == null) return false;
        return Pattern.compile(regex).matcher(text).find();
    }
	
    /**
     * Valida ignorando mayúsculas/minúsculas.
     */
    public static boolean matchesIgnoreCase(String text, String regex) {
        if (text == null || regex == null) return false;
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).matches();
    }
    
 // ── Extracción ─────────────────────────────────────────────────────────────

    /**
     * Extrae el primer resultado que coincida con el patrón.
     * Útil para sacar datos de textos de elementos web.
     */
    public static String extractFirst(String text, String regex) {
        if (text == null || regex == null) return null;
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group() : null;
    }

    /**
     * Extrae un grupo de captura específico del patrón.
     * Ej: extractGroup("Orden #4521 lista", "(\\d+)", 1) → "4521"
     */
    public static String extractGroup(String text, String regex, int group) {
        if (text == null || regex == null) return null;
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(group) : null;
    }



    // ── Utilidades adicionales ─────────────────────────────────────────────────

    /**
     * Verifica longitud mínima del texto.
     */
    public static boolean hasMinLength(String text, int min) {
        return text != null && text.trim().length() >= min;
    }

    /**
     * Verifica longitud máxima del texto.
     */
    public static boolean hasMaxLength(String text, int max) {
        return text != null && text.trim().length() <= max;
    }

    /**
     * Verifica que el texto esté dentro de un rango de longitud.
     */
    public static boolean hasLengthBetween(String text, int min, int max) {
        return hasMinLength(text, min) && hasMaxLength(text, max);
    }

}
