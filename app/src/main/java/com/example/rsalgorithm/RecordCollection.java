package com.example.rsalgorithm;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
*   数据记录集合，包含一些共同的属性
*
* */
public class RecordCollection {

    // 集合包含的属性
    private HashMap<String, String> attrValues;

    // 数据记录列表
    private ArrayList<Record> recordList;

    public RecordCollection() {
        this.attrValues = new HashMap<>();
        this.recordList = new ArrayList<>();
    }

    public RecordCollection(HashMap<String,String> attrValues, ArrayList<Record> recordList) {
        this.attrValues = attrValues;
        this.recordList = recordList;
    }


    // 获取记录列表
    public ArrayList<Record> getRecordList() {
        return recordList;
    }

    /*
    *   返回集合的字符名称数组
    *
    * */
    public ArrayList<String> getRecordNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < recordList.size(); i++) {
            names.add(recordList.get(i).getName());
        }
        return names;
    }

    /*
    *   判断集合是否包含此属性名称对应的属性值
    *
    *   attrName 属性名
    *
    * */
    public boolean isContainedAttrName(String attrName) {
        boolean isContained = false;
        if (this.attrValues.containsKey(attrName)) {
            isContained = true;
        }
        return isContained;
    }


    /*
    *   判断两个集合是否相等,比较包含的数据记录是否一致
    *
    *   rc 待比较的集合
    * */
    public boolean isCollectionSame(RecordCollection rc) {
        boolean isSame = false;

        for (Record r: recordList) {
            isSame = false;
            for (Record r2: rc.recordList) {
                if (r.isRecordSame(r2)) {
                    isSame = true;
                    break;
                }
            }

            // 如果有一条记录不包含,就算集合不相等
            if (!isSame) {
                break;
            }
        }
        return isSame;
    }

    /*
    *   集合之间的交运算
    *
    *   rc 交运算的参与运算的另外一集合
    *
    * */
    public RecordCollection overlapCalculate(RecordCollection rc) {
        String key;
        String value;
        RecordCollection resultCollection = null;
        HashMap<String, String> resultAttrValues = new HashMap<>();
        ArrayList<Record> resultRecords = new ArrayList<>();

        //进行集合的交运算, 有相同的记录的则进行添加
        for (Record record : this.recordList) {
            for (Record record2 : rc.recordList) {
                if (record.isRecordSame(record2)) {
                    resultRecords.add(record);
                    break;
                }
            }
        }

        if (resultRecords.size() == 0) {
            return null;
        }

        // 将两个集合的属性进行合并
        for (Map.Entry entry : this.attrValues.entrySet()) {
            key = (String) entry.getKey();
            value = (String) entry.getValue();

            resultAttrValues.put(key, value);
        }

        for (Map.Entry entry : rc.attrValues.entrySet()) {
            key = (String) entry.getKey();
            value = (String) entry.getValue();

            resultAttrValues.put(key, value);
        }

        resultCollection = new RecordCollection(resultAttrValues, resultRecords);

        return resultCollection;
    }


    /*
    *   求集合的并集,各自保留各自的属性
    *
    *   rc 待合并的集合
    * */
    public RecordCollection unionCal(RecordCollection rc) {
        RecordCollection resultRc = null;
        ArrayList<Record> recordList = new ArrayList<>();

        for (Record r1: this.recordList) {
            recordList.add(r1);
        }

        for (Record r2: rc.recordList) {
            recordList.add(r2);
        }

        resultRc = new RecordCollection(null, recordList);
        return resultRc;
    }


    /*
    *   输出集合中包含的元素
    *
    * */
    public void printRc() {
        System.out.print("{");
        for (Record r : this.recordList) {
            System.out.print(r.getName() + ",");
        }
        System.out.print("}");
    }



}
