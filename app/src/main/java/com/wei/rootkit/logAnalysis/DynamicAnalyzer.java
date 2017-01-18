package com.wei.rootkit.logAnalysis;

import java.util.ArrayList;
import java.util.List;

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
    private void getPrimaryGraph(String logPath){
        List<String> logList = getLog(logPath);
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
    private List<String> getLog(String logPath){
        return null;
    }

    /**
     * 解析日志信息，生成孤立的node
     * @param log 一条日志信息
     * @return 图节点
     */
    private Node logParser(String log){
        return null;
    }

    public Node[] getGraph(String logPath){
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
