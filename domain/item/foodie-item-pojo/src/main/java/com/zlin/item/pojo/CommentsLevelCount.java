package com.zlin.item.pojo;

/**
 * @author zlin
 * @date 20201229
 */
public class CommentsLevelCount {

    private Integer commentsLevel;
    private Integer commentsCount;

    public CommentsLevelCount() {
        this.commentsCount = 0;
    }

    public Integer getCommentsLevel() {
        return commentsLevel;
    }

    public void setCommentsLevel(Integer commentsLevel) {
        this.commentsLevel = commentsLevel;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }
}
