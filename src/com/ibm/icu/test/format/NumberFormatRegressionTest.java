/*
 *******************************************************************************
 * Copyright (C) 2001, International Business Machines Corporation and         *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 * $Source: /xsrl/Nsvn/icu/icu4j/src/com/ibm/icu/test/format/Attic/NumberFormatRegressionTest.java,v $ 
 * $Date: 2001/10/19 12:14:05 $ 
 * $Revision: 1.1 $
 *
 *****************************************************************************************
 */

/** 
 * Port From:   ICU4C v1.8.1 : format : NumberFormatRegressionTest
 * Source File: $ICU4CRoot/source/test/intltest/numrgts.cpp
 **/

package com.ibm.icu.test.format;

import com.ibm.text.*;
import com.ibm.util.*;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;

/** 
 * Performs regression test for MessageFormat
 **/
public class NumberFormatRegressionTest extends com.ibm.test.TestFmwk {
    
    public static void main(String[] args) throws Exception{
        new NumberFormatRegressionTest().run(args);
    }
    
    /**
     * alphaWorks upgrade
     */
    public void Test4161100() {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(1);
        nf.setMaximumFractionDigits(1);
        double a = -0.09;
        String s = nf.format(a);
        logln(a + " x " +
              ((DecimalFormat) nf).toPattern() + " = " + s);
        if (!s.equals("-0.1")) {
            errln("FAIL");
        }
    }
    
    /**
     * DateFormat should call setIntegerParseOnly(TRUE) on adopted
     * NumberFormat objects.
     */
    public void TestJ691() {
        
        Locale loc = new Locale("fr", "CH");
    
        // set up the input date string & expected output
        String udt = "11.10.2000";
        String exp = "11.10.00";
    
        // create a Calendar for this locale
        Calendar cal = Calendar.getInstance(loc);
    
        // create a NumberFormat for this locale
        NumberFormat nf = NumberFormat.getInstance(loc);
    
        // *** Here's the key: We don't want to have to do THIS:
        //nf.setParseIntegerOnly(true);
    
        // create the DateFormat
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, loc);
    
        df.setCalendar(cal);
        df.setNumberFormat(nf);
    
        // set parsing to lenient & parse
        Date ulocdat = new Date();
        df.setLenient(true);
        try {
            ulocdat = df.parse(udt);
        } catch (java.text.ParseException pe) {
            errln(pe.getMessage());
        }
        // format back to a string
        String outString = df.format(ulocdat);
    
        if (!outString.equals(exp)) {
            errln("FAIL: " + udt + " => " + outString);
        }
    }
    
    /**
     * Test getIntegerInstance();
     */
    public void Test4408066() {
        
        NumberFormat nf1 = NumberFormat.getIntegerInstance();
        NumberFormat nf2 = NumberFormat.getIntegerInstance(Locale.CHINA);
    
        //test isParseIntegerOnly
        if (!nf1.isParseIntegerOnly() || !nf2.isParseIntegerOnly()) {
            errln("Failed : Integer Number Format Instance should set setParseIntegerOnly(true)");
        }
    
        //Test format
        {
            double[] data = {
                -3.75, -2.5, -1.5, 
                -1.25, 0,    1.0, 
                1.25,  1.5,  2.5, 
                3.75,  10.0, 255.5
                };
            String[] expected = {
                "-4", "-2", "-2",
                "-1", "0",  "1",
                "1",  "2",  "2",
                "4",  "10", "256"
                };
    
            for (int i = 0; i < data.length; ++i) {
                String result = nf1.format(data[i]);
                if (!result.equals(expected[i])) {
                    errln("Failed => Source: " + Double.toString(data[i]) 
                        + ";Formatted : " + result
                        + ";but expectted: " + expected[i]);
                }
            }
        }
        //Test parse, Parsing should stop at "."
        {
            String data[] = {
                "-3.75", "-2.5", "-1.5", 
                "-1.25", "0",    "1.0", 
                "1.25",  "1.5",  "2.5", 
                "3.75",  "10.0", "255.5"
                };
            long[] expected = {
                -3, -2, -1,
                -1, 0,  1,
                1,  1,  2,
                3,  10, 255
                };
            
            for (int i = 0; i < data.length; ++i) {
                Number n = null;
                try {
                    n = nf1.parse(data[i]);
                } catch (ParseException e) {
                    errln("Failed: " + e.getMessage());
                }
                if (!(n instanceof Long) || (n instanceof Integer)) {
                    errln("Failed: Integer Number Format should parse string to Long/Integer");
                }
                if (n.longValue() != expected[i]) {
                    errln("Failed=> Source: " + data[i] 
                        + ";result : " + n.toString()
                        + ";expected :" + Long.toString(expected[i]));
                }
            }
        }
    }
}