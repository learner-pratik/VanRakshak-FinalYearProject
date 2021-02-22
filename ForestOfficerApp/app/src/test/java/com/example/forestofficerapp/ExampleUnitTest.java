package com.example.forestofficerapp;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void checkPassword() {
        String pattern = "([a-z])([A-Z](d)([@$&#]))";
        String password = "pdNaik@234";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);

        if (m.find()) {
            if (password.length()>=8 && password.length()<=12)
                System.out.println("Password correct");
            else
                System.out.println("Password incorrect");
        } else
            System.out.println("Password incorrect");
    }
}