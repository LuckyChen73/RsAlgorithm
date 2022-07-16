package com.example.rsalgorithm;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
*   粗糙集属性约简算法工具类
*
* */
public class RoughSetsTool {

    // 决策属性名称
    public static String DECISION_ATTR_NAME;

    // 测试数据文件地址
    private String filePath;
    // 数据属性列名称
    private String[] attrNames;
    // 所有的数据
    private ArrayList<String[]> totalDatas;
    // 所有的数据记录，与上面的区别是记录的属性是可约简的，原始数据是不能变的
    private ArrayList<Record> totalRecords;
    // 条件属性图
    private HashMap<String, ArrayList<String>> conditionAttr;
    // 属性记录集合
    private ArrayList<RecordCollection> collectionList;


    public RoughSetsTool(String filePath) {
        this.filePath = filePath;
        readDataFile();
    }

    /*
    *   从文件中读取数据
    * */
    private void readDataFile() {
        File file = new File(filePath);
        ArrayList<String[]> dataArray = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            String[] tempArray;
            while ((str = in.readLine()) != null) {
                tempArray = str.split(" ");
                dataArray.add(tempArray);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] array;
        Record tempRecord;
        HashMap<String, String> attrMap;
        ArrayList<String> attrList;
        totalDatas = new ArrayList<>();
        totalRecords = new ArrayList<>();
        conditionAttr = new HashMap<>();
        // 赋值属性名称行
        attrNames = dataArray.get(0);
        // 决策属性值
        DECISION_ATTR_NAME = attrNames[attrNames.length-1];

        for (int i = 0; i < dataArray.size(); i++) {
            array = dataArray.get(i);
            totalDatas.add(array);

            if (i==0) {
                // 过滤第一行列名称数据
                continue;
            }

            attrMap = new HashMap<>();
            for (int j = 0; j < attrNames.length; j++) {
                attrMap.put(attrNames[j], array[j]);

                // 寻找条件属性
                if (j > 0 && j < attrNames.length - 1) {
                    // 条件属性
                    if (conditionAttr.containsKey(attrNames[i])) {
                        attrList = conditionAttr.get(attrNames[j]);
                        if (!attrList.contains(array[j])) {
                            attrList.add(array[j]);
                        }
                    } else {
                        attrList = new ArrayList<>();
                        attrList.add(array[i]);
                    }
                    conditionAttr.put(attrNames[j], attrList);
                }
            }
            tempRecord = new Record(array[0], attrMap);
            totalRecords.add(tempRecord);
        }
    }


    private void recordSplitToCollection() {
        String attrName;
        ArrayList<String> attrList;
        ArrayList<Record> recordList;
        HashMap<String,String> collectionAttrValues;
        RecordCollection collection;
        collectionList = new ArrayList<>();

        for (Map.Entry entry : conditionAttr.entrySet()) {
            attrName = (String) entry.getKey();
            attrList = (ArrayList<String>) entry.getValue();

            for (String s : attrList) {
                recordList = new ArrayList<>();
                // 寻找属性为s的数据记录分入到集合中
                for (Record record : totalRecords) {
                    if (record.isContainedAttr(s)) {
                        recordList.add(record);
                    }
                }
                collectionAttrValues = new HashMap<>();
                collectionAttrValues.put(attrName, s);
                collection = new RecordCollection(collectionAttrValues,recordList);
                collectionList.add(collection);
            }
        }
    }



    private HashMap<String,ArrayList<RecordCollection>> constructCollectionMap(ArrayList<String> reductAttr) {
        String currentAttrName;
        ArrayList<RecordCollection> cList;
        // 集合属性对应图

    }







}
