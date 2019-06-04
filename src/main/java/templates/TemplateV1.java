package templates;

import definitions.annotations.*;
import definitions.types.*;

import java.util.Date;

/**
 * skdjflsd
 */
@SuppressWarnings("unused")
class TemplateV1 {


    public static class Enums {

        /**
         * 用户状态
         */
        enum UserStatus implements FixedEnumValueInterface {
            INIT(0),
            DISABLED(2),
            IN_CLASS(4),
            ;
            int value;

            UserStatus(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
        }

        @Memo({
            "支付方式",
            "这个注释是通过memo的方式添加的"
        })
        @StringEnum
        @AsBO
        enum PayWay {
            @Memo("微信支付")
            WECHAT,
            @Memo
            ALIPAY,
        }

        /**
         * 性别
         *
         * 这个注释是通过注释方式加的
         */
        enum Gender {
            /**
             * 没有填写
             */
            UNKNOWN,
            // 这样的注释
            MALE,
            /** inline doc */
            FEMALE,
        }
    }


    public class Models {
        /**
         * 用户信息
         * 第二行
         */
        @RESTFul
        class User extends UserProfile {
            /**
             * 一个注释
             */
            @DBField("id")
            @Mutable
            Integer id;
            /**
             * 这里只是留了一个备注
             */
            @DBField
            String name;
            String avatar;
        }

        /**
         * 用户的基本信息
         */
        class UserProfile {
            @DBField
            String name;
            // @DBField
            // Integer age;
        }

        /**
         * 一个分享场景的抽象
         */
        public class SharePayload {
            /**
             * 分享的标题
             */
            String title;
            /**
             * 分享的描述
             */
            String description;
            /**
             * 一个缩略图
             */
            String icon;
            /**
             * 点进去的链接
             */
            String url;
        }

        class Article {
            @Optional
            @Mutable
            Integer id;
            String title;
            @Optional
            String content;
            Models.User user;
            @Optional
            Integer commentsCount;
            /**
             * 需要时用于标记是否收藏
             */
            @Optional
            Boolean favorite;
            /**
             * 是否是添加火热标记
             */
            @Optional
            Boolean hot;
            Date createdAt;
        }

        class ArticleComment {
            Integer id;
            Models.User user;
            String content;
            Date createdAt;
        }
    }

    class Controllers {
        class Article {
            /**
             * 获取文章列表
             */
            class GetArticles {
                @Response
                Models.Article[] articles;
            }

            /**
             * 取得一篇文章
             */
            class GetArticle {
                @Request
                Integer id;
                @Response
                Models.Article article;
            }

            /**
             * 添加文章
             */
            @Transactional
            class CreateArticle {
                @Request
                String title;
                @Request
                String content;

                @Response
                Models.Article article;
            }

            /**
             * 获取文章评论
             */
            class GetArticleComments {
                @Request
                Integer articleId;
                @Request
                Integer page;

                @Response
                Models.ArticleComment[] comments;
            }
        }

        /**
         * 用户控制器
         */
        public class User {
            @Methods({MethodType.POST, MethodType.DELETE})
            @Middleware("admin")
            class GetUsers {
                /**
                 * 查询的ID
                 */
                @Request
                Integer id;

                @Request
                Enums.Gender gender;

                @Request
                @Optional
                Integer page;

                @Request
                @Optional
                Models.User testUser;

                @Request
                @Optional
                Models.User[] testUsers;

                @Response
                String val;

                @Response
                Models.User user;
            }

            @Methods({MethodType.POST})
            class CreateUser {
                /**
                 * 查询的ID
                 */
                @Request
                String email;
                @Request
                String[] emails;
                @Request
                Enums.Gender gender;
                @Request
                Enums.Gender[] genders;
                @Request
                Integer id;
                @Request
                Integer[] ids;

                @Request
                DateYm ym;
                @Request
                DateYm[] ymarray;
                @Request
                DateYm[][][][] ymarrayn;

                @Request
                File file;

                @Response
                Models.User user;
                @Response
                Models.User[] users;
            }
        }
    }

}
