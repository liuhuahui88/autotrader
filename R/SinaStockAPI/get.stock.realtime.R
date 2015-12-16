get.stock.realtime <- function(stock.codes){

    detect.exchange <- function(stock.code){
        if(nchar(stock.code) != 6){
            stop("The stock code entered is invalid. The code should be like \"601898\" or \"000034\"")
        }
        if(is.na(as.integer(stock.code)) == TRUE){
            stop("The stock code entered is invalid. The code should be like \"601898\" or \"000034\"")
        }
        if((substr(stock.code, 1, 1) %in% c("0", "1", "3", "5", "6")) == FALSE){
            stop("The stock code entered is invalid. The code should be like \"601898\" or \"000034\"")
        }
        exchange <- switch(substr(stock.code, 1, 1),
                           "0" = "sz",
                           "1" = "sz",
                           "3" = "sz",
                           "5" = "sh",
                           "6" = "sh"
                           )
        return(exchange);
    }

    exchanges <- lapply(stock.codes, detect.exchange);

    query <- paste("http://hq.sinajs.cn/list=",
                   paste(exchanges, stock.codes, sep="", collapse=","),
                   sep="")

    raw_content <- scan(query,
                        what = "raw", encoding = "UTF-8", quiet = TRUE)

    contents <- raw_content[seq(0, length(raw_content), 2)]

    extract.result <- function(content) {
        elements <- strsplit(content, split = ",")[[1]]
        elements <- elements[-c(1, 7,8)]  # remove duplicated elements
        elements <- (elements[-length(elements)])  # remove the last element which is meaningless
        return(elements)
    }

    results <- lapply(contents, extract.result)
    results <- do.call(rbind, results)
    results <- cbind(stock.codes, results)
    results <- data.frame(results)
    names(results) <-c("code", "open", "prev.close", "current", "high", "low",
                       "volume", "amount",
                       "bid.volume.1", "bid.price.1","bid.volume.2", "bid.price.2",
                       "bid.volume.3", "bid.price.3","bid.volume.4", "bid.price.4","bid.volume.5", "bid.price.5",
                       "ask.volume.1", "ask.price.1","ask.volume.2", "ask.price.2",
                       "ask.volume.3", "ask.price.3","ask.volume.4", "ask.price.4","ask.volume.5", "ask.price.5",
                       "date", "time")

#    result$code <- stock.code

    return(results)
}
