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
    private int matchid;//广播方法对应的uid或pid
    private int app;//？？

    private Node parent;//父节点
    private Edge next;//node发出的第一条边
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

    public boolean isCheckPermission(){
        if (func != null){
            if (func.equals("android.app.IActivityManager.checkPermission")){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean isNetworkAccess(){
        if (func != null){
            if (func.equals("recvfrom")||func.equals("sendto")){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean isFileAccess(){
        if (func != null){
            if (func.equals("open")||func.equals("read")||func.equals("write")||func.equals("close")){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean isSchedule(){
        if (func != null){
            if (func.equals("scheduleReceiver") || func.equals("scheduleRegisteredReceiver")){
                return true;
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean isFinish(){
        if (func != null){
            if (func .equals("finishReceiver")){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
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

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Edge getNext() {
        return next;
    }

    public void setNext(Edge next) {
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
//    int scheduleFunc;标志位
//    int finishFunc;
//
//    int app;
//    int visit;
}
