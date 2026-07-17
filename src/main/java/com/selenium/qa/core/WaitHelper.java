package com.selenium.qa.core;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase utilitaria para manejar diferentes tipos de esperas en Selenium WebDriver.
 *
 * <p>Cubre las condiciones más comunes: visibilidad, clickeabilidad, presencia,
 * texto, atributos, alertas, URL, frames y ventanas. Internamente utiliza
 * {@link WebDriverWait} y {@link FluentWait}.</p>
 *
 * <p>El tiempo máximo de espera por defecto es de {@value #DEFAULT_TIMEOUT_SECONDS} segundos,
 * configurable mediante el constructor de dos parámetros.</p>
 *
 * @version 2.0
 * @since 1.0
 */
public class WaitHelper {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT_SECONDS = 20;

    /**
     * Construye una instancia de {@code WaitHelper} con el WebDriver indicado.
     * Inicializa una espera explícita con un tiempo máximo de 20 segundos.
     *
     * @param driver instancia de {@link WebDriver} usada para interactuar con el navegador
     */
    public WaitHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }

    /**
     * Construye una instancia de {@code WaitHelper} con un tiempo de espera personalizado.
     *
     * @param driver instancia de {@link WebDriver} usada para interactuar con el navegador
     * @param timeoutSeconds tiempo máximo de espera en segundos
     */
    public WaitHelper(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ==================== MÉTODOS BÁSICOS DE ESPERA ====================

    /**
     * Espera a que un elemento esté presente en el DOM (no necesariamente visible).
     *
     * @param locator localizador del elemento
     * @return WebElement encontrado
     * @throws TimeoutException si el elemento no aparece en el tiempo máximo de espera
     */
    public WebElement waitForElementPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Espera a que un elemento sea visible en la página.
     *
     * @param locator localizador del elemento
     * @return WebElement visible encontrado
     * @throws TimeoutException si el elemento no se vuelve visible
     */
    public WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Espera a que un elemento sea visible y esté habilitado para hacer clic.
     *
     * @param locator localizador del elemento
     * @return WebElement clickeable encontrado
     * @throws TimeoutException si el elemento no se vuelve clickeable
     */
    public WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Espera a que todos los elementos que coinciden con el localizador sean visibles.
     *
     * @param locator localizador de los elementos
     * @return Lista de WebElement visibles
     * @throws TimeoutException si no todos los elementos son visibles
     */
    public List<WebElement> waitForAllElementsVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Espera a que al menos uno de los elementos especificados sea visible.
     *
     * <p>Útil cuando hay múltiples resultados posibles y solo uno aparecerá,
     * por ejemplo, un mensaje de éxito o uno de error.</p>
     *
     * @param locators localizadores de los elementos a verificar
     * @return el primer {@link WebElement} que se vuelve visible
     * @throws TimeoutException si ningún elemento se vuelve visible en el tiempo máximo
     */
    public WebElement waitForAnyElementVisible(By... locators) {
        return wait.until(driver -> {
            for (By locator : locators) {
                try {
                    WebElement element = driver.findElement(locator);
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    // Continuar con el siguiente localizador
                }
            }
            return null;
        });
    }

    /**
     * Espera a que un elemento sea invisible o no esté presente en el DOM.
     *
     * @param locator localizador del elemento
     * @return true si el elemento desaparece, false si no
     */
    public boolean waitForElementInvisible(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un elemento esté habilitado (sin atributo "disabled").
     *
     * @param locator localizador del elemento
     * @return true si el elemento está habilitado, false si no
     */
    public boolean waitForElementEnabled(By locator) {
        try {
            return wait.until(driver -> {
                WebElement element = driver.findElement(locator);
                return element.isEnabled();
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un elemento esté deshabilitado (con atributo "disabled").
     *
     * @param locator localizador del elemento
     * @return true si el elemento está deshabilitado, false si no
     */
    public boolean waitForElementDisabled(By locator) {
        try {
            return wait.until(driver -> {
                WebElement element = driver.findElement(locator);
                return !element.isEnabled();
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un elemento checkbox o radio button esté seleccionado.
     *
     * @param locator localizador del elemento
     * @return true si el elemento está seleccionado, false si no
     */
    public boolean waitForElementSelected(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeSelected(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un elemento checkbox o radio button NO esté seleccionado.
     *
     * @param locator localizador del elemento
     * @return true si el elemento no está seleccionado, false si no
     */
    public boolean waitForElementNotSelected(By locator) {
        try {
            return wait.until(ExpectedConditions.elementSelectionStateToBe(locator, false));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==================== MÉTODOS DE ESPERA CON REINTENTOS ====================

    /**
     * Espera una condición con reintentos explícitos y pausa entre cada uno.
     *
     * <p>Útil para condiciones intermitentes causadas por carga asíncrona,
     * animaciones o latencia de red.</p>
     *
     * @param <T>          tipo de retorno de la condición
     * @param condition    condición esperada de tipo {@link ExpectedCondition}
     * @param maxRetries   número máximo de reintentos
     * @param delaySeconds segundos de pausa entre reintentos
     * @return resultado de la condición
     * @throws TimeoutException si todos los intentos fallan
     */
    public <T> T waitWithRetry(ExpectedCondition<T> condition, int maxRetries, int delaySeconds) {
        TimeoutException lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return wait.until(condition);
            } catch (TimeoutException e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(delaySeconds * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Espera interrumpida", ie);
                    }
                }
            }
        }
        
        throw new TimeoutException(
            "La condición no se cumplió después de " + maxRetries + " intentos", 
            lastException
        );
    }

    /**
     * Espera a que un elemento sea clickeable, reintentando hasta {@code maxAttempts} veces.
     *
     * <p>Si todos los intentos fallan, la ejecución continúa sin lanzar excepción.</p>
     *
     * @param locator     localizador del elemento
     * @param maxAttempts número máximo de intentos
     * @param seconds     segundos de pausa entre cada intento
     */
    public void waitElementClickableWithRetry(By locator, int maxAttempts, int seconds) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(locator));
                return;
            } catch (TimeoutException e) {
                if (i < maxAttempts - 1) {
                    try {
                        Thread.sleep(seconds * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Espera a que un elemento sea visible, reintentando hasta {@code maxAttempts} veces.
     *
     * <p>Si todos los intentos fallan, la ejecución continúa sin lanzar excepción.</p>
     *
     * @param locator     localizador del elemento
     * @param maxAttempts número máximo de intentos
     * @param seconds     segundos de pausa entre cada intento
     */
    public void waitElementVisibleWithRetry(By locator, int maxAttempts, int seconds) {
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
                        return;
                    }
                }
            }
        }
    }

    // ==================== MÉTODOS DE ESPERA PARA TEXTO ====================

    /**
     * Espera a que el texto de un elemento coincida exactamente con el esperado.
     *
     * @param locator      localizador del elemento
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
     * Espera a que el texto del elemento contenga {@code partialText} y retorna el texto completo.
     *
     * @param locator     localizador del elemento
     * @param partialText texto parcial a buscar
     * @return texto completo del elemento
     * @throws TimeoutException si el texto no aparece en el tiempo máximo
     */
    public String waitForTextContains(By locator, String partialText) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String text = element.getText();
                if (text != null && text.contains(partialText)) {
                    return text;
                }
                return null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    /**
     * Espera hasta que el elemento tenga texto no vacío y lo retorna.
     *
     * @param locator localizador del elemento
     * @return texto no vacío del elemento
     * @throws TimeoutException si el elemento no tiene texto en el tiempo máximo
     */
    public String waitForNonEmptyText(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String text = element.getText();
                return (text != null && !text.trim().isEmpty()) ? text : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    /**
     * Espera a que el texto del elemento se estabilice y retorna su valor final.
     *
     * <p>Realiza dos lecturas separadas por {@code seconds} segundos; si coinciden
     * y no están vacías, el texto se considera estable.</p>
     *
     * @param locator localizador del elemento
     * @param seconds intervalo en segundos entre ambas lecturas
     * @return texto estabilizado del elemento
     * @throws TimeoutException si el texto no se estabiliza en el tiempo máximo
     */
    public String waitForStableText(By locator, int seconds) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String firstCheck = element.getText();

                if (firstCheck == null || firstCheck.trim().isEmpty()) {
                    return null;
                }

                try {
                    Thread.sleep(seconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }

                // Re-localizar el elemento para evitar StaleElementReferenceException
                String secondCheck = driver.findElement(locator).getText();
                if (firstCheck.equals(secondCheck)) {
                    return secondCheck;
                }

                return null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    /**
     * Espera a que el elemento tenga texto visible y luego limpia su contenido.
     *
     * @param locator localizador del elemento
     * @throws TimeoutException si el elemento no está disponible o no tiene texto
     */
    public void waitAndClearText(By locator) {
        WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(driver -> {
            try {
                return !campo.getText().trim().isEmpty();
            } catch (StaleElementReferenceException e) {
                WebElement newElement = driver.findElement(locator);
                return !newElement.getText().trim().isEmpty();
            }
        });
        campo.clear();
    }

    // ==================== MÉTODOS DE ESPERA PARA ATRIBUTOS Y VALORES ====================

    /**
     * Espera a que el valor (atributo value) de un elemento coincida con el esperado.
     *
     * @param locator       localizador del elemento
     * @param expectedValue valor esperado
     * @return true si el valor coincide, false en caso contrario
     */
    public boolean waitForValueToBe(By locator, String expectedValue) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElementValue(locator, expectedValue));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un atributo tenga un valor específico.
     *
     * @param locator       localizador del elemento
     * @param attribute     nombre del atributo
     * @param expectedValue valor esperado del atributo
     * @return true si el atributo tiene el valor esperado, false si no
     */
    public boolean waitForAttributeToBe(By locator, String attribute, String expectedValue) {
        try {
            return wait.until(ExpectedConditions.attributeToBe(locator, attribute, expectedValue));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un atributo contenga un valor parcial.
     *
     * @param locator      localizador del elemento
     * @param attribute    nombre del atributo
     * @param partialValue valor parcial a buscar en el atributo
     * @return true si el atributo contiene el valor parcial, false si no
     */
    public boolean waitForAttributeContains(By locator, String attribute, String partialValue) {
        try {
            return wait.until(ExpectedConditions.attributeContains(locator, attribute, partialValue));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que un atributo no esté vacío y retorna su valor.
     *
     * @param locator   localizador del elemento
     * @param attribute nombre del atributo
     * @return valor del atributo como String
     * @throws TimeoutException si el atributo permanece vacío
     */
    public String waitForAttributeNotEmpty(By locator, String attribute) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String value = element.getAttribute(attribute);
                return (value != null && !value.trim().isEmpty()) ? value : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    // ==================== MÉTODOS DE ESPERA PARA ALERTAS Y NOTIFICACIONES ====================

    /**
     * Espera a que un toast o notificación aparezca y obtiene su texto.
     *
     * @param locator localizador del elemento toast o notificación
     * @return texto del toast/notificación o cadena vacía si no aparece
     */
    public String waitForNotificationText(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = element.getText();
            return text != null ? text.trim() : "";
        } catch (TimeoutException e) {
            return "";
        }
    }

    /**
     * Espera a que un toast o notificación aparezca y luego desaparezca.
     *
     * <p>Útil para verificar que mensajes temporales se muestran y se ocultan
     * correctamente, como notificaciones de éxito o error.</p>
     *
     * @param locator localizador del elemento toast o notificación
     * @return true si el toast apareció y desapareció, false si no
     */
    public boolean waitForNotificationAppearAndDisappear(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que aparezca una alerta de JavaScript y retorna su texto.
     *
     * @return texto de la alerta
     * @throws TimeoutException si la alerta no aparece
     */
    public String waitForAlertPresent() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    // ==================== MÉTODOS DE ESPERA PARA PÁGINA Y URL ====================

    /**
     * Espera a que la página termine de cargar completamente.
     * Verifica que el estado del documento sea "complete".
     *
     * @return true si la página cargó completamente, false si no
     */
    public boolean waitForPageLoad() {
        try {
            return wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String readyState = js.executeScript("return document.readyState").toString();
                return readyState.equals("complete");
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que las peticiones AJAX estén completas.
     * Verifica que jQuery, si está presente, no tenga peticiones activas.
     *
     * @return true si no hay peticiones AJAX activas, false si no se pudo verificar
     */
    public boolean waitForAjaxComplete() {
        try {
            return wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                try {
                    // Verificar si jQuery está definido
                    boolean isJQueryPresent = (boolean) js.executeScript(
                        "return (typeof jQuery !== 'undefined')"
                    );
                    
                    if (isJQueryPresent) {
                        long activeConnections = (long) js.executeScript(
                            "return jQuery.active"
                        );
                        return activeConnections == 0;
                    }
                    
                    // Si jQuery no está presente, asumimos que AJAX está completo
                    return true;
                } catch (Exception e) {
                    // Si hay error en la ejecución del script, asumimos que está completo
                    return true;
                }
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que el título de la página coincida exactamente.
     *
     * @param expectedTitle título exacto esperado
     * @return true si el título coincide, false si no
     */
    public boolean waitForTitleIs(String expectedTitle) {
        try {
            return wait.until(ExpectedConditions.titleIs(expectedTitle));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que el título de la página contenga un texto parcial.
     *
     * @param partialTitle texto parcial a buscar en el título
     * @return true si el título contiene el texto, false si no
     */
    public boolean waitForTitleContains(String partialTitle) {
        try {
            return wait.until(ExpectedConditions.titleContains(partialTitle));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que la URL actual coincida exactamente.
     *
     * @param expectedUrl URL exacta esperada
     * @return true si la URL coincide, false si no
     */
    public boolean waitForUrlToBe(String expectedUrl) {
        try {
            return wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que la URL actual contenga un texto parcial.
     *
     * @param partialUrl texto parcial a buscar en la URL
     * @return true si la URL contiene el texto, false si no
     */
    public boolean waitForUrlContains(String partialUrl) {
        try {
            return wait.until(ExpectedConditions.urlContains(partialUrl));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==================== MÉTODOS DE ESPERA PARA FRAMES Y VENTANAS ====================

    /**
     * Espera a que un frame esté disponible y cambia el contexto a ese frame.
     *
     * @param locator localizador del frame
     * @return WebDriver con el contexto cambiado al frame
     * @throws TimeoutException si el frame no está disponible
     */
    public WebDriver waitForFrameAndSwitch(By locator) {
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    /**
     * Espera a que un frame esté disponible por su nombre o ID y cambia a él.
     *
     * @param frameNameOrId nombre o ID del frame
     * @return WebDriver con el contexto cambiado al frame
     * @throws TimeoutException si el frame no está disponible
     */
    public WebDriver waitForFrameByNameAndSwitch(String frameNameOrId) {
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameNameOrId));
    }

    /**
     * Espera a que el número de ventanas abiertas sea al menos {@code expectedWindowCount}.
     *
     * @param expectedWindowCount número mínimo de ventanas esperadas
     * @return {@code true} si se alcanza el número de ventanas, {@code false} si no
     */
    public boolean waitForNumberOfWindows(int expectedWindowCount) {
        try {
            return wait.until(driver -> driver.getWindowHandles().size() >= expectedWindowCount);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera a que una nueva ventana/pestaña se abra y cambia a ella.
     *
     * @param currentWindows conjunto de ventanas actuales antes de abrir la nueva
     * @return identificador de la nueva ventana
     * @throws TimeoutException si no se abre una nueva ventana
     */
    public String waitForNewWindow(Set<String> currentWindows) {
        return wait.until(driver -> {
            Set<String> allWindows = driver.getWindowHandles();
            for (String window : allWindows) {
                if (!currentWindows.contains(window)) {
                    return window;
                }
            }
            return null;
        });
    }

    // ==================== MÉTODOS DE ESPERA FLUENT ====================

    /**
     * Espera a que un elemento aparezca usando {@link FluentWait} con intervalo de sondeo configurable.
     *
     * <p>Ignora {@link NoSuchElementException} y {@link StaleElementReferenceException}
     * durante el sondeo.</p>
     *
     * @param locator        localizador del elemento
     * @param maxTimeSeconds tiempo máximo de espera en segundos
     * @param intervalMillis intervalo de sondeo en milisegundos
     * @return {@link WebElement} encontrado
     * @throws TimeoutException si el elemento no aparece en el tiempo máximo
     */
    public WebElement waitFluentElement(By locator, int maxTimeSeconds, int intervalMillis) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(maxTimeSeconds))
                .pollingEvery(Duration.ofMillis(intervalMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("El elemento no se encontró luego de esperar " + maxTimeSeconds + " segundos");

        return fluentWait.until(driver -> driver.findElement(locator));
    }

    /**
     * Espera a que una condición personalizada se cumpla usando {@link FluentWait}.
     *
     * @param <T>            tipo de retorno de la condición
     * @param condition      función que define la condición a esperar
     * @param maxTimeSeconds tiempo máximo de espera en segundos
     * @param intervalMillis intervalo de sondeo en milisegundos
     * @return resultado de la condición
     * @throws TimeoutException si la condición no se cumple en el tiempo máximo
     */
    public <T> T waitFluentCondition(Function<WebDriver, T> condition, int maxTimeSeconds, int intervalMillis) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(maxTimeSeconds))
                .pollingEvery(Duration.ofMillis(intervalMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        return fluentWait.until(condition);
    }

    // ==================== MÉTODOS DE ESPERA PARA ELEMENTOS STALE ====================

    /**
     * Espera a que un elemento se vuelva "stale" (ya no esté adjunto al DOM).
     * Útil después de refrescos de página o actualizaciones dinámicas.
     *
     * @param element elemento que se espera que se vuelva stale
     * @return true si el elemento se vuelve stale, false si no
     */
    public boolean waitForElementStaleness(WebElement element) {
        try {
            return wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==================== MÉTODOS DE ESPERA PARA ELEMENTOS CON CONDICIONES PERSONALIZADAS ====================

    /**
     * Espera a que un elemento cumpla una condición personalizada definida por el usuario.
     *
     * <p>Ejemplo de uso:</p>
     * <pre>{@code
     * WebElement element = waitHelper.waitForElementWithCondition(
     *     By.id("myElement"),
     *     el -> el.getAttribute("data-loaded").equals("true")
     * );
     * }</pre>
     *
     * @param locator   localizador del elemento
     * @param condition función que evalúa el elemento y retorna true cuando se cumple la condición
     * @return WebElement que cumple la condición
     * @throws TimeoutException si el elemento no cumple la condición
     */
    public WebElement waitForElementWithCondition(By locator, Function<WebElement, Boolean> condition) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed() && condition.apply(element)) {
                    return element;
                }
                return null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    // ==================== MÉTODOS UTILITARIOS ====================

    /**
     * Retorna la instancia de WebDriverWait configurada.
     * Útil para crear esperas personalizadas adicionales.
     *
     * @return instancia de {@link WebDriverWait}
     */
    public WebDriverWait getWait() {
        return wait;
    }

    /**
     * Realiza una pausa forzada en la ejecución.
     * 
     * <p><b>Nota:</b> Usar con moderación. Es preferible usar esperas explícitas
     * en lugar de pausas fijas.</p>
     *
     * @param milliseconds tiempo de pausa en milisegundos
     */
    public void pauseMillis(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Realiza una pausa forzada en la ejecución.
     *
     * <p><b>Nota:</b> Usar con moderación; preferir esperas explícitas.</p>
     *
     * @param seconds tiempo de pausa en segundos
     */
    public void pauseSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}