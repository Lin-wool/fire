package com.bcloud.fire;

public class Constants {

    public static final String SESSION_SYSUSER_KEY = "sysUser";
    public static final String SESSION_ACCOUNT_KEY = "account";

    public static final long LOGIN_TYPE_SYS_USER = 0;
    public static final long LOGIN_TYPE_MAINTAIN_ADMIN = 1;
    public static final long LOGIN_TYPE_MAINTAIN_SUB = 2;
    public static final long LOGIN_TYPE_ACCOUNT = 3;

    public static final long LOG_TYPE_LOGIN = 1;

    public static final long LOG_TYPE_LOGOUT = 2;

    public static final long LOG_TYPE_ADD = 3;

    public static final long LOG_TYPE_DELETE = 4;

    public static final long LOG_TYPE_UPDATE = 5;

    public static final long LOG_TYPE_AUDIT = 6;

    public static final String DEFAULT_PASSWORD = "111111";

    public static enum Role {
        ROLE_SYSUSER("用户管理"),
        ROLE_SYSPARAM("配置管理"),
        ROLE_ACCOUNT("客户管理"),
        ROLE_SYSLOG("日志管理"),
        ROLE_DEVICE("设备管理"),
        ROLE_SIM("SIM卡管理"),
        ROLE_BUILDING("建筑管理"),
        ROLE_ALARM("告警管理");

        private String text;

        private Role(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }

        public static String text(String name) {
            for (Role r : Role.values()) {
                if (r.name().equals(name)) {
                    return r.text;
                }
            }
            return "";
        }

    }

}
