package com.wei.rootkit.logAnalysis;

/**
 * Created by xu on 2017/2/19.
 */
public class Edge {
    private Node to;
    private Edge next;

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public Edge getNext() {
        return next;
    }

    public void setNext(Edge next) {
        this.next = next;
    }
}
