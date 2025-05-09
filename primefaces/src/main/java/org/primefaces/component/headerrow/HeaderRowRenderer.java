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
package org.primefaces.component.headerrow;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class HeaderRowRenderer extends CoreRenderer<HeaderRow> {

    @Override
    public void encodeEnd(FacesContext context, HeaderRow component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean expandable = component.isExpandable();
        boolean expanded = component.isExpanded();

        // GitHub #7296 prevent issue with PanelGrid rendering
        Object helperRenderer = context.getAttributes().remove(Constants.HELPER_RENDERER);

        writer.startElement("tr", null);
        writer.writeAttribute("class", DataTable.HEADER_ROW_CLASS, null);

        if (component.getChildCount() > 0) {
            boolean firstColumn = true;
            for (int i = 0; i < component.getChildCount(); i++) {
                UIComponent child = component.getChildren().get(i);
                if (child.isRendered() && child instanceof Column) {
                    Column column = (Column) child;
                    encodeHeaderRowWithColumn(context, column, expandable, expanded, firstColumn);
                    firstColumn = false;
                }
            }
        }
        else {
            DataTable table = (DataTable) component.getParent();
            encodeHeaderRowWithoutColumn(context, component, table, expandable, expanded);
        }

        writer.endElement("tr");
        context.getAttributes().put(Constants.HELPER_RENDERER, helperRenderer);
    }

    protected void encodeToggleIcon(FacesContext context, boolean expanded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("class", DataTable.ROW_GROUP_TOGGLER_CLASS, null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(expanded), null);
        writer.writeAttribute("href", "#", null);
        writer.startElement("span", null);
        writer.writeAttribute("class", expanded ? DataTable.ROW_GROUP_TOGGLER_OPEN_ICON_CLASS : DataTable.ROW_GROUP_TOGGLER_CLOSED_ICON_CLASS, null);
        writer.endElement("span");
        writer.endElement("a");
    }

    protected void encodeHeaderRowWithColumn(FacesContext context, UIColumn column, boolean expandable, boolean expanded, boolean first) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        int rowspan = column.getRowspan();
        int colspan = column.getColspan();

        writer.startElement("td", null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }

        if (expandable && first) {
            encodeToggleIcon(context, expanded);
        }

        column.encodeAll(context);

        writer.endElement("td");
    }

    protected void encodeHeaderRowWithoutColumn(FacesContext context, HeaderRow row, DataTable table, boolean expandable, boolean expanded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = row.getStyle();
        String styleClass = row.getStyleClass();
        Integer rowspan = row.getRowspan();
        Integer colspan = row.getColspan();

        writer.startElement("td", null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }
        if (rowspan != null) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (colspan == null) {
            colspan = table.getColumnsCount();
        }
        writer.writeAttribute("colspan", colspan, null);

        if (expandable) {
            encodeToggleIcon(context, expanded);
        }

        String field = row.getField();
        Object value = LangUtils.isNotBlank(field)
                ? UIColumn.createValueExpressionFromField(context, table.getVar(), field).getValue(context.getELContext())
                : row.getGroupBy();
        writer.writeText(value, null);

        writer.endElement("td");
    }

    @Override
    public void encodeChildren(FacesContext context, HeaderRow component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
