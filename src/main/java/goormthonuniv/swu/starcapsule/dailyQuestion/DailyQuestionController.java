package goormthonuniv.swu.starcapsule.dailyQuestion;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.myMemory.MyMemoryService;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "1일 1질문", description = "질문 조회 API")
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;
    private final UserService userService;
    private final MyMemoryService myMemoryService;

    @Operation(summary = "오늘의 질문 조회", description = "오늘의 질문을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오늘의 질문 조회 성공",
                    content = @Content(schema = @Schema(implementation = DailyQuestionDto.class))),
            @ApiResponse(responseCode = "404", description = "오늘의 질문을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/api/question")
    public ResponseEntity<?> getTodayQuestion(@RequestHeader("Authorization") String token) {
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        boolean hasWrittenMyMemory = myMemoryService.existsMemoryForUser(user.getEmail());
        if (hasWrittenMyMemory) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.response("이미 작성한 기록이 있습니다."));
        }

        Optional<DailyQuestion> todayQuestion = dailyQuestionService.getTodayQuestion();
        if (todayQuestion.isPresent()) {
            DailyQuestionDto questionDto = DailyQuestionDto.fromEntity(todayQuestion.get());
            return ResponseEntity.ok(BaseResponse.response(questionDto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.response("오늘의 질문을 찾을 수 없습니다."));
        }
    }
}


