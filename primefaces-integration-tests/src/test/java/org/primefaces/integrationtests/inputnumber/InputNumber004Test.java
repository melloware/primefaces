/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.integrationtests.inputnumber;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class InputNumber004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive still ensure decimalPlaces='0'")
    void integerNoDecimalPlaces(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        assertEquals("66", inputNumber.getValue());

        // Act
        inputNumber.setValue("87.31");
        page.button.click();

        // Assert
        assertEquals("87", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), "0", "0", "999999");
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive doesn't accept negative number")
    void integerPositiveConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        assertEquals("66", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("-5");
            fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            assertEquals(
                        "The value [-5] being set falls outside of the minimumValue [0] and maximumValue [999999] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(3)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Max doesn't accept higher than max value")
    void integerMaxConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        assertEquals("66", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("23999999");
            fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            assertEquals(
                        "The value [23999999] being set falls outside of the minimumValue [0] and maximumValue [999999] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(4)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive removing value resets component")
    void integerRemovingValue(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        assertEquals("66", inputNumber.getValue());

        // Act
        inputNumber.setValue("3");
        inputNumber.getInput().sendKeys(Keys.BACK_SPACE);
        inputNumber.getInput().sendKeys(Keys.DELETE);
        assertEquals("", inputNumber.getValue());
        page.button.click();

        // Assert
        assertEquals("", inputNumber.getValue());

        assertConfiguration(inputNumber.getWidgetConfiguration(), "0", "0", "999999");
    }

    @Test
    @Order(5)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Positive still ensure decimalPlaces='2'")
    void decimal(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        assertEquals("6.78", inputNumber.getValue());

        // Act
        inputNumber.setValue("31.9");
        page.button.click();

        // Assert
        assertEquals("31.90", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), "2", "0", "999999.99");
    }

    @Test
    @Order(6)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Positive doesn't accept negative number")
    void decimalPositiveConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        assertEquals("6.78", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("-8.23");
            fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            assertEquals(
                        "The value [-8.23] being set falls outside of the minimumValue [0] and maximumValue [999999.99] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(7)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Max doesn't accept higher than max value")
    void decimalMaxConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        assertEquals("6.78", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("4599999999999");
            fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            assertEquals(
                        "The value [4599999999999] being set falls outside of the minimumValue "
                                + "[0] and maximumValue [999999.99] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    private void assertConfiguration(JSONObject cfg, String decimalPlaces, String minValue, String maxValue) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        assertEquals(Integer.valueOf(decimalPlaces), cfg.get("decimalPlaces"));
        assertEquals(minValue, cfg.get("minimumValue"));
        assertEquals(maxValue, cfg.get("maximumValue"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:integer")
        InputNumber integer;

        @FindBy(id = "form:decimal")
        InputNumber decimal;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber004.xhtml";
        }
    }
}
