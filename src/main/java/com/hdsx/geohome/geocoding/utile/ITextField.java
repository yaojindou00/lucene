package com.hdsx.geohome.geocoding.utile;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

import java.io.Reader;

/**
 * Created by jzh on 2016/11/9.
 */
public class ITextField extends Field {
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED = new FieldType();

    public ITextField(String name, Reader reader) {
        super(name, reader, TYPE_NOT_STORED);
    }

    public ITextField(String name, String value, Store store) {
        super(name, value, store == Store.YES?TYPE_STORED:TYPE_NOT_STORED);
    }

    public ITextField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }

    static {
        TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        TYPE_NOT_STORED.setTokenized(false);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        TYPE_STORED.setTokenized(false);
        TYPE_STORED.setStored(true);
        TYPE_STORED.freeze();
    }
}
