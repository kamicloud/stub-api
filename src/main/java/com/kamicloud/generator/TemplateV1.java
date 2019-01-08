package com.kamicloud.generator;

import com.kamicloud.generator.annotations.*;
import com.kamicloud.generator.interfaces.FixedEnumValueInterface;
import com.kamicloud.generator.interfaces.TemplateInterface;

import java.lang.annotation.Documented;
import java.util.Date;

/**
 * skdjflsd
 */
@SuppressWarnings("unused")
class TemplateV1 implements TemplateInterface {


    public static class Enums {
        /**
         * 教师请假原因
         * 54646
         */
//        enum TeacherLeaveReason {
//            /**
//             * 事件
//             */
//            EVENT,
//            /**
//             * 休息
//             */
//            RELAX,
//            /**
//             * 活动
//             */
//            ACTIVITY
//        }


        @Memo(memo = {""}, ignores = {Endpoint.ANDROID})
        enum TeacherLeaveReason {
            @Memo(memo = "事件")
            EVENT,
            @Memo(memo = "休息")
            RELAX,
            @Memo(memo = "活动")
            ACTIVITY
        }

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

        @StringEnum
        enum PayWay {
            WECHAT,
            ALIPAY,
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

        @StringEnum
        enum Gender implements FixedEnumValueInterface {
            UNKNOWN(2),
            MALE(4),
            FEMALE(6);

            int value;

            Gender(int value) {
                this.value = value;
            }

            @Override
            public int getValue() {
                return value;
            }
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
            @DBField
            @Optional
            Models.User[] children;
        }

        /**
         * 用户的基本信息
         */
        class UserProfile {
            @DBField
            String name;
            @DBField
            Integer age;
        }

        /**
         * 模拟一个老师的信息
         */
        class Teacher {
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

        class TeacherLeaveRecord {
            @Mutable
            Integer id;
            String tname;
            Enums.TeacherLeaveReason reason;
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

    class Controllers {
        /**
         * 后台用户控制器，用来提供和用户相关的后台接口
         * 多行文本
         */
        public class AdminUser {
            @API(methods = {MethodType.POST})
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
            @API(methods = {MethodType.POST, MethodType.DELETE})
            @Middleware(name = "某一个小范围的middleware")
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

            @API(methods = {MethodType.POST})
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

                @Response
                Models.User user;
                @Response
                Models.User[] users;
            }
        }

        /**
         * 老师控制器用来提供前台老师接口
         */
        @Middleware(name = "某一个作用范围很广的middleware")
        class Teacher {
            @API(methods = {MethodType.POST})
            class AddTeacherLeave {
                @Request
                String name;
                @Request
                @Optional
                String[] names;
                @Request
                Enums.TeacherLeaveReason reason;

                @Response
                Models.TeacherLeaveRecord record;
                @Response
                @Optional
                Models.TeacherLeaveRecord records;
            }

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
