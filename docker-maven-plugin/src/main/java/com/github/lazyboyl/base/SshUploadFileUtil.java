package com.github.lazyboyl.base;

import ch.ethz.ssh2.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author linzf
 * @since 2019-01-30 实现将文件上传到linux服务器
 */
public class SshUploadFileUtil {

    private static final Logger log = LoggerFactory.getLogger(SshUploadFileUtil.class);

    /**
     * 功能描述：实现文件上传
     *
     * @param file                  本地文件file对象
     * @param remoteTargetDirectory 目标服务器的文件存放的地址
     * @param conn                  linux的连接
     * @throws IOException 读取异常
     */
    public void transferFile(File file, String remoteTargetDirectory, Connection conn) throws IOException {
        if (file.isDirectory()) {
            throw new RuntimeException(file + "  is not a file");
        }
        String fileName = file.getName();
        // 创建文件夹，同时删除旧的文件。
        execCommand("mkdir -p " + remoteTargetDirectory + ";cd " + remoteTargetDirectory + ";rm " + fileName + "; touch " + fileName, conn);
        SCPClient sCPClient = conn.createSCPClient();
        SCPOutputStream scpOutputStream = sCPClient.put(fileName, file.length(), remoteTargetDirectory, "7777");
        scpOutputStream.write(fileToBinArray(file));
        scpOutputStream.flush();
        scpOutputStream.close();
    }

    /**
     * 功能描述：实现连接到linux然后创建文件的命令
     *
     * @param command 执行的命令
     * @param conn    ssh连接对象
     * @return 返回执行的结果
     * @throws IOException 读取异常
     */
    private String execCommand(String command, Connection conn) throws IOException {
        Session session = conn.openSession();
        session.execCommand(command, StandardCharsets.UTF_8.toString());
        String result;
        try (
                InputStream streamGobbler = new StreamGobbler(session.getStdout());
        ) {
            result = IOUtils.toString(streamGobbler, StandardCharsets.UTF_8);
            session.waitForCondition(ChannelCondition.EXIT_SIGNAL, Long.MAX_VALUE);
            if (session.getExitStatus().intValue() == 0) {
                log.info("execCommand: {} success ", command);
            } else {
                log.error("execCommand : {} fail", command);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    /**
     * 文件转为二进制数组
     *
     * @param file 需要解析的文件
     * @return 返回二进制数据
     */
    private byte[] fileToBinArray(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            if (fis == null) {
                return new byte[0];
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
                try {
                    copy(fis, out);
                } finally {
                    try {
                        fis.close();
                    } catch (IOException var12) {
                        log.info(var12.getMessage());
                    }
                    try {
                        out.close();
                    } catch (IOException var11) {
                        log.info(var11.getMessage());
                    }
                }
                return out.toByteArray();
            }
        } catch (Exception ex) {
            throw new RuntimeException("transform file into bin Array 出错", ex);
        }
    }

    private static int copy(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        int bytesRead;
        for (; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        return byteCount;
    }


}
