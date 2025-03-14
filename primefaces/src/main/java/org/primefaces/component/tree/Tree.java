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
package org.primefaces.component.tree;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.el.MethodExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.BehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Tree extends TreeBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Tree";

    public static final String FILTER_CLASS = "ui-tree-filter ui-inputfield ui-inputtext ui-widget ui-state-default";
    public static final String FILTER_CONTAINER = "ui-tree-filter-container";
    public static final String CONTAINER_CLASS = "ui-tree ui-widget ui-widget-content";
    public static final String CONTAINER_RTL_CLASS = "ui-tree ui-tree-rtl ui-widget ui-widget-content";
    public static final String HORIZONTAL_CONTAINER_CLASS = "ui-tree ui-tree-horizontal ui-widget ui-widget-content";
    public static final String HORIZONTAL_CONTAINER_RTL_CLASS = "ui-tree ui-tree-horizontal ui-tree-rtl ui-widget ui-widget-content";
    public static final String ROOT_NODES_CLASS = "ui-tree-container";
    public static final String PARENT_NODE_CLASS = "ui-treenode ui-treenode-parent";
    public static final String LEAF_NODE_CLASS = "ui-treenode ui-treenode-leaf";
    public static final String CHILDREN_NODES_CLASS = "ui-treenode-children";
    public static final String NODE_CONTENT_CLASS_V = "ui-treenode-content";
    public static final String SELECTABLE_NODE_CONTENT_CLASS_V = "ui-treenode-content ui-tree-selectable";
    public static final String NODE_CONTENT_CLASS_H = "ui-treenode-content ui-state-default";
    public static final String SELECTABLE_NODE_CONTENT_CLASS_H = "ui-treenode-content ui-tree-selectable ui-state-default";
    public static final String EXPANDED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-s";
    public static final String COLLAPSED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-e";
    public static final String COLLAPSED_ICON_RTL_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-w";
    public static final String EXPANDED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-minus";
    public static final String COLLAPSED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-plus";
    public static final String LEAF_ICON_CLASS = "ui-treenode-leaf-icon";
    public static final String NODE_ICON_CLASS = "ui-treenode-icon ui-icon";
    public static final String NODE_LABEL_CLASS = "ui-treenode-label";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("select", NodeSelectEvent.class)
            .put("unselect", NodeUnselectEvent.class)
            .put("expand", NodeExpandEvent.class)
            .put("collapse", NodeCollapseEvent.class)
            .put("dragdrop", TreeDragDropEvent.class)
            .put("contextMenu", NodeSelectEvent.class)
            .put("filter", null)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    private Map<String, UITreeNode> nodes;
    private TreeNode<?> dragNode;
    private TreeNode<?>[] dragNodes;
    private TreeNode<?> dropNode;
    private boolean retValOnDrop = true;
    private List<String> filteredRowKeys = new ArrayList<>();

    public UITreeNode getUITreeNodeByType(String type) {
        UITreeNode node = getTreeNodes().get(type);

        if (node == null) {
            throw new jakarta.faces.FacesException("Unsupported tree node type:" + type);
        }
        else {
            return node;
        }
    }

    public boolean isFiltering() {
        return getValueExpression("filterBy") != null || getFilterFunction() != null;
    }

    public boolean isNodeExpandRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_expandNode");
    }

    public boolean isSelectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_instantSelection");
    }

    public boolean isFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_filtering");
    }

    public Map<String, UITreeNode> getTreeNodes() {
        if (nodes == null) {
            nodes = new HashMap<>();
            for (UIComponent child : getChildren()) {
                if (child instanceof UITreeNode) {
                    UITreeNode node = (UITreeNode) child;
                    nodes.put(node.getType(), node);
                }
            }
        }

        return nodes;
    }

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            TreeNode<?> root = getValue();

            if ("expand".equals(eventName)) {
                setRowKey(root, params.get(clientId + "_expandNode"));
                TreeNode<?> expandedNode = getRowNode();
                expandedNode.setExpanded(true);

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), expandedNode);
            }
            else if ("collapse".equals(eventName)) {
                setRowKey(root, params.get(clientId + "_collapseNode"));
                TreeNode<?> collapsedNode = getRowNode();
                collapsedNode.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), collapsedNode);
            }
            else if ("select".equals(eventName)) {
                setRowKey(root, params.get(clientId + "_instantSelection"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), getRowNode());
            }
            else if ("unselect".equals(eventName)) {
                setRowKey(root, params.get(clientId + "_instantUnselection"));

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), getRowNode());
            }
            else if ("dragdrop".equals(eventName)) {
                if (!retValOnDrop) {
                    return;
                }

                int dndIndex = Integer.parseInt(params.get(clientId + "_dndIndex"));
                boolean isDroppedNodeCopy = Boolean.parseBoolean(params.get(clientId + "_isDroppedNodeCopy"));

                if (isMultipleDrag()) {
                    wrapperEvent = new TreeDragDropEvent(this, behaviorEvent.getBehavior(), dragNodes, dropNode, dndIndex, isDroppedNodeCopy);
                }
                else {
                    wrapperEvent = new TreeDragDropEvent(this, behaviorEvent.getBehavior(), dragNode, dropNode, dndIndex, isDroppedNodeCopy);
                }
            }
            else if ("contextMenu".equals(eventName)) {
                setRowKey(root, params.get(clientId + "_contextMenuNode"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), getRowNode(), true);
            }
            else if ("filter".equals(eventName)) {
                wrapperEvent = behaviorEvent;
            }

            if (wrapperEvent == null) {
                throw new FacesException("Component " + this.getClass().getName() + " does not support event " + eventName + "!");
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);

            setRowKey(root, null);
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isToggleRequest(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_expandNode") != null || params.get(clientId + "_collapseNode") != null;
    }

    public boolean isDragDropRequest(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);
        String source = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);

        return clientId.equals(source) && params.get(clientId + "_dragdrop") != null;
    }

    private boolean shouldSkipNodes(FacesContext context) {
        return isToggleRequest(context) || isDragDropRequest(context);
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered() || isDisabled()) {
            return;
        }
        if (shouldSkipNodes(context)) {
            decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!shouldSkipNodes(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (shouldSkipNodes(context)) {
            if (isSelectionEnabled()) {
                updateSelection(context);
            }
        }
        else {
            super.processUpdates(context);
        }
    }

    TreeNode<?> getDragNode() {
        return dragNode;
    }

    void setDragNode(TreeNode<?> dragNode) {
        this.dragNode = dragNode;
    }

    TreeNode<?>[] getDragNodes() {
        return dragNodes;
    }

    void setDragNodes(TreeNode<?>[] dragNodes) {
        this.dragNodes = dragNodes;
    }

    TreeNode<?> getDropNode() {
        return dropNode;
    }

    void setDropNode(TreeNode<?> dropNode) {
        this.dropNode = dropNode;
    }

    @Override
    protected boolean shouldVisitNode(TreeNode<?> node) {
        return !isDynamic() || (node.isExpanded() || node.getParent() == null);
    }

    @Override
    protected void processColumnChildren(FacesContext context, PhaseId phaseId, TreeNode<?> root, String nodeKey) {
        setRowKey(root, nodeKey);
        TreeNode<?> treeNode = getRowNode();

        if (treeNode == null) {
            return;
        }

        String treeNodeType = treeNode.getType();

        for (UIComponent child : getChildren()) {
            if (child instanceof UITreeNode && child.isRendered()) {
                UITreeNode uiTreeNode = (UITreeNode) child;

                if (!treeNodeType.equals(uiTreeNode.getType())) {
                    continue;
                }

                for (UIComponent grandkid : child.getChildren()) {
                    if (!grandkid.isRendered()) {
                        continue;
                    }

                    if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                        grandkid.processDecodes(context);
                    }
                    else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                        grandkid.processValidators(context);
                    }
                    else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                        grandkid.processUpdates(context);
                    }
                    else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

    public TreeNode<?> createCopyOfTreeNode(TreeNode<?> node) {
        TreeNode newNode;
        if (node instanceof CheckboxTreeNode) {
            newNode = new CheckboxTreeNode<>(node.getData());
        }
        else {
            newNode = new DefaultTreeNode<>(node.getData());
        }

        newNode.setType(node.getType());
        newNode.setSelectable(node.isSelectable());
        newNode.setExpanded(node.isExpanded());

        if (node.getChildCount() > 0) {
            for (TreeNode childNode : node.getChildren()) {
                newNode.getChildren().add(createCopyOfTreeNode(childNode));
            }
        }

        return newNode;
    }

    public boolean isTreeNodeDropped() {
        MethodExpression me = getOnDrop();
        if (me != null) {
            FacesContext context = getFacesContext();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            int dndIndex = Integer.parseInt(params.get(clientId + "_dndIndex"));
            boolean isDroppedNodeCopy = Boolean.parseBoolean(params.get(clientId + "_isDroppedNodeCopy"));
            TreeDragDropInfo info;

            if (isMultipleDrag()) {
                info = new TreeDragDropInfo(this, getDragNodes(), getDropNode(), dndIndex, isDroppedNodeCopy);
            }
            else {
                info = new TreeDragDropInfo(this, getDragNode(), getDropNode(), dndIndex, isDroppedNodeCopy);
            }

            retValOnDrop = (Boolean) me.invoke(context.getELContext(), new Object[]{info});
            PrimeFaces.current().ajax().addCallbackParam("access", retValOnDrop);
        }

        return retValOnDrop;
    }

    public String getScrollState() {
        Map<String, String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = getClientId() + "_scrollState";
        String value = params.get(name);

        return value == null ? "0,0" : value;
    }

    public List<String> getFilteredRowKeys() {
        return filteredRowKeys;
    }

    public void setFilteredRowKeys(List<String> filteredRowKeys) {
        this.filteredRowKeys = filteredRowKeys;
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        nodes = null;
        dragNode = null;
        dragNodes = null;
        dropNode = null;
        retValOnDrop = true;
        filteredRowKeys = new ArrayList<>();

        return super.saveState(context);
    }

}
