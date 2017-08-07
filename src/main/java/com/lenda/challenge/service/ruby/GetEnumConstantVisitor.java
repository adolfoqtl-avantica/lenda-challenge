package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.ConstDeclNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.SymbolNode;

/**
 * Parses ane enum constant name from a Ruby module.
 */
class GetEnumConstantVisitor extends BaseVisitor<String> {

    static String getEnumConstant(Node node) {
        GetEnumConstantVisitor visitor = new GetEnumConstantVisitor();
        node.accept(visitor);
        return visitor.getFirstItem("Undefined");
    }

    @Override
    public Object visitConstDeclNode(ConstDeclNode constDeclNode) {
        visitNodeItems(constDeclNode, constDeclNode.getName());
        return null;
    }
}
