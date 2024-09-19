package com.gvs.avisacitas.autoSend;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityNode {
        AccessibilityNodeInfo nodeInfo;
        AccessibilityNode parent;
        List<AccessibilityNode> children;

        public AccessibilityNode(AccessibilityNodeInfo nodeInfo) {
            this.nodeInfo = nodeInfo;
            this.children = new ArrayList<>();
        }

        public void addChild(AccessibilityNode child) {
            this.children.add(child);
            child.parent = this;
        }

        public AccessibilityNodeInfo getNodeInfo() {
            return nodeInfo;
        }

        public List<AccessibilityNode> getChildren() {
            return children;
        }

        public AccessibilityNode getParent() {
            return parent;
        }

}
