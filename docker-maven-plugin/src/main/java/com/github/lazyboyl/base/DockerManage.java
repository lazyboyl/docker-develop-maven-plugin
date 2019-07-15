package com.github.lazyboyl.base;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.github.lazyboyl.constant.DockerCommands;
import com.github.lazyboyl.entity.DockerContainer;
import com.github.lazyboyl.entity.DockerImages;
import com.github.lazyboyl.entity.DockerRun;
import com.github.lazyboyl.entity.SshResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linzf
 * @since 2019-02-20
 * 类描述：实现对docker的管理
 */
public class DockerManage {

    private static final Logger log = LoggerFactory.getLogger(DockerManage.class);

    /**
     * @param conn 服务器连接对象
     * @param imageName 镜像名字
     * @return 返回镜像加载处理结果
     * 功能描述： 实现通过文件直接加载镜像
     */
    public static SshResult dockerLoad(Connection conn, String imageName) {
        Session session = null;
        try {
            session = conn.openSession();
            String dockerLoad = DockerCommands.LOAD_IMAGE + imageName;
            String dockerLoadResult = LinuxManage.getSessionResult(conn, dockerLoad);
            log.info("加载镜像返回的结果是：{}", dockerLoadResult);
            if (dockerLoadResult.contains(DockerCommands.LOAD_IMAGE_SUCCESS)) {
                return new SshResult(true, "0000", "镜像加载成功！");
            } else {
                return new SshResult(false, "0014", "镜像加载失败！失败原因：" + dockerLoadResult);
            }
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @param containerIds 容器的ID集合使用小写逗号分隔开
     * @return 返回删除容器处理的结果
     * 功能描述： 根据容器的ID集合来删除已经停止的容器数据
     */
    public static SshResult dockerRm(Connection conn, String containerIds) {
        Session session = null;
        try {
            session = conn.openSession();
            String dockerRm = DockerCommands.DOCKER_RM + containerIds;
            String dockerRmResult = LinuxManage.getSessionResult(conn, dockerRm);
            log.info("删除停止的容器的返回结果：{}", dockerRmResult);
            return new SshResult(true, "0000", "停止的容器删除成功！");
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @param containerIds 容器ID的集合使用小写的逗号分隔开
     * @return 停止容器返回的处理结果
     * 功能描述： 根据容器的ID来停止容器
     */
    public static SshResult dockerStop(Connection conn, String containerIds) {
        Session session = null;
        try {
            session = conn.openSession();
            String stopContainer = DockerCommands.DOCKER_STOP + containerIds;
            String stopContainerResult = LinuxManage.getSessionResult(conn, stopContainer);
            log.info("停止容器的返回结果是：{}", stopContainerResult);
            return new SshResult(true, "0000", "容器停止成功！");
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @param imageName  镜像名字
     * @param dockerPath 镜像所需文件目录
     * @param dockerFile 创建镜像的主文件名字
     * @return 返回build镜像的处理结果
     * @since 创建镜像
     */
    public static SshResult dockerBuild(Connection conn, String imageName, String dockerPath, String dockerFile) {
        Session session = null;
        if ("".equalsIgnoreCase(dockerPath)) {
            dockerPath = DockerCommands.DOCKER_BUILD_PATH;
        }
        if ("".equalsIgnoreCase(dockerFile)) {
            dockerFile = DockerCommands.DOCKER_BUILD_FILE;
        }
        // 镜像的名字只能是小写字母，因此此处将大写字母转为小写字母
        imageName = imageName.toLowerCase();
        try {
            session = conn.openSession();
            String dockerBuildCommand = "cd " + dockerPath + "; " + DockerCommands.DOCKER_BUILD + "\"" + imageName + "\"" + " . -f " + dockerPath + dockerFile;
            String dockerBuildCommandResult = LinuxManage.getSessionResult(conn, dockerBuildCommand);
            log.info("dockerBuildCommandResult：{}", dockerBuildCommandResult);
            if (dockerBuildCommandResult.contains(DockerCommands.DOCKER_BUILD_SUCCESS)) {
                return new SshResult(true, "0000", "创建镜像成功！");
            } else {
                return new SshResult(false, "0013", "创建镜像失败！失败原因：" + dockerBuildCommandResult);
            }
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @param dockerRun 启动docker镜像的命令
     * @return 启动docker镜像返回的操作结果
     * 功能描述： 根据启动的命令来启动镜像
     */
    public static SshResult dockerImageStart(Connection conn, DockerRun dockerRun) {
        Session session = null;
        try {
            session = conn.openSession();
            String dockerStartCommand = DockerCommands.DOCKER_START + dockerRun.toString();
            String dockerStartCommandResult = LinuxManage.getSessionResult(conn, dockerStartCommand);
            log.info("dockerStartCommandResult：{}，执行的命令为：{}", dockerStartCommandResult,dockerStartCommand);
            // 判断当前的容器是否启动成功
            SshResult sshResult = getDockerContainers(conn, dockerRun.getName());
            if (sshResult.isSuccess()) {
                List<DockerContainer> dockerContainerList = (List<DockerContainer>) sshResult.getObj();
                if (dockerContainerList.size() > 0) {
                    DockerContainer dockerContainer = dockerContainerList.get(0);
                    if (dockerContainer.getStatus().contains(DockerCommands.DOCKER_START_SUCCESS)) {
                        log.info("启动成功");
                        return new SshResult(true, "0000", "镜像启动成功，镜像ID为：" + dockerStartCommandResult);
                    }
                }
            }
            return new SshResult(false, "9999", "镜像启动失败！失败原因：" + dockerStartCommandResult);
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @return 获取启动的容器的数据
     * 功能描述： 获取启动的容器信息
     */
    public static SshResult getDockerContainers(Connection conn) {
        return getDockerContainers(conn, "");
    }

    /**
     * @param conn 服务器连接对象
     * @param containerName 容器的名字
     * @return  返回启动的容器的数据
     * 功能描述： 根据容器的名字来获取容器信息
     */
    public static SshResult getDockerContainers(Connection conn, String containerName) {
        List<DockerContainer> dockerContainerList = new ArrayList<>();
        Session session = null;
        String[] dockerContainerResult;
        try {
            session = conn.openSession();
            String dockerPs = DockerCommands.DOCKER_PS;
            if (!"".equals(containerName)) {
                dockerPs = dockerPs + " -f name=" + containerName;
            }
            String dockerPsCommandResult = LinuxManage.getSessionResult(conn, dockerPs);
            String[] dockerPsResults = dockerPsCommandResult.split("\n");
            if (dockerPsResults.length > 1) {
                for (int i = 1; i < dockerPsResults.length; i++) {
                    // 根据至少两个空格符以上进行一行数据的拆分
                    dockerContainerResult = dockerPsResults[i].split("\\s{2,}");
                    dockerContainerList.add(new DockerContainer(dockerContainerResult));
                }
            }
            log.info("获取的容器的返回是：{}", dockerPsCommandResult);
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new SshResult(true, "0000", "获取容器数据成功！", dockerContainerList);
    }

    /**
     * @param conn 服务器连接对象
     * @param imageName 镜像名字
     * @return 返回移除镜像处理的结果
     * @since 根据镜像名字来删除镜像
     */
    public static SshResult removeImage(Connection conn, String imageName) {
        Session session = null;
        try {
            session = conn.openSession();
            String dockerRmiCommand = DockerCommands.DOCKER_RMI + imageName;
            String dockerRmiCommandResult = LinuxManage.getSessionResult(conn, dockerRmiCommand);
            log.info("删除镜像返回的结果是：{}", dockerRmiCommandResult);
            if (dockerRmiCommandResult.contains(DockerCommands.DOCKER_RMI_SUCCESS)) {
                return new SshResult(true, "0000", "镜像删除成功！");
            } else {
                return new SshResult(false, "0011", "镜像删除失败！失败原因：" + dockerRmiCommandResult);
            }
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @param imageName 镜像名字
     * 功能描述： 从公网拉取docker的镜像
     */
    public static SshResult getImageFromNet(Connection conn, String imageName) {
        Session session = null;
        try {
            session = conn.openSession();
            String dockerPullCommand = DockerCommands.DOCKER_PULL + imageName;
            String dockerPullCommandResult = LinuxManage.getSessionResult(conn, dockerPullCommand);
            log.info("dockerPullCommandResult：{}", dockerPullCommandResult);
            if (dockerPullCommandResult.contains(DockerCommands.DOCKER_PULL_SUCCESS)) {
                return new SshResult(true, "0000", "镜像拉取成功！");
            } else {
                return new SshResult(false, "0011", "镜像拉取失败！失败原因：" + dockerPullCommandResult);
            }
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败，失败原因：" + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @param conn 服务器连接对象
     * @return 获取所有的镜像列表
     * 功能描述： 获取服务器的所有镜像列表
     */
    public static SshResult getDockerImages(Connection conn) {
        return getDockerImages(conn, "");
    }

    /**
     * @param conn 服务器连接对象
     * @param imagesName 镜像名字
     * @return 以镜像名字的形式来获取服务器的镜像列表
     * 功能描述： 以镜像名字的形式来获取服务器的镜像列表
     */
    public static SshResult getDockerImages(Connection conn, String imagesName) {
        List<DockerImages> dockerImagesList = new ArrayList<>();
        Session session = null;
        DockerImages dockerImages;
        String[] dockerImagesResult;
        try {
            session = conn.openSession();
            String dockerImagesCommand = DockerCommands.DOCKER_IMAGES + imagesName;
            String dockerImagesCommandResult = LinuxManage.getSessionResult(conn, dockerImagesCommand);
            String[] dockerImagesResults = dockerImagesCommandResult.split("\n");
            if (dockerImagesResults.length > 1) {
                for (int i = 1; i < dockerImagesResults.length; i++) {
                    // 根据至少两个空格符以上进行一行数据的拆分
                    dockerImagesResult = dockerImagesResults[i].split("\\s{2,}");
                    if (dockerImagesResult.length >= 5) {
                        dockerImages = new DockerImages();
                        dockerImages.setRepository(dockerImagesResult[0]);
                        dockerImages.setTag(dockerImagesResult[1]);
                        dockerImages.setImageId(dockerImagesResult[2]);
                        dockerImages.setCreated(dockerImagesResult[3]);
                        dockerImages.setSize(dockerImagesResult[4]);
                        dockerImagesList.add(dockerImages);
                    }
                }
            }
            log.info("dockerImagesCommandResult:{}", dockerImagesCommandResult);
        } catch (IOException e) {
            log.info("打开服务器连接失败，失败原因：{}", e.getMessage());
            return new SshResult(false, "9999", "打开服务器连接失败！失败原因：" + e.getMessage(), dockerImagesList);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new SshResult(true, "0000", "获取镜像数据成功！", dockerImagesList);
    }

}
