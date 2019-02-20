package templates;

import definitions.annotations.ErrorInterface;

@SuppressWarnings("unused")
public enum Errors implements ErrorInterface {
    /**
     * 服务器内部错误
     */
    SERVER_INTERNAL_ERROR(-1),
    /**
     * 参数不合法
     */
    INVALID_PARAMETER(-2),

    CUSTOM_ERROR_MESSAGE(-3),

    // comment 2
    OBJECT_NOT_FOUND(10002),
    ;
//
    int value;

    Errors(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
