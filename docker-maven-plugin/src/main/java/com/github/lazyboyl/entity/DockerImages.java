package com.github.lazyboyl.entity;

/**
 * @author linzf
 * @since 2019-02-13
 * 类描述：docker镜像信息对象
 */
public class DockerImages {

    /**
     * 镜像名字
     */
    private String repository;

    /**
     * 镜像更新版本
     */
    private String tag;

    /**
     * 镜像ID
     */
    private String imageId;

    /**
     * 镜像创建时间
     */
    private String created;

    /**
     * 镜像大小
     */
    private String size;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
