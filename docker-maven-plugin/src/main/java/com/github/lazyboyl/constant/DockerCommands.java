package com.github.lazyboyl.constant;

/**
 * @author linzf
 * @since 2019-02-13 docker的常用命令
 */
public class DockerCommands {

    /**
     * 加载镜像成功的标志
     */
    public static final String LOAD_IMAGE_SUCCESS = "Loaded image:";


    /**
     * 加载镜像
     */
    public static final String LOAD_IMAGE = "docker load -i ";

    /**
     * 删除停止的容器
     */
    public static final String DOCKER_RM = "docker rm ";

    /**
     * 停止容器
     */
    public static final String DOCKER_STOP = "docker stop ";


    /**
     * 创建镜像成功以后的标志
     */
    public static final String DOCKER_BUILD_SUCCESS = "Successfully built";

    /**
     * 创建镜像的文件的名字
     */
    public static final String DOCKER_BUILD_FILE = "Dockerfile";

    /**
     * 创建镜像的基础目录
     */
    public static final String DOCKER_BUILD_PATH = "/home/docker/upload/";

    /**
     * 创建镜像
     */
    public static final String DOCKER_BUILD = "docker build -t ";

    /**
     * 根据镜像来启动容器
     */
    public static final String DOCKER_START = "docker run ";

    /**
     * 查询镜像是否启动成功
     */
    public static final String DOCKER_START_SUCCESS = "Up";

    /**
     * 获取启动的容器
     */
    public static final String DOCKER_PS = "docker ps -a ";

    /**
     * 获取docker的镜像列表
     */
    public static final String DOCKER_IMAGES = "docker images ";

    /**
     * 删除镜像
     */
    public static final String DOCKER_RMI = "docker rmi ";

    /**
     * 删除镜像成功
     */
    public static final String DOCKER_RMI_SUCCESS = "No such image";

    /**
     * 从公网拉取docker镜像
     */
    public static final String DOCKER_PULL = "docker pull ";

    /**
     * 表示镜像拉取成功
     */
    public static final String DOCKER_PULL_SUCCESS = "Downloaded newer image for ";


    /**
     * 设置docker为开机启动
     */
    public static final String DOCKER_SYSTEMCTL_ENABLE = "systemctl enable docker";

    /**
     * 表示程序退出的标志
     */
    public static final String DOCKER_CONTAINER_STATUS_EXITED = "Exited";

}
