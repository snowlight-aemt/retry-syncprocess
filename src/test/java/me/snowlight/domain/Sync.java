package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;

class Sync {
    int cnt = 0;
    public void sync(TeamDao teamDao) {
        cnt++;
        if (cnt % 3 == 0) {
            throw new RuntimeException("1");
        }

        System.out.println(teamDao.toString());

    }
}
