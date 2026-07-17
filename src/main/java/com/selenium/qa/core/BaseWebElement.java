package com.selenium.qa.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Utilidad sencilla para interactuar con elementos web en pruebas Selenium.
 * Centraliza acciones comunes como buscar elementos, escribir texto, hacer clic y mover el scroll.
 */
public class BaseWebElement {

    private WebDriver driver;
    protected WaitHelper wait;

    /**
     * Crea una instancia usando el WebDriver recibido y prepara la espera automática.
     *
     * @param driver instancia de WebDriver para interactuar con el navegador
     */
    public BaseWebElement(WebDriver driver) {
        this.driver = driver;
        wait = new WaitHelper(driver);
    }

    /**
     * Busca un elemento y espera a que sea visible antes de devolverlo.
     * Si no aparece, devuelve null.
     *
     * @param locator estrategia usada para encontrar el elemento
     * @return el elemento encontrado o null si no existe
     */
    public WebElement findElement(By locator) {
        try {
            wait.waitElementVisibleWithRetry(locator, 3, 5);
            return driver.findElement(locator);
        } catch (Exception e) {
            System.err.println("Error al buscar elemento: " + locator);
        }
        return null;
    }

    /**
     * Busca varios elementos y espera a que estén visibles.
     * Si no encuentra ninguno, devuelve una lista vacía.
     *
     * @param locator estrategia usada para encontrar los elementos
     * @return lista con los elementos encontrados
     */
    public List<WebElement> findElements(By locator) {
        try {
            wait.waitElementVisibleWithRetry(locator, 3, 5);
            return driver.findElements(locator);
        } catch (Exception e) {
            System.err.println("Error al buscar elemento: " + locator);
        }
        return new ArrayList<WebElement>();
    }

    /**
     * Indica si el elemento existe, se ve y está habilitado.
     *
     * @param locator estrategia usada para encontrar el elemento
     * @return true si está visible y habilitado, false en caso contrario
     */
    public boolean isDisplay(By locator) {
        return findElement(locator).isDisplayed() && findElement(locator).isEnabled();
    }

    /**
     * Borra el contenido actual del campo indicado.
     *
     * @param locator estrategia usada para encontrar el elemento
     */
    public void clearText(By locator) {
        this.findElement(locator).clear();
    }

    /**
     * Hace clic en el elemento indicado.
     *
     * @param locator estrategia usada para encontrar el elemento
     */
    public void click(By locator) {
        this.findElement(locator).click();
    }

    /**
     * Borra el texto actual y escribe uno nuevo en el campo.
     *
     * @param locator estrategia usada para encontrar el elemento
     * @param value texto que se escribirá
     * @throws InterruptedException si la espera es interrumpida
     */
    public void sendText(By locator, String value) throws InterruptedException {
        wait.waitElementVisibleWithRetry(locator, 5, 2);
        this.clearText(locator);
        this.findElement(locator).sendKeys(value);
    }

    /**
     * Obtiene el texto visible del elemento.
     *
     * @param locator estrategia usada para encontrar el elemento
     * @return el texto mostrado en pantalla
     */
    public String getText(By locator) {
        return findElement(locator).getText();
    }

    /**
     * Cambia el foco del navegador a otra ventana abierta por su índice.
     *
     * @param windows índice de la ventana destino, empezando en 0
     */
    public void switchWindows(int windows) {
        Object[] windowsHandles = driver.getWindowHandles().toArray();
        driver.switchTo().window((String) windowsHandles[windows]);
    }

    /**
     * Selecciona una opción dentro de una lista tipo UL/LI por su texto.
     *
     * @param listLocator estrategia usada para encontrar la lista
     * @param text texto visible de la opción a elegir
     */
    public void selectFromUlList(By listLocator, String text) {
        List<WebElement> items = driver.findElement(listLocator).findElements(By.tagName("li"));
        for (WebElement item : items) {
            if (item.getText().trim().equalsIgnoreCase(text)) {
                item.click();
                return;
            }
        }
        throw new RuntimeException("[BaseWebElement] Opción no encontrada en la lista: " + text);
    }


    /**
     * Abre un dropdown personalizado y selecciona una opción con JavaScript.
     *
     * @param dropdownTrigger elemento que abre el dropdown
     * @param optionText texto de la opción que se desea elegir
     */
    public void selectFromDropdownJs(By dropdownTrigger, String optionText) {
        WebElement trigger = driver.findElement(dropdownTrigger);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);

