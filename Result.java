package top.tt.common.result;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author TJ Yuan
 * @date 2024/9/25 上午10:40
 * 返回结果封装类
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码
    private String msg; //信息
    private Object data; //数据

    public static <T> Result<T> success(Object data, String msg) {
        Result<T> result = new Result<>();
        result.code = HttpStatus.OK.value();
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = HttpStatus.OK.value();
        return result;
    }

    public static <T> Result<T> success(Object data, Integer code, String msg) {
        Result<T> result = new Result<>();
        result.data = data;
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.msg = HttpStatus.BAD_REQUEST.getReasonPhrase();
        result.code = HttpStatus.BAD_REQUEST.value();
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = HttpStatus.BAD_REQUEST.value();
        return result;
    }
}

