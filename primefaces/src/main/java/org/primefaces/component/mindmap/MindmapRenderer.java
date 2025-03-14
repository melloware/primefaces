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
package org.primefaces.component.mindmap;

import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class MindmapRenderer extends CoreRenderer<Mindmap> {

    @Override
    public void decode(FacesContext context, Mindmap component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Mindmap component) throws IOException {
        if (component.isNodeSelectRequest(context)) {
            MindmapNode node = component.getSelectedNode();

            encodeNode(context, component, node, component.getSelectedNodeKey(context));
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeScript(FacesContext context, Mindmap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MindmapNode root = map.getValue();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Mindmap", map)
                .attr("effectSpeed", map.getEffectSpeed());

        if (root != null) {
            writer.write(",model:");
            encodeNode(context, map, root, "root");
        }

        encodeClientBehaviors(context, map);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Mindmap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? Mindmap.STYLE_CLASS : Mindmap.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.endElement("div");
    }

    protected void encodeNode(FacesContext context, Mindmap component, MindmapNode node, String nodeKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<MindmapNode> children = node.getChildren();
        MindmapNode parent = node.getParent();

        writer.write("{");

        encodeNodeConfig(context, component, node, nodeKey);

        if (parent != null) {
            String parentNodeKey = (nodeKey.indexOf('_') != -1) ? nodeKey.substring(0, nodeKey.lastIndexOf('_')) : "root";

            writer.write(",\"parent\":{");
            encodeNodeConfig(context, component, parent, parentNodeKey);
            writer.write("}");
        }

        if (!children.isEmpty()) {
            int size = children.size();

            writer.write(",\"children\":[");

            for (int i = 0; i < size; i++) {
                String childKey = ("root".equals(nodeKey)) ? String.valueOf(i) : nodeKey + "_" + i;

                MindmapNode child = children.get(i);
                encodeNode(context, component, child, childKey);

                if (i != (size - 1)) {
                    writer.write(",");
                }
            }

            writer.write("]");
        }

        writer.write("}");
    }

    protected void encodeNodeConfig(FacesContext context, Mindmap component, MindmapNode node, String nodeKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write("\"label\":\"" + node.getLabel() + "\"");

        if (nodeKey != null) {
            writer.write(",\"key\":\"" + nodeKey + "\"");
        }
        if (node.getFill() != null) {
            writer.write(",\"fill\":\"" + node.getFill() + "\"");
        }
        if (node.isSelectable()) {
            writer.write(",\"selectable\":true");
        }
    }
}
