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
package org.primefaces.model.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import jakarta.faces.FacesWrapper;
import jakarta.faces.component.StateHolder;
import jakarta.faces.context.FacesContext;

/**
 * Internal wrapper to avoid the file binaries to beeing saved in the ViewState.
 */
public class UploadedFileWrapper implements UploadedFile, FacesWrapper<UploadedFile>, StateHolder, Serializable {

    private static final long serialVersionUID = 1L;

    private UploadedFile wrapped;

    public UploadedFileWrapper() {
        // NOOP
    }

    public UploadedFileWrapper(UploadedFile wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public UploadedFile getWrapped() {
        return wrapped;
    }

    @Override
    public Object saveState(FacesContext fc) {
        return null;
    }

    @Override
    public void restoreState(FacesContext fc, Object o) {
        // NOOP
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void setTransient(boolean value) {
        // NOOP
    }

    @Override
    public long getSize() {
        return getWrapped().getSize();
    }

    @Override
    public File write(String directoryPath) throws Exception {
        return getWrapped().write(directoryPath);
    }

    @Override
    public String getFileName() {
        return getWrapped().getFileName();
    }


    @Override
    public String getWebkitRelativePath() {
        return getWrapped().getWebkitRelativePath();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getWrapped().getInputStream();
    }

    @Override
    public byte[] getContent() {
        return getWrapped().getContent();
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public void delete() throws IOException {
        getWrapped().delete();
    }
}
