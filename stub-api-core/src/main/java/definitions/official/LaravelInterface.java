package definitions.official;

public interface LaravelInterface {
    /**
     * 检验数据的规则
     *
     * 如 date_format:Y-m-d
     * @return String
     */
    String getLaravelRule();

    /**
     * 初始化的参数
     *
     * 如 Y-m-d
     * @return String
     */
    String getLaravelParam();
}
