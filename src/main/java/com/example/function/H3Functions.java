package com.example.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import org.apache.druid.math.expr.ExprEval;
import org.apache.druid.math.expr.ExpressionType;
import org.apache.druid.segment.ColumnInspector;
import org.apache.druid.segment.column.ColumnType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class H3Functions {
    private static H3Core h3;

    static {
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException("H3Core could not be initialized", e);
        }
    }

    public static class H3IndexFunction extends AbstractH3Function {
        public static final String NAME = "h3_geo_to_h3";

        @JsonCreator
        public H3IndexFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 3, "Function[%s] requires 3 arguments: latitude, longitude, resolution", getName());

            double lat = args.get(0).asDouble();
            double lon = args.get(1).asDouble();
            int resolution = args.get(2).asInt();

            long h3Index = h3.latLngToCell(lat, lon, resolution);
            return ExprEval.of(Long.toString(h3Index));
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.STRING;
        }
    }


    public static class H3ToGeoFunction extends AbstractH3Function {
        public static final String NAME = "h3_h3_to_geo";

        @JsonCreator
        public H3ToGeoFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 1, "Function[%s] requires 1 argument: h3Index", getName());

            String h3IndexStr = args.get(0).asString();
            long h3Index;
            try {
                h3Index = Long.parseLong(h3IndexStr);
            } catch (NumberFormatException e) {
                return ExprEval.of(null);
            }

            LatLng geoCoord = h3.cellToLatLng(h3Index);
            return ExprEval.of(String.format("{\"lat\": %f, \"lon\": %f}", geoCoord.lat, geoCoord.lng));
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.STRING;
        }
    }

    public static class H3KRingFunction extends AbstractH3Function {
        public static final String NAME = "h3_k_ring";

        @JsonCreator
        public H3KRingFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 2, "Function[%s] requires 2 arguments: h3Index, k", getName());

            String h3IndexStr = args.get(0).asString();
            long h3Index;
            try {
                h3Index = Long.parseLong(h3IndexStr);
            } catch (NumberFormatException e) {
                return ExprEval.of(null);
            }

            int k = args.get(1).asInt();

            List<Long> kRing = h3.gridRingUnsafe(h3Index, k);
            List<String> kRingStr = new ArrayList<>();
            for (Long index : kRing) {
                kRingStr.add(Long.toString(index));
            }

            return ExprEval.of(String.join(",", kRingStr));
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.STRING;
        }
    }

    public static class H3DistanceFunction extends AbstractH3Function {
        public static final String NAME = "h3_distance";

        @JsonCreator
        public H3DistanceFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 2, "Function[%s] requires 2 arguments: h3Index1, h3Index2", getName());

            String h3IndexStr1 = args.get(0).asString();
            String h3IndexStr2 = args.get(1).asString();

            long h3Index1, h3Index2;
            try {
                h3Index1 = Long.parseLong(h3IndexStr1);
                h3Index2 = Long.parseLong(h3IndexStr2);
            } catch (NumberFormatException e) {
                return ExprEval.of(null);
            }

            long distance = h3.gridDistance(h3Index1, h3Index2);
            return ExprEval.of(distance);
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.LONG;
        }
    }


    public static class H3IsValidFunction extends AbstractH3Function {
        public static final String NAME = "h3_is_valid";

        @JsonCreator
        public H3IsValidFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 1, "Function[%s] requires 1 argument: h3Index", getName());

            String h3IndexStr = args.get(0).asString();
            long h3Index;
            try {
                h3Index = Long.parseLong(h3IndexStr);
                boolean isValid = h3.isValidCell(h3Index);
                return ExprEval.of(isValid ? 1 : 0);
            } catch (NumberFormatException e) {
                return ExprEval.of(0);
            }
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.LONG;
        }
    }


    public static class H3ToBoundaryFunction extends AbstractH3Function {
        public static final String NAME = "h3_h3_to_boundary";

        @JsonCreator
        public H3ToBoundaryFunction() {
            super(NAME);
        }

        @Override
        public ExprEval apply(List<ExprEval> args) {
            Preconditions.checkArgument(args.size() == 1, "Function[%s] requires 1 argument: h3Index", getName());

            String h3IndexStr = args.get(0).asString();
            long h3Index;
            try {
                h3Index = Long.parseLong(h3IndexStr);
            } catch (NumberFormatException e) {
                return ExprEval.of(null);
            }

            List<LatLng> boundary = h3.cellToBoundary(h3Index);
            List<String> boundaryStr = new ArrayList<>();

            for (LatLng coord : boundary) {
                boundaryStr.add(String.format("[%f,%f]", coord.lat, coord.lng));
            }

            return ExprEval.of(String.format("{\"type\":\"Polygon\",\"coordinates\":[%s]}", String.join(",", boundaryStr)));
        }

        @Override
        public Set<String> getRequiredFields() {
            return ImmutableSet.of();
        }

        @Override
        public ColumnType getOutputType(ColumnInspector inspector) {
            return ColumnType.STRING;
        }
    }

    public abstract static class AbstractH3Function {
        private final String name;

        public AbstractH3Function(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract ExprEval apply(List<ExprEval> args);

        public abstract Set<String> getRequiredFields();

        public abstract ColumnType getOutputType(ColumnInspector inspector);

        public ExpressionType getOutputType() {
            return ExpressionType.STRING;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AbstractH3Function that = (AbstractH3Function) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}