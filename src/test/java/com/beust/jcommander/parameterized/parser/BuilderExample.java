package com.beust.jcommander.parameterized.parser;

import com.beust.jcommander.Parameter;

/**
 * Because this class extends a class which uses a recursive generic, the compiler will create a bridge method in this
 * class for setBaseProperty that we should ignore when searching for parameterized methods.
 */
public final class BuilderExample extends BaseBuilder<BuilderExample> {
    public String directProperty;

    @Parameter(names = "--direct-property")
    public BuilderExample setDirectProperty(String directProperty) {
        this.directProperty = directProperty;
        return this;
    }
}

class BaseBuilder<T extends BaseBuilder<T>> {
    public String baseProperty;

    @Parameter(names = "--base-property")
    public T setBaseProperty(String baseProperty) {
        this.baseProperty = baseProperty;
        return (T) this;
    }
}
