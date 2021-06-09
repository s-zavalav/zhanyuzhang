package tech.yuliu.dao;

import org.springframework.stereotype.Repository;
import tech.yuliu.bean.Schedule;
import tech.yuliu.bean.Word;

import java.util.List;

@Repository
public interface ScheduleDao {

    Integer insertSchedule(Schedule schedule);

    Integer insertManySchedule(List<Schedule> scheduleList);

    Integer updateManySchedule(List<Schedule> scheduleList);

    List<Word> selectWordUnderLowThresholdTime(Integer userId);

    List<Word> selectWordBetweenLowThresholdTimeAndThresholdTime(Integer userId);

    List<Word> selectWordHigherThresholdTime(Integer userId);
}
