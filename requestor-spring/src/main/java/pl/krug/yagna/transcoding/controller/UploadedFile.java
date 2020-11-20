package pl.krug.yagna.transcoding.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class UploadedFile {

    private final Path path;
    private final String name;

}
