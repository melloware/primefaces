if (!PrimeFaces.expressions) {

    /**
     * The object with functionality related to working with search expressions.
     * @namespace
     */
    PrimeFaces.expressions = {};

    /**
     * The interface of the object with all methods for working with search expressions.
     * @interface {PrimeFaces.expressions.SearchExpressionFacadeObject}
     * @constant {PrimeFaces.expressions.SearchExpressionFacade} . The object with all methods for working with search
     * expressions.
     */
    PrimeFaces.expressions.SearchExpressionFacade = {

        /**
         * Takes a search expression that may contain multiple components, separated by commas or whitespaces. Resolves
         * each search expression to the component it refers to and returns a JQuery object with the DOM elements of
         * the resolved components.
         * @param {JQuery} source the source element where to start the search (e.g. required for @form).
         * @param {string | HTMLElement | JQuery} expressions A search expression with one or multiple components to resolve.
         * @return {JQuery} A list with the resolved components.
         */
        resolveComponentsAsSelector: function(source, expressions) {

            if (expressions instanceof $) {
                return expressions;
            }

            if (expressions instanceof HTMLElement) {
                return $(expressions);
            }

            var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions);
            var elements = $();

            if (splittedExpressions) {
                for (const splittedExpression of splittedExpressions) {
                    var expression = PrimeFaces.trim(splittedExpression);
                    if (expression.length > 0) {

                        // skip unresolvable keywords
                        if (expression == '@none' || expression == '@all' || expression.indexOf("@obs(") == 0) {
                            continue;
                        }

                        // just a id
                        if (expression.indexOf("@") == -1) {
                            elements = elements.add(
                                    $(document.getElementById(expression)));
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring("@widgetVar(".length, expression.length - 1);
                            var widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                                elements = elements.add(
                                        $(document.getElementById(widget.id)));
                            } else {
                                PrimeFaces.widgetNotAvailable(widgetVar);
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                            elements = elements.add(
                                    $(expression.substring(2, expression.length - 1)));
                        }
                        else if (expression == '@form') {
                            var form = source.closest('form');
                            if (form.length == 0) {
                                PrimeFaces.error("Could not resolve @form for source '" + source.attr('id') + "'");
                            }
                            else {
                                elements = elements.add(form[0]);
                            }
                        }
                    }
                }
            }

            return elements;
        },

        /**
         * Takes a search expression that may contain multiple components, separated by commas or whitespaces. Resolves
         * each search expression to the component it refers to and returns a list of IDs of the resolved components.
         *
         * @param {JQuery} source the source element where to start the search (e.g. required for @form).
         * @param {string} expressions A search expression with one or multiple components to resolve.
         * @return {string[]} A list of IDs with the resolved components.
         */
        resolveComponents: function(source, expressions) {
            var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions),
            ids = [];

            if (splittedExpressions) {
                for (const splittedExpression of splittedExpressions) {
                    var expression = PrimeFaces.trim(splittedExpression);
                    if (expression.length > 0) {

                        // just a id or passtrough keywords
                        if (expression.indexOf("@") == -1 || expression == '@none'
                                || expression == '@all' || expression.indexOf("@obs(") == 0) {
                            if (!PrimeFaces.inArray(ids, expression)) {
                                ids.push(expression);
                            }
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring(11, expression.length - 1),
                            widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                                if (!PrimeFaces.inArray(ids, widget.id)) {
                                    ids.push(widget.id);
                                }
                            } else {
                                PrimeFaces.widgetNotAvailable(widgetVar);
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                            var elements = $(expression.substring(2, expression.length - 1));

                            for (const element of elements) {
                                var $element = $(element);
                                var clientId = $element.data(PrimeFaces.CLIENT_ID_DATA) || $element.attr('id');

                                if (clientId && !PrimeFaces.inArray(ids, clientId)) {
                                    ids.push(clientId);
                                }
                            }
                        }
                        else if (expression == '@form') {
                            var form = source.closest('form');
                            if (form.length == 0) {
                                PrimeFaces.error("Could not resolve @form for source '" + source.attr('id') + "'");
                            }
                            else {
                                var formClientId = form.data(PrimeFaces.CLIENT_ID_DATA) || form.attr('id');
                                if (!PrimeFaces.inArray(ids, formClientId)) {
                                    ids.push(formClientId);
                                }
                            }
                        }
                    }
                }
            }

            return ids;
        },

        /**
         * Splits the given search expression into its components. The components of a search expression are separated
         * by either a comman or a whitespace.
         * ```javascript
         * splitExpressions("") // => [""]
         * splitExpressions("form") // => ["form"]
         * splitExpressions("form,input") // => ["form", "input"]
         * splitExpressions("form input") // => ["form", "input"]
         * splitExpressions("form,@child(1,2)") // => ["form", "child(1,2)"]
         * ```
         * @param {string} expression A search expression to split.
         * @return {string[]} The individual components of the given search expression.
         */
        splitExpressions: function(expression) {

            var expressions = [];
            var buffer = '';

            var parenthesesCounter = 0;

            if (!expression) {return expressions;}
            for (const c of expression) {

                if (c === '(') {
                    parenthesesCounter++;
                }

                if (c === ')') {
                    parenthesesCounter--;
                }

                if ((c === ' ' || c === ',') && parenthesesCounter === 0) {
                    // lets add token inside buffer to our tokens
                    expressions.push(buffer);
                    // now we need to clear buffer
                    buffer = '';
                } else {
                    buffer += c;
                }
            }

            // lets not forget about part after the separator
            expressions.push(buffer);

            return expressions;
        }
    };
}