package com.selenium.qa.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
	     * Localiza y retorna todos los elementos web identificados por el localizador dado.
	     *
	     * <p>Antes de localizar los elementos, espera hasta 3 intentos (con intervalos de 5 segundos)
	     * a que sean visibles. Retorna una lista vacía si no se encuentran elementos o si ocurre
	     * una excepción durante la búsqueda.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar los elementos
	     * @return lista de {@link WebElement} encontrados, o una lista vacía si no se encuentran
	     */
	    public List<WebElement> findElements(By locator){

	    	  try { 
		        	wait.waitVisibleClickable(locator, 3, 5);
		        	return driver.findElements(locator);
		        } catch (Exception e) {
		            System.err.println("Error al buscar elemento: " + locator);
		        }
		        return new ArrayList<WebElement>();

	    }

	   
	    /**
	     * Verifica si el elemento identificado por el localizador está visible y habilitado en la página.
	     *
	     * <p>Combina las comprobaciones {@code isDisplayed()} e {@code isEnabled()} del elemento.
	     * Este método localiza el elemento dos veces; si el elemento es dinámico, considere
	     * guardarlo en una variable antes de llamarlo.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @return {@code true} si el elemento está visible y habilitado, {@code false} en caso contrario
	     */
	    public boolean isDisplay(By locator) {
	    	return findElement(locator).isDisplayed() && findElement(locator).isEnabled() ;
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

	    /**
	     * Abre un dropdown personalizado y selecciona la opción cuyo texto coincida con el valor dado,
	     * usando JavaScript para realizar los clics y evitar problemas con elementos no nativos.
	     *
	     * <p>Primero hace clic en el disparador del dropdown para abrirlo, luego busca la opción
	     * mediante un XPath que coincide con etiquetas {@code <a>} o {@code <span>} cuyo texto
	     * sea exactamente igual a {@code optionText}.</p>
	     *
	     * @param dropdownTrigger estrategia {@link By} usada para localizar el botón o elemento
	     *                        que abre el dropdown
	     * @param optionText      texto exacto de la opción a seleccionar dentro del dropdown
	     */
	    public void selectFromDropdownJs(By dropdownTrigger, String optionText) {
	        WebElement trigger = driver.findElement(dropdownTrigger);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);

	        WebElement option = driver.findElement(
	            By.xpath("//a[text()='" + optionText + "'] | //span[text()='" + optionText + "']"));
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
	    }

	    /**
	     * Verifica si el logo identificado por el localizador es válido:
	     * está visible en la página y su imagen ha cargado correctamente.
	     *
	     * <p>Utiliza JavaScript para comprobar que la propiedad {@code complete} del elemento
	     * {@code <img>} es {@code true} y que su {@code naturalWidth} es mayor que 0,
	     * lo que indica que la imagen se descargó exitosamente.</p>
	     *
	     * @param logoLocator estrategia {@link By} usada para localizar el elemento de imagen del logo
	     * @return {@code true} si el logo es visible y la imagen cargó correctamente,
	     *         {@code false} en caso contrario
	     */
	    public boolean isLogoValid(By logoLocator) {
	        WebElement logo = driver.findElement(logoLocator);

	        boolean isVisible = logo.isDisplayed();
	        boolean isLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript(
	            "return arguments[0].complete && arguments[0].naturalWidth > 0;", logo);

	        return isVisible && isLoaded;
	    }

	    /**
	     * Verifica si un gráfico en {@code <canvas>} tiene contenido dibujado.
	     *
	     * <p>Compara el canvas real contra uno en blanco del mismo tamaño,
	     * ya que Selenium no puede leer datos internos de un {@code <canvas>}.</p>
	     *
	     * @param canvasLocator localizador del {@code <canvas>} a validar
	     * @return {@code true} si el gráfico tiene contenido dibujado, {@code false} si está vacío
	     */
	    public boolean isCanvasRendered(By canvasLocator) {
	        WebElement canvas = driver.findElement(canvasLocator);
	        String dataURL = (String) ((JavascriptExecutor) driver).executeScript(
	            "return arguments[0].toDataURL();", canvas);

	        String blankCanvas = (String) ((JavascriptExecutor) driver).executeScript(
	            "var blank = document.createElement('canvas');" +
	            "blank.width = arguments[0].width;" +
	            "blank.height = arguments[0].height;" +
	            "return blank.toDataURL();", canvas);

	        return !dataURL.equals(blankCanvas);
	    }

	    /**
	     * Baja o sube la página completa usando scroll.
	     * Usa un número positivo para bajar y uno negativo para subir.
	     *
	     * <p>Ejemplo: {@code scrollVertical(300)} baja 300px la página.</p>
	     *
	     * @param pixels cuánto se desplaza el scroll
	     */
	    public void scrollVertical(int pixels) {
	        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", pixels);
	    }

	    /**
	     * Mueve la página hacia la izquierda o derecha.
	     * Usa un número positivo para ir a la derecha y uno negativo para ir a la izquierda.
	     *
	     * <p>Ejemplo: {@code scrollHorizontal(200)} mueve la página 200px a la derecha.</p>
	     *
	     * @param pixels cuánto se desplaza el scroll
	     */
	    public void scrollHorizontal(int pixels) {
	        ((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], 0);", pixels);
	    }

	    /**
	     * Lleva la pantalla hasta donde está un elemento, como si el usuario
	     * bajara manualmente hasta encontrarlo.
	     *
	     * <p>Ejemplo: {@code scrollToElement(By.id("btnSave"))} baja hasta el botón Guardar.</p>
	     *
	     * @param locator elemento al que se quiere llegar
	     */
	    public void scrollToElement(By locator) {
	        WebElement element = driver.findElement(locator);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	    }

	    /**
	     * Hace scroll dentro de un contenedor puntual (por ejemplo una tabla o
	     * un widget que tiene su propia barra de scroll interna).
	     *
	     * <p>Ejemplo: {@code scrollWithinElement(By.id("employeeTable"), 0, 150)} baja 150px
	     * dentro de la tabla, sin mover el resto de la página.</p>
	     *
	     * @param containerLocator contenedor que tiene su propio scroll
	     * @param x desplazamiento horizontal
	     * @param y desplazamiento vertical
	     */
	    public void scrollWithinElement(By containerLocator, int x, int y) {
	        WebElement container = driver.findElement(containerLocator);
	        ((JavascriptExecutor) driver).executeScript(
	            "arguments[0].scrollLeft += arguments[1]; arguments[0].scrollTop += arguments[2];",
	            container, x, y);
	    }

	    /**
	     * Baja automáticamente hasta el final de un contenedor con scroll propio,
	     * sin necesidad de indicar cuántos píxeles.
	     *
	     * <p>Ejemplo: {@code scrollToBottomOfElement(By.xpath("//div[@class='widget-body']"))}
	     * baja hasta el final del widget indicado.</p>
	     *
	     * @param containerLocator contenedor que tiene su propio scroll
	     */
	    public void scrollToBottomOfElement(By containerLocator) {
	        WebElement container = driver.findElement(containerLocator);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", container);
	    }


}
