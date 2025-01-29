package com.example.demo.repository.project;

import com.example.demo.entity.Users;
import com.example.demo.entity.project.Control;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ControlRepository extends JpaRepository<Control, Long> {

    List<Control> findByUsers_IdOrderById(Long id);

    Optional<Control> findByIdAndUsers_Id(Long idControl, Long users_id);

    Optional<Control> findByIdAndOutControllerId(Long idControl, Long outControllerId);

    List<Control> findByReturnedAndSeen(Boolean returned, Boolean seen);

    List<Control> findByUsers_Id(Long id);

    Set<Control> findByResPersonOrUsersOrderById(Users resPerson, Users users);

    Set<Control> findByResPersonOrderById(Users resPerson);

    List<Control> findByResPerson_IdAndUsers_StageAndUsers_Id(Long resPerson_id, Integer users_stage, Long users_id);

    List<Control> findByUsersOrderByIdDesc(Users users);

    List<Control> findByResPersonAndUsersIsNotOrderByIdDesc(Users resPerson, Users users);

    @Query("select d from Control d " +
            "join Charger c on d.id = c.users.id " +
            "join Users u on d.users.id = u.id " +
            "where u.id = :currentUserId and c.users.id = :userId order by u.id desc")
    List<Control> findByCurrentUsersChargerUserOrderByIdDesc(@Param("currentUserId") Long currentUserId, @Param("userId") Long userId);

    List<Control> findByResPersonAndUsersIsNotAndUsers_IdOrderByIdDesc(Users resPerson, Users users, Long usersChildId);


    @Query("select d " +
            "from Control d " +
            "         join Charger c on d.id = c.control.id " +
            "where c.users.id = :userId order by d.id desc")
    List<Control> findDocsByChargerUserIdDesc(@Param("userId") Long userId);


    //statistics

    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, nazorat kirishiga ega bo'lgan, berilgan bControl qiymatiga mos keladigan,
     * berilgan userId ga ega bo'lgan va nazorat davri berilgan sanadan katta bo'lgan Foydalanuvchilar sonini
     * olish uchun ishlatiladi.
     *
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param date     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan Foydalanuvchilar soni
     */
    @Query("select count(u) from Users u " +
            "join Control d on u.id = d.users.id " +
            "where d.bControl is not null and d.bControl = :bControl and u.id = :userId and d.controlPeriod < :date")
    Integer countByBControlAndUsersAndLate(@Param("bControl") Boolean bControl, @Param("userId") Long userId, @Param("date") Date date);

    @Query("select count(d) from Control d " +
            "where d.bControl is not null and d.bControl = true and d.resPerson.id = :resPersonId and d.users.id = :userId and d.controlPeriod < :date")
    Integer countControlByResPersonAndUsersAndControlPeriodIsAfter(@Param("resPersonId") Long resPersonId, @Param("userId") Long userId, Date date);


    @Query("select count(d) from Control d " +
            "where d.bControl is not null and d.bControl = true and d.resPerson.id = :respersonId and d.users.id = :userId and d.controlPeriod > :date")
    Integer countControlByResPersonAndUsersNotLate(@Param("respersonId") Long respersonId, @Param("userId") Long userId, @Param("date") Date date);



    @Query("select count(d) from Control d " +
            "where d.bControl is not null and d.bControl = true and d.resPerson.id = :respersonId " +
            "and d.users.id = :userId and d.controlPeriod > :firstDate and d.controlPeriod < :seconDate")
    Integer countControlByResPersonAndUsersDistance(@Param("respersonId") Long respersonId, @Param("userId") Long userId
            , @Param("firstDate") Date firstDate, @Param("seconDate") Date seconDate);
    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, nazorat kirishiga ega bo'lgan, berilgan bControl qiymatiga mos keladigan,
     * berilgan userId ga ega bo'lgan va nazorat davri berilgan sanadan katta bo'lgan Foydalanuvchilar sonini
     * olish uchun ishlatiladi.
     *
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param date1     nazorat davrini solishtirish uchun sana
     * @param date2     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan Foydalanuvchilar soni
     */
    @Query("select count(u) from Users u " +
            "join Control d on u.id = d.users.id " +
            "where d.bControl is not null " +
            "and d.bControl = :bControl " +
            "and u.id = :userId " +
            "and d.controlPeriod < :date1 " +
            "and d.controlPeriod > :date2")
    Integer countByBControlAndUsersAndDistance(
            @Param("bControl") Boolean bControl, @Param("userId") Long userId, @Param("date1") Date date1, @Param("date2") Date date2);


    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan vaqti o'tmagan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, nazorat kirishiga ega bo'lgan, berilgan bControl qiymatiga mos keladigan,
     * berilgan userId ga ega bo'lgan va nazorat davri berilgan sanadan kichik bo'lgan Foydalanuvchilar sonini
     * olish uchun ishlatiladi.
     *
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param date     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan vaqti o'tmagan Foydalanuvchilar soni
     */
    @Query("select count(u) from Users u " +
            "join Control d on u.id = d.users.id " +
            "where d.bControl is not null and d.bControl = :bControl and u.id = :userId and d.controlPeriod > :date")
    Integer countByBControlAndUsersAndNotLate(@Param("bControl") Boolean bControl, @Param("userId") Long userId, @Param("date") Date date);


    /**
     * Kirgan foydalanuvchidan bitta past hodim boshliqlarining barcha nazoratdan yechilgan hujjatlari soni
     * @param controlUserId controlga qo'ygan foydalanuvchi id raqami
     * @param chargerUserId nazoratga qo'yilgan foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) " +
            "from Control d " +
            "         join Charger c on d.id = c.control.id " +
            "where d.bControl is not null " +
            "  and d.bControl = false " +
            "  and d.users.id = :controlUserId " +
            "  and c.users.id = :chargerUserId")
    Integer countByDeletedBControlAndUsers(@Param("controlUserId") Long controlUserId, @Param("chargerUserId") Long chargerUserId);


    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan vaqti o'tgan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, Charger bilan bog'langan Foydalanuvchilarni hisoblaydi,
     * berilgan userId ga mos keladigan, berilgan bControl qiymatiga ega bo'lgan
     * va nazorat davri berilgan sanadan katta bo'lgan Foydalanuvchilar sonini olish uchun ishlatiladi.
     *
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param date     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan vaqti o'tgan Foydalanuvchilar soni
     */
    @Query("select count(u) " +
            "from Users u " +
            "         join Control d on u.id = d.users.id " +
            "         join Charger c on d.id = c.control.id " +
            "where c.users.id = :userId " +
            "  and d.bControl = :bControl" +
            "  and d.controlPeriod < :date")
    Integer countByChiefControlAndUsers_IdLate(@Param("userId") Long userId, @Param("bControl") Boolean bControl, @Param("date") Date date);


    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan vaqti o'tmagan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, Charger bilan bog'langan Foydalanuvchilarni hisoblaydi,
     * berilgan userId ga mos keladigan, berilgan bControl qiymatiga ega bo'lgan
     * va nazorat davri berilgan sanadan kichik bo'lgan Foydalanuvchilar sonini olish uchun ishlatiladi.
     *
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param date     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan vaqti o'tmagan Foydalanuvchilar soni
     */
    @Query("select count(u) " +
            "from Users u " +
            "         join Control d on u.id = d.users.id " +

            "         join Charger c on d.id = c.control.id " +
            "where c.users.id = :userId " +
            "  and d.bControl = :bControl" +
            "  and d.controlPeriod > :date")
    Integer countByChiefControlAndUsers_IdNotLate(@Param("userId") Long userId, @Param("bControl") Boolean bControl, @Param("date") Date date);


    /**
     * Muayyan nazorat shartiga bog'liq bo'lgan Foydalanuvchilar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, nazorat kirishiga ega bo'lgan, berilgan bControl qiymatiga mos keladigan,
     * berilgan userId ga ega bo'lgan va nazorat davri berilgan sanadan katta bo'lgan Foydalanuvchilar sonini
     * olish uchun ishlatiladi.
     *
     * @param bControl nazorat boolean qiymati, filtr uchun
     * @param userId   foydalanuvchining ID raqami, filtr uchun
     * @param date1     nazorat davrini solishtirish uchun sana
     * @param date2     nazorat davrini solishtirish uchun sana
     * @return berilgan mezonlarga mos keladigan Foydalanuvchilar soni
     */
    @Query("select count(u) " +
            "from Users u " +
            "         join Control d on u.id = d.users.id " +

            "         join Charger c on d.id = c.control.id " +
            "where c.users.id = :userId " +
            "  and d.bControl = :bControl" +
            "  and d.controlPeriod < :date1 " +
            "  and d.controlPeriod > :date2")
    Integer countByChiefControlAndUsers_IdDistance(
            @Param("bControl") Boolean bControl, @Param("userId") Long userId, @Param("date1") Date date1, @Param("date2") Date date2);

    /**
     * Berilgan sanada yaratilgan va berilgan foydalanuvchi ID raqami bo'lgan hujjatlar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, yaratilgan sanasi berilgan sanaga teng bo'lgan va
     * berilgan foydalanuvchi ID raqami bo'lgan hujjatlar sonini olish uchun ishlatiladi.
     *
     * @param createdDate yaratilgan sana, filtr uchun
     * @param userId      foydalanuvchining ID raqami, filtr uchun
     * @return berilgan mezonlarga mos keladigan hujjatlar soni
     */
    @Query("select count(d) from Control d " +
            "where d.createdDate is not null " +
            "  and d.createdDate = :createdDate " +
            "  and d.users.id = :userId")
    Integer countDocsByCreatedDateAndUserId(@Param("createdDate") Date createdDate, @Param("userId") Long userId);


    /**
     * Berilgan sanada yaratilgan va berilgan foydalanuvchi ID raqami bo'lgan hujjatlar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, yaratilgan sanasi berilgan sanaga teng bo'lgan va
     * berilgan foydalanuvchi ID raqami bo'lgan hujjatlar sonini olish uchun ishlatiladi.
     *
     * @param createdDate yaratilgan sana, filtr uchun
     * @param userId      foydalanuvchining ID raqami, filtr uchun
     * @return berilgan mezonlarga mos keladigan hujjatlar soni
     */
    @Query("select count(d) from Control d " +
            "join Charger c on d.id = c.control.id " +
            "where d.createdDate is not null " +
            "  and d.createdDate = :createdDate " +
            "  and c.users.id = :userId")
    Integer countChiefDocsByCreatedDateAndUserId(@Param("createdDate") Date createdDate, @Param("userId") Long userId);


    // Statistika

    /**
     * Berilgan sanada yaratilgan va berilgan mas'ul shaxs ID raqami bo'lgan hujjatlar sonini hisoblaydi.
     * <p>
     * Ushbu so'rov, yaratilgan sanasi berilgan sanaga teng bo'lgan va
     * berilgan mas'ul shaxs ID raqami bo'lgan hujjatlar sonini olish uchun ishlatiladi.
     *
     * @param createdDate yaratilgan sana, filtr uchun
     * @param resPersonId mas'ul shaxsning ID raqami, filtr uchun
     * @return berilgan mezonlarga mos keladigan hujjatlar soni
     */
    @Query("select count(d) from Control d " +
            "where d.createdDate is not null " +
            "  and d.createdDate = :createdDate " +
            "  and d.resPerson.id = :resPersonId")
    Integer countDocsByCreatedDateAndResPersonId(@Param("createdDate") Date createdDate, @Param("resPersonId") Long resPersonId);


    /**
     * Barcha nazoratlar soni
     * @param userId foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d where d.users.id = :userId")
    Integer countAllControlByUsers_Id(@Param("userId") Long userId);


    /**
     * Barcha nazoratga qo'yilgan va qo'yilmagan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d " +
            "where d.controlPeriod is not null and d.users.id = :userId")
    Integer countControlByUsers_IdAndBControlIsNotNull(@Param("userId") Long userId);


    /**
     * Barcha nazoratga qo'yilgan va qo'yilmagan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @param bControl nazoratga qo'yilgan va qo'yilmagan hujjatlar
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d " +
            "where d.controlPeriod is not null and d.users.id = :userId and d.bControl = :bControl")
    Integer countControlByUsers_IdAndBControlIsNotNullAndControlAndBControl(@Param("userId") Long userId, @Param("bControl") Boolean bControl);




    /**
     * Barcha nazoratga qo'yilmagan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d " +
            "where d.controlPeriod is null and d.users.id = :userId")
    Integer countControlByUsers_IdAndBControlIsNull(@Param("userId") Long userId);


    /**
     * Barcha nazoratga qo'yilgan va qo'yilmagan hujjatlar soni
     * @param chargerUserId nazoratga qo'yilgan foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) " +
            "from Control d " +
            "join Charger c on d.id = c.control.id " +
            "where c.users.id = :chargerUserId")
    Integer countAllChiefControlByChargerUserId(@Param("chargerUserId") Long chargerUserId);



    /**
     * Barcha rahbar tomonidan nazoratga qo'yilgan va qo'yilmagan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) " +
            "from Control d " +
            "join Charger c on d.id = c.control.id " +
            "where d.controlPeriod is not null " +
            "  and c.users.id = :userId")
    Integer countAllReturnedAndNotReturnedChiefControlByUserId(@Param("userId") Long userId);


    /**
     * Barcha rahbar tomonidan nazoratdan yewchilmagan yoki yechilgan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @param bControl nazoratga qo'yilgan yoki qo'yilmagan hujjatlar
     * @return soni qaytadi
     */
    @Query("select count(d) " +
            "from Control d " +
            "join Charger c on d.id = c.control.id " +
            "where d.bControl = :bControl " +
            "  and c.users.id = :userId")
    Integer countAllReturnedOrNotReturnedChiefControlByUserIdAndBControl(@Param("userId") Long userId, @Param("bControl") Boolean bControl);


    /**
     * Barcha rahbar tomonidan nazoratga qo'yilmagan hujjatlar soni
     * @param userId foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) " +
            "from Control d " +
            "join Charger c on d.id = c.control.id " +
            "where d.bControl is null " +
            "  and c.users.id = :userId")
    Integer countAllNotChiefControlByUserIdAndBControl(@Param("userId") Long userId);


    /**
     * Barcha o'zidan pastgi rahbarlar o'zini resolyutsiyaga qo'ygan nazoratlar soni
     * @param resPersonId resolyutsiya foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d where d.resPerson.id = :resPersonId and d.users.stage = :stage")
    Integer countAllChildControlByResPersonId(@Param("resPersonId") Long resPersonId, @Param("stage") Integer stage);


    /**
     * Barcha o'zidan pastgi rahbarlar o'zini resolyutsiyaga qo'ygan va qo'yilmagan nazoratlar soni
     * @param resPersonId resolyutsiya foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d where d.resPerson.id = :resPersonId and d.controlPeriod is not null and d.users.stage = :stage")
    Integer countAllChildControlByResPersonIdAndBControlIsNotNul(@Param("resPersonId") Long resPersonId, @Param("stage") Integer stage);



    /**
     * Barcha o'zidan pastgi rahbarlar o'zini resolyutsiyaga qo'ygan va qo'yilmagan nazoratlar soni
     * @param resPersonId resolyutsiya foydalanuvchi id raqami
     * @param bControl nazoratga qo'yilgan yoki qo'yilmagan hujjatlar
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d where d.resPerson.id = :resPersonId and d.bControl = :bControl and d.users.stage = :stage and d.controlPeriod is not null")
    Integer countAllChildControlByResPersonIdAndBControl(@Param("resPersonId") Long resPersonId, @Param("bControl") Boolean bControl, @Param("stage") Integer stage);

    /**
     * Barcha o'zidan pastgi rahbarlar o'zini resolyutsiyaga qo'yilmagan nazoratlar soni
     * @param resPersonId resolyutsiya foydalanuvchi id raqami
     * @return soni qaytadi
     */
    @Query("select count(d) from Control d where d.resPerson.id = :resPersonId and d.controlPeriod is null and d.users.stage = :stage")
    Integer countAllChildControlByResPersonIdAndBControlIsNull(@Param("resPersonId") Long resPersonId, @Param("stage") Integer stage);
}
