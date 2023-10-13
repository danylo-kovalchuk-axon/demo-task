package com.demo.demotask.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Demo Config Properties.
 *
 * @author Danylo Kovalchuk
 */
@Validated
@Component
@ConfigurationProperties("demo")
public class DemoConfigProperties {

    @NotNull
    @Min(1)
    private Integer delaySeconds;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer percentToNotify;

    @Valid
    private Bot bot;

    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public Integer getPercentToNotify() {
        return percentToNotify;
    }

    public void setPercentToNotify(Integer percentToNotify) {
        this.percentToNotify = percentToNotify;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public static final class Bot {

        @NotBlank
        private String token;

        @NotBlank
        private String username;

        @NotNull
        @Min(0)
        private Integer maxUsers;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getMaxUsers() {
            return maxUsers;
        }

        public void setMaxUsers(Integer maxUsers) {
            this.maxUsers = maxUsers;
        }
    }
}