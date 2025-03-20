package com.example.module;

import com.example.binding.DruidH3Bindings;
import com.example.function.H3Functions;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import org.apache.druid.initialization.DruidModule;

import java.util.List;

public class H3Module implements DruidModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule("H3Module")
                        .registerSubtypes(
                                new NamedType(H3Functions.H3IndexFunction.class, H3Functions.H3IndexFunction.NAME),
                                new NamedType(H3Functions.H3ToGeoFunction.class, H3Functions.H3ToGeoFunction.NAME),
                                new NamedType(H3Functions.H3KRingFunction.class, H3Functions.H3KRingFunction.NAME),
                                new NamedType(H3Functions.H3DistanceFunction.class, H3Functions.H3DistanceFunction.NAME),
                                new NamedType(H3Functions.H3IsValidFunction.class, H3Functions.H3IsValidFunction.NAME),
                                new NamedType(H3Functions.H3ToBoundaryFunction.class, H3Functions.H3ToBoundaryFunction.NAME)
                        )
        );
    }

    @Override
    public void configure(Binder binder) {
        DruidH3Bindings.registerFunctions(binder);
    }
}