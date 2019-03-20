package com.lqm.qa.client.impl;

import com.lqm.qa.client.LabelClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.REMOTE_ERROR,"熔断器开启。。");
    }
}
