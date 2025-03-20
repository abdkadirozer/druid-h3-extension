package com.example.binding;

import com.example.function.H3FunctionExprMacro;
import com.example.function.H3Functions;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import org.apache.druid.math.expr.ExprMacroTable;

public class DruidH3Bindings {
    public static void registerFunctions(Binder binder) {
        Multibinder<ExprMacroTable.ExprMacro> exprMacroTableBinder =
                Multibinder.newSetBinder(binder, ExprMacroTable.ExprMacro.class);

        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3IndexFunction()));
        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3ToGeoFunction()));
        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3KRingFunction()));
        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3DistanceFunction()));
        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3IsValidFunction()));
        exprMacroTableBinder.addBinding().toInstance(new H3FunctionExprMacro(new H3Functions.H3ToBoundaryFunction()));
    }
}