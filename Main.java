package com.company;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
    DecisionTreeAlgorithm d = new DecisionTreeAlgorithm();
    //d.readCSV();
    //d.attributeCount(1);
    //d.labelCounter();
        // d.calculateDataSetInfo();
       // d.calculateAttributeInfo(0, false);
        //d.filterData();
        //d.splitData();

        NaiveBayesianAlgorithm n = new NaiveBayesianAlgorithm();

        n.testData();
    }
}
