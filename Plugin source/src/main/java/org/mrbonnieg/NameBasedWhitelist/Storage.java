package org.mrbonnieg.NameBasedWhitelist;

import java.util.List;

public interface Storage {
    List<String> getPlayers();
    boolean addPlayer(String username);
    boolean removePlayer(String username);
    void saveWhitelist();
}
