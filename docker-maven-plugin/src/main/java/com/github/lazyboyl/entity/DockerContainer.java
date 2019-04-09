package com.github.lazyboyl.entity;


import com.github.lazyboyl.constant.DockerCommands;

/**
 * @author linzf
 * @since 2019-02-13
 * 类描述：容器对象
 */
public class DockerContainer {

    /**
     * 容器ID
     */
    private String containerId;

    /**
     * 镜像名字
     */
    private String image;

    /**
     * 执行的命令
     */
    private String command;

    /**
     * 启动时间
     */
    private String created;

    /**
     * 状态
     */
    private String status;

    /**
     * 映射端口集合
     */
    private String ports;

    /**
     * 容器名字
     */
    private String names;

    public DockerContainer(){
        super();
    }

    public DockerContainer(String [] dockerContainers){
        this.containerId = dockerContainers[0];
        this.image = dockerContainers[1];
        this.command = dockerContainers[2];
        this.created = dockerContainers[3];
        this.status = dockerContainers[4];
        // 表示当前的程序已经停止了
        if(this.status.indexOf(DockerCommands.DOCKER_CONTAINER_STATUS_EXITED)!=-1){
            this.names = dockerContainers[5];
        }else{
            this.ports = dockerContainers[5];
            this.names = dockerContainers[6];
        }
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
