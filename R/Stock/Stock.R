library(quantmod)
library(h2o)

getCompleteCode <-function(code) {
    exchange <- switch(substr(code, 1, 1),
                       "0" = "SZ",
                       "1" = "SZ",
                       "3" = "SZ",
                       "5" = "SS",
                       "6" = "SS"
                       )
    paste(code, exchange, sep=".")
}

getAdjustedStock <- function(code) {
    code <- getCompleteCode(code)
    print(paste('handling ', code))
    tryCatch(adjustOHLC(getSymbols(code, auto.assign=F), use.Adjusted=T), error=function(e){NA})
}

getAdjustedStocks <- function(codes) {
    lapply(codes, getAdjustedStock)
}

chartAround <- function(stock, date, window=10) {
    index = which(time(stock) == date)[1]
    from = max(1, index-window)
    to = min(dim(stock)[[1]], index+window)
    chartSeries(stock[from:to,])
}

extract <- function(stock, ops) {
    do.call(merge.xts, lapply(ops, function(op) {
        op(stock)
    }))
}

extractAndMerge <- function(stocks, ops) {
    do.call(merge.xts, lapply(stocks, function(stock) {
        extract(stock, ops)
    }))
}

extractAndUnion <- function(stocks, ops) {
    do.call(rbind, lapply(stocks, function(stock) {
         as.matrix(extract(stock, ops))
    }))
}

Lead <- function(x, k=1) {
    do.call(merge.xts, lapply(k, function(offset){
        xts(Next(x, offset), time(x))
    }))
}

Abstract <- function(x, k, f, n) {
    t = xts(apply(Lead(x, seq_len(k)), 1, f), time(x))
    names(t) = paste(names(x), n)
    return(t)
}

AbstractMaxCumSum <- function(x, k) {
    Abstract(x, k, function(x){max(cumsum(x))}, "MaxCumSum")
}

generateData <- function(raw, num.features, num.days, i) {
    num.stocks = dim(raw)[[2]]
    window = num.features + num.days
    raw.sub = raw[(i-window):(i-1),]
    data = do.call(rbind, lapply(seq_len(num.stocks), function(x){Lag(raw.sub[,x], 0:num.features)[-(1:num.features),]}))
    colnames(data) = c()
    rownames(data) = c()
    return(data)
}

backTest <- function(cyb, name.df, delt.df, vo.df, friction, num.features, num.days, skip) {
    num.history = dim(delt.df)[[1]]
    num.stocks = dim(delt.df)[[2]]

    window = num.features + num.days

    index = rep(-1, window+skip)
    gain = rep(0, window+skip)

    emp.cor = rep(0, window+skip)
    emp.diff = rep(0, window+skip)
    exp.cor = 0
    exp.diff = 0

    for (i in (window+1+skip):num.history) {
        index[i] = -1
        gain[i] = 0

        train = generateData(delt.df, num.features, num.days, i)
        train[abs(train)>0.12] = NA
        train[train==0] = NA
        train.h2o = as.h2o(train)
        model = h2o.gbm(x=1+seq(num.features), y=1, training_frame=train.h2o)
        train.pred = as.vector(h2o.predict(model, train.h2o))

        #hist(train[,1])
        #readline()
        #hist(train.pred)
        #readline()

        test = generateData(delt.df, num.features, 1, i+1)
        test[abs(test)>0.12] = NA
        test[test==0] = NA
        test.h2o = as.h2o(test)
        test.pred = as.vector(h2o.predict(model, test.h2o))

        for(k in seq(num.stocks)) {
            if (length(which(is.na(test[k,]))) > 0) {
                test[k,1] = NA
                test.pred[k] = NA
            }
        }

        #plot(train.pred, train[,1])
        plot(test.pred, test[,1])
        
        pred = test.pred

        candidates = order(pred, decreasing=T)
        for (j in seq(num.stocks)) {
            candidate = candidates[j]
            pred.max = pred[candidate]

            if (is.na(pred.max) || exp.cor < 0 || (exp.diff) > .01 || pred.max < 0.05) {
                break
            }
            
            if (rownames(delt.df)[i-1] == time(cyb[[candidate]])) {
            } else if (abs(delt.df[i-1,candidate]) > 0.085) {
            } else if (vo.df[i-1,candidate] == 0) {
            } else if (abs(delt.df[i,candidate]) > 0.12) {
            } else {
                index[i] = candidate
                gain[i] = log(1-friction) + max(log(0.92), delt.df[i,candidate])
                break
            }
        }

        emp.cor[i] = cor(test[,1], pred, method='spearman', use='na.or.complete')
        emp.diff[i] = median(pred, na.rm=T) - median(test[,1], na.rm=T)

        stat = data.frame(i, index[i], rownames(delt.df)[i], ifelse(index[i]<1, NA, name.df[index[i]]),
                 length(which(gain!=0)), sum(gain), gain[i], ifelse(index[i]<1, NA, pred[index[i]]),
                 h2o.r2(model), emp.cor[i], emp.diff[i])
        names(stat) = c("Round", "Index", "Date", "Name", "Tran", "Gain", "Act", "Pred", "R2", "Cor", "Diff")
        rownames(stat) = c()
        print(stat, digits=3)
#        if (index[i] != -1 && abs(gain[i])>0.03) {
#            chartAround(cyb[[index[i]]], rownames(delt.df)[i], 10)
#            readline()
#        }
#        plot(ts(cumsum(gain)[-seq_len(window+skip)]))
        exp.cor = (emp.cor[i] + exp.cor) / 2
        exp.diff = (ifelse(emp.diff[i]>0, emp.diff[i], 0) + exp.diff) / 2
        print(exp.cor)
        print(exp.diff)
    }
    return(gain)
}

