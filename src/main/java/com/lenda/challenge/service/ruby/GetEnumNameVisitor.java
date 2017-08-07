package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.Colon2ImplicitNode;
import org.jrubyparser.ast.ModuleNode;
import org.jrubyparser.ast.Node;

/**
 * Parses an enum name from a Ruby module.
 */
class GetEnumNameVisitor extends BaseVisitor<String> {

    static String getEnumName(Node node) {
        GetEnumNameVisitor visitor = new GetEnumNameVisitor();
        node.accept(visitor);
        return visitor.getFirstItem("Undefined");
    }

    @Override
    public Object visitModuleNode(ModuleNode moduleNode) {
        visitNodeItems(moduleNode, Colon2ImplicitNode.class.cast(moduleNode.childNodes().get(0)).getName());
        return null;
    }
}
