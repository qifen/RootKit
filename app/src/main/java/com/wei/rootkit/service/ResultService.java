package com.wei.rootkit.service;

/**
 * Created by sharon on 2017/3/7.
 */

public class ResultService {
    private static ResultService resultService = new ResultService();

    public static ResultService getInstance() {
        return resultService;
    }

    private ResultService() {
    }

    public void generateLog(String packageName){

    }
}
