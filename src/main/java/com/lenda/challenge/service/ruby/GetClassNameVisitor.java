package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.ClassNode;
import org.jrubyparser.ast.Colon2ImplicitNode;
import org.jrubyparser.ast.Node;

/**
 * Parses a class name from a Ruby class.
 */
class GetClassNameVisitor extends BaseVisitor<String> {

    static String getClassName(Node node) {
        GetClassNameVisitor visitor = new GetClassNameVisitor();
        node.accept(visitor);
        return visitor.getFirstItem("Undefined");
    }

    @Override
    public Object visitClassNode(ClassNode classNode) {
        visitNodeItems(classNode, Colon2ImplicitNode.class.cast(classNode.childNodes().get(0)).getName());
        return null;
    }
}
