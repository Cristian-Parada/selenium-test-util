package com.selenium.qa.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    private static Navegador navegadorConfigurado = Navegador.CHROME;
    private static boolean headlessConfigurado = false;
    
    

    private static final String RUTA_BASE    = System.getProperty("user.dir") + "/src/test/resources/drivers/";
    
    private static final String CHROME_PATH  = RUTA_BASE + "chromedriver.exe";
    private static final String FIREFOX_PATH = RUTA_BASE + "geckodriver.exe";
    private static final String EDGE_PATH    = RUTA_BASE + "msedgedriver.exe";

 
    public static void setNavegador(Navegador navegador) {
        navegadorConfigurado = navegador;
    }

    public static void setHeadless(boolean isHeadless) {
        headlessConfigurado = isHeadless;
    }
    
    private static WebDriver crearDriver(Navegador navegador, boolean isHeadless) {
        return switch (navegador) {
            case CHROME  -> crearChromeDriver(isHeadless);
            case FIREFOX -> crearFirefoxDriver(isHeadless);
            case EDGE    -> crearEdgeDriver(isHeadless);
            case SAFARI -> throw new UnsupportedOperationException("Safari no está soportado");
            default -> throw new IllegalStateException("Navegador no válido: " + navegador);
        };
    }

    private static WebDriver crearChromeDriver(boolean isHeadless) {
        System.setProperty("webdriver.chrome.driver", CHROME_PATH);
        ChromeOptions options = new ChromeOptions();

        if (isHeadless) {
            options.addArguments("--headless=new");        // corre sin interfaz gráfica
            options.addArguments("--window-size=1920,1080"); // tamaño de viewport (obligatorio sin ventana física)
            options.addArguments("--disable-gpu");          // sin GPU, no se necesita en headless
            options.addArguments("--no-sandbox");           // requerido en entornos CI/Docker
            options.addArguments("--disable-dev-shm-usage"); // evita fallos por poco espacio en /dev/shm
        } else {
            options.addArguments("--start-maximized"); // maximiza ventana (solo aplica con interfaz visible)
        }

        options.addArguments("--disable-notifications");    // bloquea notificaciones push del navegador
        options.addArguments("--block-new-web-contents");   // bloquea pop-ups / ventanas nuevas
        options.addArguments("--incognito");                // sin cookies ni sesión previa
        options.addArguments("--disable-extensions");        // desactiva extensiones del perfil

        return new ChromeDriver(options);
    }

    private static WebDriver crearFirefoxDriver(boolean isHeadless) {
        System.setProperty("webdriver.gecko.driver", FIREFOX_PATH);
        FirefoxOptions options = new FirefoxOptions();

        if (isHeadless) {
            options.addArguments("-headless");     // corre sin interfaz gráfica
            options.addArguments("--width=1920");  // ancho del viewport
            options.addArguments("--height=1080"); // alto del viewport
        } else {
            options.addArguments("--start-maximized");
        }
        
        options.addPreference("browser.privatebrowsing.autostart", true); // equivalente a modo incógnito
        options.addPreference("dom.webnotifications.enabled", false);     // desactiva notificaciones push
        options.addPreference("dom.disable_open_during_load", true);      // bloquea pop-ups / ventanas nuevas
        options.addPreference("extensions.autoDisableScopes", 15);        // evita que se autocarguen extensiones
        return new FirefoxDriver(options);
    }

    private static WebDriver crearEdgeDriver(boolean isHeadless) {
        System.setProperty("webdriver.edge.driver", EDGE_PATH);
        EdgeOptions options = new EdgeOptions();

        if (isHeadless) {
            options.addArguments("--headless=new");         // basado en Chromium, misma sintaxis que Chrome
            options.addArguments("--window-size=1920,1080"); // tamaño de viewport
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
        } else {
            options.addArguments("--start-maximized");
        }
        // Edge es Chromium, así que acepta los mismos flags de línea de comandos que Chrome.
        options.addArguments("--disable-notifications");
        options.addArguments("--block-new-web-contents");
        options.addArguments("--inprivate");            // equivalente de Edge a "--incognito"
        options.addArguments("--disable-extensions");

        return new EdgeDriver(options);
    }

    
    
    
    /**
     * Obtiene el driver (lo crea si no existe)
     */

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(crearDriver(navegadorConfigurado,headlessConfigurado));
        }
        return driverThreadLocal.get();
    }

    /**
     * Cierra el navegador y libera recursos
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
    
    /**
     * Cierra el navegador y libera recursos
     */
    public static void closeDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.close();;
            driverThreadLocal.remove();
        }
    }

    /**
     * Navega a una URL específica
     */
    public static void sendUrl(String url) {
        getDriver().get(url);
        //System.out.println("🌐 Navegando a: " + url);
    }

    /**
     * Reinicia el navegador (útil cuando cambias de navegador)
     */
    public static void reiniciarDriver() {
        quitDriver();
        getDriver();
    }

    /**
     * Cambia el navegador en tiempo de ejecución
     */
    public static void cambiarNavegador(Navegador nuevoNavegador) {
        navegadorConfigurado = nuevoNavegador;
        reiniciarDriver();
        System.out.println("🌍 Navegador cambiado a: " + nuevoNavegador);
    }

    /**
     * Obtiene el navegador actualmente configurado
     */
    public static Navegador getNavegadorActual() {
        return navegadorConfigurado;
    }

    /**
     * Obtiene el título de la página actual
     */
    public static String getTitulo() {
        return getDriver().getTitle();
    }

    /**
     * Obtiene la URL actual
     */
    public static String getUrlActual() {
        return getDriver().getCurrentUrl();
    }

    /**
     * Refresca la página actual
     */
    public static void refrescarPagina() {
        getDriver().navigate().refresh();
        System.out.println("🔄 Página refrescada");
    }

    /**
     * Navega hacia atrás en el historial
     */
    public static void volverAtras() {
        getDriver().navigate().back();
        System.out.println("◀️ Navegando hacia atrás");
    }

    /**
     * Navega hacia adelante en el historial
     */
    public static void volverAdelante() {
        getDriver().navigate().forward();
        System.out.println("▶️ Navegando hacia adelante");
    }

    /**
     * Espera explícita (útil para condiciones específicas)
     */
    public static void esperarSegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
            System.out.println(" Esperando " + segundos + " segundos");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Limpia todas las cookies
     */
    public static void limpiarCookies() {
        getDriver().manage().deleteAllCookies();
        System.out.println(" Cookies eliminadas");
    }



}
