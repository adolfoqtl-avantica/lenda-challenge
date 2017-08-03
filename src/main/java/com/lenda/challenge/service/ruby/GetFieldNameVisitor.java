package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.SymbolNode;

/**
 * Parses a field name from a Ruby class.
 */
class GetFieldNameVisitor extends BaseVisitor<String> {

    static String getFieldName(Node node) {
        GetFieldNameVisitor visitor = new GetFieldNameVisitor();
        node.accept(visitor);
        return visitor.getFirstItem("Undefined");
    }

    @Override
    public Object visitSymbolNode(SymbolNode symbolNode) {
        visitNodeItems(symbolNode, symbolNode.getName());
        return null;
    }
}
