package com.neo.autotrader.feature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.neo.autotrader.data.DailyInfo;
import com.neo.autotrader.data.StockInfo;
import com.neo.autotrader.feature.basic.Close;
import com.neo.autotrader.feature.basic.High;
import com.neo.autotrader.feature.basic.Low;
import com.neo.autotrader.feature.basic.Open;
import com.neo.autotrader.feature.basic.Volume;
import com.neo.autotrader.feature.kdj.RSV;
import com.neo.autotrader.feature.logic.Branch;
import com.neo.autotrader.feature.logic.Or;
import com.neo.autotrader.feature.ma.EMA;
import com.neo.autotrader.feature.ma.SMA;
import com.neo.autotrader.feature.math.*;
import com.neo.autotrader.feature.util.Const;
import com.neo.autotrader.feature.util.Cross;
import com.neo.autotrader.feature.util.DecreaseCycle;
import com.neo.autotrader.feature.util.IncreaseCycle;
import com.neo.autotrader.feature.util.Shift;
import com.neo.autotrader.model.LogisticRegression;
import com.neo.autotrader.spider.YahooSpider;

public class ValueManager {

    private int size;
    private Map<String, Value> valueMap;

    public ValueManager(int size) {
        this.size = size;
        valueMap = new HashMap<String, Value>();
    }

    public Set<String> getValueNames() {
        return valueMap.keySet();
    }

    public List<Map<String, Double>> getAll() {
        List<Map<String, Double>> records =
                new ArrayList<Map<String, Double>>();
        for (int i = 0; i < size; i++) {
            records.add(get(i));
        }
        return records;
    }

    public Map<String, Double> get(int index) {
        Map<String, Double> record = new HashMap<String, Double>();
        for (String name : valueMap.keySet()) {
            record.put(name, valueMap.get(name).get(0, index));
        }
        return record;
    }

    public Map<String, Double> getLast(int index) {
        return get(size - 1 - index);
    }

    public void define(String name, Function function) {
        define(name, function, new String[0]);
    }

    public void define(String name, Function function, String... paramNames) {
        Value value = new Value(name);
        valueMap.put(name, value);

        Value paramValues[] = new Value[paramNames.length];
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Value paramValue = valueMap.get(paramName);
            paramValues[i] = paramValue;
        }

