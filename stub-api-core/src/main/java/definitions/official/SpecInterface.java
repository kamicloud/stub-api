package definitions.official;

public interface SpecInterface {
    /**
     * 数据最基本的类型，决定初始化的规则等
     */
    TypeSpec getSpec();

    /**
     * 客户端查看文档的描述
     */
    String getComment();

}
