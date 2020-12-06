package com.nbdyn.community;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dyn
 * @create 2020-12-05-19:30
 */
public class NormalTest {
    @Test
    public void NormalTest() {
        String s="dyn22A2";
        System.out.println(s);
        System.out.println(s.length()>6);
        Pattern pattern = Pattern.compile(".*[a-z]+.*");
        System.out.println(pattern.matcher(s).matches());
        Pattern pattern2 = Pattern.compile(".*[A-Z]+.*");
        System.out.println(pattern2.matcher(s).matches());

        Pattern pattern3 = Pattern.compile("[^0-9]");
        Matcher t = pattern3.matcher(s);
        String tt=t.replaceAll("").trim();
        System.out.println(t);
        System.out.println(tt);


    }
}
