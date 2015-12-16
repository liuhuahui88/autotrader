package com.neo.autotrader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticRegression {

	public static Map<String, Double> regress(List<Map<String, Double>> records,
			List<String> featureNames, String targetName,
			int round, double learningRate, double lambda) {
		Map<String, Double> coefs = new HashMap<String, Double>();
		for (String featureName : featureNames) {
			coefs.put(featureName, Math.random() - 0.5);
		}
		System.out.println("init " + coefs);
		
		for (int i = 0; i < round; i++) {
			for (Map<String, Double> record : records) {
				double product = product(coefs, record);
				int y = record.get(targetName) > 0D ? 1 : -1;
				for (String key : coefs.keySet()) {
					double coef = coefs.get(key);
					double x = record.get(key);
					double grad = -y * x / (1D + Math.exp(y * product))
							+ lambda * coef;
					coefs.put(key, coef - learningRate * grad);
				}
			}
			if (i % 100 == 0) {
				System.out.println(i + " : " + coefs);
			}
		}
		
		return coefs;
	}
	
	public static double product(Map<String, Double> coefs,
			Map<String, Double> record) {
		double result = 0D;
		for (String key : coefs.keySet()) {
			result += coefs.get(key) * record.get(key);
		}
		return result;
	}
	
	public static double predict(Map<String, Double> coefs,
			Map<String, Double> record) {
		return 1D / (1D + Math.exp(-product(coefs, record)));
	}
	
	public static void validate(Map<String, Double> coefs,
			List<Map<String, Double>> records, String targetName,
			double threshold) {
		int t2t = 0;
		int t2f = 0;
		int f2t = 0;
		int f2f = 0;
		double ratio = 1D;
		for (Map<String, Double> record : records) {
			int y = record.get(targetName) > 0D ? 1 : -1;
			double prob = predict(coefs, record);
			if (y == 1) {
				if (prob > threshold) {t2t++; if(record.get("ratio") >= 0.9D && record.get("ratio") <= 1.1D) ratio *= record.get("ratio");}
				else t2f++;
			} else {
				if (prob > threshold) {f2t++; if(record.get("ratio") >= 0.9D && record.get("ratio") <= 1.1D) ratio *= record.get("ratio");}
				else f2f++;
			}
		}
		System.out.println("t2t " + t2t);
		System.out.println("t2f " + t2f);
		System.out.println("f2t " + f2t);
		System.out.println("f2f " + f2f);
		System.out.println("score " + (double)(t2t + f2f) / records.size());
		System.out.println("baseline " + (double)(t2t + t2f) / records.size());
		System.out.println("precision " + (double)t2t / (t2t + f2t));
		System.out.println("recall " + (double)t2t / (t2t + t2f));
		System.out.println("earn " + Math.pow(ratio, 1D / (t2t + f2t)));
	}
	
	public static void main(String args[]) {
		List<Map<String, Double>> records = new ArrayList<Map<String, Double>>();
		Map<String, Double> r1 = new HashMap<String, Double>();
		r1.put("x", 1D);
		r1.put("y", 1D);
		records.add(r1);
		Map<String, Double> r2 = new HashMap<String, Double>();
		r2.put("x", -1D);
		r2.put("y", -1D);
		records.add(r2);
		
		Map<String, Double> coefs = regress(records, Arrays.asList("x"),
				"y", 10000, 1e-1, 1e-2);
		
		System.out.println(coefs);
		System.out.println(predict(coefs, r1));
		System.out.println(predict(coefs, r2));

		validate(coefs, records, "y", 0.5);
	}
}
