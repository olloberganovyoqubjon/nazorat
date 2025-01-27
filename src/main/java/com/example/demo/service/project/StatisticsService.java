package com.example.demo.service.project;

import com.example.demo.entity.Users;
import com.example.demo.helper.WeekDaysCalculator;
import com.example.demo.payload.ApiResult;
import com.example.demo.payload.project.StatisticsDto;
import com.example.demo.payload.project.StatisticsTable;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.project.ChargersRepository;
import com.example.demo.repository.project.ControlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    private final ControlRepository controlRepository;
    private final UserRepository userRepository;

    public StatisticsService(ControlRepository controlRepository, ChargersRepository chargersRepository, UserRepository userRepository) {
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
        return new ApiResult("O'tgan hafta va bugungi kungacha bo'lgan nazoratlar statistikasi", true, statisticsDtoList);
    }

    public ApiResult getTableControls(Users currentUser) {
        List<StatisticsTable> statisticsTableList = new ArrayList<>();
        int allControls;                        //barcha nazoratlar
        int allReturnedAndNotReturnedControls;  //barcha qaytarilgan va qaytarilmagan nazoratlar
        int nowControls;                        //hali nazoratda
        int returnedControls;                   //qaytarilgan
        int notControls;                        //barcha nazoratdan qo'yilmaganlart


        //mening nazoratlarim
        allControls = controlRepository.countAllControlByUsers_Id(currentUser.getId());
        allReturnedAndNotReturnedControls = controlRepository.countControlByUsers_IdAndBControlIsNotNull(currentUser.getId());
        nowControls = controlRepository.countControlByUsers_IdAndBControlIsNotNullAndControlAndBControl(currentUser.getId(), true);
        returnedControls = controlRepository.countControlByUsers_IdAndBControlIsNotNullAndControlAndBControl(currentUser.getId(), false);
        notControls = controlRepository.countControlByUsers_IdAndBControlIsNull(currentUser.getId());

        statisticsTableList.add(new StatisticsTable("Barcha nazoratlar", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        //rahbar nazoratlari
        allControls = controlRepository.countAllChiefControlByChargerUserId(currentUser.getId());
        allReturnedAndNotReturnedControls = controlRepository.countAllReturnedAndNotReturnedChiefControlByUserId(currentUser.getId());
        nowControls = controlRepository.countAllReturnedOrNotReturnedChiefControlByUserIdAndBControl(currentUser.getId(), true);
        returnedControls = controlRepository.countAllReturnedOrNotReturnedChiefControlByUserIdAndBControl(currentUser.getId(), false);
        notControls = controlRepository.countAllNotChiefControlByUserIdAndBControl(currentUser.getId());

        statisticsTableList.add(new StatisticsTable("Rahbar nazoratlari", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        //qo'l ostidagi nazoratlar
        allControls = controlRepository.countAllChildControlByResPersonId(currentUser.getId());
        allReturnedAndNotReturnedControls = controlRepository.countAllChildControlByResPersonIdAndBControlIsNotNul(currentUser.getId());
        nowControls = controlRepository.countAllChildControlByResPersonIdAndBControl(currentUser.getId(), true);
        returnedControls = controlRepository.countAllChildControlByResPersonIdAndBControl(currentUser.getId(), false);
        notControls = controlRepository.countAllChildControlByResPersonIdAndBControlIsNull(currentUser.getId());

        statisticsTableList.add(new StatisticsTable("Qo'l ostidagi nazoratlar", allControls, allReturnedAndNotReturnedControls, nowControls, returnedControls, notControls));

        return new ApiResult("Barcha nazoratlar statistikasi", true, statisticsTableList);
    }
}
