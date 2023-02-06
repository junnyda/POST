package board.common;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.dto.BoardFileDto;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class FileUtils {

    public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
        if(ObjectUtils.isEmpty(multipartHttpServletRequest)){ // 비어있는지 확인
            return null;
        }

        List<BoardFileDto> fileList = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        String path = "images/" + current.format(format);
        File file = new File(path); // 파일이 업로드될 폴더 생성
        if(file.exists() == false) {
            file.mkdirs();
        }

        Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

        String newFileName, originalFileExtension, contentType;

        while (iterator.hasNext()) {
            List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
            for(MultipartFile multipartFile : list) {
                if(multipartFile.isEmpty() == false){
                    contentType = multipartFile.getContentType(); // 파일 형식을 확인하고 그에 따라 이미지 확장자를 지정
                    if(ObjectUtils.isEmpty(contentType)){
                        break;
                    }
                    else {
                        if(contentType.contains("image/jpeg")){
                            originalFileExtension =".jpg";
                        }
                        else if(contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        }
                        else if(contentType.contains("image/gif")) {
                            originalFileExtension = ".gif";
                        }
                        else {
                            break;
                        }
                    }

                    newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
                    BoardFileDto boardFile = new BoardFileDto();
                    boardFile.setBoardIdx(boardIdx);
                    boardFile.setFileSize(multipartFile.getSize());
                    boardFile.setOriginalFileName(multipartFile.getOriginalFilename());
                    boardFile.setStoredFilePath(path +"/" +newFileName);
                    fileList.add(boardFile);

                    file = new File(path +"/" +newFileName);
                    multipartFile.transferTo(file);
                }
            }
        }
        return fileList;
    }
}