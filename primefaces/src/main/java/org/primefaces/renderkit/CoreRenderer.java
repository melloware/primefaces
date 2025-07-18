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
package org.primefaces.renderkit;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.convert.ClientConverter;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.StyleBuilder;
import org.primefaces.util.StyleClassBuilder;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;
import org.primefaces.validate.bean.BeanValidationMetadata;
import org.primefaces.validate.bean.BeanValidationMetadataMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.el.PropertyNotFoundException;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIParameter;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.behavior.ClientBehavior;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.render.Renderer;
import jakarta.faces.validator.Validator;

public abstract class CoreRenderer<T extends UIComponent> extends Renderer<T> {

    private static final Logger LOGGER = Logger.getLogger(CoreRenderer.class.getName());

    private static final String SB_RENDER_DOM_EVENTS = CoreRenderer.class.getName() + "#renderDomEvents";
    private static final String SB_BUILD_NON_AJAX_REQUEST = CoreRenderer.class.getName() + "#buildNonAjaxRequest";
    private static final String SB_GET_EVENT_BEHAVIORS = CoreRenderer.class.getName() + "#getEventBehaviors";
    private static final String SB_RENDER_VALIDATOR_IDS = CoreRenderer.class.getName() + "#renderValidatorIds";

    protected void renderChildren(FacesContext context, UIComponent component) throws IOException {
        if (component.getChildCount() > 0) {
            for (int i = 0; i < component.getChildCount(); i++) {
                UIComponent child = component.getChildren().get(i);
                renderChild(context, child);
            }
        }
    }

    protected UIComponent renderChild(FacesContext context, UIComponent child) throws IOException {
        if (!child.isRendered()) {
            return child;
        }

        child.encodeBegin(context);

        if (child.getRendersChildren()) {
            child.encodeChildren(context);
        }
        else {
            renderChildren(context, child);
        }
        child.encodeEnd(context);
        return child;
    }

    protected String getResourceURL(FacesContext context, String value) {
        return ResourceUtils.getResourceURL(context, value);
    }

    protected String getResourceRequestPath(FacesContext context, String resourceName) {
        return ResourceUtils.getResourceRequestPath(context, resourceName);
    }

    protected void renderPassThruAttributes(FacesContext context, UIComponent component, List<String> attrs) throws IOException {
        //pre-defined attributes
        if (attrs != null && !attrs.isEmpty()) {
            ResponseWriter writer = context.getResponseWriter();

            for (int i = 0; i < attrs.size(); i++) {
                String attribute = attrs.get(i);
                Object value = component.getAttributes().get(attribute);
                if (shouldRenderAttribute(value)) {
                    writer.writeAttribute(attribute, value.toString(), attribute);
                }
            }
        }

        renderDynamicPassThruAttributes(context, component);
    }

    @SafeVarargs
    protected final void renderPassThruAttributes(FacesContext context, UIComponent component, List<String>... attrs) throws IOException {
        if (attrs == null || attrs.length == 0) {
            renderDynamicPassThruAttributes(context, component);
            return;
        }

        for (List<String> a : attrs) {
            renderPassThruAttributes(context, component, a);
        }
    }

    protected void renderDynamicPassThruAttributes(FacesContext context, UIComponent component) throws IOException {
        renderPassThroughAttributes(context, component);
    }

    protected void renderDomEvents(FacesContext context, UIComponent component, List<String> eventAttrs) throws IOException {
        if (component instanceof ClientBehaviorHolder) {
            renderDomEvents(context, component, eventAttrs, ((ClientBehaviorHolder) component).getClientBehaviors());
        }
        else {
            renderPassThruAttributes(context, component, eventAttrs);
        }
    }

