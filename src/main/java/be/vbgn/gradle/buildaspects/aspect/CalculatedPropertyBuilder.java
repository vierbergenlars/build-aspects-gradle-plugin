package be.vbgn.gradle.buildaspects.aspect;

import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.function.Function;

public class CalculatedPropertyBuilder<T> {

    private final String name;
    private final Function<? super Variant, ? extends T> calculator;

    CalculatedPropertyBuilder(String name, Function<? super Variant, ? extends T> calculator) {
        this.name = name;
        this.calculator = calculator;
    }

    public Property<T> build(Variant variant) {
        return new PropertyImpl<>(name, calculator.apply(variant));
    }

}
