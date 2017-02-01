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
import java.util.Queue;
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
    private Map<Integer,Map<Integer,Node>> map;
    private List<String> config;
    private Map<String,Integer> packageListName2ID;
    private Map<Integer,String> packageListID2Name;
    /**
     * 实现图生成算法，将原始日志信息转换成为初级行为图
     * 生成初级行为图：树形节点序列，节点内部保存节点之间的关系
     */
    private void getPrimaryGraph(String logPath) throws FileNotFoundException {
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
                    if (node.getFunc().equals(cloneFunc)){//clone方法
                        int cid = Integer.parseInt(node.getParam());
                        node.setCid(cid);
                        Node parent = m.get(node.getPid());
                        node.setParent(parent.getIndex());
                        parent.setChild(parent.getChild()+1);//孩子的个数
                        m.put(cid,node);//将clone的进程也加入到这个程序的map中
                    }else {//不是clone方法
                        Node parent = m.get(node.getPid());
                        node.setParent(parent.getIndex());
                        m.remove(node.getPid());//将当前的树叶节点移除
                        m.put(node.getPid(),node);
                        //将最新的动作（当前node）做为最新的树叶节点
                        //也就是说，后续的动作跟在当前node后面，总是保存最新的动作
                    }
                }else {//某应用的第一个进程
                    m.put(node.getPid(),node);
                }
            }else {//第一个应用程序节点
                Map<Integer,Node> m = new HashMap<>();
                m.put(node.getPid(),node);
                map.put(node.getUid(),m);
            }
        }
    }

    /**
     * 实现广播生命周期匹配算法
     * 生成语义更加丰富的行为图
     */
    private void matchBroadCastLifeCycle(){
        String dynamicScheduleFunc = "scheduleRegisteredReceiver";
        String staticScheduleFunc = "scheduleReceiver";
        String finishFunc = "finishReceiver";
        Queue<Node> queue = new LinkedList<>();
        int isMatch = 0;
        int cnode = 0;
        int pnode = 0;
        int last = 0;
        for (int i = 1 ; i < graph.size() ; i ++){//遍历所有node，注意 0 是root，不是实际node
            Node node = graph.get(i);
            if (node.getFunc().equals(dynamicScheduleFunc) || node.getFunc().equals(staticScheduleFunc)) {
                //是schedule方法,解析出与之匹配的package ID
                String[] params = getParams(node.getParam());
                if (node.getFunc().equals(dynamicScheduleFunc)){
                    int id = Integer.parseInt(params[params.length-1]);
                    node.setMatchid(id);
                }else {//staticScheduleFunc
                    Integer id = packageListName2ID.get(params[params.length-1]);
                    if (id != null){//成功匹配
                        node.setMatchid(id);
                    }else {//未匹配到
                        node.setMatchid(0);
                    }
                }

                queue.add(node);
            }else {
                if (node.getFunc().equals(finishFunc)){//是finish方法
                    int scheduleIndex = 0;
                    Node scheduleNode = null;
                    for (Node n : queue){//遍历队列中的所有schedule方法
                        scheduleNode = n;
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
                        scheduleIndex ++;
                    }
                    if (isMatch == 1){
                        cnode = pnode = i;
                        last = i;
                        do {
                            cnode = pnode;
                            pnode = graph.get(pnode).getParent();
                        }while (graph.get(pnode).getPid() == graph.get(i).getPid() && graph.get(pnode).getApp() != 1 && pnode > last && pnode > scheduleIndex);
                        //todo node.app 还没搞定
                        build(scheduleIndex,cnode,0,0,0);
                        build(scheduleIndex,i,0,0,0);
//                        last = i;
                        queue.remove(scheduleNode);
                        isMatch = 0;
                        break;
//                        continue; //我觉得应该是continue
                    }
                }
            }
        }
    }

    //将用逗号连接的参数分离开
    private String[] getParams(String params){
        String[] p = params.split(",");
        for (int i = 0 ; i < p.length ; i ++){
            p[i].trim();
        }
        return p;
    }

    private void build(int parent , int child , int uid , int pid , int cid){
        graph.get(parent).setChild(graph.get(parent).getChild() + 1);
        graph.get(child).setParent(parent);
        if (uid != 0 && cid == 0){
            map.get(uid).put(pid,graph.get(child));
        }
        if (cid != 0){
            map.get(uid).put(cid,graph.get(child));
        }
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

//    public static void main(String[] args) {
//        String log = "[Rootkit](954)(10074)finishReceiver(abortBroadcast = 0)";
//        DynamicAnalyzer analyzer = new DynamicAnalyzer();
//        Node node = analyzer.logParser(log);
//        System.out.println(node);
//        try {
//            Node[] nodes = analyzer.getGraph("F:/sample.log");
//            System.out.println(nodes);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

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
        getPrimaryGraph(logPath);
        matchBroadCastLifeCycle();
        simplifyGraph();
        Node[] nodes = new Node[graph.size()];
        return graph.toArray(nodes);
    }


    public DynamicAnalyzer(){
        map = new HashMap<>();
        graph = new ArrayList<>();
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
        String regexPattern = "(.*) ([0-9]*)";
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
