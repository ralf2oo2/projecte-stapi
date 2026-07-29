package net.ralf2oo2.projecte.api.config;

public class PrefixConfiguration extends Configuration{
    private final Configuration inner;
    private final String prefix;

    public PrefixConfiguration(Configuration inner, String prefix) {
        super();
        if (prefix.endsWith(".")) {
            throw new IllegalArgumentException("Prefix is not allowed to end with a dot.");
        }
        this.inner = inner;
        this.prefix = prefix;
    }

    private String getPrefixedCategory(String category) {
        if (category == null || category.isEmpty()) {
            return this.prefix;
        }
        return this.prefix + "." + category;
    }

    @Override
    public boolean getBoolean(String key, String category, boolean defaultValue, String comment) {
        return inner.getBoolean(key, getPrefixedCategory(category), defaultValue, comment);
    }

    @Override
    public int getInt(String key, String category, int defaultValue, String comment) {
        return inner.getInt(key, getPrefixedCategory(category), defaultValue, comment);
    }

    @Override
    public void save() {
        inner.save();
    }
}
