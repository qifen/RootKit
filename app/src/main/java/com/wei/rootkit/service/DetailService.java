package com.wei.rootkit.service;

/**
 * Created by sharon on 2017/3/7.
 */

public class DetailService {
    private static DetailService detailService = new DetailService();

    public static DetailService getInstance() {
        return detailService;
    }

    private DetailService() {
    }


}
