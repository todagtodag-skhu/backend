package kr.omong.todagtodag.domain.relation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.omong.todagtodag.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@Entity
@Table(
        name = "user_relation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"todak_id", "sungjang_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todak_id", nullable = false)
    private User todak;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sungjang_id", nullable = false)
    private User sungjang;

    private String childName;
    private LocalDate childBirthday;

    public static UserRelation of(User todak, User sungjang, String childName, LocalDate childBirthday) {
        return UserRelation.builder()
                .todak(todak)
                .sungjang(sungjang)
                .childName(childName)
                .childBirthday(childBirthday)
                .build();
    }

    public void updateChildInfo(String childName, LocalDate childBirthday) {
        this.childName = childName;
        this.childBirthday = childBirthday;
    }

    public boolean isOwnedByTodak(User todak) {
        return this.todak.equals(todak);
    }
}
