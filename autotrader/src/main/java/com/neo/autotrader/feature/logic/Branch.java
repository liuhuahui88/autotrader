package com.neo.autotrader.feature.logic;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

/**
 * Created by neo on 4/1/17.
 */
public class Branch implements Function {
    @Override
    public Double evaluate(Value self, int base, Value... values) {
        return values[0].get(base, 0) > 0D ? values[1].get(base, 0) : values[2].get(base, 0);
    }
}
