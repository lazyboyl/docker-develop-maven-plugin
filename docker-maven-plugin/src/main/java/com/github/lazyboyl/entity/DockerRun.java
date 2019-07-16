package com.github.lazyboyl.entity;

/**
 * @author linzf
 * @since 2019-02-13 docker
 * 类描述：启动的时候的参数
 */
public class DockerRun {

    /**
     * 容器名字
     */
    private String name;

    /**
     * 镜像名字
     */
    private String image;

    /**
     * 是否后台运行true，加-d参数，false则不加-D参数
     */
    private boolean isBack;

    /**
     * 端口映射
     */
    private String port;

    /**
     * 共享文件映射
     */
    private String share;

    /**
     * 容器的网络的模式
     */
    private String netType;



    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    @Override
    public String toString() {
        StringBuilder command = new StringBuilder();
        if(isBack){
            command.append(" -d ");
        }
        if(port!=null&&!"".equals(port)){
            String [] ports = port.split(",");
            for(String p:ports){
                if(!"".equals(p)){
                    command.append(" -p "+p);
                }
            }
        }
        if(share!=null&&!"".equals(share)){
            command.append(" -v "+share);
        }
        if(netType!=null&&!"".equals(netType)){
            command.append(" --net=" + netType);
        }
        if(name!=null&&!"".equals(name)){
            command.append(" --name "+name);
        }
        if(image!=null&&!"".equals(image)){
            command.append(" "+image);
        }
        return command.toString();
    }

}
