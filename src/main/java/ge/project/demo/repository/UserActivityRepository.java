package ge.project.demo.repository;

import ge.project.demo.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {

    @Query("select count(distinct u.userId) from UserActivity ua inner join ua.user u where ua.activityTime between ?1 and ?2")
    long countLogedInUsers(LocalDateTime activityTimeStart, LocalDateTime activityTimeEnd);

    @Query("select count(ua) from UserActivity ua where ua.activityTime between ?1 and ?2")
    long countActivities(LocalDateTime activityTimeStart, LocalDateTime activityTimeEnd);

}
