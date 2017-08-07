package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.SymbolNode;

/**
 * Parses a field type from a Ruby class.
 */
class GetEnumValueVisitor extends BaseVisitor<String> {

    static String getEnumValue(Node node) {
        GetEnumValueVisitor visitor = new GetEnumValueVisitor();
        node.accept(visitor);
        return visitor.getFirstItem("Undefined");
    }

    @Override
    public Object visitSymbolNode(SymbolNode symbolNode) {
        visitNodeItems(symbolNode, symbolNode.getName());
        return null;
    }

    @Override
    public Object visitStrNode(StrNode strNode) {
        visitNodeItems(strNode, strNode.getValue());
        return null;
    }
}
