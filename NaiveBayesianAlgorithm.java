package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NaiveBayesianAlgorithm {
    int percentage = 1;
    ////////////to count yes and no/////////////
    ArrayList<Integer> labelCounter() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("new.csv"));
        String row;
        ArrayList<Integer> yesNoCounter = new ArrayList<>();
        int counter =0,yesCounter =0, noCounter=0;
        int toread = 4522*percentage/100;
        while ((row = csvReader.readLine()) != null && counter < toread) {
            if(counter==0){
                counter++;
                continue;
            }
            String[] data = row.split(";");
            if(data[data.length-1].equals('"'+"yes"+'"')){
                yesCounter++;
            }
            else {
                noCounter++;
            }
            counter++;
        }
        csvReader.close();
        yesNoCounter.add(yesCounter);
        yesNoCounter.add(noCounter);
        return yesNoCounter;
    }

    ////////////////to get the distinct values in one attribute////////////////
    ArrayList<Distinct> distinctValueCounter(int index, boolean catgorical) throws IOException {
        ArrayList<Distinct> attCounters = new ArrayList<>();
        ArrayList<String> distinctValues = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader("new.csv"));
        String row;
        int counter =0;
        int toread = 4522*percentage/100;
        if(catgorical){
            while ((row = csvReader.readLine()) != null && counter < toread) {
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = row.split(";");
                if(! distinctValues.contains(data[index])){
                    Distinct d = new Distinct();
                    d.name = data[index];
                    distinctValues.add(data[index]);
                    attCounters.add(d);
                }
                for(int k=0;k<attCounters.size();k++){
                    if(attCounters.get(k).name.equals(data[index])){
                        if(data[data.length-1].equals('"'+"yes"+'"')){
                            attCounters.get(k).yes++;
                        }
                        else{
                            attCounters.get(k).no++;
                        }
                    }
                }
                counter++;
            }
        }
        else {
            ArrayList<Integer> values = new ArrayList<>();
            while ((row = csvReader.readLine()) != null && counter < toread) {
                if (counter == 0) {
                    counter++;
                    continue;
                }
                String[] data = row.split(";");
                values.add(Integer.parseInt(data[index]));
                counter++;
            }
            csvReader.close();
            csvReader = new BufferedReader(new FileReader("new.csv"));
            Collections.sort(values);
            double median = 0;
            if (values.size() % 2 == 0) {
                int sumOfMiddleElements = (int)(values.get(values.size() / 2) + values.get((values.size() / 2)-1));
                median = (sumOfMiddleElements) / 2;
            }
            else {
                median = values.get(values.size() / 2);
            }
            Distinct less = new Distinct();
            less.name = "less";
            Distinct greater = new Distinct();
            greater.name = "greater";
            counter=0;
            while ((row = csvReader.readLine()) != null && counter < toread) {
                if (counter == 0) {
                    counter++;
                    continue;
                }
                String[] data = row.split(";");
                if (Integer.parseInt(data[index]) < median) {
                    if (data[data.length - 1].equals('"' + "yes" + '"')) {
                        less.yes++;
                    } else {
                        less.no++;
                    }
                } else {
                    if (data[data.length - 1].equals('"' + "yes" + '"')) {
                        greater.yes++;
                    } else {
                        greater.no++;
                    }
                }
                counter++;
            }
            attCounters.add(less);
            attCounters.add(greater);
        }

        csvReader.close();
        return attCounters;
    }

    /////////////////to calculate the algorithm////////////////
    public String calcNaive(String[] data) throws IOException {
        int toRead = 4522 * percentage / 100;
        float labelyes = labelCounter().get(0);
        float labelno = labelCounter().get(1);
        float yes = labelyes / (float) toRead;
        float no = labelno / (float) toRead;
        for (int i = 0; i < 5; i++) {
            boolean categorical = true;
            if (i == 0) {
                categorical = false;
            }
            ArrayList<Distinct> distincts = distinctValueCounter(i, categorical);
            for (int j = 0; j < distincts.size(); j++) {
                if (data[i].equals(distincts.get(j).name)) {
                    if (distincts.get(j).yes == 0) {
                        for (int k = 0; k < distincts.size(); k++) {
                            distincts.get(k).yes++;
                        }
                        labelyes++;
                    }
                    if (distincts.get(j).no == 0) {
                        for (int k = 0; k < distincts.size(); k++) {
                            distincts.get(k).no++;
                        }
                        labelno++;
                    }
                    yes *= distincts.get(j).yes / labelyes;
                    no *= distincts.get(j).no / labelno;
                }
            }
        }
            if (yes > no) {
                return "yes";
            }

            return "no";
        }

    /////////////data testing////////////
    public void testData() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("test.csv"));
        String row;
        ArrayList<String> real = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(";");

            real.add(data[data.length-1]);
            String[] test =Arrays.copyOf(data, data.length-1) ;
            if(Integer.parseInt(test[0]) <= calcMedian()){
                test[0] = "less";
            }
            else{
                test[0] = "greater";
            }
            result.add(calcNaive(test));
        }
        csvReader.close();
        int prob=0;
        for(int i=0;i<result.size();i++){
            if(('"'+result.get(i)+'"').equals(real.get(i))){
                prob++;
            }
        }
        float accuracy = prob/(float)result.size()*100;
        System.out.println(accuracy);
        System.out.println(result);
        System.out.println(real);
    }

    public float calcMedian() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("new.csv"));
         String row1;
        int counter = 0;
        int toread = percentage*4255/100;
        ArrayList<Integer> values = new ArrayList<>();
        while ((row1 = csvReader.readLine()) != null && counter < toread) {
            if (counter == 0) {
                counter++;
                continue;
            }
            String[] data = row1.split(";");
            values.add(Integer.parseInt(data[0]));
            counter++;
        }
        csvReader.close();
        csvReader = new BufferedReader(new FileReader("new.csv"));
        Collections.sort(values);
        float median = 0;
        if (values.size() % 2 == 0) {
            int sumOfMiddleElements = (int)(values.get(values.size() / 2) + values.get((values.size() / 2)-1));
            median = (sumOfMiddleElements) / 2;
        }
        else {
            median = values.get(values.size() / 2);
        }
        csvReader.close();
        return median;
    }

}
