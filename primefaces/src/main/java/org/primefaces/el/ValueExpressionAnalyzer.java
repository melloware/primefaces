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
package org.primefaces.el;

import jakarta.el.ELContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.el.CompositeComponentExpressionHolder;

public class ValueExpressionAnalyzer {

    private ValueExpressionAnalyzer() {
    }

    public static ValueReference getReference(ELContext elContext, ValueExpression expression) {
        return getReference(elContext, expression, false);
    }

    public static ValueReference getReference(ELContext elContext, ValueExpression expression,
            boolean returnNullWhenUnresolvable) {

        if (expression == null) {
            return null;
        }

        try {
            ValueReference reference = toValueReference(expression, elContext);

            // check for a CC expression
            if (reference != null && isCompositeComponentReference(reference)) {
                ValueExpression unwrapped = unwrapCompositeComponentReference(reference);

                // check for nested CC expressions
                if (unwrapped != null) {
                    ValueReference unwrappedRef = toValueReference(unwrapped, elContext);
                    if (isCompositeComponentReference(unwrappedRef)) {
                        return getReference(elContext, unwrapped);
                    }
                    else {
                        return unwrappedRef;
                    }
                }

                // return null if it cant be further unwrapped
                return null;
            }

            return reference;
        }
        catch (PropertyNotFoundException e) {
            if (returnNullWhenUnresolvable) {
                return null;
            }
            throw e;
        }
    }

    public static ValueExpression getExpression(ELContext elContext, ValueExpression expression) {
        return getExpression(elContext, expression, false);
    }

    public static ValueExpression getExpression(ELContext elContext, ValueExpression expression,
            boolean returnNullWhenUnresolvable) {

        if (expression == null) {
            return null;
        }

        try {
            // Unwrapping is required e.g. for p:graphicImage to support nested expressions in composites
            ValueReference reference = toValueReference(expression, elContext);

            // check for a CC expression
            if (reference != null && isCompositeComponentReference(reference)) {
                ValueExpression unwrapped = unwrapCompositeComponentReference(reference);

                // check for nested CC expressions
                if (unwrapped != null && isCompositeComponentReference(toValueReference(unwrapped, elContext))) {
                    return getExpression(elContext, unwrapped);
                }

                // also return null if it cant be further unwrapped
                return unwrapped;
            }

            return expression;
        }
        catch (PropertyNotFoundException e) {
            if (returnNullWhenUnresolvable) {
                return null;
            }
            throw e;
        }
    }

    public static boolean isCompositeComponentReference(ValueReference vr) {
        return vr != null && vr.getBase() != null && vr.getBase() instanceof CompositeComponentExpressionHolder;
    }

    public static ValueExpression unwrapCompositeComponentReference(ValueReference vr) {
        return ((CompositeComponentExpressionHolder) vr.getBase()).getExpression((String) vr.getProperty());
    }

    public static ValueReference intercept(ELContext elContext, ValueExpression expression) {

        if (expression == null) {
            return null;
        }

        InterceptingResolver resolver = new InterceptingResolver(elContext.getELResolver());
        ELContext interceptingContext = new InterceptingContext(elContext, resolver);

        // #getType throws a PropertyNotFoundException when a sub-expression is null
        expression.getType(interceptingContext);

        // intercept EL calls
        expression.getValue(interceptingContext);

        return resolver.getValueReference();
    }

    public static ValueReference toValueReference(ValueExpression ve, ELContext elContext) {
        ValueReference reference = ve.getValueReference(elContext);

        // e.g. TagValueExpression returns null
        if (reference == null) {
            reference = intercept(elContext, ve);
        }

        return reference;
    }
}
