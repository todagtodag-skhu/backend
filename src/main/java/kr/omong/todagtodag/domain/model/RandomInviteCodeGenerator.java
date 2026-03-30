package kr.omong.todagtodag.domain.model;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomInviteCodeGenerator implements InviteCodeGenerator {

    private static final String CHARS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
