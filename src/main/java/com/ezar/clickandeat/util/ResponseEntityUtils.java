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
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
