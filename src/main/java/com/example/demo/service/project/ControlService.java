package com.example.demo.service.project;

import com.example.demo.entity.Role;
import com.example.demo.entity.Users;
import com.example.demo.entity.enums.Huquq;
import com.example.demo.entity.project.Charger;
import com.example.demo.entity.project.Control;
import com.example.demo.helper.ConfigReader;
import com.example.demo.payload.ApiResult;
import com.example.demo.payload.project.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.project.ChargersRepository;
import com.example.demo.repository.project.ControlRepository;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTLine;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.*;
import java.math.BigInteger;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


@Service
public class ControlService {

    private final ControlRepository controlRepository;
    private final ChargersRepository chargersRepository;
    private final UserRepository userRepository;

    public ControlService(ControlRepository controlRepository, ChargersRepository chargersRepository, UserRepository userRepository) {
        this.controlRepository = controlRepository;
        this.chargersRepository = chargersRepository;
        this.userRepository = userRepository;
    }


    /**
     * yangi nazoratni shakllantirib bazaga yozuvchi metod
     *
     * @param controlDto  nazorat qiymatlarino olib keluvchi class
     * @param currentUser currentUser
     * @return muvaffaqqiyatli saqlangan yoki saqlanmaganligini qaytaradi
     */
    public ApiResult createControl(ControlDto controlDto, Users currentUser, Integer stage, Long controlId) {
        Set<Long> userSet = new HashSet<>();
        getChildren(userSet, currentUser.getFatherUsers());
        Control savedControl;
        if (stage == 1) {
            List<Control> controlList = controlRepository.findByUsers_IdOrderById(currentUser.getId());
            String blankNum;
            if (controlList.isEmpty()) {
                blankNum = autoIncrement("0");
            } else {
                blankNum = controlList.get(controlList.size() - 1).getBlankNum();
                blankNum = autoIncrement(blankNum);
            }
            Control control1 = new Control();
            control1.setBlankNum(blankNum);
            control1.setUsers(currentUser);
            control1.setComeDate(controlDto.getComeDate());
            if (controlDto.getB_control()) {
                if (controlDto.getControlPeriod() == null) {
                    return new ApiResult("Nazoratga qo'yilganlik belgilangandan so'ng" +
                            ", nazorat sa'nasi kiriritilishi shart", false);
                }
                control1.setControlPeriod(controlDto.getControlPeriod());
                control1.setBControl(controlDto.getB_control());
            }
            Optional<Users> optionalUsers = userRepository.findById(controlDto.getResPersonId());
            control1.setOtdName(controlDto.getOtdName());
            control1.setRegNum(controlDto.getRegNum());
            control1.setDocDate(controlDto.getDocDate());
            control1.setRegNumCome(controlDto.getRegNumCome());
            control1.setRegNumComeDate(controlDto.getRegNumComeDate());
            control1.setDocDate(controlDto.getDocDate());
            optionalUsers.ifPresent(control1::setResPerson);
            control1.setResDate(controlDto.getResDate());
            control1.setResolution(controlDto.getResolution());
            control1.setControllerPerson(controlDto.getControllerPerson());
            control1.setTel(controlDto.getTel());
            control1.setDocName(controlDto.getDoc_name());
            control1.setCreatedDate(new Date(System.currentTimeMillis()));
            savedControl = controlRepository.save(control1);
        } else {
            Optional<Control> optionalControl = controlRepository.findById(controlId);
            if (optionalControl.isEmpty()) {
                return new ApiResult("Bunday hujjat mavjud emas!", false);
            }
            savedControl = optionalControl.get();
        }
        Users users1 = null;
        List<ChargerDto> chargerList = controlDto.getChargerList();
        List<Charger> chargers = new ArrayList<>();
        for (int i = 0; i < chargerList.size(); i++) {
            if (chargerList.get(i).getUserId() != null) {
                Optional<Users> optionalUsers = userRepository.findById(chargerList.get(i).getUserId());
                if (optionalUsers.isEmpty()) {
                    return new ApiResult("Bunday foydalanuvchi mavjud emas!", false);
                }
                users1 = optionalUsers.get();
            }
            boolean chargeable = i == 0;
            Charger charger = new Charger(null, savedControl, users1, chargeable, false, stage, chargerList.get(i).getChargerName());
            charger = chargersRepository.save(charger);
            chargers.add(charger);
        }

        String error = add16(savedControl, chargers);
        if (error != null) {
            return new ApiResult(error, false);
        }
        return new ApiResult("Yangi nazorat muvaffaqiyatli kiritildi", true);
    }


    /**
     * nazoratga config.json faylidan olingan qiymatlarni qo'shish
     * @param savedControl nazorat
     * @param chargers     nazoratga qo'yilganlar
     * @return xatolik yuz berib qolgan bo'lsa uni qaytaradi, aks holda null qaytaradi
     */
    private String add16(Control savedControl, List<Charger> chargers) {
        if (savedControl.getResPerson().getStage() == 1) {
            Boolean isIgnore = ConfigReader.readConfigFileToBoolean("isIgnore");
            if (Boolean.TRUE.equals(isIgnore)) {
                List notIgnoreUserList = ConfigReader.readConfigFileToList("NotIgnoreUser");
                List ignoreUserList = ConfigReader.readConfigFileToList("IgnoreUser");
                if (notIgnoreUserList == null || ignoreUserList == null) {
                    return "Tizimda qandaydir xatolik yuz berdi.\nIltimos tizim administratorlariga murojaat qiling";
                }
                boolean ignoreUserBoolean = false;
                try {
                    for (Object ignoreUserObject : ignoreUserList) {
                        Double ignoreUserDouble = Double.parseDouble(ignoreUserObject.toString());
                        Optional<Users> optionalUsers = userRepository.findById(ignoreUserDouble.longValue());
                        if (optionalUsers.isEmpty()) {
                            return "Tizimdan tashqaridagi qiymat bazada mavjud eams.\nIltimos tizim administratorlariga murojaat qiling";
                        }
                        for (Charger charger : chargers) {
                            if (charger.getUsers().getId().equals(ignoreUserDouble.longValue())) {
                                ignoreUserBoolean = true;
                                break;
                            }
                        }
                        if (ignoreUserBoolean) {
                            break;
                        }
                    }
                    for (Object notIgnoreObject : notIgnoreUserList) {
                        Double notIgnoreDouble = Double.parseDouble(notIgnoreObject.toString());
                        if (chargers.stream().noneMatch(charger -> charger.getUsers().getId() == notIgnoreDouble.longValue()) && !ignoreUserBoolean) {
                            Optional<Users> optionalUsers = userRepository.findById(notIgnoreDouble.longValue());
                            if (optionalUsers.isEmpty()) {
                                return "Tizimdan tashqaridagi qiymat bazada mavjud eams.\nIltimos tizim administratorlariga murojaat qiling";
                            }
                            Users users2 = optionalUsers.get();
                            chargersRepository.save(new Charger(null, savedControl, users2, false, false, 1, users2.getChiefName()));
                        }
                    }
                } catch (Exception e) {
                    return "Tashqaridagi qiymatni yuklab olish formatida xatolik yuz berdi.\nIltimos tizim administratorlariga murojaat qiling";
                }
            } else {
                return "Tizimda qandaydir xatolik yuz berdi.\nIltimos tizim administratorlariga murojaat qiling";
            }
        }
        return null;
    }


