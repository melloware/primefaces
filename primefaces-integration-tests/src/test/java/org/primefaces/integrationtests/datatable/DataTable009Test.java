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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.model.datatable.Row;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable009Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: filter - issue 1390 - https://github.com/primefaces/primefaces/issues/1390")
    void filterIssue1390(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act - do some filtering
        dataTable.sort("Name");
        dataTable.filter("Name", "Java");
        PrimeExpectedConditions.visibleAndAnimationComplete(page.messages);

        // Assert
        assertEquals("FilterValue for name", page.messages.getMessage(0).getSummary());
        assertEquals("Java", page.messages.getMessage(0).getDetail());
        assertEquals("FilteredValue(s)", page.messages.getMessage(1).getSummary());
//        Assertions.assertEquals("Java,JavaScript", page.messages.getMessage(1).getDetail());
        assertEquals("null", page.messages.getMessage(1).getDetail()); //filtered values are the old filtered values from last time

        // Act
        page.buttonReportFilteredProgLanguages.click();

        // Assert
        assertEquals("FilteredValue(s)", page.messages.getMessage(0).getSummary());
        assertEquals("Java,JavaScript", page.messages.getMessage(0).getDetail());

        // Act - do some other filtering
        dataTable.filter("Name", "JavaScript");

        // Assert
        assertEquals("FilterValue for name", page.messages.getMessage(0).getSummary());
        assertEquals("JavaScript", page.messages.getMessage(0).getDetail());
        assertEquals("FilteredValue(s)", page.messages.getMessage(1).getSummary());
//        Assertions.assertEquals("JavaScript", page.messages.getMessage(1).getDetail());
        assertEquals("Java,JavaScript", page.messages.getMessage(1).getDetail()); //filtered values are the old filtered values from last time

        // Act
        page.buttonReportFilteredProgLanguages.click();

        // Assert
        assertEquals("FilteredValue(s)", page.messages.getMessage(0).getSummary());
        assertEquals("JavaScript", page.messages.getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: SelectOneMenu acts as filter")
    void keepSelectOneMenuFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act - do some filtering
        dataTable.sort("Name");
        page.firstAppearedFilter.select("2000");
        PrimeExpectedConditions.visibleAndAnimationComplete(page.messages);

        // Assert
        validateFirstAppeared(dataTable, page.firstAppearedFilter, "2000");
        assertEquals("FilterValue for firstAppeared", page.messages.getMessage(0).getSummary());
        assertEquals("2000", page.messages.getMessage(0).getDetail());
        assertEquals("FilteredValue(s)", page.messages.getMessage(1).getSummary());
//        Assertions.assertEquals("C#", page.messages.getMessage(1).getDetail());
        assertEquals("null", page.messages.getMessage(1).getDetail()); //filtered values are the old filtered values from last time

        // Act
        page.buttonReportFilteredProgLanguages.click();

        // Assert
        validateFirstAppeared(dataTable, page.firstAppearedFilter, "2000");
        assertEquals("FilteredValue(s)", page.messages.getMessage(0).getSummary());
        assertEquals("C#", page.messages.getMessage(0).getDetail());

        // Act - do some other filtering
        page.firstAppearedFilter.select("1995");

        // Assert
        validateFirstAppeared(dataTable, page.firstAppearedFilter, "1995");
        assertEquals("FilterValue for firstAppeared", page.messages.getMessage(0).getSummary());
        assertEquals("1995", page.messages.getMessage(0).getDetail());
        assertEquals("FilteredValue(s)", page.messages.getMessage(1).getSummary());
//        Assertions.assertEquals("Java,JavaScript", page.messages.getMessage(1).getDetail());
        assertEquals("C#", page.messages.getMessage(1).getDetail()); //filtered values are the old filtered values from last time

        // Act
        page.buttonReportFilteredProgLanguages.click();

        // Assert
        validateFirstAppeared(dataTable, page.firstAppearedFilter, "1995");
        assertEquals("FilteredValue(s)", page.messages.getMessage(0).getSummary());
        assertEquals("Java,JavaScript", page.messages.getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: filter - issue 7026 - https://github.com/primefaces/primefaces/issues/7026")
    void filterIssue7026(Page page) {
        page.buttonFacesImpl.click();
        if (page.messages.getMessage(0).getSummary().contains("Mojarra")) {
            // known issue with Mojarra 2.3.14, fixed since Mojarra 2.3.15
        }

        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        dataTable.sort("Name");

        // Assert
        assertEquals("C#", dataTable.getRow(0).getCell(1).getText());
        SelectOneMenu firstAppeared = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:datatable:0:firstAppeared"));
        assertEquals("2000", firstAppeared.getSelectedLabel());

        // Act
        firstAppeared.select("2020");
        dataTable.filter("Name", "xxx");
        dataTable.removeFilter("Name");

        // Assert
        assertEquals("C#", dataTable.getRow(0).getCell(1).getText());
        assertEquals("2020", firstAppeared.getSelectedLabel());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void validateFirstAppeared(DataTable dataTable, SelectOneMenu firstAppearedFilter, String firstAppearedExpected) {
        assertEquals(firstAppearedExpected, firstAppearedFilter.getSelectedLabel());

        int rowNumber = 0;
        for (Row row : dataTable.getRows()) {
            assertNotNull(row);
            SelectOneMenu firstAppeared = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:datatable:" + rowNumber + ":firstAppeared"));
            assertEquals(firstAppearedExpected, firstAppeared.getSelectedLabel());
            rowNumber++;
        }
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("filter"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonReportFilteredProgLanguages")
        CommandButton buttonReportFilteredProgLanguages;

        @FindBy(id = "form:buttonFacesImpl")
        CommandButton buttonFacesImpl;

        @FindBy(id = "form:datatable:firstAppearedFilter")
        SelectOneMenu firstAppearedFilter;

        @Override
        public String getLocation() {
            return "datatable/dataTable009.xhtml";
        }
    }
}
