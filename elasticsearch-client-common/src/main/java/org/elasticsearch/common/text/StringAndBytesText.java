/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.text;

import java.io.UnsupportedEncodingException;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;

/**
 * Both {@link String} and {@link BytesReference} representation of the text. Starts with one of those, and if
 * the other is requests, caches the other one in a local reference so no additional conversion will be needed.
 */
public class StringAndBytesText implements Text {

    public static final Text[] EMPTY_ARRAY = new Text[0];

    public static Text[] convertFromStringArray(String[] strings) {
        if (strings.length == 0) {
            return EMPTY_ARRAY;
        }
        Text[] texts = new Text[strings.length];
        for (int i = 0; i < strings.length; i++) {
            texts[i] = new StringAndBytesText(strings[i]);
        }
        return texts;
    }

    private BytesReference bytes;
    private String text;

    public StringAndBytesText(BytesReference bytes) {
        this.bytes = bytes;
    }

    public StringAndBytesText(String text) {
        this.text = text;
    }

    
    public boolean hasBytes() {
        return bytes != null;
    }

    
    public BytesReference bytes() {
        if (bytes == null) {
            try {
                bytes = new BytesArray(text.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                // ignore
            }
        }
        return bytes;
    }

    
    public boolean hasString() {
        return text != null;
    }

    
    public String string() {
        // TODO: we can optimize the conversion based on the bytes reference API similar to UnicodeUtil
        if (text == null) {
            if (!bytes.hasArray()) {
                bytes = bytes.toBytesArray();
            }
            try {
                text = new String(bytes.array(), bytes.arrayOffset(), bytes.length(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                // ignore
            }
        }
        return text;
    }

    
    public String toString() {
        return string();
    }
}
