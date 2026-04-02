package kr.omong.todagtodag.domain.user.entity;

public enum Role {
    PENDING,
    SUNGJANG,
    TODAK;

    public boolean isSelectableForOnboarding() {
        return this == SUNGJANG || this == TODAK;
    }
}
