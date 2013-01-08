package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
public class FileUploadController implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(FileUploadController.class);

    private String bucketName;
    
    private String basePath = "resources/images/restaurant";

    private S3Service s3Service;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JSONUtils jsonUtils;

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties props = new Properties();
        props.load(new ClassPathResource("/aws.s3.synchronize.properties").getInputStream());
        String accessKey = props.getProperty("accesskey");
        String secretKey = props.getProperty("secretkey");
        bucketName = props.getProperty("bucketname");
        AWSCredentials credentials = new AWSCredentials(accessKey, secretKey);
        s3Service = new RestS3Service(credentials);
    }


    @ResponseBody
    @RequestMapping(value="/admin/upload.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> upload(@RequestParam("restaurantId") String restaurantId, @RequestParam("file") CommonsMultipartFile file ) throws Exception {

        Map<String,Object> model = new HashMap<String,Object>();
        
        try {
            S3Object object = new S3Object(basePath + "/" + restaurantId);
            object.setDataInputStream(file.getInputStream());
            object.setContentLength(file.getSize());
            object.setContentType(file.getContentType());
            S3Bucket bucket = s3Service.getBucket(bucketName);
            s3Service.putObject(bucket, object);
            
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            restaurant.setHasUploadedImage(true);
            restaurantRepository.saveRestaurant(restaurant);
            
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = jsonUtils.serializeAndEscape(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
