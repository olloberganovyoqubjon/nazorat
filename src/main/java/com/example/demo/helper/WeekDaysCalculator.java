package com.example.demo.helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

/**
 * o'tgan to'liq hafta va haftaning bugungi sanasigacha bo'lgan hafta kunlari nomlarini olib beruvchi klass
 */
public class WeekDaysCalculator {

    public HashMap<Date,String> getPastWeeksAndCurrentWeekDays(LocalDate currentDate) {
        HashMap<Date,String> days = new HashMap<>();
        LocalDate startOfCurrentWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1); // Haftaning boshiga o'tish

        // O'tgan haftalardagi kunlar

            LocalDate startOfWeek = startOfCurrentWeek.minusWeeks(1);
            for (int j = 0; j < 7; j++) {
                LocalDate day = startOfWeek.plusDays(j);
                Date utilDate = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
                days.put(utilDate,formatDayWithWeekday(day));
            }


        // Joriy haftaning kunlari
        for (int i = 0; i < currentDate.getDayOfWeek().getValue(); i++) {
            LocalDate day = startOfCurrentWeek.plusDays(i);
            Date utilDate = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
            days.put(utilDate,formatDayWithWeekday(day));
        }

        return days;
    }

    private String formatDayWithWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("uz"));
        return dayName + ", " + date; // Kun nomi va sana birga qaytariladi
    }
}

