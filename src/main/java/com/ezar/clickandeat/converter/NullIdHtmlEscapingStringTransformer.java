package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.StringTransformer;
import org.apache.commons.lang.StringEscapeUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NullIdHtmlEscapingStringTransformer extends NullIdStringTransformer {

    final Map<String,String> escapeMap = new HashMap<String, String>();

    public NullIdHtmlEscapingStringTransformer() {
        escapeMap.put("\u200B","");
    }

    @Override
    public void transform(Object object) {
        if( object == null ) {
            getContext().write("");
        }
        else {
            getContext().writeQuoted(StringEscapeUtils.escapeHtml((String)object));
        }
    }

    
}

