package com.selenium.qa.core;


import java.time.Duration;
import java.util.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitHelper{

	  	private WebDriver driver;
	    private WebDriverWait wait;
	    /**
	     * Construye una instancia de {@code BaseWebElement} con el WebDriver indicado.
	     * Inicializa una espera explícita con un tiempo máximo de 20 segundos.
	     *
	     * @param driver instancia de {@link WebDriver} usada para interactuar con el navegador
	     */
	    public WaitHelper(WebDriver driver) {
	    	this.driver = driver;
	    	this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    }
	    
	  
	    
	    /**
	     * Espera a que un elemento sea clickeable, reintentando hasta {@code maxAttempts} veces.
	     *
	     * <p>Entre cada intento fallido, el método pausa {@code minutes} segundos antes de reintentar.
	     * Si todos los intentos fallan, la ejecución continúa sin lanzar excepción.</p>
	     *
	     * @param locator     estrategia {@link By} usada para localizar el elemento
	     * @param maxAttempts número máximo de intentos de reintento
	     * @param minutes     tiempo de espera en segundos entre cada reintento
	     * @throws InterruptedException si el hilo es interrumpido durante el sleep
	     */
	    public void waitElementClickable(By locator, int maxAttempts, int minutes) throws InterruptedException {
	        for (int i = 0; i < maxAttempts; i++) {
	            try {
	                wait.until(ExpectedConditions.elementToBeClickable(locator));
	                return;
	            } catch (TimeoutException e) {
	                if (i < maxAttempts - 1) {
	                    try {
	                        Thread.sleep(minutes * 1000L);
	                    } catch (InterruptedException ie) {
	                        Thread.currentThread().interrupt();
	                    }
	                }
	            }
	        }
	    }

	    /**
	     * Espera a que un elemento sea visible, reintentando hasta {@code maxAttempts} veces.
	     *
	     * <p>Entre cada intento fallido, el método pausa {@code minutes} segundos antes de reintentar.
	     * Si todos los intentos fallan, la ejecución continúa sin lanzar excepción.</p>
	     *
	     * @param locator     estrategia {@link By} usada para localizar el elemento
	     * @param maxAttempts número máximo de intentos de reintento
	     * @param minutes     tiempo de espera en segundos entre cada reintento
	     * @throws InterruptedException si el hilo es interrumpido durante el sleep
	     */
	    public void waitVisibleClickable(By locator, int maxAttempts, int seconds) throws InterruptedException {
	        for (int i = 0; i < maxAttempts; i++) {
	            try {
	                wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	                return;
	            } catch (TimeoutException e) {
	                if (i < maxAttempts - 1) {
	                    try {
	                        Thread.sleep(seconds * 1000L);
	                    } catch (InterruptedException ie) {
	                        Thread.currentThread().interrupt();
	                    }
	                }
	            }
	        }
	    }

	    /**
	     * Espera a que un elemento aparezca usando una estrategia {@link FluentWait},
	     * consultando a intervalos fijos e ignorando excepciones transitorias comunes.
	     *
	     * <p>Ignora {@link NoSuchElementException} y {@link StaleElementReferenceException}
	     * durante el sondeo. Lanza una excepción con mensaje descriptivo si el tiempo máximo
	     * se agota sin encontrar el elemento.</p>
	     *
	     * @param locator        estrategia {@link By} usada para localizar el elemento
	     * @param maxTimeSeconds tiempo máximo de espera en segundos
	     * @param intervalMillis intervalo de sondeo en milisegundos entre cada intento
	     */
	    
	    public void waitFluentWait(By locator, int maxTimeSeconds, int intervalMillis) {
	        FluentWait<WebDriver> wait = new FluentWait<>(driver)
	                .withTimeout(Duration.ofSeconds(maxTimeSeconds))
	                .pollingEvery(Duration.ofMillis(intervalMillis))
	                .ignoring(NoSuchElementException.class)
	                .ignoring(StaleElementReferenceException.class)
	                .withMessage("El elemento no se encontró luego de esperar " + maxTimeSeconds + " segundos");

	        wait.until(driver -> driver.findElement(locator));
	    }

	    /**
	     * Espera a que el elemento tenga texto visible y luego limpia su contenido.
	     *
	     * <p>Primero aguarda a que el elemento sea visible, luego espera a que su texto
	     * no esté vacío antes de proceder con la limpieza.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     */
	    public void waitClearText(By locator) {
	        WebElement campo = wait.until(
	                ExpectedConditions.visibilityOfElementLocated(locator)
	        );
	        wait.until(driver -> !campo.getText().trim().isEmpty());
	        campo.clear();
	    }

	    /**
	     * Espera hasta que el elemento tenga texto no vacío y lo retorna.
	     *
	     * <p>Reintenta continuamente dentro del tiempo de espera definido hasta que
	     * el texto del elemento sea no nulo y no esté en blanco.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @return el texto no vacío del elemento como {@link String}
	     * @throws RuntimeException si el tiempo de espera se agota sin obtener texto válido
	     */
	    public  String waitForNonEmptyText(By locator) {
	        try {
	            return wait.until(driver -> {
	                String text = driver.findElement(locator).getText();
	                return (text != null && !text.trim().isEmpty()) ? text : null;
	            });
	        } catch (Exception e) {
	            throw new RuntimeException("Error: no se obtuvo texto en el elemento: " + locator);
	        }
	    }

	    /**
	     * Espera a que el texto del elemento se estabilice y retorna su valor final.
	     *
	     * <p>Realiza dos lecturas separadas por {@code seconds} segundos. Si ambas lecturas
	     * producen el mismo texto no vacío, se considera estable y se retorna.
	     * Si el texto sigue cambiando, el método reintenta dentro del tiempo de espera definido.</p>
	     *
	     * @param locator estrategia {@link By} usada para localizar el elemento
	     * @param seconds tiempo en segundos entre las dos lecturas de verificación
	     * @return el texto estabilizado del elemento como {@link String}
	     * @throws RuntimeException si el tiempo de espera se agota sin que el texto se estabilice
	     */
	    public  String waitForStableText(By locator, int seconds) {
	        try {
	            return wait.until(driver -> {
	                String firstCheck =  driver.findElement(locator).getText();

	                if (firstCheck == null || firstCheck.trim().isEmpty()) {
	                    return null;
	                }

	                try {
	                    Thread.sleep(seconds * 1000L);
	                } catch (InterruptedException e) {
	                    Thread.currentThread().interrupt();
	                }

	                String secondCheck =  driver.findElement(locator).getText();
	                if (firstCheck.equals(secondCheck)) {
	                    return secondCheck;
	                }

	                return null;
	            });
	        } catch (Exception e) {
	            throw new RuntimeException("Error: el texto del elemento no se estabilizó: " + locator);
	        }
	    }
	    
	    /**
	     * Espera a que el texto de un elemento coincida exactamente con el esperado.
	     *
	     * @param locator localizador del elemento
	     * @param expectedText texto exacto esperado
	     * @return true si el texto coincide, false en caso contrario
	     */
	    public boolean waitForExactText(By locator, String expectedText) {
	        try {
	            return wait.until(ExpectedConditions.textToBe(locator, expectedText));
	        } catch (TimeoutException e) {
	            return false;
	        }
	    } 

	    /**
	     * Espera a que un elemento contenga un texto específico.
	     *
	     * @param locator localizador del elemento
	     * @param partialText texto parcial a buscar
	     * @return true si el texto está presente, false en caso contrario
	     */
	    public String waitForTextContains(By locator, String partialText) {
	        try {
	            //WebDriverWait tempWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
	            return wait.until(d -> {
	                String text = d.findElement(locator).getText();
	                if( text != null && text.contains(partialText)) {
	                	return text;
	                }
					return text;
	            });
	        } catch (Exception e) {
	            throw new RuntimeException("Fallo el metodo ");
	        }
	    }
	    /**
	     * Espera a que el valor (atributo value) de un elemento coincida con el esperado.
	     *
	     * @param locator localizador del elemento
	     * @param expectedValue valor esperado
	     * @return true si el valor coincide, false en caso contrario
	     */
	    public  boolean waitForValueToBe(By locator, String expectedValue) {
	        try {
	            return wait.until(ExpectedConditions.textToBePresentInElementValue(locator, expectedValue));
	        } catch (TimeoutException e) {
	            return false;
	        }
	    }

	    /**
	     * Espera a que un elemento esté presente en el DOM (no necesariamente visible).
	     *
	     * @param locator localizador del elemento
	     * @return WebElement encontrado
	     * @throws TimeoutException si el elemento no aparece
	     */
	    public  WebElement waitForElementPresence(By locator) {
	        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	    }

	    /**
	     * Obtiene y espera a que un atributo tenga un valor específico.
	     *
	     * @param locator localizador del elemento
	     * @param attribute nombre del atributo
	     * @param expectedValue valor esperado del atributo
	     * @return true si el atributo tiene el valor esperado
	     */
	    public  boolean waitForAttributeToBe(By locator, String attribute, String expectedValue) {
	        try {
	            return wait.until(ExpectedConditions.attributeToBe(locator, attribute, expectedValue));
	        } catch (TimeoutException e) {
	            return false;
	        }
	    }

	    /**
	     * Obtiene y espera a que un atributo contenga un valor parcial.
	     *
	     * @param locator localizador del elemento
	     * @param attribute nombre del atributo
	     * @param partialValue valor parcial a buscar en el atributo
	     * @return true si el atributo contiene el valor parcial
	     */
	    public  boolean waitForAttributeContains(By locator, String attribute, String partialValue) {
	        try {
	            return wait.until(ExpectedConditions.attributeContains(locator, attribute, partialValue));
	        } catch (TimeoutException e) {
	            return false;
	        }
	    }
	    
	    /**
	     * Espera a que un toast o notificación aparezca y obtiene su texto.
	     *
	     * @param toastLocator localizador del elemento toast
	     * @return texto del toast o null si no aparece
	     */
	    public  String waitForAlertTextNotification(By locator) {
	        try {
	            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	            return element.getText().trim();
	        } catch (TimeoutException e) {
	            return null;
	        }
	    }

	    /**
	     * Espera a que un toast o notificación aparezca y luego desaparezca.
	     *
	     * @param toastLocator localizador del elemento toast
	     * @return true si el toast apareció y desapareció
	     */
	    public  boolean waitForAlertAppearAndDisappear(By locator) {
	        try {
	            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	        } catch (TimeoutException e) {
	            return false;
	        }
	    }
	    
}
