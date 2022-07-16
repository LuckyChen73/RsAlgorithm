package com.example.rsalgorithm;


import android.util.Log;

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

    private String TAG = RoughSetsTool.class.getSimpleName();

    // 决策属性名称
    public static String DECISION_ATTR_NAME;

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


    public RoughSetsTool(ArrayList arrayList) {
        readDataFile(arrayList);
    }

    /*
    *   从文件中读取数据
    * */
    private void readDataFile(ArrayList<String> arrayList) {
        ArrayList<String[]> dataArray = new ArrayList<>();
        String[] tempArray;
        for (String s : arrayList) {
            tempArray = s.split(" ");
            dataArray.add(tempArray);
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
                    if (conditionAttr.containsKey(attrNames[j])) {
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


    /*
    *   将数据记录根据属性分割到集合中
    * */
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


    /*  构造属性集合图
    *
    *   reduceAttr 需要约简的属性
    * */
    private HashMap<String,ArrayList<RecordCollection>> constructCollectionMap(ArrayList<String> reduceAttr) {
        String currentAttrName;
        ArrayList<RecordCollection> cList;
        // 集合属性对应图
        HashMap<String,ArrayList<RecordCollection>> collectionMap = new HashMap<>();

        // 1. 截取出条件属性部分
        // 遍历属性名称
        for (int i = 1; i < attrNames.length - 1; i++) {
            // 属性名
            currentAttrName = attrNames[i];

            // 判断此属性是否需要约简
            // 如果包含在需要约简的属性列表中
            if (reduceAttr != null && reduceAttr.contains(currentAttrName)) {
                continue;
            }

            // 创建记录集合列表
            cList = new ArrayList<>();

            // 遍历全局记录集合列表
            for (RecordCollection rc : collectionList) {
                if (rc.isContainedAttrName(currentAttrName)) {
                    cList.add(rc);
                }
            }

            collectionMap.put(currentAttrName, cList);
        }

        return collectionMap;
    }


    /*
    *   根据已有的分裂集合计算信息系统
    *
    * */
    private ArrayList<RecordCollection> computeKnowledgeSystem(HashMap<String,ArrayList<RecordCollection>> collectionMap) {
        String attrName = null;
        ArrayList<RecordCollection> cList = null;
        // 信息系统
        ArrayList<RecordCollection> ksCollections = new ArrayList<>();

        // 取出一项
        for (Map.Entry entry : collectionMap.entrySet()) {
            attrName = (String) entry.getKey();
            cList = (ArrayList<RecordCollection>) entry.getValue();
            break;
        }
        collectionMap.remove(attrName);

        for (RecordCollection rc: cList) {
            recurrentComputeKS(ksCollections,collectionMap,rc);
        }

        return ksCollections;
    }


    /*
    *   递归计算所有的信息系统,通过计算所有集合的交集
    *
    *   ksCollections 已经求得信息系统的集合
    *
    *   map 还未曾进行 "交" 运算的集合
    *
    *   preCollection 前个步骤中已经通过交运算计算出的集合
    * */
    private void recurrentComputeKS(ArrayList<RecordCollection> ksCollections,
                                    HashMap<String, ArrayList<RecordCollection>> map,
                                    RecordCollection preCollection) {

        String attrName = null;
        RecordCollection tempCollection;
        ArrayList<RecordCollection> cList = null;
        HashMap<String, ArrayList<RecordCollection>> mapCopy = new HashMap<>();

        // 如果已经没有数据了,直接添加
        if (map.size() == 0) {
            ksCollections.add(preCollection);
            return;
        }

        for (Map.Entry entry : map.entrySet()) {
            cList = (ArrayList<RecordCollection>) entry.getValue();
            mapCopy.put((String) entry.getKey(), cList);
        }

        // 取出一项
        for (Map.Entry entry : map.entrySet()) {
            attrName = (String) entry.getKey();
            cList = (ArrayList<RecordCollection>) entry.getValue();
            break;
        }

        mapCopy.remove(attrName);

        for (RecordCollection rc : cList) {
            // 挑选此属性的一个集合进行 "交" 运算,然后再次递归
            tempCollection = preCollection.overlapCalculate(rc);

            if (tempCollection == null) {
                continue;
            }

            // 如果map中没有数据了,说明递归到头了
            if (mapCopy.size() == 0) {
                ksCollections.add(tempCollection);
            } else {
                recurrentComputeKS(ksCollections, mapCopy, tempCollection);
            }
        }

    }


    /*
    *   进行粗糙集属性约简算法
    *
    * */
    public void findingReduce() {
        RecordCollection[] sameClassRs;
        KnowledgeSystem ks;
        ArrayList<RecordCollection> ksCollections;
        // 待约简的属性
        ArrayList<String> reduceAttr = null;
        ArrayList<String> attrNameList;
        // 最终可约简的属性组
        ArrayList<ArrayList<String>> canReduceAttrs;
        HashMap<String, ArrayList<RecordCollection>> collectionMap;

        // 选出决策属性一致的集合
        sameClassRs = selectTheSameClassRC();

        // 这里将数据按照各个分类的小属性划分了9个集合
        recordSplitToCollection();
        // 构建集合图
        collectionMap = constructCollectionMap(reduceAttr);
        // 计算信息系统
        ksCollections = computeKnowledgeSystem(collectionMap);
        ks = new KnowledgeSystem(ksCollections);
        System.out.print("原始集合分类的上下近似集合");
        // 获取下近似属性集
        ks.getDownSimilarRC(sameClassRs[0]).printRc();
        // 获取上近似属性集
        ks.getUpSimilarRC(sameClassRs[0]).printRc();
        // 获取x2下近似属性集
        ks.getDownSimilarRC(sameClassRs[1]).printRc();
        // 获取x2上近似属性集
        ks.getUpSimilarRC(sameClassRs[1]).printRc();

        // 创建属性名称列表
        attrNameList = new ArrayList<>();
        for (int i = 0; i < attrNames.length; i++) {
            attrNameList.add(attrNames[i]);
        }

        ArrayList<String> remainAttr;
        // 创建可约简的属性
        canReduceAttrs = new ArrayList<>();
        reduceAttr = new ArrayList<>();
        // 进行条件属性的递归约简
        for (String s: attrNameList) {
            remainAttr = (ArrayList<String>) attrNameList.clone();
            remainAttr.remove(s);
            reduceAttr = new ArrayList<>();
            reduceAttr.add(s);
            recurrenceFindingReduce(canReduceAttrs, reduceAttr, remainAttr, sameClassRs);
        }

        printRules(canReduceAttrs);
    }


    /*
    *   输出决策规则
    *
    *   reduceAttrArray 约简属性组
    * */
    public void printRules(ArrayList<ArrayList<String>> reduceAttrArray) {
        // 用来保存已经描述过的规则,避免重复输出
        ArrayList<String> rulesArray;
        String rule;
        for (ArrayList<String> ra : reduceAttrArray) {
            rulesArray = new ArrayList<>();
            System.out.print("约简的属性: ");
            for (String s: ra) {
                System.out.print(s + ",");
            }
            System.out.println();

            for (Record r: totalRecords) {
                rule = r.getDecisionRule(ra);
                if (!rulesArray.contains(rule)) {
                    rulesArray.add(rule);
                    System.out.println(rule);
                }
            }
            System.out.println();
        }
    }



    /*
    *   递归进行属性约简
    *
    *   resultAttr 已经计算出的约简属性组
    *   reduceAttr 将要约简的属性组
    *   remainAttr 剩余的属性
    *   sameClassRc 待计算上下近似集合的同类集合
    * */
    private void recurrenceFindingReduce(
            ArrayList<ArrayList<String>> resultAttr,
            ArrayList<String> reduceAttr,
            ArrayList<String> remainAttr,
            RecordCollection[] sameClassRc) {
        KnowledgeSystem ks;
        ArrayList<RecordCollection> ksCollections;
        ArrayList<String> copyRemainAttr;
        ArrayList<String> copyReduceAttr;
        HashMap<String,ArrayList<RecordCollection>> collectionMap;
        RecordCollection upRc1;
        RecordCollection downRc1;
        RecordCollection upRc2;
        RecordCollection downRc2;

        // 根据约简属性列表构建集合图
        collectionMap = constructCollectionMap(reduceAttr);
        // 根据集合图计算信息系统
        ksCollections = computeKnowledgeSystem(collectionMap);
        // 根据信息系统集合创建信息系统
        ks = new KnowledgeSystem(ksCollections);

        downRc1 = ks.getDownSimilarRC(sameClassRc[0]);
        upRc1 = ks.getUpSimilarRC(sameClassRc[0]);
        downRc2 = ks.getDownSimilarRC(sameClassRc[1]);
        upRc2 = ks.getUpSimilarRC(sameClassRc[1]);

        // 如果上下近似没有完全拟合原集合 则认为属性不能被约简
        if (!upRc1.isCollectionSame(sameClassRc[0])
                || !downRc1.isCollectionSame(sameClassRc[0])) {
            return;
        }

        // 正类和负类都需比较
        if (!upRc2.isCollectionSame(sameClassRc[1])
                || !downRc2.isCollectionSame(sameClassRc[1])) {
            return;
        }

        // 把待约简属性集加入到结果集中
        resultAttr.add(reduceAttr);
        // 只剩下一个属性不能再约简
        if (remainAttr.size() == 1) {
            return;
        }

        for (String s: remainAttr) {
            copyRemainAttr = (ArrayList<String>) remainAttr.clone();
            copyReduceAttr = (ArrayList<String>) reduceAttr.clone();
            copyRemainAttr.remove(s);
            copyReduceAttr.add(s);

            // 递归查找待约简属性
            recurrenceFindingReduce(resultAttr, copyReduceAttr, copyRemainAttr, sameClassRc);
        }

    }


    /*
    *   选出决策属性一致的集合
    *
    * */
    private RecordCollection[] selectTheSameClassRC() {
        RecordCollection[] resultRC = new RecordCollection[2];
        resultRC[0] = new RecordCollection();
        resultRC[1] = new RecordCollection();
        String attrValue;

        // 找出第一个记录的决策属性作为一个分类
        attrValue = totalRecords.get(0).getRecordDecisionClass();
        for (Record r : totalRecords) {
            if (attrValue.equals(r.getRecordDecisionClass())) {
                resultRC[0].getRecordList().add(r);
            } else {
                resultRC[1].getRecordList().add(r);
            }
        }
        return resultRC;
    }


    /*
    *   输出记录集合
    *
    *   rcList 待输出记录集合
    * */
    public void printRecordCollectionList(ArrayList<RecordCollection> rcList) {
        for (RecordCollection rc : rcList) {
            System.out.print("{");
            for (Record r : rc.getRecordList()) {
                System.out.print(r.getName() + ", ");
            }
            System.out.println("}");
        }
    }




}
