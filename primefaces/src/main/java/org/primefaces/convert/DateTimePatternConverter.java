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
package org.primefaces.convert;

import org.primefaces.convert.PatternReader.TokenVisitor;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for converting between {@link SimpleDateFormat} /
 * {@link DateTimeFormatter} patterns and JQuery UI date picker patterns.
 */
public class DateTimePatternConverter implements PatternConverter {
    private static final Logger LOGGER = Logger.getLogger(DateTimePatternConverter.class.getName());

    /**
     * Converts a Java date pattern with the given pattern letters to a JQuery UI
     * date picker pattern.
     *
     * @param pattern Pattern to be converted.
     * @return The converted JQuery UI date picker pattern.
     */
    public String convert(String pattern) {
        pattern = pattern != null ? pattern : "";
        final JQueryDateTimePatternBuilder builder = new JQueryDateTimePatternBuilder();
        final TokenVisitor visitor = new CombinedDateTimePatternTokenVisitor(builder);
        try {
            PatternReader.parsePattern(pattern, visitor);
        }
        catch (final Exception e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Could not process pattern '{0}" + pattern + "'", e);
            }
            return pattern;
        }
        return builder.toString();
    }
}
