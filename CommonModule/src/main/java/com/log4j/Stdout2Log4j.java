package com.log4j;


import java.io.PrintStream;

import org.apache.log4j.Logger;


/**

 * 把在程序中的System.out.print()的信息自动转成日志信息

 */

public class Stdout2Log4j {

    private void log(Object info) {
    	Logger.getLogger(getClass()).info(info);
    }

    public Stdout2Log4j() {
        PrintStream printStream = new PrintStream(System.out) {
            public void println(boolean x) {
                log(Boolean.valueOf(x));
            }
            public void println(char x) {
                log(Character.valueOf(x));
            }
            public void println(char[] x) {
                log(x == null ? null : new String(x));
            }
            public void println(double x) {
                log(Double.valueOf(x));
            }
            public void println(float x) {
                log(Float.valueOf(x));
            }
            public void println(int x) {
                log(Integer.valueOf(x));
            }
            public void println(long x) {
                log(x);
            }
            public void println(Object x) {
                log(x);
            }
            public void println(String x) {
                log(x);
            }
        };
        System.setOut(printStream);
        System.setErr(printStream);
    }
    
}