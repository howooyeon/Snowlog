package goormthonuniv.swu.starcapsule.myMemory;

import goormthonuniv.swu.starcapsule.global.template.BaseResponse;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/my_memory")
@Tag(name = "내 추억", description = "내 추억 기록 API")
public class MyMemoryController {

    private final UserService userService;
    private final MyMemoryService myMemoryService;

    public MyMemoryController(UserService userService, MyMemoryService myMemoryService) {
        this.userService = userService;
        this.myMemoryService = myMemoryService;
    }

    @Operation(summary = "내 추억", description = "나의 추억을 기록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "내 추억 작성 성공",
                    content = @Content(schema = @Schema(implementation = MyMemory.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "요청에 대한 응답을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/write")
    public ResponseEntity<?> createMemory(@RequestHeader("Authorization") String token,
                                          @RequestParam("title") String title,
                                          @RequestParam("answer") String answer,
                                          @RequestParam("shapeName") String shapeName,
                                          @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // 사용자 인증 확인
            User user = userService.findByAccessToken(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(BaseResponse.response("로그인 후 이용해주세요."));
            }

            // 필수 입력 값 유효성 검사
            if (title.isEmpty() || answer.isEmpty() || shapeName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponse.response("필수 값이 누락되었습니다."));
            }

            // 추억 기록 생성 로직 호출
            myMemoryService.createMemory(title, answer, shapeName, user.getId(), image);

            // 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.response("기록이 성공적으로 저장되었습니다."));
        } catch (IOException e) {
            // 파일 처리 중 오류 발생 시 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.response("파일 업로드 중 문제가 발생했습니다."));
        } catch (Exception e) {
            // 기타 서버 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.response("서버에서 오류가 발생했습니다."));
        }
    }


    @Operation(summary = "내 추억 상세 조회", description = "나의 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 추억 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyMemoryDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 추억을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{memory_id}")
    public ResponseEntity<?> getMemory(@PathVariable("memory_id") Long memoryId,
                                       @RequestHeader("Authorization") String token) {
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        MyMemory memory = myMemoryService.getMemoryById(memoryId);

        if (memory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.response("해당 추억을 찾을 수 없습니다."));
        }

        MyMemoryDto memoryDto = MyMemoryDto.fromEntity(memory);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.response(memoryDto));
    }


    @Operation(summary = "공개 전 추억 조회", description = "공개 전인 나의 모든 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공개 전 추억 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyMemoryListDto.class))),
            @ApiResponse(responseCode = "404", description = "공개 전 추억을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/unreleased")
    public ResponseEntity<?> getUnreleasedMemories(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @RequestHeader("Authorization") String token) {

        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        // 공개 전 메모리들만 조회하는 서비스 호출
        Page<MyMemory> unreleasedMemories = myMemoryService.getUnreleasedMemories(page, size);
        int totalElements = myMemoryService.countUnreleasedMemories();

        if (unreleasedMemories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.response("공개 전 추억을 찾을 수 없습니다."));
        }

        // DTO 변환
        List<MyMemoryDto> memoryDtos = unreleasedMemories.stream()
                .map(MyMemoryDto::fromEntity)
                .collect(Collectors.toList());

        // 페이지 정보 생성
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .totalElements(totalElements)
                .build();

        MyMemoryListDto responseDto = MyMemoryListDto.builder()
                .memory(memoryDtos)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.response(responseDto));
    }

    @Operation(summary = "공개 후 추억 조회", description = "공개 후 나의 모든 추억을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공개 후 추억 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyMemoryListDto.class))),
            @ApiResponse(responseCode = "404", description = "공개 후 추억을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/released")
    public ResponseEntity<?> getReleasedMemories(@RequestParam("page") int page, @RequestParam("size") int size, @RequestHeader("Authorization") String token) {
        User user = userService.findByAccessToken(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.response("로그인 후 이용해주세요."));
        }

        // 공개 후 메모리들만 조회하는 서비스 호출
        Page<MyMemory> releasedMemories = myMemoryService.getReleasedMemories(page, size);
        int totalElements = myMemoryService.countReleased();

        if (releasedMemories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.response("공개 후 추억을 찾을 수 없습니다."));
        }

        // DTO 변환
        List<MyMemoryDto> memoryDtos = releasedMemories.stream()
                .map(MyMemoryDto::fromEntity)
                .collect(Collectors.toList());

        // 페이지 정보 생성
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .totalElements(totalElements)
                .build();

        MyMemoryListDto responseDto = MyMemoryListDto.builder()
                .memory(memoryDtos)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.response(responseDto));
    }

}
