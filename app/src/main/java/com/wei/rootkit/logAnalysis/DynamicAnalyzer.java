package com.wei.rootkit.logAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    /**
     * 实现图生成算法，将原始日志信息转换成为初级行为图
     * 生成初级行为图：树形节点序列，节点内部保存节点之间的关系
     */
    private void getPrimaryGraph(String logPath) throws FileNotFoundException {
        List<String> logList = getLog(logPath);
        List<Node> rawNodes = new ArrayList<>();
        rawNodes.add(new Node());//root
        for (int logIndex = 1; logIndex < logList.size() ; logIndex++){
            Node n = logParser(logList.get(logIndex));
            n.setIndex(logIndex);
            rawNodes.add(n);
        }
        Map<Integer,Map<Integer,Node>> map = new HashMap<>();
        String cloneFunc = "clone";
        for (int nodeIndex = 1 ; nodeIndex < rawNodes.size() ; nodeIndex ++){//遍历每一行日志
            Node node = rawNodes.get(nodeIndex);
            if (map.containsKey(node.getUid())){//应用程序节点存在
                Map<Integer,Node> m = map.get(node.getUid());
                if (m.containsKey(node.getPid())){//进程节点存在
                    if (node.getFunc().equals(cloneFunc)){//clone
                        int cid = Integer.parseInt(node.getParam());
                        node.setCid(cid);
                        Node parent = m.get(node.getPid());
                        node.setParent(parent.getIndex());
                        parent.setChild(parent.getChild()+1);
                        m.put(cid,node);
                    }else {
                        Node parent = m.get(node.getPid());
                        node.setParent(parent.getIndex());
                        m.remove(node.getPid());//将当前的树叶节点移除
                        m.put(node.getPid(),node);
                        //将最新的动作（当前node）做为最新的树叶节点
                        //也就是说，后续的动作跟在当前node后面
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
        String regexPattern = "\\[Rootkit\\]\\((\\d+)\\)\\((\\d+)\\)(\\w+)\\((.*)\\)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(log);
        if (matcher.find()){
            int uid = Integer.parseInt(matcher.group(1));
            int pid = Integer.parseInt(matcher.group(2));
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
//    }

    public Node[] getGraph(String logPath) throws FileNotFoundException {
        getPrimaryGraph(logPath);
        matchBroadCastLifeCycle();
        simplifyGraph();
        Node[] nodes = new Node[graph.size()];
        return graph.toArray(nodes);
    }


    public DynamicAnalyzer(){
        graph = new ArrayList<>();
    }

}
