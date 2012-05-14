package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.validator.UserValidator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class UserRepositoryTest {
    
    private static final Logger LOGGER = Logger.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserValidator userValidator;
    
    private String testUserId = "testuser@email.com";
    
    @Before
    public void setup() throws Exception {
        removeTestUser();
    }

    @After
    public void tearDown() throws Exception {
        removeTestUser();
    }

    private void removeTestUser() throws Exception {
        User testUser = repository.findByUsername(testUserId);
        if( testUser != null ) {
            repository.delete(testUser);
        }
    }
    
    
    @Test
    public void testCreateAndDeleteUser() throws Exception {
        

        
    }
    
}
