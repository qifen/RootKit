package com.wei.rootkit.logAnalysis;

/**
 * Created by xu on 2017/1/18.
 *
 * 初级行为图的辅助类，表示图中的一个节点
 *
 */
class Node {
    private int index;
    private int pid, uid;
    private int cid;
    private String func;
    private String param;
    private int matchid;//广播方法匹配？？
    private int app;//？？

    private int parent;
    private int next;
    private int child;

    public int getApp() {
        return app;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getMatchid() {
        return matchid;
    }

    public void setMatchid(int matchid) {
        this.matchid = matchid;
    }

    public String toString(){
        String s = "";
        s += ("pid:" + pid + "\n");
        s += ("cid:" + cid + "\n");
        s += ("method:" + func + "\n");
        s += ("param:" + param + "\n");
        return s;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    //    后面的暂时还不知道干嘛用的
//    int scheduleFunc;
//    int matchid;
//    int finishFunc;
//
//    int app;
//    int visit;
}