    /**
     * blanka raqamini bittaga oshirib yana baza uchun to'rt xonali songa shakllantirib beruvchi metod
     *
     * @param strNum bittaga oshirilib, to'rt xonali songa shakllantirib beriluvchi qiymat
     * @return bittaga oshirilgan va to'rt xonali songa shakllantirilgan qiymat
     */
    private String autoIncrement(String strNum) {
        try {
            int num = Integer.parseInt(strNum);
            num++;
            strNum = String.valueOf(num);
            int length = strNum.length();
            if (length == 1) {
                strNum = "000" + strNum;
            } else if (length == 2) {
                strNum = "00" + strNum;
            } else if (length == 3) {
                strNum = "0" + strNum;
            }
            return strNum;
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private boolean getFatherChargerReturned(Long userId, Long controlId) {
        Optional<Charger> byControlIdAndUsersId = chargersRepository.findByControl_IdAndUsers_Id(controlId, userId);
        if (byControlIdAndUsersId.isPresent()) {
            Charger charger = byControlIdAndUsersId.get();
            if (!charger.getChargeable()) {
                return false;
            } else {
                Users fatherUsers = charger.getUsers().getFatherUsers();
                return getFatherChargerReturned(fatherUsers.getId(), controlId);
            }
        } else
            return true;
    }

    /**
     * currentUser ning bolalarini topadi
     *
     * @param userIds topilgan bolalarini id larini shu Set ga yig'adi
     * @param user    bolalrini topish uchun kerak bo'ladigan ota Id ya'ni currentUser
     */
    private void getChildren(Set<Long> userIds, Users user) {
        Long userId;
        try {
            userId = user.getId();
        } catch (Exception e) {
            userId = 3L;
        }
        for (Users users : userRepository.findByFatherUsers_Id(userId)) {
            userIds.add(users.getId());
        }
    }


    /**
     * nazoratga qo'yilishi uchun tavsiya etiladiganlar
     *
     * @param currentUser kirgan foydalanuvchi
     * @return nazoratga qo'yilishi uchun tavsiya etiladiganlar foydalanuvchilarni shakllantirib qaytaradi
     */
    public ApiResult getAllControlUsers(Users currentUser) {
        List<ChargerDto> dtoArrayList = new ArrayList<>();
        List<Users> sortUser2 = userRepository.findByStageOrderBySortUser(2);
        if (currentUser.getId().equals(2L)) {
            for (Users user : sortUser2) {
                dtoArrayList.add(new ChargerDto(null, user.getId(), user.getChiefName(), user.getBoldUser(), user.getSortUser()));
            }
            return new ApiResult("Tegishli barcha nazoratga qo'yilishi mumkin bo'lgan boshliqlar", true, dtoArrayList);
        }
        List<Users> sortUser = userRepository.findByStage(currentUser.getStage() + 1);
        if (currentUser.getStage() == 2) {
            sortUser2.addAll(sortUser);
            sortUser = sortUser2;
        }
        List<Users> sortUsersFirst = new ArrayList<>();
        List<Users> sortUsersSecond = new ArrayList<>();

        for (Users user : sortUser) {
            if (Objects.equals(user.getFatherUsers().getId(), currentUser.getId())) {
                sortUsersFirst.add(user);
            } else {
                sortUsersSecond.add(user);
            }
        }
        sortUser.clear();
        sortUser.addAll(sortUsersFirst);
        sortUser.addAll(sortUsersSecond);

        for (Users users : sortUser) {
            if (users.getFatherUsers().getId().equals(currentUser.getId()))
                dtoArrayList.add(new ChargerDto(null, users.getId(), users.getChiefName(), true, users.getSortUser()));
            else
                dtoArrayList.add(new ChargerDto(null, users.getId(), users.getChiefName(), false, users.getSortUser()));
        }
        return new ApiResult("Tegishli barcha nazoratga qo'yilishi mumkin bo'lgan boshliqlar", true, dtoArrayList);
    }

    public String getName(Users users) {
        String patronym;
        try {
            patronym = users.getPatronym().substring(0, 1).toUpperCase() + ".";
        } catch (NullPointerException e) {
            patronym = "";
        }
        return users.getFirstName().substring(0, 1).toUpperCase() + "." + patronym + users.getLastName();
    }


    /**
     * Hujjat qaytarilganqanda uni hisobga ilib qo'yish metodi
     *
     * @param currentUser  kirgan foydalanuvchi
     * @param receptionDto qaytariligan hujjat qiymatlari
     * @return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    public ApiResult returnToReception(Users currentUser, ReceptionDto receptionDto) {
        Optional<Control> optionalControl = controlRepository.findById(receptionDto.getIdControl());
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday hujjat mavjud emas!", false);
        }
        Optional<Charger> optionalCharger = chargersRepository.findByControl_IdAndUsers_Id(receptionDto.getIdControl(), currentUser.getId());
        if (optionalCharger.isEmpty()) {
            if (!Objects.equals(currentUser.getId(), optionalControl.get().getUsers().getId())) {
                return new ApiResult("Sizda bu hujjatni qaytarib olish huquqi mavjud emas", false);
            }
        }
        Control control = optionalControl.get();
        control.setReceptionDate(receptionDto.getReception_date());
        control.setExecutionRegNum(receptionDto.getExecutionRegNum());
        control.setExecutionDate(receptionDto.getExecutionDate());
        control.setExecutorTel(receptionDto.getExecutorTel());
        control.setWorkDone(receptionDto.getWorkDone());
        control.setReturned(true);
        control.setWorkbookNum(receptionDto.getWorkbookNum());
        control.setWorkbookPageNum(receptionDto.getWorkbookPageNum());
        controlRepository.save(control);
        return new ApiResult("Hujjat qaytarilganligi hisobga olindi", true);
    }


    /**
     * Hujjatni qaytarib olishni bekor qilish
     *
     * @param currentUser kirgan foydalanuvchi
     * @return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    public ApiResult refuseReturnToReception(Users currentUser, Long controlId) {
        Optional<Control> optionalControl = controlRepository.findById(controlId);
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday hujjat mavjud emas!", false);
        }
        Optional<Charger> optionalCharger = chargersRepository.findByControl_IdAndUsers_Id(controlId, currentUser.getId());
        if (optionalCharger.isEmpty()) {
            if (!Objects.equals(currentUser.getId(), optionalControl.get().getUsers().getId())) {
                return new ApiResult("Sizda bu hujjatni qaytarib olishni bekor qilish huquqi mavjud emas", false);
            }
        }
        Control control = optionalControl.get();
        control.setReceptionDate(null);
        control.setExecutionRegNum(null);
        control.setExecutionDate(null);
        control.setExecutorTel(null);
        control.setWorkDone(null);
        control.setReturned(false);
        control.setWorkbookNum(null);
        control.setWorkbookPageNum(null);
        controlRepository.save(control);
        return new ApiResult("Hujjat qaytarilganligi hisobga olindi", true);
    }


    /**
     * hujjatni nazoratdan yechish metodi
     *
     * @param currentUser   kirgan foydalanuvchi
     * @param outControlDto nazoratdan yechish qiymatlari
     * return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    public ApiResult deleteControl(Users currentUser, OutControlDto outControlDto) {
        Role role = currentUser.getRole();
        List<Huquq> huquqList = role.getHuquqList();
        if (!huquqList.contains(Huquq.DELETE_CONTROL)) {
            return new ApiResult("Sizda ushbu hujjatni nazoratdan yechish huquqi mavjud emas!", false);
        }
        Optional<Control> optionalControl = controlRepository.findById(outControlDto.getIdControl());
        if (optionalControl.isEmpty()) {
            return new ApiResult("Ushbu hujjat topilmadi!", false);
        }
        Control control = optionalControl.get();
        if (!control.getBControl()) {
            return new ApiResult("Ushbu hujjat nazoratda yo'q!", false);
        }
        control.setOutControllerId(currentUser.getId());
        control.setBControl(false);
        control.setReasonOutControl(outControlDto.getReturnReason());
        controlRepository.save(control);
        return new ApiResult("Ushbu hujjat nazoratdan yechildi", true);
    }


    /**
     * kelgan nazoratni yana kimgadir biriktirish
     *
     * @param controlId   nazorat id raqami
     * @param chargerDtos nazoratga qo'yilganlar
     * @param currentUser kirgan foydalanuvchi
     */
    public ApiResult addControl(Long controlId, List<ChargerDto> chargerDtos, Users currentUser) {
        Optional<Control> optionalControl = controlRepository.findById(controlId);
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday hujjat mavjud emas", false);
        }

        int stage;
        Optional<Charger> optionalChargerCurrentUser = chargersRepository.findByControl_IdAndUsers_Id(controlId, currentUser.getId());
        if (optionalChargerCurrentUser.isPresent()) {
            stage = optionalChargerCurrentUser.get().getStage();
            stage++;
        } else stage = 1;
        List<Charger> chargerList = new ArrayList<>();
        int counter = 0;
        boolean chargeable;
        for (ChargerDto chargerDto : chargerDtos) {
            counter++;
            Optional<Charger> optionalCharger = chargersRepository.findByControl_IdAndUsers_Id(controlId, chargerDto.getUserId());
            if (optionalCharger.isPresent()) {
                return new ApiResult("Bunday nazoratni avval biriktirgansiz", false);
            }
            Optional<Users> optionalUsers = userRepository.findById(chargerDto.getUserId());
            if (optionalUsers.isEmpty()) {
                return new ApiResult("Bunday foydalanuvchi mavjudf emas!", false);
            }
            chargeable = counter == 1;
            chargerList.add(new Charger(null, optionalControl.get(), optionalUsers.get(), chargeable, false, stage, chargerDto.getChargerName()));
        }
        chargersRepository.saveAll(chargerList);

        return new ApiResult("Ushbu hujjatga yangi resolyutsiya yaratildi!", true);
    }


    /**
     * yangi nazoratlar bor yoki yo'qligini kuratib turadi
     *
     * @param currentUser kirgan foydalanuvchi
     * @return agar nazorat da yangi paydo bo'lgan bo'lsa true ak holda false qaytaradi
     */
    public ApiResult monitoringControl(Users currentUser) {
        List<Control> controlList = controlRepository.findByReturnedAndSeen(true, false);
        Set<Long> controlSet = new HashSet<>();
        getChildren(controlSet, currentUser);
        Set<MonitoringDto> monitoringDtoSet = new HashSet<>();
        for (Control control : controlList) {
            for (Long id : controlSet) {
                Optional<Charger> optionalCharger = chargersRepository.findByControl_IdAndUsers_Id(control.getId(), id);
                optionalCharger.ifPresent(charger -> monitoringDtoSet.add(new MonitoringDto(control.getId(), control.getRegNum())));
            }
        }
        return new ApiResult("Barcha Qaytarilgan hujjatlar", true, monitoringDtoSet, monitoringDtoSet.size());
    }


    /**
     * bitta nazorat qiymatlarini olish
     *
     * @param controlId   nazorat id raqami
     * @param currentUser kirgan foydalanuvchi
     */
    public ApiResult getOneControl(Long controlId, Users currentUser) {
        Optional<Control> controlOptional = controlRepository.findById(controlId);
        if (controlOptional.isEmpty()) {
            return new ApiResult("Bunday hujjat mavjud emas", false);
        }
        Control control = controlOptional.get();
        if (!Objects.equals(control.getUsers().getId(), currentUser.getId())) {
            return new ApiResult("Bunday hujjat sizga tegishli emas", false);
        }
        return new ApiResult("Bitta nazorat", true, control);
    }


    /**
     * nazorat kartasini chop etish
     *
     * @param controlId nazorat id raqami
     * @param response  fayl uzatish
     * @return fayl ko'rinishida qaytadi
     */
    public ApiResult getReport(Long controlId, HttpServletResponse response) {
        try {
            Optional<Control> optionalControl = controlRepository.findById(controlId);
            if (optionalControl.isEmpty()) {
                return null;
            }
            Control control = optionalControl.get();
            File file = new File("hisobot.docx");
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument doc = new XWPFDocument(fis);
            XWPFTable table = doc.createTable(1, 3);
            table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(6110));
            table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 16, 0, "FFFFFF");
            table.getCTTbl().getTblPr().getTblBorders().getLeft().setVal(STBorder.NONE);
            table.getCTTbl().getTblPr().getTblBorders().getRight().setVal(STBorder.NONE);
            table.getCTTbl().getTblPr().getTblBorders().getTop().setVal(STBorder.NONE);
            table.getCTTbl().getTblPr().getTblBorders().getBottom().setVal(STBorder.NONE);
            XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
            XWPFRun r1 = p1.createRun();
            r1.addBreak();
            r1.setBold(true);
            r1.setText("                       ");
            r1.setUnderline(UnderlinePatterns.SINGLE);
            XWPFRun r1_1 = p1.createRun();
            r1_1.setText("                .");
            r1_1.setColor("ffffff");

            XWPFRun r1_2 = p1.createRun();
            r1_2.setText("       (Nazoratdan yechilganlik");
            r1_2.addBreak();
            r1_2.setFontSize(5);

            XWPFRun r1_3 = p1.createRun();
            r1_3.setText("               haqida belgi)");
            r1_3.setFontSize(5);

            table.getRow(0).getCell(2).setText("");
            XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
            XWPFRun run2 = p2.createRun();
            p2.setAlignment(ParagraphAlignment.CENTER);
            p2.setSpacingBefore(150);
            p2.setSpacingAfter(0);

            XWPFParagraph p3 = table.getRow(0).getCell(2).getParagraphs().get(0);
            XWPFRun run3 = p3.createRun();
            run3.setBold(true);
            run3.setFontFamily("Times New Roman");
            p3.setAlignment(ParagraphAlignment.RIGHT);
            p3.setSpacingBefore(0);
            run3.setFontSize(14);
            p3.setSpacingBetween(1);
            p3.setSpacingAfterLines(1);
            p3.setSpacingAfter(0);
            run3.setText("Qaytarilishi     majburiy");
            run3.setUnderline(UnderlinePatterns.SINGLE);
            XWPFRun run = p1.createRun();
            run.setBold(true);

            File img = new File("gerb.png");
            FileInputStream imageData = new FileInputStream(img);
            int imgType = XWPFDocument.PICTURE_TYPE_PNG;
            String imageName = img.getName();
            int width = 60;
            int height = 60;
            run2.addPicture(imageData, imgType, imageName, Units.toEMU(width), Units.toEMU(height));

            XWPFParagraph paragraphHeaderCenter = doc.createParagraph();
            XWPFRun runHeaderCenter = paragraphHeaderCenter.createRun();

            runHeaderCenter.setText(control.getResPerson().getFirstReport());
            String secondReport = control.getResPerson().getSecondReport();
            if (secondReport != null) {
                runHeaderCenter.addBreak();
                runHeaderCenter.setText(secondReport);
            }
            paragraphHeaderCenter.setAlignment(ParagraphAlignment.CENTER);
            runHeaderCenter.setFontFamily("Times New Roman");
            runHeaderCenter.setFontSize(14);
            runHeaderCenter.setBold(true);
            paragraphHeaderCenter.setSpacingAfter(0);

            CTGroup ctGroupLine = CTGroup.Factory.newInstance();
            CTLine ctLine1 = ctGroupLine.addNewLine();
            ctLine1.setStyle("left:0pt;width:300pt;height:0pt;");
            ctLine1.setStrokeweight("0.5pt");
            Node ctGroupNodeLine = ctGroupLine.getDomNode();
            CTPicture ctPictureLine = CTPicture.Factory.parse(ctGroupNodeLine);
            XWPFParagraph paragraphLine = doc.createParagraph();
            XWPFRun runLine = paragraphLine.createRun();
            CTR cTRLine = runLine.getCTR();
            runLine.setFontSize(14);
            runLine.setFontFamily("Times New Roman");
            cTRLine.addNewPict();
            cTRLine.setPictArray(0, ctPictureLine);
            spacing(paragraphLine);

            XWPFTable tableMain = doc.createTable(1, 2);
            tableMain.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(5800));
            tableMain.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 16, 0, "FFFFFF");
            tableMain.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 16, 0, "FFFFFF");
            tableMain.getCTTbl().getTblPr().getTblBorders().getLeft().setVal(STBorder.NONE);
            tableMain.getCTTbl().getTblPr().getTblBorders().getRight().setVal(STBorder.NONE);
            tableMain.getCTTbl().getTblPr().getTblBorders().getTop().setVal(STBorder.NONE);
            tableMain.getCTTbl().getTblPr().getTblBorders().getBottom().setVal(STBorder.NONE);
            XWPFParagraph p1Main = tableMain.getRow(0).getCell(0).getParagraphs().get(0);
            p1Main.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r1Main = p1Main.createRun();
            r1Main.setText("   NAZORAT KARTOCHKASI    №");
            spacing(p1Main);
            r1Main.setBold(true);
            r1Main.setFontSize(16);
            r1Main.setFontFamily("Bahnschrift SemiLight");
            XWPFParagraph p2Main = tableMain.getRow(0).getCell(1).getParagraphs().get(0);
            XWPFRun r2Main = p2Main.createRun();
            r2Main.setText(control.getBlankNum());
            spacing(p2Main);
            r2Main.setUnderline(UnderlinePatterns.SINGLE);
            r2Main.setBold(true);
            r2Main.setFontSize(16);
            r2Main.setFontFamily("Bahnschrift SemiLight");
            r2Main.setColor("0070C0");

            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.BOTH);
            XWPFRun runNewParagraph = paragraph.createRun();
            spacing(p2Main);
            runNewParagraph.setUnderline(UnderlinePatterns.SINGLE);
            runNewParagraph.setFontSize(5);
            runNewParagraph.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph.addBreak();

            XWPFRun runNewParagraph0 = paragraph.createRun();
            runNewParagraph0.setText("Maxsus          ");
            runNewParagraph0.setBold(true);
            runNewParagraph0.setColor("ff0000");
            runNewParagraph0.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph0.setFontSize(16);

            XWPFRun runNewParagraph1 = paragraph.createRun();
            runNewParagraph1.setText("Bajarish muddati:");
            runNewParagraph1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph1.setBold(true);
            runNewParagraph1.setFontSize(12);
            runNewParagraph1.setUnderline(UnderlinePatterns.SINGLE);

            XWPFRun runNewParagraph1_1 = paragraph.createRun();
            runNewParagraph1_1.setFontFamily("Bahnschrift SemiLight");

            runNewParagraph1_1.setColor("ff0000");
            runNewParagraph1_1.setFontSize(12);
            runNewParagraph1_1.setBold(true);
            String controlPeriod = format(control.getControlPeriod());
            if (controlPeriod.isEmpty()) {
                runNewParagraph1_1.setText("      ____________________");
            } else
                runNewParagraph1_1.setText("      " + controlPeriod);
            runNewParagraph1_1.addBreak();
            runNewParagraph1_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph1_1.setFontSize(12);
            XWPFRun runNewParagraph2 = paragraph.createRun();
            runNewParagraph2.setText("nazorat          ");
            runNewParagraph2.setColor("ff0000");
            runNewParagraph2.setBold(true);
            runNewParagraph2.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph2.setFontSize(16);
            XWPFRun runNewParagraph3 = paragraph.createRun();
            runNewParagraph3.setBold(true);
            runNewParagraph3.setText("Uzaytirilgan vaqti:");
            runNewParagraph3.setUnderline(UnderlinePatterns.SINGLE);
            XWPFRun runNewParagraph3_1 = paragraph.createRun();
            runNewParagraph3_1.setText("    ____________");
            runNewParagraph3_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph3_1.setFontSize(12);
            runNewParagraph3.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph3.setFontSize(12);
            XWPFRun runNewParagraph4 = paragraph.createRun();
            runNewParagraph4.addBreak();
            runNewParagraph4.setText("                                                   ");
            XWPFRun runNewParagraph4_0 = paragraph.createRun();
            runNewParagraph4_0.setText("Bajarilgan vaqti:");
            runNewParagraph4_0.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph4_0.setFontSize(12);
            runNewParagraph4_0.setUnderline(UnderlinePatterns.SINGLE);
            runNewParagraph4_0.setBold(true);
            XWPFRun runNewParagraph4_1 = paragraph.createRun();
            String executionDate = format(control.getExecutionDate());
            if (!executionDate.isEmpty()) {
                runNewParagraph4_1.setBold(true);
                runNewParagraph4_1.setColor("ff0000");
                runNewParagraph4_1.setText("        " + executionDate);
            } else {
                runNewParagraph4_1.setText("    ____________");
            }
            runNewParagraph4_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph4_1.setFontSize(12);
            runNewParagraph4_0.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph4_0.setFontSize(12);
            XWPFRun runNewParagraph5 = paragraph.createRun();
            runNewParagraph5.addBreak();
            runNewParagraph5.addBreak();
            runNewParagraph5.setFontSize(12);
            runNewParagraph5.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph5.setText("Hujjat qayerdan kelgan: ");
            runNewParagraph5.setBold(true);
            runNewParagraph5.setUnderline(UnderlinePatterns.SINGLE);
            paragraph.setSpacingAfter(0);
            XWPFRun runNewParagraph5_1 = paragraph.createRun();
            runNewParagraph5_1.setFontSize(12);
            runNewParagraph5_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph5_1.setText(control.getOtdName());
            paragraph.setSpacingAfter(0);

            XWPFTable tableSecond = doc.createTable(2, 2);
            tableSecond.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(5800));
            tableSecond.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 16, 0, "FFFFFF");
            tableSecond.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 16, 0, "FFFFFF");
            tableSecond.getCTTbl().getTblPr().getTblBorders().getLeft().setVal(STBorder.NONE);
            tableSecond.getCTTbl().getTblPr().getTblBorders().getRight().setVal(STBorder.NONE);
            tableSecond.getCTTbl().getTblPr().getTblBorders().getTop().setVal(STBorder.NONE);
            tableSecond.getCTTbl().getTblPr().getTblBorders().getBottom().setVal(STBorder.NONE);

            XWPFParagraph p1Second1_1 = tableSecond.getRow(0).getCell(0).getParagraphs().get(0);
            XWPFRun r1Second1_1 = p1Second1_1.createRun();
            r1Second1_1.setText("Qayd raqami: ");
            r1Second1_1.setUnderline(UnderlinePatterns.SINGLE);
            r1Second1_1.setBold(true);
            r1Second1_1.setFontSize(12);
            r1Second1_1.setFontFamily("Bahnschrift SemiLight");
            spacing(p1Second1_1);
            XWPFRun r1Second1_1_1 = p1Second1_1.createRun();
            r1Second1_1_1.setText(control.getRegNum());
            spacing(p1Second1_1);
            r1Second1_1_1.setFontSize(12);
            r1Second1_1_1.setFontFamily("Bahnschrift SemiLight");

            XWPFParagraph p1Second1_2 = tableSecond.getRow(0).getCell(1).getParagraphs().get(0);
            XWPFRun r1Second1_2 = p1Second1_2.createRun();
            r1Second1_2.setText("Kun:");
            r1Second1_2.setUnderline(UnderlinePatterns.SINGLE);
            r1Second1_2.setBold(true);
            spacing(p1Second1_2);
            r1Second1_2.setFontSize(12);
            r1Second1_2.setFontFamily("Bahnschrift SemiLight");
            XWPFRun r1Second1_2_1 = p1Second1_2.createRun();
            String docDate = format(control.getDocDate());
            if (docDate.isEmpty())
                r1Second1_2_1.setText("____________________");
            else
                r1Second1_2_1.setText("  " + docDate);
            spacing(p1Second1_2);
            r1Second1_2_1.setFontSize(12);
            r1Second1_2_1.setFontFamily("Bahnschrift SemiLight");

            XWPFParagraph p1Second2_1 = tableSecond.getRow(1).getCell(0).getParagraphs().get(0);
            XWPFRun r1Second2_1 = p1Second2_1.createRun();
            r1Second2_1.setText("Qayd raqam kel: ");
            r1Second2_1.setBold(true);
            r1Second2_1.setUnderline(UnderlinePatterns.SINGLE);
            spacing(p1Second2_1);
            r1Second2_1.setFontSize(12);
            r1Second2_1.setFontFamily("Bahnschrift SemiLight");
            XWPFRun r1Second2_1_1 = p1Second2_1.createRun();
            if (control.getRegNumCome() == null) {
                r1Second2_1_1.setText("__________");
            } else
                r1Second2_1_1.setText(control.getRegNumCome());
            spacing(p1Second2_1);
            r1Second2_1_1.setFontSize(12);
            r1Second2_1_1.setFontFamily("Bahnschrift SemiLight");

            XWPFParagraph p1Second2_2 = tableSecond.getRow(1).getCell(1).getParagraphs().get(0);
            XWPFRun r1Second2_2 = p1Second2_2.createRun();
            r1Second2_2.setText("Kun:");
            r1Second2_2.setBold(true);
            r1Second2_2.setUnderline(UnderlinePatterns.SINGLE);
            spacing(p1Second2_2);
            r1Second2_2.setFontSize(12);
            r1Second2_2.setFontFamily("Bahnschrift SemiLight");
            XWPFRun r1Second2_2_1 = p1Second2_2.createRun();
            String regNumComDate = format(control.getRegNumComeDate());
            if (regNumComDate.isEmpty()) {
                r1Second2_2_1.setText("_____________");
            } else
                r1Second2_2_1.setText("  " + regNumComDate);
            spacing(p1Second2_2);
            r1Second2_2_1.setFontSize(12);
            r1Second2_2_1.setFontFamily("Bahnschrift SemiLight");

            XWPFParagraph paragraph6 = doc.createParagraph();
            paragraph6.setSpacingAfter(0);
            XWPFRun runNewParagraph6 = paragraph6.createRun();
            paragraph6.setAlignment(ParagraphAlignment.CENTER);
            runNewParagraph6.setFontSize(12);
            runNewParagraph6.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph6.setText("Hujjatning qisqacha mazmuni:");
            runNewParagraph6.setBold(true);
            runNewParagraph6.setUnderline(UnderlinePatterns.SINGLE);

            XWPFParagraph paragraph7 = doc.createParagraph();
            paragraph7.setSpacingAfter(0);
            XWPFRun runNewParagraph7 = paragraph7.createRun();
            runNewParagraph7.setFontSize(12);
            runNewParagraph7.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph7.setText("\t" + control.getDocName());
            runNewParagraph7.addBreak();

            XWPFParagraph paragraph7_0 = doc.createParagraph();
            XWPFRun runNewParagraph7_0 = paragraph7_0.createRun();
            runNewParagraph7_0.setText("Topshiriq beruvchi:  ");
            runNewParagraph7_0.setBold(true);
            runNewParagraph7_0.setFontSize(12);
            runNewParagraph7_0.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph7_0.setUnderline(UnderlinePatterns.SINGLE);
            XWPFRun runNewParagraph7_1 = paragraph7_0.createRun();
            runNewParagraph7_1.setFontSize(12);
            runNewParagraph7_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph7_1.setText(control.getResPerson().getChiefName());
            runNewParagraph7_1.setColor("ff0000");
            runNewParagraph7_1.addBreak();

            XWPFRun runNewParagraph8 = paragraph7_0.createRun();
            runNewParagraph8.setFontSize(12);
            runNewParagraph8.setBold(true);
            runNewParagraph8.setText("Kimga yuklatilgan: ");
            runNewParagraph8.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph8.setUnderline(UnderlinePatterns.SINGLE);
            XWPFRun runNewParagraph8_1 = paragraph7_0.createRun();
            runNewParagraph8_1.setFontSize(12);
            runNewParagraph8_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph8_1.setColor("ff0000");
            List<Charger> listChargers = chargersRepository.findByControl_IdOrderById(controlId);
            if (!listChargers.isEmpty())
                if (listChargers.size() == 1) {
                    runNewParagraph8_1.setText(listChargers.get(0).getChargerName());
                } else {
                    for (int i = 0; i < listChargers.size() - 1; i++) {
                        runNewParagraph8_1.setText(listChargers.get(i).getChargerName() + ", ");
                    }
                    runNewParagraph8_1.setText(listChargers.get(listChargers.size() - 1).getChargerName());
                }
            runNewParagraph8_1.addBreak();

            XWPFRun runNewParagraph9 = paragraph7_0.createRun();
            runNewParagraph9.setText("Vazifa: ");
            runNewParagraph9.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph9.setBold(true);
            runNewParagraph9.setFontSize(12);

            runNewParagraph9.setUnderline(UnderlinePatterns.SINGLE);

            XWPFRun runNewParagraph9_1 = paragraph7_0.createRun();
            runNewParagraph9_1.setText(control.getResolution());
            runNewParagraph9_1.addBreak();
            runNewParagraph9_1.setFontSize(12);
            runNewParagraph9_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph9_1.addBreak(BreakType.PAGE);
            XWPFRun runNewParagraph10 = paragraph7_0.createRun();
            runNewParagraph10.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph10.setText("Ijro natijalari:");
            runNewParagraph10.setFontSize(12);
            runNewParagraph10.setUnderline(UnderlinePatterns.SINGLE);
            runNewParagraph10.setBold(true);
            XWPFRun runNewParagraph10_0 = paragraph7_0.createRun();
            runNewParagraph10_0.setText("_______________________________________");
            runNewParagraph10_0.addBreak();
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.setText("_________________________________________________________________________________________________________________________________________________________");
            runNewParagraph10_0.addBreak();
            runNewParagraph10_0.setFontSize(12);

            XWPFRun runNewParagraph10_01 = paragraph7_0.createRun();
            runNewParagraph10_01.setText("Ijrochi FIO:                                             Imzo:                      .");
            runNewParagraph10_01.setUnderline(UnderlinePatterns.SINGLE);
            runNewParagraph10_01.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph10_01.setFontSize(12);
            runNewParagraph10_01.addBreak();
            runNewParagraph10_01.addBreak();
            runNewParagraph10_01.setBold(true);

            runNewParagraph10_01.setText("Nazoratchi shaxs: ");
            XWPFRun runNewParagraph10_1 = paragraph7_0.createRun();
            runNewParagraph10_1.setText(control.getControllerPerson());
            runNewParagraph10_1.setText(" " + control.getTel());
            runNewParagraph10_1.setFontFamily("Bahnschrift SemiLight");
            runNewParagraph10_1.setFontSize(12);
            runNewParagraph10_1.addBreak();
            XWPFRun runNewParagraph10_4 = paragraph7_0.createRun();
            runNewParagraph10_4.addBreak();
            runNewParagraph10_4.setText(format(control.getComeDate()) + "    _______________");
            runNewParagraph10_4.setFontFamily("Bahnschrift SemiLight");

            CTDocument1 ctDocument = doc.getDocument();
            CTBody ctBody = ctDocument.getBody();
            CTSectPr ctSectPr = (ctBody.isSetSectPr()) ? ctBody.getSectPr() : ctBody.addNewSectPr();
            CTPageSz ctPageSz = (ctSectPr.isSetPgSz()) ? ctSectPr.getPgSz() : ctSectPr.addNewPgSz();
            ctPageSz.setOrient(STPageOrientation.PORTRAIT);

            ctPageSz.setW(BigInteger.valueOf(8390));
            ctPageSz.setH(BigInteger.valueOf(11905));
            CTPageMar pageMar = (ctSectPr.isSetPgMar()) ? ctSectPr.getPgMar() : ctSectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1420L)); // Chap margin
            pageMar.setRight(BigInteger.valueOf(850L)); // O'ng margin
            pageMar.setTop(BigInteger.valueOf(400L));   // Yuqori margin
            pageMar.setBottom(BigInteger.valueOf(1160L)); // Pastki margin
            pageMar.setGutter(BigInteger.valueOf(0L)); // Bog'lash uchun joy (kerak bo'lmasa 0 qiling)
            CTTblLayoutType layoutType = CTTblLayoutType.Factory.newInstance();
            layoutType.setType(STTblLayoutType.FIXED);
            ctSectPr.addNewTitlePg(); // Zerkalniy uchun kerak bo'lgan atribut
            response.setHeader("Content-Disposition", "attachment; filename=hisobot.docx");

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                doc.write(byteArrayOutputStream);
                response.setContentLength(byteArrayOutputStream.size());

                try (OutputStream responseOutputStream = response.getOutputStream()) {
                    byteArrayOutputStream.writeTo(responseOutputStream);
                }
            }
            return new ApiResult("Fayl muvaffaqiyatli yuklandi!", true);
        } catch (IOException | InvalidFormatException | XmlException exception) {

            return new ApiResult(exception.getMessage(), false);
        }

    }


    private String format(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }

    private void spacing(XWPFParagraph xwpfParagraph) {
        CTPPr ppr = xwpfParagraph.getCTP().getPPr();
        if (ppr == null) ppr = xwpfParagraph.getCTP().addNewPPr();
        CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
        spacing.setAfter(BigInteger.valueOf(0));
        spacing.setBefore(BigInteger.valueOf(0));
        spacing.setLineRule(STLineSpacingRule.AUTO);
        spacing.setLine(BigInteger.valueOf(240));
    }

    public ApiResult seen(Long controlId) {
        Optional<Control> optionalControl = controlRepository.findById(controlId);
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday hujjat mavjud emas!", false);
        }
        Control control = optionalControl.get();
        control.setSeen(true);
        controlRepository.save(control);
        return new ApiResult("Hujjat ko'rildi!", true);
    }

    public ApiResult getFatherUsers(Users currentUser) {
        List<FatherUser> fatherUsers = new ArrayList<>();
        fatherUsers.add(new FatherUser(currentUser.getId(), currentUser.getChiefName()));
        if (currentUser.getFatherUsers() != null)
            fatherUsers.add(new FatherUser(currentUser.getFatherUsers().getId(), currentUser.getFatherUsers().getChiefName()));
        return new ApiResult("success", true, fatherUsers);
    }

    public ApiResult getAllControls(Users currentUser, Integer sortNum, Long userId) {
        if (sortNum == 0) {
            return getMeinControllers(currentUser);
        } else if (sortNum == 1) {
            return getChildrenControllers(currentUser);
        } else if (sortNum == 2) {
            return getChefControllers(currentUser, userId);
        } else {
            return new ApiResult("error", false);
        }
    }

    private ApiResult getChildrenControllers(Users currentUser) {
        List<Control> controlList = controlRepository.findDocsByChargerUserIdDesc(currentUser.getId());
        sortListControl(controlList);
        return allControllers(controlList, currentUser);
    }

    /**
     * qo'l ostidagi hodimlarning kirgan foydalanuvchi nomidan yaratgan nazoratlari
     *
     * @param currentUser firgan foydalanuvchi
     * @param userId      tanlangan foydalanuvchi
     * @return ApiResult
     */
    private ApiResult getChefControllers(Users currentUser, Long userId) {
        List<Control> usersIsNotOrderById;
        if (userId == 0) {
            usersIsNotOrderById = controlRepository.findByResPersonAndUsersIsNotOrderByIdDesc(currentUser, currentUser);
        } else {
            usersIsNotOrderById = controlRepository.findByResPersonAndUsersIsNotAndUsers_IdOrderByIdDesc(currentUser, currentUser, userId);
        }
        sortListControl(usersIsNotOrderById);
        return allControllers(usersIsNotOrderById, currentUser);
    }

    private ApiResult getMeinControllers(Users currentUser) {
        List<Control> orderByIdDesc = controlRepository.findByUsersOrderByIdDesc(currentUser);
        sortListControl(orderByIdDesc);
        return allControllers(orderByIdDesc, currentUser);
    }

    private ApiResult allControllers(List<Control> controlSet, Users users) {
        List<ControlResponce> controlSetResponce = new ArrayList<>();
        for (Control control : controlSet) {
            List<Charger> chargerList = chargersRepository.findByControl_IdOrderById(control.getId());
            String mainCharger = "";
            List<ChargerDto> chargerDtoList = new ArrayList<>();
            for (Charger charger : chargerList) {
                chargerDtoList.add(new ChargerDto(charger.getId(), charger.getUsers().getId(), charger.getChargerName(), null, null));
            }
            String outController = "";
            Long outControllerId = control.getOutControllerId();
            if (outControllerId != null) {
                Optional<Users> optionalUsers = userRepository.findById(outControllerId);
                if (optionalUsers.isPresent()) {
                    outController = getName(optionalUsers.get());
                }
            }
            if (!chargerDtoList.isEmpty()) {
                mainCharger = chargerDtoList.get(0).getChargerName();
            }
            boolean returned;
            if (control.getReturned())
                returned = false;
            else
                returned = getFatherChargerReturned(users.getId(), control.getId());
            String createPerson = getName(control.getUsers());
            controlSetResponce.add(new ControlResponce(users.getId(), control.getId(), control.getComeDate(), control.getControlPeriod()
                    , control.getBlankNum(), control.getBControl(), control.getOtdName(), control.getRegNum(), control.getDocDate()
                    , control.getRegNumCome(), control.getRegNumComeDate(), control.getDocName(), control.getResPerson().getChiefName(), createPerson
                    , control.getResDate(), chargerDtoList, mainCharger, control.getResolution(), control.getControllerPerson()
                    , control.getTel(), control.getReceptionDate(), control.getExecutionRegNum(), control.getExecutionDate()
                    , control.getExecutorTel(), control.getWorkDone(), outController, returned, control.getWorkbookNum(), control.getWorkbookPageNum()));
        }
        return new ApiResult("Barcha nazoratlar", true, controlSetResponce);
    }

    private void sortListControl(List<Control> controlList) {
        List<Control> firstControlList = new ArrayList<>();
        List<Control> secondControlList = new ArrayList<>();

        for (Control control : controlList) {
            if (control.getBControl() != null)
                if (control.getBControl())
                    firstControlList.add(control);
                else secondControlList.add(control);
            else secondControlList.add(control);
        }
        firstControlList.sort(Comparator.comparing(Control::getControlPeriod));
        controlList.clear();
        controlList.addAll(firstControlList);
        controlList.addAll(secondControlList);
    }

    public ApiResult helpPdf(HttpServletResponse response) {

        try {
            File file = new File("help.pdf");
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());
            response.setContentLength((int) file.length());

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
            }
            return new ApiResult("Fayl muvaffaqiyatli yuklandi!", true);
        } catch (IOException exception) {
            return new ApiResult("Qandaydir xatolik yuz berdi.\nTizim administratoriga murojaat qiling", false);
        }
    }

    public ApiResult helpVideo(HttpServletResponse response) {
        // Faylni o'qing
        File file = new File("help.mp4");
        if (!file.exists()) {
            return new ApiResult("Fayl topilmadi", false);
        }

        // Javob boshliqlarini o'rnatish
        response.setContentType("video/mp4");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName());
        response.setContentLengthLong(file.length());

        // Faylni oqim orqali yuborish
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.flushBuffer(); // Oqimni tozalash
        } catch (Exception e) {
            System.out.println("xato: " + e.getMessage());
        }
        return new ApiResult("Fayl muvaffaqiyatli yuklandi!", true);
    }

    public ApiResult updateControl(ControlDto controlDto, Users currentUser, Long controlId) {
        try {
            Optional<Control> optionalControl = controlRepository.findById(controlId);
            if (optionalControl.isEmpty()) {
                return new ApiResult("Bunday nazorat mavjud emas!", false);
            }
            Control control = optionalControl.get();
            if (!control.getUsers().getId().equals(currentUser.getId())) {
                return new ApiResult("Bunday nazoratni o'zgartirish huquqi sizda mavjud emas!", false);
            }
            if (control.getReturned()) {
                return new ApiResult("Bu hujjat qaytarilgan. Bunday nazoratni o'zgartirish huquqi endi mavjud emas!", false);
            }
            Optional<Users> optionalUsers = userRepository.findById(controlDto.getResPersonId());
            if (optionalUsers.isEmpty()) {
                return new ApiResult("Bunday foydalanuvchi mavjud emas!", false);
            }
            for (Charger charger : chargersRepository.findByControl_IdOrderById(controlId)) {
                if (charger.getStage() > 1)
                    return new ApiResult("Bu nazorat boshqa pastgi pog'onaga tushurilgan.\nEndi bu nazoratga o'zgartirish kiritib bo'lmaydi!", false);
            }

            try {
                chargersRepository.removeByControlId(controlId);
            } catch (Exception exception) {
                return new ApiResult("Nazoratni o'zgartirishda muammo bo'ldi!", false);
            }
            boolean chargeable;
            List<ChargerDto> chargerDtoList = controlDto.getChargerList();
            List<Charger> chargerList = new ArrayList<>();
            for (int i = 0; i < chargerDtoList.size(); i++) {
                chargeable = i == 0;
                Optional<Users> userRepositoryById = userRepository.findById(chargerDtoList.get(i).getUserId());
                if (userRepositoryById.isEmpty()) {
                    return new ApiResult("Bunday foydalanuvchi mavjud emas!", false);
                }
                Charger charger = chargersRepository.save(new Charger(null, control, userRepositoryById.get(), chargeable, false, 1, chargerDtoList.get(i).getChargerName()));
                chargerList.add(charger);
            }
            control = new Control(control.getId(), control.getBlankNum(), controlDto.getComeDate(), controlDto.getControlPeriod()
                    , controlDto.getB_control(), control.getOutControllerId(), controlDto.getOtdName(), controlDto.getRegNum(), controlDto.getDocDate()
                    , controlDto.getRegNumCome(), controlDto.getRegNumComeDate(), controlDto.getDoc_name(), optionalUsers.get()
                    , controlDto.getResDate(), controlDto.getResolution(), controlDto.getControllerPerson(), controlDto.getTel()
                    , control.getReceptionDate(), control.getExecutionRegNum(), control.getExecutionDate(), control.getExecutorTel()
                    , control.getWorkDone(), currentUser, control.getReturned(), control.getSeen(), control.getWorkbookNum()
                    , control.getWorkbookPageNum(), control.getReasonOutControl(), control.getCreatedDate(), new Date(System.currentTimeMillis()),null);

            controlRepository.save(control);
            String error = add16(control, chargerList);
            if (error != null) return new ApiResult(error, false);
        } catch (Exception exception) {
            return new ApiResult("Qandaydir xatolik yuz berdi!\nIltimos Administratorga murojaat qiling", false);
        }
        return new ApiResult("Nazorat muvaffaqiyatli o'zgartirildi!", true);
    }


    public ApiResult getReturnData(Long controlId, Users currentUser) {
        Optional<Control> optionalControl = controlRepository.findById(controlId);
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday nazorat mavjud emas!", false);
        }
        Control control = optionalControl.get();
        return new ApiResult("Qaytarilayotgan nazorat qiymatlari!", true
                , new ReturnControlDto(control.getReceptionDate(), control.getExecutionRegNum(), control.getExecutionDate()
                , control.getExecutorTel(), control.getWorkbookNum(), control.getWorkbookPageNum(), control.getWorkDone()));
    }

    public ApiResult updateReturn(Users currentUser, OutControlDto outControlDto) {
        Optional<Control> optionalControl = controlRepository.findById(outControlDto.getIdControl());
        if (optionalControl.isEmpty()) {
            return new ApiResult("Bunday nazorat mavjud emas!", false);
        }
        Control control = optionalControl.get();
        if (!control.getUsers().getId().equals(currentUser.getId())) {
            return new ApiResult("Bunday nazoratni o'zgartirish huquqi sizda mavjud emas!", false);
        }
        control.setReturned(false);
        control.setSeen(false);
        control.setReceptionDate(null);
        control.setExecutionRegNum(null);
        control.setExecutionDate(null);
        control.setExecutorTel(null);
        control.setWorkDone(null);
        control.setWorkbookNum(null);
        control.setWorkbookPageNum(null);
        control.setReasonOutControl(null);
        control.setReturnReason(outControlDto.getReturnReason());
        controlRepository.save(control);
        return new ApiResult("Nazorat muvaffaqiyatli qaytarildi!", true);
    }
}