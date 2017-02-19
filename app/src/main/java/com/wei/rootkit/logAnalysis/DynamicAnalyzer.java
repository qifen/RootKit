package com.wei.rootkit.logAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xu on 2017/1/18.
 *
 *  动态分析器
 *
 */
class DynamicAnalyzer {
    private List<Node> graph;
    private List<Edge> edgs;
    private Map<Integer,Map<Integer,Node>> map;//<uid,<pid,node>> 只存储树叶，用于继续向后生长
    private List<String> config;
    private Map<String,Integer> packageListName2ID;
    private Map<Integer,String> packageListID2Name;
    /**
     * 实现图生成算法，将原始日志信息转换成为初级行为图
     * 生成初级行为图：树形节点序列，节点内部保存节点之间的关系
     */
    private void drawPrimaryGraph(String logPath) throws FileNotFoundException {
        List<String> logList = getLog(logPath);
        graph.add(new Node());//root
        for (int logIndex = 1; logIndex < logList.size() ; logIndex++){//解析所有的日志为node list
            Node n = logParser(logList.get(logIndex));
            n.setIndex(logIndex);
            graph.add(n);
        }
        String cloneFunc = "clone";
        for (int nodeIndex = 1 ; nodeIndex < graph.size() ; nodeIndex ++){//遍历每一行日志
            Node node = graph.get(nodeIndex);
            if (map.containsKey(node.getUid())){//应用程序节点存在
                Map<Integer,Node> m = map.get(node.getUid());
                if (m.containsKey(node.getPid())){//进程节点存在
                    if (node.getFunc().equals(cloneFunc)){//clone方法,参数只有cid
                        int cid = Integer.parseInt(node.getParam());
                        build(m.get(node.getPid()),node,node.getUid(),node.getPid(),cid);
                    }else {//不是clone方法
                        build(m.get(node.getPid()),node,node.getUid(),node.getPid(),0);
                    }
                }else {//某应用的第一个进程
                    m.put(node.getPid(),node);
                }
            }else {//第一个应用程序节点
                Map<Integer,Node> m = new HashMap<>();
                m.put(node.getPid(),node);
                map.put(node.getUid(),m);
                //下面这段代码是直接翻译来的，还没搞懂为什么
//                if (node.getUid() != 1000){
//                    String func = packageListID2Name.get(node.getUid());
//                    if (func != null){
//                        Node n = new Node();
//                        n.setFunc(func);
//                        n.setParam("");
//                        n.setApp(1);
//                        build(graph.get(0),n,0,0,0);
//                    }
//                    build(node,map.get(node.getUid()).get(node.getPid()),0,0,0);
//                }else {
//                    build(graph.get(0),node,0,0,0);
//                }
            }
        }
    }

    private void build(Node parent ,Node child , int uid , int pid , int cid){
        Edge e = new Edge();
        e.setTo(child);
        e.setNext(parent.getNext());
        parent.setNext(e);
        parent.setChild(parent.getChild()+1);
        child.setParent(parent);
        //将最新的动作（当前node）做为最新的树叶节点
        //也就是说，后续的动作跟在当前node后面，总是保存最新的动作
        if (uid != 0 && cid == 0){
            map.get(uid).remove(pid);
            map.get(uid).put(pid,child);
        }
        if (cid != 0){
            child.setCid(cid);
            map.get(uid).put(cid,child);
        }
        edgs.add(e);
    }

