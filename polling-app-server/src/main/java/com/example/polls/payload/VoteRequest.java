package com.example.polls.payload;
import javax.validation.constraints.NotNull;

/**
 * 选票的请求payload，请求Vote所需数据的打包
 */
public class VoteRequest {
    @NotNull
    private Long choiceId;

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }
}

