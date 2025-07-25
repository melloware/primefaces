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
package org.primefaces.component.api;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.util.MessageFactory;

import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.context.FacesContext;

/**
 * UIData for pageable components
 */
public class UIPageableData extends PrimeUIData implements Pageable, TouchAware {

    public static final String PAGINATOR_TOP_CONTAINER_CLASS = "ui-paginator ui-paginator-top ui-widget-header";
    public static final String PAGINATOR_BOTTOM_CONTAINER_CLASS = "ui-paginator ui-paginator-bottom ui-widget-header";
    public static final String PAGINATOR_PAGES_CLASS = "ui-paginator-pages";
    public static final String PAGINATOR_CENTER_CONTENT_CLASS = "ui-paginator-center-content";
    public static final String PAGINATOR_TOP_LEFT_CONTENT_CLASS = "ui-paginator-top-left-content";
    public static final String PAGINATOR_TOP_RIGHT_CONTENT_CLASS = "ui-paginator-top-right-content";
    public static final String PAGINATOR_BOTTOM_LEFT_CONTENT_CLASS = "ui-paginator-bottom-left-content";
    public static final String PAGINATOR_BOTTOM_RIGHT_CONTENT_CLASS = "ui-paginator-bottom-right-content";
    public static final String PAGINATOR_PAGE_CLASS = "ui-paginator-page ui-button ui-button-flat ui-state-default";
    public static final String PAGINATOR_ACTIVE_PAGE_CLASS = "ui-paginator-page ui-button ui-button-flat ui-state-default ui-state-active";
    public static final String PAGINATOR_CURRENT_CLASS = "ui-paginator-current";
    public static final String PAGINATOR_RPP_OPTIONS_CLASS = "ui-paginator-rpp-options ui-widget ui-state-default";
    public static final String PAGINATOR_RPP_LABEL_CLASS = "ui-paginator-rpp-label ui-helper-hidden";
    public static final String PAGINATOR_JTP_SELECT_CLASS = "ui-paginator-jtp-select ui-widget ui-state-default";
    public static final String PAGINATOR_JTP_INPUT_CLASS = "ui-paginator-jtp-input " + InputText.STYLE_CLASS;
    public static final String PAGINATOR_FIRST_PAGE_LINK_CLASS = "ui-paginator-first ui-state-default";
    public static final String PAGINATOR_PREV_PAGE_LINK_CLASS = "ui-paginator-prev ui-state-default";
    public static final String PAGINATOR_NEXT_PAGE_LINK_CLASS = "ui-paginator-next ui-state-default";
    public static final String PAGINATOR_LAST_PAGE_LINK_CLASS = "ui-paginator-last ui-state-default";
    public static final String EMPTY_MESSAGE = "primefaces.data.EMPTY_MESSAGE";

    public enum PropertyKeys {
        rows, // #5068
        touchable,
        paginator,
        paginatorTemplate,
        rowsPerPageTemplate,
        currentPageReportTemplate,
        pageLinks,
        paginatorPosition,
        paginatorAlwaysVisible,
        emptyMessage
    }

