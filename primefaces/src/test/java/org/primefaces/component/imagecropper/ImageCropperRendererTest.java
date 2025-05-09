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
package org.primefaces.component.imagecropper;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageCropperRendererTest {

    private FacesContext context;
    private ExternalContext externalContext;

    @BeforeEach
    void setup() {
        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRealPath(anyString())).thenReturn("src/test/resources");
    }

    @Test
    void checkStreamIsNullButImageIsGiven() {
        ImageCropper cropper = new ImageCropper();
        cropper.setImage("org/primefaces/images/nature/nature1.jpg");
        ImageCropperRenderer renderer = new ImageCropperRenderer();
        Object value = renderer.getConvertedValue(context, cropper, "1_100_1_100");
        assertNotNull(value);
    }

    @Test
    void checkImageIsNullButStreamIsGiven() {
        ImageCropper cropper = new ImageCropper();
        StreamedContent stream = DefaultStreamedContent.builder().contentType("image/png").stream(() -> {
            try {
                BufferedImage bufferedImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = bufferedImg.createGraphics();
                g2.drawString("This is a text", 50, 50);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bufferedImg, "png", os);
                return new ByteArrayInputStream(os.toByteArray());
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).build();
        cropper.setImage(stream);
        ImageCropperRenderer renderer = new ImageCropperRenderer();
        Object value = renderer.getConvertedValue(context, cropper, "1_100_1_100");
        assertNotNull(value);
    }

    @Test
    void checkImageAndStreamAreNull() {
        ImageCropper cropper = new ImageCropper();
        try {
            ImageCropperRenderer renderer = new ImageCropperRenderer();
            renderer.getConvertedValue(context, cropper, "1_100_1_100");
            fail("should thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (!"ImageCropper 'image' must be either a String relative path or a StreamedObject.".equals(message)) {
                fail("should thrown IllegalArgumentException with message: " + message);
            }
        }
        catch (Exception e) {
            fail("should thrown IllegalArgumentException");
        }
    }

}
