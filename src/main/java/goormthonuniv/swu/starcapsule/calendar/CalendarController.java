package goormthonuniv.swu.starcapsule.calendar;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.memory.MemoryService;
import goormthonuniv.swu.starcapsule.myMemory.MyMemoryService;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/calendar")
@Tag(name = "캘린더", description = "캘린더 조회 API")
public class CalendarController {

    private final MyMemoryService myMemoryService;
    private final MemoryService memoryService;
    private final UserService userService;

    @Autowired
    public CalendarController(MyMemoryService myMemoryService, MemoryService memoryService, UserService userService) {
        this.myMemoryService = myMemoryService;
        this.memoryService = memoryService;
        this.userService = userService;
    }

    @Operation(summary = "캘린더 퍼즐 조회", description = "캘린더에 추억 퍼즐이 생깁니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기록 조회 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/data/{snowball_Id}")
    public ResponseEntity<?> getCalendarData(
            @RequestHeader("Authorization") String token,
            @PathVariable("snowball_Id") Long snowballId) {

        String serverTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        boolean[] writtenArray = new boolean[32];
        LocalDate startDate = LocalDate.of(2023, 11, 30);
        int myMemoryCount = 0;

        for (int i = 0; i < 32; i++) {
            LocalDate date = startDate.plusDays(i);

            boolean hasMyMemory = myMemoryService.existsByDateAndUser(date.atStartOfDay(), user.getEmail());
            boolean hasSnowballMemory = memoryService.existsByDateAndSnowball(date, snowballId);

            // 기록 여부 배열에 기록
            if (hasMyMemory || hasSnowballMemory) {
                writtenArray[i] = true;
            }

            if (hasMyMemory) {
                myMemoryCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("myMemoryCount", myMemoryCount);
        response.put("serverTime", serverTime);
        response.put("writtenArray", writtenArray);

        return ResponseEntity.ok(BaseResponse.response(response));
    }



}


