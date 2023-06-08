package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;

class Sync {
    public void sync(TeamDao teamDao) {
        System.out.println(teamDao.toString());
    }
}
