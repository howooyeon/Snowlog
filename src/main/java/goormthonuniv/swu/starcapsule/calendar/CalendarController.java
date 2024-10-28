package goormthonuniv.swu.starcapsule.calendar;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.memory.Memory;
import goormthonuniv.swu.starcapsule.memory.MemoryService;
import goormthonuniv.swu.starcapsule.myMemory.MyMemory;
import goormthonuniv.swu.starcapsule.myMemory.MyMemoryDto;
import goormthonuniv.swu.starcapsule.myMemory.MyMemoryService;
import goormthonuniv.swu.starcapsule.snowball.MemoryDto;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping("/data")
    public ResponseEntity<?> getCalendarData(
            @RequestHeader("Authorization") String token) {

        String serverTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        Long snowballId = user.getSnowball() != null ? user.getSnowball().getId() : null;

        boolean[] writtenArray = new boolean[32];

        LocalDate startDate = LocalDate.of(2024, 11, 30);
        int myMemoryCount = 0;

        for (int i = 0; i < 32; i++) {
            LocalDate date = startDate.plusDays(i);

            boolean hasMyMemory = myMemoryService.existsByDateAndUser(date.atStartOfDay(), user.getEmail());
            boolean hasSnowballMemory = snowballId != null && memoryService.existsByDateAndSnowball(date, snowballId);

            if (hasMyMemory || hasSnowballMemory) {
                writtenArray[i] = true;
            }

            if (hasMyMemory) {
                myMemoryCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("my_memory_count", myMemoryCount);
        response.put("server_time", serverTime);
        response.put("written_array", writtenArray);

        return ResponseEntity.ok(BaseResponse.response(response));
    }

    @Operation(summary = "캘린더 특정 날짜의 추억 조회",
            description = "주어진 날짜에 해당하는 사용자의 추억과 함께한 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추억 조회 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청, 유효하지 않은 날짜 형식입니다.",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패, 로그인 후 이용해주세요.",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 날짜의 추억이 없습니다.",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/memories/{date}")
    public ResponseEntity<?> getMemoriesByDate(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd 형식)", required = true) @PathVariable("date") String date) {

        User user = userService.findByAccessToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date); // LocalDate로 변환
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(BaseResponse.response("유효하지 않은 날짜 형식입니다."));
        }

        // 해당 날짜의 시작과 끝 시간 설정
        LocalDateTime startOfDay = localDate.atStartOfDay(); // 해당 날짜의 시작 시간
        LocalDateTime endOfDay = startOfDay.plusDays(1); // 해당 날짜의 끝 시간 (하루 후)

        // 사용자가 작성한 추억 가져오기
        List<MyMemory> myMemories = myMemoryService.findMyMemoriesByDateAndUserBetween(startOfDay, endOfDay, user.getEmail());
        List<MyMemoryDto> myMemoryDTOs = myMemories.stream()
                .map(MyMemoryDto::fromEntity)
                .collect(Collectors.toList());

        // 스노우볼에 있는 추억 가져오기 (본인 + 친구)
        Long snowballId = user.getSnowball() != null ? user.getSnowball().getId() : null;

        List<Memory> memories = memoryService.findMemoriesByDateAndSnowballBetween(startOfDay, endOfDay, snowballId);
        List<MemoryDto> memoryDTOs = memories.stream()
                .map(MemoryDto::new)
                .collect(Collectors.toList());

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("my_memory", myMemoryDTOs);
        response.put("memories", memoryDTOs);

        return ResponseEntity.ok(BaseResponse.response(response));
    }

}


