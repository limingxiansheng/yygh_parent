package com.atguigu.yygh.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @createTime : 2022/9/29 18:35
 * "success": 布尔, //响应是否成功
 *   "code": 数字, //响应码
 *   "message": 字符串, //返回消息
 *   "data": HashMap //返回数据，放在键值对中
 */
@Data
@NoArgsConstructor
public class R {
    private Boolean success;
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<String,Object>();

    //默认的ok和error方法
    public static R ok() {
     R r = new R();
     r.setSuccess(true);
     r.setCode(ResultCode.SUCCESS);
     r.setMessage("成功");
     return r;
    }

    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    //字符串 返回消息
    public R message(String message){
        this.setMessage(message);
        return this;
    }

    //下面是返回的响应状态码
    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    //data属性赋值
    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

}
