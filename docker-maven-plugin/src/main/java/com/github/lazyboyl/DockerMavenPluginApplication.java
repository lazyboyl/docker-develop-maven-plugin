package com.github.lazyboyl;

import ch.ethz.ssh2.Connection;
import com.github.lazyboyl.base.DockerManage;
import com.github.lazyboyl.base.LinuxManage;
import com.github.lazyboyl.base.SshUploadFileUtil;
import com.github.lazyboyl.entity.DockerContainer;
import com.github.lazyboyl.entity.DockerRun;
import com.github.lazyboyl.entity.SshResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author linzf
 * @since 22:05
 * 类描述：docker插件的主入口类
 */
@Mojo(name = "dockerMavenPlugin", defaultPhase = LifecyclePhase.PACKAGE)
public class DockerMavenPluginApplication extends AbstractMojo {

    private static final Logger log = LoggerFactory.getLogger(DockerMavenPluginApplication.class);

    /**
     * docker的镜像文件dockerFile文件和jar包存放的位置
     */
    @Parameter(property = "dockerImagesPath")
    private String dockerImagesPath;

    /**
     * 生成的jar包所在的路径
     */
    @Parameter(property = "jarTargetPath")
    private String jarTargetPath;

    /**
     * dockerFile的包的存放位置
     */
    @Parameter(property = "dockerFilePath")
    private String dockerFilePath;

    /**
     * 上传完成以后将jar包重新命名成符合dockerfile中一致的命名
     */
    @Parameter(property = "jarRename")
    private String jarRename;

    /**
     * 镜像头部名字
     */
    @Parameter(property = "imagesHeadName")
    private String imagesHeadName;

    /**
     * 启动容器的端口映射
     */
    @Parameter(property = "containerRunPorts")
    private List<String> containerRunPorts;

    /**
     * 启动容器的名字
     */
    @Parameter(property = "containerRunName")
    private String containerRunName;

    /**
     * 启动容器的文件映射路径
     */
    @Parameter(property = "containerRunShare")
    private String containerRunShare;

    /**
     * 服务器的配置参数集合
     */
    @Parameter(property = "options")
    private List<String> options;

    /**
     * 容器的网络的模式
     */
    @Parameter(property = "netType")
    private String netType;


    @Override
    public void execute() throws MojoFailureException {
        // 前置判断jarTargetPath的值不能为空
        if (jarTargetPath == null || "".equals(jarTargetPath)) {
            throw new MojoFailureException("jarTargetPath属性的值不能为空。");
        }
        // 前置判断dockerImagesPath的值不能为空
        if (dockerImagesPath == null || "".equals(dockerImagesPath)) {
            throw new MojoFailureException("dockerImagesPath属性的值不能为空。");
        }
        // 前置判断dockerFilePath的值不能为空
        if (dockerFilePath == null || "".equals(dockerFilePath)) {
            throw new MojoFailureException("dockerFilePath属性的值不能为空。");
        }
        // 获取jar的名字xxx.jar
        String jarName = jarTargetPath.split("/")[jarTargetPath.split("/").length - 1];
        // 若镜像头名字为空则使用jar包的头名字来进行镜像的删除
        if (imagesHeadName == null || "".equals(imagesHeadName)) {
            imagesHeadName = jarName.split("\\.")[jarName.split("\\.").length - 2];
        }
        // 前置判断jarRename的值不能为空
        if (jarRename == null || "".equals(jarRename)) {
            jarRename = imagesHeadName + ".jar";
        }
        // 容器的名字若为空则直接使用镜像名字作为容器名字启动
        if (containerRunName == null || "".equals(containerRunName)) {
            containerRunName = imagesHeadName;
        }
        // 实现多服务器的部署上线
        for (String linuxInfo : options) {
            String[] linuxInfos = linuxInfo.split(",");
            String host = linuxInfos[0];
            String uName = linuxInfos[1];
            String uPass = linuxInfos[2];
            // 实现上传jar包
            Connection conn = LinuxManage.login(host, uName, uPass);
            if (conn == null) {
                throw new MojoFailureException("登录失败！失败原因：请确定账号密码以及IP地址是否正确！");
            }
            // 实现文件的重命名,同时删除旧的文件
            LinuxManage.execute(conn, "cd " + dockerImagesPath + ";rm -rf " + jarRename + "; mv " + jarName + " " + jarRename);
            // 上传文件
            SshUploadFileUtil sshUploadFileUtil = new SshUploadFileUtil();
            File file = new File(jarTargetPath);
            if (file == null) {
                throw new MojoFailureException("在" + jarTargetPath + "路径底下没有找到相应的dockerFile文件");
            }
            try {
                sshUploadFileUtil.transferFile(file, dockerImagesPath, conn);
            } catch (IOException e) {
                throw new MojoFailureException("文件上传失败！");
            }
            // 实现上传dockerfile文件
            file = new File(dockerFilePath);
            if (file == null) {
                throw new MojoFailureException("在" + dockerFilePath + "路径底下没有找到相应的dockerFile文件");
            }
            try {
                sshUploadFileUtil.transferFile(file, dockerImagesPath, conn);
            } catch (IOException e) {
                throw new MojoFailureException("dockerfile文件上传失败！");
            }
            /**
             * 实现删除旧版本的容器和镜像
             */
            // 获取所有的启动的容器
            List<DockerContainer> dockerContainerList = (List<DockerContainer>) DockerManage.getDockerContainers(conn).getObj();
            StringBuilder containerIds = new StringBuilder();
            // 匹配所有包含imagesHeadName的容器
            for (DockerContainer d : dockerContainerList) {
                if (d.getImage().indexOf(imagesHeadName) != -1) {
                    containerIds.append(" " + d.getContainerId());
                }
            }
            // 判断当前是否有需要删除的容器，若containerIds不为空则有需要删除的容器
            if (!"".equals(containerIds.toString())) {
                // 停止所有镜像为imagesHeadName属性的值的容器
                DockerManage.dockerStop(conn, containerIds.toString());
                // 删除所有的镜像为imagesHeadName属性的值的停止的容器
                DockerManage.dockerRm(conn, containerIds.toString());
                // 删除镜像为imagesHeadName属性的值
                DockerManage.removeImage(conn, imagesHeadName);
            } else {
                // 删除镜像为imagesHeadName属性的值
                DockerManage.removeImage(conn, imagesHeadName);
            }
            String dockerFile = dockerFilePath.split("/")[dockerFilePath.split("/").length - 1];
            // 创建镜像
            SshResult sshResult = DockerManage.dockerBuild(conn, imagesHeadName, dockerImagesPath, dockerFile);
            // 镜像创建成功，才可以启动容器
            if (sshResult.isSuccess()) {
                DockerRun dockerRun;
                for (int i = 0; i < containerRunPorts.size(); i++) {
                    dockerRun = new DockerRun();
                    dockerRun.setBack(true);
                    dockerRun.setImage(imagesHeadName);
                    // 设置容器启动的名字带上数字
                    dockerRun.setName(containerRunName + "-" + (i + 1));
                    dockerRun.setPort(containerRunPorts.get(i));
                    dockerRun.setShare(containerRunShare);
                    dockerRun.setNetType(netType);
                    // 启动镜像
                    DockerManage.dockerImageStart(conn, dockerRun);
                }
            } else {
                log.info("镜像创建失败，失败原因：{}", sshResult.getMsg());
            }
        }
    }
}
