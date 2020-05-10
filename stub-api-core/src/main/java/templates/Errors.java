package templates;

import definitions.annotations.ErrorInterface;

@SuppressWarnings("unused")
public enum Errors implements ErrorInterface {
    /** 服务器内部错误，等同于500 */
    SERVER_INTERNAL_ERROR(-1),
    /** 参数不合法 */
    INVALID_PARAMETER(-2),
    /** 接口已失效，应校验是否是该版本接口已禁用，若已禁用客户端应触发升级提示 */
    API_DEPRECATED(-10),
    /** 等同于404 */
    API_NOT_FOUND(-11),
    /** 维护模式 */
    MAINTAIN_MODE(-20),
    /** 用户未登录 */
    AUTH_FAILED(-100),
    /** 没有权限 */
    NO_PERMISSION(-200),
    /** 自定义的ERROR信息 */
    CUSTOM_ERROR_MESSAGE(-10000),

    // comment 2
    OBJECT_NOT_FOUND(10002),
    ;
    /**
     * 后边为控制部分，请勿编辑。
     */
    int value;

    Errors(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
