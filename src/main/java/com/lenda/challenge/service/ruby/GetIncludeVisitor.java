package com.lenda.challenge.service.ruby;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.jrubyparser.ast.Colon2Node;
import org.jrubyparser.ast.ConstNode;
import org.jrubyparser.ast.Node;

import java.util.List;

/**
 * Parses an include from a Ruby class.
 */
class GetIncludeVisitor extends BaseVisitor<String> {

    private List<String> constants;

    private GetIncludeVisitor() {
        constants = Lists.newArrayList();
    }

    static String getImportModule(Node node) {
        GetIncludeVisitor visitor = new GetIncludeVisitor();
        node.accept(visitor);
        return visitor.getFirstItem(null) != null ? (visitor.constants.size() > 0 ? visitor.getFirstItem(null) + "::" + Joiner.on("::").join(Lists.reverse(visitor.constants)) : visitor.getFirstItem(null)) : null;
    }

    @Override
    public Object visitConstNode(ConstNode constNode) {
        visitNodeItems(constNode, constNode.getName());
        return null;
    }

    @Override
    public Object visitColon2Node(Colon2Node colon2Node) {
        if (colon2Node.getName() != null) {
            constants.add(colon2Node.getName());
        }
        visitNodeItems(colon2Node, (String) null);
        return null;
    }
}
