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
package org.primefaces.integrationtests.autocomplete;

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.GeneratedDriverService;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class AutoComplete006 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    @Inject
    private GeneratedDriverService service;

    private Driver driver;

    @PostConstruct
    public void init() {
        driver = service.getDrivers().get(4);
    }

    public void submit() {
        if (driver != null) {
            FacesMessage msg = new FacesMessage("Driver", "id: " + driver.getId() + ", name: " + driver.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public List<Driver> completeText(String query) {
        // add a delay to simulate slow response
        TestUtils.wait(2000);
        return service.getDrivers().stream().filter(d -> d.getName().contains(query)).collect(Collectors.toList());
    }

}
