package com.github.lazyboyl.base;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author linzf
 * @since 2019-02-20
 * 类描述：实现ssh的管理
 */
public class LinuxManage {

    private static final Logger log = LoggerFactory.getLogger(LinuxManage.class);
    private static String DEFAULT_CHART = "UTF-8";

    /**
     * @param conn 服务器连接对象
     * @param cmd  执行的脚本
     * @return 执行脚本返回的结果集
     */
    public static String getSessionResult(Connection conn, String cmd) {
        String result = "";
        Session session = null;
        //执行命令
        try {
            session = conn.openSession();
            session.execCommand(cmd);
            result = processStdout(session.getStdout(), DEFAULT_CHART);
            //如果为得到标准输出为空，说明脚本执行出错了
            if (StringUtils.isBlank(result)) {
                result = processStdout(session.getStderr(), DEFAULT_CHART);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    /**
     * 登录主机
     *
     * @return 登录成功返回true，否则返回false
     */
    public static Connection login(String ip, String userName, String userPwd) {
        boolean flg;
        Connection conn = null;
        try {
            String[] ips = ip.split(":");
            if(ips.length>1){
                conn = new Connection(ips[0],Integer.parseInt(ips[1]));
            }else{
                conn = new Connection(ip);
            }
            //连接
            conn.connect();
            //认证
            flg = conn.authenticateWithPassword(userName, userPwd);
            if (flg) {
                log.info("=========登录成功=========" + conn);
                return conn;
            }
        } catch (IOException e) {
            log.error("=========登录失败=========" + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 解析脚本执行返回的结果集
     *
     * @param in      输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 远程执行shll脚本或者命令
     *
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public static String execute(Connection conn, String cmd) {
        String result = "";
        Session session = null;
        try {
            if (conn != null) {
                //打开一个会话
                session = conn.openSession();
                //执行命令
                session.execCommand(cmd);
                result = processStdout(session.getStdout(), DEFAULT_CHART);
                //如果为得到标准输出为空，说明脚本执行出错了
                if (StringUtils.isBlank(result)) {
                    log.info("执行的命令：{}", cmd);
                    result = processStdout(session.getStderr(), DEFAULT_CHART);
                } else {
                    log.info("执行的命令：{}", cmd);
                }
            }
        } catch (IOException e) {
            log.info("执行的命令失败：{},错误原因：{}", cmd,e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

}
