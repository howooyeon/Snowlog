package goormthonuniv.swu.starcapsule.global.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class BaseResponse<T> {
    HttpStatus status;
    String message;
    T result;

    public static <T> BaseResponse<T> response(T result) {
        if (result == null) {
            return new BaseResponse<>(HttpStatus.FORBIDDEN, "fail", result);
        } else {
            return new BaseResponse<>(HttpStatus.OK, "success", result);
        }
    }
}

