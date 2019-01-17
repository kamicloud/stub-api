package definitions;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
    String name = "Request";
}