    /**
     * 实现广播生命周期匹配算法
     * 生成语义更加丰富的行为图
     *
     * 监控scheduleRegisteredReceiver -> finishReceiver (动态注册的广播生命周期)
     *     scheduleReceiver -> finishReceiver (静态注册的广播生命周期)
     */
    private void matchBroadCastLifeCycle(){
        String dynamicScheduleFunc = "scheduleRegisteredReceiver";
        String staticScheduleFunc = "scheduleReceiver";
        String finishFunc = "finishReceiver";
        List<Integer> queue = new LinkedList<>();//schdule方法对应节点在graph中的位置
        int isMatch = 0;
        Node cnode;                  //当前节点
        Node pnode;                  //当前节点的父节点
        int last = 0;                   //上一个广播周期结束节点
        for (int i = 1 ; i < graph.size() ; i ++){//遍历所有node，注意 0 是root，不是实际node
            Node node = graph.get(i);
            if (node.getFunc().equals(dynamicScheduleFunc) || node.getFunc().equals(staticScheduleFunc)) {
                //是schedule方法
                String[] params = getParams(node.getParam());
                if (node.getFunc().equals(dynamicScheduleFunc)){//获取pid
                    int id = Integer.parseInt(params[params.length-1]);
                    node.setMatchid(id);
                }else {//staticScheduleFunc，获取uid
                    Integer id = packageListName2ID.get(params[params.length-1]);
                    if (id != null){//成功匹配
                        node.setMatchid(id);
                    }else {//未匹配到
                        node.setMatchid(0);
                    }
                }

                queue.add(i);
            }else {
                if (node.getFunc().equals(finishFunc)){//是finish方法
                    Node scheduleNode = null;
                    int scheduleIndex = 0;
                    for (Integer index : queue){//遍历队列中的所有schedule方法
                        Node n = graph.get(index);
                        scheduleNode = n;
                        scheduleIndex = index;
                        if (n.getFunc().equals(staticScheduleFunc)){
                            if (node.getUid() == n.getMatchid()){
                                //finish方法的uid与scheduleReceiver方法相匹配
                                isMatch = 1;
                                break;
                            }
                        }else {
                            if (node.getPid() == n.getMatchid()){
                                //finish方法的pid与scheduleRegisterReceiver方法相匹配
                                isMatch = 1;
                                break;
                            }
                        }
                    }
                    if (isMatch == 1){
                        cnode = pnode = node;
                        do {
                            cnode = pnode;
                            pnode = pnode.getParent();
                        }while (pnode.getPid() == node.getPid() && pnode.getApp() != 1 && pnode.getIndex() > last && pnode.getIndex() > scheduleIndex);
                        //上一行：此时，pnode是在寻找schedule的开始节点，i是finish节点，last是上一个schedule的结束节点
                        //todo node.app 还没搞定
                        build(scheduleNode,cnode,0,0,0);
                        build(scheduleNode,node,0,0,0);
                        last = i;
                        queue.remove(scheduleNode);
                        isMatch = 0;
//                        break;    //我觉得应该是continue,不可能只匹配一次啊
                        continue;
                    }
                }
            }
        }
    }

    //将用逗号连接的参数分离开
    private String[] getParams(String params){
        String[] p = params.split(",");
        for (int i = 0 ; i < p.length ; i ++){
            p[i] = p[i].trim();
        }
        return p;
    }



    /**
     * 使用图精简算法消除图中的冗余信息
     */
    private void  simplifyGraph(){

    }

    /**
     * 将日志读入内存
     * @param logPath
     * @return 一条一条的日志信息
     */
    private List<String> getLog(String logPath) throws FileNotFoundException {
        List<String> logList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(logPath));
        String log;
        try {
            while ((log = reader.readLine()) != null){
                logList.add(log);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("log 文件未找到");
            e.printStackTrace();
        }
        return logList;
    }

    /**
     * 解析日志信息，生成孤立的node
     * @param log 一条日志信息
     * @return 图节点
     */
    private Node logParser(String log){
        String regexPattern = "\\[Rootkit\\]\\((\\d+)\\)\\((\\d+)\\)([^\\n]*)\\((.*)\\)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(log);
        if (matcher.find()){
            int pid = Integer.parseInt(matcher.group(1));
            int uid = Integer.parseInt(matcher.group(2));
            String method = matcher.group(3);
            String param = matcher.group(4);
            Node n = new Node();
            n.setUid(uid);
            n.setPid(pid);
            n.setFunc(method);
            n.setParam(param);
            return n;
        }
        return null;
    }

    public static void main(String[] args) {
        DynamicAnalyzer analyzer = new DynamicAnalyzer();
        try {
            Node[] nodes = analyzer.getGraph("F:/sample.log","F:/config.ini","F:/packages.list");
            System.out.println(nodes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取抽象行为图，以父子树表示
     * @param logPath : log文件路径
     * @param configPath : config.ini文件路径
     * @param packageListPath : packages.list文件路径
     * @return 抽象行为图
     * @throws FileNotFoundException
     */
    public Node[] getGraph(String logPath,String configPath,String packageListPath) throws FileNotFoundException {
        getConfig(configPath);
        getPackageList(packageListPath);
        drawPrimaryGraph(logPath);
        matchBroadCastLifeCycle();
        simplifyGraph();
        Node[] nodes = new Node[graph.size()];
        return graph.toArray(nodes);
    }


    public DynamicAnalyzer(){
        map = new HashMap<>();
        graph = new ArrayList<>();
        edgs = new ArrayList<>();
        config = new ArrayList<>();
        packageListID2Name = new HashMap<>();
        packageListName2ID = new HashMap<>();
    }

    private void getConfig(String path){//读取config.ini内容放进list中,这个文件还可以优化一下
        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = reader.readLine()) != null){
                config.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("config.ini文件未找到");
            e.printStackTrace();
        }
    }

    private void getPackageList(String path){//读取packages.list，内容放进map中
        File file = new File(path);
        String regexPattern = "([0-9A-Za-z._]*) ([0-9]*)";
        Pattern pattern = Pattern.compile(regexPattern);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()){
                    String packageName = matcher.group(1);
                    int id = Integer.parseInt(matcher.group(2));
                    packageListName2ID.put(packageName,id);
                    packageListID2Name.put(id,packageName);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("packages.list 未找到");
            e.printStackTrace();
        }
    }

}
