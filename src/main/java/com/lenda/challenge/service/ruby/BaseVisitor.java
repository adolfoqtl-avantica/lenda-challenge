package com.lenda.challenge.service.ruby;

import com.google.common.collect.Lists;
import org.jrubyparser.ast.Node;
import org.jrubyparser.util.NoopVisitor;

import java.util.List;

abstract class BaseVisitor<T> extends NoopVisitor {

    private List<T> items;

    BaseVisitor() {
        items = Lists.newArrayList();
    }

    @SafeVarargs
    final void visitNodeItems(Node node, T... items) {
        if (items != null && items.length > 0) {
            for (T item : items) {
                if (item != null) {
                    this.items.add(item);
                }
            }
        }
        for (Node child : node.childNodes()) {
            child.accept(this);
        }
    }

    T getFirstItem(T undefined) {
        return items.size() > 0 ? items.get(0) : undefined;
    }

    List<T> getItems() {
        return items;
    }
}
