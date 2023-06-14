package me.snowlight.domain.sync;

import me.snowlight.domain.team.TeamDao;

public class Sync {
    int cnt = 0;
    public void sync(TeamDao teamDao) {
        cnt++;
        if (cnt % 3 == 0) {
            throw new RuntimeException("1");
        }

        System.out.println(teamDao.toString());

    }
}
