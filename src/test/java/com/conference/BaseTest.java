package com.conference;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public abstract class BaseTest {
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    protected Object invokdePrivateMethod(Object object, String methodName, Object arg) {
        try {
            Method[] methods = object.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (methodName.equalsIgnoreCase(method.getName())) {
                    method.setAccessible(true);
                    return method.invoke(object, arg);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
