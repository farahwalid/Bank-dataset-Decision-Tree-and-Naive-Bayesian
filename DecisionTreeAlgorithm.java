package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DecisionTreeAlgorithm {
    String filename = "Bank_dataset.csv";
    int percentage=1;
    String filtered = "new.csv";
    void readCSV() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(filename));
        String row;
        int counter =0;
        int toread = 4522*percentage/100;
        while ((row = csvReader.readLine()) != null && counter < toread) {
            String[] data = row.split(";");
            counter++;
        }
        csvReader.close();
    }
    /////////to count yes no values in dataset////////
    ArrayList<Integer> labelCounter() throws IOException {
            BufferedReader csvReader = new BufferedReader(new FileReader(filtered));
            String row;
            ArrayList<Integer> yesNoCounter = new ArrayList<>();
            String yes="yes";
            int counter =0,yesCounter =0, noCounter=0;
            int toread = 4522*percentage/100;
            while ((row = csvReader.readLine()) != null && counter < toread) {
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = row.split(";");
                if(data[data.length-1].equals('"'+yes+'"')){
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
    /////////// calculate info of dataset//////
    double calculateDataSetInfo() throws IOException {
        int toread = 4522*percentage/100;
        double probYes = labelCounter().get(0)/ (double)toread;
        double probNo = labelCounter().get(1)/ (double) toread;
        double info = - (probYes * (Math.log(probYes)/Math.log(2))) - (probNo * (Math.log(probNo)/Math.log(2))) ;
        return info;
    }

    /////////to count each distinct value in yes and no ////////
    ArrayList<Distinct> distinctValueCounter(int index, boolean catgorical) throws IOException {
        ArrayList<Distinct> attCounters = new ArrayList<>();
        ArrayList<String> distinctValues = new ArrayList<>();
        BufferedReader csvReader = new BufferedReader(new FileReader(filtered));
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
            csvReader = new BufferedReader(new FileReader(filename));
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

    /////////to calculate attribute info////////////
    double calculateAttributeInfo(int index, boolean categorical) throws IOException {
        double info=0;
        int toread = 4522*percentage/100;
        ArrayList<Distinct> attribute = distinctValueCounter(index, categorical);
        for(int i=0;i<attribute.size();i++){
            double probYes = (attribute.get(i).yes) / (double)toread;
            double probNo = (attribute.get(i).no) / (double)toread;
            double distinctprob = (attribute.get(i).yes + attribute.get(i).no) / (double)toread;
            if(probYes == 0){
                info += distinctprob * ( - probNo * (Math.log(probNo)/Math.log(2)));
                continue;
            }
            if(probNo == 0){
                info += distinctprob * ( - probYes * (Math.log(probYes)/Math.log(2)));
                continue;
            }
            info += distinctprob * ( - probYes * (Math.log(probYes)/Math.log(2)) - probNo * (Math.log(probNo)/Math.log(2)));
        }
        return info;
    }
    //////////to calculate the gain of specific attribute////////////
    double calculateGain(int index, boolean categorical) throws IOException {
        return calculateDataSetInfo() - calculateAttributeInfo(index,categorical);
    }

    void splitData() throws IOException {
        ArrayList<String> att = new ArrayList<>(Arrays.asList("age","job","marital","education","housing"));
        while(true){
            double greater = 0;
            int attIndex = 0;
            ArrayList<Distinct> distinctValues = new ArrayList<>();
            for(int i=0;i<att.size();i++){
                boolean categorical = true;
                if(i==0){
                    categorical = false;
                }
                ArrayList<Distinct> distinct = distinctValueCounter(i, categorical);
                double gain =calculateGain(i, categorical);
                if(gain > greater){
                    greater = gain;
                    attIndex = i;
                    distinctValues = distinct;
                }
            }
            BufferedReader csvReader = new BufferedReader(new FileReader(filename));
            String row;
            row = csvReader.readLine();
            String[] data = row.split(";");
            csvReader.close();
            Node node = new Node(data[attIndex], attIndex);
            //ArrayList<String> nodeData = new ArrayList<>();
            for(int i=0;i<distinctValues.size();i++){
                if(distinctValues.get(i).yes == 0){
                    Node child1 = new Node(distinctValues.get(i).name);
                    node.children.add(child1);
                    Node child2 = new Node("no");
                    child2.children.add(child2);
                }
                else if (distinctValues.get(i).no == 0){
                    Node child1 = new Node(distinctValues.get(i).name);
                    node.children.add(child1);
                    Node child2 = new Node("yes");
                    child2.children.add(child2);
                }
                else{
                    Node child1 = new Node(distinctValues.get(i).name);
                    node.children.add(child1);
                }
            }
            att.remove(attIndex);
            int i=0;
            while(! node.isLeaf() ){
                for(int j = 0;j<node.children.size();j++){
                    node.children.get(j);
                    csvReader = new BufferedReader(new FileReader(filtered));
                    int counter =0;
                    int toread = 4522*percentage/100;
                    while ((row = csvReader.readLine()) != null && counter < toread) {
                        data = row.split(";");
                        counter++;
                    }
                    csvReader.close();
                }
            }

            for(int j = 0 ;j<node.children.size(); j++){
                System.out.println(node.children.get(i).data+" "+ node.children.get(i).attIndex);
            }

        }

            }




















    void filterData() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(filename));
        String row;
        int counter =0;
        //File newFile = new File("new.csv");
        BufferedWriter csvWriter = new BufferedWriter(new FileWriter("new.csv"));
        //int toread = 4522*percentage/100;
        ////(4,5,7,8,9,10,11,12,13,14,15)
        ArrayList<Integer> unused = new ArrayList<>(Arrays.asList (0,1,2,3,6,16));
        while ((row = csvReader.readLine()) != null /*&& counter < toread*/) {
            if(counter ==0 ){
                counter++;
                continue;
            }
            String[] data = row.split(";");
            String r ="";
            for(int i=0;i<data.length;i++){
                if(unused.contains(i)){
                    r+=data[i]+";" ;
                }
            }
            r+= "\n";
            csvWriter.write(r);
            counter++;
        }
        csvReader.close();
    }



}

