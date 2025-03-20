package com.example.function;

import org.apache.druid.math.expr.*;
import org.apache.druid.math.expr.vector.ExprVectorProcessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class H3FunctionExprMacro implements ExprMacroTable.ExprMacro {
    private final H3Functions.AbstractH3Function function;

    public H3FunctionExprMacro(H3Functions.AbstractH3Function function) {
        this.function = function;
    }

    @Override
    public String name() {
        return function.getName();
    }

    @Override
    public Expr apply(List<Expr> args) {
        return new H3Expr(name(), args, function);
    }

    private static class H3Expr implements Expr {
        private final String name;
        private final List<Expr> args;
        private final H3Functions.AbstractH3Function function;

        private H3Expr(String name, List<Expr> args, H3Functions.AbstractH3Function function) {
            this.name = name;
            this.args = args;
            this.function = function;
        }

        @Override
        public boolean isLiteral() {
            return Expr.super.isLiteral();
        }

        @Override
        public boolean isNullLiteral() {
            return Expr.super.isNullLiteral();
        }

        @Override
        public boolean isIdentifier() {
            return Expr.super.isIdentifier();
        }

        @Nullable
        @Override
        public Object getLiteralValue() {
            return Expr.super.getLiteralValue();
        }


        @Nullable
        @Override
        public String getIdentifierIfIdentifier() {
            return Expr.super.getIdentifierIfIdentifier();
        }

        @Nullable
        @Override
        public String getBindingIfIdentifier() {
            return Expr.super.getBindingIfIdentifier();
        }

        @Nullable
        @Override
        public ExprEval eval(ObjectBinding bindings) {
            return function.apply(args.stream().map(arg -> arg.eval(bindings)).collect(Collectors.toList()));
        }

        @Override
        public String stringify() {
            return "";
        }

        @Override
        public Expr visit(Shuttle shuttle) {
            return null;
        }

        @Override
        public BindingAnalysis analyzeInputs() {
            return null;
        }

        @Nullable
        @Override
        public ExpressionType getOutputType(InputBindingInspector inspector) {
            return Expr.super.getOutputType(inspector);
        }

        @Override
        public boolean canVectorize(InputBindingInspector inspector) {
            return Expr.super.canVectorize(inspector);
        }

        @Override
        public <T> ExprVectorProcessor<T> buildVectorized(VectorInputBindingInspector inspector) {
            return Expr.super.buildVectorized(inspector);
        }

        @Override
        public byte[] getCacheKey() {
            return Expr.super.getCacheKey();
        }

        @Override
        public String toString() {
            return name + "(" + args.stream().map(Expr::toString).collect(Collectors.joining(", ")) + ")";
        }
    }
}