package com.lenda.challenge.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.base.Joiner;

import static com.lenda.challenge.util.JavaWriter.Symbols.*;

/**
 * Generates Java source code.
 */
public class JavaWriter implements Appendable {

    private static final int INDENT_SPACES = 4;

    private static final String EXTENDS = " extends ";

    private static final String IMPLEMENTS = " implements ";

    private static final String IMPORT = "import ";

    private static final String IMPORT_STATIC = "import static ";

    private static final String PACKAGE = "package ";

    private static final String PRIVATE = "private ";

    private static final String PRIVATE_STATIC_FINAL = "private static final ";

    private static final String PROTECTED = "protected ";

    private static final String PUBLIC = "public ";

    private static final String PUBLIC_CLASS = "public class ";

    private static final String PUBLIC_ENUM = "public enum ";

    private static final String PUBLIC_FINAL = "public final ";

    private static final String PUBLIC_INTERFACE = "public interface ";

    private static final String PUBLIC_STATIC = "public static ";

    private static final String PUBLIC_STATIC_FINAL = "public static final ";

    private final Appendable appendable;

    private final Set<Class<?>> importedClasses = new HashSet<Class<?>>();

    private final Set<Package> importedPackages = new HashSet<Package>();

    private String indent = "";

    private String type;

    public JavaWriter(Appendable appendable){
        if (appendable == null){
            throw new IllegalArgumentException("appendable is null");
        }
        this.appendable = appendable;
        this.importedPackages.add(Object.class.getPackage());
    }

