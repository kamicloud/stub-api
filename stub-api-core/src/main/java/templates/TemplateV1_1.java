package templates;

import definitions.annotations.*;
import definitions.types.*;

import java.util.Date;

/**
 * 接口模板
 */
@SuppressWarnings("all")
class TemplateV1_1 {


    public static class Enums {
        /**
         * Pet status in the store
         */
        enum PetStatus {
            AVAILABLE,
            PENDING,
            SOLD,
        }

        enum OrderStatus {
            PLACED,
            APPROVED,
            DELIVERED
        }
    }


    class Models {
        class Order {
            int id;
            int petId;
            int quantity;
            Date shipDate;
            Enums.OrderStatus status;
            boolean complete;
        }

        class Category {
            int id;
            String name;
        }

        class User {
            int id;
            String username;
            String firstName;
            String lastName;
            String email;
            String password;
            String phone;
            /**
             * UserStatus
             */
            int userStatus;
        }

        class Tag {
            int id;
            String name;
        }

        class Pet {
            int id;
            Models.Category category;
            String name = "doggie";
            String[] photoUrls;
            Models.Tag[] tags;
            Enums.PetStatus status;
        }
    }

    class AdminControllers {

    }

    static class Controllers {
        class Pet {
            /**
             * Add a new pet to the store
             */
            @Methods(MethodType.POST)
            class AddPet {

            }
        }
    }

}
