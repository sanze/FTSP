package com.fujitsu.manager.faultManager.util;

import java.util.HashMap;
import java.util.Iterator;

import org.springframework.util.StringUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongodbGroupUtil {
	/**
	 * 方法描述：根据用户选择的维度编码和指标编码，生成Group中的key
	 * 
	 * @param dimMap
	 *            维度编码
	 * @return key 对象
	 */
	public static DBObject generateFormulaKeyObject(HashMap dimMap) {
		DBObject key = new BasicDBObject();
		Iterator dimIt = dimMap.keySet().iterator();
		while (dimIt.hasNext()) {
			String dimId = (String) dimIt.next();
			key.put(dimId, true);
		}
		return key;
	}

	/**
	 * 方法描述：根据用户选择的维度编码和指标编码，生成Group中的属性初始化值
	 * 
	 * @param dimMap
	 *            维度编码
	 * @return key 对象
	 */
	public static DBObject generateFormulaInitObject(HashMap indexMap) {
		DBObject initial = new BasicDBObject();
		// 设置计算指标中使用的指标对应的统计值：sum、count、avg、max、min
		Iterator indexIt = indexMap.keySet().iterator();
		while (indexIt.hasNext()) {
			DBObject index = new BasicDBObject();
			index.put("count", 0);
			index.put("sum", 0);
			index.put("max", 0);
			index.put("min", 0);
			index.put("avg", 0);
			index.put("self", 0);
			String indexId = (String) indexIt.next();
			initial.put(indexId, index);
		}

		return initial;
	}

	/**
	 * 方法描述：根据用户选择的指标编码，生成Group中的reduce函数
	 * 
	 * @param indexMap
	 *            指标编码
	 * @return reduce函数
	 */
	public static String generateFormulaReduceObject(HashMap indexMap) {
		StringBuffer reduceBuf = new StringBuffer();

		reduceBuf.append("function(doc, prev) {");
		reduceBuf.append("var tempVal;");
		Iterator indexIt = indexMap.keySet().iterator();
		while (indexIt.hasNext()) {
			String indexId = (String) indexIt.next();
			// 计算指标数量
			reduceBuf.append("prev.").append(indexId).append(".count ++;");
			// 计算指标总计
			reduceBuf.append("if(isNaN(").append("prev.").append(indexId)
					.append(".sum").append(")){");
			reduceBuf.append("prev.").append(indexId).append(".sum = 0;");
			reduceBuf.append("}");
			reduceBuf.append("prev.").append(indexId)
					.append(".sum += parseFloat(doc.").append(indexId)
					.append(");");
			reduceBuf.append("if(isNaN(").append("prev.").append(indexId)
					.append(".self").append(")){");
			reduceBuf.append("prev.").append(indexId).append(".self = 0;");
			reduceBuf.append("}");
			reduceBuf.append("prev.").append(indexId)
					.append(".self = parseFloat(doc.").append(indexId)
					.append(");");

			reduceBuf.append("print(prev.").append(indexId).append(".self);");
			// 计算指标最大值
			reduceBuf.append("tempVal = parseFloat(doc.").append(indexId)
					.append(");");
			reduceBuf.append("if(").append("prev.").append(indexId)
					.append(".max == 0").append("){");
			reduceBuf.append("prev.").append(indexId).append(".max = tempVal;");
			reduceBuf.append("}else{");
			reduceBuf.append("prev.").append(indexId).append(".max = ");
			reduceBuf.append("prev.").append(indexId)
					.append(".max > tempVal ? ");
			reduceBuf.append("prev.").append(indexId).append(".max : tempVal;");
			reduceBuf.append("}");
			// 计算指标最小值
			reduceBuf.append("if(").append("prev.").append(indexId)
					.append(".min == 0").append("){");
			reduceBuf.append("prev.").append(indexId).append(".min = tempVal;");
			reduceBuf.append("}else{");
			reduceBuf.append("prev.").append(indexId).append(".min = ");
			reduceBuf.append("prev.").append(indexId)
					.append(".min < tempVal ? ");
			reduceBuf.append("prev.").append(indexId).append(".min : tempVal;");
			reduceBuf.append("}");
			// 计算指标的平均值
			reduceBuf.append("prev.").append(indexId).append(".avg = ");
			reduceBuf.append("prev.").append(indexId).append(".sum");
			reduceBuf.append(" / ");
			reduceBuf.append("prev.").append(indexId).append(".count;");
		}
		reduceBuf.append("}");

		return reduceBuf.toString();
	}

	/**
	 * 方法描述：根据用户选择的指标编码，生成MapReduce中的finalize函数
	 * 
	 * @param indexMap
	 *            指标编码
	 * @return reduce函数
	 */
	public static String generateFormulaFinalizeObject(HashMap forIdxMap,
			HashMap indexMap) {
		StringBuffer reduceBuf = new StringBuffer();
		reduceBuf.append("function(doc){");
		// 得到计算指标的公式运行值
		Iterator formulaIt = forIdxMap.keySet().iterator();
		while (formulaIt.hasNext()) {
			String indexId = (String) formulaIt.next();
			String idxFormula = (String) forIdxMap.get(indexId);
			reduceBuf.append("var tempIdx, tempFormula;");
			Iterator indexItB = indexMap.keySet().iterator();
			int i = 0;
			while (indexItB.hasNext()) {
				String indexIdS = (String) indexItB.next();
				if (i == 0) {
					reduceBuf.append("tempFormula = \"").append(idxFormula)
							.append("\";");
				}
				reduceBuf.append("tempIdx = ").append("doc.").append(indexIdS)
						.append(".sum;");
				reduceBuf.append("tempFormula = ").append("tempFormula")
						.append(".replace(/sum\\(").append(indexIdS)
						.append("\\)/g,tempIdx);");
				reduceBuf.append("tempIdx = ").append("doc.").append(indexIdS)
						.append(".count;");
				reduceBuf.append("tempFormula = ").append("tempFormula")
						.append(".replace(/count\\(").append(indexIdS)
						.append("\\)/g,tempIdx);");
				reduceBuf.append("tempIdx = ").append("doc.").append(indexIdS)
						.append(".min;");
				reduceBuf.append("tempFormula = ").append("tempFormula")
						.append(".replace(/min\\(").append(indexIdS)
						.append("\\)/g,tempIdx);");
				reduceBuf.append("tempIdx = ").append("doc.").append(indexIdS)
						.append(".max;");
				reduceBuf.append("tempFormula = ").append("tempFormula")
						.append(".replace(/max\\(").append(indexIdS)
						.append("\\)/g,tempIdx);");
				reduceBuf.append("tempIdx = ").append("doc.").append(indexIdS)
						.append(".avg;");
				reduceBuf.append("tempFormula = ").append("tempFormula")
						.append(".replace(/avg\\(").append(indexIdS)
						.append("\\)/g,tempIdx);");
				i++;
			}
			reduceBuf.append("var resTemp = ").append("eval(tempFormula);");
			reduceBuf.append("doc.").append(indexId)
					.append(" = resTemp.toFixed(2);");
		}

		Iterator indexItC = indexMap.keySet().iterator();
		while (indexItC.hasNext()) {
			String indexId = (String) indexItC.next();
			reduceBuf.append("doc.").append(indexId).append(" = doc.")
					.append(indexId).append(".self;");
		}
		reduceBuf.append("}");

		return reduceBuf.toString();
	}
	
	/**
     * keyColumn : new String[]{"xxxName","xxxType"} <br>
     * condition : 查询条件 ，可为空<br>
     * initial : 分组统计初始变量，为空时自动为每列提供初始变量<br>
     * reduce ： 记录处理function<br>
     * finalize : finalize函数，可为空 <br>
     */
    public static BasicDBList group(DBCollection coll,String[] keyColumn, DBObject condition,
            DBObject initial, String reduce, String finalize) {
        DBObject key = new BasicDBObject();
        for (int i = 0; i < keyColumn.length; i++) {
            key.put(keyColumn[i], true);
        }
        condition = (condition == null) ? new BasicDBObject() : condition;
        if (StringUtils.isEmpty(finalize)) {
            finalize = null;
        }
        if (initial == null) {      //定义一些初始变量
            initial = new BasicDBObject();
            for (int i = 0; i < keyColumn.length; i++) {
                DBObject index = new BasicDBObject();
                index.put("count", 0);
                index.put("sum", 0);
                index.put("max", 0);
                index.put("min", 0);
                index.put("avg", 0);
                index.put("self", "");
                initial.put(keyColumn[i], index);
            }
        }
        BasicDBList resultList = (BasicDBList) coll.group(key, condition,initial, reduce, finalize);
        return resultList;
    }
	
	
}
