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
package org.primefaces.component.datatable;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.api.UITable;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ELUtils;

import java.util.Collection;

import jakarta.el.MethodExpression;
import jakarta.faces.component.behavior.ClientBehaviorHolder;

public abstract class DataTableBase extends UIPageableData implements Widget, RTLAware, ClientBehaviorHolder,
        PrimeClientBehaviorHolder, UITable<DataTableState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataTableRenderer";

    public enum PropertyKeys {

        allowUnsorting,
        ariaRowLabel,
        caseSensitiveSort,
        cellEditMode,
        cellNavigation,
        cellSeparator,
        clientCache,
        dataLocale,
        dir,
        disableContextMenuIfEmpty,
        selectionDisabled,
        selectionTextDisabled,
        draggableColumns,
        draggableRows,
        draggableRowsFunction,
        editInitEvent,
        editMode,
        editable,
        editingRow,
        escapeText,
        expandedRow,
        exportTag,
        exportRowTag,
        filterBy,
        filterDelay,
        filterEvent,
        filterNormalize,
        filteredValue,
        frozenColumns,
        frozenColumnsAlignment,
        frozenRows,
        globalFilter,
        globalFilterFunction,
        globalFilterOnly,
        liveResize,
        liveScroll,
        liveScrollBuffer,
        multiViewState,
        nativeElements,
        onExpandStart,
        onRowClick,
        reflow,
        renderEmptyFacets,
        resizableColumns,
        resizeMode,
        rowDragSelector,
        rowEditMode,
        rowExpandMode,
        rowHover,
        rowKey,
        rowSelector,
        rowStyleClass,
        rowTitle,
        saveOnCellBlur,
        scrollHeight,
        scrollRows,
        scrollWidth,
        scrollable,
        selectAllFilteredOnly,
        selection,
        selectionMode,
        selectionPageOnly,
        selectionRowMode,
        skipChildren,
        sortBy,
        sortMode,
        stickyHeader,
        stickyTopAt,
        style,
        styleClass,
        summary,
        tabindex,
        tableStyle,
        tableStyleClass,
        virtualScroll,
        stripedRows,
        showGridlines,
        size,
        widgetVar,
        partialUpdate,
        showSelectAll
    }

    public DataTableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isScrollable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
    }

    public void setScrollable(boolean scrollable) {
        getStateHelper().put(PropertyKeys.scrollable, scrollable);
    }

    public String getScrollHeight() {
        return (String) getStateHelper().eval(PropertyKeys.scrollHeight, null);
    }

    public void setScrollHeight(String scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public String getScrollWidth() {
        return (String) getStateHelper().eval(PropertyKeys.scrollWidth, null);
    }

    public void setScrollWidth(String scrollWidth) {
        getStateHelper().put(PropertyKeys.scrollWidth, scrollWidth);
    }

    public String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, () -> {
            // if not set by xhtml, we need to check the type of the value binding
            Class<?> type = ELUtils.getType(getFacesContext(),
                    getValueExpression(PropertyKeys.selection.toString()),
                    this::getSelection);
            if (type != null) {
                String selectionMode = "single";
                if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                    selectionMode = "multiple";
                }

                // remember in ViewState, to not do the same check again
                setSelectionMode(selectionMode);

                return selectionMode;
            }
            else {
                return null;
            }
        });
    }

    public void setSelectionMode(String selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, selectionMode);
    }

    public Object getSelection() {
        return getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(Object selection) {
        getStateHelper().put(PropertyKeys.selection, selection);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isLiveScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.liveScroll, false);
    }

    public void setLiveScroll(boolean liveScroll) {
        getStateHelper().put(PropertyKeys.liveScroll, liveScroll);
    }

    public String getRowStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public String getRowTitle() {
        return (String) getStateHelper().eval(PropertyKeys.rowTitle, null);
    }

    public void setRowTitle(String rowTitle) {
        getStateHelper().put(PropertyKeys.rowTitle, rowTitle);
    }

    public String getOnExpandStart() {
        return (String) getStateHelper().eval(PropertyKeys.onExpandStart, null);
    }

    public void setOnExpandStart(String onExpandStart) {
        getStateHelper().put(PropertyKeys.onExpandStart, onExpandStart);
    }

    public boolean isResizableColumns() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, resizableColumns);
    }

    public int getScrollRows() {
        return (Integer) getStateHelper().eval(PropertyKeys.scrollRows, 0);
    }

    public void setScrollRows(int scrollRows) {
        getStateHelper().put(PropertyKeys.scrollRows, scrollRows);
    }

    public String getRowKey() {
        return (String) getStateHelper().eval(PropertyKeys.rowKey, null);
    }

    public void setRowKey(String rowKey) {
        getStateHelper().put(PropertyKeys.rowKey, rowKey);
    }

    public String getFilterEvent() {
        return (String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(String filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, filterEvent);
    }

    public int getFilterDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.filterDelay, Integer.MAX_VALUE);
    }

    public void setFilterDelay(int filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, filterDelay);
    }

    public String getTableStyle() {
        return (String) getStateHelper().eval(PropertyKeys.tableStyle, null);
    }

    public void setTableStyle(String tableStyle) {
        getStateHelper().put(PropertyKeys.tableStyle, tableStyle);
    }

    public String getTableStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.tableStyleClass, null);
    }

    public void setTableStyleClass(String tableStyleClass) {
        getStateHelper().put(PropertyKeys.tableStyleClass, tableStyleClass);
    }

    public boolean isDraggableColumns() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggableColumns, false);
    }

    public void setDraggableColumns(boolean draggableColumns) {
        getStateHelper().put(PropertyKeys.draggableColumns, draggableColumns);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
    }

    public String getSortMode() {
        return (String) getStateHelper().eval(PropertyKeys.sortMode, "multiple");
    }

    public void setSortMode(String sortMode) {
        getStateHelper().put(PropertyKeys.sortMode, sortMode);
    }

    @Override
    public Object getSortBy() {
        return getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    @Override
    public void setSortBy(Object sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, sortBy);
    }

    public boolean isAllowUnsorting() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowUnsorting, false);
    }

    public void setAllowUnsorting(boolean allowUnsorting) {
        getStateHelper().put(PropertyKeys.allowUnsorting, allowUnsorting);
    }

    public String getEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.editMode, "row");
    }

    public void setEditMode(String editMode) {
        getStateHelper().put(PropertyKeys.editMode, editMode);
    }

    public boolean isEditingRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editingRow, false);
    }

    public void setEditingRow(boolean editingRow) {
        getStateHelper().put(PropertyKeys.editingRow, editingRow);
    }

    public String getCellSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.cellSeparator, null);
    }

    public void setCellSeparator(String cellSeparator) {
        getStateHelper().put(PropertyKeys.cellSeparator, cellSeparator);
    }

    public String getSummary() {
        return (String) getStateHelper().eval(PropertyKeys.summary, null);
    }

    public void setSummary(String summary) {
        getStateHelper().put(PropertyKeys.summary, summary);
    }

    public int getFrozenRows() {
        return (Integer) getStateHelper().eval(PropertyKeys.frozenRows, 0);
    }

    public void setFrozenRows(int frozenRows) {
        getStateHelper().put(PropertyKeys.frozenRows, frozenRows);
    }

    @Override
    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public boolean isLiveResize() {
        return (Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, liveResize);
    }

    public boolean isStickyHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, stickyHeader);
    }

    public boolean isExpandedRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expandedRow, false);
    }

    public void setExpandedRow(boolean expandedRow) {
        getStateHelper().put(PropertyKeys.expandedRow, expandedRow);
    }

    public boolean isSelectionDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectionDisabled, false);
    }

    public void setSelectionDisabled(boolean selectionDisabled) {
        getStateHelper().put(PropertyKeys.selectionDisabled, selectionDisabled);
    }

    public String getSelectionRowMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionRowMode, "new");
    }

    public void setSelectionRowMode(String rowSelectMode) {
        getStateHelper().put(PropertyKeys.selectionRowMode, rowSelectMode);
    }

    public String getRowExpandMode() {
        return (String) getStateHelper().eval(PropertyKeys.rowExpandMode, "multiple");
    }

    public void setRowExpandMode(String rowExpandMode) {
        getStateHelper().put(PropertyKeys.rowExpandMode, rowExpandMode);
    }

    @Override public Object getDataLocale() {
        return getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(Object dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, dataLocale);
    }

    public boolean isNativeElements() {
        return (Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, nativeElements);
    }

    public int getFrozenColumns() {
        return (Integer) getStateHelper().eval(PropertyKeys.frozenColumns, 0);
    }

    public void setFrozenColumns(int frozenColumns) {
        getStateHelper().put(PropertyKeys.frozenColumns, frozenColumns);
    }

    public String getFrozenColumnsAlignment() {
        return (String) getStateHelper().eval(PropertyKeys.frozenColumnsAlignment, "left");
    }

    public void setFrozenColumnsAlignment(String alignFrozenColumnsRight) {
        getStateHelper().put(PropertyKeys.frozenColumnsAlignment, alignFrozenColumnsRight);
    }

    public boolean isDraggableRows() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggableRows, false);
    }

    public void setDraggableRows(boolean draggableRows) {
        getStateHelper().put(PropertyKeys.draggableRows, draggableRows);
    }

    public boolean isSkipChildren() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipChildren, false);
    }

    public void setSkipChildren(boolean skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isSelectionTextDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectionTextDisabled, true);
    }

    public void setSelectionTextDisabled(boolean selectionTextDisabled) {
        getStateHelper().put(PropertyKeys.selectionTextDisabled, selectionTextDisabled);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public boolean isReflow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.reflow, false);
    }

    public void setReflow(boolean reflow) {
        getStateHelper().put(PropertyKeys.reflow, reflow);
    }

    public int getLiveScrollBuffer() {
        return (Integer) getStateHelper().eval(PropertyKeys.liveScrollBuffer, 0);
    }

    public void setLiveScrollBuffer(int liveScrollBuffer) {
        getStateHelper().put(PropertyKeys.liveScrollBuffer, liveScrollBuffer);
    }

    public boolean isRowHover() {
        return (Boolean) getStateHelper().eval(PropertyKeys.rowHover, false);
    }

    public void setRowHover(boolean rowHover) {
        getStateHelper().put(PropertyKeys.rowHover, rowHover);
    }

    public String getResizeMode() {
        return (String) getStateHelper().eval(PropertyKeys.resizeMode, "fit");
    }

    public void setResizeMode(String resizeMode) {
        getStateHelper().put(PropertyKeys.resizeMode, resizeMode);
    }

    public String getAriaRowLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaRowLabel, null);
    }

    public void setAriaRowLabel(String ariaRowLabel) {
        getStateHelper().put(PropertyKeys.ariaRowLabel, ariaRowLabel);
    }

    public boolean isSaveOnCellBlur() {
        return (Boolean) getStateHelper().eval(PropertyKeys.saveOnCellBlur, true);
    }

    public void setSaveOnCellBlur(boolean saveOnCellBlur) {
        getStateHelper().put(PropertyKeys.saveOnCellBlur, saveOnCellBlur);
    }

    public boolean isClientCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.clientCache, false);
    }

    public void setClientCache(boolean clientCache) {
        getStateHelper().put(PropertyKeys.clientCache, clientCache);
    }

    @Override
    public boolean isMultiViewState() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiViewState, false);
    }

    public void setMultiViewState(boolean multiViewState) {
        getStateHelper().put(PropertyKeys.multiViewState, multiViewState);
    }

    @Override
    public Object getFilterBy() {
        return getStateHelper().eval(PropertyKeys.filterBy);
    }

    @Override
    public void setFilterBy(Object filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, filterBy);
    }

    @Override
    public String getGlobalFilter() {
        return (String) getStateHelper().eval(PropertyKeys.globalFilter, null);
    }

    @Override
    public void setGlobalFilter(String globalFilter) {
        getStateHelper().put(PropertyKeys.globalFilter, globalFilter);
    }

    @Override
    public boolean isGlobalFilterOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.globalFilterOnly, false);
    }

    @Override
    public void setGlobalFilterOnly(boolean globalFilterOnly) {
        getStateHelper().put(PropertyKeys.globalFilterOnly, globalFilterOnly);
    }

    public String getCellEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(String cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, cellEditMode);
    }

    public boolean isVirtualScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.virtualScroll, false);
    }

    public void setVirtualScroll(boolean virtualScroll) {
        getStateHelper().put(PropertyKeys.virtualScroll, virtualScroll);
    }

    public boolean isStripedRows() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stripedRows, false);
    }

    public void setStripedRows(boolean stripedRows) {
        getStateHelper().put(PropertyKeys.stripedRows, stripedRows);
    }

    public boolean isShowGridlines() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showGridlines, false);
    }

    public void setShowGridlines(boolean showGridlines) {
        getStateHelper().put(PropertyKeys.showGridlines, showGridlines);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, "regular");
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public String getRowDragSelector() {
        return (String) getStateHelper().eval(PropertyKeys.rowDragSelector, null);
    }

    public void setRowDragSelector(String rowDragSelector) {
        getStateHelper().put(PropertyKeys.rowDragSelector, rowDragSelector);
    }

    public jakarta.el.MethodExpression getDraggableRowsFunction() {
        return (jakarta.el.MethodExpression) getStateHelper().eval(PropertyKeys.draggableRowsFunction, null);
    }

    public void setDraggableRowsFunction(jakarta.el.MethodExpression draggableRowsFunction) {
        getStateHelper().put(PropertyKeys.draggableRowsFunction, draggableRowsFunction);
    }

    public String getOnRowClick() {
        return (String) getStateHelper().eval(PropertyKeys.onRowClick, null);
    }

    public void setOnRowClick(String onRowClick) {
        getStateHelper().put(PropertyKeys.onRowClick, onRowClick);
    }

    public String getEditInitEvent() {
        return (String) getStateHelper().eval(PropertyKeys.editInitEvent, "click");
    }

    public void setEditInitEvent(String editInitEvent) {
        getStateHelper().put(PropertyKeys.editInitEvent, editInitEvent);
    }

    public String getRowSelector() {
        return (String) getStateHelper().eval(PropertyKeys.rowSelector, null);
    }

    public void setRowSelector(String rowSelector) {
        getStateHelper().put(PropertyKeys.rowSelector, rowSelector);
    }

    public boolean isDisableContextMenuIfEmpty() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableContextMenuIfEmpty, false);
    }

    public void setDisableContextMenuIfEmpty(boolean disableContextMenuIfEmpty) {
        getStateHelper().put(PropertyKeys.disableContextMenuIfEmpty, disableContextMenuIfEmpty);
    }

    public boolean isEscapeText() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escapeText, true);
    }

    public void setEscapeText(boolean escapeText) {
        getStateHelper().put(PropertyKeys.escapeText, escapeText);
    }

    public String getRowEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.rowEditMode, "eager");
    }

    public void setRowEditMode(String rowEditMode) {
        getStateHelper().put(PropertyKeys.rowEditMode, rowEditMode);
    }

    public String getStickyTopAt() {
        return (String) getStateHelper().eval(PropertyKeys.stickyTopAt, null);
    }

    public void setStickyTopAt(String stickyTopAt) {
        getStateHelper().put(PropertyKeys.stickyTopAt, stickyTopAt);
    }

    @Override
    public MethodExpression getGlobalFilterFunction() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.globalFilterFunction, null);
    }

    @Override
    public void setGlobalFilterFunction(MethodExpression globalFilterFunction) {
        getStateHelper().put(PropertyKeys.globalFilterFunction, globalFilterFunction);
    }

    public boolean isRenderEmptyFacets() {
        return (Boolean) getStateHelper().eval(PropertyKeys.renderEmptyFacets, false);
    }

    public void setRenderEmptyFacets(boolean renderEmptyFacets) {
        getStateHelper().put(PropertyKeys.renderEmptyFacets, renderEmptyFacets);
    }

    public boolean isSelectionPageOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectionPageOnly, true);
    }

    public void setSelectionPageOnly(boolean selectionPageOnly) {
        getStateHelper().put(PropertyKeys.selectionPageOnly, selectionPageOnly);
    }

    public boolean isPartialUpdate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.partialUpdate, true);
    }

    public void setPartialUpdate(boolean partialUpdate) {
        getStateHelper().put(PropertyKeys.partialUpdate, partialUpdate);
    }

    public boolean isShowSelectAll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showSelectAll, true);
    }

    public void setShowSelectAll(boolean showSelectAll) {
        getStateHelper().put(PropertyKeys.showSelectAll, showSelectAll);
    }

    public String getExportRowTag() {
        return (String) getStateHelper().eval(PropertyKeys.exportRowTag, null);
    }

    public void setExportRowTag(String exportRowTag) {
        getStateHelper().put(PropertyKeys.exportRowTag, exportRowTag);
    }

    public String getExportTag() {
        return (String) getStateHelper().eval(PropertyKeys.exportTag, null);
    }

    public void setExportTag(String exportTag) {
        getStateHelper().put(PropertyKeys.exportTag, exportTag);
    }

    public boolean isSelectAllFilteredOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectAllFilteredOnly, false);
    }

    public void setSelectAllFilteredOnly(boolean selectAllFilteredOnly) {
        getStateHelper().put(PropertyKeys.selectAllFilteredOnly, selectAllFilteredOnly);
    }

    public Boolean isCellNavigation() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cellNavigation, null);
    }

    public void setCellNavigation(boolean cellNavigation) {
        getStateHelper().put(PropertyKeys.cellNavigation, cellNavigation);
    }

    @Override
    public boolean isFilterNormalize() {
        return (Boolean) getStateHelper().eval(PropertyKeys.filterNormalize, false);
    }

    public void setFilterNormalize(boolean filterNormalize) {
        getStateHelper().put(PropertyKeys.filterNormalize, filterNormalize);
    }
}
