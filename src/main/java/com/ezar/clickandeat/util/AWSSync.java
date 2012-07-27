package com.ezar.clickandeat.util;

import org.jets3t.apps.synchronize.Synchronize;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public class AWSSync {

    /**
     * Synchronizes webapp/resources directory onto amazon s3 bucket
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        
        String awsbucket = args[0];
        
        Resource propsResource = new ClassPathResource("/aws.s3.synchronize.properties");
        Resource sourceResource = new FileSystemResource("src/main/webapp/resources");
        
        List<String> argsList = new ArrayList<String>();
        argsList.add("--properties");
        argsList.add(propsResource.getFile().getAbsolutePath());
        argsList.add("UP");
        argsList.add(awsbucket);
        argsList.add(sourceResource.getFile().getAbsolutePath());

        Synchronize.main(argsList.toArray(new String[argsList.size()]));
        
    }
    
}
