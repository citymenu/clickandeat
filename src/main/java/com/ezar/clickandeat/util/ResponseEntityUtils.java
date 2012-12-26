package com.ezar.clickandeat.util;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseEntityUtils {

    @Autowired
    private JSONUtils jsonUtils;

    /**
     * @param model
     * @return
     * @throws Exception
     */

    public ResponseEntity<byte[]> buildResponse(Map<String,Object> model ) throws Exception {
        String json = jsonUtils.serializeAndEscape(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    /**
     * @param model
     * @return
     * @throws Exception
     */

    public ResponseEntity<byte[]> buildResponse(Map<String,Object> model, String ... excludes ) throws Exception {
        JSONSerializer serializer = jsonUtils.buildSerializer(excludes);
        String json = jsonUtils.serializeAndEscape(serializer,model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    /**
     * @param xml
     * @return
     * @throws Exception
     */

    public ResponseEntity<byte[]> buildXmlResponse(String xml) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(xml.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
