package com.ezar.clickandeat.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseEntityUtils {

    /**
     * @param model
     * @return
     * @throws Exception
     */

    public static ResponseEntity<byte[]> buildResponse(Map<String,Object> model ) throws Exception {
        String json = JSONUtils.serialize(model);
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

    public static ResponseEntity<byte[]> buildXmlResponse(String xml) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(xml.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
