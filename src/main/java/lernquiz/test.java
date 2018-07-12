package main.java.lernquiz;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class test {

    public static void main(String[] args){
        NumberFormat formatter = new DecimalFormat("#0.00");
        System.out.println(formatter.format(114.66666));
    }
}
