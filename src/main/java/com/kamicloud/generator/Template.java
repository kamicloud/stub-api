package com.kamicloud.generator;

import com.kamicloud.generator.annotations.*;
import com.kamicloud.generator.interfaces.FixedEnumValueInterface;
import com.kamicloud.generator.interfaces.StringEnumValueInterface;
import com.kamicloud.generator.interfaces.TemplateInterface;

import java.util.Date;

public class Template implements TemplateInterface {

    public static class Enums {
        /**
         * 用户状态
         */
        public enum UserStatus implements FixedEnumValueInterface {
            INIT(0),
            DISABLED(2),
            IN_CLASS(4),
            ;

            int value;

            UserStatus(int value) {
                this.value = value;
            }
        }

        public enum PayWay implements StringEnumValueInterface {
            WECHAT("wechat"),
            ALIPAY("alipay"),
            ;

            String value;

            PayWay(String value) {
                this.value = value;
            }
        }

        /**
         * 教师类型
         */
        enum TeacherCatalog {
            PHH,
            NSH,
            XXX,
        }

        /**
         * 上课工具枚举
         */
        enum ToolAllow {
            XXX,
        }

        enum Gender {
            MALE,
            FEMALE,
            TRANNY,
        }
    }


    public class Models {
        /**
         * 用户信息
         * 第二行
         */
        @RESTFul
        public class User extends UserProfile {
            /**
             * 一个注释
             */
            @DBField(name = "id")
            @Mutable
            Integer id;
            /**
             * 这里只是留了一个备注
             */
            @DBField
            String name;
            /**
             * 用户的状态
             */
            @DBField
            String email;
            @DBField
            @Optional
            @Mutable
            Date createdAt;
            @DBField
            @Optional
            @Mutable
            Date updatedAt;

            @DBField
            @Optional
            Models.User child;
        }

        /**
         * 用户的基本信息
         */
        public class UserProfile {
            @DBField
            String name;
            @DBField
            Integer age;
        }

        /**
         * 模拟一个老师的信息
         */
        public class Teacher {
            @DBField(name = "id")
            int teacherId;
            String nickname;
            String pic;
            int[] marks;
            Enums.TeacherCatalog catalog;
            Teacher[] teachers;
            /**
             * 好评率，以1为单位
             */
            float goodCmtRate;
            boolean isMyFave;
            int[] openClass;
            int okClass;
            int classNum;
            Date sortTchTime;
            boolean isRecommended;
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
    }

    public class Controllers {
        /**
         * 后台用户控制器，用来提供和用户相关的后台接口
         * 多行文本
         */
        public class AdminUser {
            class GetUsers {
                @Request
                String[] strings;
                @Request
                int[] ints;
            }
        }

        /**
         * 用户控制器
         */
        public class User {
            @API(methods = {MethodType.POST})
            class GetUsers {
                /**
                 * 查询的ID
                 */
                @Request
                Integer id;

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

            @API(methods = {MethodType.POST})
            class CreateUser {
                /**
                 * 查询的ID
                 */
                @Request
                String email;

                @Response
                Models.User user;
            }
        }

        /**
         * 老师控制器用来提供前台老师接口
         */
        @Middleware(name = "某一个作用范围很广的middleware")
        class Teacher {
            /**
             * 约课搜索老师
             */
            @API(methods = {MethodType.GET})
            @Middleware(name = "某一个小范围的middleware")
            class List {
                /**
                 * 教材分类
                 */
                @Request
                Integer sType;

                @Request
                @Optional
                Enums.TeacherCatalog catalog;

                @Request
                @Optional
                Date date;

                /**
                 * 分页
                 */
                @Request
                @Optional
                int p;

                @Request
                @Optional
                boolean bind;

                @Request
                @Optional
                Enums.Gender gender;

                @Request
                @Optional
                Enums.ToolAllow toolAllow;

                @Request
                @Optional
                int[] marks;

                @Request
                @Optional
                int timeHH;

                @Request
                @Optional
                int timeMM;

                /**
                 * 搜索到的老师，也不知道加什么注释
                 * 反正加一点试试呗
                 */
                @Response
                Models.Teacher[] teachers;
            }

            /**
             * 更新用户的昵称
             */
            @API(methods = {MethodType.GET}, transactional = true, path = "xxxxxxxxx")
            public class UpdateUserName {
                /**
                 * 用户id
                 */
                @Request
                int userId;

                @Request
                String name;

                @Response
                Models.User user;
            }
        }
    }

}
