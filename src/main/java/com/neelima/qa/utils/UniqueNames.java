package com.neelima.qa.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates usernames that other users of the shared demo cannot block.
 *
 * Random letters keep names unique across parallel runs and other visitors, and the
 * result stays within ParaBank's 20-character limit: longer usernames are wrongly
 * rejected as "already exists" (DEF-005).
 */
public final class UniqueNames {

    private UniqueNames() {
    }

    /** Returns prefix + 4 random letters + 9 digits; keep the prefix to 7 characters or fewer. */
    public static String username(String prefix) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            letters.append((char) ('a' + random.nextInt(26)));
        }
        return prefix + letters + System.currentTimeMillis() % 1_000_000_000L;
    }
}
