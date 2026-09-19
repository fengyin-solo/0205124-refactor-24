package com.redtourism.interaction;

/**
 * 可被互动的内容类型。
 * 新增一种可互动内容时，只需在此登记一次，并在各 {@link InteractionType}
 * 的 supportedTargets 中声明它支持哪些互动即可。
 */
public enum TargetType {
    SPOT("景点"),
    ROUTE("线路"),
    CULTURE("红色文化"),
    HOTEL("酒店");

    private final String label;

    TargetType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 解析请求传入的类型字符串，非法类型返回 null。 */
    public static TargetType of(String code) {
        if (code == null) {
            return null;
        }
        try {
            return TargetType.valueOf(code.trim().toUpperCase());
        } catch (e) {
            return null;
        }
    }
}