    protected enum InternalPropertyKeys {
        rowsInitialValue;
    }

    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage, MessageFactory.getMessage(getFacesContext(), EMPTY_MESSAGE));
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
    }

    @Override
    public Boolean isTouchable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.touchable);
    }

    @Override
    public void setTouchable(Boolean touchable) {
        getStateHelper().put(PropertyKeys.touchable, touchable);
    }

    public boolean isPaginator() {
        return (Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
    }

    public void setPaginator(boolean paginator) {
        getStateHelper().put(PropertyKeys.paginator, paginator);
    }

    @Override
    public String getPaginatorTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.paginatorTemplate,
                "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
    }

    public void setPaginatorTemplate(String paginatorTemplate) {
        getStateHelper().put(PropertyKeys.paginatorTemplate, paginatorTemplate);
    }

    @Override
    public String getRowsPerPageTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.rowsPerPageTemplate, null);
    }

    public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
        getStateHelper().put(PropertyKeys.rowsPerPageTemplate, rowsPerPageTemplate);
    }

    @Override
    public String getCurrentPageReportTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, "({currentPage} of {totalPages})");
    }

    public void setCurrentPageReportTemplate(String currentPageReportTemplate) {
        getStateHelper().put(PropertyKeys.currentPageReportTemplate, currentPageReportTemplate);
    }

    @Override
    public int getPageLinks() {
        return (Integer) getStateHelper().eval(PropertyKeys.pageLinks, 10);
    }

    public void setPageLinks(int pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, pageLinks);
    }

    @Override
    public String getPaginatorPosition() {
        return (String) getStateHelper().eval(PropertyKeys.paginatorPosition, "both");
    }

    public void setPaginatorPosition(String paginatorPosition) {
        getStateHelper().put(PropertyKeys.paginatorPosition, paginatorPosition);
    }

    @Override
    public boolean isPaginatorAlwaysVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.paginatorAlwaysVisible, true);
    }

    public void setPaginatorAlwaysVisible(boolean paginatorAlwaysVisible) {
        getStateHelper().put(PropertyKeys.paginatorAlwaysVisible, paginatorAlwaysVisible);
    }

    @Override
    public int getRows() {
        return (Integer) getStateHelper().eval(PropertyKeys.rows, 0);
    }

    @Override
    public void setRows(int rows) {
        if (getStateHelper().eval(InternalPropertyKeys.rowsInitialValue) == null) {
            int rowsOld = getRows();
            if (rowsOld != 0) {
                getStateHelper().put(InternalPropertyKeys.rowsInitialValue, rowsOld);
            }
        }

        if (rows < 0) {
            throw new IllegalArgumentException(String.valueOf(rows));
        }
        ELContext elContext = getFacesContext().getELContext();
        ValueExpression rowsVe = ValueExpressionAnalyzer.getExpression(
                elContext, getValueExpression(PropertyKeys.rows.name()), true);
        if (isWriteable(elContext, rowsVe)) {
            rowsVe.setValue(elContext, rows);
        }
        else {
            getStateHelper().put(PropertyKeys.rows, rows);
        }
    }

    @Override
    public void setFirst(int first) {
        ELContext elContext = getFacesContext().getELContext();
        ValueExpression firstVe = ValueExpressionAnalyzer.getExpression(
                elContext, getValueExpression("first"), true);
        if (isWriteable(elContext, firstVe)) {
            firstVe.setValue(elContext, first);
        }
        else {
            super.setFirst(first);
        }
    }

    public void resetRows() {
        ELContext elContext = getFacesContext().getELContext();
        ValueExpression rowsVe = ValueExpressionAnalyzer.getExpression(
                elContext, getValueExpression(PropertyKeys.rows.name()), true);
        if (rowsVe != null) {
            //ValueExpression --> remove state to ensure the VE is re-evaluated
            getStateHelper().remove(PropertyKeys.rows);
        }
        //normal attribute value --> restore initial rows
        Object rows = getStateHelper().eval(InternalPropertyKeys.rowsInitialValue);
        if (rows != null) {
            setRows((int) rows);
        }
    }

    private boolean isWriteable(ELContext elContext, ValueExpression ve) {
        return ve != null && !ve.isReadOnly(elContext);
    }

    public void calculateRows() {
        if ("*".equals(getRowsPerPage())) {
            setRows(getRowCount());
        }
    }

    public boolean calculateFirst() {
        int rows = getRows();

        if (rows > 0) {
            int first = getFirst();
            int rowCount = getRowCount();

            if (rowCount > 0 && first >= rowCount) {
                int numberOfPages = (int) Math.ceil(rowCount * 1d / rows);
                int newFirst = Math.max((numberOfPages - 1) * rows, 0);

                setFirst(newFirst);

                return first != newFirst;
            }
        }

        return false;
    }

    @Override
    public int getPage() {
        if (getRowCount() > 0) {
            int rows = getRowsToRender();

            if (rows > 0) {
                int first = getFirst();

                return first / rows;
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    @Override
    public int getPageCount() {
        return (int) Math.ceil(getRowCount() * 1d / getRowsToRender());
    }

    @Override
    public int getRowsToRender() {
        int rows = getRows();

        return rows == 0 ? getRowCount() : rows;
    }

    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }

    private boolean isRowsPerPageValid(String rowsParam) {

        if (rowsParam == null) {
            return true;
        }

        String rowsPerPageTemplate = getRowsPerPageTemplate();

        if (rowsPerPageTemplate != null) {
            String[] options = rowsPerPageTemplate.split("[,]+");

            for (String option : options) {
                String opt = option.trim();

                if (opt.equals(rowsParam) || (opt.startsWith("{ShowAll|") && "*".equals(rowsParam))) {
                    return true;
                }
            }

            return false;
        }

        int rows = getRows();

        if (rows > 0) {
            return Integer.toString(rows).equals(rowsParam);
        }

        return true;
    }

    public void updatePaginationData(FacesContext context) {
        setRowIndex(-1);
        String clientId = getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String firstParam = params.get(clientId + "_first");
        String rowsParam = params.get(clientId + "_rows");

        if (!isRowsPerPageValid(rowsParam)) {
            throw new IllegalArgumentException("Unsupported rows per page value: " + rowsParam);
        }

        setFirst(Integer.parseInt(firstParam));
        int newRowsValue = "*".equals(rowsParam) ? getRowCount() : Integer.parseInt(rowsParam);
        setRows(newRowsValue);
        setRowsPerPage(rowsParam);
    }

    public String getRowsPerPage() {
        return (String) getStateHelper().eval("rowsPerPage", null);
    }

    public void setRowsPerPage(String rowsPerPage) {
        getStateHelper().put("rowsPerPage", rowsPerPage);
    }
}
