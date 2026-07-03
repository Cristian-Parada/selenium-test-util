package com.selenium.qa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoadProperties {
	
	private static final Map<String, Properties> cache = new HashMap<>();

    public static String getProperties(String fileName, String key) {

        
        if (!cache.containsKey(fileName)) {
            Properties props = new Properties();
            
            // Busca el archivo en las posibles carpetas:
            // 1. testdata/page/
            // 2. testdata/test/
            // 3. testdata/ (raíz)
            InputStream input = LoadProperties.class.getClassLoader()
                    .getResourceAsStream("testdata/page/" + fileName + ".properties");
            
            if (input == null) {
                input = LoadProperties.class.getClassLoader()
                        .getResourceAsStream("testdata/test/" + fileName + ".properties");
            }
            
            if (input == null) {
                input = LoadProperties.class.getClassLoader()
                        .getResourceAsStream("testdata/" + fileName + ".properties");
            }

            if (input == null) {
                throw new RuntimeException("Archivo no encontrado: " + fileName);
            }
            
            try (InputStream in = input) {
                props.load(in);
                cache.put(fileName, props);
            } catch (IOException e) {
                throw new RuntimeException("Error al leer " + fileName + ": " + e.getMessage());
            }
        }

        String value = 
        		cache.get(fileName).//obtiene el Properties del archivo
        		getProperty(key);// busca la clave dentro de ese archivo
        if (value == null) {
            throw new RuntimeException("Clave '" + key + "' no encontrada en: " + fileName);
        }
        return value.trim();
    }

    
    
    public static int getInt(String fileName, String key) {
        return Integer.parseInt(getProperties(fileName, key));
    }

    public static boolean getBoolean(String fileName, String key) {
        return Boolean.parseBoolean(getProperties(fileName, key));
    }

    public static double getDouble(String fileName, String key) {
        return Double.parseDouble(getProperties(fileName, key));
    }
	
    public static String[] getStringArray(String fileName, String key) {
        return getProperties(fileName, key).split("\\s*,\\s*");
    }

}
