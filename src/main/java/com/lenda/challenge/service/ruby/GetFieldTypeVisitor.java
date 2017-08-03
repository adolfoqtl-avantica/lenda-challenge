package com.lenda.challenge.service.ruby;

import org.apache.commons.lang3.text.WordUtils;
import org.jrubyparser.ast.ConstNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.SymbolNode;

/**
 * Parses a field type from a Ruby class.
 */
class GetFieldTypeVisitor extends BaseVisitor<String> {

    static String getFieldType(Node node) {
        GetFieldTypeVisitor visitor = new GetFieldTypeVisitor();
        node.accept(visitor);
        String fieldType = visitor.getFirstItem(null);
        if (fieldType == null) {
            String fieldName = GetFieldNameVisitor.getFieldName(node);
            fieldType = WordUtils.capitalizeFully(fieldName.endsWith("s") ? fieldName.substring(0, fieldName.length() - 1) : fieldName, '_').replaceAll("_", "");
        }
        return fieldType;
    }

    @Override
    public Object visitSymbolNode(SymbolNode symbolNode) {
        visitNodeItems(symbolNode, symbolNode.getName().equals("type") ? ConstNode.class.cast(symbolNode.getParent().childNodes().get(1)).getName() : null);
        return null;
    }
}
