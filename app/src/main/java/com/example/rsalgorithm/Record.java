package com.example.rsalgorithm;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    数据记录，包含这条记录所有属性
*
* */
public class Record {

    // 记录名称
    private String name;

    // 记录属性键值对
    private HashMap<String, String> attrValues;

    public Record(String name, HashMap<String,String> attrValues) {
        this.name = name;
        this.attrValues = attrValues;
    }

    public String getName() {
        return this.name;
    }

    /*
    *   此数据是否包含此属性值
    *
    *   attr 属性值
    * */
    public boolean isContainedAttr(String attr) {
        boolean isContained = false;
        if (this.attrValues.containsValue(attr)) {
            isContained = true;
        }
        return isContained;
    }

    /*
    *   判断数据记录是否是同一条记录，根据数据名称来判断
    *
    *   record : 目标比较对象
    *
    * */
    public boolean isRecordSame(Record record) {
        boolean isSame = false;
        if (this.name.equals(record.name)) {
            isSame = true;
        }
        return isSame;
    }


    /*
    *   获取数据的记录决策属性分类
    *
    * */
    public String getRecordDecisionClass() {
        String value = attrValues.get(RoughSetsTool.DECISION_ATTR_NAME);
        return value;
    }


    /*
    *   根据约简属性输出决策规则
    *
    *   reduceAttr 约简属性集合
    * */
    public String getDecisionRule(ArrayList<String> reduceAttr) {
        String ruleStr = "";
        String attrName = null;
        String value = null;
        String decisionValue;
        // 决策值
        decisionValue = attrValues.get(RoughSetsTool.DECISION_ATTR_NAME);
        ruleStr += "属性";
        for (Map.Entry entry : this.attrValues.entrySet()) {
            attrName = (String) entry.getKey();
            value = (String) entry.getValue();

            if (attrName.equals(RoughSetsTool.DECISION_ATTR_NAME)
                    || reduceAttr.contains(attrName)
                    || value.equals(name)) {
                continue;
            }

            ruleStr += MessageFormat.format("{0}={1},", attrName, value);
        }
        ruleStr += "他的分类为" + decisionValue;

        return ruleStr;
    }


}
