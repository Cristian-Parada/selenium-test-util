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

    private static final String RUTA_BASE    = System.getProperty("user.dir") + "/src/test/resources/drivers/";
    private static final String CHROME_PATH  = RUTA_BASE + "chromedriver.exe";
    private static final String FIREFOX_PATH = RUTA_BASE + "geckodriver.exe";
    private static final String EDGE_PATH    = RUTA_BASE + "msedgedriver.exe";

 
    public static void setNavegador(Navegador navegador) {
        navegadorConfigurado = navegador;
    }

    private static WebDriver crearDriver(Navegador navegador) {
        return switch (navegador) {
            case CHROME  -> crearChromeDriver();
            case FIREFOX -> crearFirefoxDriver();
            case EDGE    -> crearEdgeDriver();
            case SAFARI -> throw new UnsupportedOperationException("Safari no está soportado");
            default -> throw new IllegalStateException("Navegador no válido: " + navegador);
        };
    }

    private static WebDriver crearChromeDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        
        // Bloquea TODAS las ventanas nuevas
        options.addArguments("--block-new-web-contents");

     // Modo incógnito (evita cookies de seguimiento, pero los anuncios siguen ahí)
     options.addArguments("--incognito");

     // Deshabilita extensiones problemáticas
     options.addArguments("--disable-extensions");
        return new ChromeDriver(options);
    }

    private static WebDriver crearFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", FIREFOX_PATH);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        return new FirefoxDriver(options);
    }

    private static WebDriver crearEdgeDriver() {
        System.setProperty("webdriver.edge.driver", EDGE_PATH);
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        return new EdgeDriver(options);
    }



    // =============================================
    // 4. MÉTODOS PÚBLICOS PRINCIPALES
    // =============================================

    /**
     * Obtiene el driver (lo crea si no existe)
     */

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(crearDriver(navegadorConfigurado));
        }
        return driverThreadLocal.get();
    }

    /**
     * Cierra el navegador y libera recursos
     */
    public static void cerrarDriver() {
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
        cerrarDriver();
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
    // =============================================
    // 5. MÉTODOS DE UTILIDAD ADICIONALES
    // =============================================

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
            System.out.println("⏱️ Esperando " + segundos + " segundos");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Limpia todas las cookies
     */
    public static void limpiarCookies() {
        getDriver().manage().deleteAllCookies();
        System.out.println("🍪 Cookies eliminadas");
    }



}
