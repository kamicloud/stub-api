package com.zhh.generator;

import com.zhh.generator.annotations.*;
import com.zhh.generator.interfaces.FixedEnumValueInterface;

import java.util.Date;

public class Global {

    public static class Enums {
        /**
         * 用户状态
         */
        public enum UserStatus implements FixedEnumValueInterface {
            INIT(0),
            DISABLED(2),
            ;

            int value;

            UserStatus(int value) {
                this.value = value;
            }
        }

        /**
         * 教师类型
         */
        enum TeacherCatalog {
            PHH,
            NSH,
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
         */
        public class User extends UserProfile {
            class NO {

            }
            /**
             * 一个注释
             */
            @API(methods = MethodType.GET, path = "xxxx")
            @DBField(name = "id")
            Integer userId;
            /**
             * 这里只是留了一个备注
             */
            @DBField
            String comment;
            /**
             * 用户的状态
             */
            @DBField
            Global.Enums.UserStatus status;
            @DBField
            Date createdAt;
            @DBField
            @Optional
            Date updatedAt;

            @DBField
            @Optional
            Global.Models.User child;
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
        public class sharePayload {
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
}