    public JavaWriter annotation(Annotation annotation) throws IOException {
        append(indent).append("@").appendType(annotation.annotationType()).append("(");
        boolean first = true;
        for (Method method : annotation.annotationType().getDeclaredMethods()){
            try {
                Object value = method.invoke(annotation);
                if (value == null){
                    continue;
                }else if (!first){
                    append(COMMA);
                }
                append(method.getName()+"=");
                annotationConstant(value);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
            first = false;
        }
        return append(")").nl();
    }

    public JavaWriter annotation(Class<? extends Annotation> annotation) throws IOException{
        return append(indent).append("@").appendType(annotation).nl();
    }
    
    public JavaWriter annotation(Class<? extends Annotation> annotation, String datasource) throws IOException{    	
        return append(indent).append("@").appendType(annotation).append("(\"").append(datasource).append("\")").nl();
    }    

    @SuppressWarnings("unchecked")
    private void annotationConstant(Object value) throws IOException{
        if (value instanceof Class){
            appendType((Class)value).append(".class");
        }else if (value instanceof Number){
            append(value.toString());
        }else if (value instanceof Enum){
            Enum enumValue = (Enum)value;
            append(enumValue.getDeclaringClass().getName()+DOT+enumValue.name());
        }else if (value instanceof String){
            append(QUOTE + escapeJava(value.toString()) + QUOTE);
        }else{
            throw new IllegalArgumentException("Unsupported annotation value : " + value);
        }
    }

    @Override
    public JavaWriter append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    @Override
    public JavaWriter append(CharSequence csq) throws IOException {
        appendable.append(csq);
        return this;
    }

    @Override
    public JavaWriter append(CharSequence csq, int start, int end) throws IOException {
        appendable.append(csq, start, end);
        return this;
    }

    private JavaWriter appendType(Class<?> type) throws IOException{
        if (importedClasses.contains(type) || importedPackages.contains(type.getPackage())){
            append(type.getSimpleName());
        }else{
            append(type.getName());
        }
        return this;
    }

    public JavaWriter beginClass(String simpleName) throws IOException{
        return beginClass(simpleName, null);
    }

    public JavaWriter beginClass(String simpleName, String superClass, String... interfaces) throws IOException{
        append(indent + PUBLIC_CLASS + simpleName);
        if (superClass != null){
            append(EXTENDS + superClass);
        }
        if (interfaces.length > 0){
            append(IMPLEMENTS);//.join(COMMA, interfaces);
            append(Joiner.on(COMMA).join(interfaces));
        }
        append(" {").nl().nl();
        goIn();

        type = simpleName;
        if (type.contains("<")){
            type = type.substring(0, type.indexOf('<'));
        }
        return this;
    }

    public JavaWriter beginEnum(String simpleName, String... interfaces) throws IOException{
        append(indent + PUBLIC_ENUM + simpleName);
        if (interfaces.length > 0){
            append(IMPLEMENTS);//.join(COMMA, interfaces);
            append(Joiner.on(COMMA).join(interfaces));
        }
        append(" {").nl().nl();
        goIn();
        return this;
    }

    public <T> JavaWriter beginConstructor(Collection<T> parameters, Function<T, String> transformer) throws IOException {
        append(indent + PUBLIC + type).params(parameters, transformer).append(" {").nl();
        return goIn();
    }

    public JavaWriter beginConstructor(String... parameters) throws IOException{
        append(indent + PUBLIC + type).params(parameters).append(" {").nl();
        return goIn();
    }

    public JavaWriter beginInterface(String simpleName, String... interfaces) throws IOException {
        append(indent + PUBLIC_INTERFACE + simpleName);
        if (interfaces.length > 0){
            append(EXTENDS);
            append(Joiner.on(COMMA).join(interfaces));
        }
        append(" {").nl().nl();
        goIn();

        type = simpleName;
        if (type.contains("<")){
            type = type.substring(0, type.indexOf('<'));
        }
        return this;

    }

    public JavaWriter beginLine(String... segments) throws IOException {
        append(indent);
        for (String segment : segments){
            append(segment);
        }
        return this;
    }

    private JavaWriter beginMethod(String modifiers, String returnType, String methodName, String... args) throws IOException{
        append(indent + modifiers + returnType + SPACE + methodName).params(args).append(" {").nl();
        return goIn();
    }

    public <T> JavaWriter beginPublicMethod(String returnType, String methodName, Collection<T> parameters, Function<T, String> transformer) throws IOException {
        return beginMethod(PUBLIC, returnType, methodName, transform(parameters, transformer));
    }

    public JavaWriter beginPublicMethod(String returnType, String methodName, String... args) throws IOException{
        return beginMethod(PUBLIC, returnType, methodName, args);
    }

    public <T> JavaWriter beginStaticMethod(String returnType, String methodName, Collection<T> parameters, Function<T, String> transformer) throws IOException {
        return beginMethod(PUBLIC_STATIC, returnType, methodName, transform(parameters, transformer));
    }

    public JavaWriter beginStaticMethod(String returnType, String methodName, String... args) throws IOException{
        return beginMethod(PUBLIC_STATIC, returnType, methodName, args);
    }

    public JavaWriter end() throws IOException{
        goOut();
        return line("}").nl();
    }

    public JavaWriter field(String type, String name) throws IOException {
        return stmt(type + SPACE + name).nl();
    }

    private JavaWriter goIn(){
        indent += "    ";
        return this;
    }

    private JavaWriter goOut(){
        if (indent.length() >= INDENT_SPACES){
            indent = indent.substring(0, indent.length() - INDENT_SPACES);
        }
        return this;
    }

    public JavaWriter imports(String... imports) throws IOException{
        for (String importStr : imports){
            line(IMPORT + importStr.replaceAll("\\$","\\.") + SEMICOLON);
        }
        nl();
        return this;
    }

    public JavaWriter imports(Class<?>... imports) throws IOException{
        for (Class<?> cl : imports){
            importedClasses.add(cl);
            line(IMPORT + cl.getName().replaceAll("\\$","\\.") + SEMICOLON);
        }
        nl();
        return this;
    }

    public JavaWriter imports(Package... imports) throws IOException {
        for (Package p : imports){
            importedPackages.add(p);
            line(IMPORT + p.getName() + ".*;");
        }
        nl();
        return this;
    }

    public JavaWriter javadoc(String... lines) throws IOException {
        line("/**");
        for (String line : lines){
            line(" * " + line);
        }
        return line(" */");
    }

    public JavaWriter line(String... segments) throws IOException{
        append(indent);
        for (String segment : segments){
            append(segment);
        }
        return nl();
    }

    public JavaWriter nl() throws IOException {
        return append(NEWLINE);
    }

    public JavaWriter packageDecl(String packageName) throws IOException{
        importedPackages.add(Package.getPackage(packageName));
        return line(PACKAGE + packageName + SEMICOLON).nl();
    }

    private <T> JavaWriter params(Collection<T> parameters, Function<T, String> transformer) throws IOException{
        append("(");
        boolean first = true;
        for (T param : parameters){
            if (!first){
                append(COMMA);
            }
            append(transformer.apply(param));
            first = false;
        }
        append(")");
        return this;
    }

    private JavaWriter params(String... params) throws IOException{
        append("(");
        append(Joiner.on(COMMA).join(params));
        append(")");
        return this;
    }

    public JavaWriter privateField(String type, String name) throws IOException {
        return field(PRIVATE, type, name);
    }

    public JavaWriter privateFinal(String type, String name) throws IOException {
        return field(PRIVATE, type, name);
    }

    public JavaWriter privateFinal(String type, String name, String value) throws IOException {
        return field(PRIVATE, type, name, value);
    }

    public JavaWriter privateStaticFinal(String type, String name, String value) throws IOException {
        return field(PRIVATE_STATIC_FINAL, type, name, value);
    }

    public JavaWriter protectedField(String type, String name) throws IOException {
        return field(PROTECTED, type, name);
    }

    public JavaWriter protectedFinal(String type, String name) throws IOException {
        return field(PROTECTED, type, name);
    }

    public JavaWriter protectedFinal(String type, String name, String value) throws IOException {
        return field(PROTECTED, type, name, value);
    }

    public JavaWriter publicField(String type, String name) throws IOException {
        return field(PUBLIC, type, name);
    }

    public JavaWriter publicFinal(String type, String name) throws IOException {
        return field(PUBLIC_FINAL, type, name);
    }

    public JavaWriter publicFinal(String type, String name, String value) throws IOException {
        return field(PUBLIC_FINAL, type, name, value);
    }

    public JavaWriter publicStaticFinal(String type, String name, String value) throws IOException {
        return field(PUBLIC_STATIC_FINAL, type, name, value);
    }

    private JavaWriter field(String modifier, String type, String name) throws IOException{
        return stmt(modifier + type + SPACE + name).nl();
    }

    private JavaWriter field(String modifier, String type, String name, String value) throws IOException {
        return stmt(modifier + type + SPACE + name + ASSIGN + value).nl();
    }

    public JavaWriter staticimports(Class<?>... imports) throws IOException {
        for (Class<?> cl : imports){
            line(IMPORT_STATIC + cl.getName() + ".*;");
        }
        return this;
    }

    private JavaWriter stmt(String stmt) throws IOException {
        return line(stmt + SEMICOLON);
    }

    public JavaWriter suppressWarnings(String type) throws IOException {
        return line("@SuppressWarnings(\"" + type +"\")");
    }

    private <T> String[] transform(Collection<T> parameters, Function<T, String> transformer) {
        String[] rv = new String[parameters.size()];
        int i = 0;
        for (T value : parameters){
            rv[i++] = transformer.apply(value);
        }
        return rv;
    }

    private String escapeJava(String str) {
        return escapeJavaStyleString(str, false);
    }

    private String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter ioe = new StringWriter(str.length() * 2);
                escapeJavaStyleString(ioe, str, escapeSingleQuotes);
                return ioe.toString();
            } catch (IOException var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    private void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str != null) {
            int sz = str.length();

            for(int i = 0; i < sz; ++i) {
                char ch = str.charAt(i);
                if(ch > 4095) {
                    out.write("\\u" + hex(ch));
                } else if(ch > 255) {
                    out.write("\\u0" + hex(ch));
                } else if(ch > 127) {
                    out.write("\\u00" + hex(ch));
                } else if(ch < 32) {
                    switch(ch) {
                        case '\b':
                            out.write(92);
                            out.write(98);
                            break;
                        case '\t':
                            out.write(92);
                            out.write(116);
                            break;
                        case '\n':
                            out.write(92);
                            out.write(110);
                            break;
                        case '\u000b':
                        default:
                            if(ch > 15) {
                                out.write("\\u00" + hex(ch));
                            } else {
                                out.write("\\u000" + hex(ch));
                            }
                            break;
                        case '\f':
                            out.write(92);
                            out.write(102);
                            break;
                        case '\r':
                            out.write(92);
                            out.write(114);
                    }
                } else {
                    switch(ch) {
                        case '\"':
                            out.write(92);
                            out.write(34);
                            break;
                        case '\'':
                            if(escapeSingleQuote) {
                                out.write(92);
                            }

                            out.write(39);
                            break;
                        case '/':
                            out.write(92);
                            out.write(47);
                            break;
                        case '\\':
                            out.write(92);
                            out.write(92);
                            break;
                        default:
                            out.write(ch);
                    }
                }
            }

        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public static class Symbols {

        public static final String ASSIGN = " = ";

        public static final String COMMA = ", ";

        public static final String DOT = ".";

        public static final String DOT_CLASS = ".class";

        public static final String EMPTY = "";

        public static final String NEW = "new ";

        public static final String NEWLINE = "\n";

        public static final String QUOTE = "\"";

        public static final String RETURN = "return ";

        public static final String SEMICOLON = ";";

        public static final String SERIAL = "serial";

        public static final String SPACE = " ";

        public static final String STAR = "*";

        public static final String SUPER = "super";

        public static final String THIS = "this";

        public static final String UNCHECKED = "unchecked";

        private Symbols(){}
    }
}