        for (int i = 0; i < size; i++) {
            value.append(function.evaluate(value, i, paramValues));
        }
    }

    public static void main(String args[]) {
        List<Map<String, Double>> records = getRecords(
                Arrays.asList(
                        "603026.SS"
//						"600048.SS",
//						"600895.SS",
//						"600708.SS",
//						"600170.SS",
//						"600201.SS",
//						"600601.SS",
//						"600030.SS",
//						"600639.SS"
                ),
                1000);
        //Collections.shuffle(records);
        learn(records);
    }

    private static void learn(List<Map<String, Double>> records) {
        final double ratio = 0.8D;

        int trainingSize = (int) (records.size() * ratio);

        List<Map<String, Double>> trainingRecords =
                new ArrayList<Map<String, Double>>();
        for (int i = 0; i < trainingSize; i++) {
            trainingRecords.add(records.get(i));
        }

        List<Map<String, Double>> testingRecords =
                new ArrayList<Map<String, Double>>();
        for (int i = trainingSize; i < records.size(); i++) {
            testingRecords.add(records.get(i));
        }

        List<String> featureNames = Arrays.asList(//"1",

//				"close-inc-cycle", "close-dec-cycle",
//				"vol-inc-cycle", "vol-dec-cycle",
//				"close-inc-cycle^2", "close-dec-cycle^2",
//				"vol-inc-cycle^2", "vol-dec-cycle^2",

                "k(3)_X_d(3)", "d(3)_X_k(3)",
                "diff_X_dea", "dea_X_diff",

				"trend-1", "trend-2", "trend-3", "trend-4", "trend-5", "trend-6", "trend-7", "trend-8"
//				"voltrend-1", "voltrend-2", "voltrend-3", "voltrend-4", "voltrend-5", "voltrend-6", "voltrend-7", "voltrend-8",

//				"k(3)", "d(3)", "k(3)?d(3)",
//				"k(3)?0.2", "k(3)?0.5", "k(3)?0.8", "d(3)?0.2", "d(3)?0.5", "d(3)?0.8",
//
//				"diff", "dea", "diff?dea"
//				"sign(diff)", "sign(dea)"

//				"longrsi", "longrsi?0.2", "longrsi?0.5", "longrsi?0.8",
//				"midrsi", "midrsi?0.2", "midrsi?0.5", "midrsi?0.8",
//				"shortrsi", "shortrsi?0.2", "shortrsi?0.5", "shortrsi?0.8"
        );
        String targetName = "earn";

        format("train.txt", trainingRecords, targetName, featureNames);
        format("test.txt", testingRecords, targetName, featureNames);

        Map<String, Double> coefs = LogisticRegression.regress(trainingRecords,
                featureNames, targetName, 10000, 1e-3, 0);
        LogisticRegression.validate(coefs, trainingRecords, targetName, 0.5);
        LogisticRegression.validate(coefs, testingRecords, targetName, 0.5);
    }

    private static List<Map<String, Double>> getRecords(List<String> ids,
                                                        int latestSize) {
        List<Map<String, Double>> records = new ArrayList<Map<String, Double>>();
        for (String id : ids) {
            records.addAll(getRecords(id, latestSize));
        }
        return records;
    }

    private static List<Map<String, Double>> getRecords(String id,
                                                        int latestSize) {
        StockInfo stockInfo = YahooSpider.spide(id);
        System.out.println("spide " + id + " done");


        List<DailyInfo> historyInfo = stockInfo.historyInfo;
        int size = historyInfo.size();
        if (size < latestSize) latestSize = size;
        List<DailyInfo> latestHistoryInfo = new ArrayList<DailyInfo>();
        for (int i = 0; i < latestSize; i++) {
            latestHistoryInfo.add(historyInfo.get(size - latestSize + i));
        }
        historyInfo = latestHistoryInfo;

        ValueManager manager = new ValueManager(historyInfo.size());
        manager.define("high", new High(historyInfo));
        manager.define("low", new Low(historyInfo));
        manager.define("open", new Open(historyInfo));
        manager.define("close", new Close(historyInfo));
        manager.define("vol", new Volume(historyInfo));

        // Const
        manager.define("0", new Const(0D));
        manager.define("0.2", new Const(0.2D));
        manager.define("0.5", new Const(0.5D));
        manager.define("0.8", new Const(0.8D));
        manager.define("1", new Const(1D));
        manager.define("1.01", new Const(1.01D));
        manager.define("1.02", new Const(1.02D));
        manager.define("1.03", new Const(1.03D));
        manager.define("1.04", new Const(1.04D));
        manager.define("1.05", new Const(1.05D));

        // Shifted Close
        manager.define("close+4", new Shift(4, 0D), "close");
        manager.define("close+3", new Shift(3, 0D), "close");
        manager.define("close+2", new Shift(2, 0D), "close");
        manager.define("close+1", new Shift(1, 0D), "close");

        manager.define("close-1", new Shift(-1, 0D), "close");
        manager.define("close-2", new Shift(-2, 0D), "close");
        manager.define("close-3", new Shift(-3, 0D), "close");
        manager.define("close-4", new Shift(-4, 0D), "close");
        manager.define("close-5", new Shift(-5, 0D), "close");
        manager.define("close-6", new Shift(-6, 0D), "close");
        manager.define("close-7", new Shift(-7, 0D), "close");
        manager.define("close-8", new Shift(-8, 0D), "close");

        // Trend of Close
        manager.define("trend+4", new Compare(), "close+4", "close");
        manager.define("trend+3", new Compare(), "close+3", "close");
        manager.define("trend+2", new Compare(), "close+2", "close");
        manager.define("trend+1", new Compare(), "close+1", "close");

        manager.define("trend-1", new Compare(), "close", "close-1");
        manager.define("trend-2", new Compare(), "close", "close-2");
        manager.define("trend-3", new Compare(), "close", "close-3");
        manager.define("trend-4", new Compare(), "close", "close-4");
        manager.define("trend-5", new Compare(), "close", "close-5");
        manager.define("trend-6", new Compare(), "close", "close-6");
        manager.define("trend-7", new Compare(), "close", "close-7");
        manager.define("trend-8", new Compare(), "close", "close-8");

        // Shifted Volume
        manager.define("vol-1", new Shift(-1, 0D), "vol");
        manager.define("vol-2", new Shift(-2, 0D), "vol");
        manager.define("vol-3", new Shift(-3, 0D), "vol");
        manager.define("vol-4", new Shift(-4, 0D), "vol");
        manager.define("vol-5", new Shift(-5, 0D), "vol");
        manager.define("vol-6", new Shift(-6, 0D), "vol");
        manager.define("vol-7", new Shift(-7, 0D), "vol");
        manager.define("vol-8", new Shift(-8, 0D), "vol");

        // Trend of Volume
        manager.define("voltrend-1", new Compare(), "vol", "vol-1");
        manager.define("voltrend-2", new Compare(), "vol", "vol-2");
        manager.define("voltrend-3", new Compare(), "vol", "vol-3");
        manager.define("voltrend-4", new Compare(), "vol", "vol-4");
        manager.define("voltrend-5", new Compare(), "vol", "vol-5");
        manager.define("voltrend-6", new Compare(), "vol", "vol-6");
        manager.define("voltrend-7", new Compare(), "vol", "vol-7");
        manager.define("voltrend-8", new Compare(), "vol", "vol-8");

        // Cycle
        manager.define("close-inc-cycle", new IncreaseCycle(), "close");
        manager.define("close-inc-cycle^2", new Power(2), "close-inc-cycle");
        manager.define("close-dec-cycle", new DecreaseCycle(), "close");
        manager.define("close-dec-cycle^2", new Power(2), "close-dec-cycle");
        manager.define("vol-inc-cycle", new IncreaseCycle(), "vol");
        manager.define("vol-inc-cycle^2", new Power(2), "vol-inc-cycle");
        manager.define("vol-dec-cycle", new DecreaseCycle(), "vol");
        manager.define("vol-dec-cycle^2", new Power(2), "vol-dec-cycle");

        // KDJ
        manager.define("rvs(9)", new RSV(9), "high", "low", "close");
        manager.define("k(3)", new SMA(3, 1), "rvs(9)");
        manager.define("d(3)", new SMA(3, 1), "k(3)");
        manager.define("k(3)?d(3)", new Compare(), "k(3)", "d(3)");

        manager.define("k(3)_X_d(3)", new Cross(), "k(3)", "d(3)");
        manager.define("d(3)_X_k(3)", new Cross(), "d(3)", "k(3)");

        manager.define("k(3)?0.2", new Compare(), "k(3)", "0.2");
        manager.define("k(3)?0.5", new Compare(), "k(3)", "0.5");
        manager.define("k(3)?0.8", new Compare(), "k(3)", "0.8");
        manager.define("d(3)?0.2", new Compare(), "d(3)", "0.2");
        manager.define("d(3)?0.5", new Compare(), "d(3)", "0.5");
        manager.define("d(3)?0.8", new Compare(), "d(3)", "0.8");

        // MACD
        manager.define("ema(26)", new EMA(26), "close");
        manager.define("ema(12)", new EMA(12), "close");
        manager.define("diff", new Minus(), "ema(12)", "ema(26)");
        manager.define("dea", new EMA(9), "diff");

        manager.define("diff_X_dea", new Cross(), "diff", "dea");
        manager.define("dea_X_diff", new Cross(), "dea", "diff");

        manager.define("diff?dea", new Compare(), "dea", "diff");
        manager.define("sign(diff)", new Sign(), "diff");
        manager.define("sign(dea)", new Sign(), "dea");

        // RSI
        manager.define("diffclose-1", new Minus(), "close", "close-1");
        manager.define("buystrength", new Max(), "diffclose-1", "0");
        manager.define("strength", new Abs(), "diffclose-1");

        manager.define("longbuystrength", new SMA(24, 1), "buystrength");
        manager.define("longstrength", new SMA(24, 1), "strength");
        manager.define("longrsi", new Divide(), "longbuystrength", "longstrength");
        manager.define("longrsi?0.2", new Compare(), "longrsi", "0.2");
        manager.define("longrsi?0.5", new Compare(), "longrsi", "0.5");
        manager.define("longrsi?0.8", new Compare(), "longrsi", "0.8");

        manager.define("midbuystrength", new SMA(12, 1), "buystrength");
        manager.define("midstrength", new SMA(12, 1), "strength");
        manager.define("midrsi", new Divide(), "midbuystrength", "midstrength");
        manager.define("midrsi?0.2", new Compare(), "midrsi", "0.2");
        manager.define("midrsi?0.5", new Compare(), "midrsi", "0.5");
        manager.define("midrsi?0.8", new Compare(), "midrsi", "0.8");

        manager.define("shortbuystrength", new SMA(6, 1), "buystrength");
        manager.define("shortstrength", new SMA(6, 1), "strength");
        manager.define("shortrsi", new Divide(), "shortbuystrength", "shortstrength");
        manager.define("shortrsi?0.2", new Compare(), "shortrsi", "0.2");
        manager.define("shortrsi?0.5", new Compare(), "shortrsi", "0.5");
        manager.define("shortrsi?0.8", new Compare(), "shortrsi", "0.8");

        manager.define("longrsi?midrsi", new Compare(), "longrsi", "midrsi");
        manager.define("midrsi?shortrsi", new Compare(), "midrsi", "shortrsi");

        // Customized
        manager.define("ratio1", new Divide(), "close+1", "close");
        manager.define("earn1", new Compare(), "ratio1", "1");
        manager.define("ratio2", new Divide(), "close+2", "close");
        manager.define("earn2", new Compare(), "ratio2", "1");

        manager.define("earn", new Or(), "earn1", "earn2");
        manager.define("ratio", new Branch(), "earn1", "ratio1", "ratio2");

        return manager.getAll();
    }

    private static void format(String path, List<Map<String, Double>> records,
                               String targetName, List<String> featureNames) {
        try {
            File file = new File(path);
            StringBuffer buf = new StringBuffer();
            for (Map<String, Double> record : records) {
                buf.append(record.get(targetName));
                for (int i = 0; i < featureNames.size(); i++) {
                    buf.append(" ").append(i + 1).append(":");
                    buf.append(record.get(featureNames.get(i)));
                }
                buf.append("\n");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(buf.toString());
            writer.close();
        } catch (IOException ex) {
        }
    }
}
