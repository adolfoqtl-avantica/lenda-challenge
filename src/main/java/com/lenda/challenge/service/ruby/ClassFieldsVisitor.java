package com.lenda.challenge.service.ruby;

import org.jrubyparser.ast.FCallNode;
import org.jrubyparser.ast.Node;

import java.util.List;

/**
 * Parses a Ruby class fields.
 */
class ClassFieldsVisitor extends BaseVisitor<RubyModelFieldDef> {

    static List<RubyModelFieldDef> findClassFields(Node rootNode) {
        ClassFieldsVisitor visitor = new ClassFieldsVisitor();
        rootNode.accept(visitor);
        return visitor.getItems();
    }

    @Override
    public Object visitFCallNode(FCallNode fCallNode) {
        // (RootNode, (NewlineNode, (ClassNode, (Colon2ImplicitNode:Account), (BlockNode, (NewlineNode, (FCallNode:include, (ArrayNode, (Colon2ConstNode:Document, (ConstNode:Mongoid))))), (NewlineNode, (FCallNode:include, (ArrayNode, (Colon2ConstNode:Short, (Colon2ConstNode:Timestamps, (ConstNode:Mongoid)))))), (NewlineNode, (FCallNode:devise, (ArrayNode, (SymbolNode:database_authenticatable), (SymbolNode:registerable), (SymbolNode:recoverable), (SymbolNode:lockable), (SymbolNode:trackable), (SymbolNode:validatable), (SymbolNode:timeoutable)))),
        // (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:encrypted_password), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String), (SymbolNode:default), (StrNode)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:reset_password_token), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:reset_password_sent_at), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Time)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:sign_in_count), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Integer), (SymbolNode:default), (FixnumNode)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:current_sign_in_at), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Time)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:last_sign_in_at), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Time)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:current_sign_in_ip), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:last_sign_in_ip), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:failed_attempts), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Integer), (SymbolNode:default), (FixnumNode)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:unlock_token), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:locked_at), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Time)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:admin), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Boolean)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:devteam), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:Boolean)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:mixpanel_distinct_id), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:ga_id), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String)))))), (NewlineNode, (FCallNode:embeds_one, (ArrayNode, (SymbolNode:persistent_split_data), (HashNode, (ArrayNode, (SymbolNode:autobuild), (TrueNode:true)))))), (NewlineNode, (FCallNode:embeds_one, (ArrayNode, (SymbolNode:persistent_suggestion_engine_data), (HashNode, (ArrayNode, (SymbolNode:autobuild), (TrueNode:true)))))), (NewlineNode, (FCallNode:has_many, (ArrayNode, (SymbolNode:applications), (HashNode, (ArrayNode, (SymbolNode:autosave), (TrueNode:true), (SymbolNode:order), (HashNode, (ArrayNode, (SymbolNode:id), (SymbolNode:asc)))))))), (NewlineNode, (FCallNode:has_many, (ArrayNode, (SymbolNode:referrals), (HashNode, (ArrayNode, (SymbolNode:class_name), (StrNode)))))), (NewlineNode, (FCallNode:has_many, (ArrayNode, (SymbolNode:tickets)))), (NewlineNode, (FCallNode:after_update, (ArrayNode, (SymbolNode:send_slack_notification_if_locked_out)))), (NewlineNode, (DefnNode:active_application, (MethodNameNode:active_application), (ArgsNode), (NewlineNode, (CallNode:last, (CallNode:select, (VCallNode:applications), (BlockPassNode, (SymbolNode:open?))), (ListNode))))), (NewlineNode, (DefnNode:create_application, (MethodNameNode:create_application), (ArgsNode, (ArrayNode, (ArgumentNode:application_id))), (BlockNode, (NewlineNode, (LocalAsgnNode:quote, (CallNode:find, (ConstNode:Quote), (ArrayNode, (LocalVarNode:application_id))))), (NewlineNode, (AttrAssignNode:email_address=, (LocalVarNode:quote), (ListNode, (CallNode:email, (SelfNode:self), (ListNode))))), (NewlineNode, (LocalAsgnNode:application, (CallNode:becomes, (LocalVarNode:quote), (ArrayNode, (ConstNode:Application))))), (NewlineNode, (CallNode:<<, (CallNode:applications, (SelfNode:self), (ListNode)), (ArrayNode, (LocalVarNode:application)))), (NewlineNode, (LocalVarNode:application))))), (NewlineNode, (DefnNode:find_latest_application_or_create_new, (MethodNameNode:find_latest_application_or_create_new), (ArgsNode, (ArrayNode, (ArgumentNode:application_id))), (BlockNode, (NewlineNode, (IfNode, (FCallNode:has_open_application?, (ListNode)), (ReturnNode, (VCallNode:active_application)))), (NewlineNode, (IfNode, (CallNode:present?, (VCallNode:applications), (ListNode)), (ReturnNode, (CallNode:last, (CallNode:select, (VCallNode:applications), (BlockPassNode, (SymbolNode:closed?))), (ListNode))))), (NewlineNode, (FCallNode:create_application, (ArrayNode, (LocalVarNode:application_id))))))), (NewlineNode, (DefnNode:timeout_in, (MethodNameNode:timeout_in), (ArgsNode), (NewlineNode, (IfNode, (CallNode:admin?, (SelfNode:self), (ListNode)), (CallNode:hours, (FixnumNode), (ListNode)), (CallNode:minutes, (FixnumNode), (ListNode)))))), (NewlineNode, (DefnNode:has_open_application?, (MethodNameNode:has_open_application?), (ArgsNode), (NewlineNode, (CallNode:any?, (VCallNode:applications), (BlockPassNode, (SymbolNode:open?)))))), (NewlineNode, (DefnNode:name, (MethodNameNode:name), (ArgsNode), (BlockNode, (NewlineNode, (IfNode, (CallNode:empty?, (VCallNode:applications), (ListNode)), (ReturnNode, (NilNode:nil)))), (NewlineNode, (LocalAsgnNode:borrower, (CallNode:detect, (CallNode:map, (CallNode:reverse, (CallNode:sort_by, (VCallNode:applications), (BlockPassNode, (SymbolNode:id))), (ListNode)), (BlockPassNode, (SymbolNode:borrower))), (ListNode), (IterNode, (ArgsNode, (ArrayNode, (ArgumentNode:b))), (NewlineNode, (CallNode:present?, (CallNode:name, (DVarNode:b), (ListNode)), (ListNode))))))), (NewlineNode, (IfNode, (CallNode:blank?, (LocalVarNode:borrower), (ListNode)), (NilNode:nil), (CallNode:name, (LocalVarNode:borrower), (ListNode))))))), (NewlineNode, (DefnNode:nested_errors, (MethodNameNode:nested_errors), (ArgsNode), (NewlineNode, (CallNode:to_a, (CallNode:new, (ConstNode:NestedErrorsPresenter), (ArrayNode, (SelfNode:self))), (ListNode))))), (NewlineNode, (VCallNode:private)), (NewlineNode, (DefnNode:password_required?, (MethodNameNode:password_required?), (ArgsNode), (NewlineNode, (OrNode, (OrNode, (CallNode:!, (FCallNode:persisted?, (ListNode)), (ListNode)), (CallNode:!, (CallNode:nil?, (VCallNode:password), (ListNode)), (ListNode))), (CallNode:!, (CallNode:nil?, (VCallNode:password_confirmation), (ListNode)), (ListNode)))))), (NewlineNode, (DefnNode:send_slack_notification_if_locked_out, (MethodNameNode:send_slack_notification_if_locked_out), (ArgsNode), (NewlineNode, (CallNode:notify_locked_out_account, (Colon2ConstNode:SupportNotification, (ConstNode:Slack)), (ArrayNode, (SelfNode:self))))))))))

        // (NewlineNode, (FCallNode:field, (ArrayNode, (SymbolNode:email), (HashNode, (ArrayNode, (SymbolNode:type), (ConstNode:String), (SymbolNode:default), (StrNode)))))),
        try {
            if (fCallNode.getName() != null && RubyModelFieldDef.VALID_FIELD_DEFS.contains(fCallNode.getName())) {
                Node nodeDef = fCallNode.childNodes().get(0);
                String fieldName = GetFieldNameVisitor.getFieldName(nodeDef);
                String fieldType = GetFieldTypeVisitor.getFieldType(nodeDef);
                Boolean isRef = fCallNode.getName().equals("has_one") || fCallNode.getName().equals("has_many");
                visitNodeItems(fCallNode, new RubyModelFieldDef(fieldName, fieldType, isRef));
            } else if (fCallNode.getName() != null && fCallNode.getName().equals("include")) {
                Node nodeDef = fCallNode.childNodes().get(0);
                String moduleImport = GetIncludeVisitor.getImportModule(nodeDef);
                if (moduleImport != null && moduleImport.equals("Mongoid::Document")) {
                    visitNodeItems(fCallNode, new RubyModelFieldDef("created_by", "String", false), new RubyModelFieldDef("modified_by", "String", false));
                } else if (moduleImport != null && moduleImport.equals("Mongoid::Timestamps::Short")) {
                    visitNodeItems(fCallNode, new RubyModelFieldDef("c_at", "Time", false), new RubyModelFieldDef("u_at", "Time", false));
                } else {
                    visitNodeItems(fCallNode, (RubyModelFieldDef) null);
                }
            } else {
                visitNodeItems(fCallNode, (RubyModelFieldDef) null);
            }
        } catch (Exception e) {
            System.out.println(fCallNode.toString());
        }
        return null;
    }
}
