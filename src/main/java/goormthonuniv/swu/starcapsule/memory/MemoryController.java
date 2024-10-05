package goormthonuniv.swu.starcapsule.memory;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.snowball.SnowballResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/share_memory")
@Tag(name = "함께하는 기록", description = "타인이 나의 페이지의 추억을 기록하는 API")
public class MemoryController {
    private final MemoryService memoryService;
    @Operation(summary = "함께하는 기록", description = "타인이 나의 페이지의 추억을 기록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "함께하는 기록 작성 성공",
                    content = @Content(schema = @Schema(implementation = SnowballResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/{user_id}/write")
    public ResponseEntity<?> writeMemory(@RequestParam("title") String title,
                                         @RequestParam("answer") String answer,
                                         @RequestParam("object_name") String objectName,
                                         @RequestParam("writer") String writer,
                                         @PathVariable("user_id") String userId,
                                         @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Snowball snowball = memoryService.writeMemory(userId, title, answer, writer, image, objectName);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.response(new SnowballResponse(snowball)));
    }
}
