package com.github.lazyboyl.entity;

/**
 * @author linzf
 * @since 2019-02-12
 * 类描述：ssh操作返回的结果集
 */
public class SshResult {

    /**
     * true:成功，false：失败
     */
    private boolean success;

    /**
     * 成功或者失败以后返回的消息
     */
    private String msg;

    /**
     * 返回的结果
     */
    private Object obj;

    /**
     * 失败编码
     * 0000：操作成功
     * 0001：文件不存在
     * 0002：服务器无此文件目录
     * 0003：上传的文件的大小不对
     *
     * 0009：当前系统已经安装了docker
     *
     * 0011：镜像拉取失败
     * 0012：镜像删除失败
     * 0013：创建镜像失败
     * 0014：镜像加载失败
     *
     * 0021：邮件发送失败
     *
     * 9999：连接服务器失败
     */
    private String code;

    public SshResult(){
        super();
    }

    public SshResult(boolean success, String code, String msg){
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public SshResult(boolean success, String code, String msg, Object obj){
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
