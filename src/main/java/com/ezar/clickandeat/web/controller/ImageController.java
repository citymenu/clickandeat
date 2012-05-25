package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Image;
import com.ezar.clickandeat.repository.ImageRepository;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ImageController {

    private static final Logger LOGGER = Logger.getLogger(RestaurantSearchController.class);

    private ImageRepository repository;

    @RequestMapping(value="/image.html", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@RequestParam(value = "id", required = false) String id ) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("");
        }
        
        Image image = repository.findOne(id);
        
        if( image == null ) {
            LOGGER.warn("No image resource found for id [" + id + "]");
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(0);
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<byte[]>(new byte[0], headers, HttpStatus.CREATED);
        }
        else {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(image.getData().length);
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setLastModified(image.getLastUpdated());
            return new ResponseEntity<byte[]>(image.getData(), headers, HttpStatus.CREATED);
        }
    }

}