        WebElement option = driver.findElement(
                By.xpath("//a[text()='" + optionText + "'] | //span[text()='" + optionText + "']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    /**
     * Escribe el texto en el campo y presiona Enter para seleccionar la opción.
     * Ideal para selects con muchas opciones (como Supervisor o Job Title),
     * donde escribir es más rápido que buscar en la lista completa.
     *
     * <pre>{@code
     * typeAndSelectOption(supervisorField, "John Smith");
     * }</pre>
     */
    public void typeAndSelectOption(By dropdownTrigger, String optionText) {
        WebElement trigger = driver.findElement(dropdownTrigger);
        trigger.click();
        trigger.sendKeys(optionText);
        trigger.sendKeys(Keys.ENTER);
    }

    /**
     * Abre el dropdown y baja con la flecha del teclado hasta la posición indicada,
     * luego confirma con Enter. Útil cuando el texto de la opción no es confiable,
     * pero sabemos en qué posición está.
     *
     * <pre>{@code
     * selectOptionByKeyboardPosition(statusDropdown, 2); // baja 2 opciones y confirma
     * }</pre>
     */
    public void selectOptionByKeyboardPosition(By dropdownTrigger, int positionsDown) {
        WebElement trigger = driver.findElement(dropdownTrigger);
        trigger.click();
        for (int i = 0; i < positionsDown; i++) {
            trigger.sendKeys(Keys.ARROW_DOWN);
        }
        trigger.sendKeys(Keys.ENTER);
    }

    /**
     * Selecciona una opción simulando el movimiento del mouse (hover + click),
     * en vez de hacer click directo. Sirve como alternativa cuando el click
     * normal o por JS no logra activar la selección.
     *
     * <pre>{@code
     * selectOptionWithActions(statusTrigger, By.xpath("//span[text()='Enabled']"));
     * }</pre>
     */
    public void selectOptionWithActions(By dropdownTrigger, By optionLocator) {
        driver.findElement(dropdownTrigger).click();
        WebElement option = wait.waitForElementClickable(optionLocator);
        new Actions(driver).moveToElement(option).click().perform();
    }


    /**
     * Abre el dropdown y busca la opción comparando el texto directamente,
     * en vez de armar un XPath con el texto adentro. Es más seguro cuando
     * el texto de la opción puede traer comillas u otros caracteres raros.
     *
     * <pre>{@code
     * selectOptionFromList(statusTrigger, By.cssSelector("div[role='listbox'] span"), "Enabled");
     * }</pre>
     */
    public void selectOptionFromList(By dropdownTrigger, By optionsContainer, String optionText) {
        driver.findElement(dropdownTrigger).click();

        List<WebElement> options = wait.getWait().until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(optionsContainer)
        );
        options.stream()
            .filter(option -> option.getText().trim().equalsIgnoreCase(optionText))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("[BaseWebElement] Opción no encontrada: " + optionText))
            .click();
    }
    
    
    /**
     * Carga un archivo en un input de tipo file, validando que exista antes de enviarlo.
     *
     * @param rute ruta relativa o absoluta del archivo
     * @param locator estrategia usada para encontrar el input de archivo
     */
    public void loadFile(String rute, By locator) {
        File fileX = new File(rute);
        if (!fileX.exists()) {
            throw new RuntimeException("File not found: " + fileX.getAbsolutePath());
        }
        findElement(locator).sendKeys(fileX.getAbsolutePath());
    }
    
    /**
     * Verifica si un logo se ve correctamente y si su imagen ya cargó.
     *
     * @param logoLocator estrategia usada para encontrar el logo
     * @return true si el logo es visible y su imagen está cargada
     */
    public boolean isLogoValid(By logoLocator) {
        WebElement logo = driver.findElement(logoLocator);

        boolean isVisible = logo.isDisplayed();
        boolean isLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].complete && arguments[0].naturalWidth > 0;", logo);

        return isVisible && isLoaded;
    }

    /**
     * Comprueba si un canvas tiene contenido real y no está vacío.
     *
     * @param canvasLocator estrategia usada para encontrar el canvas
     * @return true si el gráfico o dibujo está presente
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
     * Desplaza la página hacia arriba o hacia abajo.
     *
     * @param pixels cantidad de píxeles a mover
     */
    public void scrollVertical(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", pixels);
    }

    /**
     * Desplaza la página hacia la izquierda o hacia la derecha.
     *
     * @param pixels cantidad de píxeles a mover
     */
    public void scrollHorizontal(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], 0);", pixels);
    }

    /**
     * Lleva la vista hasta que el elemento quede visible en pantalla.
     *
     * @param locator estrategia usada para encontrar el elemento
     */
    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Desplaza el contenido de un contenedor que tiene su propio scroll.
     *
     * @param containerLocator estrategia usada para encontrar el contenedor
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
     * Lleva el scroll del contenedor hasta el final.
     *
     * @param containerLocator estrategia usada para encontrar el contenedor
     */
    public void scrollToBottomOfElement(By containerLocator) {
        WebElement container = driver.findElement(containerLocator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", container);
    }
}
