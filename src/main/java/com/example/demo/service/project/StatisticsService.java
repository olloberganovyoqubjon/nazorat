package com.example.demo.service.project;

import com.example.demo.entity.Users;
import com.example.demo.entity.project.Control;
import com.example.demo.helper.WeekDaysCalculator;
import com.example.demo.payload.ApiResult;
import com.example.demo.payload.project.ManagementStatisticsDto;
import com.example.demo.payload.project.StatisticsDto;
import com.example.demo.payload.project.StatisticsTable;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.project.ControlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    private final ControlRepository controlRepository;
    private final UserRepository userRepository;

    public StatisticsService(ControlRepository controlRepository, UserRepository userRepository) {
        this.controlRepository = controlRepository;
        this.userRepository = userRepository;
    }

    public ApiResult getControls(Users user) {
        List<StatisticsDto> statisticsDtoList = new ArrayList<>();
        Date subtractedDate1 = plusDate(1);
        Date subtractedDate3 = plusDate(3);
        Date subtractedDate4 = plusDate(4);
        List<Users> repositoryByStage = userRepository.findByStage(user.getStage() + 1);
        List<Users> firstUsers = new ArrayList<>();
        List<Users> secondUsers = new ArrayList<>();
        for (Users users : repositoryByStage) {
            if (users.getFatherUsers().getId().equals(user.getId())) {
                firstUsers.add(users);
            } else {
                secondUsers.add(users);
            }
        }
        repositoryByStage.clear();
        repositoryByStage.addAll(firstUsers);
        repositoryByStage.addAll(secondUsers);
        for (Users childUser : repositoryByStage) {
            int count1 = controlRepository.countControlByResPersonAndUsersAndControlPeriodIsAfter(user.getId(), childUser.getId(), subtractedDate1);
            int count2 = controlRepository.countControlByResPersonAndUsersNotLate(user.getId(), childUser.getId(), subtractedDate3);
            int count3 = controlRepository.countControlByResPersonAndUsersDistance(user.getId(), childUser.getId(), new Date(), subtractedDate4);
            List<Integer> integerList = new ArrayList<>();
            integerList.add(count1);
            integerList.add(count2);
            integerList.add(count3);
            statisticsDtoList.add(new StatisticsDto(null, null, null, childUser.getManagement(), null, null, integerList));
        }
        return new ApiResult("Barcha o'zidan pastgi hodimlarning nazoratdagi hujjatlar statistikasi", true, statisticsDtoList);
    }

    public ApiResult getDeletedControls(Users currentUser) {
        List<StatisticsDto> statisticsDtoList = new ArrayList<>();
        for (Users childUser : userRepository.findByStage(currentUser.getStage() + 1)) {
            int count1 = controlRepository.countByDeletedBControlAndUsers(currentUser.getId(), childUser.getId());
            statisticsDtoList.add(new StatisticsDto(count1, null, null, childUser.getChiefName(), null, null,null));
        }
        return new ApiResult("Barcha o'zidan pastgi hodimlarning nazoratdan yechilgan hujjatlar statistikasi", true, statisticsDtoList);
    }

    public ApiResult getMeinControls(Users currentUser) {
        Date subtractedDate1 = plusDate(1);
        int count1 = controlRepository.countByBControlAndUsersAndLate(true, currentUser.getId(), subtractedDate1);
        Date subtractedDate3 = plusDate(3);
        int count2 = controlRepository.countByBControlAndUsersAndNotLate(true, currentUser.getId(), subtractedDate3);
        Date subtractedDate4 = plusDate(4);
        int count3 = controlRepository.countByBControlAndUsersAndDistance(true, currentUser.getId(), subtractedDate4, new Date());
        List<Integer> integerList = new ArrayList<>();
        integerList.add(count1);
        integerList.add(count2);
        integerList.add(count3);
        return new ApiResult(currentUser.getChiefName() + "ning kechikkan va kechikmagan yaratgan nazoratlar soni!", true, integerList);
    }

    private Date plusDate(int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, +count);
        return calendar.getTime();
    }

    public ApiResult getChiefControls(Users currentUser) {
        Date subtractedDate1 = plusDate(1);
        int count1 = controlRepository.countByChiefControlAndUsers_IdLate(currentUser.getId(),true,subtractedDate1);
        Date subtractedDate3 = plusDate(3);
        int count2 = controlRepository.countByChiefControlAndUsers_IdNotLate(currentUser.getId(),true,subtractedDate3);
        Date subtractedDate4 = plusDate(4);
        int count3 = controlRepository.countByChiefControlAndUsers_IdDistance(true,currentUser.getId(),subtractedDate4,new Date());
        List<Integer> integerList = new ArrayList<>();
        integerList.add(count1);
        integerList.add(count2);
        integerList.add(count3);
        return new ApiResult(currentUser.getChiefName() + "ga tushgan kechikkan va kechikmagan nazoratlar soni!", true, integerList);
    }

    public ApiResult getWeekControls(Users currentUser) {
        List<StatisticsDto> statisticsDtoList = new ArrayList<>();
        WeekDaysCalculator weekDaysCalculator = new WeekDaysCalculator();
        HashMap<Date,String> pastWeeksAndCurrentWeekDays = weekDaysCalculator.getPastWeeksAndCurrentWeekDays(LocalDate.now());
        pastWeeksAndCurrentWeekDays.forEach((week, currentDate) -> {
            Integer count1 = controlRepository.countDocsByCreatedDateAndUserId(week, currentUser.getId());
            Integer count2 = controlRepository.countChiefDocsByCreatedDateAndUserId(week, currentUser.getId());
            Integer count3 = controlRepository.countDocsByCreatedDateAndResPersonId(week, currentUser.getId());
            statisticsDtoList.add(new StatisticsDto(count1, count2, count3, "O'tgan hafta va bugungi kungacha bo'lgan nazoratlar statistikasi", week, currentDate,null));
        });
        statisticsDtoList.sort(Comparator.comparing(StatisticsDto::getDate));
        System.out.println(statisticsDtoList);
        return new ApiResult("O'tgan hafta va bugungi kungacha bo'lgan nazoratlar statistikasi", true, statisticsDtoList);
    }

    public ApiResult getTableControls(Users currentUser) {
        List<StatisticsTable> statisticsTableList = new ArrayList<>();
        int allControls;                        //barcha nazoratlar
        int allReturnedAndNotReturnedControls;  //barcha qaytarilgan va qaytarilmagan nazoratlar
        int nowControls;                        //hali nazoratda
        int returnedControls;                   //qaytarilgan
        int notControls;                        //barcha nazoratdan qo'yilmaganlar


        //mening nazoratlarim
        allControls = controlRepository.countAllControlByUsers_Id(currentUser.getId());
        allReturnedAndNotReturnedControls = controlRepository.countControlByUsers_IdAndBControlIsNotNull(currentUser.getId());
        nowControls = controlRepository.countControlByUsers_IdAndBControlIsNotNullAndControlAndBControl(currentUser.getId(), true);
        returnedControls = controlRepository.countControlByUsers_IdAndBControlIsNotNullAndControlAndBControl(currentUser.getId(), false);
        notControls = controlRepository.countControlByUsers_IdAndBControlIsNull(currentUser.getId());

        statisticsTableList.add(new StatisticsTable("Mening nazoratlarim", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        //rahbar nazoratlari
        allControls = controlRepository.countAllChiefControlByChargerUserId(currentUser.getId());
        allReturnedAndNotReturnedControls = controlRepository.countAllReturnedAndNotReturnedChiefControlByUserId(currentUser.getId());
        nowControls = controlRepository.countAllReturnedOrNotReturnedChiefControlByUserIdAndBControl(currentUser.getId(), true);
        returnedControls = controlRepository.countAllReturnedOrNotReturnedChiefControlByUserIdAndBControl(currentUser.getId(), false);
        notControls = controlRepository.countAllNotChiefControlByUserIdAndBControl(currentUser.getId());

        statisticsTableList.add(new StatisticsTable("Rahbar nazoratlari", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        //qo'l ostidagi nazoratlar
        allControls = controlRepository.countAllChildControlByResPersonId(currentUser.getId(), currentUser.getStage() + 1);
        allReturnedAndNotReturnedControls = controlRepository.countAllChildControlByResPersonIdAndBControlIsNotNul(currentUser.getId(), currentUser.getStage() + 1);
        returnedControls = controlRepository.countAllChildControlByResPersonIdAndBControl(currentUser.getId(), false, currentUser.getStage() + 1);
        nowControls = controlRepository.countAllChildControlByResPersonIdAndBControl(currentUser.getId(), true, currentUser.getStage() + 1);
        notControls = controlRepository.countAllChildControlByResPersonIdAndBControlIsNull(currentUser.getId(), currentUser.getStage() + 1);

        statisticsTableList.add(new StatisticsTable("Qo'l ostidagi nazoratlar", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        return new ApiResult("Barcha nazoratlar statistikasi", true, statisticsTableList);
    }

    public ApiResult getAllManagementControls(Users currentUser) {
        List<ManagementStatisticsDto> managementStatisticsDtoList = new ArrayList<>();
        userRepository.findByStage(currentUser.getStage() + 1).forEach(users -> {
            int notControlled = 0;
            int returnedControl = 0;
            int threeDaysMore = 0;
            int twoDaysLess = 0;
            int late = 0;
            List<Control> controlSet = controlRepository.findByResPerson_IdAndUsers_StageAndUsers_Id(currentUser.getId(), users.getStage(), users.getId());
            for (Control control : controlSet) {
                if (control.getReturned()) {
                    returnedControl++;
                } else {
                    if (control.getControlPeriod() != null) {
                        if (control.getControlPeriod().before(new Date())) {
                            late++;
                        } else {
                            if (control.getControlPeriod().after(plusDate(3))) {
                                threeDaysMore++;
                            } else {
                                twoDaysLess++;
                            }
                        }
                    } else {
                        notControlled++;
                    }
                }
            }
            managementStatisticsDtoList.add(new ManagementStatisticsDto(users.getManagement(), notControlled, returnedControl, threeDaysMore, twoDaysLess, late));
        });
        return new ApiResult("Barcha rahbarlar, o'z va qo'l ostidagilarning nazoratlari statistikasi", true, managementStatisticsDtoList);
    }

}