    private void renderDomEvents(FacesContext context, UIComponent component, List<String> eventAttrs, Map<String, List<ClientBehavior>> behaviors)
            throws IOException {

        if (eventAttrs == null || eventAttrs.isEmpty()) {
            return;
        }

        StringBuilder builder = null;

        for (int i = 0; i < eventAttrs.size(); i++) {
            String domEvent = eventAttrs.get(i);
            Object eventValue = component.getAttributes().get(domEvent);
            String behaviorEvent = domEvent.substring(2, domEvent.length());
            List<ClientBehavior> eventBehaviors = behaviors.get(behaviorEvent);
            boolean hasEventValue = (eventValue != null);
            boolean hasEventBehaviors = (eventBehaviors != null && !eventBehaviors.isEmpty());

            if ("onchange".equals(domEvent) && !hasEventBehaviors) {
                eventBehaviors = behaviors.get("valueChange");
                hasEventBehaviors = (eventBehaviors != null && !eventBehaviors.isEmpty());
                if (hasEventBehaviors) {
                    behaviorEvent = "valueChange";
                }
            }

            if (hasEventValue || hasEventBehaviors) {
                if (builder == null) {
                    builder = SharedStringBuilder.get(context, SB_RENDER_DOM_EVENTS);
                }

                if (hasEventBehaviors) {
                    String clientId = component.getClientId(context);

                    List<ClientBehaviorContext.Parameter> params = new ArrayList<>(1);
                    params.add(new ClientBehaviorContext.Parameter(
                            Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.OBSTRUSIVE));

                    ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                            context, component, behaviorEvent, clientId, params);
                    int size = eventBehaviors.size();
                    boolean chained = false;

                    if (size > 1 || hasEventValue) {
                        builder.append("PrimeFaces.bcn(this,event,[");

                        if (hasEventValue) {
                            builder.append("function(event){").append(eventValue).append("}");
                            chained = true;
                        }

                        for (int j = 0; j < size; j++) {
                            ClientBehavior behavior = eventBehaviors.get(j);
                            String script = behavior.getScript(cbc);
                            if (script != null) {
                                if (chained) {
                                    builder.append(",");
                                }
                                builder.append("function(event){").append(script).append("}");
                                chained = true;
                            }
                        }
                        builder.append("])");
                    }
                    else {
                        ClientBehavior behavior = eventBehaviors.get(0);
                        String script = behavior.getScript(cbc);
                        if (script != null) {
                            builder.append(script);
                        }
                    }
                }
                else if (shouldRenderAttribute(eventValue)) {
                    builder.append(eventValue);
                }

                if (builder.length() > 0) {
                    renderAttribute(context, component, domEvent, builder.toString());
                    builder.setLength(0);
                }
            }
        }
    }

    protected void renderPassThruAttributes(FacesContext context, UIComponent component, String[] attrs, String[] ignoredAttrs)
            throws IOException {
        //pre-defined attributes
        for (String attribute : attrs) {
            if (isIgnoredAttribute(attribute, ignoredAttrs)) {
                continue;
            }

            Object value = component.getAttributes().get(attribute);

            renderAttribute(context, component, attribute, value);
        }

        //dynamic attributes
        renderPassThroughAttributes(context, component);
    }

    protected void renderAttribute(FacesContext context, UIComponent component, String attribute, Object value)
                throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (shouldRenderAttribute(value)) {
            String stringValue = value.toString();
            if (Boolean.parseBoolean(stringValue)) {
                writer.writeAttribute(attribute, true, attribute);
            }
            else {
                writer.writeAttribute(attribute, stringValue, attribute);
            }
        }
    }

    protected void renderOnchange(FacesContext context, UIComponent component) throws IOException {
        this.renderDomEvent(context, component, "onchange", "change", "valueChange", null);
    }

    protected void renderOnclick(FacesContext context, UIComponent component, String command) throws IOException {
        this.renderDomEvent(context, component, "onclick", "click", "action", command);
    }

    protected void renderDomEvent(FacesContext context, UIComponent component, String domEvent, String behaviorEvent,
            String behaviorEventAlias, String command) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String callback = buildDomEvent(context, component, domEvent, behaviorEvent, behaviorEventAlias, command);
        if (callback != null) {
            writer.writeAttribute(domEvent, callback, domEvent);
        }
    }

    /**
     * Renders a hidden input field commonly use for holding state for a component.
     *
     * @param context the FacesContext
     * @param id the id of the hidden field
     * @param value the value of the hidden field, can be NULL
     * @param disabled whether the hidden field is disabled or not
     * @throws IOException if any error occurs
     */
    protected void renderHiddenInput(FacesContext context, String id, String value, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "hidden", null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        // autocomplete="off" is invalid HTML but fixes Firefox caching issues
        if (AgentUtils.isFirefox(context)) {
            writer.writeAttribute("autocomplete", "off", null);
        }
        writer.endElement("input");
    }

    public <T extends UIComponent & RTLAware> void renderRTLDirection(FacesContext context, T component) throws IOException {
        if (ComponentUtils.isRTL(context, component)) {
            context.getResponseWriter().writeAttribute("dir", "rtl", null);
        }
    }

    protected String buildDomEvent(FacesContext context, UIComponent component, String domEvent, String behaviorEvent,
            String behaviorEventAlias, String command) {

        StringBuilder builder = null;

        boolean hasCommand = (command != null);

        Map<String, List<ClientBehavior>> allBehaviors = null;
        if (component instanceof ClientBehaviorHolder) {
            allBehaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        }

        Object event = component.getAttributes().get(domEvent);
        boolean hasEvent = (event != null);

        String behaviorEventName = behaviorEventAlias;
        if (allBehaviors != null && allBehaviors.containsKey(behaviorEvent)) {
            behaviorEventName = behaviorEvent;
        }

        List<ClientBehavior> behaviors = (allBehaviors == null) ? null : allBehaviors.get(behaviorEventName);
        boolean hasBehaviors = (behaviors != null && !behaviors.isEmpty());

        if (hasEvent || hasBehaviors || hasCommand) {
            if (builder == null) {
                builder = SharedStringBuilder.get(context, SB_RENDER_DOM_EVENTS);
            }

            int commandSize = 0;
            if (hasBehaviors) {
                commandSize += behaviors.size();
            }
            if (hasEvent) {
                commandSize++;
            }
            if (hasCommand) {
                commandSize++;
            }

            if (commandSize > 1) {
                boolean first = true;
                builder.append("PrimeFaces.bcn(this,event,[");

                if (hasEvent) {
                    builder.append("function(event){").append(event).append("}");
                    first = false;
                }

                if (hasBehaviors) {
                    ClientBehaviorContext cbc = null;
                    for (int i = 0; i < behaviors.size(); i++) {
                        ClientBehavior behavior = behaviors.get(i);
                        if (cbc == null) {
                            cbc = ClientBehaviorContext.createClientBehaviorContext(context, component, behaviorEventName,
                                    component.getClientId(context), Collections.emptyList());
                        }
                        String script = behavior.getScript(cbc);

                        if (script != null) {
                            if (!first) {
                                builder.append(",");
                            }

                            builder.append("function(event){").append(script).append("}");
                            first = false;
                        }
                    }
                }

                if (hasCommand) {
                    if (!first) {
                        builder.append(",");
                    }

                    builder.append("function(event){").append(command).append("}");
                    first = false;
                }

                builder.append("]);");
            }
            else {
                if (hasBehaviors) {
                    ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                            context, component, behaviorEventName, component.getClientId(context), Collections.emptyList());
                    ClientBehavior behavior = behaviors.get(0);
                    String script = behavior.getScript(cbc);
                    if (script != null) {
                        builder.append(script);
                    }
                }
                else if (hasCommand) {
                    builder.append(command);
                }
                else if (hasEvent) {
                    builder.append(event);
                }
            }
        }

        return (builder == null) ? null : builder.toString();
    }

    private boolean isIgnoredAttribute(String attribute, String[] ignoredAttrs) {
        for (String ignoredAttribute : ignoredAttrs) {
            if (attribute.equals(ignoredAttribute)) {
                return true;
            }
        }

        return false;
    }

    protected boolean shouldRenderAttribute(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        else if (value instanceof Number) {
            Number number = (Number) value;

            if (value instanceof Integer) {
                return number.intValue() != Integer.MIN_VALUE;
            }
            else if (value instanceof Double) {
                return number.doubleValue() != Double.MIN_VALUE;
            }
            else if (value instanceof Long) {
                return number.longValue() != Long.MIN_VALUE;
            }
            else if (value instanceof Byte) {
                return number.byteValue() != Byte.MIN_VALUE;
            }
            else if (value instanceof Float) {
                return number.floatValue() != Float.MIN_VALUE;
            }
            else if (value instanceof Short) {
                return number.shortValue() != Short.MIN_VALUE;
            }
        }

        return true;
    }

    public boolean isValueBlank(String value) {
        return LangUtils.isBlank(value);
    }

    protected <T extends UIComponent & AjaxSource> AjaxRequestBuilder preConfiguredAjaxRequestBuilder(FacesContext context, T source) {
        return preConfiguredAjaxRequestBuilder(context, source, source, null);
    }

    protected AjaxRequestBuilder preConfiguredAjaxRequestBuilder(FacesContext context, UIComponent component, AjaxSource source, UIForm form) {
        String clientId = component.getClientId(context);
        AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance(context).getAjaxRequestBuilder();

        builder.init()
                .source(clientId)
                .form(source, component, form)
                .process(component, source.getProcess(), source.isIgnoreComponentNotFound())
                .update(component, source.getUpdate(), source.isIgnoreComponentNotFound())
                .async(source.isAsync())
                .global(source.isGlobal())
                .delay(source.getDelay())
                .timeout(source.getTimeout())
                .partialSubmit(source.isPartialSubmit(), source.isPartialSubmitSet(), source.getPartialSubmitFilter())
                .resetValues(source.isResetValues(), source.isResetValuesSet())
                .ignoreAutoUpdate(source.isIgnoreAutoUpdate())
                .onstart(source.getOnstart())
                .onerror(source.getOnerror())
                .onsuccess(source.getOnsuccess())
                .oncomplete(source.getOncomplete());

        return builder;
    }

    protected <T extends UIComponent & AjaxSource> String buildAjaxRequest(FacesContext context, T source) {
        return buildAjaxRequest(context, source, null);
    }

    protected <T extends UIComponent & AjaxSource> String buildAjaxRequest(FacesContext context, T source, UIForm form) {
        AjaxRequestBuilder builder = preConfiguredAjaxRequestBuilder(context, source, source, form)
                .params(source)
                .preventDefault();

        return builder.build();
    }

    protected String buildAjaxRequest(FacesContext context, UIComponent component, AjaxSource source, UIForm form, Map<String, List<String>> params) {
        AjaxRequestBuilder builder = preConfiguredAjaxRequestBuilder(context, component, source, form)
                .params(params)
                .preventDefault();

        return builder.build();
    }

    protected String buildNonAjaxRequest(FacesContext context, UIComponent component, UIComponent form, String decodeParam, boolean submit) {
        return buildNonAjaxRequest(context, component, form, decodeParam, Collections.emptyMap(), submit);
    }

    protected String buildNonAjaxRequest(FacesContext context, UIComponent component, UIComponent form, String decodeParam,
                                         Map<String, List<String>> parameters, boolean submit) {
        StringBuilder request = SharedStringBuilder.get(context, SB_BUILD_NON_AJAX_REQUEST);

        String submitId;
        if (form == null) {
            submitId = component.getClientId(context);
        }
        else {
            submitId = form.getClientId(context);
        }

        Map<String, Object> params = new HashMap<>();

        if (decodeParam != null) {
            params.put(decodeParam, decodeParam);
        }

        for (int i = 0; i < component.getChildCount(); i++) {
            UIComponent child = component.getChildren().get(i);
            if (child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;
                params.put(param.getName(), param.getValue());
            }
        }

        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((k, v) -> params.put(k, v.get(0)));
        }

        //append params
        if (!params.isEmpty()) {
            request.append("PrimeFaces.addSubmitParam('").append(submitId).append("',{");

            request.append(
                    params.entrySet().stream()
                            .map(e -> "'" + e.getKey() + "':'" + EscapeUtils.forJavaScript(String.valueOf(e.getValue())) + "'")
                            .collect(Collectors.joining(","))
            );

            request.append("})");
        }

        if (submit) {
            Object target = component.getAttributes().get("target");
            request.append(".submit('").append(submitId).append("'");

            if (target != null) {
                request.append(",'").append(target).append("'");
            }

            request.append(");return false;");
        }

        if (!submit && !params.isEmpty()) {
            request.append(";");
        }

        return request.toString();
    }

    protected void encodeClientBehaviors(FacesContext context, ClientBehaviorHolder component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, List<ClientBehavior>> clientBehaviors = component.getClientBehaviors();

        if (clientBehaviors != null && !clientBehaviors.isEmpty()) {
            boolean written = false;
            Collection<String> eventNames = (component instanceof MixedClientBehaviorHolder)
                    ? ((MixedClientBehaviorHolder) component).getUnobstrusiveEventNames() : clientBehaviors.keySet();
            String clientId = ((UIComponent) component).getClientId(context);
            List<ClientBehaviorContext.Parameter> params = new ArrayList<>(1);
            params.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));

            writer.write(",behaviors:{");
            for (Iterator<String> eventNameIterator = eventNames.iterator(); eventNameIterator.hasNext();) {
                String eventName = eventNameIterator.next();
                List<ClientBehavior> eventBehaviors = clientBehaviors.get(eventName);

                if (eventBehaviors != null && !eventBehaviors.isEmpty()) {
                    if (written) {
                        writer.write(",");
                    }

                    int eventBehaviorsSize = eventBehaviors.size();

                    writer.write(eventName + ":");
                    writer.write("function(ext,event) {");

                    if (eventBehaviorsSize > 1) {
                        boolean chained = false;
                        writer.write("PrimeFaces.bcnu(ext,event,[");

                        for (int i = 0; i < eventBehaviorsSize; i++) {
                            ClientBehavior behavior = eventBehaviors.get(i);
                            ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                                    context, (UIComponent) component, eventName, clientId, params);
                            String script = behavior.getScript(cbc);

                            if (script != null) {
                                if (chained) {
                                    writer.write(",");
                                }

                                writer.write("function(ext,event) {");
                                writer.write(script);
                                writer.write("}");

                                chained = true;
                            }
                        }

                        writer.write("]);");
                    }
                    else {
                        ClientBehavior behavior = eventBehaviors.get(0);
                        ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                                context, (UIComponent) component, eventName, clientId, params);
                        String script = behavior.getScript(cbc);

                        if (script != null) {
                            writer.write(script);
                        }
                    }

                    writer.write("}");

                    written = true;
                }
            }

            writer.write("}");
        }
    }

    protected void decodeBehaviors(FacesContext context, UIComponent component) {
        ComponentUtils.decodeBehaviors(context, component);
    }

    protected String getEventBehaviors(FacesContext context, ClientBehaviorHolder cbh, String event,
            List<ClientBehaviorContext.Parameter> parameters) {

        List<ClientBehavior> behaviors = cbh.getClientBehaviors().get(event);
        StringBuilder sb = SharedStringBuilder.get(context, SB_GET_EVENT_BEHAVIORS);

        if (behaviors != null && !behaviors.isEmpty()) {
            UIComponent component = (UIComponent) cbh;
            String clientId = component.getClientId(context);
            List<ClientBehaviorContext.Parameter> params;
            if (parameters != null && !parameters.isEmpty()) {
                params = parameters;
            }
            else {
                params = Collections.emptyList();
            }

            for (int i = 0; i < behaviors.size(); i++) {
                ClientBehavior behavior = behaviors.get(i);
                ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(
                        context, component, event, clientId, params);
                String script = behavior.getScript(cbc);

                if (script != null) {
                    sb.append(script).append(";");
                }
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    protected boolean shouldWriteId(UIComponent component) {
        String id = component.getId();

        return (null != id) && (!id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX) || ((component instanceof ClientBehaviorHolder)
                && !((ClientBehaviorHolder) component).getClientBehaviors().isEmpty()));
    }

    protected WidgetBuilder getWidgetBuilder(FacesContext context) {
        return PrimeRequestContext.getCurrentInstance(context).getWidgetBuilder();
    }

    protected StyleClassBuilder getStyleClassBuilder(FacesContext context) {
        return PrimeRequestContext.getCurrentInstance(context).getStyleClassBuilder();
    }

    protected StyleBuilder getStyleBuilder(FacesContext context) {
        return PrimeRequestContext.getCurrentInstance(context).getStyleBuilder();
    }

    protected void renderValidationMetadata(FacesContext context, EditableValueHolder component,
                                            ClientValidator... clientValidators) throws IOException {
        if (!PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            return; //GitHub #4049: short circuit method
        }

        ResponseWriter writer = context.getResponseWriter();
        UIComponent comp = (UIComponent) component;

        Converter<?> converter;

        try {
            converter = ComponentUtils.getConverter(context, comp);
        }
        catch (PropertyNotFoundException e) {
            String message = "Skip rendering of CSV metadata for component \"" + comp.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOGGER.log(Level.FINE, message);
            return;
        }

        Map<String, Object> attrs = comp.getAttributes();
        Object label = attrs.get("label");
        Object requiredMessage = attrs.get("requiredMessage");
        Object validatorMessage = attrs.get("validatorMessage");
        Object converterMessage = attrs.get("converterMessage");
        List<String> validatorIds = null;
        String highlighter = getHighlighter();


        //messages
        if (label != null) writer.writeAttribute(HTML.ValidationMetadata.LABEL, label, null);
        if (requiredMessage != null) writer.writeAttribute(HTML.ValidationMetadata.REQUIRED_MESSAGE, requiredMessage, null);
        if (validatorMessage != null) writer.writeAttribute(HTML.ValidationMetadata.VALIDATOR_MESSAGE, validatorMessage, null);
        if (converterMessage != null) writer.writeAttribute(HTML.ValidationMetadata.CONVERTER_MESSAGE, converterMessage, null);

        //converter
        if (converter instanceof ClientConverter) {
            ClientConverter clientConverter = (ClientConverter) converter;
            Map<String, Object> metadata = clientConverter.getMetadata();

            writer.writeAttribute(HTML.ValidationMetadata.CONVERTER, ((ClientConverter) converter).getConverterId(), null);

            if (metadata != null && !metadata.isEmpty()) {
                renderValidationMetadataMap(context, metadata);
            }
        }

        //bean validation
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);
        if (applicationContext.getConfig().isBeanValidationEnabled()) {
            BeanValidationMetadata beanValidationMetadata = BeanValidationMetadataMapper.resolveValidationMetadata(context, comp, applicationContext);
            if (beanValidationMetadata != null) {
                if (beanValidationMetadata.getAttributes() != null) {
                    renderValidationMetadataMap(context, beanValidationMetadata.getAttributes());
                }
                if (beanValidationMetadata.getValidatorIds() != null) {
                    if (validatorIds == null) {
                        validatorIds = new ArrayList<>(5);
                    }
                    validatorIds.addAll(beanValidationMetadata.getValidatorIds());
                }
            }
        }

        //required validation
        if (component.isRequired()) {
            writer.writeAttribute(HTML.ValidationMetadata.REQUIRED, "true", null);
        }

        //validators
        Validator<?>[] validators = component.getValidators();
        if (validators != null) {
            for (Validator<?> validator : validators) {
                if (validator instanceof ClientValidator) {
                    ClientValidator clientValidator = (ClientValidator) validator;
                    if (validatorIds == null) {
                        validatorIds = new ArrayList<>(5);
                    }
                    validatorIds.add(clientValidator.getValidatorId());

                    Map<String, Object> metadata = clientValidator.getMetadata();
                    if (metadata != null && !metadata.isEmpty()) {
                        renderValidationMetadataMap(context, metadata);
                    }
                }
            }
        }
        if (clientValidators != null) {
            for (ClientValidator clientValidator : clientValidators) {
                if (validatorIds == null) {
                    validatorIds = new ArrayList<>(5);
                }
                validatorIds.add(clientValidator.getValidatorId());

                Map<String, Object> metadata = clientValidator.getMetadata();
                if (metadata != null && !metadata.isEmpty()) {
                    renderValidationMetadataMap(context, metadata);
                }
            }
        }

        renderValidatorIds(context, validatorIds);

        if (highlighter != null) {
            writer.writeAttribute(HTML.ValidationMetadata.HIGHLIGHTER, highlighter, null);
        }

        if (isGrouped()) {
            writer.writeAttribute(HTML.ValidationMetadata.GROUPED, "true", null);
        }
    }

    private void renderValidationMetadataMap(FacesContext context, Map<String, Object> metadata) throws IOException {
        if (metadata == null || metadata.isEmpty()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value != null) {
                writer.writeAttribute(key, value, null);
            }
        }
    }

    private void renderValidatorIds(FacesContext context, List<String> validatorIds) throws IOException {
        if (validatorIds == null || validatorIds.isEmpty()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        StringBuilder builder = SharedStringBuilder.get(context, SB_RENDER_VALIDATOR_IDS);

        for (int i = 0; i < validatorIds.size(); i++) {
            if (i != 0) {
                builder.append(',');
            }

            String validatorId = validatorIds.get(i);
            builder.append(validatorId);
        }

        writer.writeAttribute(HTML.ValidationMetadata.VALIDATOR_IDS, builder.toString(), null);
    }

    protected String getHighlighter() {
        return null;
    }

    protected boolean isGrouped() {
        return false;
    }

    /**
     * Used by script-only widget to fix #3265 and allow updating of the component.
     *
     * @param context the {@link FacesContext}.
     * @param component the widget without actual HTML markup.
     * @param clientId the component clientId.
     * @throws IOException if an input/ output error occurs.
     */
    protected void renderDummyMarkup(FacesContext context, UIComponent component, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("style", "display: none;", null);

        renderPassThruAttributes(context, component);

        writer.endElement("div");
    }

    /**
     * GitHub #7763 Renders an in memory UIComponent and updates its ID with an index.
     * For example id="form:test" becomes id="form:test:0".
     *
     * @param context the {@link FacesContext}.
     * @param component the {@link UIComponent} to render.
     * @param index the index number to append to the ID
     * @throws IOException if an input/ output error occurs.
     */
    protected void encodeIndexedId(FacesContext context, UIComponent component, int index) throws IOException {
        ComponentUtils.encodeIndexedId(context, component, index);
    }

    protected boolean endsWithLenghtUnit(String val) {
        return val.endsWith("px") || val.endsWith("%") // most common first
                || val.endsWith("cm") || val.endsWith("mm") || val.endsWith("in")
                || val.endsWith("pt") || val.endsWith("pc")
                || val.endsWith("em") || val.endsWith("ex") || val.endsWith("ch") || val.endsWith("rem")
                || val.endsWith("vw") || val.endsWith("vh")
                || val.endsWith("vmin") || val.endsWith("vmax");
    }

    /**
     * Gets the ARIA text for an icon only button for screen readers.
     * @param title the tooltip for the button
     * @param ariaLabel the ARIA label for the button
     * @return either title, or ARIA label, or fallback "ui-button"
     */
    protected String getIconOnlyButtonText(String title, String ariaLabel) {
        String text = (title != null) ? title : ariaLabel;
        return (text != null) ? text : "ui-button";
    }

    /**
     * Renders a button value or label for screen readers if no value is present or icon only button.
     *
     * @param writer The response writer
     * @param escaped is the button escaped?
     * @param value the value for the button
     * @param title the title for the button
     * @param arialLabel the aria label for the button
     * @throws IOException if any error occurs
     */
    protected void renderButtonValue(ResponseWriter writer, boolean escaped, Object value, String title, String arialLabel) throws IOException {
        if (value == null) {
            //For ScreenReader
            String label = getIconOnlyButtonText(title, arialLabel);
            if (escaped) {
                writer.writeText(label, null);
            }
            else {
                writer.write(label);
            }
        }
        else {
            if (escaped) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }
    }

    /**
     * Logs a WARN log message in ProjectStage == DEVELOPMENT.
     * @param context the FacesContext
     * @param clazz the class or object for which the log is generated
     * @param message the message to log
     */
    protected void logDevelopmentWarning(FacesContext context, Object clazz, String message) {
        if (LOGGER.isLoggable(Level.WARNING) && context.isProjectStage(ProjectStage.Development)) {
            LOGGER.logp(Level.WARNING, clazz.getClass().getName(), message, message);
        }
    }

    private void renderPassThroughAttributes(FacesContext context, UIComponent component) throws IOException {
        Map<String, Object> passthroughAttributes = component.getPassThroughAttributes(false);

        if (passthroughAttributes != null && !passthroughAttributes.isEmpty()) {
            ResponseWriter writer = context.getResponseWriter();

            for (Map.Entry<String, Object> attribute : passthroughAttributes.entrySet()) {
                Object attributeValue = attribute.getValue();
                if (attributeValue != null) {
                    String value = null;

                    if (attributeValue instanceof ValueExpression) {
                        Object expressionValue = ((ValueExpression) attributeValue).getValue(context.getELContext());
                        if (expressionValue != null) {
                            value = expressionValue.toString();
                        }
                    }
                    else {
                        value = attributeValue.toString();
                    }

                    if (value != null) {
                        writer.writeAttribute(attribute.getKey(), value, null);
                    }
                }
            }
        }
    }
}
