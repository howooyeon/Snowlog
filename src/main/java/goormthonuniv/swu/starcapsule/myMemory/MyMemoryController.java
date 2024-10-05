package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/my_memory")
@Tag(name = "내 추억", description = "내 추억 관리 API")
public class MyMemoryController {

    private final UserService userService;
    private final MyMemoryService myMemoryService;

    public MyMemoryController(UserService userService, MyMemoryService myMemoryService) {
        this.userService = userService;
        this.myMemoryService = myMemoryService;
    }

    @Operation(summary = "내 기록", description = "나의 페이지의 추억을 기록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "내 기록 작성 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/write")
    public ResponseEntity<?> createMemory(@RequestHeader("Authorization") String token,
                                          @RequestParam("title") String title,
                                          @RequestParam("answer") String answer,
                                          @RequestParam("shapeName") String shapeName) {

        Long userId = userService.findByAccessToken(token).getId();
        myMemoryService.createMemory(title, answer, shapeName, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.response("기록이 성공적으로 저장되었습니다."));
    }

}
