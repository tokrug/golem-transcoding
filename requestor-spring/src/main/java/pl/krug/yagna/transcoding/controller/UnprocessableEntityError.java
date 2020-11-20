package pl.krug.yagna.transcoding.controller;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UnprocessableEntityError {

    public static final UnprocessableEntityError SIZE_TOO_BIG = builder()
            .code(1)
            .message("Uploaded file size is too big. Uploaded file must be under 10 MB.")
            .build();

    private final int code;
    private final String message;

}
