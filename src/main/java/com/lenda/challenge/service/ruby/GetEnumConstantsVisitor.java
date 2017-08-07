package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.ConstDeclNode;
import org.jrubyparser.ast.NewlineNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.StrNode;
import org.jrubyparser.ast.SymbolNode;

import java.util.List;

/**
 * Parses a Ruby enum constants.
 */
class GetEnumConstantsVisitor extends BaseVisitor<RubyEnumConstantDef> {

    static List<RubyEnumConstantDef> findEnumConstants(Node rootNode) {
        GetEnumConstantsVisitor visitor = new GetEnumConstantsVisitor();
        rootNode.accept(visitor);
        return visitor.getItems();
    }

    @Override
    public Object visitNewlineNode(NewlineNode newlineNode) {
        try {
            Node childNode = newlineNode.childNodes().get(0);
            if (ConstDeclNode.class.isAssignableFrom(childNode.getClass())
                    && (SymbolNode.class.isAssignableFrom(childNode.childNodes().get(0).getClass())
                    || StrNode.class.isAssignableFrom(childNode.childNodes().get(0).getClass()))) {
                String constant = GetEnumConstantVisitor.getEnumConstant(childNode);
                String value = GetEnumValueVisitor.getEnumValue(childNode);
                visitNodeItems(newlineNode, new RubyEnumConstantDef(constant, value));
            } else {
                visitNodeItems(newlineNode, (RubyEnumConstantDef) null);
            }
        } catch (Exception e) {
            // Skip
        }
        return null;
    }
}
