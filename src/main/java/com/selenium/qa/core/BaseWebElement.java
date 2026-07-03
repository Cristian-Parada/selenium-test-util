package com.selenium.qa.core;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


public class BaseWebElement{

	 private WebDriver driver;
	 protected WaitHelper wait;
	    /**
	     * Construye una instancia de {@code BaseWebElement} con el WebDriver indicado.
	     * Inicializa una espera explícita con un tiempo máximo de 20 segundos.
	     *
	     * @param driver instancia de {@link WebDriver} usada para interactuar con el navegador
	     */
	    public BaseWebElement(WebDriver driver) {
	    	this.driver = driver;
	    	wait = new WaitHelper(driver);
	    }

	    /**
	     * Localiza y retorna el elemento web identificado por el localizador dado.
	     *
	     * <p>Antes de localizar el elemento, espera hasta 3 intentos (con intervalos de 5 segundos)
	     * a que sea visible. Retorna {@code null} si el elemento no puede ser encontrado.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @return el {@link WebElement} localizado, o {@code null} si no se encuentra
	     */
	    public WebElement findElement(By locator) {
	        try { 
	        	wait.waitVisibleClickable(locator, 3, 5);
	            return driver.findElement(locator);
	        } catch (Exception e) {
	            System.err.println("Error al buscar elemento: " + locator);
	        }
	        return null;
	    }

	    /**
	     * Limpia el contenido de texto del elemento identificado por el localizador dado.
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     */
	    public void clearText(By locator) {
	        this.findElement(locator).clear();
	    }

	    /**
	     * Hace clic sobre el elemento identificado por el localizador dado.
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     */
	    public void click(By locator) {
	        this.findElement(locator).click();
	    }

	    /**
	     * Limpia el texto existente y escribe el valor dado en el elemento
	     * identificado por el localizador.
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @param value   texto que se escribirá en el elemento
	     * @throws InterruptedException 
	     */
	    public void sendText(By locator, String value) throws InterruptedException {
	    	wait.waitVisibleClickable(locator, 5, 2);
	        this.clearText(locator);
	        this.findElement(locator).sendKeys(value);
	    }

	    /**
	     * Obtiene el texto visible del elemento identificado por el localizador dado.
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @return el texto visible del elemento como {@link String}
	     */
	    public String getText(By locator) {
	        return findElement(locator).getText();
	    }

	    /**
	     * Selecciona una opción de un elemento {@code <select>} por su texto visible.
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento {@code <select>}
	     * @param text    texto visible de la opción a seleccionar
	     */
	    public void selectListValue(By locator, String text) {  
	    	if(text == null || text.isEmpty()) return;
	        Select option = new Select(driver.findElement(locator));
	        option.selectByVisibleText(text);
	    }

	    
	    /**
	     * Selecciona una opción de una lista {@code <ul>} buscando el {@code <li>}
	     * cuyo texto visible coincida exactamente con el valor dado.
	     *
	     * @param listLocator estrategia {@link By} usada para localizar el contenedor {@code <ul>}
	     * @param text        texto visible de la opción {@code <li>} a seleccionar
	     * @throws RuntimeException si no se encuentra ningún {@code <li>} con el texto indicado
	     */
	    public void selectFromUlList(By listLocator, String text) {
	        List<WebElement> items = driver.findElement(listLocator).findElements(By.tagName("li"));
	        for (WebElement item : items) {
	            if ((item.getText().trim()).equalsIgnoreCase(text)) {
	                item.click();
	                return;
	            }
	        }
	        throw new RuntimeException("[BaseWebElement] Opción no encontrada en la lista: " + text);
	    }
	    
	    
	    
	    
	    
	    /**
	     * Carga un archivo enviando su ruta absoluta a un elemento input de tipo file.
	     *
	     * <p>Resuelve el archivo desde la ruta indicada y valida su existencia
	     * antes de interactuar con el elemento.</p>
	     *
	     * @param rute    ruta relativa o absoluta del archivo a cargar
	     * @param locator estrategia {@link By} usada para localizar el input de archivo
	     * @throws RuntimeException si el archivo no existe en la ruta especificada
	     */
	    public void loadFile(String rute, By locator) {
	        File fileX = new File(rute);
	        if (!fileX.exists()) {
	            throw new RuntimeException("File not found: " + fileX.getAbsolutePath());
	        }
	        findElement(locator).sendKeys(fileX.getAbsolutePath());
	    }

	    /**
	     * Cambia el foco del WebDriver a la ventana del navegador en el índice especificado.
	     *
	     * <p>Los identificadores de ventana se indexan desde 0, donde 0 es la primera ventana abierta.</p>
	     *
	     * @param windows índice base cero de la ventana destino
	     */
	    public void switchWindows(int windows) {
	        Object[] windowsHandles = driver.getWindowHandles().toArray();
	        driver.switchTo().window((String) windowsHandles[windows]);
	    }

	   	
}
