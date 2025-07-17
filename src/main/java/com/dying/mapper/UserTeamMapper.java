package com.dying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dying.domain.UserTeam;
import org.apache.ibatis.annotations.Select;

/**
* @author 666
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2025-07-09 14:55:05
* @Entity generator.domain.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {

    /**
     * 根据队伍id查询最早加入的两个用户id
     * @param teamId 队伍id
     * @return 最早加入的两个用户id
     */
    @Select("SELECT user_id FROM user_team WHERE team_id = #{teamId} AND is_delete = 0 ORDER BY join_time ASC LIMIT 2")
    Long[] selectByJoinTime(Long teamId);

    /**
     * 根据用户id查询用未加入的队伍id
     * @param userId
     * @return
     */
    @Select("select distinct team_id from user_team where user_team.team_id not in(select team_id from user_team where user_id = #{userId} AND is_delete = 0)")
    Long[] selectTeam(Long userId);
}




