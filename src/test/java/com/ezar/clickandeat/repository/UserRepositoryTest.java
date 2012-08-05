package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.model.User;
import com.ezar.clickandeat.validator.UserValidator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;

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
    public void testValidateValidUser() throws Exception {
        
        User user = new User();
        user.setUsername(testUserId);
        user.setPassword("password");
        user.setConfirmPassword("password");
        
        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("User");
        user.setPerson(person);
        
        Address address = new Address();
        address.setAddress1("80 Peel Road");
        address.setPostCode("E18 2LG");
        user.setAddress(address);

        BindException errors = new BindException(user,"user");
        userValidator.validate(user,errors);
        if( errors.hasErrors()) {
            LOGGER.info(errors.getLocalizedMessage());
        }

        Assert.assertTrue("No validation errors should occur",!errors.hasErrors());

    }
    
}
